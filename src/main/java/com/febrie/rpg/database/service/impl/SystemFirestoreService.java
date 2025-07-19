package com.febrie.rpg.database.service.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.system.LeaderboardEntryDTO;
import com.febrie.rpg.dto.system.ServerStatsDTO;
import com.febrie.rpg.util.LogUtil;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.Query;
import com.google.cloud.firestore.QuerySnapshot;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 시스템 데이터 Firestore 서비스
 * server-stats, leaderboards 컬렉션 관리
 *
 * @author Febrie, CoffeeTory
 */
public class SystemFirestoreService {
    
    private final RPGMain plugin;
    private final Firestore firestore;
    
    // 리더보드 타입
    public enum LeaderboardType {
        LEVEL("level", "level"),
        MONEY("money", "currencies.gold"),
        PLAYTIME("playtime", "playtime"),
        ISLAND_SIZE("island_size", "size"),
        QUEST_COMPLETED("quest_completed", "completedCount"),
        PVP_KILLS("pvp_kills", "pvpKills"),
        PVE_KILLS("pve_kills", "pveKills");
        
        private final String id;
        private final String fieldPath;
        
        LeaderboardType(String id, String fieldPath) {
            this.id = id;
            this.fieldPath = fieldPath;
        }
        
        public String getId() {
            return id;
        }
        
        public String getFieldPath() {
            return fieldPath;
        }
    }
    
    public SystemFirestoreService(@NotNull RPGMain plugin, @NotNull Firestore firestore) {
        this.plugin = plugin;
        this.firestore = firestore;
    }
    
    // ===== Server Stats 관련 =====
    
    /**
     * 서버 통계 조회
     */
    @NotNull
    public CompletableFuture<ServerStatsDTO> getServerStats() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentSnapshot doc = firestore.collection("server-stats")
                        .document("current")
                        .get()
                        .get();
                
                if (!doc.exists()) {
                    // 기본값 반환
                    return new ServerStatsDTO(0, 0, 0, 0, 0, 0, 0, 
                            System.currentTimeMillis(), new HashMap<>());
                }
                
