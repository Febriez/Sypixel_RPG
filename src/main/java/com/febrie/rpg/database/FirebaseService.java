package com.febrie.rpg.database;

import com.febrie.rpg.dto.*;
import com.febrie.rpg.util.LogUtil;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Firebase Firestore 데이터베이스 서비스
 * 환경변수를 통한 설정 관리 및 DTO 기반 데이터 처리
 *
 * @author Febrie, CoffeeTory
 */
public class FirebaseService {

    private final Plugin plugin;
    private final String serverName;

    private Firestore firestore;
    private boolean connected = false;

    // 캐시 (동시접속 1000명 대응)
    private final Map<String, PlayerDTO> playerCache = new ConcurrentHashMap<>();
    private final Map<String, Long> cacheTimestamps = new ConcurrentHashMap<>();
    private static final long CACHE_DURATION = 5 * 60 * 1000; // 5분

    // 컬렉션 이름들
    private static final String PLAYERS_COLLECTION = "players";
    private static final String LEADERBOARDS_COLLECTION = "leaderboards";
    private static final String SERVER_STATS_COLLECTION = "server_stats";

    // 서브컬렉션 이름들
    private static final String STATS_SUBCOLLECTION = "stats";
    private static final String TALENTS_SUBCOLLECTION = "talents";
    private static final String PROGRESS_SUBCOLLECTION = "progress";

    // 환경변수 이름들
    private static final String ENV_PROJECT_ID = "FIREBASE_PROJECT_ID";
    private static final String ENV_DATABASE_URL = "FIREBASE_DATABASE_URL";
    private static final String ENV_CREDENTIALS_PATH = "FIREBASE_CREDENTIALS_PATH";
    private static final String ENV_CREDENTIALS_JSON = "FIREBASE_CREDENTIALS_JSON";

    public FirebaseService(@NotNull Plugin plugin) {
        this.plugin = plugin;
        this.serverName = plugin.getConfig().getString("server-name", "default");
        initializeFirebase();
    }

    /**
     * Firebase 초기화 (환경변수 사용)
     */
    private void initializeFirebase() {
        try {
            // 환경변수 읽기
            String projectId = System.getenv(ENV_PROJECT_ID);
            String databaseUrl = System.getenv(ENV_DATABASE_URL);
            String credentialsPath = System.getenv(ENV_CREDENTIALS_PATH);
            String credentialsJson = System.getenv(ENV_CREDENTIALS_JSON);

            if (projectId == null || projectId.isEmpty()) {
                LogUtil.error("Firebase 초기화 실패: " + ENV_PROJECT_ID + " 환경변수가 설정되지 않았습니다.");
                LogUtil.info("필요한 환경변수:");
                LogUtil.info("  - " + ENV_PROJECT_ID + ": Firebase 프로젝트 ID");
                LogUtil.info("  - " + ENV_DATABASE_URL + ": Firestore 데이터베이스 URL (선택사항)");
                LogUtil.info("  - " + ENV_CREDENTIALS_PATH + ": 서비스 계정 JSON 파일 경로");
                LogUtil.info("  또는");
                LogUtil.info("  - " + ENV_CREDENTIALS_JSON + ": 서비스 계정 JSON 내용 (Base64 인코딩)");
                return;
            }

            // Credentials 설정
            GoogleCredentials credentials;
            if (credentialsJson != null && !credentialsJson.isEmpty()) {
                // JSON 문자열로부터 직접 로드 (Base64 디코딩)
                byte[] credentialsBytes = java.util.Base64.getDecoder().decode(credentialsJson);
                ByteArrayInputStream credentialsStream = new ByteArrayInputStream(credentialsBytes);
                credentials = GoogleCredentials.fromStream(credentialsStream);
                LogUtil.info("Firebase 인증정보를 환경변수에서 로드했습니다.");
            } else if (credentialsPath != null && !credentialsPath.isEmpty()) {
                // 파일 경로로부터 로드
                FileInputStream serviceAccount = new FileInputStream(credentialsPath);
                credentials = GoogleCredentials.fromStream(serviceAccount);
                LogUtil.info("Firebase 인증정보를 파일에서 로드했습니다: " + credentialsPath);
            } else {
                // 기본 위치에서 시도
                String defaultPath = plugin.getDataFolder().getPath() + "/firebase-credentials.json";
                FileInputStream serviceAccount = new FileInputStream(defaultPath);
                credentials = GoogleCredentials.fromStream(serviceAccount);
                LogUtil.warning("Firebase 인증정보를 기본 경로에서 로드했습니다: " + defaultPath);
            }

            // Firebase 옵션 빌드
            FirebaseOptions.Builder optionsBuilder = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setProjectId(projectId);

            if (databaseUrl != null && !databaseUrl.isEmpty()) {
                optionsBuilder.setDatabaseUrl(databaseUrl);
            }

            FirebaseOptions options = optionsBuilder.build();

            // Firebase 앱 초기화
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            firestore = FirestoreClient.getFirestore();
            connected = true;
            LogUtil.info("Firebase 초기화 완료! 프로젝트 ID: " + projectId);

        } catch (IOException e) {
            LogUtil.error("Firebase 초기화 중 오류 발생", e);
        }
    }

