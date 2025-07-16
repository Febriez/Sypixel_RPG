package com.febrie.rpg.command;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.database.FirestoreRestService;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.LogUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
    private final FirestoreRestService firestoreService;
    
    // 이메일 유효성 검사 패턴
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$"
    );

    public SiteAccountCommand(@NotNull RPGMain plugin, @NotNull FirestoreRestService firestoreService) {
        this.plugin = plugin;
        this.firestoreService = firestoreService;
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
        
        // 로딩 메시지 표시
        player.sendMessage(Component.text("계정을 생성하는 중...", ColorUtil.YELLOW));
        
        // 디버깅: 사용자 컬렉션 확인
        firestoreService.debugCheckUsersCollection();
        
        // 비동기로 계정 생성 처리
        CompletableFuture<FirestoreRestService.SiteAccountResult> future = 
                firestoreService.createSiteAccount(uuid, email);
        
        future.thenAccept(result -> {
            // 메인 스레드에서 메시지 전송
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                if (result.isSuccess()) {
                    sendSuccessMessage(player, email, result.getPassword());
                } else {
                    sendErrorMessage(player, result.getMessage());
                }
            });
        }).exceptionally(throwable -> {
            // 오류 발생 시 메인 스레드에서 오류 메시지 전송
            plugin.getServer().getScheduler().runTask(plugin, () -> {
                LogUtil.error("사이트 계정 생성 중 오류 발생", throwable);
                sendErrorMessage(player, "계정 생성 중 오류가 발생했습니다. 관리자에게 문의해주세요.");
            });
            return null;
        });
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