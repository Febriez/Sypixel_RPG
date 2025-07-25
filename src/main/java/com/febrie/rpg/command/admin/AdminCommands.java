package com.febrie.rpg.command.admin;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.npc.trait.RPGGuideTrait;
import com.febrie.rpg.npc.trait.RPGQuestTrait;
import com.febrie.rpg.npc.trait.RPGShopTrait;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.job.JobType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.quest.registry.QuestRegistry;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LogUtil;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.IslandLocationDTO;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import com.febrie.rpg.util.DateFormatUtil;

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
    private final QuestManager questManager;
    private final IslandManager islandManager;

    public AdminCommands(@NotNull RPGMain plugin, @NotNull RPGPlayerManager playerManager,
                         @NotNull GuiManager guiManager, @NotNull LangManager langManager) {
        this.plugin = plugin;
        this.playerManager = playerManager;
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.questManager = QuestManager.getInstance();
        this.islandManager = plugin.getIslandManager();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {

        if (!sender.hasPermission("rpg.admin")) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.no-permission"));
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
            case "viewprofile" -> handleViewProfileCommand(sender, args);
            case "exp" -> handleExpCommand(sender, args);
            case "level" -> handleLevelCommand(sender, args);
            case "job" -> handleJobCommand(sender, args);
            case "npc" -> handleNpcCommand(sender, args);
            case "quest" -> handleQuestCommand(sender, args);
            case "island" -> handleIslandCommand(sender, args);
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
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.usage.title"));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.usage.stats"));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.usage.reload"));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.usage.viewprofile"));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.usage.exp"));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.usage.level"));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.usage.job"));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.usage.npc"));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.usage.npc-setcode"));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.usage.npc-reward"));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.usage.quest"));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.usage.island"));
    }

    /**
     * 통계 명령어 처리
     */
    private boolean handleStatsCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.stats.title"));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.stats.online-players",
                "count", String.valueOf(Bukkit.getOnlinePlayers().size())));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.stats.loaded-players",
                "count", String.valueOf(playerManager.getAllPlayers().size())));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.stats.registered-quests",
                "count", String.valueOf(QuestID.values().length)));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.stats.implemented-quests",
                "count", String.valueOf(QuestRegistry.getImplementedCount())));

        // 메모리 사용량
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.stats.memory-usage",
                "used", String.valueOf(usedMemory),
                "max", String.valueOf(maxMemory)));

        return true;
    }

    /**
     * 리로드 명령어 처리
     */
    private boolean handleReloadCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.reload.reloading"));

        // 언어 파일 리로드
        langManager.reload();
        
        // 퀘스트 리로드
        questManager.reloadQuests();
        
        // 섬 설정 리로드 (추가 가능)

        sender.sendMessage(langManager.getComponent(sender, "commands.admin.reload.success"));
        return true;
    }


    /**
     * 프로필 보기 명령어 처리
     */
    private boolean handleViewProfileCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.viewprofile.usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.player-not-found"));
            return true;
        }

        RPGPlayer rpgPlayer = playerManager.getPlayer(target);
        if (rpgPlayer == null) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.player-data-not-found"));
            return true;
        }

        // 프로필 정보 표시
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.viewprofile.title",
                "player", target.getName()));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.viewprofile.level",
                "level", String.valueOf(rpgPlayer.getLevel())));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.viewprofile.exp",
                "current", String.valueOf(rpgPlayer.getExperience()),
                "next", String.valueOf(rpgPlayer.getExperienceToNextLevel())));
        
        String jobName = rpgPlayer.getJob() != null ? rpgPlayer.getJob().name() : 
                langManager.getMessage(sender, "commands.admin.viewprofile.job-none");
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.viewprofile.job",
                "job", jobName));
        
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.viewprofile.gold",
                "amount", String.valueOf(rpgPlayer.getWallet().getBalance(CurrencyType.GOLD))));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.viewprofile.diamond",
                "amount", String.valueOf(rpgPlayer.getWallet().getBalance(CurrencyType.DIAMOND))));

        // 퀘스트 정보
        List<QuestProgress> activeQuests = questManager.getActiveQuests(target.getUniqueId());
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.viewprofile.active-quests",
                "count", String.valueOf(activeQuests.size())));

        return true;
    }

    /**
     * 경험치 명령어 처리
     */
    private boolean handleExpCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 4 || !args[1].equalsIgnoreCase("give")) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.exp.usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.player-not-found"));
            return true;
        }

        try {
            int amount = Integer.parseInt(args[3]);
            if (amount <= 0) {
                sender.sendMessage(langManager.getComponent(sender, "commands.admin.exp.must-positive"));
                return true;
            }

            RPGPlayer rpgPlayer = playerManager.getPlayer(target);
            if (rpgPlayer == null) {
                sender.sendMessage(langManager.getComponent(sender, "commands.admin.player-data-not-found"));
                return true;
            }

            rpgPlayer.addExperience(amount);
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.exp.success",
                    "player", target.getName(),
                    "amount", String.valueOf(amount)));
            target.sendMessage(langManager.getComponent(target, "commands.admin.exp.received",
                    "amount", String.valueOf(amount)));

        } catch (NumberFormatException e) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.invalid-number"));
        }

        return true;
    }

    /**
     * 레벨 명령어 처리
     */
    private boolean handleLevelCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 4 || !args[1].equalsIgnoreCase("set")) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.level.usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[2]);
        if (target == null) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.player-not-found"));
            return true;
        }

        try {
            int level = Integer.parseInt(args[3]);
            if (level < 1 || level > 100) {
                sender.sendMessage(langManager.getComponent(sender, "commands.admin.level.range"));
                return true;
            }

            RPGPlayer rpgPlayer = playerManager.getPlayer(target);
            if (rpgPlayer == null) {
                sender.sendMessage(langManager.getComponent(sender, "commands.admin.player-data-not-found"));
                return true;
            }

            // 레벨에 맞는 총 경험치 계산
            long totalExp = 0;
            for (int i = 1; i < level; i++) {
                totalExp += rpgPlayer.getExpForLevel(i);
            }

            rpgPlayer.setExperience(totalExp);
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.level.success",
                    "player", target.getName(),
                    "level", String.valueOf(level)));
            target.sendMessage(langManager.getComponent(target, "commands.admin.level.set",
                    "level", String.valueOf(level)));

        } catch (NumberFormatException e) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.invalid-number"));
        }

        return true;
    }

    /**
     * 직업 명령어 처리
     */
    private boolean handleJobCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 3) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.job.usage"));
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.job.available"));
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.player-not-found"));
            return true;
        }
        
        String jobName = args[2].toUpperCase();
        JobType newJob;
        
        try {
            newJob = JobType.valueOf(jobName);
        } catch (IllegalArgumentException e) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.job.invalid",
                    "job", jobName));
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.job.available"));
            return true;
        }
        
        RPGPlayer rpgPlayer = plugin.getRPGPlayerManager().getPlayer(target);
        if (rpgPlayer == null) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.player-data-not-found"));
            return true;
        }
        
        JobType currentJob = rpgPlayer.getJob();
        if (currentJob != null) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.job.already-has",
                    "player", target.getName(),
                    "job", currentJob.name()));
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.job.need-reset"));
            return true;
        }
        
        if (rpgPlayer.setJob(newJob)) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.job.success",
                    "player", target.getName(),
                    "job", newJob.name()));
            target.sendMessage(langManager.getComponent(target, "commands.admin.job.set",
                    "job", newJob.name()));
        } else {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.job.failed"));
        }
        
        return true;
    }

    /**
     * NPC 명령어 처리 (Citizens API 사용)
     */
    private boolean handleNpcCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.player-only"));
            return true;
        }

        if (!Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.citizens-not-found"));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.usage.title"));
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.usage.set"));
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.usage.setcode"));
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.usage.reward"));
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.usage.list"));
            return true;
        }

        String subCmd = args[1].toLowerCase();

        switch (subCmd) {
            case "set" -> {
                if (args.length < 3) {
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.set.usage"));
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.set.example"));
                    return true;
                }

                // 퀘스트 ID 파싱
                try {
                    QuestID questId = QuestID.valueOf(args[2].toUpperCase());
                    
                    // NPCTraitSetter를 통해 대기 상태로 설정
                    com.febrie.rpg.npc.NPCTraitSetter.getInstance().prepareQuestTrait(player, questId);
                    
                } catch (IllegalArgumentException e) {
                    player.sendMessage(langManager.getComponent(player, "commands.admin.npc.set.invalid-quest", "id", args[2]));
                    player.sendMessage(langManager.getComponent(player, "commands.admin.npc.set.available"));
                    
                    // 모든 퀘스트 ID 나열
                    for (QuestID id : QuestID.values()) {
                        player.sendMessage(Component.text("  - " + id.name(), ColorUtil.GRAY));
                    }
                    return true;
                }
            }
            
            case "setcode" -> {
                // player 변수가 이미 정의되어 있으므로 타입 체크만
                if (!(sender instanceof Player)) {
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.player-only"));
                    return true;
                }
                
                if (args.length < 3) {
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.setcode.usage"));
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.setcode.example"));
                    return true;
                }
                
                String npcId = args[2];
                
                // NPC ID 유효성 검사 (영문 소문자와 언더스코어만 허용)
                if (!npcId.matches("^[a-z_]+$")) {
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.setcode.invalid-format"));
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.setcode.format-example"));
                    return true;
                }
                
                String displayName = args.length > 3 ? String.join(" ", Arrays.copyOfRange(args, 3, args.length)) : npcId;
                
                // Trait 등록 막대기 생성
                ItemStack traitItem = com.febrie.rpg.quest.trait.QuestTraitRegistrationItem.createRegistrationItem(npcId, displayName);
                
                // 인벤토리가 가득 찬 경우 바닥에 드롭
                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItem(player.getLocation(), traitItem);
                    player.sendMessage(langManager.getComponent(player, "commands.admin.npc.setcode.inventory-full"));
                } else {
                    player.getInventory().addItem(traitItem);
                }
                
                player.sendMessage(langManager.getComponent(player, "commands.admin.npc.setcode.success"));
                player.sendMessage(langManager.getComponent(player, "commands.admin.npc.setcode.npc-id", "id", npcId));
                player.sendMessage(langManager.getComponent(player, "commands.admin.npc.setcode.display-name", "name", displayName));
            }
            
            case "reward" -> {
                // player 변수가 이미 정의되어 있으므로 타입 체크만
                if (!(sender instanceof Player)) {
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.player-only"));
                    return true;
                }
                
                if (args.length < 3) {
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.reward.usage"));
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.reward.example"));
                    return true;
                }
                
                String rewardNpcId = args[2];
                
                // NPC ID 유효성 검사 (영문 소문자와 언더스코어만 허용)
                if (!rewardNpcId.matches("^[a-z_]+$")) {
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.reward.invalid-format"));
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.reward.format-example"));
                    return true;
                }
                
                String displayName = args.length > 3 ? String.join(" ", Arrays.copyOfRange(args, 3, args.length)) : rewardNpcId;
                
                // 보상 Trait 등록 막대기 생성
                ItemStack rewardItem = com.febrie.rpg.quest.trait.RewardTraitRegistrationItem.createRegistrationItem(rewardNpcId, displayName);
                
                // 인벤토리가 가득 찬 경우 바닥에 드롭
                if (player.getInventory().firstEmpty() == -1) {
                    player.getWorld().dropItem(player.getLocation(), rewardItem);
                    player.sendMessage(langManager.getComponent(player, "commands.admin.npc.reward.inventory-full"));
                } else {
                    player.getInventory().addItem(rewardItem);
                }
                
                player.sendMessage(langManager.getComponent(player, "commands.admin.npc.reward.success"));
                player.sendMessage(langManager.getComponent(player, "commands.admin.npc.reward.npc-id", "id", rewardNpcId));
                player.sendMessage(langManager.getComponent(player, "commands.admin.npc.reward.display-name", "name", displayName));
            }
            
            case "list" -> {
                sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.list.title"));
                int count = 0;
                for (NPC npc : CitizensAPI.getNPCRegistry().sorted()) {
                    Component npcInfo = langManager.getComponent(sender, "commands.admin.npc.list.entry", 
                            "id", String.valueOf(npc.getId()), 
                            "name", npc.getName());
                    
                    // RPGQuestTrait 확인
                    if (npc.hasTrait(RPGQuestTrait.class)) {
                        RPGQuestTrait trait = npc.getTraitNullable(RPGQuestTrait.class);
                        if (trait.hasNpcId()) {
                            npcInfo = npcInfo.append(langManager.getComponent(sender, "commands.admin.npc.list.id", "id", trait.getNpcId()));
                        }
                        if (!trait.getQuestIds().isEmpty()) {
                            npcInfo = npcInfo.append(langManager.getComponent(sender, "commands.admin.npc.list.quests", "count", String.valueOf(trait.getQuestIds().size())));
                        }
                    }
                    
                    sender.sendMessage(npcInfo);
                    count++;
                }
                sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.list.total", "count", String.valueOf(count)));
            }
            
            default -> {
                sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.usage.title"));
                sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.usage.set"));
                sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.usage.setcode"));
                sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.usage.reward"));
                sender.sendMessage(langManager.getComponent(sender, "commands.admin.npc.usage.list"));
            }
        }

        return true;
    }

    /**
     * 퀘스트 명령어 처리
     */
    private boolean handleQuestCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.quest.usage"));
            return true;
        }

        String subCommand = args[1].toLowerCase();

        switch (subCommand) {
            case "give" -> {
                if (args.length < 4) {
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.quest.give.usage"));
                    return true;
                }

                Player target = Bukkit.getPlayer(args[2]);
                if (target == null) {
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.player-not-found"));
                    return true;
                }

                // QuestID 파싱
                QuestID questId;
                try {
                    questId = QuestID.valueOf(args[3].toUpperCase());
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.quest.give.invalid-id", "id", args[3]));
                    return true;
                }

                // 퀘스트 시작
                if (questManager.startQuest(target, questId)) {
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.quest.give.success", "id", questId.name()));
                    target.sendMessage(langManager.getComponent(target, "commands.admin.quest.give.received", "id", questId.name()));
                } else {
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.quest.give.failed"));
                }
            }

            case "list" -> {
                sender.sendMessage(langManager.getComponent(sender, "commands.admin.quest.list.title"));

                for (QuestCategory category : QuestCategory.values()) {
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.quest.list.category", "category", category.name()));

                    QuestID[] questIds = QuestID.getByCategory(category);
                    for (QuestID id : questIds) {
                        boolean implemented = QuestRegistry.isImplemented(id);
                        Component status = implemented
                                ? langManager.getComponent(sender, "commands.admin.quest.list.implemented")
                                : langManager.getComponent(sender, "commands.admin.quest.list.not-implemented");

                        sender.sendMessage(Component.text("  - " + id.name() + " (" + id.name() + ")")
                                .append(status));
                    }
                }
            }

            case "reload" -> {
                // 퀘스트 데이터 리로드 (언어 파일 등)
                langManager.reload();
                sender.sendMessage(langManager.getComponent(sender, "commands.admin.quest.reload.success"));
            }

            default -> {
                sender.sendMessage(langManager.getComponent(sender, "commands.admin.quest.usage"));
            }
        }

        return true;
    }

    /**
     * 섬 관리 명령어 처리
     */
    private boolean handleIslandCommand(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 2) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.usage.title"));
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.usage.info"));
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.usage.delete"));
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.usage.reset"));
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.usage.tp"));
            return true;
        }

        String subCmd = args[1].toLowerCase();
        if (args.length < 3) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.player-required"));
            return true;
        }

        String targetName = args[2];
        Player target = Bukkit.getPlayer(targetName);
        
        if (target == null) {
            sender.sendMessage(langManager.getComponent(sender, "commands.admin.player-not-found"));
            return true;
        }

        String targetUuid = target.getUniqueId().toString();

        switch (subCmd) {
            case "info" -> {
                islandManager.getPlayerIsland(targetUuid, targetName).thenAccept(island -> {
                    if (island == null) {
                        sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.no-island", "player", targetName));
                        return;
                    }

                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.info.title", "player", targetName));
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.info.id", "id", island.getId()));
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.info.name", "name", island.getName()));
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.info.owner", "owner", island.getOwnerName()));
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.info.size", "size", String.valueOf(island.getSize())));
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.info.members", 
                            "current", String.valueOf(island.getData().members().size() + 1),
                            "max", String.valueOf(island.getData().upgradeData().memberLimit() + 1)));
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.info.created", 
                            "date", DateFormatUtil.formatFullDateTimeFromMillis(island.getData().createdAt()) + ":" + String.format("%02d", java.time.LocalDateTime.ofInstant(java.time.Instant.ofEpochMilli(island.getData().createdAt()), java.time.ZoneId.systemDefault()).getSecond())));
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.info.resets", "count", String.valueOf(island.getData().totalResets())));
                });
            }
            
            case "delete" -> {
                islandManager.getPlayerIsland(targetUuid, targetName).thenAccept(island -> {
                    if (island == null) {
                        sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.no-island", "player", targetName));
                        return;
                    }

                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.delete.deleting"));
                    
                    islandManager.deleteIsland(island.getId()).thenAccept(success -> {
                        if (success) {
                            sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.delete.success", "player", targetName));
                            
                            // 섬장이 온라인인 경우 스폰으로 이동
                            if (target.isOnline()) {
                                plugin.getServer().getScheduler().runTask(plugin, () -> {
                                    if (!plugin.getServer().getWorlds().isEmpty()) {
                                        target.teleport(plugin.getServer().getWorlds().get(0).getSpawnLocation());
                                        target.sendMessage(langManager.getComponent(target, "commands.admin.island.delete.owner-notify"));
                                    }
                                });
                            }
                        } else {
                            sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.delete.failed"));
                        }
                    });
                });
            }
            
            case "reset" -> {
                islandManager.getPlayerIsland(targetUuid, targetName).thenAccept(island -> {
                    if (island == null) {
                        sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.no-island", "player", targetName));
                        return;
                    }

                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.reset.resetting"));
                    
                    islandManager.resetIsland(island.getId()).thenAccept(success -> {
                        if (success) {
                            sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.reset.success", "player", targetName));
                            
                            // 섬장이 온라인인 경우 알림
                            if (target.isOnline()) {
                                target.sendMessage(langManager.getComponent(target, "commands.admin.island.reset.owner-notify"));
                            }
                        } else {
                            sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.reset.failed"));
                        }
                    });
                });
            }
            
            case "tp" -> {
                if (!(sender instanceof Player admin)) {
                    sender.sendMessage(langManager.getComponent(sender, "commands.admin.player-only"));
                    return true;
                }
                
                islandManager.getPlayerIsland(targetUuid, targetName).thenAccept(island -> {
                    if (island == null) {
                        sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.no-island", "player", targetName));
                        return;
                    }

                    plugin.getServer().getScheduler().runTask(plugin, () -> {
                        World islandWorld = islandManager.getWorldManager().getIslandWorld();
                        if (islandWorld == null) {
                            sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.tp.world-not-found"));
                            return;
                        }
                        
                        Location tpLocation = island.getSpawnLocation();
                        admin.teleport(tpLocation);
                        admin.sendMessage(Component.text(targetName + "의 섬으로 이동했습니다.", ColorUtil.SUCCESS));
                    });
                });
            }
            
            default -> {
                sender.sendMessage(langManager.getComponent(sender, "commands.admin.island.unknown-command"));
                return true;
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
            return Arrays.asList("stats", "reload", "viewprofile", "exp", "level", "job", "npc", "quest", "island")
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
                    return Arrays.asList("set", "setcode", "reward", "list");
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
                        return Bukkit.getOnlinePlayers().stream()
                                .map(Player::getName)
                                .filter(name -> name.toLowerCase().startsWith(args[2].toLowerCase()))
                                .collect(Collectors.toList());
                    }
                }
                case "npc" -> {
                    if (args[1].equalsIgnoreCase("set")) {
                        return getQuestIdSuggestions(args[2]);
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
                case "island" -> {
                    if (args[1].equalsIgnoreCase("info") || args[1].equalsIgnoreCase("delete") || 
                        args[1].equalsIgnoreCase("reset") || args[1].equalsIgnoreCase("tp")) {
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
            // NPC create QUEST 뒤에 퀘스트 ID 제안
            if (args[0].equalsIgnoreCase("npc") && args[1].equalsIgnoreCase("create") 
                    && args[2].equalsIgnoreCase("QUEST")) {
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

        // enum name으로만 검색 (대문자만 사용)
        for (QuestID id : QuestID.values()) {
            if (id.name().startsWith(upperPartial)) {
                suggestions.add(id.name());
            }
        }

        return suggestions;
    }
}