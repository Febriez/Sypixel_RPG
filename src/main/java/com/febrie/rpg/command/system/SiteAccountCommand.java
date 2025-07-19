package com.febrie.rpg.command.system;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.FirestoreManager;
import com.febrie.rpg.dto.player.PlayerDTO;
import com.febrie.rpg.dto.player.PlayerDataDTO;
import com.febrie.rpg.database.service.impl.PlayerFirestoreService;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.LogUtil;
import com.google.auth.oauth2.AccessToken;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Pattern;

/**
 * 사이트 계정 발급 명령어
 * 마인크래프트 플레이어가 웹사이트 계정을 발급받을 수 있도록 하는 명령어
 *
 * @author CoffeeTory
 */
public class SiteAccountCommand implements CommandExecutor {

    private final RPGMain plugin;

    // 이메일 유효성 검사 패턴
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    // Firebase REST API 엔드포인트
    private static final String FIREBASE_AUTH_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=";
    private static final String FIREBASE_SET_CUSTOM_CLAIMS_URL = "https://identitytoolkit.googleapis.com/v1/accounts:setAccountInfo?key=";
    private static final String FIREBASE_USER_LOOKUP_URL = "https://identitytoolkit.googleapis.com/v1/accounts:lookup?key=";

    // HTTP 클라이언트
    private final HttpClient httpClient;
    private final Gson gson;

    // 비밀번호 생성용
    private static final String PASSWORD_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*";
    private static final SecureRandom random = new SecureRandom();

    // 환경 변수 키
    private static final String ENV_WEB_API_KEY = "FIREBASE_WEB_API_KEY";

    // 관리자 UUID (추후 설정 파일로 이동 필요)
    private static final String[] ADMIN_UUIDS = {
            // 관리자 UUID를 여기에 추가
    };

