package com.febrie.rpg.island.manager;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.impl.IslandFirestoreService;
import com.febrie.rpg.database.service.impl.PlayerIslandDataService;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.island.Island;
import com.febrie.rpg.island.IslandCache;
import com.febrie.rpg.island.IslandPlayer;
import com.febrie.rpg.island.IslandService;
import com.febrie.rpg.island.world.IslandWorldManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.LogUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * 섬 시스템 관리자
 * 섬 생성, 삭제, 관리 등 모든 섬 관련 기능 총괄
 *
 * @author Febrie, CoffeeTory
 */
public class IslandManager {

    private final RPGMain plugin;
    private final IslandWorldManager worldManager;
    private final IslandService islandService;
    private final IslandCache cache;
    private IslandVisitTracker visitTracker;

    public IslandManager(@NotNull RPGMain plugin, @Nullable IslandFirestoreService firestoreService, @Nullable PlayerIslandDataService playerDataService) {
        this.plugin = plugin;
        this.worldManager = new IslandWorldManager(plugin);

        this.islandService = new IslandService(firestoreService, playerDataService);
        this.cache = new IslandCache();
        // this escape 경고 방지 - initialize()에서 생성
        this.visitTracker = null;
    }

    /**
     * 초기화
     */
    public void initialize() {
        worldManager.initialize();
        // visitTracker 초기화
        this.visitTracker = new IslandVisitTracker(plugin, this);
        visitTracker.startTracking();

        // 모든 섬과 플레이어 데이터 사전 로드
        preloadAllData();
    }

    /**
     * 모든 데이터 사전 로드
     */
    private void preloadAllData() {
        LogUtil.info("섬 시스템 데이터 사전 로드 시작...");
        long startTime = System.currentTimeMillis();

        // 병렬 로드로 속도 향상
        CompletableFuture<Void> islandsFuture = preloadAllIslands();
        CompletableFuture<Void> playersFuture = preloadAllPlayerData();

        CompletableFuture.allOf(islandsFuture, playersFuture)
                .thenRun(() -> {
                    long elapsed = System.currentTimeMillis() - startTime;
                    LogUtil.info(String.format("섬 데이터 로드 완료: %d개 섬, %d명 플레이어 데이터 (%d ms)", cache.getIslandCount(), cache.getPlayerCount(), elapsed));
                    LogUtil.info("캐시 상태: " + cache.getCacheStats());
                })
                .exceptionally(ex -> {
                    LogUtil.error("섬 데이터 사전 로드 실패", ex);
                    return null;
                });
    }

    /**
     * 모든 섬 데이터 사전 로드
     */
    private @NotNull CompletableFuture<Void> preloadAllIslands() {
        return islandService.loadAllIslands()
                .thenAccept(islands -> {
                    for (IslandDTO island : islands) {
                        cache.putIsland(island.core().islandId(), island);
                        cache.updateIslandMembers(island);
                    }
                    LogUtil.debug("섬 " + islands.size() + "개 캐시 로드 완료");
                });
    }

    /**
     * 모든 플레이어 섬 데이터 사전 로드
     */
    private @NotNull CompletableFuture<Void> preloadAllPlayerData() {
        return islandService.loadAllPlayerData()
                .thenAccept(players -> {
                    for (PlayerIslandDataDTO player : players) {
                        cache.putPlayerData(player.playerUuid(), player);
                    }
                    LogUtil.debug("플레이어 " + players.size() + "명 데이터 캐시 로드 완료");
                });
    }

    /**
     * 종료 처리
     */
    public void shutdown() {
        if (visitTracker != null) {
            visitTracker.stopTracking();
        }
    }

    /**
     * WorldManager 가져오기 (Island 클래스용)
     */
    private IslandWorldManager getWorldManagerForIsland() {
        return worldManager;
    }

