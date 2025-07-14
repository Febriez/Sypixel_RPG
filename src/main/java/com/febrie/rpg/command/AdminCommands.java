package com.febrie.rpg.command;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.listener.ListenerManager;
import com.febrie.rpg.npc.NPCType;
import com.febrie.rpg.npc.manager.NPCManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.manager.RPGPlayerManager;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.quest.registry.QuestRegistry;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LogUtil;
import net.kyori.adventure.text.Component;
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

        if (!sender.hasPermission("rpg.admin")) {
            sender.sendMessage(Component.text("권한이 없습니다.", ColorUtil.ERROR));
            return true;
        }

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
                showUsage(sender);
                yield true;
            }
        };
    }

    /**
     * 사용법 표시
     */
    private void showUsage(@NotNull CommandSender sender) {
        sender.sendMessage(Component.text("=== RPG Admin Commands ===", ColorUtil.GOLD));
        sender.sendMessage(Component.text("/rpgadmin stats - 서버 통계 확인", ColorUtil.YELLOW));
        sender.sendMessage(Component.text("/rpgadmin reload - 설정 리로드", ColorUtil.YELLOW));
        sender.sendMessage(Component.text("/rpgadmin debug - 디버그 모드 토글", ColorUtil.YELLOW));
        sender.sendMessage(Component.text("/rpgadmin viewprofile <플레이어> - 프로필 확인", ColorUtil.YELLOW));
        sender.sendMessage(Component.text("/rpgadmin exp give <플레이어> <경험치> - 경험치 지급", ColorUtil.YELLOW));
        sender.sendMessage(Component.text("/rpgadmin level set <플레이어> <레벨> - 레벨 설정", ColorUtil.YELLOW));
        sender.sendMessage(Component.text("/rpgadmin job set <플레이어> <직업> - 직업 설정", ColorUtil.YELLOW));
        sender.sendMessage(Component.text("/rpgadmin npc create <타입> - NPC 생성", ColorUtil.YELLOW));
        sender.sendMessage(Component.text("/rpgadmin quest <give|list|reload> - 퀘스트 관리", ColorUtil.YELLOW));
    }

    /**
     * 통계 명령어 처리
     */
    private boolean handleStatsCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        sender.sendMessage(Component.text("=== RPG 서버 통계 ===", ColorUtil.GOLD));
        sender.sendMessage(Component.text("온라인 플레이어: " + Bukkit.getOnlinePlayers().size(), ColorUtil.WHITE));
        sender.sendMessage(Component.text("로드된 RPG 플레이어: " + playerManager.getAllPlayers().size(), ColorUtil.WHITE));
        sender.sendMessage(Component.text("활성 리스너: " + listenerManager.getRegisteredListeners().size(), ColorUtil.WHITE));
        sender.sendMessage(Component.text("등록된 퀘스트: " + QuestID.values().length, ColorUtil.WHITE));
        sender.sendMessage(Component.text("구현된 퀘스트: " + QuestRegistry.getImplementedCount(), ColorUtil.WHITE));

        // 메모리 사용량
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        sender.sendMessage(Component.text("메모리 사용량: " + usedMemory + "MB / " + maxMemory + "MB", ColorUtil.GRAY));

        return true;
    }

    /**
     * 리로드 명령어 처리
     */
    private boolean handleReloadCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        sender.sendMessage(Component.text("설정을 리로드하는 중...", ColorUtil.YELLOW));

        // 언어 파일 리로드
        langManager.reload();

        // TODO: 다른 설정 파일들도 리로드

        sender.sendMessage(Component.text("설정 리로드 완료!", ColorUtil.SUCCESS));
        return true;
    }

    /**
     * 디버그 명령어 처리
     */
    private boolean handleDebugCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        boolean debugMode = LogUtil.toggleDebug();

        if (debugMode) {
            sender.sendMessage(Component.text("디버그 모드가 활성화되었습니다.", ColorUtil.SUCCESS));
        } else {
            sender.sendMessage(Component.text("디버그 모드가 비활성화되었습니다.", ColorUtil.WARNING));
        }

        return true;
    }

    /**
     * 프로필 보기 명령어 처리
     */
    private boolean handleViewProfileCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("사용법: /rpgadmin viewprofile <플레이어>", ColorUtil.ERROR));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("플레이어를 찾을 수 없습니다.", ColorUtil.ERROR));
            return true;
        }

        RPGPlayer rpgPlayer = playerManager.getPlayer(target);
        if (rpgPlayer == null) {
            sender.sendMessage(Component.text("RPG 플레이어 데이터를 찾을 수 없습니다.", ColorUtil.ERROR));
            return true;
        }

        // 프로필 정보 표시
        sender.sendMessage(Component.text("=== " + target.getName() + "의 프로필 ===", ColorUtil.GOLD));
        sender.sendMessage(Component.text("레벨: " + rpgPlayer.getLevel(), ColorUtil.WHITE));
        sender.sendMessage(Component.text("경험치: " + rpgPlayer.getExperience() + "/" + rpgPlayer.getExperienceToNextLevel(), ColorUtil.WHITE));
        sender.sendMessage(Component.text("직업: " + (rpgPlayer.getJob() != null ? rpgPlayer.getJob().getDisplayName() : "없음"), ColorUtil.WHITE));
        sender.sendMessage(Component.text("골드: " + rpgPlayer.getWallet().getGold(), ColorUtil.GOLD));
        sender.sendMessage(Component.text("다이아몬드: " + rpgPlayer.getWallet().getDiamond(), ColorUtil.AQUA));

        // 퀘스트 정보
        List<QuestProgress> activeQuests = questManager.getActiveQuests(target.getUniqueId());
        sender.sendMessage(Component.text("진행중인 퀘스트: " + activeQuests.size() + "개", ColorUtil.YELLOW));

        return true;
    }

    /**
     * 경험치 명령어 처리
     */
    private boolean handleExpCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 4 || !args[1].equalsIgnoreCase("give")) {
            sender.sendMessage(Component.text("사용법: /rpgadmin exp give <플레이어> <경험치>", ColorUtil.ERROR));
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
                sender.sendMessage(Component.text("경험치는 양수여야 합니다.", ColorUtil.ERROR));
                return true;
            }

            RPGPlayer rpgPlayer = playerManager.getPlayer(target);
            if (rpgPlayer == null) {
                sender.sendMessage(Component.text("RPG 플레이어 데이터를 찾을 수 없습니다.", ColorUtil.ERROR));
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
    private boolean handleLevelCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 4 || !args[1].equalsIgnoreCase("set")) {
            sender.sendMessage(Component.text("사용법: /rpgadmin level set <플레이어> <레벨>", ColorUtil.ERROR));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(Component.text("플레이어를 찾을 수 없습니다.", ColorUtil.ERROR));
            return true;
        }

        try {
            int level = Integer.parseInt(args[3]);
            if (level < 1 || level > 100) {
                sender.sendMessage(Component.text("레벨은 1-100 사이여야 합니다.", ColorUtil.ERROR));
                return true;
            }

            RPGPlayer rpgPlayer = playerManager.getPlayer(target);
            if (rpgPlayer == null) {
                sender.sendMessage(Component.text("RPG 플레이어 데이터를 찾을 수 없습니다.", ColorUtil.ERROR));
                return true;
            }

            // 레벨에 맞는 총 경험치 계산
            long totalExp = 0;
            for (int i = 1; i < level; i++) {
                totalExp += rpgPlayer.getExpForLevel(i);
            }

            rpgPlayer.setExperience(totalExp);
            sender.sendMessage(Component.text(target.getName() + "의 레벨을 " + level + "로 설정했습니다.", ColorUtil.SUCCESS));
            target.sendMessage(Component.text("레벨이 " + level + "로 설정되었습니다!", ColorUtil.GOLD));

        } catch (NumberFormatException e) {
            sender.sendMessage(Component.text("올바른 숫자를 입력해주세요.", ColorUtil.ERROR));
        }

        return true;
    }

    /**
     * 직업 명령어 처리
     */
    private boolean handleJobCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        // TODO: 직업 변경 구현
        sender.sendMessage(Component.text("직업 변경 기능은 아직 구현되지 않았습니다.", ColorUtil.WARNING));
        return true;
    }

    /**
     * NPC 명령어 처리
     */
    private boolean handleNpcCommand(@NotNull CommandSender sender, @NotNull String[] args) {
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
    private boolean handleQuestCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(Component.text("사용법: /rpgadmin quest <give|list|reload> [플레이어] [퀘스트ID]", ColorUtil.ERROR));
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
                sender.sendMessage(Component.text("사용법: /rpgadmin quest <give|list|reload>", ColorUtil.ERROR));
            }
        }

        return true;
    }

    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
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