                return fromServerStatsDocument(doc);
                
            } catch (Exception e) {
                LogUtil.warning("서버 통계 조회 실패: " + e.getMessage());
                return new ServerStatsDTO(0, 0, 0, 0, 0, 0, 0, 
                        System.currentTimeMillis(), new HashMap<>());
            }
        });
    }
    
    /**
     * 서버 통계 업데이트
     */
    @NotNull
    public CompletableFuture<Void> updateServerStats(@NotNull ServerStatsDTO stats) {
        Map<String, Object> data = serverStatsToMap(stats);
        
        return CompletableFuture.runAsync(() -> {
            try {
                firestore.collection("server-stats")
                        .document("current")
                        .set(data)
                        .get();
                LogUtil.info("서버 통계 업데이트 성공");
            } catch (Exception e) {
                LogUtil.warning("서버 통계 업데이트 실패: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }
    
    /**
     * 서버 통계 증가 (원자적 업데이트)
     */
    @NotNull
    public CompletableFuture<Void> incrementServerStat(@NotNull String field, long amount) {
        return CompletableFuture.runAsync(() -> {
            try {
                firestore.collection("server-stats")
                        .document("current")
                        .update(field, com.google.cloud.firestore.FieldValue.increment(amount))
                        .get();
            } catch (Exception e) {
                LogUtil.warning("서버 통계 증가 실패 [" + field + "]: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }
    
    // ===== Leaderboard 관련 =====
    
    /**
     * 리더보드 조회
     */
    @NotNull
    public CompletableFuture<List<LeaderboardEntryDTO>> getLeaderboard(@NotNull LeaderboardType type, int limit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String collectionName = getLeaderboardCollection(type);
                
                QuerySnapshot snapshot = firestore.collection("leaderboards")
                        .document(type.getId())
                        .collection("entries")
                        .orderBy("value", Query.Direction.DESCENDING)
                        .limit(limit)
                        .get()
                        .get();
                
                List<LeaderboardEntryDTO> entries = new ArrayList<>();
                int rank = 1;
                
                for (DocumentSnapshot doc : snapshot.getDocuments()) {
                    LeaderboardEntryDTO entry = fromLeaderboardDocument(doc, rank++);
                    if (entry != null) {
                        entries.add(entry);
                    }
                }
                
                return entries;
                
            } catch (Exception e) {
                LogUtil.warning("리더보드 조회 실패 [" + type.getId() + "]: " + e.getMessage());
                return new ArrayList<>();
            }
        });
    }
    
    /**
     * 리더보드 엔트리 업데이트
     */
    @NotNull
    public CompletableFuture<Void> updateLeaderboardEntry(@NotNull LeaderboardType type,
                                                           @NotNull String playerUuid,
                                                           @NotNull String playerName,
                                                           long value) {
        LeaderboardEntryDTO entry = new LeaderboardEntryDTO(
                playerUuid, playerName, value, 0, System.currentTimeMillis()
        );
        
        Map<String, Object> data = leaderboardEntryToMap(entry);
        
        return CompletableFuture.runAsync(() -> {
            try {
                firestore.collection("leaderboards")
                        .document(type.getId())
                        .collection("entries")
                        .document(playerUuid)
                        .set(data)
                        .get();
            } catch (Exception e) {
                LogUtil.warning("리더보드 업데이트 실패 [" + type.getId() + "]: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }
    
    /**
     * 플레이어의 리더보드 순위 조회
     */
    @NotNull
    public CompletableFuture<Integer> getPlayerRank(@NotNull LeaderboardType type, @NotNull String playerUuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 먼저 플레이어의 값 조회
                DocumentSnapshot playerDoc = firestore.collection("leaderboards")
                        .document(type.getId())
                        .collection("entries")
                        .document(playerUuid)
                        .get()
                        .get();
                
                if (!playerDoc.exists()) {
                    return -1; // 순위 없음
                }
                
                Long playerValue = playerDoc.getLong("value");
                if (playerValue == null) {
                    return -1;
                }
                
                // 플레이어보다 높은 값을 가진 엔트리 수 계산
                QuerySnapshot higherEntries = firestore.collection("leaderboards")
                        .document(type.getId())
                        .collection("entries")
                        .whereGreaterThan("value", playerValue)
                        .get()
                        .get();
                
                return higherEntries.size() + 1;
                
            } catch (Exception e) {
                LogUtil.warning("플레이어 순위 조회 실패: " + e.getMessage());
                return -1;
            }
        });
    }
    
    /**
     * 리더보드 초기화
     */
    @NotNull
    public CompletableFuture<Void> resetLeaderboard(@NotNull LeaderboardType type) {
        return CompletableFuture.runAsync(() -> {
            try {
                // 모든 엔트리 삭제
                QuerySnapshot snapshot = firestore.collection("leaderboards")
                        .document(type.getId())
                        .collection("entries")
                        .get()
                        .get();
                
                for (DocumentSnapshot doc : snapshot.getDocuments()) {
                    doc.getReference().delete().get();
                }
                
                LogUtil.info("리더보드 초기화 완료: " + type.getId());
                
            } catch (Exception e) {
                LogUtil.warning("리더보드 초기화 실패: " + e.getMessage());
                throw new RuntimeException(e);
            }
        });
    }
    
    // ===== 헬퍼 메소드들 =====
    
    private Map<String, Object> serverStatsToMap(@NotNull ServerStatsDTO stats) {
        Map<String, Object> map = new HashMap<>();
        map.put("totalPlayers", stats.totalPlayers());
        map.put("totalIslands", stats.totalIslands());
        map.put("totalQuests", stats.totalQuests());
        map.put("totalMoney", stats.totalMoney());
        map.put("totalPlaytime", stats.totalPlaytime());
        map.put("totalMonstersKilled", stats.totalMonstersKilled());
        map.put("totalItemsCreated", stats.totalItemsCreated());
        map.put("lastUpdated", stats.lastUpdated());
        map.put("additionalStats", stats.additionalStats());
        return map;
    }
    
    private ServerStatsDTO fromServerStatsDocument(@NotNull DocumentSnapshot doc) {
        try {
            Map<String, Long> additionalStats = new HashMap<>();
            Map<String, Object> additionalData = doc.get("additionalStats", Map.class);
            if (additionalData != null) {
                additionalData.forEach((key, value) -> {
                    if (value instanceof Number) {
                        additionalStats.put(key, ((Number) value).longValue());
                    }
                });
            }
            
            return new ServerStatsDTO(
                    doc.getLong("totalPlayers").intValue(),
                    doc.getLong("totalIslands").intValue(),
                    doc.getLong("totalQuests").intValue(),
                    doc.getLong("totalMoney"),
                    doc.getLong("totalPlaytime"),
                    doc.getLong("totalMonstersKilled"),
                    doc.getLong("totalItemsCreated"),
                    doc.getLong("lastUpdated"),
                    additionalStats
            );
        } catch (Exception e) {
            LogUtil.warning("ServerStatsDTO 파싱 실패: " + e.getMessage());
            return new ServerStatsDTO(0, 0, 0, 0, 0, 0, 0, 
                    System.currentTimeMillis(), new HashMap<>());
        }
    }
    
    private Map<String, Object> leaderboardEntryToMap(@NotNull LeaderboardEntryDTO entry) {
        Map<String, Object> map = new HashMap<>();
        map.put("playerUuid", entry.playerUuid());
        map.put("playerName", entry.playerName());
        map.put("value", entry.value());
        map.put("lastUpdated", entry.lastUpdated());
        return map;
    }
    
    private LeaderboardEntryDTO fromLeaderboardDocument(@NotNull DocumentSnapshot doc, int rank) {
        try {
            return new LeaderboardEntryDTO(
                    doc.getString("playerUuid"),
                    doc.getString("playerName"),
                    doc.getLong("value"),
                    rank,
                    doc.getLong("lastUpdated")
            );
        } catch (Exception e) {
            LogUtil.warning("LeaderboardEntryDTO 파싱 실패: " + e.getMessage());
            return null;
        }
    }
    
    private String getLeaderboardCollection(@NotNull LeaderboardType type) {
        return "leaderboard_" + type.getId();
    }
}