    /**
     * 연결 상태 확인
     */
    public boolean isConnected() {
        return firestore != null;
    }

    /**
     * 캐시 유효성 확인
     */
    private boolean isCacheValid(@NotNull String key) {
        Long timestamp = cacheTimestamps.get(key);
        return timestamp != null && (System.currentTimeMillis() - timestamp) < CACHE_DURATION;
    }

    /**
     * 캐시에서 제거
     */
    private void invalidateCache(@NotNull String key) {
        playerCache.remove(key);
        cacheTimestamps.remove(key);
    }

    // ========== PlayerDTO 관련 메소드 ==========

    /**
     * 플레이어 데이터 저장/업데이트
     */
    public CompletableFuture<Boolean> savePlayer(@NotNull PlayerDTO player) {
        if (!isConnected()) return CompletableFuture.completedFuture(false);

        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentReference docRef = firestore.collection(PLAYERS_COLLECTION)
                        .document(player.getUuid());

                player.updateLastSeen();
                docRef.set(player.toMap()).get();

                // 캐시 업데이트
                playerCache.put(player.getUuid(), player);
                cacheTimestamps.put(player.getUuid(), System.currentTimeMillis());

                LogUtil.debug("플레이어 데이터 저장 완료: " + player.getPlayerName());
                return true;
            } catch (Exception e) {
                LogUtil.error("플레이어 데이터 저장 실패: " + player.getPlayerName(), e);
                return false;
            }
        });
    }

    /**
     * 플레이어 데이터 로드
     */
    public CompletableFuture<PlayerDTO> loadPlayer(@NotNull String uuid) {
        if (!isConnected()) return CompletableFuture.completedFuture(null);

        // 캐시 확인
        if (isCacheValid(uuid)) {
            PlayerDTO cached = playerCache.get(uuid);
            if (cached != null) {
                return CompletableFuture.completedFuture(cached);
            }
        }

        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentSnapshot doc = firestore.collection(PLAYERS_COLLECTION)
                        .document(uuid).get().get();

                if (doc.exists()) {
                    PlayerDTO player = PlayerDTO.fromMap(doc.getData());

                    // 캐시에 저장
                    playerCache.put(uuid, player);
                    cacheTimestamps.put(uuid, System.currentTimeMillis());

                    return player;
                }
                return null;
            } catch (Exception e) {
                LogUtil.error("플레이어 데이터 로드 실패: " + uuid, e);
                return null;
            }
        });
    }

    // ========== StatsDTO 관련 메소드 ==========

    /**
     * 스탯 데이터 저장
     */
    public CompletableFuture<Boolean> saveStats(@NotNull String playerUuid, @NotNull StatsDTO stats) {
        if (!isConnected()) return CompletableFuture.completedFuture(false);

        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentReference docRef = firestore.collection(PLAYERS_COLLECTION)
                        .document(playerUuid)
                        .collection(STATS_SUBCOLLECTION)
                        .document("current");

                stats.markUpdated();
                docRef.set(stats.toMap()).get();

                LogUtil.debug("스탯 데이터 저장 완료: " + playerUuid);
                return true;
            } catch (Exception e) {
                LogUtil.error("스탯 데이터 저장 실패: " + playerUuid, e);
                return false;
            }
        });
    }

    /**
     * 스탯 데이터 로드
     */
    public CompletableFuture<StatsDTO> loadStats(@NotNull String playerUuid) {
        if (!isConnected()) return CompletableFuture.completedFuture(null);

        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentSnapshot doc = firestore.collection(PLAYERS_COLLECTION)
                        .document(playerUuid)
                        .collection(STATS_SUBCOLLECTION)
                        .document("current").get().get();

                if (doc.exists()) {
                    return StatsDTO.fromMap(doc.getData());
                }
                return new StatsDTO(); // 기본값 반환
            } catch (Exception e) {
                LogUtil.error("스탯 데이터 로드 실패: " + playerUuid, e);
                return new StatsDTO();
            }
        });
    }

    // ========== TalentDTO 관련 메소드 ==========

    /**
     * 특성 데이터 저장
     */
    public CompletableFuture<Boolean> saveTalents(@NotNull String playerUuid, @NotNull TalentDTO talents) {
        if (!isConnected()) return CompletableFuture.completedFuture(false);

        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentReference docRef = firestore.collection(PLAYERS_COLLECTION)
                        .document(playerUuid)
                        .collection(TALENTS_SUBCOLLECTION)
                        .document("current");

                talents.markUpdated();
                docRef.set(talents.toMap()).get();

                LogUtil.debug("특성 데이터 저장 완료: " + playerUuid);
                return true;
            } catch (Exception e) {
                LogUtil.error("특성 데이터 저장 실패: " + playerUuid, e);
                return false;
            }
        });
    }

    /**
     * 특성 데이터 로드
     */
    public CompletableFuture<TalentDTO> loadTalents(@NotNull String playerUuid) {
        if (!isConnected()) return CompletableFuture.completedFuture(null);

        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentSnapshot doc = firestore.collection(PLAYERS_COLLECTION)
                        .document(playerUuid)
                        .collection(TALENTS_SUBCOLLECTION)
                        .document("current").get().get();

                if (doc.exists()) {
                    return TalentDTO.fromMap(doc.getData());
                }
                return new TalentDTO(); // 기본값 반환
            } catch (Exception e) {
                LogUtil.error("특성 데이터 로드 실패: " + playerUuid, e);
                return new TalentDTO();
            }
        });
    }

    // ========== ProgressDTO 관련 메소드 ==========

    /**
     * 진행도 데이터 저장
     */
    public CompletableFuture<Boolean> saveProgress(@NotNull String playerUuid, @NotNull ProgressDTO progress) {
        if (!isConnected()) return CompletableFuture.completedFuture(false);

        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentReference docRef = firestore.collection(PLAYERS_COLLECTION)
                        .document(playerUuid)
                        .collection(PROGRESS_SUBCOLLECTION)
                        .document("current");

                progress.markUpdated();
                docRef.set(progress.toMap()).get();

                LogUtil.debug("진행도 데이터 저장 완료: " + playerUuid);
                return true;
            } catch (Exception e) {
                LogUtil.error("진행도 데이터 저장 실패: " + playerUuid, e);
                return false;
            }
        });
    }

    /**
     * 진행도 데이터 로드
     */
    public CompletableFuture<ProgressDTO> loadProgress(@NotNull String playerUuid) {
        if (!isConnected()) return CompletableFuture.completedFuture(null);

        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentSnapshot doc = firestore.collection(PLAYERS_COLLECTION)
                        .document(playerUuid)
                        .collection(PROGRESS_SUBCOLLECTION)
                        .document("current").get().get();

                if (doc.exists()) {
                    return ProgressDTO.fromMap(doc.getData());
                }
                return new ProgressDTO(); // 기본값 반환
            } catch (Exception e) {
                LogUtil.error("진행도 데이터 로드 실패: " + playerUuid, e);
                return new ProgressDTO();
            }
        });
    }

    // ========== LeaderboardEntryDTO 관련 메소드 ==========

    /**
     * 순위표 업데이트
     */
    public CompletableFuture<Boolean> updateLeaderboard(@NotNull String type, @NotNull LeaderboardEntryDTO entry) {
        if (!isConnected()) return CompletableFuture.completedFuture(false);

        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentReference docRef = firestore.collection(LEADERBOARDS_COLLECTION)
                        .document(type)
                        .collection("entries")
                        .document(entry.getUuid());

                entry.markUpdated();
                docRef.set(entry.toMap()).get();

                LogUtil.debug("순위표 업데이트 완료: " + type + " - " + entry.getPlayerName());
                return true;
            } catch (Exception e) {
                LogUtil.error("순위표 업데이트 실패: " + type, e);
                return false;
            }
        });
    }

    /**
     * 플레이어 재화 정보 로드
     */
    public CompletableFuture<WalletDTO> loadWallet(@NotNull String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            if (!isConnected()) {
                return new WalletDTO();
            }

            try {
                DocumentReference docRef = getPlayerDocument(uuid);
                DocumentSnapshot document = docRef.get().get();

                if (document.exists()) {
                    Map<String, Object> walletData = (Map<String, Object>) document.get("wallet");
                    if (walletData != null) {
                        WalletDTO dto = new WalletDTO();

                        Map<String, Object> currencies = (Map<String, Object>) walletData.get("currencies");
                        if (currencies != null) {
                            currencies.forEach((key, value) -> {
                                if (value instanceof Number) {
                                    dto.setCurrency(key, ((Number) value).longValue());
                                }
                            });
                        }

                        Object lastUpdated = walletData.get("lastUpdated");
                        if (lastUpdated instanceof Number) {
                            dto.setLastUpdated(((Number) lastUpdated).longValue());
                        }

                        return dto;
                    }
                }

                return new WalletDTO();

            } catch (Exception e) {
                LogUtil.error("Failed to load wallet data for " + uuid, e);
                return new WalletDTO();
            }
        });
    }

    /**
     * 플레이어 재화 정보 저장
     */
    public CompletableFuture<Boolean> saveWallet(@NotNull String uuid, @NotNull WalletDTO wallet) {
        return CompletableFuture.supplyAsync(() -> {
            if (!isConnected()) {
                return false;
            }

            try {
                DocumentReference docRef = getPlayerDocument(uuid);

                Map<String, Object> walletData = new HashMap<>();
                walletData.put("currencies", wallet.getCurrencies());
                walletData.put("lastUpdated", wallet.getLastUpdated());

                docRef.update("wallet", walletData).get();
                return true;

            } catch (Exception e) {
                LogUtil.error("Failed to save wallet data for " + uuid, e);
                return false;
            }
        });
    }

    /**
     * 모든 플레이어 데이터 저장 (Wallet 포함)
     */
    public CompletableFuture<Boolean> saveAllPlayerDataWithWallet(@NotNull String uuid,
                                                                  @NotNull PlayerDTO player,
                                                                  @NotNull StatsDTO stats,
                                                                  @NotNull TalentDTO talents,
                                                                  @NotNull ProgressDTO progress,
                                                                  @NotNull WalletDTO wallet) {
        return CompletableFuture.supplyAsync(() -> {
            if (!isConnected()) {
                return false;
            }

            try {
                DocumentReference docRef = getPlayerDocument(uuid);

                Map<String, Object> data = new HashMap<>();

                // 기본 플레이어 정보
                data.put("name", player.getName());
                data.put("lastLogin", player.getLastLogin());
                data.put("totalPlaytime", player.getTotalPlaytime());
                if (player.getJob() != null) {
                    data.put("job", player.getJob().name());
                }

                // 스탯 정보
                Map<String, Object> statsData = new HashMap<>();
                statsData.put("strength", stats.getStrength());
                statsData.put("intelligence", stats.getIntelligence());
                statsData.put("dexterity", stats.getDexterity());
                statsData.put("vitality", stats.getVitality());
                statsData.put("wisdom", stats.getWisdom());
                statsData.put("luck", stats.getLuck());
                data.put("stats", statsData);

                // 특성 정보
                Map<String, Object> talentsData = new HashMap<>();
                talentsData.put("availablePoints", talents.getAvailablePoints());
                talentsData.put("learnedTalents", talents.getLearnedTalents());
                data.put("talents", talentsData);

                // 진행도 정보
                Map<String, Object> progressData = new HashMap<>();
                progressData.put("currentLevel", progress.getCurrentLevel());
                progressData.put("totalExperience", progress.getTotalExperience());
                progressData.put("levelProgress", progress.getLevelProgress());
                progressData.put("mobsKilled", progress.getMobsKilled());
                progressData.put("playersKilled", progress.getPlayersKilled());
                progressData.put("deaths", progress.getDeaths());
                data.put("progress", progressData);

                // 재화 정보
                Map<String, Object> walletData = new HashMap<>();
                walletData.put("currencies", wallet.getCurrencies());
                walletData.put("lastUpdated", wallet.getLastUpdated());
                data.put("wallet", walletData);

                // 업데이트 시간
                data.put("lastUpdated", FieldValue.serverTimestamp());

                // 저장
                docRef.set(data, SetOptions.merge()).get();
                return true;

            } catch (Exception e) {
                LogUtil.error("Failed to save all player data for " + uuid, e);
                return false;
            }
        });
    }

    /**
     * 플레이어 문서 참조 가져오기
     */
    @NotNull
    private DocumentReference getPlayerDocument(@NotNull String uuid) {
        return firestore
                .collection("servers")
                .document(serverName)
                .collection("players")
                .document(uuid);
    }

    /**
     * 서버 문서 참조 가져오기
     */
    @NotNull
    private DocumentReference getServerDocument() {
        return firestore
                .collection("servers")
                .document(serverName);
    }

    /**
     * 총 플레이어 수 가져오기
     */
    public CompletableFuture<Integer> getTotalPlayerCount() {
        return CompletableFuture.supplyAsync(() -> {
            if (!isConnected()) {
                return 0;
            }

            try {
                CollectionReference playersCollection = firestore
                        .collection("servers")
                        .document(serverName)
                        .collection("players");

                AggregateQuerySnapshot snapshot = playersCollection.count().get().get();
                return (int) snapshot.getCount();

            } catch (Exception e) {
                LogUtil.error("Failed to get total player count", e);
                return 0;
            }
        });
    }

    /**
     * 순위표 가져오기
     */
    public CompletableFuture<List<LeaderboardEntryDTO>> getLeaderboard(@NotNull String type, int limit) {
        return CompletableFuture.supplyAsync(() -> {
            if (!isConnected()) {
                return new ArrayList<>();
            }

            try {
                List<LeaderboardEntryDTO> entries = new ArrayList<>();
                Query query = null;

                CollectionReference playersCollection = firestore
                        .collection("servers")
                        .document(serverName)
                        .collection("players");

                // 타입별 쿼리 생성
                switch (type.toLowerCase()) {
                    case "level":
                        query = playersCollection
                                .orderBy("progress.currentLevel", Query.Direction.DESCENDING)
                                .limit(limit);
                        break;

                    case "wealth":
                        query = playersCollection
                                .orderBy("wallet.totalValue", Query.Direction.DESCENDING)
                                .limit(limit);
                        break;

                    case "kills":
                        query = playersCollection
                                .orderBy("progress.mobsKilled", Query.Direction.DESCENDING)
                                .limit(limit);
                        break;

                    case "pvp":
                        query = playersCollection
                                .orderBy("progress.playersKilled", Query.Direction.DESCENDING)
                                .limit(limit);
                        break;

                    default:
                        LogUtil.warning("Unknown leaderboard type: " + type);
                        return entries;
                }

                QuerySnapshot querySnapshot = query.get().get();
                int rank = 1;

                for (QueryDocumentSnapshot document : querySnapshot) {
                    try {
                        String uuid = document.getId();
                        String name = document.getString("name");
                        if (name == null) name = "Unknown";

                        long value = 0;

                        // 타입별 값 추출
                        switch (type.toLowerCase()) {
                            case "level":
                                Map<String, Object> progressData = (Map<String, Object>) document.get("progress");
                                if (progressData != null) {
                                    Object levelObj = progressData.get("currentLevel");
                                    if (levelObj instanceof Number) {
                                        value = ((Number) levelObj).longValue();
                                    }
                                }
                                break;

                            case "wealth":
                                Map<String, Object> walletData = (Map<String, Object>) document.get("wallet");
                                if (walletData != null) {
                                    // 각 통화의 총 가치 계산
                                    Map<String, Object> currencies = (Map<String, Object>) walletData.get("currencies");
                                    if (currencies != null) {
                                        value = calculateTotalValue(currencies);
                                    }
                                }
                                break;

                            case "kills":
                                progressData = (Map<String, Object>) document.get("progress");
                                if (progressData != null) {
                                    Object killsObj = progressData.get("mobsKilled");
                                    if (killsObj instanceof Number) {
                                        value = ((Number) killsObj).longValue();
                                    }
                                }
                                break;

                            case "pvp":
                                progressData = (Map<String, Object>) document.get("progress");
                                if (progressData != null) {
                                    Object pvpObj = progressData.get("playersKilled");
                                    if (pvpObj instanceof Number) {
                                        value = ((Number) pvpObj).longValue();
                                    }
                                }
                                break;
                        }

                        LeaderboardEntryDTO entry = new LeaderboardEntryDTO(uuid, name, rank, value, type);
                        entries.add(entry);
                        rank++;

                    } catch (Exception e) {
                        LogUtil.error("Failed to parse leaderboard entry", e);
                    }
                }

                return entries;

            } catch (Exception e) {
                LogUtil.error("Failed to get leaderboard: " + type, e);
                return new ArrayList<>();
            }
        });
    }

    /**
     * 총 재산 가치 계산 (골드 기준)
     */
    private long calculateTotalValue(@NotNull Map<String, Object> currencies) {
        long total = 0;

        // 각 통화별 환율 적용
        for (Map.Entry<String, Object> entry : currencies.entrySet()) {
            if (entry.getValue() instanceof Number) {
                long amount = ((Number) entry.getValue()).longValue();

                switch (entry.getKey()) {
                    case "gold":
                        total += amount;
                        break;
                    case "diamond":
                        total += amount * 100;
                        break;
                    case "emerald":
                        total += amount * 50;
                        break;
                    case "ghast_tear":
                        total += amount * 1000;
                        break;
                    case "nether_star":
                        total += amount * 10000;
                        break;
                }
            }
        }

        return total;
    }

    /**
     * 순위표 조회 (상위 N명)
     */
    public CompletableFuture<List<LeaderboardEntryDTO>> getTopPlayers(@NotNull String type, int limit) {
        if (!isConnected()) return CompletableFuture.completedFuture(new ArrayList<>());

        return CompletableFuture.supplyAsync(() -> {
            try {
                QuerySnapshot querySnapshot = firestore.collection(LEADERBOARDS_COLLECTION)
                        .document(type)
                        .collection("entries")
                        .orderBy("score", Query.Direction.DESCENDING)
                        .limit(limit)
                        .get().get();

                List<LeaderboardEntryDTO> entries = new ArrayList<>();
                int rank = 1;

                for (QueryDocumentSnapshot doc : querySnapshot) {
                    LeaderboardEntryDTO entry = LeaderboardEntryDTO.fromMap(doc.getData());
                    entry.setRank(rank++);
                    entries.add(entry);
                }

                return entries;
            } catch (Exception e) {
                LogUtil.error("순위표 조회 실패: " + type, e);
                return new ArrayList<>();
            }
        });
    }

    /**
     * 전체 플레이어 데이터 저장 (일괄)
     */
    public CompletableFuture<Boolean> saveAllPlayerData(@NotNull String playerUuid,
                                                        @NotNull PlayerDTO player,
                                                        @NotNull StatsDTO stats,
                                                        @NotNull TalentDTO talents,
                                                        @NotNull ProgressDTO progress) {
        if (!isConnected()) return CompletableFuture.completedFuture(false);

        return CompletableFuture.allOf(
                        savePlayer(player),
                        saveStats(playerUuid, stats),
                        saveTalents(playerUuid, talents),
                        saveProgress(playerUuid, progress)
                ).thenApply(v -> true)
                .exceptionally(e -> {
                    LogUtil.error("전체 플레이어 데이터 저장 실패: " + playerUuid, e);
                    return false;
                });
    }

    /**
     * 캐시 정리
     */
    public void clearCache() {
        playerCache.clear();
        cacheTimestamps.clear();
        LogUtil.info("Firebase 캐시가 정리되었습니다.");
    }

    /**
     * 캐시 통계
     */
    public Map<String, Object> getCacheStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("playerCacheSize", playerCache.size());
        stats.put("cacheTimestampsSize", cacheTimestamps.size());
        return stats;
    }
}