    /**
     * 새 섬 생성
     */
    public CompletableFuture<IslandDTO> createIsland(@NotNull Player owner, @NotNull String islandName) {
        String ownerUuid = owner.getUniqueId()
                .toString();
        String ownerName = owner.getName();

        // 플레이어 로드
        return loadIslandPlayer(ownerUuid, ownerName).thenCompose(islandPlayer -> {
            // 이미 섬이 있는지 확인
            if (islandPlayer.hasIsland()) {
                return CompletableFuture.completedFuture(null);
            }

            // 새 섬 생성
            return Island.create(worldManager, ownerUuid, ownerName, islandName)
                    .thenCompose(island -> {
                        // Firestore에 저장
                        return islandService.saveIsland(island.getData())
                                .thenCompose(saved -> {
                                    if (!saved) {
                                        return CompletableFuture.completedFuture(null);
                                    }

                                    // 플레이어를 섬에 가입시킴
                                    return islandPlayer.joinIsland(island.getId(), IslandRole.OWNER)
                                            .thenCompose(joined -> {
                                                if (!joined) {
                                                    return CompletableFuture.completedFuture(null);
                                                }

                                                // 플레이어 데이터 저장
                                                return islandService.savePlayerData(islandPlayer.getData())
                                                        .thenApply(playerSaved -> {
                                                            if (playerSaved) {
                                                                // 캐시 업데이트
                                                                cache.putIsland(island.getId(), island.getData());
                                                                cache.putPlayerData(ownerUuid, islandPlayer.getData());
                                                                cache.updateIslandMembers(island.getData());

                                                                // 플레이어를 섬으로 텔레포트
                                                                Bukkit.getScheduler()
                                                                        .runTask(plugin, () -> owner.teleport(island.getSpawnLocation()));

                                                                return island.getData();
                                                            } else {
                                                                return null;
                                                            }
                                                        });
                                            });
                                });
                    });
        });
    }

    /**
     * 새 섬 생성 (확장 버전 - 색상, 바이옴, 템플릿 지정)
     */
    public CompletableFuture<Boolean> createIsland(@NotNull Player owner, @NotNull String islandName, @NotNull String colorHex, @NotNull String biome, @NotNull String template) {
        String ownerUuid = owner.getUniqueId()
                .toString();
        String ownerName = owner.getName();

        // 플레이어 로드
        return loadIslandPlayer(ownerUuid, ownerName).thenCompose(islandPlayer -> {
            // 이미 섬을 가지고 있는지 확인
            if (islandPlayer.hasIsland()) {
                LogUtil.warning("섬 생성 실패: 플레이어가 이미 섬을 소유하고 있음 - " + ownerName);
                return CompletableFuture.completedFuture(false);
            }

            // 물리적 섬 생성
            CompletableFuture<IslandLocationDTO> locationFuture = worldManager.createNewIsland(85);

            return locationFuture.thenCompose(location -> {
                if (location == null) {
                    LogUtil.error("물리적 섬 생성 실패");
                    return CompletableFuture.completedFuture(false);
                }

                String islandId = UUID.randomUUID()
                        .toString();

                // 기본 섬 데이터 생성
                IslandDTO baseIsland = IslandDTO.createNew(islandId, ownerUuid, ownerName, islandName);

                // 설정을 포함한 새로운 섬 데이터 생성
                IslandSettingsDTO settings = new IslandSettingsDTO(colorHex, biome, template);
                IslandDTO islandData = IslandDTO.fromFields(
                        baseIsland.core().islandId(), baseIsland.core().ownerUuid(), baseIsland.core().ownerName(), baseIsland.core().islandName(),
                        baseIsland.core().size(), baseIsland.core().isPublic(), baseIsland.core().createdAt(), baseIsland.core().lastActivity(),
                        baseIsland.membership().members(), baseIsland.membership().workers(), baseIsland.membership().contributions(),
                        new IslandSpawnDTO(new IslandSpawnPointDTO(location.centerX(), 66.0, location.centerZ(), 0.0f, 0.0f, "섬 중앙"), null, List.of(), Map.of()),
                        baseIsland.configuration().upgradeData(), baseIsland.configuration().permissions(), baseIsland.social().pendingInvites(), baseIsland.social().recentVisits(),
                        baseIsland.core().totalResets(), baseIsland.core().deletionScheduledAt(), settings);

                // 섬 저장
                return islandService.saveIsland(islandData)
                        .thenCompose(saved -> {
                            if (!saved) {
                                return CompletableFuture.completedFuture(false);
                            }

                            // 플레이어 데이터 업데이트
                            PlayerIslandDataDTO playerData = new PlayerIslandDataDTO(islandPlayer.getUuid(), islandId, IslandRole.OWNER, 0, // totalIslandResets
                                    0L, // totalContribution
                                    System.currentTimeMillis(), // lastJoined
                                    System.currentTimeMillis() // lastActivity
                            );

                            return islandService.savePlayerData(playerData)
                                    .thenCompose(playerSaved -> {
                                        if (!playerSaved) {
                                            return CompletableFuture.completedFuture(false);
                                        }

                                        // 캐시 업데이트
                                        cache.putIsland(islandId, islandData);
                                        cache.putPlayerData(ownerUuid, playerData);

                                        // 바이옴 설정 적용
                                        return applyBiomeToIsland(location.centerX(), location.centerZ(), baseIsland.core().size(), biome).thenApply(biomeApplied -> {
                                            if (biomeApplied) {
                                                LogUtil.info("섬 바이옴 설정 성공: " + biome);
                                            } else {
                                                LogUtil.warning("섬 바이옴 설정 실패: " + biome);
                                            }
                                            return true;
                                        });
                                    });
                        });
            });
        });
    }

