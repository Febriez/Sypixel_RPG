package com.febrie.rpg.database;

import com.febrie.rpg.dto.*;
import com.febrie.rpg.job.JobType;
import com.febrie.rpg.util.LogUtil;
import com.google.gson.*;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;

/**
 * Firebase Firestore REST API 서비스
 * SDK 의존성 없이 REST API를 직접 사용하여 Firestore 데이터 관리
 *
 * @author Febrie, CoffeeTory
 */
public class FirestoreRestService {

    private static final String FIREBASE_PROJECT_ID = "sypixel-rpg";
    private static final String FIREBASE_CLIENT_EMAIL = "firebase-adminsdk-fbsvc@sypixel-rpg.iam.gserviceaccount.com";
    private static final String FIRESTORE_BASE_URL = "https://firestore.googleapis.com/v1/projects/" + FIREBASE_PROJECT_ID + "/databases/(default)/documents";
    private static final String TOKEN_URL = "https://oauth2.googleapis.com/token";

    private final HttpClient httpClient;
    private String accessToken;
    private long tokenExpiryTime;
    private final ScheduledExecutorService tokenRefreshExecutor;

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
    private static final String QUEST_SUBCOLLECTION = "Quest";
    private static final String ENV_PRIVATE_KEY = "FIREBASE_PRIVATE_KEY";

    private boolean connected = false;
    private PrivateKey privateKey;

