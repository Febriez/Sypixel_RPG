package com.febrie.rpg.database;

import com.febrie.rpg.dto.*;
import com.febrie.rpg.job.JobType;
import com.febrie.rpg.util.LogUtil;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Firebase Firestore 데이터베이스 서비스
 * 모든 DTO 데이터의 저장/로드 로직을 중앙화
 *
 * @author Febrie, CoffeeTory
 */
public class FirebaseService {

    private final String serverName;

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

    // 환경변수 이름들
    private static final String ENV_PROJECT_ID = "FIREBASE_PROJECT_ID";
    private static final String ENV_DATABASE_URL = "FIREBASE_DATABASE_URL";
    private static final String ENV_CREDENTIALS_PATH = "FIREBASE_CREDENTIALS_PATH";
    private static final String ENV_CREDENTIALS_JSON = "FIREBASE_CREDENTIALS_JSON";

    public FirebaseService(@NotNull Plugin plugin) {
        this.serverName = plugin.getConfig().getString("server-name", "default");
        initializeFirebase();
    }

    /**
     * Firebase 초기화
     */
    private void initializeFirebase() {
        try {
            Map<String, String> env = getEnvironmentVariables();

            if (env.get("projectId") == null) {
                LogUtil.error("Firebase 초기화 실패: " + ENV_PROJECT_ID + " 환경변수가 설정되지 않았습니다.");
                return;
            }

            GoogleCredentials credentials = loadCredentials(env);
            if (credentials == null) return;

            FirebaseOptions options = buildFirebaseOptions(credentials, env);
            FirebaseApp.initializeApp(options);
            firestore = FirestoreClient.getFirestore();

            testConnection();

        } catch (Exception e) {
            LogUtil.error("Firebase 초기화 중 오류 발생", e);
            connected = false;
        }
    }

    /**
     * 환경변수 읽기
     */
    private Map<String, String> getEnvironmentVariables() {
        Map<String, String> env = new HashMap<>();
        env.put("projectId", System.getenv(ENV_PROJECT_ID));
        env.put("databaseUrl", System.getenv(ENV_DATABASE_URL));
        env.put("credentialsPath", System.getenv(ENV_CREDENTIALS_PATH));
        env.put("credentialsJson", System.getenv(ENV_CREDENTIALS_JSON));
        return env;
    }

    /**
     * 인증 정보 로드
     */
    private GoogleCredentials loadCredentials(Map<String, String> env) throws IOException {
        String credentialsJson = env.get("credentialsJson");
        String credentialsPath = env.get("credentialsPath");

        if (credentialsJson != null && !credentialsJson.isEmpty()) {
            LogUtil.info("Firebase 인증 정보를 환경변수에서 로드했습니다.");
            return GoogleCredentials.fromStream(new ByteArrayInputStream(credentialsJson.getBytes()));
        } else if (credentialsPath != null && !credentialsPath.isEmpty()) {
            LogUtil.info("Firebase 인증 정보를 파일에서 로드했습니다: " + credentialsPath);
            return GoogleCredentials.fromStream(new FileInputStream(credentialsPath));
        } else {
            LogUtil.error("Firebase 초기화 실패: 인증 정보가 없습니다.");
            LogUtil.error(ENV_CREDENTIALS_JSON + " 또는 " + ENV_CREDENTIALS_PATH + " 환경변수를 설정하세요.");
            return null;
        }
    }

