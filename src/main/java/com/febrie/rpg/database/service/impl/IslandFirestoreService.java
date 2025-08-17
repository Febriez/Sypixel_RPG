package com.febrie.rpg.database.service.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.GenericFirestoreService;
import com.febrie.rpg.dto.island.*;
import com.febrie.rpg.util.LogUtil;
import com.google.cloud.firestore.Firestore;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 섬 데이터 Firestore 서비스
 * islands 컬렉션 관리
 *
 * @author Febrie, CoffeeTory
 */
public class IslandFirestoreService {

    private static final String COLLECTION_NAME = "Island";
    private final GenericFirestoreService<IslandDTO> service;
    private final Firestore firestore;

    public IslandFirestoreService(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
        this.firestore = firestore;
        this.service = GenericFirestoreService.create(plugin, firestore, COLLECTION_NAME, IslandDTO.class, IslandDTO::toMap, IslandDTO::fromMap, id -> null // 섬은 기본값이 없음
        );
    }

    /**
     * 문서 ID로 데이터 조회
     */
    @NotNull
    public CompletableFuture<IslandDTO> get(@NotNull String id) {
        return service.get(id);
    }

    /**
     * 데이터 저장
     */
    public CompletableFuture<Void> save(@NotNull String id, @NotNull IslandDTO data) {
        return service.save(id, data);
    }

    /**
     * 데이터 삭제
     */
    public CompletableFuture<Void> delete(@NotNull String id) {
        return service.delete(id);
    }

    // ===== 부분 로드 메서드들 =====

