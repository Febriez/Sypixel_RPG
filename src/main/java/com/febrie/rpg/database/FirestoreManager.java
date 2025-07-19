package com.febrie.rpg.database;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.util.LogUtil;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Firestore 초기화 및 관리자
 * Google Cloud Firestore 직접 연결
 *
 * @author Febrie, CoffeeTory
 */
public class FirestoreManager {

    private final RPGMain plugin;
    private Firestore firestore;
    private boolean initialized = false;

    // 환경 변수 키
    private static final String ENV_SERVICE_ACCOUNT_BASE64 = "FIREBASE_PRIVATE_KEY";  // Base64 인코딩된 JSON

    public FirestoreManager(@NotNull RPGMain plugin) {
        this.plugin = plugin;
    }

    /**
     * Firestore 초기화
     *
     * @return 초기화 성공 여부
     */
    public boolean initialize() {
        if (initialized) {
            LogUtil.warning("Firestore가 이미 초기화되어 있습니다.");
            return true;
        }

        try {
            // 환경 변수에서 인증 정보 가져오기
            GoogleCredentials credentials = getCredentials();
            if (credentials == null) {
                LogUtil.error("Firestore 인증 정보를 찾을 수 없습니다.");
                return false;
            }

            // Firestore 옵션 설정
            FirestoreOptions options = FirestoreOptions.newBuilder()
                    .setCredentials(credentials)
                    .build();

            // Firestore 인스턴스 생성
            firestore = options.getService();

            initialized = true;
            LogUtil.info("Firestore가 성공적으로 초기화되었습니다.");

            return true;

        } catch (Exception e) {
            LogUtil.error("Firestore 초기화 실패", e);
            return false;
        }
    }

    /**
     * 인증 정보 가져오기 (공용 메서드로 변경)
     */
    @Nullable
    public static GoogleCredentials getCredentials() {
        // 환경 변수에서 private key 직접 가져오기
        String privateKey = System.getenv(ENV_SERVICE_ACCOUNT_BASE64);

        if (privateKey == null || privateKey.isEmpty()) {
            LogUtil.error("환경 변수 FIREBASE_PRIVATE_KEY가 설정되지 않았습니다.");
            return null;
        }

        try {
            String serviceAccountJson = "{\n" +
                    "  \"type\": \"service_account\",\n" +
                    "  \"project_id\": \"sypixel-rpg\",\n" +
                    "  \"private_key_id\": \"3f270e6d73f3042d7d3edb4c447ec8c64dfb2a8d\",\n" +
                    "  \"private_key\": \"" + privateKey.replace("\n", "\\n") + "\",\n" +
                    "  \"client_email\": \"firebase-adminsdk-fbsvc@sypixel-rpg.iam.gserviceaccount.com\",\n" +
                    "  \"client_id\": \"102998351283687511462\",\n" +
                    "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                    "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                    "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                    "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40sypixel-rpg.iam.gserviceaccount.com\"\n" +
                    "}";

            // JSON으로부터 인증 정보 생성
            return GoogleCredentials.fromStream(
                    new ByteArrayInputStream(serviceAccountJson.getBytes(StandardCharsets.UTF_8))
            );
        } catch (Exception e) {
            LogUtil.error("Service Account 인증 정보 생성 실패", e);
            return null;
        }
    }

    /**
     * Firestore 인스턴스 가져오기
     */
    @Nullable
    public Firestore getFirestore() {
        if (!initialized || firestore == null) {
            LogUtil.error("Firestore가 초기화되지 않았습니다.");
            return null;
        }
        return firestore;
    }

    /**
     * 초기화 여부 확인
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * Firestore 종료
     */
    public void shutdown() {
        if (firestore != null) {
            try {
                firestore.close();
                LogUtil.info("Firestore가 정상적으로 종료되었습니다.");
            } catch (Exception e) {
                LogUtil.error("Firestore 종료 중 오류 발생", e);
            }
        }
        initialized = false;
        firestore = null;
    }
}