package com.febrie.rpg.command.admin.subcommand;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.command.admin.subcommand.base.BaseSubCommand;
import com.febrie.rpg.dto.quest.ActiveQuestDTO;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.quest.registry.QuestRegistry;
import com.febrie.rpg.util.LangManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 퀘스트 관리 명령어
 * 
 * @author Febrie
 */
public class QuestCommand extends BaseSubCommand {
    
    private final QuestManager questManager;
    private final QuestRegistry questRegistry;
    
    public QuestCommand(@NotNull RPGMain plugin) {
        super("quest", "rpg.admin.quest", "Manage player quests");
        this.questManager = QuestManager.getInstance();
        this.questRegistry = QuestRegistry.getInstance();
        this.setMinArgs(1);
        this.setUsage("/rpgadmin quest <give|complete|reset|list> [player] [questId]");
    }
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        String action = args[0].toLowerCase();
        
        switch (action) {
            case "list" -> {
                if (args.length < 2) {
                    // 모든 퀘스트 목록 표시
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.list.title"));
                    for (QuestID questId : QuestID.values()) {
                        Quest quest = questRegistry.getQuest(questId);
                        if (quest != null) {
                            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.list.item",
                                "id", questId.name(),
                                "name", quest.getName()));
                        }
                    }
                } else {
                    // 특정 플레이어의 퀘스트 목록
                    Player target = Bukkit.getPlayer(args[1]);
                    if (target == null) {
                        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-not-found"));
                        return true;
                    }
                    
                    Map<String, ActiveQuestDTO> activeQuests = questManager.getActiveQuests(target.getUniqueId());
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.player-list.title",
                        "player", target.getName()));
                    
                    if (activeQuests.isEmpty()) {
                        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.player-list.empty"));
                    } else {
                        for (ActiveQuestDTO activeQuest : activeQuests.values()) {
                            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.player-list.item",
                                "id", activeQuest.questId(),
                                "progress", String.valueOf(activeQuest.currentProgress()),
                                "required", String.valueOf(activeQuest.requiredProgress())));
                        }
                    }
                }
                return true;
            }
            
            case "give" -> {
                if (args.length < 3) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.give.usage"));
                    return true;
                }
                
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-not-found"));
                    return true;
                }
                
                try {
                    QuestID questId = QuestID.valueOf(args[2].toUpperCase());
                    Quest quest = questRegistry.getQuest(questId);
                    
                    if (quest == null) {
                        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.not-found"));
                        return true;
                    }
                    
                    if (questManager.hasActiveQuest(target.getUniqueId(), questId)) {
                        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.already-active"));
                        return true;
                    }
                    
                    questManager.startQuest(target, questId);
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.give.success",
                        "player", target.getName(),
                        "quest", quest.getName()));
                    target.sendMessage(LangManager.getMessage(target, "commands.admin.quest.give.received",
                        "quest", quest.getName()));
                    
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.invalid-id"));
                }
                return true;
            }
            
            case "complete" -> {
                if (args.length < 3) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.complete.usage"));
                    return true;
                }
                
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-not-found"));
                    return true;
                }
                
                try {
                    QuestID questId = QuestID.valueOf(args[2].toUpperCase());
                    
                    if (!questManager.hasActiveQuest(target.getUniqueId(), questId)) {
                        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.not-active"));
                        return true;
                    }
                    
                    // questId로 활성 퀘스트 찾기
                    String instanceId = null;
                    for (Map.Entry<String, ActiveQuestDTO> entry : questManager.getActiveQuests(target.getUniqueId()).entrySet()) {
                        if (entry.getValue().questId().equals(questId.name())) {
                            instanceId = entry.getKey();
                            break;
                        }
                    }
                    
                    if (instanceId != null) {
                        questManager.completeQuest(target, instanceId);
                    }
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.complete.success",
                        "player", target.getName(),
                        "quest", args[2]));
                    
                } catch (IllegalArgumentException e) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.invalid-id"));
                }
                return true;
            }
            
            case "reset" -> {
                if (args.length < 3) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.reset.usage"));
                    return true;
                }
                
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-not-found"));
                    return true;
                }
                
                if (args[2].equalsIgnoreCase("all")) {
                    questManager.resetAllQuests(target.getUniqueId());
                    sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.reset.all",
                        "player", target.getName()));
                } else {
                    try {
                        QuestID questId = QuestID.valueOf(args[2].toUpperCase());
                        questManager.resetQuest(target.getUniqueId(), questId);
                        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.reset.success",
                            "player", target.getName(),
                            "quest", args[2]));
                    } catch (IllegalArgumentException e) {
                        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.invalid-id"));
                    }
                }
                return true;
            }
            
            default -> {
                sender.sendMessage(LangManager.getComponent(sender, "commands.admin.quest.usage"));
                return false;
            }
        }
    }
    
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("give", "complete", "reset", "list").stream()
                .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        } else if (args.length == 2) {
            if (!args[0].equalsIgnoreCase("list")) {
                return getOnlinePlayerNames().stream()
                    .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
            }
        } else if (args.length == 3) {
            if (!args[0].equalsIgnoreCase("list")) {
                if (args[0].equalsIgnoreCase("reset")) {
                    List<String> options = Arrays.stream(QuestID.values())
                        .map(QuestID::name)
                        .map(String::toLowerCase)
                        .collect(Collectors.toList());
                    options.add("all");
                    return options.stream()
                        .filter(opt -> opt.startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
                } else {
                    return Arrays.stream(QuestID.values())
                        .map(QuestID::name)
                        .map(String::toLowerCase)
                        .filter(id -> id.startsWith(args[2].toLowerCase()))
                        .collect(Collectors.toList());
                }
            }
        }
        
        return List.of();
    }
}