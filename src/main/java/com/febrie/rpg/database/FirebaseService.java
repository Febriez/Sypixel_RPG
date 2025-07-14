package com.febrie.rpg.database;

import com.febrie.rpg.dto.*;
import com.febrie.rpg.job.JobType;
import com.febrie.rpg.util.LogUtil;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.firestore.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * Firebase Firestore 데이터베이스 서비스
 * Cloud Firestore를 사용하여 모든 DTO 데이터의 저장/로드 로직을 중앙화
 * Private Key만 환경 변수로 관리, 나머지는 하드코딩
 *
 * @author Febrie, CoffeeTory
 */
public class FirebaseService {

    private Firestore firestore;
    private boolean connected = false;

    // 캐시 시스템
    private final Map<String, PlayerDTO> playerCache = new ConcurrentHashMap<>();
    private final Map<String, Long> cacheTimestamps = new ConcurrentHashMap<>();
    private static final long CACHE_DURATION = 5 * 60 * 1000; // 5분

    // 컬렉션 이름들
    private static final String PLAYERS_COLLECTION = "Player";
    private static final String LEADERBOARDS_COLLECTION = "Leaderboard";
    private static final String SERVER_STATS_COLLECTION = "ServerStat";

    // 서브컬렉션 이름들
    private static final String STATS_SUBCOLLECTION = "Stat";
    private static final String TALENTS_SUBCOLLECTION = "Talent";
    private static final String PROGRESS_SUBCOLLECTION = "Progress";

    // Firebase 설정 상수 (Private Key 제외)
    private static final String FIREBASE_PROJECT_ID = "sypixel-rpg";
    private static final String FIREBASE_CLIENT_EMAIL = "firebase-adminsdk-fbsvc@sypixel-rpg.iam.gserviceaccount.com";

    // 환경변수 이름들 (Private Key만)
    private static final String ENV_PRIVATE_KEY = "FIREBASE_PRIVATE_KEY";

    public FirebaseService(@NotNull Plugin plugin) {
        initializeFirebase();
    }

    /**
     * Firebase 초기화
     */
    private void initializeFirebase() {
        try {
            // Private Key 환경 변수 확인
            String privateKey = System.getenv(ENV_PRIVATE_KEY);
            if (privateKey == null || privateKey.isEmpty()) {
                LogUtil.error("Firebase 초기화 실패: " + ENV_PRIVATE_KEY + " 환경변수가 설정되지 않았습니다.");
                LogUtil.error("환경 변수 설정 예시:");
                LogUtil.error("  export " + ENV_PRIVATE_KEY + "=\"-----BEGIN PRIVATE KEY-----\\nYOUR_PRIVATE_KEY\\n-----END PRIVATE KEY-----\"");
                return;
            }

            GoogleCredentials credentials = createCredentials(privateKey);
            if (credentials == null) return;
            FirestoreOptions options = FirestoreOptions.newBuilder()
                    .setCredentials(credentials)
                    .setProjectId(FIREBASE_PROJECT_ID)
                    .build();
            firestore = options.getService();
            testConnection();

        } catch (Exception e) {
            LogUtil.error("Firebase 초기화 중 오류 발생", e);
            connected = false;
        }
    }

    /**
     * Private Key로부터 인증 정보 생성
     */
    private GoogleCredentials createCredentials(String privateKey) {
        try {
            // Private Key 형식 정리 (환경 변수에서 \\n을 실제 개행으로 변환)
            privateKey = privateKey.replace("\\n", "\n");

            // ServiceAccountCredentials 빌더 사용
            ServiceAccountCredentials.Builder builder = ServiceAccountCredentials.newBuilder()
                    .setProjectId(FIREBASE_PROJECT_ID)
                    .setPrivateKeyString(privateKey)
                    .setClientEmail(FIREBASE_CLIENT_EMAIL);

            // 기본 스코프 설정
            builder.setScopes(Arrays.asList(
                    "https://www.googleapis.com/auth/cloud-platform",
                    "https://www.googleapis.com/auth/datastore"
            ));

            GoogleCredentials credentials = builder.build();
            LogUtil.info("Firebase 인증 정보를 성공적으로 생성했습니다.");
            return credentials;

        } catch (Exception e) {
            LogUtil.error("Firebase 인증 정보 생성 실패", e);
            LogUtil.error("Private Key 형식을 확인하세요. \\n을 포함한 전체 키가 필요합니다.");
            return null;
        }
    }

