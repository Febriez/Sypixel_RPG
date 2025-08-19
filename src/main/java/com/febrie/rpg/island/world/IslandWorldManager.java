package com.febrie.rpg.island.world;

import com.fastasyncworldedit.core.util.TaskManager;
import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandLocationDTO;
import net.kyori.adventure.text.Component;
import com.febrie.rpg.util.LogUtil;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.MaxChangedBlocksException;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 섬 월드 관리자
 * FAWE를 사용한 섬 전용 월드 생성 및 관리
 *
 * @author Febrie, CoffeeTory
 */
public class IslandWorldManager {

    private static final String ISLAND_WORLD_NAME = "Island";
    private static final int ISLAND_HEIGHT = 64; // 섬 기본 높이
    private static final int ISLAND_SPACING = 1000; // 섬 간 간격

    private final RPGMain plugin;
    private World islandWorld;
    private final AtomicInteger nextIslandIndex = new AtomicInteger(0);

    public IslandWorldManager(@NotNull RPGMain plugin) {
        this.plugin = plugin;
    }

    /**
     * 섬 월드 초기화
     */
    public void initialize() {
        // 월드 존재 확인
        World existingWorld = Bukkit.getWorld(ISLAND_WORLD_NAME);
        if (existingWorld != null) {
            this.islandWorld = existingWorld;
            LogUtil.info("기존 섬 월드를 사용합니다: " + ISLAND_WORLD_NAME);
            return;
        }

        // 새 월드 생성
        createIslandWorld();
    }