    /**
     * 섬 정보 로드
     */
    public CompletableFuture<Island> loadIsland(@NotNull String islandId) {
        // 캐시 확인
        IslandDTO cached = cache.getIsland(islandId);
        if (cached != null) {
            return CompletableFuture.completedFuture(new Island(getWorldManagerForIsland(), cached));
        }

        // Firestore에서 로드
        return islandService.loadIsland(islandId)
                .thenApply(islandData -> {
                    if (islandData != null) {
                        cache.putIsland(islandId, islandData);
                        cache.updateIslandMembers(islandData);
                        return new Island(getWorldManagerForIsland(), islandData);
                    }
                    return null;
                });
    }

    /**
     * 플레이어의 섬 데이터 로드
     */
    public CompletableFuture<IslandPlayer> loadIslandPlayer(@NotNull String playerUuid, @NotNull String playerName) {
        // 캐시 확인
        PlayerIslandDataDTO cached = cache.getPlayerData(playerUuid);
        if (cached != null) {
            return CompletableFuture.completedFuture(new IslandPlayer(playerUuid, playerName, cached));
        }

        // Firestore에서 로드
        return islandService.loadPlayerData(playerUuid)
                .thenApply(data -> {
                    if (data != null) {
                        cache.putPlayerData(playerUuid, data);
                    }
                    return new IslandPlayer(playerUuid, playerName, data);
                });
    }

    /**
     * 플레이어의 섬 가져오기
     */
    public CompletableFuture<Island> getPlayerIsland(@NotNull String playerUuid, @NotNull String playerName) {
        String islandId = cache.getPlayerIslandId(playerUuid);
        if (islandId != null) {
            return loadIsland(islandId);
        }

        return loadIslandPlayer(playerUuid, playerName).thenCompose(islandPlayer -> {
            String currentIslandId = islandPlayer.getCurrentIslandId();
            if (currentIslandId != null) {
                return loadIsland(currentIslandId);
            }
            return CompletableFuture.completedFuture(null);
        });
    }

    /**
     * 섬 삭제
     */
    public CompletableFuture<Boolean> deleteIsland(@NotNull String islandId) {
        LogUtil.info("섬 삭제 시작: " + islandId);
        
        return loadIsland(islandId).thenCompose(island -> {
            if (island == null) {
                LogUtil.warning("섬 삭제 실패 - 섬을 찾을 수 없음: " + islandId);
                return CompletableFuture.completedFuture(false);
            }

            LogUtil.debug("물리적 섬 삭제 시작: " + islandId);
            
            // 물리적 섬 삭제
            return island.delete()
                    .thenCompose(deleted -> {
                        if (!deleted) {
                            LogUtil.error("물리적 섬 삭제 실패: " + islandId);
                            return CompletableFuture.completedFuture(false);
                        }

                        LogUtil.debug("물리적 섬 삭제 성공, 멤버 데이터 제거 시작: " + islandId);

                        // 모든 멤버의 섬 데이터 제거
                        List<CompletableFuture<Boolean>> memberUpdates = new ArrayList<>();

                        for (String memberUuid : island.getAllMemberUuids()) {
                            memberUpdates.add(loadIslandPlayer(memberUuid, "Unknown").thenCompose(islandPlayer -> islandPlayer.leaveIsland()
                                    .thenCompose(left -> islandService.savePlayerData(islandPlayer.getData()))));
                        }

                        return CompletableFuture.allOf(memberUpdates.toArray(new CompletableFuture<?>[0]))
                                .thenCompose(v -> {
                                    LogUtil.debug("멤버 데이터 제거 완료, Firestore에서 섬 삭제 시작: " + islandId);
                                    return islandService.deleteIsland(islandId);
                                })
                                .thenApply(firestoreDeleted -> {
                                    if (firestoreDeleted) {
                                        LogUtil.info("섬 삭제 완료: " + islandId);
                                        // 캐시 제거
                                        cache.removeIslandMembers(island.getData());
                                        cache.removeIsland(islandId);
                                    } else {
                                        LogUtil.error("Firestore에서 섬 삭제 실패: " + islandId);
                                    }
                                    return firestoreDeleted;
                                });
                    })
                    .exceptionally(ex -> {
                        LogUtil.error("섬 삭제 중 예외 발생: " + islandId, ex);
                        return false;
                    });
        });
    }

