package com.febrie.rpg.command;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.job.JobType;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RPG 시스템 관리자 명령어
 * 경험치 지급, 레벨 설정, 직업 변경 등의 기능 제공
 *
 * @author Febrie, CoffeeTory
 */
public class AdminCommands extends BaseCommand {

    private final RPGMain rpgMain;
    private boolean debugMode = false;

    public AdminCommands(@NotNull Plugin plugin, @NotNull LangManager langManager) {
        super(plugin, langManager);
        this.rpgMain = (RPGMain) plugin;
    }

    @Override
    protected boolean executeCommand(@NotNull CommandSender sender, @NotNull Command command,
                                     @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("sypixelrpg.admin")) {
            sender.sendMessage(Component.text("권한이 없습니다.", NamedTextColor.RED));
            return true;
        }

        // 통계 명령어
        if (args.length > 0 && args[0].equalsIgnoreCase("stats")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players!");
                return true;
            }
            showStats(player);
            return true;
        }

        // 리로드 명령어
        if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players!");
                return true;
            }
            reloadPlugin(player);
            return true;
        }

        // 디버그 모드 토글
        if (args.length > 0 && args[0].equalsIgnoreCase("debug")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players!");
                return true;
            }
            toggleDebug(player);
            return true;
        }

        // 경험치 지급
        if (args.length >= 3 && args[0].equalsIgnoreCase("exp")) {
            return handleExpCommand(sender, args);
        }

        // 레벨 설정
        if (args.length >= 3 && args[0].equalsIgnoreCase("level")) {
            return handleLevelCommand(sender, args);
        }

        // 직업 변경
        if (args.length >= 3 && args[0].equalsIgnoreCase("job")) {
            return handleJobCommand(sender, args);
        }

        // 사용법 표시
        showUsage(sender);
        return true;
    }

    /**
     * 플러그인 통계 표시
     */
    private void showStats(Player player) {
        player.sendMessage(Component.text("=== Sypixel RPG Statistics ===", NamedTextColor.GOLD));
        player.sendMessage(Component.text("Online Players: " + Bukkit.getOnlinePlayers().size(), NamedTextColor.WHITE));
        player.sendMessage(Component.text("Server TPS: " + String.format("%.2f", getServerTPS()), NamedTextColor.WHITE));
        player.sendMessage(Component.text("Memory Usage: " + getMemoryUsage(), NamedTextColor.WHITE));
        player.sendMessage(Component.text("Debug Mode: " + (debugMode ? "ON" : "OFF"), NamedTextColor.WHITE));
    }

    /**
     * 플러그인 리로드
     */
    private void reloadPlugin(Player player) {
        player.sendMessage(Component.text("Reloading language files...", NamedTextColor.YELLOW));

        try {
            langManager.reload();
            player.sendMessage(Component.text("Language files reloaded successfully!", NamedTextColor.GREEN));
        } catch (Exception e) {
            player.sendMessage(Component.text("Failed to reload language files: " + e.getMessage(), NamedTextColor.RED));
        }
    }

    /**
     * 디버그 모드 토글
     */
    private void toggleDebug(Player player) {
        debugMode = !debugMode;
        player.sendMessage(Component.text("Debug mode " + (debugMode ? "enabled" : "disabled"),
                debugMode ? NamedTextColor.GREEN : NamedTextColor.RED));
    }

    /**
     * 서버 TPS 가져오기
     */
    private double getServerTPS() {
        try {
            Object server = Bukkit.getServer().getClass().getMethod("getServer").invoke(Bukkit.getServer());
            double[] recentTps = (double[]) server.getClass().getField("recentTps").get(server);
            return recentTps[0];
        } catch (Exception e) {
            return 20.0;
        }
    }

    /**
     * 메모리 사용량 가져오기
     */
    private String getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        long totalMemory = runtime.totalMemory() / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024;
        long usedMemory = totalMemory - freeMemory;

        return String.format("%dMB / %dMB (Max: %dMB)", usedMemory, totalMemory, maxMemory);
    }

    /**
     * 경험치 명령어 처리
     */
    private boolean handleExpCommand(CommandSender sender, String[] args) {
        String action = args[1];

        if (!action.equalsIgnoreCase("give")) {
            sender.sendMessage(Component.text("사용법: /sypixelrpg exp give <플레이어> <양>", ColorUtil.ERROR));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(Component.text("플레이어를 찾을 수 없습니다.", ColorUtil.ERROR));
            return true;
        }

        if (args.length < 4) {
            sender.sendMessage(Component.text("경험치 양을 입력해주세요.", ColorUtil.ERROR));
            return true;
        }

        try {
            long amount = Long.parseLong(args[3]);
            if (amount <= 0) {
                sender.sendMessage(Component.text("경험치는 양수여야 합니다.", ColorUtil.ERROR));
                return true;
            }

            RPGPlayer rpgPlayer = rpgMain.getRPGPlayerManager().getPlayer(target);
            if (rpgPlayer == null) {
                sender.sendMessage(Component.text("플레이어 데이터를 찾을 수 없습니다.", ColorUtil.ERROR));
                return true;
            }

            if (!rpgPlayer.hasJob()) {
                sender.sendMessage(Component.text("대상 플레이어가 아직 직업을 선택하지 않았습니다.", ColorUtil.ERROR));
                return true;
            }

            rpgPlayer.addExperience(amount);
            sender.sendMessage(Component.text(target.getName() + "에게 " + amount + " 경험치를 지급했습니다.", ColorUtil.SUCCESS));
            target.sendMessage(Component.text(amount + " 경험치를 받았습니다!", ColorUtil.EMERALD));

        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("올바른 숫자를 입력해주세요.", ColorUtil.ERROR));
        }

        return true;
    }

    /**
     * 레벨 명령어 처리
     */
    private boolean handleLevelCommand(CommandSender sender, String[] args) {
        // TODO: 레벨 설정 구현
        sender.sendMessage(Component.text("레벨 설정 기능은 아직 구현되지 않았습니다.", ColorUtil.WARNING));
        return true;
    }

    /**
     * 직업 명령어 처리
     */
    private boolean handleJobCommand(CommandSender sender, String[] args) {
        // TODO: 직업 변경 구현
        sender.sendMessage(Component.text("직업 변경 기능은 아직 구현되지 않았습니다.", ColorUtil.WARNING));
        return true;
    }

    /**
     * 사용법 표시
     */
    private void showUsage(CommandSender sender) {
        sender.sendMessage(Component.text("=== Sypixel RPG Admin Commands ===", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("/sypixelrpg stats - Show plugin statistics", NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/sypixelrpg reload - Reload language files", NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/sypixelrpg debug - Toggle debug mode", NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/sypixelrpg exp give <플레이어> <양> - 경험치 지급", NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/sypixelrpg level set <플레이어> <레벨> - 레벨 설정", NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/sypixelrpg job set <플레이어> <직업> - 직업 변경", NamedTextColor.WHITE));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("sypixelrpg.admin")) {
            return List.of();
        }

        if (args.length == 1) {
            return List.of("stats", "reload", "debug", "exp", "level", "job").stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("exp")) {
                return List.of("give");
            } else if (args[0].equalsIgnoreCase("level") || args[0].equalsIgnoreCase("job")) {
                return List.of("set");
            }
        }

        if (args.length == 3) {
            if ((args[0].equalsIgnoreCase("exp") && args[1].equalsIgnoreCase("give")) ||
                    (args[0].equalsIgnoreCase("level") && args[1].equalsIgnoreCase("set")) ||
                    (args[0].equalsIgnoreCase("job") && args[1].equalsIgnoreCase("set"))) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
            }
        }

        if (args.length == 4 && args[0].equalsIgnoreCase("job") && args[1].equalsIgnoreCase("set")) {
            return Arrays.stream(JobType.values())
                    .map(JobType::name)
                    .filter(name -> name.toLowerCase().startsWith(args[3].toLowerCase()))
                    .collect(Collectors.toList());
        }

        return List.of();
    }
}