    /**
     * Firebase 옵션 빌드
     */
    private FirebaseOptions buildFirebaseOptions(GoogleCredentials credentials, Map<String, String> env) {
        FirebaseOptions.Builder builder = FirebaseOptions.builder()
                .setCredentials(credentials)
                .setProjectId(env.get("projectId"));

        String databaseUrl = env.get("databaseUrl");
        if (databaseUrl != null && !databaseUrl.isEmpty()) {
            builder.setDatabaseUrl(databaseUrl);
        }

        return builder.build();
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

    // ========== 범용 데이터 처리 메소드 ==========

    /**
     * 문서 로드 및 매핑
     */
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
     * 문서 로드 및 매핑 (Optional 반환)
     */
    private <T> CompletableFuture<Optional<T>> loadDocumentOptional(
            @NotNull DocumentReference docRef,
            @NotNull Function<Map<String, Object>, T> mapper,
            @NotNull String errorContext) {

        if (!isConnected()) return CompletableFuture.completedFuture(Optional.empty());

        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentSnapshot doc = docRef.get().get();
                if (doc.exists()) {
                    return Optional.ofNullable(mapper.apply(doc.getData()));
                }
                return Optional.<T>empty();
            } catch (Exception e) {
                LogUtil.error(errorContext + " 로드 실패", e);
                return Optional.<T>empty();
            }
        });
    }

    /**
     * 문서 저장
     */
    private CompletableFuture<Boolean> saveDocument(
            @NotNull DocumentReference docRef,
            @NotNull Map<String, Object> data,
            @NotNull String errorContext) {

        if (!isConnected()) return CompletableFuture.completedFuture(false);

        return CompletableFuture.supplyAsync(() -> {
            try {
                docRef.set(data, SetOptions.merge()).get();
                LogUtil.debug(errorContext + " 저장 완료");
                return true;
            } catch (Exception e) {
                LogUtil.error(errorContext + " 저장 실패", e);
                return false;
            }
        });
    }

    /**
     * 서브컬렉션 문서 참조 가져오기
     */
    private DocumentReference getSubcollectionDocument(String playerUuid, String subcollection) {
        return firestore.collection(PLAYERS_COLLECTION)
                .document(playerUuid)
                .collection(subcollection)
                .document("current");
    }

    // ========== PlayerDTO 메소드 ==========

    /**
     * 플레이어 기본 정보를 Map으로 변환
     */
    private Map<String, Object> playerToMap(@NotNull PlayerDTO player) {
        Map<String, Object> data = new HashMap<>();
        data.put("name", player.name());
        data.put("lastLogin", player.lastLogin());
        data.put("totalPlaytime", player.totalPlaytime());
        if (player.job() != null) {
            data.put("job", player.job().name());
        }
        return data;
    }

    /**
     * 플레이어 기본 정보 저장
     */
    public CompletableFuture<Boolean> savePlayer(@NotNull String playerUuid, @NotNull PlayerDTO player) {
        Map<String, Object> data = playerToMap(player);
        data.put("lastUpdated", FieldValue.serverTimestamp());

        return saveDocument(
                firestore.collection(PLAYERS_COLLECTION).document(playerUuid),
                data,
                "플레이어 데이터"
        ).thenApply(success -> {
            if (success) updateCache(playerUuid, player);
            return success;
        });
    }

    /**
     * 플레이어 기본 정보 로드
     */
    public CompletableFuture<PlayerDTO> loadPlayer(@NotNull String playerUuid) {
        PlayerDTO cached = getFromCache(playerUuid);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }

        return loadDocumentOptional(
                firestore.collection(PLAYERS_COLLECTION).document(playerUuid),
                data -> mapToPlayerDTO(playerUuid, data),
                "플레이어 데이터"
        ).thenApply(optionalPlayer -> {
            optionalPlayer.ifPresent(player -> updateCache(playerUuid, player));
            return optionalPlayer.orElse(null);
        });
    }

    /**
     * Map을 PlayerDTO로 변환
     */
    private PlayerDTO mapToPlayerDTO(String uuid, Map<String, Object> data) {
        String name = (String) data.get("name");
        long lastLogin = getLongValue(data, "lastLogin", System.currentTimeMillis());
        long totalPlaytime = getLongValue(data, "totalPlaytime", 0L);

        JobType job = Optional.ofNullable((String) data.get("job"))
                .flatMap(jobStr -> {
                    try {
                        return Optional.of(JobType.valueOf(jobStr));
                    } catch (IllegalArgumentException e) {
                        LogUtil.warning("알 수 없는 직업 타입: " + jobStr);
                        return Optional.empty();
                    }
                })
                .orElse(null);

        return new PlayerDTO(uuid, name, lastLogin, totalPlaytime, job);
    }

    // ========== StatsDTO 메소드 ==========

    /**
     * 스탯 데이터 저장
     */
    public CompletableFuture<Boolean> saveStats(@NotNull String playerUuid, @NotNull StatsDTO stats) {
        Map<String, Object> data = Map.of(
                "strength", stats.strength(),
                "intelligence", stats.intelligence(),
                "dexterity", stats.dexterity(),
                "vitality", stats.vitality(),
                "wisdom", stats.wisdom(),
                "luck", stats.luck(),
                "lastUpdated", FieldValue.serverTimestamp()
        );

        return saveDocument(
                getSubcollectionDocument(playerUuid, STATS_SUBCOLLECTION),
                data,
                "스탯 데이터"
        );
    }

    /**
     * 스탯 데이터 로드
     */
    public CompletableFuture<StatsDTO> loadStats(@NotNull String playerUuid) {
        return loadDocument(
                getSubcollectionDocument(playerUuid, STATS_SUBCOLLECTION),
                data -> new StatsDTO(
                        getIntValue(data, "strength", 10),
                        getIntValue(data, "intelligence", 10),
                        getIntValue(data, "dexterity", 10),
                        getIntValue(data, "vitality", 10),
                        getIntValue(data, "wisdom", 10),
                        getIntValue(data, "luck", 1)
                ),
                new StatsDTO(),
                "스탯 데이터"
        );
    }

    // ========== TalentDTO 메소드 ==========

    /**
     * 특성 데이터 저장
     */
    public CompletableFuture<Boolean> saveTalents(@NotNull String playerUuid, @NotNull TalentDTO talents) {
        Map<String, Object> data = Map.of(
                "availablePoints", talents.availablePoints(),
                "learnedTalents", talents.learnedTalents(),
                "lastUpdated", FieldValue.serverTimestamp()
        );

        return saveDocument(
                getSubcollectionDocument(playerUuid, TALENTS_SUBCOLLECTION),
                data,
                "특성 데이터"
        );
    }

    /**
     * 특성 데이터 로드
     */
    public CompletableFuture<TalentDTO> loadTalents(@NotNull String playerUuid) {
        return loadDocument(
                getSubcollectionDocument(playerUuid, TALENTS_SUBCOLLECTION),
                data -> new TalentDTO(
                        getIntValue(data, "availablePoints", 0),
                        extractTypedMap(data, "learnedTalents", v -> ((Number) v).intValue())
                ),
                new TalentDTO(),
                "특성 데이터"
        );
    }

    // ========== ProgressDTO 메소드 ==========

    /**
     * 진행도 데이터 저장
     */
    public CompletableFuture<Boolean> saveProgress(@NotNull String playerUuid, @NotNull ProgressDTO progress) {
        Map<String, Object> data = new HashMap<>();
        data.put("currentLevel", progress.currentLevel());
        data.put("totalExperience", progress.totalExperience());
        data.put("levelProgress", progress.levelProgress());
        data.put("mobsKilled", progress.mobsKilled());
        data.put("playersKilled", progress.playersKilled());
        data.put("deaths", progress.deaths());
        data.put("lastUpdated", FieldValue.serverTimestamp());

        return saveDocument(
                getSubcollectionDocument(playerUuid, PROGRESS_SUBCOLLECTION),
                data,
                "진행도 데이터"
        );
    }

    /**
     * 진행도 데이터 로드
     */
    public CompletableFuture<ProgressDTO> loadProgress(@NotNull String playerUuid) {
        return loadDocument(
                getSubcollectionDocument(playerUuid, PROGRESS_SUBCOLLECTION),
                data -> new ProgressDTO(
                        getIntValue(data, "currentLevel", 1),
                        getLongValue(data, "totalExperience", 0L),
                        getDoubleValue(data, "levelProgress", 0.0),
                        getIntValue(data, "mobsKilled", 0),
                        getIntValue(data, "playersKilled", 0),
                        getIntValue(data, "deaths", 0)
                ),
                new ProgressDTO(),
                "진행도 데이터"
        );
    }

    // ========== WalletDTO 메소드 ==========

    /**
     * 플레이어 재화 정보 로드
     */
    public CompletableFuture<WalletDTO> loadWallet(@NotNull String uuid) {
        return loadDocument(
                firestore.collection(PLAYERS_COLLECTION).document(uuid),
                data -> {
                    Map<String, Object> walletData = extractMap(data, "wallet");
                    if (walletData != null) {
                        return new WalletDTO(
                                extractTypedMap(walletData, "currencies", v -> ((Number) v).longValue()),
                                getLongValue(walletData, "lastUpdated", System.currentTimeMillis())
                        );
                    }
                    return new WalletDTO();
                },
                new WalletDTO(),
                "재화 데이터"
        );
    }

    /**
     * 플레이어 재화 정보 저장
     */
    public CompletableFuture<Boolean> saveWallet(@NotNull String uuid, @NotNull WalletDTO wallet) {
        Map<String, Object> walletData = Map.of(
                "currencies", wallet.currencies(),
                "lastUpdated", wallet.lastUpdated()
        );

        return CompletableFuture.supplyAsync(() -> {
            try {
                firestore.collection(PLAYERS_COLLECTION)
                        .document(uuid)
                        .update("wallet", walletData)
                        .get();
                return true;
            } catch (Exception e) {
                LogUtil.error("재화 데이터 저장 실패: " + uuid, e);
                return false;
            }
        });
    }

    // ========== 통합 저장 메소드 ==========

    /**
     * 모든 플레이어 데이터 저장
     */
    public CompletableFuture<Boolean> saveAllPlayerDataWithWallet(
            @NotNull String uuid,
            @NotNull PlayerDTO player,
            @NotNull StatsDTO stats,
            @NotNull TalentDTO talents,
            @NotNull ProgressDTO progress,
            @NotNull WalletDTO wallet) {

        if (!isConnected()) return CompletableFuture.completedFuture(false);

        return CompletableFuture.supplyAsync(() -> {
            try {
                // 메인 문서 준비
                Map<String, Object> mainData = playerToMap(player);

                // 재화 정보 추가
                mainData.put("wallet", Map.of(
                        "currencies", wallet.currencies(),
                        "lastUpdated", wallet.lastUpdated()
                ));
                mainData.put("lastUpdated", FieldValue.serverTimestamp());

                // 메인 문서 저장
                firestore.collection(PLAYERS_COLLECTION)
                        .document(uuid)
                        .set(mainData, SetOptions.merge())
                        .get();

                // 서브컬렉션 저장 (병렬)
                List<CompletableFuture<Boolean>> futures = Arrays.asList(
                        saveStats(uuid, stats),
                        saveTalents(uuid, talents),
                        saveProgress(uuid, progress)
                );

                // 모든 저장 완료 대기
                CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).get();

                // 캐시 업데이트
                updateCache(uuid, player);

                LogUtil.debug("모든 플레이어 데이터 저장 완료: " + player.name());
                return true;

            } catch (Exception e) {
                LogUtil.error("플레이어 데이터 통합 저장 실패: " + uuid, e);
                return false;
            }
        });
    }

    // ========== 순위표 메소드 ==========

    /**
     * 순위표 업데이트
     */
    public CompletableFuture<Boolean> updateLeaderboard(@NotNull String type, @NotNull LeaderboardEntryDTO entry) {
        Map<String, Object> data = new HashMap<>();
        data.put("playerUuid", entry.playerUuid());
        data.put("playerName", entry.playerName());
        data.put("rank", entry.rank());
        data.put("value", entry.value());
        data.put("type", entry.type());
        data.put("lastUpdated", FieldValue.serverTimestamp());

        return saveDocument(
                firestore.collection(LEADERBOARDS_COLLECTION)
                        .document(type)
                        .collection("Entry")
                        .document(entry.playerUuid()),
                data,
                "순위표"
        );
    }

    /**
     * 순위표 조회
     */
    public CompletableFuture<List<LeaderboardEntryDTO>> getLeaderboard(@NotNull String type, int limit) {
        if (!isConnected()) return CompletableFuture.completedFuture(new ArrayList<>());

        return CompletableFuture.supplyAsync(() -> {
            try {
                QuerySnapshot querySnapshot = firestore.collection(LEADERBOARDS_COLLECTION)
                        .document(type)
                        .collection("Entry")
                        .orderBy("value", Query.Direction.DESCENDING)
                        .limit(limit)
                        .get()
                        .get();

                return querySnapshot.getDocuments().stream()
                        .map(doc -> mapToLeaderboardEntry(doc.getData(), type))
                        .collect(Collectors.toList());

            } catch (Exception e) {
                LogUtil.error("순위표 조회 실패: " + type, e);
                return new ArrayList<>();
            }
        });
    }

    /**
     * Map을 LeaderboardEntryDTO로 변환
     */
    private LeaderboardEntryDTO mapToLeaderboardEntry(Map<String, Object> data, String type) {
        return new LeaderboardEntryDTO(
                (String) data.get("playerUuid"),
                (String) data.get("playerName"),
                getIntValue(data, "rank", 0),
                getLongValue(data, "value", 0L),
                type,
                getLongValue(data, "lastUpdated", System.currentTimeMillis())
        );
    }

    // ========== 서버 통계 메소드 ==========

    /**
     * 서버 통계 저장
     */
    public CompletableFuture<Boolean> saveServerStats(@NotNull ServerStatsDTO stats) {
        Map<String, Object> data = new HashMap<>();
        data.put("onlinePlayers", stats.onlinePlayers());
        data.put("maxPlayers", stats.maxPlayers());
        data.put("totalPlayers", stats.totalPlayers());
        data.put("uptime", stats.uptime());
        data.put("tps", stats.tps());
        data.put("totalPlaytime", stats.totalPlaytime());
        data.put("version", stats.version());
        data.put("lastUpdated", FieldValue.serverTimestamp());

        return saveDocument(
                firestore.collection(SERVER_STATS_COLLECTION).document(serverName),
                data,
                "서버 통계"
        );
    }

    /**
     * 서버 통계 로드
     */
    public CompletableFuture<ServerStatsDTO> loadServerStats() {
        return loadDocument(
                firestore.collection(SERVER_STATS_COLLECTION).document(serverName),
                data -> new ServerStatsDTO(
                        getIntValue(data, "onlinePlayers", 0),
                        getIntValue(data, "maxPlayers", 0),
                        getIntValue(data, "totalPlayers", 0),
                        getLongValue(data, "uptime", 0L),
                        getDoubleValue(data, "tps", 20.0),
                        getLongValue(data, "totalPlaytime", 0L),
                        (String) data.getOrDefault("version", "1.21.7"),
                        getLongValue(data, "lastUpdated", System.currentTimeMillis())
                ),
                new ServerStatsDTO(),
                "서버 통계"
        );
    }

    // ========== 유틸리티 메소드 ==========

    /**
     * 타입별 Map 추출 통합 메소드
     */
    @SuppressWarnings("unchecked")
    private <T> Map<String, T> extractTypedMap(Map<String, Object> map, String key,
                                               Function<Object, T> converter) {
        Object value = map.get(key);
        if (value instanceof Map) {
            Map<String, Object> innerMap = (Map<String, Object>) value;
            Map<String, T> result = new HashMap<>();

            innerMap.forEach((k, v) -> {
                if (v != null) {
                    try {
                        T converted = converter.apply(v);
                        if (converted != null) {
                            result.put(k, converted);
                        }
                    } catch (Exception ex) {
                        LogUtil.debug("값 변환 실패: " + k + " - " + ex.getMessage());
                    }
                }
            });

            return result;
        }
        return new HashMap<>();
    }

    /**
     * Map 추출
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> extractMap(Map<String, Object> map, String key) {
        Object value = map.get(key);
        return value instanceof Map ? (Map<String, Object>) value : null;
    }

    /**
     * 안전한 값 추출 메소드들
     */
    private int getIntValue(Map<String, Object> map, String key, int defaultValue) {
        return getNumberValue(map, key, Number::intValue, defaultValue);
    }

    private long getLongValue(Map<String, Object> map, String key, long defaultValue) {
        return getNumberValue(map, key, Number::longValue, defaultValue);
    }

    private double getDoubleValue(Map<String, Object> map, String key, double defaultValue) {
        return getNumberValue(map, key, Number::doubleValue, defaultValue);
    }

    private <T> T getNumberValue(Map<String, Object> map, String key,
                                 Function<Number, T> converter, T defaultValue) {
        Object value = map.get(key);
        return value instanceof Number ? converter.apply((Number) value) : defaultValue;
    }

    // ========== 캐시 관리 ==========

    /**
     * 캐시에서 데이터 가져오기
     */
    @Nullable
    private PlayerDTO getFromCache(@NotNull String uuid) {
        Long timestamp = cacheTimestamps.get(uuid);
        if (timestamp != null && (System.currentTimeMillis() - timestamp) < CACHE_DURATION) {
            return playerCache.get(uuid);
        }
        invalidateCache(uuid);
        return null;
    }

    /**
     * 캐시 업데이트
     */
    private void updateCache(@NotNull String uuid, @NotNull PlayerDTO player) {
        playerCache.put(uuid, player);
        cacheTimestamps.put(uuid, System.currentTimeMillis());
    }

    /**
     * 캐시 무효화
     */
    private void invalidateCache(@NotNull String uuid) {
        playerCache.remove(uuid);
        cacheTimestamps.remove(uuid);
    }

    /**
     * 캐시 초기화
     */
    public void clearCache() {
        playerCache.clear();
        cacheTimestamps.clear();
        LogUtil.info("Firebase 캐시가 초기화되었습니다.");
    }

    /**
     * 캐시 통계
     */
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