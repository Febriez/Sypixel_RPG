package com.febrie.rpg.database;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.service.impl.*;
import com.febrie.rpg.util.LogUtil;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Firestore 서비스 통합 관리자
 * Firebase 초기화 및 모든 서비스 인스턴스 관리
 *
 * @author Febrie, CoffeeTory
 */
public class FirestoreManager {
    
    private final RPGMain plugin;
    private FirebaseApp firebaseApp;
    private Firestore firestore;
    
    // 서비스 인스턴스들
    private PlayerFirestoreService playerService;
    private QuestFirestoreService questService;
    private IslandFirestoreService islandService;
    private PlayerIslandFirestoreService playerIslandService;
    private SocialFirestoreService socialService;
    private SystemFirestoreService systemService;
    
    private boolean initialized = false;
    
    public FirestoreManager(@NotNull RPGMain plugin) {
        this.plugin = plugin;
    }
    
    /**
     * Firebase 초기화
     */
    public boolean initialize() {
        try {
            String privateKey = System.getenv("FIREBASE_PRIVATE_KEY");
            if (privateKey == null || privateKey.isEmpty()) {
                LogUtil.warning("FIREBASE_PRIVATE_KEY 환경 변수가 설정되지 않았습니다!");
                return false;
            }
            
            // 서비스 계정 JSON 생성
            String serviceAccountJson = String.format("""
                {
                  "type": "service_account",
                  "project_id": "sypixel-rpg",
                  "private_key_id": "key-id",
                  "private_key": "%s",
                  "client_email": "firebase-adminsdk-3u9i8@sypixel-rpg.iam.gserviceaccount.com",
                  "client_id": "117901822812345439607",
                  "auth_uri": "https://accounts.google.com/o/oauth2/auth",
                  "token_uri": "https://oauth2.googleapis.com/token",
                  "auth_provider_x509_cert_url": "https://www.googleapis.com/oauth2/v1/certs",
                  "client_x509_cert_url": "https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-3u9i8%%40sypixel-rpg.iam.gserviceaccount.com"
                }
                """, privateKey.replace("\n", "\\n"));
            
            GoogleCredentials credentials = GoogleCredentials.fromStream(
                    new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8))
            );
            
            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(credentials)
                    .setProjectId("sypixel-rpg")
                    .build();
            
            // 기존 앱이 있으면 삭제
            try {
                FirebaseApp.getInstance();
                FirebaseApp.getInstance().delete();
            } catch (IllegalStateException ignored) {
                // 앱이 없으면 무시
            }
            
            firebaseApp = FirebaseApp.initializeApp(options);
            firestore = FirestoreClient.getFirestore(firebaseApp);
            
            // 서비스 초기화
            initializeServices();
            
            initialized = true;
            LogUtil.info("Firestore가 성공적으로 초기화되었습니다.");
            return true;
            
        } catch (IOException e) {
            LogUtil.warning("Firestore 초기화 실패: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * 모든 서비스 초기화
     */
    private void initializeServices() {
        playerService = new PlayerFirestoreService(plugin, firestore);
        questService = new QuestFirestoreService(plugin, firestore);
        islandService = new IslandFirestoreService(plugin, firestore);
        playerIslandService = new PlayerIslandFirestoreService(plugin, firestore);
        socialService = new SocialFirestoreService(plugin, firestore);
        systemService = new SystemFirestoreService(plugin, firestore);
    }
    
    /**
     * 종료 처리
     */
    public void shutdown() {
        if (firebaseApp != null) {
            firebaseApp.delete();
            LogUtil.info("Firestore 연결이 종료되었습니다.");
        }
        initialized = false;
    }
    
    /**
     * 초기화 여부 확인
     */
    public boolean isInitialized() {
        return initialized;
    }
    
    // 서비스 게터들
    @NotNull
    public PlayerFirestoreService getPlayerService() {
        if (!initialized) {
            throw new IllegalStateException("Firestore가 초기화되지 않았습니다!");
        }
        return playerService;
    }
    
    @NotNull
    public QuestFirestoreService getQuestService() {
        if (!initialized) {
            throw new IllegalStateException("Firestore가 초기화되지 않았습니다!");
        }
        return questService;
    }
    
    @NotNull
    public IslandFirestoreService getIslandService() {
        if (!initialized) {
            throw new IllegalStateException("Firestore가 초기화되지 않았습니다!");
        }
        return islandService;
    }
    
    @NotNull
    public PlayerIslandFirestoreService getPlayerIslandService() {
        if (!initialized) {
            throw new IllegalStateException("Firestore가 초기화되지 않았습니다!");
        }
        return playerIslandService;
    }
    
    @NotNull
    public SocialFirestoreService getSocialService() {
        if (!initialized) {
            throw new IllegalStateException("Firestore가 초기화되지 않았습니다!");
        }
        return socialService;
    }
    
    @NotNull
    public SystemFirestoreService getSystemService() {
        if (!initialized) {
            throw new IllegalStateException("Firestore가 초기화되지 않았습니다!");
        }
        return systemService;
    }
    
    /**
     * Firestore 직접 접근 (특수한 경우에만 사용)
     */
    @Nullable
    public Firestore getFirestore() {
        return firestore;
    }
}