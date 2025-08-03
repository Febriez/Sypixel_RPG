package com.febrie.rpg.database.service.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.BaseFirestoreService;
import com.febrie.rpg.dto.system.LeaderboardEntryDTO;
import com.febrie.rpg.dto.system.ServerStatsDTO;
import com.febrie.rpg.util.FirestoreUtils;
import com.febrie.rpg.util.LogUtil;
import com.google.cloud.firestore.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 시스템 데이터 Firestore 서비스
 * server-stats, leaderboards 컬렉션 관리
 *
 * @author Febrie, CoffeeTory
 */
public class SystemFirestoreService extends BaseFirestoreService<ServerStatsDTO> {

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
        super(plugin, firestore, "ServerStat", ServerStatsDTO.class);
    }

    @Override
    protected Map<String, Object> toMap(@NotNull ServerStatsDTO stats) {
        Map<String, Object> map = new HashMap<>();
        map.put("onlinePlayers", stats.onlinePlayers());
        map.put("maxPlayers", stats.maxPlayers());
        map.put("totalPlayers", stats.totalPlayers());
        map.put("uptime", stats.uptime());
        map.put("tps", stats.tps());
        map.put("totalPlaytime", stats.totalPlaytime());
        map.put("version", stats.version());
        map.put("lastUpdated", stats.lastUpdated());
        return map;
    }

    @Override
    @Nullable
    protected ServerStatsDTO fromDocument(@NotNull DocumentSnapshot doc) {
        try {
            if (!doc.exists()) {
                return null;
            }

            int onlinePlayers = FirestoreUtils.getInt(doc, "onlinePlayers");
            int maxPlayers = FirestoreUtils.getInt(doc, "maxPlayers", 100);
            int totalPlayers = FirestoreUtils.getInt(doc, "totalPlayers");
            long uptime = FirestoreUtils.getLong(doc, "uptime");
            double tps = FirestoreUtils.getDouble(doc, "tps", 20.0);
            long totalPlaytime = FirestoreUtils.getLong(doc, "totalPlaytime");
            String version = FirestoreUtils.getString(doc, "version", "1.21.7");
            long lastUpdated = FirestoreUtils.getLong(doc, "lastUpdated", System.currentTimeMillis());

            return new ServerStatsDTO(onlinePlayers, maxPlayers, totalPlayers, uptime, tps, totalPlaytime, version, lastUpdated);
        } catch (Exception e) {
            LogUtil.warning("ServerStatsDTO 파싱 실패: " + e.getMessage());
            return null;
        }
    }

    // ===== Server Stats 관련 =====

    /**
     * 서버 통계 조회
     */
    @NotNull
    public CompletableFuture<ServerStatsDTO> getServerStats() {
        return get("current").thenApply(stats -> stats != null ? stats : new ServerStatsDTO());
    }

    /**
     * 서버 통계 업데이트
     */
    @NotNull
    public CompletableFuture<Void> updateServerStats(@NotNull ServerStatsDTO stats) {
        return save("current", stats);
    }

    /**
     * 일일 서버 통계 저장
     *
     * @param date  날짜 (yyyy-MM-dd 형식)
     * @param stats 서버 통계
     * @return 저장 결과
     */
    @NotNull
    public CompletableFuture<Void> saveDailyStats(@NotNull String date, @NotNull ServerStatsDTO stats) {
        Map<String, Object> data = toMap(stats);

        return CompletableFuture.runAsync(() -> {
            try {
                // ServerStat/Daily/날짜 경로에 저장
                firestore.collection("ServerStat")
                        .document("Daily")
                        .collection(date.substring(0, 7)) // yyyy-MM 형식으로 월별 분류
                        .document(date)
                        .set(data)
                        .get();

                LogUtil.info("일일 서버 통계 저장 성공 [" + date + "]");
            } catch (Exception e) {
                LogUtil.error("일일 서버 통계 저장 실패 [" + date + "]", e);
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * 서버 통계 증가 (원자적 업데이트)
     */
    @NotNull
    public CompletableFuture<Void> incrementServerStat(@NotNull String field, long amount) {
        return incrementField("current", field, amount);
    }

    // ===== Leaderboard 관련 =====

    /**
     * 리더보드 조회
     */
    @NotNull
    public CompletableFuture<List<LeaderboardEntryDTO>> getLeaderboard(@NotNull LeaderboardType type, int limit) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                QuerySnapshot snapshot = firestore.collection("Leaderboard")
                        .document(type.getId())
                        .collection("Entry")
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
    public CompletableFuture<Void> updateLeaderboardEntry(@NotNull LeaderboardType type, @NotNull String playerUuid, @NotNull String playerName, long value) {
        LeaderboardEntryDTO entry = new LeaderboardEntryDTO(playerUuid, playerName, 0, value, type.getId());
        Map<String, Object> data = leaderboardEntryToMap(entry);

        return CompletableFuture.runAsync(() -> {
            try {
                firestore.collection("Leaderboard")
                        .document(type.getId())
                        .collection("Entry")
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
                DocumentSnapshot playerDoc = firestore.collection("Leaderboard")
                        .document(type.getId())
                        .collection("Entry")
                        .document(playerUuid)
                        .get()
                        .get();

                if (!playerDoc.exists()) {
                    return -1; // 순위 없음
                }

                long playerValue = FirestoreUtils.getLong(playerDoc, "value", -1L);
                if (playerValue == -1L) {
                    return -1;
                }

                // 플레이어보다 높은 값을 가진 엔트리 수 계산
                QuerySnapshot higherEntries = firestore.collection("Leaderboard")
                        .document(type.getId())
                        .collection("Entry")
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
                QuerySnapshot snapshot = firestore.collection("Leaderboard")
                        .document(type.getId())
                        .collection("Entry")
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

    private Map<String, Object> leaderboardEntryToMap(@NotNull LeaderboardEntryDTO entry) {
        Map<String, Object> map = new HashMap<>();
        map.put("playerUuid", entry.playerUuid());
        map.put("playerName", entry.playerName());
        map.put("rank", entry.rank());
        map.put("value", entry.value());
        map.put("type", entry.type());
        map.put("lastUpdated", entry.lastUpdated());
        return map;
    }

    @Nullable
    private LeaderboardEntryDTO fromLeaderboardDocument(@NotNull DocumentSnapshot doc, int rank) {
        try {
            return new LeaderboardEntryDTO(
                    FirestoreUtils.getString(doc, "playerUuid", ""),
                    FirestoreUtils.getString(doc, "playerName", ""),
                    rank,
                    FirestoreUtils.getLong(doc, "value"),
                    FirestoreUtils.getString(doc, "type", ""),
                    FirestoreUtils.getLong(doc, "lastUpdated"));
        } catch (Exception e) {
            LogUtil.warning("LeaderboardEntryDTO 파싱 실패: " + e.getMessage());
            return null;
        }
    }
}