    /**
     * 섬 초기화
     */
    public CompletableFuture<Boolean> resetIsland(@NotNull String islandId) {
        return loadIsland(islandId).thenCompose(island -> {
            if (island == null) {
                return CompletableFuture.completedFuture(false);
            }

            // 섬장의 초기화 가능 여부 확인
            return loadIslandPlayer(island.getOwnerUuid(), island.getOwnerName()).thenCompose(ownerPlayer -> {
                if (!ownerPlayer.canResetIsland()) {
                    return CompletableFuture.completedFuture(false);
                }

                // 물리적 섬 초기화
                return island.reset()
                        .thenCompose(reset -> {
                            if (!reset) {
                                return CompletableFuture.completedFuture(false);
                            }

                            // 섬장의 초기화 횟수 증가
                            ownerPlayer.incrementResetCount();

                            // 저장
                            return CompletableFuture.allOf(islandService.saveIsland(island.getData()), islandService.savePlayerData(ownerPlayer.getData()))
                                    .thenApply(v -> {
                                        // 캐시 업데이트
                                        cache.putIsland(islandId, island.getData());
                                        cache.putPlayerData(island.getOwnerUuid(), ownerPlayer.getData());

                                        return true;
                                    });
                        });
            });
        });
    }

    /**
     * 공개 섬 목록 조회
     */
    public CompletableFuture<List<IslandDTO>> getPublicIslands(int limit) {
        return islandService.loadPublicIslands(limit);
    }

    /**
     * 섬 업데이트
     */
    public CompletableFuture<Boolean> updateIsland(@NotNull IslandDTO island) {
        return islandService.saveIsland(island)
                .thenApply(saved -> {
                    if (saved) {
                        cache.putIsland(island.core().islandId(), island);
                        cache.updateIslandMembers(island);
                    }
                    return saved;
                });
    }

    /**
     * 플레이어가 현재 있는 섬 찾기
     */
    @Nullable
    public Island getIslandAt(@NotNull Location location) {
        if (!getWorldManager().isIslandWorld(location.getWorld())) {
            return null;
        }

        // 캐시된 모든 섬 확인
        for (IslandDTO islandData : cache.getAllIslands()) {
            Island island = new Island(getWorldManagerForIsland(), islandData);
            if (island.contains(location)) {
                return island;
            }
        }

        return null;
    }

    /**
     * 섬 월드 관리자 가져오기
     */
    public IslandWorldManager getWorldManager() {
        return worldManager;
    }

    /**
     * VisitTracker 가져오기
     */
    @NotNull
    public IslandVisitTracker getVisitTracker() {
        return visitTracker;
    }

    /**
     * 캐시 초기화
     */
    public void clearCache() {
        cache.clear();
    }

    /**
     * 캐시 통계
     */
    public String getCacheStats() {
        return cache.getStats();
    }

    /**
     * 플레이어 캐시 업데이트
     */
    public void updatePlayerCache(@NotNull String playerUuid, @NotNull PlayerIslandDataDTO data) {
        cache.putPlayerData(playerUuid, data);
    }

    /**
     * 캐시에서 플레이어 섬 데이터 가져오기
     */
    @Nullable
    public PlayerIslandDataDTO getPlayerIslandDataFromCache(@NotNull String playerUuid) {
        return cache.getPlayerData(playerUuid);
    }

