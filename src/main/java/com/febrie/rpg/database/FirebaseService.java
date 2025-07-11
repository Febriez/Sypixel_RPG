package com.febrie.rpg.database;

import com.febrie.rpg.dto.*;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.*;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * Firebase Firestore 데이터베이스 서비스
 * 모든 데이터베이스 작업을 담당
 *
 * @author Febrie, CoffeeTory
 */
public class FirebaseService {

    private final Plugin plugin;
    private Firestore firestore;

    // 컬렉션 이름들
    private static final String PLAYERS_COLLECTION = "players";
    private static final String LEADERBOARDS_COLLECTION = "leaderboards";
    private static final String SERVER_STATS_COLLECTION = "server_stats";

    // 서브컬렉션 이름들
    private static final String STATS_SUBCOLLECTION = "stats";
    private static final String TALENTS_SUBCOLLECTION = "talents";
    private static final String PROGRESS_SUBCOLLECTION = "progress";

    public FirebaseService(@NotNull Plugin plugin) {
        this.plugin = plugin;
        initializeFirebase();
    }

    /**
     * Firebase 초기화
     */
    private void initializeFirebase() {
        try {
            // Firebase 설정 파일 경로
            FileInputStream serviceAccount = new FileInputStream(
                    plugin.getDataFolder().getPath() + "/firebase-credentials.json"
            );

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }

            firestore = FirestoreClient.getFirestore();
            plugin.getLogger().info("Firebase 초기화 완료!");

        } catch (IOException e) {
            plugin.getLogger().severe("Firebase 초기화 실패: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // ========== 플레이어 데이터 작업 ==========

    /**
     * 플레이어 데이터 저장 (전체)
     */
    public CompletableFuture<Void> savePlayerData(@NotNull String uuid, @NotNull PlayerDTO playerDTO,
                                                  @NotNull StatsDTO statsDTO, @NotNull TalentDTO talentDTO,
                                                  @NotNull ProgressDTO progressDTO) {
        return CompletableFuture.runAsync(() -> {
            try {
                WriteBatch batch = firestore.batch();

                // 플레이어 기본 정보
                DocumentReference playerRef = firestore.collection(PLAYERS_COLLECTION).document(uuid);
                batch.set(playerRef, playerDTO);

                // 스탯 정보
                DocumentReference statsRef = playerRef.collection(STATS_SUBCOLLECTION).document("current");
                batch.set(statsRef, statsDTO);

                // 특성 정보
                DocumentReference talentsRef = playerRef.collection(TALENTS_SUBCOLLECTION).document("current");
                batch.set(talentsRef, talentDTO);

                // 진행도 정보
                DocumentReference progressRef = playerRef.collection(PROGRESS_SUBCOLLECTION).document("current");
                batch.set(progressRef, progressDTO);

                // 배치 실행
                batch.commit().get();

                plugin.getLogger().info("플레이어 데이터 저장 완료: " + uuid);

            } catch (InterruptedException | ExecutionException e) {
                plugin.getLogger().severe("플레이어 데이터 저장 실패: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * 플레이어 기본 정보 로드
     */
    @Nullable
    public CompletableFuture<PlayerDTO> loadPlayerData(@NotNull String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentSnapshot document = firestore.collection(PLAYERS_COLLECTION)
                        .document(uuid)
                        .get()
                        .get();

                if (document.exists()) {
                    return document.toObject(PlayerDTO.class);
                }

            } catch (InterruptedException | ExecutionException e) {
                plugin.getLogger().severe("플레이어 데이터 로드 실패: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * 플레이어 스탯 정보 로드
     */
    @Nullable
    public CompletableFuture<StatsDTO> loadPlayerStats(@NotNull String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentSnapshot document = firestore.collection(PLAYERS_COLLECTION)
                        .document(uuid)
                        .collection(STATS_SUBCOLLECTION)
                        .document("current")
                        .get()
                        .get();

                if (document.exists()) {
                    return document.toObject(StatsDTO.class);
                }

            } catch (InterruptedException | ExecutionException e) {
                plugin.getLogger().severe("플레이어 스탯 로드 실패: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * 플레이어 특성 정보 로드
     */
    @Nullable
    public CompletableFuture<TalentDTO> loadPlayerTalents(@NotNull String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentSnapshot document = firestore.collection(PLAYERS_COLLECTION)
                        .document(uuid)
                        .collection(TALENTS_SUBCOLLECTION)
                        .document("current")
                        .get()
                        .get();

                if (document.exists()) {
                    return document.toObject(TalentDTO.class);
                }

            } catch (InterruptedException | ExecutionException e) {
                plugin.getLogger().severe("플레이어 특성 로드 실패: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * 플레이어 진행도 정보 로드
     */
    @Nullable
    public CompletableFuture<ProgressDTO> loadPlayerProgress(@NotNull String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                DocumentSnapshot document = firestore.collection(PLAYERS_COLLECTION)
                        .document(uuid)
                        .collection(PROGRESS_SUBCOLLECTION)
                        .document("current")
                        .get()
                        .get();

                if (document.exists()) {
                    return document.toObject(ProgressDTO.class);
                }

            } catch (InterruptedException | ExecutionException e) {
                plugin.getLogger().severe("플레이어 진행도 로드 실패: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        });
    }

    /**
     * 플레이어 전체 데이터 로드
     */
    public CompletableFuture<PlayerDataBundle> loadAllPlayerData(@NotNull String uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                // 병렬로 모든 데이터 로드
                CompletableFuture<PlayerDTO> playerFuture = loadPlayerData(uuid);
                CompletableFuture<StatsDTO> statsFuture = loadPlayerStats(uuid);
                CompletableFuture<TalentDTO> talentsFuture = loadPlayerTalents(uuid);
                CompletableFuture<ProgressDTO> progressFuture = loadPlayerProgress(uuid);

                // 모든 Future 완료 대기
                CompletableFuture.allOf(playerFuture, statsFuture, talentsFuture, progressFuture).join();

                return new PlayerDataBundle(
                        playerFuture.get(),
                        statsFuture.get(),
                        talentsFuture.get(),
                        progressFuture.get()
                );

            } catch (InterruptedException | ExecutionException e) {
                plugin.getLogger().severe("플레이어 전체 데이터 로드 실패: " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        });
    }

    // ========== 순위표 작업 ==========

    /**
     * 순위표 업데이트
     */
    public CompletableFuture<Void> updateLeaderboard(@NotNull String type, @NotNull LeaderboardEntryDTO entry) {
        return CompletableFuture.runAsync(() -> {
            try {
                firestore.collection(LEADERBOARDS_COLLECTION)
                        .document(type)
                        .collection("entries")
                        .document(entry.getUuid())
                        .set(entry)
                        .get();

            } catch (InterruptedException | ExecutionException e) {
                plugin.getLogger().severe("순위표 업데이트 실패: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * 순위표 조회 (상위 N명)
     */
    public CompletableFuture<List<LeaderboardEntryDTO>> getTopPlayers(@NotNull String type, int limit) {
        return CompletableFuture.supplyAsync(() -> {
            List<LeaderboardEntryDTO> entries = new ArrayList<>();

            try {
                QuerySnapshot querySnapshot = firestore.collection(LEADERBOARDS_COLLECTION)
                        .document(type)
                        .collection("entries")
                        .orderBy("score", Query.Direction.DESCENDING)
                        .limit(limit)
                        .get()
                        .get();

                int rank = 1;
                for (QueryDocumentSnapshot document : querySnapshot) {
                    LeaderboardEntryDTO entry = document.toObject(LeaderboardEntryDTO.class);
                    entry.setRank(rank++);
                    entries.add(entry);
                }

            } catch (InterruptedException | ExecutionException e) {
                plugin.getLogger().severe("순위표 조회 실패: " + e.getMessage());
                e.printStackTrace();
            }

            return entries;
        });
    }

    // ========== 서버 통계 작업 ==========

    /**
     * 서버 통계 업데이트
     */
    public CompletableFuture<Void> updateServerStats(@NotNull Map<String, Object> stats) {
        return CompletableFuture.runAsync(() -> {
            try {
                firestore.collection(SERVER_STATS_COLLECTION)
                        .document("global")
                        .update(stats)
                        .get();

            } catch (InterruptedException | ExecutionException e) {
                plugin.getLogger().severe("서버 통계 업데이트 실패: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    /**
     * 정리 작업
     */
    public void shutdown() {
        if (firestore != null) {
            try {
                firestore.close();
                plugin.getLogger().info("Firebase 연결 종료");
            } catch (Exception e) {
                plugin.getLogger().severe("Firebase 종료 실패: " + e.getMessage());
            }
        }
    }

    /**
     * 플레이어 데이터 번들 (한번에 모든 데이터를 담는 클래스)
     */
    public static class PlayerDataBundle {
        public final PlayerDTO player;
        public final StatsDTO stats;
        public final TalentDTO talents;
        public final ProgressDTO progress;

        public PlayerDataBundle(@Nullable PlayerDTO player, @Nullable StatsDTO stats,
                                @Nullable TalentDTO talents, @Nullable ProgressDTO progress) {
            this.player = player;
            this.stats = stats;
            this.talents = talents;
            this.progress = progress;
        }

        public boolean isComplete() {
            return player != null && stats != null && talents != null && progress != null;
        }
    }
}