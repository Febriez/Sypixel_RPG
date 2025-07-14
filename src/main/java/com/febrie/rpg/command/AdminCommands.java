package com.febrie.rpg.command;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.impl.ProfileGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.job.JobType;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
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
 * 경험치 지급, 레벨 설정, 직업 변경, NPC 생성 등의 기능 제공
 *
 * @author Febrie, CoffeeTory
 */
public class AdminCommands extends BaseCommand {

    private final RPGMain rpgMain;
    private final GuiManager guiManager;
    private boolean debugMode = false;

    public AdminCommands(@NotNull Plugin plugin, @NotNull LangManager langManager, @NotNull GuiManager guiManager) {
        super(plugin, langManager);
        this.rpgMain = (RPGMain) plugin;
        this.guiManager = guiManager;
    }

    @Override
    protected boolean executeCommand(@NotNull CommandSender sender, @NotNull Command command,
                                     @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("sypixelrpg.admin")) {
            sender.sendMessage(Component.text("권한이 없습니다.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0) {
            showUsage(sender);
            return true;
        }

        // 통계 명령어
        if (args[0].equalsIgnoreCase("stats")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players!");
                return true;
            }
            showStats(player);
            return true;
        }

        // 리로드 명령어
        if (args[0].equalsIgnoreCase("reload")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players!");
                return true;
            }
            reloadPlugin(player);
            return true;
        }

