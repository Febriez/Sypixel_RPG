package com.febrie.rpg.command.admin;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.command.admin.subcommand.AdminSubCommand;
import com.febrie.rpg.command.admin.subcommand.StatsSubCommand;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.job.JobType;
import com.febrie.rpg.npc.NPCTraitSetter;
import com.febrie.rpg.npc.trait.RPGQuestTrait;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.quest.registry.QuestRegistry;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.DateFormatUtil;
import com.febrie.rpg.util.LangManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * RPG 관리자 명령어 처리
 * 디버그, 통계, 플레이어 관리 등
 *
 * @author Febrie
 */
public class AdminCommands implements CommandExecutor, TabCompleter {

    private final RPGMain plugin;
    private final RPGPlayerManager playerManager;
    private final QuestManager questManager;
    private final IslandManager islandManager;
    private final Map<String, AdminSubCommand> subCommands;

    public AdminCommands(@NotNull RPGMain plugin, @NotNull RPGPlayerManager playerManager) {
        this.plugin = plugin;
        this.playerManager = playerManager;
        this.questManager = QuestManager.getInstance();
        this.islandManager = plugin.getIslandManager();
        this.subCommands = new HashMap<>();

        // 서브 커맨드 등록
        registerSubCommands();
    }

    private void registerSubCommands() {
        subCommands.put("stats", new StatsSubCommand(playerManager));
        // 다른 서브 커맨드들도 점진적으로 추가
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, String @NotNull [] args) {

        if (!sender.hasPermission("rpg.admin")) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.no-permission"));
            return true;
        }