    public SiteAccountCommand(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("이 명령어는 플레이어만 사용할 수 있습니다.", ColorUtil.ERROR));
            return true;
        }

        if (args.length != 1) {
            sendUsage(player);
            return true;
        }

        String email = args[0];

        // 이메일 유효성 검사
        if (!isValidEmail(email)) {
            player.sendMessage(Component.text("올바른 이메일 형식이 아닙니다.", ColorUtil.ERROR));
            sendUsage(player);
            return true;
        }

        // 계정 발급 처리
        processAccountCreation(player, email);

        return true;
    }

    /**
     * 사용법 안내
     */
    private void sendUsage(@NotNull Player player) {
        player.sendMessage(Component.text("사용법: /사이트계정발급 <이메일>", ColorUtil.YELLOW));
        player.sendMessage(Component.text("예시: /사이트계정발급 player@example.com", ColorUtil.GRAY));
    }

    /**
     * 이메일 유효성 검사
     */
    private boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }

    /**
     * 계정 생성 처리
     */
    private void processAccountCreation(@NotNull Player player, @NotNull String email) {
        String uuid = player.getUniqueId().toString();
        String playerName = player.getName();
        boolean isAdmin = isAdminPlayer(uuid);

        // 로딩 메시지 표시
        player.sendMessage(Component.text("계정을 생성하는 중...", ColorUtil.YELLOW));

        // 비동기로 계정 생성 처리
        CompletableFuture.runAsync(() -> {
            try {
                // 환경 변수 확인
                String apiKey = System.getenv(ENV_WEB_API_KEY);
                if (apiKey == null || apiKey.isEmpty()) {
                    throw new IllegalStateException("Firebase Web API Key가 설정되지 않았습니다.");
                }

                // 1. Player 컬렉션에서 중복 계정 확인 (isAdmin 필드 존재 여부)
                PlayerFirestoreService playerService = plugin.getPlayerFirestoreService();
                if (playerService != null) {
                    PlayerDataDTO existingPlayer = playerService.getByUuid(UUID.fromString(uuid)).join();
                    if (existingPlayer != null) {
                        // 이미 계정이 있으면 중복 발급 불가
                        plugin.getServer().getScheduler().runTask(plugin, () ->
                                sendErrorMessage(player, "이미 사이트 계정이 발급되었습니다."));
                        return;
                    }
                }

                // 2. 랜덤 비밀번호 생성
                String password = generateRandomPassword();

                // 3. Firebase Auth 계정 생성
                String uid = createFirebaseAccount(email, password, apiKey);

                // 4. Custom Claims 설정 (isAdmin)
                if (isAdmin) {
                    setCustomClaims(uid, Map.of("isAdmin", true), apiKey);
                }

                // 5. Player 컬렉션의 isAdmin 필드 업데이트
                updatePlayerAdminStatus(uuid, playerName, isAdmin);

                // 성공 메시지 전송
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        sendSuccessMessage(player, email, password));

            } catch (Exception e) {
                LogUtil.error("사이트 계정 생성 중 오류 발생", e);
                plugin.getServer().getScheduler().runTask(plugin, () ->
                        sendErrorMessage(player, "계정 생성 중 오류가 발생했습니다: " + e.getMessage()));
            }
        });
    }

    /**
     * 랜덤 비밀번호 생성 (12자리)
     */
    private String generateRandomPassword() {
        StringBuilder password = new StringBuilder(12);
        for (int i = 0; i < 12; i++) {
            password.append(PASSWORD_CHARS.charAt(random.nextInt(PASSWORD_CHARS.length())));
        }
        return password.toString();
    }

    /**
     * 플레이어가 관리자인지 확인
     */
    private boolean isAdminPlayer(String uuid) {
        for (String adminUuid : ADMIN_UUIDS) {
            if (adminUuid.equals(uuid)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 이메일 중복 확인
     */
    private boolean checkEmailExists(String email, String apiKey) throws Exception {
        // Firebase Auth는 직접적인 이메일 조회를 지원하지 않으므로
        // 실제로는 Firestore에서 확인하거나 signIn 시도로 확인해야 함
        // 여기서는 간단히 false 반환 (추후 구현 필요)
        return false;
    }

    /**
     * Firebase Auth 계정 생성
     */
    private String createFirebaseAccount(String email, String password, String apiKey) throws Exception {
        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("email", email);
        requestBody.addProperty("password", password);
        requestBody.addProperty("returnSecureToken", true);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(FIREBASE_AUTH_URL + apiKey))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            JsonObject error = JsonParser.parseString(response.body()).getAsJsonObject();
            String errorMessage = error.getAsJsonObject("error").get("message").getAsString();
            throw new RuntimeException("Firebase Auth 오류: " + errorMessage);
        }

        JsonObject result = JsonParser.parseString(response.body()).getAsJsonObject();
        return result.get("localId").getAsString();
    }

    /**
     * Custom Claims 설정
     */
    private void setCustomClaims(String uid, Map<String, Object> claims, String apiKey) throws Exception {
        // Admin SDK 토큰 필요 - Service Account로 생성
        String accessToken = getAdminAccessToken();

        JsonObject requestBody = new JsonObject();
        requestBody.addProperty("localId", uid);
        requestBody.add("customAttributes", gson.toJsonTree(claims));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(FIREBASE_SET_CUSTOM_CLAIMS_URL + apiKey))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + accessToken)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
                .timeout(Duration.ofSeconds(10))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
        }
    }

    /**
     * Admin 액세스 토큰 가져오기
     */
    private String getAdminAccessToken() throws IOException {
        try {
            // FirestoreManager의 공용 메서드 사용
            GoogleCredentials credentials = FirestoreManager.getCredentials();
            if (credentials == null) {
                throw new IllegalStateException("Service Account 인증 정보를 가져올 수 없습니다.");
            }

            // Cloud Platform 스코프 추가
            credentials = credentials.createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));

            credentials.refreshIfExpired();
            AccessToken token = credentials.getAccessToken();
            return token != null ? token.getTokenValue() : null;
        } catch (Exception e) {
            throw new IOException("Service Account 인증 실패", e);
        }
    }

    /**
     * Player 컬렉션의 isAdmin 필드 업데이트
     */
    private void updatePlayerAdminStatus(String uuid, String playerName, boolean isAdmin) {
        try {
            PlayerFirestoreService playerService = plugin.getPlayerFirestoreService();
            if (playerService == null) {
                LogUtil.error("PlayerFirestoreService를 가져올 수 없습니다.");
                return;
            }
            
            // 기존 플레이어 데이터 가져오기
            PlayerDataDTO existingPlayer = playerService.getByUuid(UUID.fromString(uuid)).join();
            PlayerDataDTO updatedPlayer;
            
            if (existingPlayer != null) {
                // 기존 데이터 유지하면서 Firestore에 별도 isAdmin 플래그 저장이 필요함
                // 현재 PlayerDataDTO 구조로는 isAdmin을 저장할 수 없으므로
                // 별도의 users 컬렉션이나 플래그가 필요함
                LogUtil.info("플레이어 데이터가 이미 존재합니다: " + playerName);
            } else {
                // 새 플레이어 데이터 생성
                updatedPlayer = PlayerDataDTO.createNew(UUID.fromString(uuid), playerName);
                playerService.save(uuid, updatedPlayer).whenComplete((success, error) -> {
                    if (error != null) {
                        LogUtil.error("플레이어 데이터 저장 실패", error);
                    } else {
                        LogUtil.info("플레이어 데이터가 생성되었습니다: " + playerName);
                    }
                });
            }
                
        } catch (Exception e) {
            LogUtil.error("플레이어 데이터 업데이트 실패", e);
        }
    }

    /**
     * 성공 메시지 전송
     */
    private void sendSuccessMessage(@NotNull Player player, @NotNull String email, @NotNull String password) {
        player.sendMessage(Component.text("==== 사이트 계정 발급 완료 ====", ColorUtil.SUCCESS));
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("✅ 계정이 성공적으로 생성되었습니다!", ColorUtil.SUCCESS));
        player.sendMessage(Component.text(""));

        // 이메일 정보
        player.sendMessage(Component.text("📧 이메일: ", ColorUtil.GRAY)
                .append(Component.text(email, ColorUtil.WHITE)));

        // 비밀번호 (클릭 가능)
        Component passwordComponent = Component.text("🔑 비밀번호: ", ColorUtil.GRAY)
                .append(Component.text(password, ColorUtil.GOLD)
                        .decoration(TextDecoration.BOLD, true)
                        .clickEvent(ClickEvent.copyToClipboard(password))
                        .hoverEvent(HoverEvent.showText(Component.text("클릭하여 비밀번호 복사", ColorUtil.YELLOW)))
                );

        player.sendMessage(passwordComponent);
        player.sendMessage(Component.text(""));

        // 안내 메시지
        player.sendMessage(Component.text("💡 안내사항:", ColorUtil.YELLOW));
        player.sendMessage(Component.text("- 비밀번호를 클릭하면 클립보드에 복사됩니다", ColorUtil.GRAY));
        player.sendMessage(Component.text("- 웹사이트에서 이메일과 비밀번호로 로그인하세요", ColorUtil.GRAY));
        player.sendMessage(Component.text("- 로그인 후 비밀번호를 변경하는 것을 권장합니다", ColorUtil.GRAY));
        player.sendMessage(Component.text(""));

        // 웹사이트 링크
        Component websiteLink = Component.text("🌐 웹사이트: ", ColorUtil.GRAY)
                .append(Component.text("https://sypixel.com", ColorUtil.AQUA)
                        .decoration(TextDecoration.UNDERLINED, true)
                        .clickEvent(ClickEvent.openUrl("https://sypixel.com"))
                        .hoverEvent(HoverEvent.showText(Component.text("클릭하여 웹사이트 열기", ColorUtil.YELLOW)))
                );

        player.sendMessage(websiteLink);
        player.sendMessage(Component.text("=============================", ColorUtil.SUCCESS));
    }

    /**
     * 오류 메시지 전송
     */
    private void sendErrorMessage(@NotNull Player player, @NotNull String message) {
        player.sendMessage(Component.text("==== 사이트 계정 발급 실패 ====", ColorUtil.ERROR));
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("❌ " + message, ColorUtil.ERROR));
        player.sendMessage(Component.text(""));

        // 도움말 정보
        player.sendMessage(Component.text("💡 도움말:", ColorUtil.YELLOW));
        player.sendMessage(Component.text("- 이미 계정이 있는 경우 웹사이트에서 로그인하세요", ColorUtil.GRAY));
        player.sendMessage(Component.text("- 문제가 지속되면 관리자에게 문의하세요", ColorUtil.GRAY));
        player.sendMessage(Component.text(""));
        player.sendMessage(Component.text("=============================", ColorUtil.ERROR));
    }
}