    /**
     * 캐시에서 섬 데이터 가져오기
     */
    @Nullable
    public IslandDTO getIslandFromCache(@NotNull String islandId) {
        return cache.getIsland(islandId);
    }

    /**
     * 플레이어의 섬 리스폰 위치 가져오기
     */
    @Nullable
    public Location getPlayerIslandSpawn(@NotNull Player player) {
        String uuid = player.getUniqueId()
                .toString();

        // 플레이어 섬 데이터 확인
        PlayerIslandDataDTO playerData = getPlayerIslandDataFromCache(uuid);
        if (playerData == null || playerData.currentIslandId() == null) {
            return null;
        }

        // 섬 데이터 확인
        IslandDTO island = getIslandFromCache(playerData.currentIslandId());
        if (island == null) {
            return null;
        }

        // 스폰 위치 계산
        IslandSpawnDTO spawnData = island.configuration().spawnData();
        IslandSpawnPointDTO spawnPoint = null;

        // 개인 스폰 또는 기본 스폰
        boolean isOwner = island.core().ownerUuid()
                .equals(uuid);
        spawnPoint = spawnData.getPersonalSpawn(uuid, isOwner);

        if (spawnPoint == null) {
            spawnPoint = spawnData.defaultSpawn();
        }

        // 섬 월드에서 Location 생성
        World islandWorld = Bukkit.getWorld("island_world");
        if (islandWorld != null) {
            return new Location(islandWorld, spawnPoint.x(), spawnPoint.y() + 0.5, spawnPoint.z(), spawnPoint.yaw(), spawnPoint.pitch());
        }

        return null;
    }

    /**
     * 섬에 바이옴 적용
     */
    @Contract("_, _, _, _ -> new")
    private @NotNull CompletableFuture<Boolean> applyBiomeToIsland(int centerX, int centerZ, int size, String biome) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                World world = worldManager.getIslandWorld();
                if (world == null) return false;

                // 바이옴 문자열을 직접 매칭하여 찾기
                Biome bukkitBiome;
                String biomeUpper = biome.toUpperCase()
                        .replace("-", "_");

                // 일반적인 바이옴 매핑
                switch (biomeUpper) {
                    case "PLAINS" -> bukkitBiome = Biome.PLAINS;
                    case "FOREST" -> bukkitBiome = Biome.FOREST;
                    case "DESERT" -> bukkitBiome = Biome.DESERT;
                    case "JUNGLE" -> bukkitBiome = Biome.JUNGLE;
                    case "TAIGA" -> bukkitBiome = Biome.TAIGA;
                    case "SNOWY_TAIGA" -> bukkitBiome = Biome.SNOWY_TAIGA;
                    case "SAVANNA" -> bukkitBiome = Biome.SAVANNA;
                    case "SWAMP" -> bukkitBiome = Biome.SWAMP;
                    case "OCEAN" -> bukkitBiome = Biome.OCEAN;
                    case "BEACH" -> bukkitBiome = Biome.BEACH;
                    case "MUSHROOM_FIELDS" -> bukkitBiome = Biome.MUSHROOM_FIELDS;
                    case "BADLANDS" -> bukkitBiome = Biome.BADLANDS;
                    case "FLOWER_FOREST" -> bukkitBiome = Biome.FLOWER_FOREST;
                    case "CHERRY_GROVE" -> bukkitBiome = Biome.CHERRY_GROVE;
                    case "DARK_FOREST" -> bukkitBiome = Biome.DARK_FOREST;
                    case "BIRCH_FOREST" -> bukkitBiome = Biome.BIRCH_FOREST;
                    case "BAMBOO_JUNGLE" -> bukkitBiome = Biome.BAMBOO_JUNGLE;
                    default -> {
                        LogUtil.warning("지원하지 않는 바이옴: " + biome);
                        return false;
                    }
                }

                // 섬 크기에 따라 바이옴 설정 범위 결정
                int radius = size / 2;
                final Biome finalBiome = bukkitBiome;