    /**
     * 연결 테스트
     */
    private void testConnection() {
        try {
            firestore.collection("_test").document("ping")
                    .set(Map.of("timestamp", FieldValue.serverTimestamp())).get();
            connected = true;
            LogUtil.info("Firebase Firestore 연결 성공!");
        } catch (Exception e) {
            connected = false;
            LogUtil.error("Firebase Firestore 연결 실패", e);
        }
    }

    /**
     * 연결 상태 확인
     */
    public boolean isConnected() {
        return connected;
    }

    // ========== 개별 데이터 로드 메서드 ==========

    /**
     * 플레이어 기본 정보 로드
     */
    public CompletableFuture<PlayerDTO> loadPlayer(@NotNull String uuid) {
        return loadDocumentNullable(
                firestore.collection(PLAYERS_COLLECTION).document(uuid),
                this::mapToPlayerDTO,
                "PlayerDTO"
        );
    }

    /**
     * 스탯 정보 로드
     */
    public CompletableFuture<StatsDTO> loadStats(@NotNull String uuid) {
        return loadStatsDTO(firestore.collection(PLAYERS_COLLECTION).document(uuid));
    }

    /**
     * 특성 정보 로드
     */
    public CompletableFuture<TalentDTO> loadTalents(@NotNull String uuid) {
        return loadTalentDTO(firestore.collection(PLAYERS_COLLECTION).document(uuid));
    }

    /**
     * 진행도 정보 로드
     */
    public CompletableFuture<ProgressDTO> loadProgress(@NotNull String uuid) {
        return loadProgressDTO(firestore.collection(PLAYERS_COLLECTION).document(uuid));
    }

    /**
     * 지갑 정보 로드
     */
    public CompletableFuture<WalletDTO> loadWallet(@NotNull String uuid) {
        return loadWalletDTO(firestore.collection(PLAYERS_COLLECTION).document(uuid));
    }

    // ========== 리더보드 관리 ==========

    /**
     * 리더보드 엔트리 저장
     */
    public CompletableFuture<Boolean> saveLeaderboardEntry(@NotNull LeaderboardEntryDTO entry) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                // 문서 ID: type_playerUuid (예: level_12345-6789-abcd-efgh)
                String documentId = entry.type() + "_" + entry.playerUuid();

                firestore.collection(LEADERBOARDS_COLLECTION)
                        .document(documentId)
                        .set(convertLeaderboardToMap(entry))
                        .get();

