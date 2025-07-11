package com.febrie.rpg.command;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.job.JobType;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
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

    public AdminCommands(@NotNull Plugin plugin, @NotNull LangManager langManager) {
        super(plugin, langManager);
        this.rpgMain = (RPGMain) plugin;
    }

    @Override
    protected boolean executeCommand(@NotNull CommandSender sender, @NotNull Command command,
                                     @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("sypixelrpg.admin")) {
            sender.sendMessage(Component.text("권한이 없습니다.", ColorUtil.ERROR));
            return true;
        }

        if (args.length == 0) {
            showHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        return switch (subCommand) {
            case "exp", "경험치" -> handleExpCommand(sender, args);
            case "level", "레벨" -> handleLevelCommand(sender, args);
            case "job", "직업" -> handleJobCommand(sender, args);
            case "stats", "스탯" -> handleStatsCommand(sender, args);
            case "talent", "특성" -> handleTalentCommand(sender, args);
            case "reset", "초기화" -> handleResetCommand(sender, args);
            default -> {
                showHelp(sender);
                yield true;
            }
        };
    }

    /**
     * 도움말 표시
     */
    private void showHelp(@NotNull CommandSender sender) {
        sender.sendMessage(Component.text("=== Sypixel RPG 관리자 명령어 ===", ColorUtil.LEGENDARY));
        sender.sendMessage(Component.text("/rpgadmin exp <플레이어> <경험치> - 경험치 지급", ColorUtil.WHITE));
        sender.sendMessage(Component.text("/rpgadmin level <플레이어> <레벨> - 레벨 설정", ColorUtil.WHITE));
        sender.sendMessage(Component.text("/rpgadmin job <플레이어> <직업> - 직업 변경", ColorUtil.WHITE));
        sender.sendMessage(Component.text("/rpgadmin stats <플레이어> add <포인트> - 스탯 포인트 지급", ColorUtil.WHITE));
        sender.sendMessage(Component.text("/rpgadmin talent <플레이어> add <포인트> - 특성 포인트 지급", ColorUtil.WHITE));
        sender.sendMessage(Component.text("/rpgadmin reset <플레이어> - 플레이어 데이터 초기화", ColorUtil.WHITE));
    }

    /**
     * 경험치 명령어 처리
     */
    private boolean handleExpCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Component.text("사용법: /rpgadmin exp <플레이어> <경험치>", ColorUtil.WARNING));
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("플레이어를 찾을 수 없습니다: " + args[1], ColorUtil.ERROR));
            return true;
        }

        try {
            long exp = Long.parseLong(args[2]);
            RPGPlayer rpgPlayer = rpgMain.getRPGPlayerManager().getOrCreatePlayer(target);

            if (!rpgPlayer.hasJob()) {
                sender.sendMessage(Component.text("해당 플레이어는 직업이 없습니다.", ColorUtil.ERROR));
                return true;
            }

            rpgPlayer.addExperience(exp);

            sender.sendMessage(Component.text(target.getName() + "에게 " + exp + " 경험치를 지급했습니다.",
                    ColorUtil.SUCCESS));
            target.sendMessage(Component.text(exp + " 경험치를 받았습니다!", ColorUtil.EXPERIENCE));

        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("올바른 숫자를 입력하세요.", ColorUtil.ERROR));
        }

        return true;
    }

    /**
     * 레벨 명령어 처리
     */
    private boolean handleLevelCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Component.text("사용법: /rpgadmin level <플레이어> <레벨>", ColorUtil.WARNING));
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("플레이어를 찾을 수 없습니다: " + args[1], ColorUtil.ERROR));
            return true;
        }

        try {
            int level = Integer.parseInt(args[2]);
            RPGPlayer rpgPlayer = rpgMain.getRPGPlayerManager().getOrCreatePlayer(target);

            if (!rpgPlayer.hasJob()) {
                sender.sendMessage(Component.text("해당 플레이어는 직업이 없습니다.", ColorUtil.ERROR));
                return true;
            }

            // 해당 레벨까지의 총 경험치 계산
            long totalExp = com.febrie.rpg.level.LevelSystem.getTotalExpForLevel(level, rpgPlayer.getJob());
            long currentExp = rpgPlayer.getExperience();

            if (totalExp > currentExp) {
                rpgPlayer.addExperience(totalExp - currentExp);
            } else {
                // 레벨 다운은 직접 경험치 설정 (나중에 구현)
                sender.sendMessage(Component.text("레벨 다운은 아직 지원하지 않습니다.", ColorUtil.ERROR));
                return true;
            }

            sender.sendMessage(Component.text(target.getName() + "의 레벨을 " + level + "로 설정했습니다.",
                    ColorUtil.SUCCESS));

        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("올바른 숫자를 입력하세요.", ColorUtil.ERROR));
        }

        return true;
    }

    /**
     * 직업 명령어 처리
     */
    private boolean handleJobCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 3) {
            sender.sendMessage(Component.text("사용법: /rpgadmin job <플레이어> <직업>", ColorUtil.WARNING));
            sender.sendMessage(Component.text("직업 목록: " + Arrays.stream(JobType.values())
                    .map(JobType::name)
                    .collect(Collectors.joining(", ")), ColorUtil.GRAY));
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("플레이어를 찾을 수 없습니다: " + args[1], ColorUtil.ERROR));
            return true;
        }

        try {
            JobType job = JobType.valueOf(args[2].toUpperCase());
            RPGPlayer rpgPlayer = rpgMain.getRPGPlayerManager().getOrCreatePlayer(target);

            // 강제 직업 설정 (관리자 명령어이므로 기존 직업 무시)
            // TODO: RPGPlayer에 forceSetJob 메소드 추가 필요

            sender.sendMessage(Component.text("직업 변경 기능은 아직 구현 중입니다.", ColorUtil.WARNING));

        } catch (IllegalArgumentException e) {
            sender.sendMessage(Component.text("올바른 직업명을 입력하세요.", ColorUtil.ERROR));
            sender.sendMessage(Component.text("직업 목록: " + Arrays.stream(JobType.values())
                    .map(JobType::name)
                    .collect(Collectors.joining(", ")), ColorUtil.GRAY));
        }

        return true;
    }

    /**
     * 스탯 명령어 처리
     */
    private boolean handleStatsCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 4 || !args[2].equalsIgnoreCase("add")) {
            sender.sendMessage(Component.text("사용법: /rpgadmin stats <플레이어> add <포인트>", ColorUtil.WARNING));
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("플레이어를 찾을 수 없습니다: " + args[1], ColorUtil.ERROR));
            return true;
        }

        try {
            int points = Integer.parseInt(args[3]);
            RPGPlayer rpgPlayer = rpgMain.getRPGPlayerManager().getOrCreatePlayer(target);

            // TODO: RPGPlayer에 addStatPoints 메소드 추가 필요
            sender.sendMessage(Component.text("스탯 포인트 지급 기능은 구현 중입니다.", ColorUtil.WARNING));

        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("올바른 숫자를 입력하세요.", ColorUtil.ERROR));
        }

        return true;
    }

    /**
     * 특성 명령어 처리
     */
    private boolean handleTalentCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 4 || !args[2].equalsIgnoreCase("add")) {
            sender.sendMessage(Component.text("사용법: /rpgadmin talent <플레이어> add <포인트>", ColorUtil.WARNING));
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("플레이어를 찾을 수 없습니다: " + args[1], ColorUtil.ERROR));
            return true;
        }

        try {
            int points = Integer.parseInt(args[3]);
            RPGPlayer rpgPlayer = rpgMain.getRPGPlayerManager().getOrCreatePlayer(target);

            rpgPlayer.getTalents().addPoints(points);
            rpgPlayer.saveToPDC();

            sender.sendMessage(Component.text(target.getName() + "에게 특성 포인트 " + points + "를 지급했습니다.",
                    ColorUtil.SUCCESS));
            target.sendMessage(Component.text("특성 포인트 " + points + "를 받았습니다!", ColorUtil.LEGENDARY));

        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("올바른 숫자를 입력하세요.", ColorUtil.ERROR));
        }

        return true;
    }

    /**
     * 초기화 명령어 처리
     */
    private boolean handleResetCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Component.text("사용법: /rpgadmin reset <플레이어>", ColorUtil.WARNING));
            return true;
        }

        Player target = plugin.getServer().getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("플레이어를 찾을 수 없습니다: " + args[1], ColorUtil.ERROR));
            return true;
        }

        // TODO: 플레이어 데이터 초기화 구현
        sender.sendMessage(Component.text("플레이어 초기화 기능은 구현 중입니다.", ColorUtil.WARNING));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("sypixelrpg.admin")) {
            return null;
        }

        if (args.length == 1) {
            return Arrays.asList("exp", "level", "job", "stats", "talent", "reset");
        }

        if (args.length == 2) {
            // 플레이어 이름 자동완성
            return plugin.getServer().getOnlinePlayers().stream()
                    .map(Player::getName)
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 3) {
            String subCommand = args[0].toLowerCase();

            if (subCommand.equals("job") || subCommand.equals("직업")) {
                return Arrays.stream(JobType.values())
                        .map(JobType::name)
                        .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
            }

            if (subCommand.equals("stats") || subCommand.equals("talent")) {
                return List.of("add");
            }
        }

        return null;
    }
}