                // 비동기로 바이옴 설정
                plugin.getServer()
                        .getScheduler()
                        .runTask(plugin, () -> {
                            for (int x = centerX - radius; x <= centerX + radius; x++) {
                                for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                                    // 모든 높이에 대해 바이옴 설정
                                    for (int y = world.getMinHeight(); y < world.getMaxHeight(); y++) {
                                        world.setBiome(x, y, z, finalBiome);
                                    }
                                }
                            }
                        });

                return true;
            } catch (Exception e) {
                LogUtil.error("바이옴 설정 중 오류", e);
                return false;
            }
        });
    }

    /**
     * 플레이어를 섬으로 텔레포트 (멤버/방문자 구분)
     */
    public void teleportToIsland(@NotNull Player player, @NotNull IslandDTO island) {
        World world = worldManager.getIslandWorld();
        if (world == null) {
            player.sendMessage(UnifiedColorUtil.parse("&c섬 월드를 찾을 수 없습니다."));
            return;
        }

        Location spawnLocation;
        String playerUuid = player.getUniqueId()
                .toString();

        // 섬 주인인지 확인
        boolean isOwner = island.core().ownerUuid()
                .equals(playerUuid);

        // 섬 멤버인지 확인
        boolean isMember = island.membership().members()
                .stream()
                .anyMatch(m -> m.uuid()
                        .equals(playerUuid));

        // 알바생인지 확인
        boolean isWorker = island.membership().workers()
                .stream()
                .anyMatch(w -> w.uuid()
                        .equals(playerUuid));

        IslandSpawnDTO spawnData = island.configuration().spawnData();

        if (isOwner || isMember || isWorker) {
            // 섬원/알바생은 기본 스폰으로
            IslandSpawnPointDTO defaultSpawn = spawnData.defaultSpawn();
            spawnLocation = new Location(world, defaultSpawn.x(), defaultSpawn.y(), defaultSpawn.z(), defaultSpawn.yaw(), defaultSpawn.pitch());
        } else {
            // 방문자는 방문자 스폰으로 (없으면 기본 스폰)
            IslandSpawnPointDTO visitorSpawn = spawnData.visitorSpawn();
            if (visitorSpawn != null) {
                spawnLocation = new Location(world, visitorSpawn.x(), visitorSpawn.y(), visitorSpawn.z(), visitorSpawn.yaw(), visitorSpawn.pitch());
            } else {
                IslandSpawnPointDTO defaultSpawn = spawnData.defaultSpawn();
                spawnLocation = new Location(world, defaultSpawn.x(), defaultSpawn.y(), defaultSpawn.z(), defaultSpawn.yaw(), defaultSpawn.pitch());
            }
        }

        // 텔레포트
        player.teleport(spawnLocation);

        // 메시지
        if (isOwner || isMember || isWorker) {
            player.sendMessage(UnifiedColorUtil.parse("&a섬으로 이동했습니다."));
        } else {
            player.sendMessage(UnifiedColorUtil.parse("&e" + island.core().islandName() + " 섬을 방문했습니다."));

            // 방문 기록 추가
            recordVisit(island, player);
        }
    }

    /**
     * 방문 기록 추가
     */
    private void recordVisit(@NotNull IslandDTO island, @NotNull Player visitor) {
        String visitorUuid = visitor.getUniqueId()
                .toString();
        String visitorName = visitor.getName();
        long now = System.currentTimeMillis();

        // 새 방문 기록 생성
        IslandVisitDTO newVisit = new IslandVisitDTO(visitorUuid, visitorName, now, 0L);

        // 방문 기록 리스트 업데이트 (최대 100개 유지)
        List<IslandVisitDTO> visits = new ArrayList<>(island.social().recentVisits());
        visits.addFirst(newVisit);
        if (visits.size() > 100) {
            visits = visits.subList(0, 100);
        }

        // 섬 데이터 업데이트
        IslandDTO updated = IslandDTO.fromFields(island.core().islandId(), island.core().ownerUuid(), island.core().ownerName(), island.core().islandName(), island.core().size(), island.core().isPublic(), island.core().createdAt(), System.currentTimeMillis(), island.membership().members(), island.membership().workers(), island.membership().contributions(), island.configuration().spawnData(), island.configuration().upgradeData(), island.configuration().permissions(), island.social().pendingInvites(), visits, island.core().totalResets(), island.core().deletionScheduledAt(), island.configuration().settings());

        updateIsland(updated);
    }
}