        // 디버그 모드 토글
        if (args[0].equalsIgnoreCase("debug")) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players!");
                return true;
            }
            toggleDebug(player);
            return true;
        }

        // 다른 플레이어 프로필 보기
        if (args.length >= 2 && args[0].equalsIgnoreCase("viewprofile")) {
            return handleViewProfileCommand(sender, args[1]);
        }

        // NPC 생성 명령어
        if (args.length >= 1 && args[0].equalsIgnoreCase("npc")) {
            return handleNPCCommand(sender, args);
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
     * 서버 TPS 계산
     */
    private double getServerTPS() {
        // Bukkit의 TPS는 20이 최대
        return Math.min(20.0, Bukkit.getTPS()[0]);
    }

    /**
     * 메모리 사용량 계산
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
     * 플러그인 리로드
     */
    private void reloadPlugin(Player player) {
        player.sendMessage(Component.text("언어 파일을 다시 로드합니다...", NamedTextColor.YELLOW));

        try {
            langManager.reload();
            player.sendMessage(Component.text("언어 파일이 성공적으로 다시 로드되었습니다!", NamedTextColor.GREEN));
        } catch (Exception e) {
            player.sendMessage(Component.text("언어 파일 로드 실패: " + e.getMessage(), NamedTextColor.RED));
        }
    }

    /**
     * 디버그 모드 토글
     */
    private void toggleDebug(Player player) {
        debugMode = !debugMode;
        player.sendMessage(Component.text("디버그 모드 " + (debugMode ? "활성화" : "비활성화"),
                debugMode ? NamedTextColor.GREEN : NamedTextColor.RED));
    }

    /**
     * 다른 플레이어의 프로필 보기 (관리자 전용)
     */
    private boolean handleViewProfileCommand(CommandSender sender, String targetName) {
        if (!(sender instanceof Player admin)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        Player target = Bukkit.getPlayer(targetName);
        if (target == null) {
            admin.sendMessage(Component.text("플레이어를 찾을 수 없습니다: " + targetName, ColorUtil.ERROR));
            admin.sendMessage(Component.text("온라인 플레이어만 조회할 수 있습니다.", ColorUtil.GRAY));
            return true;
        }

        // 관리자가 다른 플레이어의 프로필을 볼 때
        ProfileGui profileGui = new ProfileGui(target, admin, guiManager, langManager);
        guiManager.openGui(admin, profileGui);

        admin.sendMessage(Component.text("=== 프로필 조회 ===", ColorUtil.GOLD));
        admin.sendMessage(Component.text("대상: ", ColorUtil.GRAY)
                .append(Component.text(target.getName(), ColorUtil.YELLOW)));
        admin.sendMessage(Component.text("프로필 GUI가 열렸습니다.", ColorUtil.SUCCESS));

        return true;
    }

    /**
     * NPC 명령어 처리
     */
    private boolean handleNPCCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("This command can only be used by players!");
            return true;
        }

        if (args.length < 2) {
            player.sendMessage(Component.text("사용법: /rpgadmin npc create <타입>", ColorUtil.WARNING));
            player.sendMessage(Component.text("사용 가능한 타입: questgiver", ColorUtil.GRAY));
            return true;
        }

        if (args[1].equalsIgnoreCase("create")) {
            if (args.length < 3) {
                player.sendMessage(Component.text("NPC 타입을 지정해주세요!", ColorUtil.ERROR));
                return true;
            }

            String npcType = args[2].toLowerCase();

            if (npcType.equals("questgiver")) {
                createQuestNPC(player);
            } else {
                player.sendMessage(Component.text("알 수 없는 NPC 타입: " + npcType, ColorUtil.ERROR));
            }
        }

        return true;
    }

    /**
     * 퀘스트 NPC 생성
     */
    private void createQuestNPC(Player player) {
        // Citizens API 확인
        if (!plugin.getServer().getPluginManager().isPluginEnabled("Citizens")) {
            player.sendMessage(Component.text("Citizens 플러그인이 필요합니다!", ColorUtil.ERROR));
            return;
        }

        try {
            // Citizens API 사용
            net.citizensnpcs.api.npc.NPC npc = net.citizensnpcs.api.CitizensAPI.getNPCRegistry()
                    .createNPC(org.bukkit.entity.EntityType.VILLAGER,
                            LegacyComponentSerializer.legacySection().serialize(Component.text("퀘스트 관리인", ColorUtil.LEGENDARY)));

            // NPC 위치 설정
            npc.spawn(player.getLocation());

            // NPC에 메타데이터 설정 (퀘스트 NPC임을 표시)
            npc.data().setPersistent("quest_npc", true);
            npc.data().setPersistent("quest_id", "main_heroes_journey");

            // 외형 설정
            if (npc.getEntity() instanceof org.bukkit.entity.Villager villager) {
                villager.setProfession(org.bukkit.entity.Villager.Profession.LIBRARIAN);
                villager.setVillagerType(org.bukkit.entity.Villager.Type.PLAINS);
            }

            player.sendMessage(Component.text("퀘스트 NPC가 생성되었습니다!", ColorUtil.SUCCESS));
            player.sendMessage(Component.text("NPC ID: " + npc.getId(), ColorUtil.GRAY));

        } catch (Exception e) {
            player.sendMessage(Component.text("NPC 생성 중 오류가 발생했습니다: " + e.getMessage(), ColorUtil.ERROR));
            e.printStackTrace();
        }
    }

    /**
     * 경험치 명령어 처리
     */
    private boolean handleExpCommand(CommandSender sender, String[] args) {
        if (args.length < 4 || !args[1].equalsIgnoreCase("give")) {
            sender.sendMessage(Component.text("사용법: /rpgadmin exp give <플레이어> <양>", ColorUtil.ERROR));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(Component.text("플레이어를 찾을 수 없습니다: " + args[2], ColorUtil.ERROR));
            return true;
        }

        try {
            int amount = Integer.parseInt(args[3]);
            if (amount <= 0) {
                sender.sendMessage(Component.text("경험치는 양수여야 합니다.", ColorUtil.ERROR));
                return true;
            }

            RPGPlayer rpgPlayer = rpgMain.getRPGPlayerManager().getOrCreatePlayer(target);

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
        sender.sendMessage(Component.text("/rpgadmin stats - 플러그인 통계 보기", NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/rpgadmin reload - 언어 파일 다시 로드", NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/rpgadmin debug - 디버그 모드 토글", NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/rpgadmin viewprofile <플레이어> - 다른 플레이어 프로필 보기", NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/rpgadmin exp give <플레이어> <양> - 경험치 지급", NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/rpgadmin level set <플레이어> <레벨> - 레벨 설정", NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/rpgadmin job set <플레이어> <직업> - 직업 변경", NamedTextColor.WHITE));
        sender.sendMessage(Component.text("/rpgadmin npc create <타입> - NPC 생성", NamedTextColor.WHITE));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("sypixelrpg.admin")) {
            return List.of();
        }

        if (args.length == 1) {
            return List.of("stats", "reload", "debug", "viewprofile", "exp", "level", "job", "npc").stream()
                    .filter(s -> s.toLowerCase().startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("viewprofile")) {
                return Bukkit.getOnlinePlayers().stream()
                        .map(Player::getName)
                        .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                        .collect(Collectors.toList());
            } else if (args[0].equalsIgnoreCase("exp")) {
                return List.of("give");
            } else if (args[0].equalsIgnoreCase("level") || args[0].equalsIgnoreCase("job")) {
                return List.of("set");
            } else if (args[0].equalsIgnoreCase("npc")) {
                return List.of("create");
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
            } else if (args[0].equalsIgnoreCase("npc") && args[1].equalsIgnoreCase("create")) {
                return List.of("questgiver");
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