    public FirestoreRestService(@NotNull Plugin plugin) {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(java.time.Duration.ofSeconds(10))
                .build();
        this.tokenRefreshExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("Firebase-Token-Refresh");
            return thread;
        });

        initializeFirebase();
    }

    /**
     * Firebase 초기화
     */
    private void initializeFirebase() {
        try {
            String privateKeyStr = System.getenv(ENV_PRIVATE_KEY);
            if (privateKeyStr == null || privateKeyStr.isEmpty()) {
                LogUtil.error("Firebase 초기화 실패: " + ENV_PRIVATE_KEY + " 환경변수가 설정되지 않았습니다.");
                return;
            }

            // Private Key 파싱
            this.privateKey = parsePrivateKey(privateKeyStr);

            // 액세스 토큰 획득
            refreshAccessToken().join();

            // 연결 테스트
            testConnection();

            // 토큰 자동 갱신 스케줄링 (50분마다)
            tokenRefreshExecutor.scheduleAtFixedRate(this::refreshAccessTokenAsync, 50, 50, TimeUnit.MINUTES);

        } catch (Exception e) {
            LogUtil.error("Firebase 초기화 중 오류 발생", e);
            connected = false;
        }
    }

    /**
     * Private Key 파싱
     */
    private PrivateKey parsePrivateKey(String privateKeyPEM) throws Exception {
        // PEM 형식 정리
        privateKeyPEM = privateKeyPEM
                .replace("\\n", "\n")
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        return keyFactory.generatePrivate(keySpec);
    }

    /**
     * JWT 토큰 생성
     */
    private String createJWT() throws Exception {
        long now = System.currentTimeMillis() / 1000;
        long exp = now + 3600; // 1시간 후 만료

        // JWT Header
        JsonObject header = new JsonObject();
        header.addProperty("alg", "RS256");
        header.addProperty("typ", "JWT");

        // JWT Claims
        JsonObject claims = new JsonObject();
        claims.addProperty("iss", FIREBASE_CLIENT_EMAIL);
        claims.addProperty("sub", FIREBASE_CLIENT_EMAIL);
        claims.addProperty("aud", TOKEN_URL);
        claims.addProperty("iat", now);
        claims.addProperty("exp", exp);
        claims.addProperty("scope", "https://www.googleapis.com/auth/cloud-platform https://www.googleapis.com/auth/datastore");

        // Base64URL 인코딩
        String headerBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(header.toString().getBytes());
        String claimsBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(claims.toString().getBytes());
        String signatureInput = headerBase64 + "." + claimsBase64;

        // RSA 서명
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(signatureInput.getBytes());
        byte[] signatureBytes = signature.sign();
        String signatureBase64 = Base64.getUrlEncoder().withoutPadding().encodeToString(signatureBytes);

        return signatureInput + "." + signatureBase64;
    }

    /**
     * 액세스 토큰 갱신
     */
    private CompletableFuture<Void> refreshAccessToken() {
        return CompletableFuture.runAsync(() -> {
            try {
                String jwt = createJWT();

                // 토큰 요청
                String requestBody = "grant_type=urn:ietf:params:oauth:grant-type:jwt-bearer&assertion=" + jwt;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(TOKEN_URL))
                        .header("Content-Type", "application/x-www-form-urlencoded")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
                    this.accessToken = jsonResponse.get("access_token").getAsString();
                    int expiresIn = jsonResponse.get("expires_in").getAsInt();
                    this.tokenExpiryTime = System.currentTimeMillis() + (expiresIn * 1000L) - 60000; // 1분 여유
                    LogUtil.info("Firebase 액세스 토큰 갱신 성공");
                } else {
                    LogUtil.error("토큰 갱신 실패: " + response.statusCode() + " - " + response.body());
                }
            } catch (Exception e) {
                LogUtil.error("액세스 토큰 갱신 중 오류", e);
            }
        });
    }

    /**
     * 비동기 토큰 갱신
     */
    private void refreshAccessTokenAsync() {
        refreshAccessToken().exceptionally(ex -> {
            LogUtil.error("토큰 자동 갱신 실패", ex);
            return null;
        });
    }

    /**
     * 연결 테스트
     */
    private void testConnection() {
        try {
            // 간단한 쿼리로 연결 테스트
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(FIRESTORE_BASE_URL + "/_test/ping"))
                    .header("Authorization", "Bearer " + accessToken)
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            connected = response.statusCode() == 200 || response.statusCode() == 404;

            if (connected) {
                LogUtil.info("Firebase Firestore REST API 연결 성공!");
            } else {
                LogUtil.error("Firebase Firestore 연결 실패: " + response.statusCode());
            }
        } catch (Exception e) {
            connected = false;
            LogUtil.error("Firebase Firestore 연결 테스트 실패", e);
        }
    }

    /**
     * 연결 상태 확인
     */
    public boolean isConnected() {
        return connected && accessToken != null && System.currentTimeMillis() < tokenExpiryTime;
    }

    /**
     * Firestore 문서 읽기
     */
    private CompletableFuture<JsonObject> getDocument(String path) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(FIRESTORE_BASE_URL + "/" + path))
                        .header("Authorization", "Bearer " + accessToken)
                        .GET()
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return JsonParser.parseString(response.body()).getAsJsonObject();
                } else if (response.statusCode() == 404) {
                    return null; // 문서가 존재하지 않음
                } else {
                    LogUtil.error("문서 읽기 실패: " + path + " - " + response.statusCode() + " - " + response.body());
                    return null;
                }
            } catch (Exception e) {
                LogUtil.error("문서 읽기 중 오류: " + path, e);
                return null;
            }
        });
    }

    /**
     * Firestore 문서 쓰기
     */
    private CompletableFuture<Boolean> setDocument(String path, JsonObject data) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(FIRESTORE_BASE_URL + "/" + path))
                        .header("Authorization", "Bearer " + accessToken)
                        .header("Content-Type", "application/json")
                        .method("PATCH", HttpRequest.BodyPublishers.ofString(data.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return true;
                } else {
                    LogUtil.error("문서 쓰기 실패: " + path + " - " + response.statusCode() + " - " + response.body());
                    return false;
                }
            } catch (Exception e) {
                LogUtil.error("문서 쓰기 중 오류: " + path, e);
                return false;
            }
        });
    }

    /**
     * Firestore 쿼리 실행
     */
    private CompletableFuture<JsonArray> runQuery(String collection, JsonObject query) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = "https://firestore.googleapis.com/v1/projects/" + FIREBASE_PROJECT_ID +
                        "/databases/(default)/documents:runQuery";

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Authorization", "Bearer " + accessToken)
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(query.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    return JsonParser.parseString(response.body()).getAsJsonArray();
                } else {
                    LogUtil.error("쿼리 실행 실패: " + collection + " - " + response.statusCode() + " - " + response.body());
                    return new JsonArray();
                }
            } catch (Exception e) {
                LogUtil.error("쿼리 실행 중 오류: " + collection, e);
                return new JsonArray();
            }
        });
    }

    // ========== 플레이어 데이터 로드 ==========

    /**
     * 플레이어 기본 정보 로드
     */
    public CompletableFuture<PlayerDTO> loadPlayer(@NotNull String uuid) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(null);
        }

        // 캐시 확인
        PlayerDTO cached = getFromCache(uuid);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }

        return getDocument(PLAYERS_COLLECTION + "/" + uuid)
                .thenApply(doc -> {
                    if (doc == null) return null;
                    PlayerDTO player = parsePlayerDTO(doc);
                    updateCache(uuid, player);
                    return player;
                });
    }

    /**
     * 플레이어 전체 데이터 로드
     */
    public CompletableFuture<Map<String, Object>> loadAllPlayerDataWithWallet(@NotNull String uuid) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(createDefaultPlayerData());
        }

        Map<String, Object> result = new HashMap<>();

        // 병렬로 모든 데이터 로드
        CompletableFuture<PlayerDTO> playerFuture = loadPlayer(uuid);
        CompletableFuture<StatsDTO> statsFuture = loadStats(uuid);
        CompletableFuture<TalentDTO> talentsFuture = loadTalents(uuid);
        CompletableFuture<ProgressDTO> progressFuture = loadProgress(uuid);
        CompletableFuture<WalletDTO> walletFuture = loadWallet(uuid);

        return CompletableFuture.allOf(playerFuture, statsFuture, talentsFuture, progressFuture, walletFuture)
                .thenApply(v -> {
                    try {
                        result.put("player", playerFuture.get());
                        result.put("stats", statsFuture.get());
                        result.put("talents", talentsFuture.get());
                        result.put("progress", progressFuture.get());
                        result.put("wallet", walletFuture.get());
                        return result;
                    } catch (Exception e) {
                        LogUtil.error("플레이어 데이터 로드 실패: " + uuid, e);
                        return createDefaultPlayerData();
                    }
                });
    }

    /**
     * 스탯 정보 로드
     */
    public CompletableFuture<StatsDTO> loadStats(@NotNull String uuid) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(new StatsDTO());
        }

        return getDocument(PLAYERS_COLLECTION + "/" + uuid + "/" + STATS_SUBCOLLECTION + "/data")
                .thenApply(doc -> doc == null ? new StatsDTO() : parseStatsDTO(doc));
    }

    /**
     * 특성 정보 로드
     */
    public CompletableFuture<TalentDTO> loadTalents(@NotNull String uuid) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(new TalentDTO());
        }

        return getDocument(PLAYERS_COLLECTION + "/" + uuid + "/" + TALENTS_SUBCOLLECTION + "/data")
                .thenApply(doc -> doc == null ? new TalentDTO() : parseTalentDTO(doc));
    }

    /**
     * 진행도 정보 로드
     */
    public CompletableFuture<ProgressDTO> loadProgress(@NotNull String uuid) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(new ProgressDTO());
        }

        return getDocument(PLAYERS_COLLECTION + "/" + uuid + "/" + PROGRESS_SUBCOLLECTION + "/data")
                .thenApply(doc -> doc == null ? new ProgressDTO() : parseProgressDTO(doc));
    }

    /**
     * 지갑 정보 로드
     */
    public CompletableFuture<WalletDTO> loadWallet(@NotNull String uuid) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(new WalletDTO());
        }

        return getDocument(PLAYERS_COLLECTION + "/" + uuid)
                .thenApply(doc -> {
                    if (doc == null || !doc.has("fields") || !doc.getAsJsonObject("fields").has("wallet")) {
                        return new WalletDTO();
                    }
                    return parseWalletDTO(doc.getAsJsonObject("fields").getAsJsonObject("wallet"));
                });
    }

    // ========== 플레이어 데이터 저장 ==========

    /**
     * 플레이어 전체 데이터 저장
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

        // 플레이어 메인 문서 (wallet 포함)
        JsonObject playerDoc = createPlayerDocument(player, wallet);
        CompletableFuture<Boolean> playerFuture = setDocument(PLAYERS_COLLECTION + "/" + uuid, playerDoc);

        // 서브컬렉션들
        JsonObject statsDoc = createStatsDocument(stats);
        CompletableFuture<Boolean> statsFuture = setDocument(
                PLAYERS_COLLECTION + "/" + uuid + "/" + STATS_SUBCOLLECTION + "/data", statsDoc);

        JsonObject talentsDoc = createTalentsDocument(talents);
        CompletableFuture<Boolean> talentsFuture = setDocument(
                PLAYERS_COLLECTION + "/" + uuid + "/" + TALENTS_SUBCOLLECTION + "/data", talentsDoc);

        JsonObject progressDoc = createProgressDocument(progress);
        CompletableFuture<Boolean> progressFuture = setDocument(
                PLAYERS_COLLECTION + "/" + uuid + "/" + PROGRESS_SUBCOLLECTION + "/data", progressDoc);

        return CompletableFuture.allOf(playerFuture, statsFuture, talentsFuture, progressFuture)
                .thenApply(v -> {
                    try {
                        boolean success = playerFuture.get() && statsFuture.get() &&
                                talentsFuture.get() && progressFuture.get();
                        if (success) {
                            updateCache(uuid, player);
                            LogUtil.debug("플레이어 데이터 저장 완료: " + uuid);
                        }
                        return success;
                    } catch (Exception e) {
                        LogUtil.error("플레이어 데이터 저장 실패: " + uuid, e);
                        return false;
                    }
                });
    }

    public CompletableFuture<Boolean> saveAllPlayerData(@NotNull String uuid,
                                                        @NotNull PlayerDTO player,
                                                        @NotNull StatsDTO stats,
                                                        @NotNull TalentDTO talents,
                                                        @NotNull ProgressDTO progress) {
        return saveAllPlayerDataWithWallet(uuid, player, stats, talents, progress, new WalletDTO());
    }

    // ========== 리더보드 관리 ==========

    /**
     * 리더보드 엔트리 저장
     */
    public CompletableFuture<Boolean> saveLeaderboardEntry(@NotNull LeaderboardEntryDTO entry) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(false);
        }

        String documentId = entry.type() + "_" + entry.playerUuid();
        JsonObject doc = createLeaderboardDocument(entry);

        return setDocument(LEADERBOARDS_COLLECTION + "/" + documentId, doc);
    }

    /**
     * 특정 타입의 리더보드 로드
     */
    public CompletableFuture<List<LeaderboardEntryDTO>> loadLeaderboard(@NotNull String type, int limit) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }

        // Firestore 쿼리 구성
        JsonObject query = new JsonObject();
        JsonObject structuredQuery = new JsonObject();

        // FROM 절
        JsonArray from = new JsonArray();
        JsonObject collection = new JsonObject();
        collection.addProperty("collectionId", LEADERBOARDS_COLLECTION);
        from.add(collection);
        structuredQuery.add("from", from);

        // WHERE 절
        JsonObject where = new JsonObject();
        JsonObject fieldFilter = new JsonObject();
        JsonObject field = new JsonObject();
        field.addProperty("fieldPath", "type");
        fieldFilter.add("field", field);
        fieldFilter.addProperty("op", "EQUAL");
        JsonObject value = new JsonObject();
        value.addProperty("stringValue", type);
        fieldFilter.add("value", value);
        where.add("fieldFilter", fieldFilter);
        structuredQuery.add("where", where);

        // ORDER BY 절
        JsonArray orderBy = new JsonArray();
        JsonObject order = new JsonObject();
        JsonObject orderField = new JsonObject();
        orderField.addProperty("fieldPath", "rank");
        order.add("field", orderField);
        order.addProperty("direction", "ASCENDING");
        orderBy.add(order);
        structuredQuery.add("orderBy", orderBy);

        // LIMIT
        structuredQuery.addProperty("limit", limit);

        query.add("structuredQuery", structuredQuery);

        return runQuery(LEADERBOARDS_COLLECTION, query)
                .thenApply(results -> {
                    List<LeaderboardEntryDTO> leaderboard = new ArrayList<>();
                    for (int i = 0; i < results.size(); i++) {
                        JsonObject result = results.get(i).getAsJsonObject();
                        if (result.has("document")) {
                            LeaderboardEntryDTO entry = parseLeaderboardDTO(result.getAsJsonObject("document"));
                            if (entry != null) {
                                leaderboard.add(entry);
                            }
                        }
                    }
                    return leaderboard;
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

        String documentId = java.time.LocalDate.now().toString();
        JsonObject doc = createServerStatsDocument(serverStats);

        return setDocument(SERVER_STATS_COLLECTION + "/" + documentId, doc);
    }

    /**
     * 최신 서버 통계 로드
     */
    public CompletableFuture<ServerStatsDTO> loadLatestServerStats() {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(new ServerStatsDTO());
        }

        // 오늘 날짜의 통계 시도
        String todayId = java.time.LocalDate.now().toString();
        return getDocument(SERVER_STATS_COLLECTION + "/" + todayId)
                .thenApply(doc -> doc == null ? new ServerStatsDTO() : parseServerStatsDTO(doc));
    }

    // ========== 퀘스트 데이터 관리 ==========

    /**
     * 플레이어 퀘스트 데이터 로드
     */
    public CompletableFuture<PlayerQuestDTO> loadPlayerQuestData(@NotNull String playerId) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(new PlayerQuestDTO(playerId));
        }

        return getDocument(PLAYERS_COLLECTION + "/" + playerId + "/" + QUEST_SUBCOLLECTION + "/data")
                .thenApply(doc -> {
                    if (doc == null) {
                        return new PlayerQuestDTO(playerId);
                    }
                    // 간단한 구현 - 실제로는 더 복잡한 파싱 필요
                    return new PlayerQuestDTO(playerId);
                });
    }

    /**
     * 플레이어 퀘스트 데이터 저장
     */
    public CompletableFuture<Boolean> savePlayerQuestData(@NotNull String playerId,
                                                          @NotNull PlayerQuestDTO questData) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(false);
        }

        // 간단한 구현 - 실제로는 더 복잡한 저장 로직 필요
        JsonObject doc = new JsonObject();
        JsonObject fields = new JsonObject();
        fields.add("playerId", createStringValue(playerId));
        fields.add("lastUpdated", createTimestampValue());
        doc.add("fields", fields);

        return setDocument(PLAYERS_COLLECTION + "/" + playerId + "/" + QUEST_SUBCOLLECTION + "/data", doc);
    }

    // ========== 문서 파싱 메서드 ==========

    private PlayerDTO parsePlayerDTO(JsonObject doc) {
        if (!doc.has("fields")) return null;

        JsonObject fields = doc.getAsJsonObject("fields");
        String uuid = getStringValue(fields, "uuid");
        String name = getStringValue(fields, "name");
        long lastLogin = getLongValue(fields, "lastLogin");
        long totalPlaytime = getLongValue(fields, "totalPlaytime");
        String jobStr = getStringValue(fields, "job");
        JobType job = jobStr != null && !jobStr.isEmpty() ? JobType.valueOf(jobStr) : null;

        return new PlayerDTO(uuid, name, lastLogin, totalPlaytime, job);
    }

    private StatsDTO parseStatsDTO(JsonObject doc) {
        if (!doc.has("fields")) return new StatsDTO();

        JsonObject fields = doc.getAsJsonObject("fields");
        return new StatsDTO(
                getIntValue(fields, "strength", 10),
                getIntValue(fields, "intelligence", 10),
                getIntValue(fields, "dexterity", 10),
                getIntValue(fields, "vitality", 10),
                getIntValue(fields, "wisdom", 10),
                getIntValue(fields, "luck", 1)
        );
    }

    private TalentDTO parseTalentDTO(JsonObject doc) {
        if (!doc.has("fields")) return new TalentDTO();

        JsonObject fields = doc.getAsJsonObject("fields");
        int availablePoints = getIntValue(fields, "availablePoints", 0);

        Map<String, Integer> learnedTalents = new HashMap<>();
        if (fields.has("learnedTalents") && fields.get("learnedTalents").isJsonObject()) {
            JsonObject talentsMap = fields.getAsJsonObject("learnedTalents").getAsJsonObject("mapValue");
            if (talentsMap != null && talentsMap.has("fields")) {
                JsonObject talentFields = talentsMap.getAsJsonObject("fields");
                for (Map.Entry<String, JsonElement> entry : talentFields.entrySet()) {
                    if (entry.getValue().isJsonObject() && entry.getValue().getAsJsonObject().has("integerValue")) {
                        learnedTalents.put(entry.getKey(),
                                entry.getValue().getAsJsonObject().get("integerValue").getAsInt());
                    }
                }
            }
        }

        return new TalentDTO(availablePoints, learnedTalents);
    }

    private ProgressDTO parseProgressDTO(JsonObject doc) {
        if (!doc.has("fields")) return new ProgressDTO();

        JsonObject fields = doc.getAsJsonObject("fields");
        return new ProgressDTO(
                getIntValue(fields, "currentLevel", 1),
                getLongValue(fields, "totalExperience", 0L),
                getDoubleValue(fields, "levelProgress", 0.0),
                getIntValue(fields, "mobsKilled", 0),
                getIntValue(fields, "playersKilled", 0),
                getIntValue(fields, "deaths", 0)
        );
    }

    private WalletDTO parseWalletDTO(JsonObject walletObj) {
        if (!walletObj.has("mapValue")) return new WalletDTO();

        JsonObject mapValue = walletObj.getAsJsonObject("mapValue");
        if (!mapValue.has("fields")) return new WalletDTO();

        JsonObject fields = mapValue.getAsJsonObject("fields");
        Map<String, Long> currencies = new HashMap<>();

        if (fields.has("currencies") && fields.get("currencies").isJsonObject()) {
            JsonObject currenciesMap = fields.getAsJsonObject("currencies").getAsJsonObject("mapValue");
            if (currenciesMap != null && currenciesMap.has("fields")) {
                JsonObject currencyFields = currenciesMap.getAsJsonObject("fields");
                for (Map.Entry<String, JsonElement> entry : currencyFields.entrySet()) {
                    if (entry.getValue().isJsonObject() && entry.getValue().getAsJsonObject().has("integerValue")) {
                        currencies.put(entry.getKey(),
                                entry.getValue().getAsJsonObject().get("integerValue").getAsLong());
                    }
                }
            }
        }

        long lastUpdated = getLongValue(fields, "lastUpdated", System.currentTimeMillis());
        return new WalletDTO(currencies, lastUpdated);
    }

    private LeaderboardEntryDTO parseLeaderboardDTO(JsonObject doc) {
        if (!doc.has("fields")) return null;

        JsonObject fields = doc.getAsJsonObject("fields");
        return new LeaderboardEntryDTO(
                getStringValue(fields, "playerUuid"),
                getStringValue(fields, "playerName"),
                getIntValue(fields, "rank", 0),
                getLongValue(fields, "value", 0L),
                getStringValue(fields, "type"),
                getLongValue(fields, "lastUpdated", System.currentTimeMillis())
        );
    }

    private ServerStatsDTO parseServerStatsDTO(JsonObject doc) {
        if (!doc.has("fields")) return new ServerStatsDTO();

        JsonObject fields = doc.getAsJsonObject("fields");
        return new ServerStatsDTO(
                getIntValue(fields, "onlinePlayers", 0),
                getIntValue(fields, "maxPlayers", 0),
                getIntValue(fields, "totalPlayers", 0),
                getLongValue(fields, "uptime", 0L),
                getDoubleValue(fields, "tps", 20.0),
                getLongValue(fields, "totalPlaytime", 0L),
                getStringValue(fields, "version"),
                getLongValue(fields, "lastUpdated", System.currentTimeMillis())
        );
    }

    // ========== 문서 생성 메서드 ==========

    private JsonObject createPlayerDocument(PlayerDTO player, WalletDTO wallet) {
        JsonObject doc = new JsonObject();
        JsonObject fields = new JsonObject();

        fields.add("uuid", createStringValue(player.uuid()));
        fields.add("name", createStringValue(player.name()));
        fields.add("lastLogin", createIntegerValue(player.lastLogin()));
        fields.add("totalPlaytime", createIntegerValue(player.totalPlaytime()));
        if (player.job() != null) {
            fields.add("job", createStringValue(player.job().name()));
        }
        fields.add("wallet", createWalletMap(wallet));
        fields.add("lastUpdated", createTimestampValue());

        doc.add("fields", fields);
        return doc;
    }

    private JsonObject createStatsDocument(StatsDTO stats) {
        JsonObject doc = new JsonObject();
        JsonObject fields = new JsonObject();

        fields.add("strength", createIntegerValue(stats.strength()));
        fields.add("intelligence", createIntegerValue(stats.intelligence()));
        fields.add("dexterity", createIntegerValue(stats.dexterity()));
        fields.add("vitality", createIntegerValue(stats.vitality()));
        fields.add("wisdom", createIntegerValue(stats.wisdom()));
        fields.add("luck", createIntegerValue(stats.luck()));

        doc.add("fields", fields);
        return doc;
    }

    private JsonObject createTalentsDocument(TalentDTO talents) {
        JsonObject doc = new JsonObject();
        JsonObject fields = new JsonObject();

        fields.add("availablePoints", createIntegerValue(talents.availablePoints()));
        fields.add("learnedTalents", createMapValue(talents.learnedTalents()));

        doc.add("fields", fields);
        return doc;
    }

    private JsonObject createProgressDocument(ProgressDTO progress) {
        JsonObject doc = new JsonObject();
        JsonObject fields = new JsonObject();

        fields.add("currentLevel", createIntegerValue(progress.currentLevel()));
        fields.add("totalExperience", createIntegerValue(progress.totalExperience()));
        fields.add("levelProgress", createDoubleValue(progress.levelProgress()));
        fields.add("mobsKilled", createIntegerValue(progress.mobsKilled()));
        fields.add("playersKilled", createIntegerValue(progress.playersKilled()));
        fields.add("deaths", createIntegerValue(progress.deaths()));

        doc.add("fields", fields);
        return doc;
    }

    private JsonObject createLeaderboardDocument(LeaderboardEntryDTO entry) {
        JsonObject doc = new JsonObject();
        JsonObject fields = new JsonObject();

        fields.add("playerUuid", createStringValue(entry.playerUuid()));
        fields.add("playerName", createStringValue(entry.playerName()));
        fields.add("rank", createIntegerValue(entry.rank()));
        fields.add("value", createIntegerValue(entry.value()));
        fields.add("type", createStringValue(entry.type()));
        fields.add("lastUpdated", createTimestampValue());

        doc.add("fields", fields);
        return doc;
    }

    private JsonObject createServerStatsDocument(ServerStatsDTO stats) {
        JsonObject doc = new JsonObject();
        JsonObject fields = new JsonObject();

        fields.add("onlinePlayers", createIntegerValue(stats.onlinePlayers()));
        fields.add("maxPlayers", createIntegerValue(stats.maxPlayers()));
        fields.add("totalPlayers", createIntegerValue(stats.totalPlayers()));
        fields.add("uptime", createIntegerValue(stats.uptime()));
        fields.add("tps", createDoubleValue(stats.tps()));
        fields.add("totalPlaytime", createIntegerValue(stats.totalPlaytime()));
        fields.add("version", createStringValue(stats.version()));
        fields.add("lastUpdated", createTimestampValue());

        doc.add("fields", fields);
        return doc;
    }

    // ========== 값 생성 헬퍼 메서드 ==========

    private JsonObject createStringValue(String value) {
        JsonObject obj = new JsonObject();
        obj.addProperty("stringValue", value != null ? value : "");
        return obj;
    }

    private JsonObject createIntegerValue(long value) {
        JsonObject obj = new JsonObject();
        obj.addProperty("integerValue", String.valueOf(value));
        return obj;
    }

    private JsonObject createDoubleValue(double value) {
        JsonObject obj = new JsonObject();
        obj.addProperty("doubleValue", value);
        return obj;
    }

    private JsonObject createTimestampValue() {
        JsonObject obj = new JsonObject();
        obj.addProperty("timestampValue", Instant.now().toString());
        return obj;
    }

    private JsonObject createMapValue(Map<String, Integer> map) {
        JsonObject mapValue = new JsonObject();
        JsonObject fields = new JsonObject();

        for (Map.Entry<String, Integer> entry : map.entrySet()) {
            fields.add(entry.getKey(), createIntegerValue(entry.getValue()));
        }

        mapValue.add("fields", fields);
        JsonObject result = new JsonObject();
        result.add("mapValue", mapValue);
        return result;
    }

    private JsonObject createWalletMap(WalletDTO wallet) {
        JsonObject mapValue = new JsonObject();
        JsonObject fields = new JsonObject();

        // currencies 맵 생성
        JsonObject currenciesMap = new JsonObject();
        JsonObject currencyFields = new JsonObject();

        for (Map.Entry<String, Long> entry : wallet.currencies().entrySet()) {
            currencyFields.add(entry.getKey(), createIntegerValue(entry.getValue()));
        }

        currenciesMap.add("fields", currencyFields);
        JsonObject currencies = new JsonObject();
        currencies.add("mapValue", currenciesMap);

        fields.add("currencies", currencies);
        fields.add("lastUpdated", createIntegerValue(wallet.lastUpdated()));

        mapValue.add("fields", fields);
        JsonObject result = new JsonObject();
        result.add("mapValue", mapValue);
        return result;
    }

    // ========== 값 추출 헬퍼 메서드 ==========

    private String getStringValue(JsonObject fields, String fieldName) {
        if (fields.has(fieldName) && fields.get(fieldName).isJsonObject()) {
            JsonObject field = fields.getAsJsonObject(fieldName);
            if (field.has("stringValue")) {
                return field.get("stringValue").getAsString();
            }
        }
        return null;
    }

    private int getIntValue(JsonObject fields, String fieldName, int defaultValue) {
        if (fields.has(fieldName) && fields.get(fieldName).isJsonObject()) {
            JsonObject field = fields.getAsJsonObject(fieldName);
            if (field.has("integerValue")) {
                return Integer.parseInt(field.get("integerValue").getAsString());
            }
        }
        return defaultValue;
    }

    private long getLongValue(JsonObject fields, String fieldName, long defaultValue) {
        if (fields.has(fieldName) && fields.get(fieldName).isJsonObject()) {
            JsonObject field = fields.getAsJsonObject(fieldName);
            if (field.has("integerValue")) {
                return Long.parseLong(field.get("integerValue").getAsString());
            }
        }
        return defaultValue;
    }

    private long getLongValue(JsonObject fields, String fieldName) {
        return getLongValue(fields, fieldName, 0L);
    }

    private double getDoubleValue(JsonObject fields, String fieldName, double defaultValue) {
        if (fields.has(fieldName) && fields.get(fieldName).isJsonObject()) {
            JsonObject field = fields.getAsJsonObject(fieldName);
            if (field.has("doubleValue")) {
                return field.get("doubleValue").getAsDouble();
            }
        }
        return defaultValue;
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

    // ========== 기타 메서드 ==========

    private Map<String, Object> createDefaultPlayerData() {
        Map<String, Object> defaultData = new HashMap<>();
        defaultData.put("player", new PlayerDTO("", "Unknown"));
        defaultData.put("stats", new StatsDTO());
        defaultData.put("talents", new TalentDTO());
        defaultData.put("progress", new ProgressDTO());
        defaultData.put("wallet", new WalletDTO());
        return defaultData;
    }

    public Map<String, Object> getCacheStats() {
        return Map.of(
                "playerCacheSize", playerCache.size(),
                "cacheHitRate", 0.0
        );
    }

    public CompletableFuture<Map<String, Object>> loadAllPlayerData(@NotNull String uuid) {
        return loadAllPlayerDataWithWallet(uuid);
    }

    /**
     * 다른 메서드들 (deleteLeaderboardEntry, loadPlayerLeaderboardEntry 등)
     * 필요에 따라 구현
     */
    public CompletableFuture<Boolean> deleteLeaderboardEntry(@NotNull String playerUuid, @NotNull String type) {
        // DELETE 구현
        return CompletableFuture.completedFuture(false);
    }

    public CompletableFuture<LeaderboardEntryDTO> loadPlayerLeaderboardEntry(@NotNull String playerUuid, @NotNull String type) {
        String documentId = type + "_" + playerUuid;
        return getDocument(LEADERBOARDS_COLLECTION + "/" + documentId)
                .thenApply(doc -> doc == null ? null : parseLeaderboardDTO(doc));
    }

    public CompletableFuture<ServerStatsDTO> loadServerStatsByDate(@NotNull String date) {
        return getDocument(SERVER_STATS_COLLECTION + "/" + date)
                .thenApply(doc -> doc == null ? new ServerStatsDTO() : parseServerStatsDTO(doc));
    }

    public CompletableFuture<List<ServerStatsDTO>> loadRecentServerStats(int days) {
        // 복잡한 쿼리 구현 필요
        return CompletableFuture.completedFuture(new ArrayList<>());
    }

    /**
     * 연결 종료
     */
    public void shutdown() {
        tokenRefreshExecutor.shutdown();
        try {
            if (!tokenRefreshExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                tokenRefreshExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            tokenRefreshExecutor.shutdownNow();
        }
        clearCache();
        LogUtil.info("Firebase REST 서비스가 정상적으로 종료되었습니다.");
    }
}