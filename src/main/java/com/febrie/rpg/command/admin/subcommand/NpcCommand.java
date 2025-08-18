package com.febrie.rpg.command.admin.subcommand;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.command.admin.subcommand.base.BaseSubCommand;
import com.febrie.rpg.npc.NPCTraitSetter;
import com.febrie.rpg.npc.trait.RPGQuestTrait;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.util.LangManager;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * NPC 관리 명령어
 * 
 * @author Febrie
 */
public class NpcCommand extends BaseSubCommand {
    
    private final NPCTraitSetter npcTraitSetter;
    
    private NpcCommand(@NotNull String name, @NotNull String permission, @NotNull String description,
                      @NotNull NPCTraitSetter npcTraitSetter) {
        super(name, permission, description);
        this.npcTraitSetter = npcTraitSetter;
        this.setMinArgs(1);
        this.setUsage("/rpgadmin npc <setcode|settrait|reward> [args]");
        this.setPlayerOnly(true);
    }
    
    public static NpcCommand create(@NotNull RPGMain plugin) {
        return new NpcCommand("npc", "rpg.admin.npc", "Manage NPC traits and settings", plugin.getNPCTraitSetter());
    }
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        Player player = (Player) sender;
        String action = args[0].toLowerCase();
        
        switch (action) {
            case "setcode" -> {
                if (args.length < 2) {
                    player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.setcode.usage"));
                    return true;
                }
                
                // NPC 선택 체크
                NPC selectedNPC = CitizensAPI.getDefaultNPCSelector().getSelected(player);
                if (selectedNPC == null) {
                    player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.not-selected"));
                    return true;
                }
                
                String code = args[1];
                
                // Trait 추가
                if (!selectedNPC.hasTrait(RPGQuestTrait.class)) {
                    selectedNPC.addTrait(RPGQuestTrait.class);
                }
                
                RPGQuestTrait trait = selectedNPC.getOrAddTrait(RPGQuestTrait.class);
                trait.setNpcId(code);
                
                player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.setcode.success",
                    "npc", selectedNPC.getName(),
                    "code", code));
                return true;
            }
            
            case "settrait" -> {
                if (args.length < 2) {
                    player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.settrait.usage"));
                    return true;
                }
                
                // NPC 선택 체크
                NPC selectedNPC = CitizensAPI.getDefaultNPCSelector().getSelected(player);
                if (selectedNPC == null) {
                    player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.not-selected"));
                    return true;
                }
                
                String traitType = args[1].toLowerCase();
                
                switch (traitType) {
                    case "quest" -> {
                        if (args.length < 3) {
                            player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.settrait.quest.usage"));
                            return true;
                        }
                        
                        try {
                            QuestID questId = QuestID.valueOf(args[2].toUpperCase());
                            // 직접 trait 설정
                            if (!selectedNPC.hasTrait(com.febrie.rpg.npc.trait.RPGQuestTrait.class)) {
                                selectedNPC.addTrait(com.febrie.rpg.npc.trait.RPGQuestTrait.class);
                            }
                            com.febrie.rpg.npc.trait.RPGQuestTrait questTrait = selectedNPC.getOrAddTrait(com.febrie.rpg.npc.trait.RPGQuestTrait.class);
                            questTrait.addQuest(questId);
                            
                            player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.settrait.quest.success",
                                "npc", selectedNPC.getName(),
                                "quest", questId.name()));
                        } catch (IllegalArgumentException e) {
                            player.sendMessage(LangManager.getMessage(player, "commands.admin.quest.invalid-id"));
                        }
                    }
                    
                    case "shop" -> {
                        // Shop trait 추가
                        if (!selectedNPC.hasTrait(com.febrie.rpg.npc.trait.RPGShopTrait.class)) {
                            selectedNPC.addTrait(com.febrie.rpg.npc.trait.RPGShopTrait.class);
                        }
                        player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.settrait.shop.success",
                            "npc", selectedNPC.getName()));
                    }
                    
                    case "dialog" -> {
                        if (args.length < 3) {
                            player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.settrait.dialog.usage"));
                            return true;
                        }
                        
                        String dialogId = args[2];
                        // Dialog trait 추가
                        if (!selectedNPC.hasTrait(com.febrie.rpg.npc.trait.RPGDialogTrait.class)) {
                            selectedNPC.addTrait(com.febrie.rpg.npc.trait.RPGDialogTrait.class);
                        }
                        com.febrie.rpg.npc.trait.RPGDialogTrait dialogTrait = selectedNPC.getOrAddTrait(com.febrie.rpg.npc.trait.RPGDialogTrait.class);
                        dialogTrait.setDialogId(dialogId);
                        
                        player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.settrait.dialog.success",
                            "npc", selectedNPC.getName(),
                            "dialog", dialogId));
                    }
                    
                    case "guide" -> {
                        // Guide trait 추가
                        if (!selectedNPC.hasTrait(com.febrie.rpg.npc.trait.RPGGuideTrait.class)) {
                            selectedNPC.addTrait(com.febrie.rpg.npc.trait.RPGGuideTrait.class);
                        }
                        player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.settrait.guide.success",
                            "npc", selectedNPC.getName()));
                    }
                    
                    default -> {
                        player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.settrait.invalid"));
                    }
                }
                return true;
            }
            
            case "reward" -> {
                if (args.length < 3) {
                    player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.reward.usage"));
                    return true;
                }
                
                // NPC 선택 체크
                NPC selectedNPC = CitizensAPI.getDefaultNPCSelector().getSelected(player);
                if (selectedNPC == null) {
                    player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.not-selected"));
                    return true;
                }
                
                try {
                    QuestID questId = QuestID.valueOf(args[1].toUpperCase());
                    String rewardType = args[2].toLowerCase();
                    
                    // Reward trait 추가
                    if (!selectedNPC.hasTrait(com.febrie.rpg.npc.trait.RPGQuestRewardTrait.class)) {
                        selectedNPC.addTrait(com.febrie.rpg.npc.trait.RPGQuestRewardTrait.class);
                    }
                    com.febrie.rpg.npc.trait.RPGQuestRewardTrait rewardTrait = selectedNPC.getOrAddTrait(com.febrie.rpg.npc.trait.RPGQuestRewardTrait.class);
                    rewardTrait.addQuest(questId);
                    
                    player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.reward.success",
                        "npc", selectedNPC.getName(),
                        "quest", questId.name()));
                    
                } catch (IllegalArgumentException e) {
                    player.sendMessage(LangManager.getMessage(player, "commands.admin.quest.invalid-id"));
                }
                return true;
            }
            
            default -> {
                player.sendMessage(LangManager.getMessage(player, "commands.admin.npc.usage"));
                return false;
            }
        }
    }
    
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("setcode", "settrait", "reward").stream()
                .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("settrait")) {
                return Arrays.asList("quest", "shop", "dialog", "guide").stream()
                    .filter(type -> type.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
            } else if (args[0].equalsIgnoreCase("reward")) {
                return Arrays.stream(QuestID.values())
                    .map(QuestID::name)
                    .map(String::toLowerCase)
                    .filter(id -> id.startsWith(args[1].toLowerCase()))
                    .collect(Collectors.toList());
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("settrait") && args[1].equalsIgnoreCase("quest")) {
                return Arrays.stream(QuestID.values())
                    .map(QuestID::name)
                    .map(String::toLowerCase)
                    .filter(id -> id.startsWith(args[2].toLowerCase()))
                    .collect(Collectors.toList());
            }
        }
        
        return List.of();
    }
}