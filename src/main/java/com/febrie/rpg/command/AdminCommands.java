package com.febrie.rpg.command;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.listener.ListenerManager;
import com.febrie.rpg.npc.NPCType;
import com.febrie.rpg.npc.manager.NPCManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.player.manager.RPGPlayerManager;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.quest.registry.QuestRegistry;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LogUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RPG 관리자 명령어 처리
 * 디버그, 통계, 플레이어 관리 등
 *
 * @author Febrie
 */
public class AdminCommands implements CommandExecutor, TabCompleter {

    private final RPGMain plugin;
    private final RPGPlayerManager playerManager;
    private final GuiManager guiManager;
    private final LangManager langManager;
    private final NPCManager npcManager;
    private final ListenerManager listenerManager;
    private final QuestManager questManager;

    public AdminCommands(@NotNull RPGMain plugin, @NotNull RPGPlayerManager playerManager,
                         @NotNull GuiManager guiManager, @NotNull LangManager langManager,
                         @NotNull NPCManager npcManager, @NotNull ListenerManager listenerManager) {
        this.plugin = plugin;
        this.playerManager = playerManager;
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.npcManager = npcManager;
        this.listenerManager = listenerManager;
        this.questManager = QuestManager.getInstance();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (args.length == 0) {
            showUsage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        return switch (subCommand) {
            case "stats" -> handleStatsCommand(sender, args);
            case "reload" -> handleReloadCommand(sender, args);
            case "debug" -> handleDebugCommand(sender, args);
            case "viewprofile" -> handleViewProfileCommand(sender, args);
            case "exp" -> handleExpCommand(sender, args);
            case "level" -> handleLevelCommand(sender, args);
            case "job" -> handleJobCommand(sender, args);
            case "npc" -> handleNpcCommand(sender, args);
            case "quest" -> handleQuestCommand(sender, args);
            default -> {
                sender.sendMessage(Component.text("알 수 없는 명령어: " + subCommand, ColorUtil.ERROR));
                yield false;
            }
        };
    }

    /**
     * 통계 명령어 처리
     */
    private boolean handleStatsCommand(CommandSender sender, String[] args) {
        sender.sendMessage(Component.text("=== Sypixel RPG Statistics ===", NamedTextColor.GOLD));
        sender.sendMessage(Component.text("온라인 플레이어: " + Bukkit.getOnlinePlayers().size(), NamedTextColor.WHITE));
        sender.sendMessage(Component.text("로드된 RPG 플레이어: " + playerManager.getLoadedPlayerCount(), NamedTextColor.WHITE));
        sender.sendMessage(Component.text("활성 GUI: " + guiManager.getActiveGuiCount(), NamedTextColor.WHITE));
        sender.sendMessage(Component.text("등록된 리스너: " + listenerManager.getRegisteredListenerCount(), NamedTextColor.WHITE));
        sender.sendMessage(Component.text("등록된 퀘스트: " + questManager.getAllQuests().size() + "/" + QuestID.values().length, NamedTextColor.WHITE));
        sender.sendMessage(Component.text("구현된 퀘스트: " + QuestRegistry.getImplementedCount(), NamedTextColor.WHITE));
        sender.sendMessage(Component.text("디버그 모드: " + (LogUtil.isDebugEnabled() ? "ON" : "OFF"), NamedTextColor.WHITE));
        sender.sendMessage(Component.text("메모리 사용량: " + getMemoryUsage(), NamedTextColor.WHITE));
        return true;
    }

    /**
     * 리로드 명령어 처리
     */
    private boolean handleReloadCommand(CommandSender sender, String[] args) {
        langManager.reload();
        sender.sendMessage(Component.text("언어 파일이 다시 로드되었습니다.", ColorUtil.SUCCESS));
        return true;
    }

    /**
     * 디버그 명령어 처리
     */
    private boolean handleDebugCommand(CommandSender sender, String[] args) {
        boolean newState = !LogUtil.isDebugEnabled();
        LogUtil.setDebugEnabled(newState);
        sender.sendMessage(Component.text("디버그 모드: " + (newState ? "ON" : "OFF"), ColorUtil.SUCCESS));
        return true;
    }

    /**
     * 프로필 보기 명령어 처리
     */
    private boolean handleViewProfileCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("사용법: /rpgadmin viewprofile <플레이어>", ColorUtil.ERROR));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("플레이어를 찾을 수 없습니다.", ColorUtil.ERROR));
            return true;
        }

        if (sender instanceof Player viewer) {
            guiManager.openProfileViewGui(viewer, target);
        } else {
            // 콘솔에서 실행시 텍스트로 출력
            RPGPlayer rpgPlayer = playerManager.getOrCreatePlayer(target);
            sender.sendMessage(Component.text("=== " + target.getName() + "님의 프로필 ===", ColorUtil.GOLD));
            sender.sendMessage(Component.text("레벨: " + rpgPlayer.getLevel(), ColorUtil.WHITE));
            sender.sendMessage(Component.text("경험치: " + rpgPlayer.getExperience(), ColorUtil.WHITE));
            // TODO: 추가 정보 출력
        }

        return true;
    }

    /**
     * 경험치 명령어 처리
     */
    private boolean handleExpCommand(CommandSender sender, String[] args) {
        if (args.length < 4) {
            sender.sendMessage(Component.text("사용법: /rpgadmin exp give <플레이어> <양>", ColorUtil.ERROR));
            return true;
        }

        if (!args[1].equalsIgnoreCase("give")) {
            sender.sendMessage(Component.text("사용법: /rpgadmin exp give <플레이어> <양>", ColorUtil.ERROR));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(Component.text("플레이어를 찾을 수 없습니다.", ColorUtil.ERROR));
            return true;
        }

        try {
            int amount = Integer.parseInt(args[3]);
            if (amount <= 0) {
                sender.sendMessage(Component.text("양수를 입력해주세요.", ColorUtil.ERROR));
                return true;
            }

            RPGPlayer rpgPlayer = playerManager.getOrCreatePlayer(target);
            rpgPlayer.addExperience(amount);
            sender.sendMessage(Component.text(target.getName() + "님에게 " + amount + " 경험치를 지급했습니다.", ColorUtil.SUCCESS));
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
     * NPC 명령어 처리
     */
    private boolean handleNpcCommand(CommandSender sender, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("플레이어만 사용할 수 있습니다.", ColorUtil.ERROR));
            return true;
        }

        if (args.length < 3) {
            sender.sendMessage(Component.text("사용법: /rpgadmin npc create <타입>", ColorUtil.ERROR));
            return true;
        }

        if (!args[1].equalsIgnoreCase("create")) {
            sender.sendMessage(Component.text("사용법: /rpgadmin npc create <타입>", ColorUtil.ERROR));
            return true;
        }

        try {
            NPCType type = NPCType.valueOf(args[2].toUpperCase());
            Location loc = player.getLocation();

            npcManager.createNPC(type, loc, null);
            player.sendMessage(Component.text(type.getDisplayName() + " NPC를 생성했습니다.", ColorUtil.SUCCESS));

        } catch (IllegalArgumentException e) {
            player.sendMessage(Component.text("올바르지 않은 NPC 타입입니다.", ColorUtil.ERROR));
            player.sendMessage(Component.text("사용 가능: " + Arrays.toString(NPCType.values()), ColorUtil.GRAY));
        }

        return true;
    }

    /**
     * 퀘스트 명령어 처리
     */
    private boolean handleQuestCommand(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("사용법: /rpgadmin quest <give|complete|reset|list|reload> [플레이어] [퀘스트ID]", ColorUtil.ERROR));
            return true;
        }

        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "give" -> {
                if (args.length < 4) {
                    sender.sendMessage(Component.text("사용법: /rpgadmin quest give <플레이어> <퀘스트ID>", ColorUtil.ERROR));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage(Component.text("플레이어를 찾을 수 없습니다.", ColorUtil.ERROR));
                    return true;
                }

                // QuestID 파싱
                QuestID questId;
                try {
                    // enum name으로 시도
                    questId = QuestID.valueOf(args[3].toUpperCase());
                } catch (IllegalArgumentException e) {
                    try {
                        // legacy ID로 시도
                        questId = QuestID.fromLegacyId(args[3]);
                    } catch (IllegalArgumentException ex) {
                        sender.sendMessage(Component.text("올바르지 않은 퀘스트 ID입니다: " + args[3], ColorUtil.ERROR));
                        return true;
                    }
                }

                // 퀘스트 시작
                if (questManager.startQuest(target, questId)) {
                    sender.sendMessage(Component.text("퀘스트를 지급했습니다: " + questId.getDisplayName(), ColorUtil.SUCCESS));
                    target.sendMessage(Component.text("새로운 퀘스트를 받았습니다: " + questId.getDisplayName(), ColorUtil.GOLD));
                } else {
                    sender.sendMessage(Component.text("퀘스트를 시작할 수 없습니다. 이미 진행중이거나 조건을 충족하지 않습니다.", ColorUtil.ERROR));
                }
            }

            case "list" -> {
                sender.sendMessage(Component.text("=== 사용 가능한 퀘스트 목록 ===", ColorUtil.GOLD));

                for (Quest.QuestCategory category : Quest.QuestCategory.values()) {
                    sender.sendMessage(Component.text("\n" + category.name() + ":", ColorUtil.YELLOW));

                    QuestID[] questIds = QuestID.getByCategory(category);
                    for (QuestID id : questIds) {
                        boolean implemented = QuestRegistry.isImplemented(id);
                        Component status = implemented
                                ? Component.text(" ✓", ColorUtil.SUCCESS)
                                : Component.text(" ✗", ColorUtil.ERROR);

                        sender.sendMessage(Component.text("  - " + id.name() + " (" + id.getDisplayName() + ")")
                                .append(status));
                    }
                }
            }

            case "reload" -> {
                // 퀘스트 데이터 리로드 (언어 파일 등)
                langManager.reload();
                sender.sendMessage(Component.text("퀘스트 데이터를 리로드했습니다.", ColorUtil.SUCCESS));
            }

            default -> {
                sender.sendMessage(Component.text("알 수 없는 하위 명령어: " + subCommand, ColorUtil.ERROR));
            }
        }

        return true;
    }

    /**
     * 메모리 사용량 조회
     */
    private String getMemoryUsage() {
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        return usedMemory + "/" + maxMemory + " MB";
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
        sender.sendMessage(Component.text("/rpgadmin quest <give|list|reload> - 퀘스트 관리", NamedTextColor.WHITE));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                                @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("rpg.admin")) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            return Arrays.asList("stats", "reload", "debug", "viewprofile", "exp", "level", "job", "npc", "quest")
                    .stream()
                    .filter(s -> s.startsWith(args[0].toLowerCase()))
                    .collect(Collectors.toList());
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "viewprofile" -> {
                    return Bukkit.getOnlinePlayers().stream()
                            .map(Player::getName)
                            .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                            .collect(Collectors.toList());
                }
                case "exp" -> {
                    return List.of("give");
                }
                case "level" -> {
                    return List.of("set");
                }
                case "job" -> {
                    return List.of("set");
                }
                case "npc" -> {
                    return List.of("create");
                }
                case "quest" -> {
                    return Arrays.asList("give", "list", "reload");
                }
            }
        }

        if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "exp", "level", "job" -> {
                    if (args[1].equalsIgnoreCase("give") || args[1].equalsIgnoreCase("set")) {
                        return Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                                .collect(Collectors.toList());
                    }
                }
                case "npc" -> {
                    if (args[1].equalsIgnoreCase("create")) {
                        return Arrays.stream(NPCType.values())
                                .map(Enum::name)
                                .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                                .collect(Collectors.toList());
                    }
                }
                case "quest" -> {
                    if (args[1].equalsIgnoreCase("give")) {
                        return Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                                .collect(Collectors.toList());
                    }
                }
            }
        }

        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("quest") && args[1].equalsIgnoreCase("give")) {
                return getQuestIdSuggestions(args[3]);
            }
        }

        return new ArrayList<>();
    }

    /**
     * 탭 완성 - 퀘스트 ID
     */
    private List<String> getQuestIdSuggestions(String partial) {
        List<String> suggestions = new ArrayList<>();
        String upperPartial = partial.toUpperCase();

        // enum name으로 검색
        for (QuestID id : QuestID.values()) {
            if (id.name().startsWith(upperPartial)) {
                suggestions.add(id.name());
            }
        }

        // legacy ID로도 검색
        for (QuestID id : QuestID.values()) {
            if (id.getLegacyId().startsWith(partial.toLowerCase())) {
                suggestions.add(id.getLegacyId());
            }
        }

        return suggestions;
    }
}