        if (args.length == 0) {
            showUsage(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        // 서브 커맨드 시스템으로 처리 가능한 경우
        AdminSubCommand subCmd = subCommands.get(subCommand);
        if (subCmd != null) {
            if (subCmd.requiresPlayer()) {
                Player player = getPlayerFromSender(sender);
                if (player == null) {
                    return true;
                }
            }
            String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
            return subCmd.execute(sender, subArgs);
        }

        // 플레이어 전용 명령어 처리
        if (requiresPlayer(subCommand)) {
            Player player = getPlayerFromSender(sender);
            if (player == null) {
                return true;
            }
            return handlePlayerCommand(player, subCommand, args);
        }

        return switch (subCommand) {
            case "reload" -> handleReloadCommand(sender, args);
            case "viewprofile" -> handleViewProfileCommand(sender, args);
            case "exp" -> handleExpCommand(sender, args);
            case "level" -> handleLevelCommand(sender, args);
            case "job" -> handleJobCommand(sender, args);
            case "quest" -> handleQuestCommand(sender, args);
            default -> {
                showUsage(sender);
                yield true;
            }
        };
    }

    /**
     * 명령어가 플레이어 전용인지 확인
     */
    @Contract(pure = true)
    private boolean requiresPlayer(@NotNull String subCommand) {
        return subCommand.equals("npc");
    }

    /**
     * 플레이어 전용 명령어 처리
     */
    private boolean handlePlayerCommand(@NotNull Player player, @NotNull String subCommand, @NotNull String[] args) {
        return switch (subCommand) {
            case "npc" -> handleNpcCommand(player, args);
            case "island" -> handleIslandCommand(player, args);
            default -> false;
        };
    }

    /**
     * CommandSender에서 Player 가져오기
     */
    @Nullable
    private Player getPlayerFromSender(@NotNull CommandSender sender) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-only"));
            return null;
        }
        return player;
    }

    /**
     * 사용법 표시
     */
    private void showUsage(@NotNull CommandSender sender) {
        sendUsageMessages(sender, "commands.admin.usage.title", "commands.admin.usage.stats", "commands.admin.usage.reload", "commands.admin.usage.viewprofile", "commands.admin.usage.exp", "commands.admin.usage.level", "commands.admin.usage.job", "commands.admin.usage.npc", "commands.admin.usage.npc-setcode", "commands.admin.usage.npc-reward", "commands.admin.usage.quest", "commands.admin.usage.island");
    }

    /**
     * 여러 메시지 전송 헬퍼 메소드
     */
    private void sendUsageMessages(@NotNull CommandSender sender, String @NotNull ... keys) {
        for (String key : keys) {
            sender.sendMessage(LangManager.getComponent(sender, key));
        }
    }


    /**
     * 리로드 명령어 처리
     */
    private boolean handleReloadCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.reload.reloading"));

        // 언어 파일 리로드
        LangManager.reload();

        // 퀘스트 리로드
        questManager.reloadQuests();

        // 섬 설정 리로드 (추가 가능)

        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.reload.success"));
        return true;
    }


    /**
     * 프로필 보기 명령어 처리
     */
    private boolean handleViewProfileCommand(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length < 2) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.viewprofile.usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-not-found"));
            return true;
        }

        RPGPlayer rpgPlayer = playerManager.getPlayer(target);
        if (rpgPlayer == null) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-data-not-found"));
            return true;
        }

        // 프로필 정보 표시
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.viewprofile.title", "player", target.getName()));
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.viewprofile.level", "level", String.valueOf(rpgPlayer.getLevel())));
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.viewprofile.exp", "current", String.valueOf(rpgPlayer.getExperience()), "next", String.valueOf(rpgPlayer.getExperienceToNextLevel())));

        Component jobNameComp = rpgPlayer.getJob() != null ? Component.text(rpgPlayer.getJob().name()) : LangManager.getComponent(sender, "commands.admin.viewprofile.job-none");
        String jobName = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText().serialize(jobNameComp);
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.viewprofile.job", "job", jobName));

        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.viewprofile.gold", "amount", String.valueOf(rpgPlayer.getWallet().getBalance(CurrencyType.GOLD))));
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.viewprofile.diamond", "amount", String.valueOf(rpgPlayer.getWallet().getBalance(CurrencyType.DIAMOND))));

        // 퀘스트 정보
        java.util.Map<String, com.febrie.rpg.dto.quest.ActiveQuestDTO> activeQuests = questManager.getActiveQuests(target.getUniqueId());
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.viewprofile.active-quests", "count", String.valueOf(activeQuests.size())));

        return true;
    }

    /**
     * 경험치 명령어 처리
     */
    private boolean handleExpCommand(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length < 4 || !args[1].equalsIgnoreCase("give")) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.exp.usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-not-found"));
            return true;
        }

        try {
            int amount = Integer.parseInt(args[3]);
            if (amount <= 0) {
                sender.sendMessage(LangManager.getComponent(sender, "commands.admin.exp.must-positive"));
                return true;
            }

            RPGPlayer rpgPlayer = playerManager.getPlayer(target);
            if (rpgPlayer == null) {
                sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-data-not-found"));
                return true;
            }

            rpgPlayer.addExperience(amount);
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.exp.success", "player", target.getName(), "amount", String.valueOf(amount)));
            target.sendMessage(LangManager.getMessage(target, "commands.admin.exp.received", "amount", String.valueOf(amount)));

        } catch (NumberFormatException e) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.invalid-number"));
        }

        return true;
    }

    /**
     * 레벨 명령어 처리
     */
    private boolean handleLevelCommand(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length < 4 || !args[1].equalsIgnoreCase("set")) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.level.usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-not-found"));
            return true;
        }

        try {
            int level = Integer.parseInt(args[3]);
            if (level < 1 || level > 100) {
                sender.sendMessage(LangManager.getComponent(sender, "commands.admin.level.range"));
                return true;
            }

            RPGPlayer rpgPlayer = playerManager.getPlayer(target);
            if (rpgPlayer == null) {
                sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-data-not-found"));
                return true;
            }

            // 레벨에 맞는 총 경험치 계산
            long totalExp = 0;
            for (int i = 1; i < level; i++) {
                totalExp += rpgPlayer.getExpForLevel(i);
            }

            rpgPlayer.setExperience(totalExp);
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.level.success", "player", target.getName(), "level", String.valueOf(level)));
            target.sendMessage(LangManager.getMessage(target, "commands.admin.level.set", "level", String.valueOf(level)));

        } catch (NumberFormatException e) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.invalid-number"));
        }

        return true;
    }

    /**
     * 직업 명령어 처리
     */
    private boolean handleJobCommand(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length < 3) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.job.usage"));
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.job.available"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-not-found"));
            return true;
        }

        String jobName = args[2].toUpperCase();
        JobType newJob;

        try {
            newJob = JobType.valueOf(jobName);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.job.invalid", "job", jobName));
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.job.available"));
            return true;
        }

        RPGPlayer rpgPlayer = plugin.getRPGPlayerManager().getPlayer(target);
        if (rpgPlayer == null) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-data-not-found"));
            return true;
        }

        JobType currentJob = rpgPlayer.getJob();
        if (currentJob != null) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.job.already-has", "player", target.getName(), "job", currentJob.name()));
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.job.need-reset"));
            return true;
        }

        if (rpgPlayer.setJob(newJob)) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.job.success", "player", target.getName(), "job", newJob.name()));
            target.sendMessage(LangManager.getMessage(target, "commands.admin.job.set", "job", newJob.name()));
        } else {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.job.failed"));
        }

        return true;
    }

    /**
     * NPC 명령어 처리 (Citizens API 사용)
     */
    private boolean handleNpcCommand(@NotNull Player player, @NotNull String[] args) {
        if (!Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.citizens-not-found"));
            return true;
        }

        if (args.length < 2) {
            showNpcUsage(player);
            return true;
        }

        String subCmd = args[1].toLowerCase();

        switch (subCmd) {
            case "set" -> {
                if (args.length < 3) {
                    player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.set.usage"));
                    player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.set.example"));
                    return true;
                }

                // 퀘스트 ID 파싱
                try {
                    QuestID questId = QuestID.valueOf(args[2].toUpperCase());

                    // NPCTraitSetter를 통해 대기 상태로 설정
                    NPCTraitSetter.getInstance().prepareQuestTrait(player, questId);

                } catch (IllegalArgumentException e) {
                    player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.set.invalid-quest", "id", args[2]));
                    player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.set.available"));

                    // 모든 퀘스트 ID 나열
                    for (QuestID id : QuestID.values()) {
                        player.sendMessage(Component.text("  - " + id.name(), ColorUtil.GRAY));
                    }
                    return true;
                }
            }

            case "setcode" -> {
                if (args.length < 3) {
                    player.sendMessage(Component.text("사용법: /rpgadmin npc setcode <npcId>", ColorUtil.ERROR));
                    player.sendMessage(Component.text("예시: /rpgadmin npc setcode village_merchant", ColorUtil.GRAY));
                    return true;
                }

                String npcId = args[2];

                // NPC ID 유효성 검사 (영문 소문자와 언더스코어만 허용)
                if (!npcId.matches("^[a-z_]+$")) {
                    player.sendMessage(Component.text("NPC ID는 영문 소문자와 언더스코어만 사용할 수 있습니다.", ColorUtil.ERROR));
                    return true;
                }

                // Trait 등록 막대기 생성
                ItemStack traitItem = com.febrie.rpg.quest.trait.QuestTraitRegistrationItem.create(npcId, npcId);

                // 인벤토리가 가득 찬 경우 지급 중단
                if (player.getInventory().firstEmpty() == -1) {
                    player.sendMessage(Component.text("인벤토리가 가득 찼습니다! 공간을 확보한 후 다시 시도하세요.", ColorUtil.ERROR));
                    return true;
                }

                player.getInventory().addItem(traitItem);

                player.sendMessage(Component.text("NPC ID 설정기가 지급되었습니다.", ColorUtil.SUCCESS));
                player.sendMessage(Component.text("NPC ID: " + npcId, ColorUtil.YELLOW));
            }

            case "reward" -> {
                if (args.length < 3) {
                    player.sendMessage(Component.text("사용법: /rpgadmin npc reward <npcId>", ColorUtil.ERROR));
                    player.sendMessage(Component.text("예시: /rpgadmin npc reward quest_giver", ColorUtil.GRAY));
                    return true;
                }

                String rewardNpcId = args[2];

                // NPC ID 유효성 검사 (영문 소문자와 언더스코어만 허용)
                if (!rewardNpcId.matches("^[a-z_]+$")) {
                    player.sendMessage(Component.text("NPC ID는 영문 소문자와 언더스코어만 사용할 수 있습니다.", ColorUtil.ERROR));
                    return true;
                }

                // 보상 Trait 등록 막대기 생성
                ItemStack rewardItem = com.febrie.rpg.quest.trait.RewardTraitRegistrationItem.create(rewardNpcId, rewardNpcId);

                // 인벤토리가 가득 찬 경우 지급 중단
                if (player.getInventory().firstEmpty() == -1) {
                    player.sendMessage(Component.text("인벤토리가 가득 찼습니다! 공간을 확보한 후 다시 시도하세요.", ColorUtil.ERROR));
                    return true;
                }

                player.getInventory().addItem(rewardItem);

                player.sendMessage(Component.text("보상 NPC 설정기가 지급되었습니다.", ColorUtil.SUCCESS));
                player.sendMessage(Component.text("NPC ID: " + rewardNpcId, ColorUtil.YELLOW));
            }

            case "list" -> {
                player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.list.title"));
                int count = 0;
                for (NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
                    Component npcInfo = LangManager.getMessage(player, "commands.admin.npc.list.entry", "id", String.valueOf(npc.getId()), "name", npc.getName());

                    // RPGQuestTrait 확인
                    if (npc.hasTrait(RPGQuestTrait.class)) {
                        RPGQuestTrait trait = npc.getTraitNullable(RPGQuestTrait.class);
                        if (trait.hasNpcId()) {
                            npcInfo = npcInfo.append(LangManager.getMessage(player, "commands.admin.npc.list.id", "id", trait.getNpcId()));
                        }
                        if (!trait.getQuestIds().isEmpty()) {
                            npcInfo = npcInfo.append(LangManager.getMessage(player, "commands.admin.npc.list.quests", "count", String.valueOf(trait.getQuestIds().size())));
                        }
                    }

                    player.sendMessage(npcInfo);
                    count++;
                }
                player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.list.total", "count", String.valueOf(count)));
            }

            case "quest" -> {
                if (args.length < 3) {
                    player.sendMessage(Component.text("사용법: /rpgadmin npc quest <questId>", ColorUtil.ERROR));
                    player.sendMessage(Component.text("예시: /rpgadmin npc quest FIRST_STEPS", ColorUtil.GRAY));
                    return true;
                }

                // 퀘스트 ID 파싱
                QuestID questId;
                try {
                    questId = QuestID.valueOf(args[2].toUpperCase());
                } catch (IllegalArgumentException e) {
                    player.sendMessage(Component.text("잘못된 퀘스트 ID: " + args[2], ColorUtil.ERROR));
                    return true;
                }

                // 퀘스트 확인
                com.febrie.rpg.quest.Quest quest = questManager.getQuest(questId);
                if (quest == null) {
                    player.sendMessage(Component.text("구현되지 않은 퀘스트입니다: " + questId.name(), ColorUtil.ERROR));
                    return true;
                }

                String displayName = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                        .serialize(quest.getDisplayName(player));

                ItemStack questItem = com.febrie.rpg.quest.trait.QuestStartTraitRegistrationItem.create(questId, displayName);

                if (player.getInventory().firstEmpty() == -1) {
                    player.sendMessage(Component.text("인벤토리가 가득 찼습니다! 공간을 확보한 후 다시 시도하세요.", ColorUtil.ERROR));
                    return true;
                }

                player.getInventory().addItem(questItem);

                player.sendMessage(Component.text("퀘스트 시작 NPC 설정기가 지급되었습니다.", ColorUtil.SUCCESS));
                player.sendMessage(Component.text("퀘스트 ID: " + questId.name(), ColorUtil.YELLOW));
            }

            case "shop" -> {
                if (args.length < 3) {
                    player.sendMessage(Component.text("사용법: /rpgadmin npc shop <타입>", ColorUtil.ERROR));
                    player.sendMessage(Component.text("예시: /rpgadmin npc shop weapons", ColorUtil.GRAY));
                    return true;
                }

                String shopType = args[2];

                ItemStack shopItem = com.febrie.rpg.npc.trait.ShopTraitRegistrationItem.create(shopType, shopType);

                if (player.getInventory().firstEmpty() == -1) {
                    player.sendMessage(Component.text("인벤토리가 가득 찼습니다! 공간을 확보한 후 다시 시도하세요.", ColorUtil.ERROR));
                    return true;
                }

                player.getInventory().addItem(shopItem);

                player.sendMessage(Component.text("상점 NPC 설정기가 지급되었습니다.", ColorUtil.SUCCESS));
                player.sendMessage(Component.text("상점 타입: " + shopType, ColorUtil.YELLOW));
            }

            case "guide" -> {
                if (args.length < 3) {
                    player.sendMessage(Component.text("사용법: /rpgadmin npc guide <타입>", ColorUtil.ERROR));
                    player.sendMessage(Component.text("예시: /rpgadmin npc guide main", ColorUtil.GRAY));
                    return true;
                }

                String guideType = args[2];

                ItemStack guideItem = com.febrie.rpg.npc.trait.GuideTraitRegistrationItem.create(guideType, guideType);

                if (player.getInventory().firstEmpty() == -1) {
                    player.sendMessage(Component.text("인벤토리가 가득 찼습니다! 공간을 확보한 후 다시 시도하세요.", ColorUtil.ERROR));
                    return true;
                }

                player.getInventory().addItem(guideItem);

                player.sendMessage(Component.text("가이드 NPC 설정기가 지급되었습니다.", ColorUtil.SUCCESS));
                player.sendMessage(Component.text("가이드 타입: " + guideType, ColorUtil.YELLOW));
            }

            case "dialog" -> {
                if (args.length < 3) {
                    player.sendMessage(Component.text("사용법: /rpgadmin npc dialog <대화ID>", ColorUtil.ERROR));
                    player.sendMessage(Component.text("예시: /rpgadmin npc dialog village_greeting", ColorUtil.GRAY));
                    return true;
                }

                String dialogId = args[2];

                // ID 유효성 검사 (영문 소문자와 언더스코어만 허용)
                if (!dialogId.matches("^[a-z_]+$")) {
                    player.sendMessage(Component.text("대화 ID는 영문 소문자와 언더스코어만 사용할 수 있습니다.", ColorUtil.ERROR));
                    return true;
                }

                ItemStack dialogItem = com.febrie.rpg.npc.trait.DialogTraitRegistrationItem.create(dialogId, dialogId);

                if (player.getInventory().firstEmpty() == -1) {
                    player.sendMessage(Component.text("인벤토리가 가득 찼습니다! 공간을 확보한 후 다시 시도하세요.", ColorUtil.ERROR));
                    return true;
                }

                player.getInventory().addItem(dialogItem);

                player.sendMessage(Component.text("대화 NPC 설정기가 지급되었습니다.", ColorUtil.SUCCESS));
                player.sendMessage(Component.text("대화 ID: " + dialogId, ColorUtil.YELLOW));
            }

            case "questall" -> {
                if (args.length < 3) {
                    player.sendMessage(Component.text("사용법: /rpgadmin npc questall <questId>", ColorUtil.ERROR));
                    player.sendMessage(Component.text("예시: /rpgadmin npc questall FIRST_STEPS", ColorUtil.GRAY));
                    return true;
                }

                // 퀘스트 ID 파싱
                QuestID questId;
                try {
                    questId = QuestID.valueOf(args[2].toUpperCase());
                } catch (IllegalArgumentException e) {
                    player.sendMessage(Component.text("잘못된 퀘스트 ID: " + args[2], ColorUtil.ERROR));
                    return true;
                }

                // 퀘스트 확인
                com.febrie.rpg.quest.Quest quest = questManager.getQuest(questId);
                if (quest == null) {
                    player.sendMessage(Component.text("구현되지 않은 퀘스트입니다: " + questId.name(), ColorUtil.ERROR));
                    return true;
                }

                String displayName = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                        .serialize(quest.getDisplayName(player));
                String npcId = questId.name(); // 대문자 ID 사용

                // 인벤토리 공간 확인 (3개 필요)
                int emptySlots = 0;
                for (ItemStack item : player.getInventory().getStorageContents()) {
                    if (item == null || item.getType() == Material.AIR) {
                        emptySlots++;
                    }
                }

                if (emptySlots < 3) {
                    player.sendMessage(Component.text("인벤토리에 최소 3칸의 빈 공간이 필요합니다!", ColorUtil.ERROR));
                    player.sendMessage(Component.text("현재 빈 공간: " + emptySlots + "칸", ColorUtil.GRAY));
                    return true;
                }

                // 3개의 막대기 생성 및 지급
                ItemStack questItem = com.febrie.rpg.quest.trait.QuestStartTraitRegistrationItem.create(questId, displayName);
                ItemStack setcodeItem = com.febrie.rpg.quest.trait.QuestTraitRegistrationItem.create(npcId, npcId);
                ItemStack rewardItem = com.febrie.rpg.quest.trait.RewardTraitRegistrationItem.create(npcId, npcId);

                player.getInventory().addItem(questItem);
                player.getInventory().addItem(setcodeItem);
                player.getInventory().addItem(rewardItem);

                player.sendMessage(Component.text("퀘스트 관련 NPC 설정기 3개가 지급되었습니다:", ColorUtil.SUCCESS));
                player.sendMessage(Component.text("- 퀘스트 시작 NPC 설정기", ColorUtil.GRAY));
                player.sendMessage(Component.text("- NPC ID 설정기 (" + npcId + ")", ColorUtil.GRAY));
                player.sendMessage(Component.text("- 보상 NPC 설정기 (" + npcId + ")", ColorUtil.GRAY));
                player.sendMessage(Component.text("퀘스트: " + questId.name() + " - " + displayName, ColorUtil.YELLOW));
            }

            default -> showNpcUsage(player);
        }

        return true;
    }

    /**
     * 퀘스트 명령어 처리
     */
    private boolean handleQuestCommand(@NotNull CommandSender sender, @NotNull String @NotNull [] args) {
        if (args.length < 2) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.usage"));
            return true;
        }

        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "give" -> {
                if (args.length < 4) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.give.usage"));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-not-found"));
                    return true;
                }

                // QuestID 파싱
                QuestID questId;
                try {
                    questId = QuestID.valueOf(args[3].toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.give.invalid-id", "id", args[3]));
                    return true;
                }

                // 퀘스트 시작
                if (questManager.startQuest(target, questId)) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.give.success", "id", questId.name()));
                    target.sendMessage(LangManager.getMessage(target, "commands.admin.quest.give.received", "id", questId.name()));
                } else {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.give.failed"));
                }
            }

            case "list" -> {
                sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.list.title"));

                for (QuestCategory category : QuestCategory.values()) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.list.category", "category", category.name()));

                    QuestID[] questIds = QuestID.getByCategory(category);
                    for (QuestID id : questIds) {
                        boolean implemented = QuestRegistry.isImplemented(id);
                        Component status = implemented ? LangManager.getComponent(sender, "commands.admin.quest.list.implemented") : LangManager.getComponent(sender, "commands.admin.quest.list.not-implemented");

                        sender.sendMessage(Component.text("  - " + id.name() + " (" + id.name() + ")").append(status));
                    }
                }
            }

            case "reload" -> {
                // 퀘스트 데이터 리로드 (언어 파일 등)
                LangManager.reload();
                sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.reload.success"));
            }

            default -> {
                sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.usage"));
            }
        }

        return true;
    }

    /**
     * NPC 사용법 표시
     */
    private void showNpcUsage(@NotNull CommandSender sender) {
        sender.sendMessage(Component.text("=== NPC 관리 명령어 ===", ColorUtil.GOLD));
        sender.sendMessage(Component.text("/rpgadmin npc set <questId>", ColorUtil.YELLOW).append(Component.text(" - NPC에 퀘스트 설정 (10초내 우클릭)", ColorUtil.GRAY)));
        sender.sendMessage(Component.text("/rpgadmin npc setcode <npcId>", ColorUtil.YELLOW).append(Component.text(" - NPC ID 설정 막대기", ColorUtil.GRAY)));
        sender.sendMessage(Component.text("/rpgadmin npc reward <npcId>", ColorUtil.YELLOW).append(Component.text(" - 보상 NPC 설정 막대기", ColorUtil.GRAY)));
        sender.sendMessage(Component.text("/rpgadmin npc quest <questId>", ColorUtil.YELLOW).append(Component.text(" - 퀘스트 시작 NPC 막대기", ColorUtil.GRAY)));
        sender.sendMessage(Component.text("/rpgadmin npc questall <questId>", ColorUtil.YELLOW).append(Component.text(" - 퀘스트 관련 막대기 3개 한번에", ColorUtil.GRAY)));
        sender.sendMessage(Component.text("/rpgadmin npc shop <타입>", ColorUtil.YELLOW).append(Component.text(" - 상점 NPC 막대기", ColorUtil.GRAY)));
        sender.sendMessage(Component.text("/rpgadmin npc guide <타입>", ColorUtil.YELLOW).append(Component.text(" - 가이드 NPC 막대기", ColorUtil.GRAY)));
        sender.sendMessage(Component.text("/rpgadmin npc dialog <대화ID>", ColorUtil.YELLOW).append(Component.text(" - 대화 NPC 막대기", ColorUtil.GRAY)));
        sender.sendMessage(Component.text("/rpgadmin npc list", ColorUtil.YELLOW).append(Component.text(" - NPC 목록 보기", ColorUtil.GRAY)));
    }

    /**
     * 섬 사용법 표시
     */
    private void showIslandUsage(@NotNull CommandSender sender) {
        sendUsageMessages(sender, "commands.admin.island.usage.title", "commands.admin.island.usage.info", "commands.admin.island.usage.delete", "commands.admin.island.usage.reset", "commands.admin.island.usage.tp");
    }

    /**
     * 섬 관리 명령어 처리 - CommandSender 버전
     */
    private boolean handleIslandCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            showIslandUsage(sender);
            return true;
        }

        String subCmd = args[1].toLowerCase();

        // tp 명령어는 플레이어 전용
        if (subCmd.equals("tp")) {
            Player player = getPlayerFromSender(sender);
            if (player == null) {
                return true;
            }
            return handleIslandCommand(player, args);
        }

        // 나머지 명령어는 CommandSender로 처리
        return handleIslandCommandInternal(sender, args);
    }

    /**
     * 섬 관리 명령어 처리 - Player 버전
     */
    private boolean handleIslandCommand(@NotNull Player player, @NotNull String[] args) {
        if (args.length < 2) {
            showIslandUsage(player);
            return true;
        }
        return handleIslandCommandInternal(player, args);
    }

    /**
     * 섬 관리 명령어 내부 처리
     *
     * @return 처리 결과 (false: 명령어 사용법 표시 필요)
     */
    private boolean handleIslandCommandInternal(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            showIslandUsage(sender);
            return true;
        }

        String subCmd = args[1].toLowerCase();
        if (args.length < 3) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.player-required"));
            return true;
        }

        String targetName = args[2];
        Player target = Bukkit.getPlayer(targetName);

        if (target == null) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-not-found"));
            return true;
        }

        String targetUuid = target.getUniqueId().toString();

        switch (subCmd) {
            case "info" -> {
                islandManager.getPlayerIsland(targetUuid, targetName).thenAccept(island -> {
                    if (island == null) {
                        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.no-island", "player", targetName));
                        return;
                    }

                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.info.title", "player", targetName));
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.info.id", "id", island.getId()));
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.info.name", "name", island.getName()));
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.info.owner", "owner", island.getOwnerName()));
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.info.size", "size", String.valueOf(island.getSize())));
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.info.members", "current", String.valueOf(island.getData().members().size() + 1), "max", String.valueOf(island.getData().upgradeData().memberLimit() + 1)));
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.info.created", "date", DateFormatUtil.formatFullDateTimeFromMillis(island.getData().createdAt()) + ":" + String.format("%02d", java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(island.getData().createdAt()), java.time.ZoneId.systemDefault()).getSecond())));
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.info.resets", "count", String.valueOf(island.getData().totalResets())));
                });
            }

            case "delete" -> {
                islandManager.getPlayerIsland(targetUuid, targetName).thenAccept(island -> {
                    if (island == null) {
                        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.no-island", "player", targetName));
                        return;
                    }

                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.delete.deleting"));

                    islandManager.deleteIsland(island.getId()).thenAccept(success -> {
                        if (success) {
                            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.delete.success", "player", targetName));

                            // 섬장이 온라인인 경우 스폰으로 이동
                            if (target.isOnline()) {
                                plugin.getServer().getScheduler().runTask(plugin, () -> {
                                    if (!plugin.getServer().getWorlds().isEmpty()) {
                                        target.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
                                        target.sendMessage(LangManager.getMessage(target, "commands.admin.island.delete.owner-notify"));
                                    }
                                });
                            }
                        } else {
                            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.delete.failed"));
                        }
                    });
                });
            }

            case "reset" -> {
                islandManager.getPlayerIsland(targetUuid, targetName).thenAccept(island -> {
                    if (island == null) {
                        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.no-island", "player", targetName));
                        return;
                    }

                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.reset.resetting"));

                    islandManager.resetIsland(island.getId()).thenAccept(success -> {
                        if (success) {
                            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.reset.success", "player", targetName));

                            // 섬장이 온라인인 경우 알림
                            if (target.isOnline()) {
                                target.sendMessage(LangManager.getMessage(target, "commands.admin.island.reset.owner-notify"));
                            }
                        } else {
                            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.reset.failed"));
                        }
                    });
                });
            }

            case "tp" -> {
                // Player 타입이 보장됨
                Player admin = (Player) sender;
                islandManager.getPlayerIsland(targetUuid, targetName).thenAccept(island -> {
                    if (island == null) {
                        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.no-island", "player", targetName));
                        return;
                    }

                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        World islandWorld = islandManager.getWorldManager().getIslandWorld();
                        if (islandWorld == null) {
                            admin.sendMessage(LangManager.getMessage(admin, "commands.admin.island.tp.world-not-found"));
                            return;
                        }

                        Location tpLocation = island.getSpawnLocation();
                        admin.teleport(tpLocation);
                        admin.sendMessage(LangManager.getMessage(admin, "commands.admin.island.tp.success", "player", targetName));
                    });
                });
            }

            default -> {
                sender.sendMessage(LangManager.getComponent(sender, "commands.admin.island.unknown-command"));
                return false;
            }
        }

        return true;
    }


    @Override
    @Nullable
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        if (!sender.hasPermission("rpg.admin")) {
            return new ArrayList<>();
        }

        if (args.length == 1) {
            List<String> commands = new ArrayList<>(subCommands.keySet());
            commands.addAll(Arrays.asList("reload", "viewprofile", "exp", "level", "job", "npc", "quest", "island"));
            return commands.stream().filter(s -> s.startsWith(args[0].toLowerCase())).sorted().toList();
        }

        // 서브 커맨드 시스템의 탭 완성 처리
        if (args.length >= 2) {
            String subCommand = args[0].toLowerCase();
            AdminSubCommand subCmd = subCommands.get(subCommand);
            if (subCmd != null) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                List<String> completions = subCmd.tabComplete(sender, subArgs);
                if (completions != null) {
                    return completions;
                }
            }
        }

        if (args.length == 2) {
            switch (args[0].toLowerCase()) {
                case "viewprofile" -> {
                    return getOnlinePlayerSuggestions(args[1]);
                }
                case "exp" -> {
                    return List.of("give");
                }
                case "level", "job" -> {
                    return List.of("set");
                }
                case "npc" -> {
                    return Arrays.asList("set", "setcode", "reward", "quest", "questall", "shop", "guide", "dialog", "list");
                }
                case "quest" -> {
                    return Arrays.asList("give", "list", "reload");
                }
                case "island" -> {
                    return Arrays.asList("info", "delete", "reset", "tp");
                }
            }
        }

        if (args.length == 3) {
            switch (args[0].toLowerCase()) {
                case "exp", "level", "job" -> {
                    if (args[1].equalsIgnoreCase("give") || args[1].equalsIgnoreCase("set")) {
                        return getOnlinePlayerSuggestions(args[2]);
                    }
                }
                case "npc" -> {
                    switch (args[1].toLowerCase()) {
                        case "set", "quest", "questall" -> {
                            return getQuestIdSuggestions(args[2]);
                        }
                        case "reward", "setcode" -> {
                            // Quest ID 대문자로 제안
                            return getQuestIdSuggestions(args[2]);
                        }
                        case "shop", "guide", "dialog" -> {
                            // 타입별 제안 가능 (현재는 자유 입력)
                            return List.of();
                        }
                    }
                }
                case "quest" -> {
                    if (args[1].equalsIgnoreCase("give")) {
                        return getOnlinePlayerSuggestions(args[2]);
                    }
                }
                case "island" -> {
                    if (List.of("info", "delete", "reset", "tp").contains(args[1].toLowerCase())) {
                        return getOnlinePlayerSuggestions(args[2]);
                    }
                }
            }
        }

        if (args.length == 4) {
            if (args[0].equalsIgnoreCase("quest") && args[1].equalsIgnoreCase("give")) {
                return getQuestIdSuggestions(args[3]);
            }
            // NPC create QUEST 뒤에 퀘스트 ID 제안
            if (args[0].equalsIgnoreCase("npc") && args[1].equalsIgnoreCase("create") && args[2].equalsIgnoreCase("QUEST")) {
                return getQuestIdSuggestions(args[3]);
            }
        }

        return new ArrayList<>();
    }

    /**
     * 온라인 플레이어 이름 제안
     */
    private @NotNull List<String> getOnlinePlayerSuggestions(@NotNull String partial) {
        String lowerPartial = partial.toLowerCase();
        return Bukkit.getOnlinePlayers().stream().map(Player::getName).filter(name -> name.toLowerCase().startsWith(lowerPartial)).sorted().toList();
    }

    /**
     * 탭 완성 - 퀘스트 ID
     */
    private @NotNull List<String> getQuestIdSuggestions(@NotNull String partial) {
        String upperPartial = partial.toUpperCase();
        return Arrays.stream(QuestID.values()).map(QuestID::name).filter(name -> name.startsWith(upperPartial)).toList();
    }
}