                LogUtil.debug("리더보드 엔트리 저장 완료: " + documentId);
                return true;
            } catch (Exception e) {
                LogUtil.error("리더보드 엔트리 저장 실패: " + entry.playerName(), e);
                return false;
            }
        });
    }

    /**
     * 특정 타입의 리더보드 로드 (상위 N명)
     */
    public CompletableFuture<List<LeaderboardEntryDTO>> loadLeaderboard(@NotNull String type, int limit) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                QuerySnapshot querySnapshot = firestore.collection(LEADERBOARDS_COLLECTION)
                        .whereEqualTo("type", type)
                        .orderBy("rank", Query.Direction.ASCENDING)
                        .limit(limit)
                        .get()
                        .get();

                List<LeaderboardEntryDTO> leaderboard = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    LeaderboardEntryDTO entry = mapToLeaderboardDTO(doc.getData());
                    if (entry != null) {
                        leaderboard.add(entry);
                    }
                }

                LogUtil.debug("리더보드 로드 완료: " + type + " (항목 " + leaderboard.size() + "개)");
                return leaderboard;
            } catch (Exception e) {
                LogUtil.error("리더보드 로드 실패: " + type, e);
                return new ArrayList<>();
            }
        });
    }

    /**
     * 플레이어의 특정 타입 리더보드 순위 조회
     */
    public CompletableFuture<LeaderboardEntryDTO> loadPlayerLeaderboardEntry(@NotNull String playerUuid, @NotNull String type) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(null);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                String documentId = type + "_" + playerUuid;
                DocumentSnapshot doc = firestore.collection(LEADERBOARDS_COLLECTION)
                        .document(documentId)
                        .get()
                        .get();

                if (doc.exists()) {
                    return mapToLeaderboardDTO(doc.getData());
                }
                return null;
            } catch (Exception e) {
                LogUtil.error("플레이어 리더보드 엔트리 로드 실패: " + playerUuid + " (" + type + ")", e);
                return null;
            }
        });
    }

    /**
     * 리더보드 엔트리 삭제
     */
    public CompletableFuture<Boolean> deleteLeaderboardEntry(@NotNull String playerUuid, @NotNull String type) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                String documentId = type + "_" + playerUuid;
                firestore.collection(LEADERBOARDS_COLLECTION)
                        .document(documentId)
                        .delete()
                        .get();

                LogUtil.debug("리더보드 엔트리 삭제 완료: " + documentId);
                return true;
            } catch (Exception e) {
                LogUtil.error("리더보드 엔트리 삭제 실패: " + playerUuid + " (" + type + ")", e);
                return false;
            }
        });
    }

    // ========== 서버 통계 관리 ==========

    /**
     * 서버 통계 저장
     */
    public CompletableFuture<Boolean> saveServerStats(@NotNull ServerStatsDTO serverStats) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                // 문서 ID는 현재 날짜 기반 (예: 2025-01-15)
                String documentId = java.time.LocalDate.now().toString();

                firestore.collection(SERVER_STATS_COLLECTION)
                        .document(documentId)
                        .set(convertServerStatsToMap(serverStats))
                        .get();

                LogUtil.debug("서버 통계 저장 완료: " + documentId);
                return true;
            } catch (Exception e) {
                LogUtil.error("서버 통계 저장 실패", e);
                return false;
            }
        });
    }

    /**
     * 최신 서버 통계 로드
     */
    public CompletableFuture<ServerStatsDTO> loadLatestServerStats() {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(new ServerStatsDTO());
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                QuerySnapshot querySnapshot = firestore.collection(SERVER_STATS_COLLECTION)
                        .orderBy("lastUpdated", Query.Direction.DESCENDING)
                        .limit(1)
                        .get()
                        .get();

                if (!querySnapshot.isEmpty()) {
                    QueryDocumentSnapshot doc = querySnapshot.getDocuments().get(0);
                    ServerStatsDTO stats = mapToServerStatsDTO(doc.getData());
                    LogUtil.debug("최신 서버 통계 로드 완료");
                    return stats;
                }

                LogUtil.debug("서버 통계가 없어 기본값 반환");
                return new ServerStatsDTO();
            } catch (Exception e) {
                LogUtil.error("서버 통계 로드 실패", e);
                return new ServerStatsDTO();
            }
        });
    }

    /**
     * 특정 날짜의 서버 통계 로드
     */
    public CompletableFuture<ServerStatsDTO> loadServerStatsByDate(@NotNull String date) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(new ServerStatsDTO());
        }

        return loadDocument(
                firestore.collection(SERVER_STATS_COLLECTION).document(date),
                this::mapToServerStatsDTO,
                new ServerStatsDTO(),
                "ServerStatsDTO"
        );
    }

    /**
     * 최근 N일간의 서버 통계 로드
     */
    public CompletableFuture<List<ServerStatsDTO>> loadRecentServerStats(int days) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                QuerySnapshot querySnapshot = firestore.collection(SERVER_STATS_COLLECTION)
                        .orderBy("lastUpdated", Query.Direction.DESCENDING)
                        .limit(days)
                        .get()
                        .get();

                List<ServerStatsDTO> statsList = new ArrayList<>();
                for (QueryDocumentSnapshot doc : querySnapshot) {
                    ServerStatsDTO stats = mapToServerStatsDTO(doc.getData());
                    statsList.add(stats);
                }

                LogUtil.debug("최근 " + days + "일 서버 통계 로드 완료 (항목 " + statsList.size() + "개)");
                return statsList;
            } catch (Exception e) {
                LogUtil.error("최근 서버 통계 로드 실패", e);
                return new ArrayList<>();
            }
        });
    }

    // ========== 플레이어 데이터 관리 ==========

    /**
     * 플레이어 전체 데이터 로드
     */
    public CompletableFuture<Map<String, Object>> loadAllPlayerData(@NotNull String uuid) {
        return loadAllPlayerDataWithWallet(uuid);
    }

    /**
     * 플레이어 전체 데이터 로드 (지갑 포함)
     */
    public CompletableFuture<Map<String, Object>> loadAllPlayerDataWithWallet(@NotNull String uuid) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(createDefaultPlayerData());
        }

        // 캐시 확인
        PlayerDTO cachedPlayer = getFromCache(uuid);
        if (cachedPlayer != null) {
            return CompletableFuture.completedFuture(Map.of("player", cachedPlayer));
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                Map<String, Object> result = new HashMap<>();

                // Player 문서 참조
                DocumentReference playerRef = firestore
                        .collection(PLAYERS_COLLECTION)
                        .document(uuid);

                // 메인 문서와 서브컬렉션 병렬 로드
                CompletableFuture<PlayerDTO> playerFuture = loadPlayerDTO(playerRef);
                CompletableFuture<StatsDTO> statsFuture = loadStatsDTO(playerRef);
                CompletableFuture<TalentDTO> talentFuture = loadTalentDTO(playerRef);
                CompletableFuture<ProgressDTO> progressFuture = loadProgressDTO(playerRef);
                CompletableFuture<WalletDTO> walletFuture = loadWalletDTO(playerRef);

                // 모든 Future 완료 대기
                CompletableFuture.allOf(playerFuture, statsFuture, talentFuture,
                        progressFuture, walletFuture).join();

                // 결과 수집
                result.put("player", playerFuture.get());
                result.put("stats", statsFuture.get());
                result.put("talents", talentFuture.get());
                result.put("progress", progressFuture.get());
                result.put("wallet", walletFuture.get());

                // 캐시 업데이트
                updateCache(uuid, playerFuture.get());

                return result;
            } catch (Exception e) {
                LogUtil.error("플레이어 데이터 로드 실패: " + uuid, e);
                return createDefaultPlayerData();
            }
        });
    }

    /**
     * 플레이어 전체 데이터 저장
     */
    public CompletableFuture<Boolean> saveAllPlayerData(@NotNull String uuid,
                                                        @NotNull PlayerDTO player,
                                                        @NotNull StatsDTO stats,
                                                        @NotNull TalentDTO talents,
                                                        @NotNull ProgressDTO progress) {
        return saveAllPlayerDataWithWallet(uuid, player, stats, talents, progress, new WalletDTO());
    }

    /**
     * 플레이어 전체 데이터 저장 (지갑 포함)
     */
    public CompletableFuture<Boolean> saveAllPlayerDataWithWallet(@NotNull String uuid,
                                                                  @NotNull PlayerDTO player,
                                                                  @NotNull StatsDTO stats,
                                                                  @NotNull TalentDTO talents,
                                                                  @NotNull ProgressDTO progress,
                                                                  @NotNull WalletDTO wallet) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(false);
        }

        return CompletableFuture.supplyAsync(() -> {
            WriteBatch batch = firestore.batch();

            try {
                // Player 문서 참조
                DocumentReference playerRef = firestore
                        .collection(PLAYERS_COLLECTION)
                        .document(uuid);

                // 메인 문서 저장 (wallet 포함)
                Map<String, Object> playerData = convertPlayerToMap(player);
                playerData.put("wallet", convertWalletToMap(wallet));
                batch.set(playerRef, playerData);

                // 서브컬렉션 저장
                batch.set(playerRef.collection(STATS_SUBCOLLECTION).document("data"),
                        convertStatsToMap(stats));
                batch.set(playerRef.collection(TALENTS_SUBCOLLECTION).document("data"),
                        convertTalentToMap(talents));
                batch.set(playerRef.collection(PROGRESS_SUBCOLLECTION).document("data"),
                        convertProgressToMap(progress));

                // 배치 실행
                batch.commit().get();

                // 캐시 업데이트
                updateCache(uuid, player);

                LogUtil.debug("플레이어 데이터 저장 완료: " + uuid);
                return true;
            } catch (Exception e) {
                LogUtil.error("플레이어 데이터 저장 실패: " + uuid, e);
                return false;
            }
        });
    }

    // ========== 개별 DTO 로드 메소드 ==========

    private CompletableFuture<PlayerDTO> loadPlayerDTO(DocumentReference playerRef) {
        return loadDocument(playerRef, this::mapToPlayerDTO,
                new PlayerDTO(playerRef.getId(), "Unknown"), "PlayerDTO");
    }

    private CompletableFuture<StatsDTO> loadStatsDTO(DocumentReference playerRef) {
        return loadDocument(
                playerRef.collection(STATS_SUBCOLLECTION).document("data"),
                this::mapToStatsDTO, new StatsDTO(), "StatsDTO"
        );
    }

    private CompletableFuture<TalentDTO> loadTalentDTO(DocumentReference playerRef) {
        return loadDocument(
                playerRef.collection(TALENTS_SUBCOLLECTION).document("data"),
                this::mapToTalentDTO, new TalentDTO(), "TalentDTO"
        );
    }

    private CompletableFuture<ProgressDTO> loadProgressDTO(DocumentReference playerRef) {
        return loadDocument(
                playerRef.collection(PROGRESS_SUBCOLLECTION).document("data"),
                this::mapToProgressDTO, new ProgressDTO(), "ProgressDTO"
        );
    }

    private CompletableFuture<WalletDTO> loadWalletDTO(DocumentReference playerRef) {
        return loadDocument(
                playerRef,
                data -> {
                    Object walletObj = data.get("wallet");
                    if (walletObj instanceof Map) {
                        return mapToWalletDTO((Map<String, Object>) walletObj);
                    }
                    return new WalletDTO();
                },
                new WalletDTO(),
                "WalletDTO"
        );
    }

    // ========== 데이터 변환 메소드 ==========

    private PlayerDTO mapToPlayerDTO(Map<String, Object> data) {
        if (data == null) return null;

        String uuid = (String) data.get("uuid");
        String name = (String) data.get("name");
        long lastLogin = getLongValue(data, "lastLogin", System.currentTimeMillis());
        long totalPlaytime = getLongValue(data, "totalPlaytime", 0L);
        String jobStr = (String) data.get("job");
        JobType job = jobStr != null ? JobType.valueOf(jobStr) : null;

        return new PlayerDTO(uuid, name, lastLogin, totalPlaytime, job);
    }

    private StatsDTO mapToStatsDTO(Map<String, Object> data) {
        if (data == null) return new StatsDTO();

        // StatsDTO는 statPoints 필드가 없음, 6개의 스탯만 있음
        return new StatsDTO(
                getIntValue(data, "strength", 10),
                getIntValue(data, "intelligence", 10),
                getIntValue(data, "dexterity", 10),
                getIntValue(data, "vitality", 10),
                getIntValue(data, "wisdom", 10),
                getIntValue(data, "luck", 1)
        );
    }

    private TalentDTO mapToTalentDTO(Map<String, Object> data) {
        if (data == null) return new TalentDTO();

        Map<String, Integer> learnedTalents = new HashMap<>();
        Object talentsObj = data.get("learnedTalents"); // talentLevels가 아닌 learnedTalents
        if (talentsObj instanceof Map<?, ?> talents) {
            talents.forEach((k, v) -> {
                if (k != null && v instanceof Number) {
                    learnedTalents.put(k.toString(), ((Number) v).intValue());
                }
            });
        }

        return new TalentDTO(
                getIntValue(data, "availablePoints", 0),
                learnedTalents
        );
    }

    private ProgressDTO mapToProgressDTO(Map<String, Object> data) {
        if (data == null) return new ProgressDTO();

        // ProgressDTO의 실제 필드들: currentLevel, totalExperience, levelProgress, mobsKilled, playersKilled, deaths
        return new ProgressDTO(
                getIntValue(data, "currentLevel", 1),
                getLongValue(data, "totalExperience", 0L), // currentExp가 아닌 totalExperience
                getDoubleValue(data, "levelProgress"),
                getIntValue(data, "mobsKilled", 0),
                getIntValue(data, "playersKilled", 0),
                getIntValue(data, "deaths", 0)
        );
    }

    private WalletDTO mapToWalletDTO(Map<String, Object> data) {
        if (data == null) return new WalletDTO();

        Map<String, Long> currencies = new HashMap<>();
        Object currenciesObj = data.get("currencies");
        if (currenciesObj instanceof Map<?, ?> currencyMap) {
            currencyMap.forEach((k, v) -> {
                if (k != null && v instanceof Number) {
                    currencies.put(k.toString(), ((Number) v).longValue());
                }
            });
        }

        return new WalletDTO(
                currencies,
                getLongValue(data, "lastUpdated", System.currentTimeMillis())
        );
    }

    private LeaderboardEntryDTO mapToLeaderboardDTO(Map<String, Object> data) {
        if (data == null) return null;

        return new LeaderboardEntryDTO(
                (String) data.get("playerUuid"),
                (String) data.get("playerName"),
                getIntValue(data, "rank", 0),
                getLongValue(data, "value", 0L),
                (String) data.get("type"),
                getLongValue(data, "lastUpdated", System.currentTimeMillis())
        );
    }

    private ServerStatsDTO mapToServerStatsDTO(Map<String, Object> data) {
        if (data == null) return new ServerStatsDTO();

        return new ServerStatsDTO(
                getIntValue(data, "onlinePlayers", 0),
                getIntValue(data, "maxPlayers", 0),
                getIntValue(data, "totalPlayers", 0),
                getLongValue(data, "uptime", 0L),
                getDoubleValue(data, "tps"),
                getLongValue(data, "totalPlaytime", 0L),
                (String) data.getOrDefault("version", "1.21.7"),
                getLongValue(data, "lastUpdated", System.currentTimeMillis())
        );
    }

    // ========== Map 변환 메소드 ==========

    private Map<String, Object> convertPlayerToMap(PlayerDTO player) {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", player.uuid());
        map.put("name", player.name());
        map.put("lastLogin", player.lastLogin());
        map.put("totalPlaytime", player.totalPlaytime());
        if (player.job() != null) {
            map.put("job", player.job().name());
        }
        map.put("lastUpdated", FieldValue.serverTimestamp());
        return map;
    }

    private Map<String, Object> convertStatsToMap(StatsDTO stats) {
        Map<String, Object> map = new HashMap<>();
        map.put("strength", stats.strength());
        map.put("intelligence", stats.intelligence());
        map.put("dexterity", stats.dexterity());
        map.put("vitality", stats.vitality());
        map.put("wisdom", stats.wisdom());
        map.put("luck", stats.luck());
        // StatsDTO에는 statPoints 필드가 없음
        return map;
    }

    private Map<String, Object> convertTalentToMap(TalentDTO talents) {
        Map<String, Object> map = new HashMap<>();
        map.put("availablePoints", talents.availablePoints());
        map.put("learnedTalents", talents.learnedTalents()); // talentLevels가 아닌 learnedTalents
        return map;
    }

    private Map<String, Object> convertProgressToMap(ProgressDTO progress) {
        Map<String, Object> map = new HashMap<>();
        map.put("currentLevel", progress.currentLevel());
        map.put("totalExperience", progress.totalExperience()); // totalExp가 아닌 totalExperience
        map.put("levelProgress", progress.levelProgress());
        map.put("mobsKilled", progress.mobsKilled());
        map.put("playersKilled", progress.playersKilled());
        map.put("deaths", progress.deaths());
        return map;
    }

    private Map<String, Object> convertWalletToMap(WalletDTO wallet) {
        Map<String, Object> map = new HashMap<>();
        map.put("currencies", wallet.currencies());
        map.put("lastUpdated", wallet.lastUpdated());
        return map;
    }

    private Map<String, Object> convertLeaderboardToMap(LeaderboardEntryDTO entry) {
        Map<String, Object> map = new HashMap<>();
        map.put("playerUuid", entry.playerUuid());
        map.put("playerName", entry.playerName());
        map.put("rank", entry.rank());
        map.put("value", entry.value());
        map.put("type", entry.type());
        map.put("lastUpdated", FieldValue.serverTimestamp());
        return map;
    }

    private Map<String, Object> convertServerStatsToMap(ServerStatsDTO serverStats) {
        Map<String, Object> map = new HashMap<>();
        map.put("onlinePlayers", serverStats.onlinePlayers());
        map.put("maxPlayers", serverStats.maxPlayers());
        map.put("totalPlayers", serverStats.totalPlayers());
        map.put("uptime", serverStats.uptime());
        map.put("tps", serverStats.tps());
        map.put("totalPlaytime", serverStats.totalPlaytime());
        map.put("version", serverStats.version());
        map.put("lastUpdated", FieldValue.serverTimestamp());
        return map;
    }

    // ========== 유틸리티 메소드 ==========

    private Map<String, Object> createDefaultPlayerData() {
        Map<String, Object> defaultData = new HashMap<>();
        defaultData.put("player", new PlayerDTO("", "Unknown"));
        defaultData.put("stats", new StatsDTO());
        defaultData.put("talents", new TalentDTO());
        defaultData.put("progress", new ProgressDTO());
        defaultData.put("wallet", new WalletDTO());
        return defaultData;
    }

    private int getIntValue(Map<String, Object> map, String key, int defaultValue) {
        Object value = map.get(key);
        return value instanceof Number ? ((Number) value).intValue() : defaultValue;
    }

    private long getLongValue(Map<String, Object> map, String key, long defaultValue) {
        Object value = map.get(key);
        return value instanceof Number ? ((Number) value).longValue() : defaultValue;
    }

    private double getDoubleValue(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value instanceof Number ? ((Number) value).doubleValue() : 0.0;
    }

    // ========== 범용 데이터 처리 메소드 ==========

    private <T> CompletableFuture<T> loadDocument(
            @NotNull DocumentReference docRef,
            @NotNull Function<Map<String, Object>, T> mapper,
            @NotNull T defaultValue,
            @NotNull String errorContext) {

        if (!isConnected()) return CompletableFuture.completedFuture(defaultValue);

        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentSnapshot doc = docRef.get().get();
                if (doc.exists()) {
                    return mapper.apply(doc.getData());
                }
                return defaultValue;
            } catch (Exception e) {
                LogUtil.error(errorContext + " 로드 실패", e);
                return defaultValue;
            }
        });
    }

    /**
     * Nullable 문서 로드 메서드
     * 문서가 존재하지 않으면 null을 반환
     */
    private <T> CompletableFuture<T> loadDocumentNullable(
            @NotNull DocumentReference docRef,
            @NotNull Function<Map<String, Object>, T> mapper,
            @NotNull String errorContext) {

        if (!isConnected()) return CompletableFuture.completedFuture(null);

        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentSnapshot doc = docRef.get().get();
                if (doc.exists()) {
                    return mapper.apply(doc.getData());
                }
                return null; // 문서가 존재하지 않으면 null 반환
            } catch (Exception e) {
                LogUtil.error(errorContext + " 로드 실패", e);
                return null;
            }
        });
    }

    // ========== 캐시 관리 ==========

    @Nullable
    private PlayerDTO getFromCache(@NotNull String uuid) {
        Long timestamp = cacheTimestamps.get(uuid);
        if (timestamp != null && (System.currentTimeMillis() - timestamp) < CACHE_DURATION) {
            return playerCache.get(uuid);
        }
        invalidateCache(uuid);
        return null;
    }

    private void updateCache(@NotNull String uuid, @NotNull PlayerDTO player) {
        playerCache.put(uuid, player);
        cacheTimestamps.put(uuid, System.currentTimeMillis());
    }

    private void invalidateCache(@NotNull String uuid) {
        playerCache.remove(uuid);
        cacheTimestamps.remove(uuid);
    }

    public void clearCache() {
        playerCache.clear();
        cacheTimestamps.clear();
        LogUtil.info("Firebase 캐시가 초기화되었습니다.");
    }

    public Map<String, Object> getCacheStats() {
        return Map.of(
                "playerCacheSize", playerCache.size(),
                "cacheHitRate", 0.0 // TODO: 구현 필요
        );
    }

    /**
     * 연결 종료
     */
    public void shutdown() {
        if (firestore != null) {
            try {
                firestore.close();
                LogUtil.info("Firebase 연결이 정상적으로 종료되었습니다.");
            } catch (Exception e) {
                LogUtil.error("Firebase 연결 종료 중 오류 발생", e);
            }
        }
    }
}