    /**
     * 섬 월드 생성
     */
    private void createIslandWorld() {
        LogUtil.info("새로운 섬 월드를 생성합니다: " + ISLAND_WORLD_NAME);

        WorldCreator creator = new WorldCreator(ISLAND_WORLD_NAME);
        creator.environment(World.Environment.NORMAL);
        creator.generateStructures(false);
        creator.generator("VoidGenerator");

        this.islandWorld = creator.createWorld();

        if (islandWorld != null) {
            // 월드 설정
            islandWorld.setDifficulty(Difficulty.NORMAL);
            islandWorld.setSpawnFlags(true, true);
            islandWorld.setPVP(true);
            // setKeepSpawnInMemory deprecated - 제거

            // 게임 룰 설정
            islandWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
            islandWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, true);
            islandWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            islandWorld.setGameRule(GameRule.KEEP_INVENTORY, false);

            LogUtil.info("섬 월드 생성 완료: " + ISLAND_WORLD_NAME);
        } else {
            LogUtil.error("섬 월드 생성 실패!");
        }
    }

    /**
     * 섬 월드 가져오기
     */
    public World getIslandWorld() {
        return islandWorld;
    }

    /**
     * 새 섬 생성 (FAWE 사용)
     */
    public CompletableFuture<IslandLocationDTO> createNewIsland(int size) {
        return createNewIsland(size, "PLAINS", "BASIC");
    }

    /**
     * 새 섬 생성 (FAWE 사용) - 상세 옵션
     */
    public CompletableFuture<IslandLocationDTO> createNewIsland(int size, String biome, String template) {
        return CompletableFuture.supplyAsync(() -> {
            int islandIndex = nextIslandIndex.getAndIncrement();
            IslandLocationDTO location = IslandLocationDTO.getNextIslandLocation(islandIndex);

            // FAWE를 사용한 섬 생성 및 바이옴 설정
            generateIslandWithFAWE(location.centerX(), location.centerZ(), size, biome, template);

            LogUtil.info(String.format("새 섬 생성 완료 - 위치: (%d, %d), 크기: %d, 바이옴: %s",
                    location.centerX(), location.centerZ(), size, biome));

            return location;
        });
    }

    /**
     * FAWE를 사용한 섬 생성 및 바이옴 설정
     */
    private void generateIslandWithFAWE(int centerX, int centerZ, int size, String biomeName, String template) {
        int radius = size / 2;

        // FAWE를 사용해 비동기로 실행
        TaskManager.taskManager().async(() -> {
            com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(islandWorld);

            // FAWE가 설치되어 있으면 자동으로 최적화됨
            try (EditSession editSession = WorldEdit.getInstance()
                    .newEditSessionBuilder()
                    .world(weWorld)
                    .maxBlocks(-1)
                    .build()) {

                // 템플릿에 따른 섬 생성
                switch (template) {
                    case "SKYBLOCK" -> generateSkyblockIsland(editSession, centerX, centerZ, radius);
                    case "LARGE" -> generateLargeIsland(editSession, centerX, centerZ, radius);
                    case "WATER" -> generateWaterIsland(editSession, centerX, centerZ, radius);
                    default -> generateBasicIsland(editSession, centerX, centerZ, radius);
                }

                // 바이옴 설정
                BiomeType biomeType = getBiomeType(biomeName);
                if (biomeType != null) {
                    // 섬 영역보다 약간 더 크게 바이옴 설정
                    CuboidRegion biomeRegion = new CuboidRegion(
                            weWorld,
                            BlockVector3.at(centerX - radius - 10, weWorld.getMinY(), centerZ - radius - 10),
                            BlockVector3.at(centerX + radius + 10, weWorld.getMaxY(), centerZ + radius + 10)
                    );
                    // 바이옴 설정
                    for (BlockVector3 pos : biomeRegion) {
                        editSession.setBiome(pos, biomeType);
                    }
                }

                // try-with-resources로 자동 close됨

                LogUtil.info("FAWE로 섬 생성 완료: " + template + " 템플릿, " + biomeName + " 바이옴");

            } catch (MaxChangedBlocksException e) {
                LogUtil.error("섬 생성 중 블록 제한 초과: " + e.getMessage());
            } catch (Exception e) {
                LogUtil.error("섬 생성 중 오류 발생", e);
            }

            // 나무와 스폰 포인트는 메인 스레드에서 설정
            TaskManager.taskManager().sync(() -> {
                // 중앙에 기본 트리 생성
                Location treeLocation = new Location(islandWorld, centerX, ISLAND_HEIGHT + 5, centerZ);
                // 나무 생성 - 다른 방법 사용
                treeLocation.getBlock().setType(Material.OAK_SAPLING);
                treeLocation.getBlock().applyBoneMeal(org.bukkit.block.BlockFace.UP);

                // 스폰 포인트 설정
                islandWorld.setSpawnLocation(centerX, ISLAND_HEIGHT + 6, centerZ);
                return null;
            });
        });
    }

    /**
     * 안전한 BlockState 가져오기
     */
    @NotNull
    private BlockState getSafeBlockState(@Nullable BlockType blockType) {
        if (blockType == null || blockType.getDefaultState() == null) {
            // 기본값으로 AIR 반환
            BlockType air = BlockTypes.AIR;
            if (air != null && air.getDefaultState() != null) {
                return air.getDefaultState();
            }
            // 최악의 경우 예외 발생
            throw new IllegalStateException("Cannot get default block state");
        }
        return blockType.getDefaultState();
    }

    /**
     * 기본 섬 생성
     */
    private void generateBasicIsland(EditSession editSession, int centerX, int centerZ, int radius) throws MaxChangedBlocksException {
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(z - centerZ, 2));
                if (distance <= radius) {
                    // 가장자리 효과를 위한 높이 계산
                    double edgeFactor = 1 - (distance / radius);
                    int extraHeight = (int) (edgeFactor * 3);

                    // 기반암 층
                    editSession.setBlock(BlockVector3.at(x, ISLAND_HEIGHT - 5, z),
                            getSafeBlockState(BlockTypes.BEDROCK));

                    // 돌 층
                    for (int y = ISLAND_HEIGHT - 4; y < ISLAND_HEIGHT + extraHeight; y++) {
                        editSession.setBlock(BlockVector3.at(x, y, z),
                                getSafeBlockState(BlockTypes.STONE));
                    }

                    // 흙 층
                    for (int y = ISLAND_HEIGHT + extraHeight; y < ISLAND_HEIGHT + extraHeight + 3; y++) {
                        editSession.setBlock(BlockVector3.at(x, y, z),
                                getSafeBlockState(BlockTypes.DIRT));
                    }

                    // 잔디 층
                    editSession.setBlock(BlockVector3.at(x, ISLAND_HEIGHT + extraHeight + 3, z),
                            getSafeBlockState(BlockTypes.GRASS_BLOCK));
                }
            }
        }
    }

    /**
     * 스카이블록 섬 생성
     */
    private void generateSkyblockIsland(EditSession editSession, int centerX, int centerZ, int radius) throws MaxChangedBlocksException {
        // 작은 플랫폼
        int smallRadius = 3;
        for (int x = centerX - smallRadius; x <= centerX + smallRadius; x++) {
            for (int z = centerZ - smallRadius; z <= centerZ + smallRadius; z++) {
                editSession.setBlock(BlockVector3.at(x, ISLAND_HEIGHT, z),
                        getSafeBlockState(BlockTypes.GRASS_BLOCK));
                editSession.setBlock(BlockVector3.at(x, ISLAND_HEIGHT - 1, z),
                        getSafeBlockState(BlockTypes.DIRT));
                editSession.setBlock(BlockVector3.at(x, ISLAND_HEIGHT - 2, z),
                        getSafeBlockState(BlockTypes.DIRT));
            }
        }

        // 상자 배치
        editSession.setBlock(BlockVector3.at(centerX + 2, ISLAND_HEIGHT + 1, centerZ),
                getSafeBlockState(BlockTypes.CHEST));
    }

    /**
     * 대형 섬 생성
     */
    private void generateLargeIsland(EditSession editSession, int centerX, int centerZ, int radius) throws MaxChangedBlocksException {
        // 더 크고 다양한 지형
        generateBasicIsland(editSession, centerX, centerZ, (int) (radius * 1.5));

        // 추가 지형 특징
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            int hillX = centerX + random.nextInt(radius) - radius / 2;
            int hillZ = centerZ + random.nextInt(radius) - radius / 2;
            int hillRadius = random.nextInt(10) + 5;

            // 작은 언덕 생성
            for (int x = hillX - hillRadius; x <= hillX + hillRadius; x++) {
                for (int z = hillZ - hillRadius; z <= hillZ + hillRadius; z++) {
                    double distance = Math.sqrt(Math.pow(x - hillX, 2) + Math.pow(z - hillZ, 2));
                    if (distance <= hillRadius) {
                        int hillHeight = (int) ((1 - distance / hillRadius) * 5);
                        for (int y = 0; y < hillHeight; y++) {
                            editSession.setBlock(BlockVector3.at(x, ISLAND_HEIGHT + 4 + y, z),
                                    getSafeBlockState(BlockTypes.DIRT));
                        }
                        editSession.setBlock(BlockVector3.at(x, ISLAND_HEIGHT + 4 + hillHeight, z),
                                getSafeBlockState(BlockTypes.GRASS_BLOCK));
                    }
                }
            }
        }
    }

    /**
     * 수상 섬 생성
     */
    private void generateWaterIsland(EditSession editSession, int centerX, int centerZ, int radius) throws MaxChangedBlocksException {
        // 기본 섬
        generateBasicIsland(editSession, centerX, centerZ, radius);

        // 주변에 물 추가
        for (int x = centerX - radius - 5; x <= centerX + radius + 5; x++) {
            for (int z = centerZ - radius - 5; z <= centerZ + radius + 5; z++) {
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(z - centerZ, 2));
                if (distance > radius && distance <= radius + 5) {
                    for (int y = ISLAND_HEIGHT - 3; y <= ISLAND_HEIGHT + 2; y++) {
                        editSession.setBlock(BlockVector3.at(x, y, z),
                                getSafeBlockState(BlockTypes.WATER));
                    }
                }
            }
        }

        // 모래 해변
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(z - centerZ, 2));
                if (distance > radius - 3 && distance <= radius) {
                    editSession.setBlock(BlockVector3.at(x, ISLAND_HEIGHT + 3, z),
                            getSafeBlockState(BlockTypes.SAND));
                }
            }
        }
    }

    /**
     * 바이옴 타입 가져오기
     */
    private BiomeType getBiomeType(String biomeName) {
        return switch (biomeName) {
            case "PLAINS" -> BiomeTypes.PLAINS;
            case "FOREST" -> BiomeTypes.FOREST;
            case "DESERT" -> BiomeTypes.DESERT;
            case "SNOWY_PLAINS" -> BiomeTypes.SNOWY_PLAINS;
            case "JUNGLE" -> BiomeTypes.JUNGLE;
            case "SWAMP" -> BiomeTypes.SWAMP;
            case "SAVANNA" -> BiomeTypes.SAVANNA;
            case "MUSHROOM_FIELDS" -> BiomeTypes.MUSHROOM_FIELDS;
            case "TAIGA" -> BiomeTypes.TAIGA;
            case "BEACH" -> BiomeTypes.BEACH;
            case "CHERRY_GROVE" -> BiomeTypes.CHERRY_GROVE;
            case "BAMBOO_JUNGLE" -> BiomeTypes.BAMBOO_JUNGLE;
            default -> BiomeTypes.PLAINS;
        };
    }

    /**
     * 섬 월드인지 확인
     */
    public boolean isIslandWorld(World world) {
        return world != null && world.getName().equals(ISLAND_WORLD_NAME);
    }

    /**
     * 섬 삭제
     */
    public CompletableFuture<Void> deleteIsland(int centerX, int centerZ, int size) {
        return CompletableFuture.runAsync(() -> {
            clearIsland(centerX, centerZ, size);
            LogUtil.info(String.format("섬 삭제 완료 - 위치: (%d, %d)", centerX, centerZ));
        });
    }

    /**
     * 섬 초기화 (리셋)
     */
    public CompletableFuture<Void> resetIsland(int centerX, int centerZ, int oldSize, int newSize) {
        return CompletableFuture.runAsync(() -> {
            // 기존 섬 제거
            clearIsland(centerX, centerZ, oldSize);

            // 새 섬 생성
            generateIslandWithFAWE(centerX, centerZ, newSize, "PLAINS", "BASIC");

            LogUtil.info(String.format("섬 초기화 완료 - 위치: (%d, %d)", centerX, centerZ));
        });
    }

    /**
     * 섬 확장
     */
    public CompletableFuture<Void> expandIsland(int centerX, int centerZ, int oldSize, int newSize) {
        return CompletableFuture.runAsync(() -> {
            int oldRadius = oldSize / 2;
            int newRadius = newSize / 2;

            // FAWE를 사용해 확장 영역만 처리
            TaskManager.taskManager().async(() -> {
                com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(islandWorld);

                try (EditSession editSession = WorldEdit.getInstance()
                        .newEditSessionBuilder()
                        .world(weWorld)
                        .maxBlocks(-1)
                        .build()) {

                    // 확장된 영역에만 새 지형 생성
                    for (int x = centerX - newRadius; x <= centerX + newRadius; x++) {
                        for (int z = centerZ - newRadius; z <= centerZ + newRadius; z++) {
                            double oldDistance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(z - centerZ, 2));
                            double newDistance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(z - centerZ, 2));

                            // 기존 섬 범위 밖이고 새 섬 범위 안인 경우에만 생성
                            if (oldDistance > oldRadius && newDistance <= newRadius) {
                                // 기반암 층
                                editSession.setBlock(BlockVector3.at(x, ISLAND_HEIGHT - 5, z),
                                        getSafeBlockState(BlockTypes.BEDROCK));

                                // 돌 층
                                for (int y = ISLAND_HEIGHT - 4; y < ISLAND_HEIGHT; y++) {
                                    editSession.setBlock(BlockVector3.at(x, y, z),
                                            getSafeBlockState(BlockTypes.STONE));
                                }

                                // 흙 층
                                for (int y = ISLAND_HEIGHT; y < ISLAND_HEIGHT + 3; y++) {
                                    editSession.setBlock(BlockVector3.at(x, y, z),
                                            getSafeBlockState(BlockTypes.DIRT));
                                }

                                // 잔디 층
                                editSession.setBlock(BlockVector3.at(x, ISLAND_HEIGHT + 3, z),
                                        getSafeBlockState(BlockTypes.GRASS_BLOCK));
                            }
                        }
                    }

                } catch (Exception e) {
                    LogUtil.error("섬 확장 중 오류", e);
                }
            });

            LogUtil.info(String.format("섬 확장 완료 - 위치: (%d, %d), 크기: %d -> %d",
                    centerX, centerZ, oldSize, newSize));
        });
    }

    /**
     * 섬 제거
     */
    private void clearIsland(int centerX, int centerZ, int size) {
        int radius = size / 2;

        TaskManager.taskManager().async(() -> {
            com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(islandWorld);

            try (EditSession editSession = WorldEdit.getInstance()
                    .newEditSessionBuilder()
                    .world(weWorld)
                    .maxBlocks(-1)
                    .build()) {

                CuboidRegion region = new CuboidRegion(
                        weWorld,
                        BlockVector3.at(centerX - radius, weWorld.getMinY(), centerZ - radius),
                        BlockVector3.at(centerX + radius, weWorld.getMaxY(), centerZ + radius)
                );

                editSession.setBlocks(region, getSafeBlockState(BlockTypes.AIR));

            } catch (Exception e) {
                LogUtil.error("섬 제거 중 오류", e);
            }
        });
    }

    /**
     * 플레이어를 섬으로 텔레포트
     */
    public void teleportToIsland(@NotNull Player player, @NotNull IslandLocationDTO location) {
        Location spawnLocation = new Location(islandWorld,
                location.centerX(), ISLAND_HEIGHT + 6, location.centerZ());

        // 청크 로드
        Chunk chunk = spawnLocation.getChunk();
        if (!chunk.isLoaded()) {
            chunk.load();
        }

        // 텔레포트
        player.teleportAsync(spawnLocation).thenAccept(success -> {
            if (success) {
                player.sendMessage(Component.translatable("island.messages.teleport-success"));
            } else {
                player.sendMessage(Component.translatable("island.messages.teleport-fail"));
            }
        });
    }

}