    /**
     * 섬 기본 정보만 조회 (목록 표시용)
     */
    @NotNull
    public CompletableFuture<IslandCoreDTO> getCore(@NotNull String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                var docRef = firestore.collection(COLLECTION_NAME)
                        .document(id);
                var future = docRef.get();
                var document = future.get(10, java.util.concurrent.TimeUnit.SECONDS);

                if (document.exists()) {
                    return IslandCoreDTO.fromMap(document.getData());
                }
                return null;
            } catch (Exception e) {
                LogUtil.error("섬 기본 정보 조회 실패: " + id, e);
                return null;
            }
        });
    }

    /**
     * 섬 멤버십 정보만 조회
     */
    @NotNull
    public CompletableFuture<IslandMembershipDTO> getMembership(@NotNull String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                var docRef = firestore.collection(COLLECTION_NAME)
                        .document(id);
                var future = docRef.get();
                var document = future.get(10, java.util.concurrent.TimeUnit.SECONDS);

                if (document.exists()) {
                    Map<String, Object> data = document.getData();

                    // 멤버십 관련 필드만 추출
                    List<IslandMemberDTO> members = new ArrayList<>();
                    Object membersObj = data.get("members");
                    if (membersObj instanceof List) {
                        for (Object obj : (List<?>) membersObj) {
                            if (obj instanceof Map) {
                                members.add(IslandMemberDTO.fromMap((Map<String, Object>) obj));
                            }
                        }
                    }

                    List<IslandWorkerDTO> workers = new ArrayList<>();
                    Object workersObj = data.get("workers");
                    if (workersObj instanceof List) {
                        for (Object obj : (List<?>) workersObj) {
                            if (obj instanceof Map) {
                                workers.add(IslandWorkerDTO.fromMap((Map<String, Object>) obj));
                            }
                        }
                    }

                    Map<String, Long> contributions = new HashMap<>();
                    Object contribObj = data.get("contributions");
                    if (contribObj instanceof Map) {
                        Map<?, ?> contribMap = (Map<?, ?>) contribObj;
                        for (Map.Entry<?, ?> entry : contribMap.entrySet()) {
                            if (entry.getKey() instanceof String && entry.getValue() instanceof Number) {
                                contributions.put((String) entry.getKey(), ((Number) entry.getValue()).longValue());
                            }
                        }
                    }

                    return new IslandMembershipDTO(id, members, workers, contributions);
                }
                return null;
            } catch (Exception e) {
                LogUtil.error("섬 멤버십 정보 조회 실패: " + id, e);
                return null;
            }
        });
    }

    /**
     * 섬 소셜 정보만 조회
     */
    @NotNull
    public CompletableFuture<IslandSocialDTO> getSocial(@NotNull String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                var docRef = firestore.collection(COLLECTION_NAME)
                        .document(id);
                var future = docRef.get();
                var document = future.get(10, java.util.concurrent.TimeUnit.SECONDS);

                if (document.exists()) {
                    Map<String, Object> data = document.getData();

                    // 소셜 관련 필드만 추출
                    List<IslandInviteDTO> invites = new ArrayList<>();
                    Object invitesObj = data.get("pendingInvites");
                    if (invitesObj instanceof List) {
                        for (Object obj : (List<?>) invitesObj) {
                            if (obj instanceof Map) {
                                IslandInviteDTO invite = IslandInviteDTO.fromMap((Map<String, Object>) obj);
                                if (!invite.isExpired()) {
                                    invites.add(invite);
                                }
                            }
                        }
                    }

                    List<IslandVisitDTO> visits = new ArrayList<>();
                    Object visitsObj = data.get("recentVisits");
                    if (visitsObj instanceof List) {
                        for (Object obj : (List<?>) visitsObj) {
                            if (obj instanceof Map) {
                                visits.add(IslandVisitDTO.fromMap((Map<String, Object>) obj));
                            }
                        }
                    }

                    return new IslandSocialDTO(id, invites, visits);
                }
                return null;
            } catch (Exception e) {
                LogUtil.error("섬 소셜 정보 조회 실패: " + id, e);
                return null;
            }
        });
    }

    /**
     * 섬 설정 정보만 조회
     */
    @NotNull
    public CompletableFuture<IslandConfigurationDTO> getConfiguration(@NotNull String id) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                var docRef = firestore.collection(COLLECTION_NAME)
                        .document(id);
                var future = docRef.get();
                var document = future.get(10, java.util.concurrent.TimeUnit.SECONDS);

                if (document.exists()) {
                    Map<String, Object> data = document.getData();

                    // 설정 관련 필드만 추출
                    IslandSpawnDTO spawnData = IslandSpawnDTO.createDefault();
                    Object spawnObj = data.get("spawnData");
                    if (spawnObj instanceof Map) {
                        spawnData = IslandSpawnDTO.fromMap((Map<String, Object>) spawnObj);
                    }

                    IslandUpgradeDTO upgradeData = IslandUpgradeDTO.createDefault();
                    Object upgradeObj = data.get("upgradeData");
                    if (upgradeObj instanceof Map) {
                        upgradeData = IslandUpgradeDTO.fromMap((Map<String, Object>) upgradeObj);
                    }

                    IslandPermissionDTO permissions = IslandPermissionDTO.createDefault();
                    Object permissionsObj = data.get("permissions");
                    if (permissionsObj instanceof Map) {
                        permissions = IslandPermissionDTO.fromMap((Map<String, Object>) permissionsObj);
                    }

                    IslandSettingsDTO settings = IslandSettingsDTO.createDefault();
                    Object settingsObj = data.get("settings");
                    if (settingsObj instanceof Map) {
                        settings = IslandSettingsDTO.fromMap((Map<String, Object>) settingsObj);
                    }

                    return new IslandConfigurationDTO(id, spawnData, upgradeData, permissions, settings);
                }
                return null;
            } catch (Exception e) {
                LogUtil.error("섬 설정 정보 조회 실패: " + id, e);
                return null;
            }
        });
    }

    // ===== 기존 기능 메소드들 =====

    /**
     * 플레이어가 소유한 섬 찾기
     */
    public CompletableFuture<IslandDTO> findByOwner(@NotNull String ownerUuid) {
        return service.query("ownerUuid", ownerUuid)
                .thenApply(islands -> islands.isEmpty() ? null : islands.get(0));
    }

    /**
     * 공개 섬 목록 조회 (전체 정보)
     */
    public CompletableFuture<List<IslandDTO>> getPublicIslands() {
        return service.query("isPublic", true);
    }

    /**
     * 공개 섬 목록 조회 (기본 정보만)
     */
    public CompletableFuture<List<IslandCoreDTO>> getPublicIslandCores() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<IslandCoreDTO> islands = new ArrayList<>();

                var future = firestore.collection(COLLECTION_NAME)
                        .whereEqualTo("isPublic", true)
                        .orderBy("lastActivity", com.google.cloud.firestore.Query.Direction.DESCENDING)
                        .get();
                var querySnapshot = future.get(10, java.util.concurrent.TimeUnit.SECONDS);

                for (var doc : querySnapshot.getDocuments()) {
                    IslandCoreDTO island = IslandCoreDTO.fromMap(doc.getData());
                    if (island != null) {
                        islands.add(island);
                    }
                }

                return islands;
            } catch (Exception e) {
                LogUtil.error("공개 섬 목록 조회 실패", e);
                return new ArrayList<>();
            }
        });
    }

    /**
     * 모든 섬 기본 정보 조회 (캐시용)
     */
    public CompletableFuture<List<IslandCoreDTO>> getAllIslandCores() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<IslandCoreDTO> islands = new ArrayList<>();

                var future = firestore.collection(COLLECTION_NAME)
                        .orderBy("lastActivity", com.google.cloud.firestore.Query.Direction.DESCENDING)
                        .get();
                var querySnapshot = future.get(30, java.util.concurrent.TimeUnit.SECONDS);

                for (var doc : querySnapshot.getDocuments()) {
                    IslandCoreDTO island = IslandCoreDTO.fromMap(doc.getData());
                    if (island != null) {
                        islands.add(island);
                    }
                }

                LogUtil.debug("Firestore에서 " + islands.size() + "개 섬 기본 데이터 로드");
                return islands;
            } catch (Exception e) {
                LogUtil.error("모든 섬 기본 데이터 로드 실패", e);
                return new ArrayList<>();
            }
        });
    }

    /**
     * 섬 이름으로 검색 (부분 일치)
     * 참고: Firestore는 부분 문자열 검색을 네이티브로 지원하지 않으므로
     * 전체 목록을 가져온 후 필터링
     */
    public CompletableFuture<List<IslandDTO>> searchByName(@NotNull String namePart) {
        String lowerSearch = namePart.toLowerCase();

        return getPublicIslands().thenApply(islands -> islands.stream()
                .filter(island -> island.core().islandName()
                        .toLowerCase()
                        .contains(lowerSearch))
                .collect(Collectors.toList()));
    }

    /**
     * 모든 섬 데이터 로드 (사전 로드용)
     * 서버 시작 시 모든 섬 데이터를 캐시에 로드하기 위해 사용
     */
    public CompletableFuture<List<IslandDTO>> getAllIslands() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<IslandDTO> islands = new ArrayList<>();

                // 모든 섬 데이터를 가져옴
                var future = firestore.collection(COLLECTION_NAME)
                        .orderBy("lastActivity", com.google.cloud.firestore.Query.Direction.DESCENDING)
                        .get();
                var querySnapshot = future.get(30, java.util.concurrent.TimeUnit.SECONDS);
                for (var doc : querySnapshot.getDocuments())
                    islands.add(IslandDTO.fromMap(doc.getData()));
                LogUtil.debug("Firestore에서 " + islands.size() + "개 섬 데이터 로드");
                return islands;
            } catch (Exception e) {
                LogUtil.error("모든 섬 데이터 로드 실패", e);
                return new ArrayList<>();
            }
        });
    }
}
