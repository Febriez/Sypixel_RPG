package com.febrie.rpg.database;

import com.febrie.rpg.dto.player.PlayerDTO;
import com.febrie.rpg.dto.player.ProgressDTO;
import com.febrie.rpg.dto.player.StatsDTO;
import com.febrie.rpg.dto.player.TalentDTO;
import com.febrie.rpg.dto.player.WalletDTO;
import com.febrie.rpg.dto.quest.CompletedQuestDTO;
import com.febrie.rpg.dto.quest.PlayerQuestDTO;
import com.febrie.rpg.dto.system.LeaderboardEntryDTO;
import com.febrie.rpg.dto.system.ServerStatsDTO;
import com.febrie.rpg.job.JobType;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.util.LogUtil;
import com.febrie.rpg.util.RetryUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

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
    private static final String USERS_COLLECTION = "users";

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
                .connectTimeout(Duration.ofSeconds(10))
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
        return RetryUtil.executeWithRetry(() -> 
            CompletableFuture.supplyAsync(() -> {
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
                        // 5xx 오류는 재시도 가능
                        if (response.statusCode() >= 500) {
                            throw new RuntimeException("Server error: " + response.statusCode());
                        }
                        return false;
                    }
                } catch (Exception e) {
                    LogUtil.error("문서 쓰기 중 오류: " + path, e);
                    // 네트워크 오류는 재시도
                    if (e instanceof java.net.ConnectException || 
                        e instanceof java.net.SocketTimeoutException ||
                        e instanceof java.io.IOException) {
                        throw new RuntimeException("네트워크 오류", e);
                    }
                    return false;
                }
            })
        , false);
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
                    JsonArray rawResults = JsonParser.parseString(response.body()).getAsJsonArray();
                    
                    // 실제 문서가 있는 결과만 필터링
                    JsonArray actualResults = new JsonArray();
                    for (int i = 0; i < rawResults.size(); i++) {
                        JsonObject result = rawResults.get(i).getAsJsonObject();
                        if (result.has("document")) {
                            actualResults.add(result);
                        }
                    }
                    
                    return actualResults;
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

        String documentId = LocalDate.now().toString();
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
        String todayId = LocalDate.now().toString();
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
                    return parsePlayerQuestDTO(doc, playerId);
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

        JsonObject doc = createQuestDataDocument(questData);
        return setDocument(PLAYERS_COLLECTION + "/" + playerId + "/" + QUEST_SUBCOLLECTION + "/data", doc);
    }

    // ========== 퀘스트 데이터 생성 및 파싱 ==========

    /**
     * 퀘스트 데이터를 Firestore 문서로 변환
     * 안전하고 가독성 좋은 구조로 리팩토링
     */
    private JsonObject createQuestDataDocument(@NotNull PlayerQuestDTO questData) {
        try {
            JsonObject doc = new JsonObject();
            JsonObject fields = new JsonObject();

            // 기본 필드
            fields.add("playerId", createStringValue(questData.playerId()));
            fields.add("lastUpdated", createIntegerValue(questData.lastUpdated()));

            // 활성 퀘스트 저장
            fields.add("activeQuests", createActiveQuestsMap(questData.activeQuests()));

            // 완료된 퀘스트 저장
            fields.add("completedQuests", createCompletedQuestsMap(questData.completedQuests()));

            doc.add("fields", fields);
            return doc;
        } catch (Exception e) {
            LogUtil.error("퀘스트 데이터 문서 생성 중 오류 발생", e);
            // 빈 문서 반환으로 안전 처리
            return createEmptyQuestDocument(questData.playerId());
        }
    }

    /**
     * 활성 퀘스트 맵 생성
     */
    private JsonObject createActiveQuestsMap(@NotNull Map<String, QuestProgress> activeQuests) {
        JsonObject activeQuestsFields = new JsonObject();
        
        activeQuests.forEach((questId, progress) -> {
            try {
                JsonObject progressValue = createQuestProgressValue(progress);
                activeQuestsFields.add(questId, progressValue);
            } catch (Exception e) {
                LogUtil.warning("활성 퀘스트 데이터 생성 실패 (ID: " + questId + "): " + e.getMessage());
            }
        });
        
        return createFirestoreMapValue(activeQuestsFields);
    }

    /**
     * 퀘스트 진행도 값 생성
     */
    private JsonObject createQuestProgressValue(@NotNull QuestProgress progress) {
        JsonObject progressFields = new JsonObject();
        
        // 기본 진행도 정보
        progressFields.add("questId", createStringValue(progress.getQuestId().name()));
        progressFields.add("playerId", createStringValue(progress.getPlayerId().toString()));
        progressFields.add("state", createStringValue(progress.getState().name()));
        progressFields.add("currentObjectiveIndex", createIntegerValue(progress.getCurrentObjectiveIndex()));
        progressFields.add("startedAt", createIntegerValue(progress.getStartedAt().toEpochMilli()));
        progressFields.add("lastUpdatedAt", createIntegerValue(progress.getLastUpdatedAt().toEpochMilli()));
        
        // 완료 시간 추가 (완료된 퀘스트인 경우)
        if (progress.getCompletedAt() != null) {
            progressFields.add("completedAt", createIntegerValue(progress.getCompletedAt().toEpochMilli()));
        }
        
        // 목표 진행도 저장
        JsonObject objectivesFields = new JsonObject();
        progress.getObjectives().forEach((objId, objProgress) -> {
            try {
                JsonObject objProgressFields = new JsonObject();
                objProgressFields.add("completed", createBooleanValue(objProgress.isCompleted()));
                objProgressFields.add("progress", createIntegerValue(objProgress.getCurrentValue()));
                objProgressFields.add("target", createIntegerValue(objProgress.getRequiredValue()));
                objProgressFields.add("lastUpdated", createIntegerValue(objProgress.getLastUpdated()));
                
                objectivesFields.add(objId, createFirestoreMapValue(objProgressFields));
            } catch (Exception e) {
                LogUtil.warning("목표 진행도 생성 실패 (ID: " + objId + "): " + e.getMessage());
            }
        });
        
        progressFields.add("objectives", createFirestoreMapValue(objectivesFields));
        
        return createFirestoreMapValue(progressFields);
    }

    /**
     * 완료된 퀘스트 맵 생성
     */
    private JsonObject createCompletedQuestsMap(@NotNull Map<String, CompletedQuestDTO> completedQuests) {
        JsonObject completedQuestsFields = new JsonObject();
        
        completedQuests.forEach((questId, completed) -> {
            try {
                JsonObject completedFields = new JsonObject();
                completedFields.add("questId", createStringValue(completed.questId()));
                completedFields.add("completedAt", createIntegerValue(completed.completedAt()));
                completedFields.add("completionCount", createIntegerValue(1));
                
                completedQuestsFields.add(questId, createFirestoreMapValue(completedFields));
            } catch (Exception e) {
                LogUtil.warning("완료된 퀘스트 데이터 생성 실패 (ID: " + questId + "): " + e.getMessage());
            }
        });
        
        return createFirestoreMapValue(completedQuestsFields);
    }

    /**
     * Firestore mapValue 타입 생성 헬퍼
     */
    private JsonObject createFirestoreMapValue(@NotNull JsonObject fields) {
        JsonObject mapValue = new JsonObject();
        mapValue.add("fields", fields);
        
        JsonObject result = new JsonObject();
        result.add("mapValue", mapValue);
        return result;
    }

    /**
     * 빈 퀘스트 문서 생성 (오류 시 안전 처리용)
     */
    private JsonObject createEmptyQuestDocument(@NotNull String playerId) {
        JsonObject doc = new JsonObject();
        JsonObject fields = new JsonObject();
        
        fields.add("playerId", createStringValue(playerId));
        fields.add("lastUpdated", createIntegerValue(System.currentTimeMillis()));
        fields.add("activeQuests", createFirestoreMapValue(new JsonObject()));
        fields.add("completedQuests", createFirestoreMapValue(new JsonObject()));
        
        doc.add("fields", fields);
        return doc;
    }

    /**
     * Firestore 문서를 퀘스트 데이터로 파싱
     * 안전하고 가독성 좋은 구조로 리팩토링
     */
    private PlayerQuestDTO parsePlayerQuestDTO(@NotNull JsonObject doc, @NotNull String playerId) {
        try {
            if (!doc.has("fields")) {
                return new PlayerQuestDTO(playerId);
            }

            JsonObject fields = doc.getAsJsonObject("fields");
            long lastUpdated = getLongValue(fields, "lastUpdated");

            // 활성 퀘스트 파싱
            Map<String, QuestProgress> activeQuests = parseActiveQuests(fields, playerId);

            // 완료된 퀘스트 파싱
            Map<String, CompletedQuestDTO> completedQuests = parseCompletedQuests(fields);

            return new PlayerQuestDTO(playerId, activeQuests, completedQuests, lastUpdated);
        } catch (Exception e) {
            LogUtil.error("퀘스트 데이터 파싱 중 오류 발생 (Player: " + playerId + ")", e);
            return new PlayerQuestDTO(playerId);
        }
    }

    /**
     * 활성 퀘스트 파싱
     */
    private Map<String, QuestProgress> parseActiveQuests(@NotNull JsonObject fields, @NotNull String playerId) {
        Map<String, QuestProgress> activeQuests = new HashMap<>();
        
        try {
            JsonObject activeQuestsMap = getFirestoreMapFields(fields, "activeQuests");
            if (activeQuestsMap == null) return activeQuests;
            
            for (Map.Entry<String, JsonElement> entry : activeQuestsMap.entrySet()) {
                String questId = entry.getKey();
                try {
                    JsonObject progressFields = getFirestoreMapFields(entry.getValue().getAsJsonObject());
                    if (progressFields != null) {
                        QuestProgress questProgress = parseQuestProgress(questId, progressFields, playerId);
                        if (questProgress != null) {
                            activeQuests.put(questId, questProgress);
                        }
                    }
                } catch (Exception e) {
                    LogUtil.warning("활성 퀘스트 파싱 실패 (ID: " + questId + "): " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LogUtil.warning("활성 퀘스트 목록 파싱 실패: " + e.getMessage());
        }
        
        return activeQuests;
    }

    /**
     * 퀘스트 진행도 파싱
     */
    private QuestProgress parseQuestProgress(@NotNull String questId, @NotNull JsonObject progressFields, @NotNull String playerId) {
        try {
            String playerIdStr = getStringValue(progressFields, "playerId");
            String state = getStringValue(progressFields, "state");
            int currentObjectiveIndex = getIntValue(progressFields, "currentObjectiveIndex", 0);
            long startedAtMillis = getLongValue(progressFields, "startedAt");
            long lastUpdatedAtMillis = getLongValue(progressFields, "lastUpdatedAt");
            
            // 목표 진행도 파싱
            Map<String, ObjectiveProgress> objectives = parseObjectiveProgress(progressFields, playerId);
            
            // 시간 변환 (밀리초 -> Instant)
            Instant startedAt = startedAtMillis > 0 ? 
                Instant.ofEpochMilli(startedAtMillis) : Instant.now();
            Instant lastUpdatedAt = lastUpdatedAtMillis > 0 ? 
                Instant.ofEpochMilli(lastUpdatedAtMillis) : Instant.now();
            
            // QuestProgress 객체 생성 (전체 생성자 사용)
            QuestID questIdEnum = QuestID.valueOf(questId);
            UUID playerUuid = UUID.fromString(playerIdStr != null ? playerIdStr : playerId);
            
            QuestProgress.QuestState questState = 
                state != null ? QuestProgress.QuestState.valueOf(state) 
                             : QuestProgress.QuestState.ACTIVE;
            
            // 완료 시간 파싱 (완료된 퀘스트인 경우)
            Instant completedAt = null;
            if (questState == QuestProgress.QuestState.COMPLETED) {
                long completedAtMillis = getLongValue(progressFields, "completedAt");
                if (completedAtMillis > 0) {
                    completedAt = Instant.ofEpochMilli(completedAtMillis);
                }
            }
            
            return new QuestProgress(
                questIdEnum, playerUuid, objectives, questState, 
                currentObjectiveIndex, startedAt, completedAt, lastUpdatedAt
            );
        } catch (Exception e) {
            LogUtil.warning("퀘스트 진행도 파싱 실패 (ID: " + questId + "): " + e.getMessage());
            return null;
        }
    }

    /**
     * 목표 진행도 파싱
     */
    private Map<String, ObjectiveProgress> parseObjectiveProgress(@NotNull JsonObject progressFields, @NotNull String playerId) {
        Map<String, ObjectiveProgress> objectives = new HashMap<>();
        
        try {
            JsonObject objectivesMap = getFirestoreMapFields(progressFields, "objectives");
            if (objectivesMap == null) return objectives;
            
            for (Map.Entry<String, JsonElement> objEntry : objectivesMap.entrySet()) {
                String objId = objEntry.getKey();
                try {
                    JsonObject objProgressFields = getFirestoreMapFields(objEntry.getValue().getAsJsonObject());
                    if (objProgressFields != null) {
                        boolean completed = getBooleanValue(objProgressFields, "completed");
                        int progress = getIntValue(objProgressFields, "progress", 0);
                        int target = getIntValue(objProgressFields, "target", 1);
                        long objLastUpdated = getLongValue(objProgressFields, "lastUpdated");
                        
                        ObjectiveProgress objProgress = 
                            new ObjectiveProgress(
                                objId, UUID.fromString(playerId), progress, target, completed
                            );
                        objectives.put(objId, objProgress);
                    }
                } catch (Exception e) {
                    LogUtil.warning("목표 진행도 파싱 실패 (ID: " + objId + "): " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LogUtil.warning("목표 진행도 목록 파싱 실패: " + e.getMessage());
        }
        
        return objectives;
    }

    /**
     * 완료된 퀘스트 파싱
     */
    private Map<String, CompletedQuestDTO> parseCompletedQuests(@NotNull JsonObject fields) {
        Map<String, CompletedQuestDTO> completedQuests = new HashMap<>();
        
        try {
            JsonObject completedQuestsMap = getFirestoreMapFields(fields, "completedQuests");
            if (completedQuestsMap == null) return completedQuests;
            
            for (Map.Entry<String, JsonElement> entry : completedQuestsMap.entrySet()) {
                String questId = entry.getKey();
                try {
                    JsonObject completedFields = getFirestoreMapFields(entry.getValue().getAsJsonObject());
                    if (completedFields != null) {
                        long completedAt = getLongValue(completedFields, "completedAt");
                        int completionCount = getIntValue(completedFields, "completionCount", 1);
                        
                        completedQuests.put(questId, new CompletedQuestDTO(questId, completedAt, completionCount));
                    }
                } catch (Exception e) {
                    LogUtil.warning("완료된 퀘스트 파싱 실패 (ID: " + questId + "): " + e.getMessage());
                }
            }
        } catch (Exception e) {
            LogUtil.warning("완료된 퀘스트 목록 파싱 실패: " + e.getMessage());
        }
        
        return completedQuests;
    }

    /**
     * Firestore mapValue에서 fields 추출 헬퍼
     */
    private JsonObject getFirestoreMapFields(@NotNull JsonObject parent, @NotNull String fieldName) {
        try {
            if (!parent.has(fieldName)) return null;
            
            JsonObject mapObj = parent.getAsJsonObject(fieldName);
            if (!mapObj.has("mapValue")) return null;
            
            JsonObject mapValue = mapObj.getAsJsonObject("mapValue");
            if (!mapValue.has("fields")) return null;
            
            return mapValue.getAsJsonObject("fields");
        } catch (Exception e) {
            LogUtil.warning("Firestore mapValue 파싱 실패 (field: " + fieldName + "): " + e.getMessage());
            return null;
        }
    }

    /**
     * JsonObject에서 직접 mapValue fields 추출
     */
    private JsonObject getFirestoreMapFields(@NotNull JsonObject mapObj) {
        try {
            if (!mapObj.has("mapValue")) return null;
            
            JsonObject mapValue = mapObj.getAsJsonObject("mapValue");
            if (!mapValue.has("fields")) return null;
            
            return mapValue.getAsJsonObject("fields");
        } catch (Exception e) {
            LogUtil.warning("Firestore mapValue 직접 파싱 실패: " + e.getMessage());
            return null;
        }
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

    private boolean getBooleanValue(JsonObject fields, String fieldName) {
        return getBooleanValue(fields, fieldName, false);
    }

    private boolean getBooleanValue(JsonObject fields, String fieldName, boolean defaultValue) {
        if (fields.has(fieldName) && fields.get(fieldName).isJsonObject()) {
            JsonObject field = fields.getAsJsonObject(fieldName);
            if (field.has("booleanValue")) {
                return field.get("booleanValue").getAsBoolean();
            }
        }
        return defaultValue;
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

    // ========== 웹사이트 계정 관리 ==========

    /**
     * 사용자 계정 중복 확인
     * 웹사이트 사용자 컬렉션에서만 확인 (RPG 플레이어 데이터와 별도)
     */
    public CompletableFuture<Boolean> checkAccountExists(@NotNull String uuid, @NotNull String email) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(false);
        }

        // UUID로 웹사이트 사용자 확인
        CompletableFuture<Boolean> uuidExists = getDocument(USERS_COLLECTION + "/" + uuid)
                .thenApply(doc -> doc != null);

        // 이메일로 웹사이트 사용자 확인 (쿼리 필요)
        JsonObject emailQuery = createEmailQuery(email);
        CompletableFuture<Boolean> emailExists = runQuery(USERS_COLLECTION, emailQuery)
                .thenApply(results -> {
                    // 실제 문서 내용을 확인하여 이메일 직접 비교
                    for (int i = 0; i < results.size(); i++) {
                        JsonObject result = results.get(i).getAsJsonObject();
                        
                        if (result.has("document")) {
                            JsonObject document = result.getAsJsonObject("document");
                            
                            if (document.has("fields")) {
                                JsonObject fields = document.getAsJsonObject("fields");
                                
                                // 이메일 필드 확인
                                if (fields.has("email") && fields.getAsJsonObject("email").has("stringValue")) {
                                    String foundEmail = fields.getAsJsonObject("email").get("stringValue").getAsString();
                                    if (email.equals(foundEmail)) {
                                        return true; // 이메일 일치, 중복 발견
                                    }
                                }
                            }
                        }
                    }
                    return false; // 일치하는 이메일 없음
                });

        return uuidExists.thenCombine(emailExists, (uuidResult, emailResult) -> uuidResult || emailResult);
    }

    /**
     * 이메일 중복 확인을 위한 쿼리 생성
     */
    private JsonObject createEmailQuery(String email) {
        JsonObject query = new JsonObject();
        JsonObject structuredQuery = new JsonObject();

        // FROM 절
        JsonArray from = new JsonArray();
        JsonObject collection = new JsonObject();
        collection.addProperty("collectionId", USERS_COLLECTION);
        from.add(collection);
        structuredQuery.add("from", from);

        // WHERE 절
        JsonObject where = new JsonObject();
        JsonObject fieldFilter = new JsonObject();
        JsonObject field = new JsonObject();
        field.addProperty("fieldPath", "email");
        fieldFilter.add("field", field);
        fieldFilter.addProperty("op", "EQUAL");
        JsonObject value = new JsonObject();
        value.addProperty("stringValue", email);
        fieldFilter.add("value", value);
        where.add("fieldFilter", fieldFilter);
        structuredQuery.add("where", where);

        // LIMIT 1
        structuredQuery.addProperty("limit", 1);

        query.add("structuredQuery", structuredQuery);
        return query;
    }

    /**
     * Firebase Auth 계정 생성
     */
    public CompletableFuture<String> createAuthAccount(@NotNull String email, @NotNull String password) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JsonObject requestBody = new JsonObject();
                requestBody.addProperty("email", email);
                requestBody.addProperty("password", password);
                requestBody.addProperty("returnSecureToken", true);

                String webApiKey = System.getenv("FIREBASE_WEB_API_KEY");
                if (webApiKey == null || webApiKey.isEmpty()) {
                    LogUtil.error("FIREBASE_WEB_API_KEY 환경변수가 설정되지 않았습니다.");
                    return null;
                }

                String url = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + webApiKey;

                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                        .build();

                HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

                if (response.statusCode() == 200) {
                    JsonObject responseBody = JsonParser.parseString(response.body()).getAsJsonObject();
                    String localId = responseBody.get("localId").getAsString();
                    return localId;
                } else {
                    LogUtil.error("Firebase Auth 계정 생성 실패: " + response.statusCode() + " - " + response.body());
                    return null;
                }
            } catch (Exception e) {
                LogUtil.error("Firebase Auth 계정 생성 중 오류", e);
                return null;
            }
        });
    }

    /**
     * 관리자 여부 확인 (마인크래프트 권한 시스템 연동)
     */
    public CompletableFuture<Boolean> checkIsAdmin(@NotNull String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 마인크래프트 서버에서 플레이어 객체 가져오기
                Player player = Bukkit.getPlayer(UUID.fromString(uuid));
                
                if (player != null) {
                    // 플레이어가 온라인인 경우 실시간 권한 확인
                    return player.isOp() || player.hasPermission("sypixelrpg.admin");
                } else {
                    // 플레이어가 오프라인인 경우 OfflinePlayer로 확인
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
                    return offlinePlayer.isOp();
                }
            } catch (Exception e) {
                LogUtil.warning("관리자 권한 확인 중 오류 발생 (UUID: " + uuid + "): " + e.getMessage());
                return false;
            }
        });
    }


    /**
     * 웹사이트 사용자 정보 저장
     */
    public CompletableFuture<Boolean> saveWebsiteUser(@NotNull String uuid, @NotNull String email, boolean isAdmin) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(false);
        }

        JsonObject doc = new JsonObject();
        JsonObject fields = new JsonObject();

        fields.add("uuid", createStringValue(uuid));
        fields.add("email", createStringValue(email));
        fields.add("isAdmin", createBooleanValue(isAdmin));
        fields.add("createdAt", createTimestampValue());

        doc.add("fields", fields);

        return setDocument(USERS_COLLECTION + "/" + uuid, doc);
    }

    /**
     * 랜덤 비밀번호 생성
     */
    public String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();
        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 12; i++) {
            password.append(chars.charAt(random.nextInt(chars.length())));
        }

        return password.toString();
    }

    /**
     * 사이트 계정 발급 (통합 메서드)
     */
    public CompletableFuture<SiteAccountResult> createSiteAccount(@NotNull String uuid, @NotNull String email) {
        return checkAccountExists(uuid, email)
                .thenCompose(exists -> {
                    if (exists) {
                        return CompletableFuture.completedFuture(
                                new SiteAccountResult(false, "이미 등록된 UUID 또는 이메일입니다.", null)
                        );
                    }

                    String password = generateRandomPassword();

                    return createAuthAccount(email, password)
                            .thenCompose(authUid -> {
                                if (authUid == null) {
                                    return CompletableFuture.completedFuture(
                                            new SiteAccountResult(false, "Firebase Auth 계정 생성 실패", null)
                                    );
                                }

                                return checkIsAdmin(uuid)
                                        .thenCompose(isAdmin -> {
                                            return saveWebsiteUser(uuid, email, isAdmin)
                                                    .thenApply(userResult -> {
                                                        if (userResult) {
                                                            return new SiteAccountResult(true, "계정이 성공적으로 생성되었습니다.", password);
                                                        } else {
                                                            return new SiteAccountResult(false, "계정 설정 중 오류가 발생했습니다.", null);
                                                        }
                                                    });
                                        });
                            });
                });
    }

    /**
     * Boolean 값 생성 헬퍼 메서드
     */
    private JsonObject createBooleanValue(boolean value) {
        JsonObject obj = new JsonObject();
        obj.addProperty("booleanValue", value);
        return obj;
    }

    /**
     * 사이트 계정 생성 결과 클래스
     */
    public static class SiteAccountResult {
        private final boolean success;
        private final String message;
        private final String password;

        public SiteAccountResult(boolean success, String message, String password) {
            this.success = success;
            this.message = message;
            this.password = password;
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }

        public String getPassword() {
            return password;
        }
    }


    // === 소셜 시스템용 공개 메소드들 ===
    
    /**
     * 문서 설정 (공개 메소드)
     */
    public boolean setDocument(@NotNull String collection, @NotNull String documentId, @NotNull Map<String, Object> data) {
        try {
            JsonObject jsonData = convertMapToJsonObject(data);
            return setDocument(collection + "/" + documentId, jsonData).get();
        } catch (Exception e) {
            LogUtil.error("문서 설정 실패: " + collection + "/" + documentId, e);
            return false;
        }
    }
    
    /**
     * 문서 가져오기 (공개 메소드)
     */
    @Nullable
    public Map<String, Object> getDocument(@NotNull String collection, @NotNull String documentId) {
        try {
            JsonObject doc = getDocument(collection + "/" + documentId).get();
            if (doc == null) return null;
            return convertJsonObjectToMap(doc);
        } catch (Exception e) {
            LogUtil.error("문서 조회 실패: " + collection + "/" + documentId, e);
            return null;
        }
    }
    
    /**
     * 문서 삭제 (공개 메소드)
     */
    public boolean deleteDocument(@NotNull String collection, @NotNull String documentId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(FIRESTORE_BASE_URL + "/" + collection + "/" + documentId))
                    .DELETE()
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            return response.statusCode() == 200;
        } catch (Exception e) {
            LogUtil.error("문서 삭제 실패: " + collection + "/" + documentId, e);
            return false;
        }
    }
    
    /**
     * 문서 쿼리 (공개 메소드)
     */
    @Nullable
    public Map<String, Object> queryDocuments(@NotNull String collection, @NotNull String query) {
        try {
            // Firestore structured query
            JsonObject structuredQuery = new JsonObject();
            JsonArray from = new JsonArray();
            JsonObject fromObj = new JsonObject();
            fromObj.addProperty("collectionId", collection);
            from.add(fromObj);
            structuredQuery.add("from", from);
            
            // TODO: 실제 쿼리 구문 분석 구현 (현재는 간단한 구현)
            
            JsonObject requestBody = new JsonObject();
            requestBody.add("structuredQuery", structuredQuery);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(FIRESTORE_BASE_URL + ":runQuery"))
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                    .header("Authorization", "Bearer " + accessToken)
                    .header("Content-Type", "application/json")
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() == 200) {
                JsonArray results = JsonParser.parseString(response.body()).getAsJsonArray();
                JsonObject result = new JsonObject();
                JsonArray documents = new JsonArray();
                
                for (JsonElement element : results) {
                    JsonObject item = element.getAsJsonObject();
                    if (item.has("document")) {
                        documents.add(item.get("document"));
                    }
                }
                
                result.add("documents", documents);
                return convertJsonObjectToMap(result);
            }
            
            return null;
        } catch (Exception e) {
            LogUtil.error("문서 쿼리 실패: " + collection, e);
            return null;
        }
    }
    
    /**
     * Map을 JsonObject로 변환
     */
    private JsonObject convertMapToJsonObject(@NotNull Map<String, Object> map) {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            JsonObject fieldValue = new JsonObject();
            Object value = entry.getValue();
            
            if (value instanceof String) {
                fieldValue.addProperty("stringValue", (String) value);
            } else if (value instanceof Boolean) {
                fieldValue.addProperty("booleanValue", (Boolean) value);
            } else if (value instanceof Integer) {
                fieldValue.addProperty("integerValue", (Integer) value);
            } else if (value instanceof Long) {
                fieldValue.addProperty("integerValue", (Long) value);
            } else if (value instanceof Double) {
                fieldValue.addProperty("doubleValue", (Double) value);
            } else {
                fieldValue.addProperty("stringValue", value.toString());
            }
            
            fields.add(entry.getKey(), fieldValue);
        }
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject를 Map으로 변환
     */
    private Map<String, Object> convertJsonObjectToMap(@NotNull JsonObject json) {
        Map<String, Object> map = new HashMap<>();
        
        if (json.has("fields")) {
            JsonObject fields = json.getAsJsonObject("fields");
            
            for (Map.Entry<String, JsonElement> entry : fields.entrySet()) {
                JsonObject fieldValue = entry.getValue().getAsJsonObject();
                
                Object value = null;
                if (fieldValue.has("stringValue")) {
                    value = fieldValue.get("stringValue").getAsString();
                } else if (fieldValue.has("booleanValue")) {
                    value = fieldValue.get("booleanValue").getAsBoolean();
                } else if (fieldValue.has("integerValue")) {
                    value = fieldValue.get("integerValue").getAsLong();
                } else if (fieldValue.has("doubleValue")) {
                    value = fieldValue.get("doubleValue").getAsDouble();
                }
                
                map.put(entry.getKey(), value);
            }
        }
        
        return map;
    }

    /**
     * 연결 종료
     */
    // ========== 섬(Island) 관련 메서드 ==========
    
    private static final String ISLANDS_COLLECTION = "Islands";
    private static final String PLAYER_ISLAND_DATA_COLLECTION = "PlayerIslandData";
    
    /**
     * 섬 정보 저장
     */
    public CompletableFuture<Boolean> saveIsland(@NotNull com.febrie.rpg.dto.island.IslandDTO island) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(false);
        }
        
        Map<String, Object> data = island.toMap();
        return setDocument(ISLANDS_COLLECTION + "/" + island.islandId(), convertMapToJsonObject(data));
    }
    
    /**
     * 섬 정보 로드
     */
    public CompletableFuture<com.febrie.rpg.dto.island.IslandDTO> loadIsland(@NotNull String islandId) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(null);
        }
        
        return getDocument(ISLANDS_COLLECTION + "/" + islandId)
                .thenApply(doc -> {
                    if (doc == null) return null;
                    Map<String, Object> data = convertJsonObjectToMap(doc);
                    return com.febrie.rpg.dto.island.IslandDTO.fromMap(data);
                });
    }
    
    /**
     * 플레이어의 섬 데이터 저장
     */
    public CompletableFuture<Boolean> savePlayerIslandData(@NotNull com.febrie.rpg.dto.island.PlayerIslandDataDTO data) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(false);
        }
        
        Map<String, Object> map = data.toMap();
        return setDocument(PLAYER_ISLAND_DATA_COLLECTION + "/" + data.playerUuid(), convertMapToJsonObject(map));
    }
    
    /**
     * 플레이어의 섬 데이터 로드
     */
    public CompletableFuture<com.febrie.rpg.dto.island.PlayerIslandDataDTO> loadPlayerIslandData(@NotNull String playerUuid) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(null);
        }
        
        return getDocument(PLAYER_ISLAND_DATA_COLLECTION + "/" + playerUuid)
                .thenApply(doc -> {
                    if (doc == null) return null;
                    Map<String, Object> data = convertJsonObjectToMap(doc);
                    return com.febrie.rpg.dto.island.PlayerIslandDataDTO.fromMap(data);
                });
    }
    
    /**
     * 섬 삭제
     */
    public CompletableFuture<Boolean> deleteIsland(@NotNull String islandId) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(false);
        }
        
        return deleteDocument(ISLANDS_COLLECTION + "/" + islandId);
    }
    
    /**
     * 공개 섬 목록 조회
     */
    public CompletableFuture<List<com.febrie.rpg.dto.island.IslandDTO>> loadPublicIslands(int limit) {
        if (!isConnected()) {
            return CompletableFuture.completedFuture(new ArrayList<>());
        }
        
        JsonObject query = new JsonObject();
        JsonObject structuredQuery = new JsonObject();
        
        // FROM
        JsonArray from = new JsonArray();
        JsonObject collection = new JsonObject();
        collection.addProperty("collectionId", ISLANDS_COLLECTION);
        from.add(collection);
        structuredQuery.add("from", from);
        
        // WHERE isPublic = true
        JsonObject where = new JsonObject();
        JsonObject fieldFilter = new JsonObject();
        JsonObject field = new JsonObject();
        field.addProperty("fieldPath", "isPublic");
        fieldFilter.add("field", field);
        fieldFilter.addProperty("op", "EQUAL");
        JsonObject value = new JsonObject();
        value.addProperty("booleanValue", true);
        fieldFilter.add("value", value);
        where.add("fieldFilter", fieldFilter);
        structuredQuery.add("where", where);
        
        // ORDER BY lastActivity DESC
        JsonArray orderBy = new JsonArray();
        JsonObject order = new JsonObject();
        JsonObject orderField = new JsonObject();
        orderField.addProperty("fieldPath", "lastActivity");
        order.add("field", orderField);
        order.addProperty("direction", "DESCENDING");
        orderBy.add(order);
        structuredQuery.add("orderBy", orderBy);
        
        // LIMIT
        structuredQuery.addProperty("limit", limit);
        
        query.add("structuredQuery", structuredQuery);
        
        return runQuery(ISLANDS_COLLECTION, query)
                .thenApply(results -> {
                    List<com.febrie.rpg.dto.island.IslandDTO> islands = new ArrayList<>();
                    for (JsonObject result : results) {
                        try {
                            if (result.has("document")) {
                                JsonObject document = result.getAsJsonObject("document");
                                Map<String, Object> data = convertJsonObjectToMap(document);
                                islands.add(com.febrie.rpg.dto.island.IslandDTO.fromMap(data));
                            }
                        } catch (Exception e) {
                            LogUtil.warning("섬 파싱 실패: " + e.getMessage());
                        }
                    }
                    return islands;
                });
    }
    
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