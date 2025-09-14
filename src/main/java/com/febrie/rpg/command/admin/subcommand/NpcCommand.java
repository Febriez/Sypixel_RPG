package com.febrie.rpg.command.admin.subcommand;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.command.admin.subcommand.base.BaseSubCommand;
import com.febrie.rpg.npc.NPCTraitSetter;
import com.febrie.rpg.npc.trait.RPGQuestTrait;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.util.UnifiedColorUtil;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
        this.setUsage("/rpgadmin npc <quest|reward|objective|settrait> [args]");
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
            case "quest" -> {
                // 퀘스트 설정 막대기 지급
                if (args.length < 2) {
                    player.sendMessage(Component.text("사용법: /rpga npc quest <퀘스트ID>", UnifiedColorUtil.ERROR));
                    return true;
                }
                
                try {
                    QuestID questId = QuestID.valueOf(args[1].toUpperCase());
                    
                    // 막대기 생성
                    ItemStack stick = new ItemStack(Material.STICK);
                    ItemMeta meta = stick.getItemMeta();
                    meta.displayName(Component.text("퀘스트 설정 막대기", UnifiedColorUtil.GOLD));
                    meta.lore(List.of(
                        Component.text("퀘스트: " + questId.name(), UnifiedColorUtil.YELLOW),
                        Component.empty(),
                        Component.text("NPC를 우클릭하면 퀘스트가 설정됩니다.", UnifiedColorUtil.GRAY)
                    ));
                    stick.setItemMeta(meta);
                    
                    // NPCTraitSetter에 대기 중인 설정 추가
                    npcTraitSetter.setPendingQuestTrait(player, questId);
                    
                    // 막대기 지급
                    player.getInventory().addItem(stick);
                    player.sendMessage(Component.text("퀘스트 설정 막대기를 받았습니다. NPC를 우클릭하세요.", UnifiedColorUtil.SUCCESS));
                    
                } catch (IllegalArgumentException e) {
                    player.sendMessage(Component.text("잘못된 퀘스트 ID입니다.", UnifiedColorUtil.ERROR));
                }
                return true;
            }
            
            case "reward" -> {
                // 보상 NPC 설정 막대기 지급
                if (args.length < 2) {
                    player.sendMessage(Component.text("사용법: /rpga npc reward <퀘스트ID>", UnifiedColorUtil.ERROR));
                    return true;
                }
                
                try {
                    QuestID questId = QuestID.valueOf(args[1].toUpperCase());
                    
                    // 막대기 생성
                    ItemStack stick = new ItemStack(Material.BLAZE_ROD);
                    ItemMeta meta = stick.getItemMeta();
                    meta.displayName(Component.text("보상 설정 막대기", UnifiedColorUtil.LIGHT_PURPLE));
                    meta.lore(List.of(
                        Component.text("퀘스트: " + questId.name(), UnifiedColorUtil.YELLOW),
                        Component.empty(),
                        Component.text("NPC를 우클릭하면 보상 NPC로 설정됩니다.", UnifiedColorUtil.GRAY)
                    ));
                    stick.setItemMeta(meta);
                    
                    // NPCTraitSetter에 대기 중인 설정 추가
                    npcTraitSetter.setPendingRewardTrait(player, questId);
                    
                    // 막대기 지급
                    player.getInventory().addItem(stick);
                    player.sendMessage(Component.text("보상 설정 막대기를 받았습니다. NPC를 우클릭하세요.", UnifiedColorUtil.SUCCESS));
                    
                } catch (IllegalArgumentException e) {
                    player.sendMessage(Component.text("잘못된 퀘스트 ID입니다.", UnifiedColorUtil.ERROR));
                }
                return true;
            }
            
            case "objective" -> {
                // 퀘스트 목표 NPC 설정 막대기 지급
                if (args.length < 2) {
                    player.sendMessage(Component.text("사용법: /rpga npc objective <NPC코드>", UnifiedColorUtil.ERROR));
                    return true;
                }
                
                String npcCode = args[1].toLowerCase();
                
                // 막대기 생성
                ItemStack stick = new ItemStack(Material.END_ROD);
                ItemMeta meta = stick.getItemMeta();
                meta.displayName(Component.text("퀘스트 목표 설정 막대기", UnifiedColorUtil.AQUA));
                meta.lore(List.of(
                    Component.text("NPC 코드: " + npcCode, UnifiedColorUtil.YELLOW),
                    Component.empty(),
                    Component.text("NPC를 우클릭하면 퀘스트 목표 NPC로 설정됩니다.", UnifiedColorUtil.GRAY)
                ));
                stick.setItemMeta(meta);
                
                // NPCTraitSetter에 대기 중인 설정 추가
                npcTraitSetter.setPendingObjectiveTrait(player, npcCode);
                
                // 막대기 지급
                player.getInventory().addItem(stick);
                player.sendMessage(Component.text("퀘스트 목표 설정 막대기를 받았습니다. NPC를 우클릭하세요.", UnifiedColorUtil.SUCCESS));
                return true;
            }
            
            case "settrait" -> {
                if (args.length < 2) {
                    player.sendMessage(Component.translatable("commands.admin.npc.settrait.usage"));
                    return true;
                }
                
                // NPC 선택 체크
                NPC selectedNPC = CitizensAPI.getDefaultNPCSelector().getSelected(player);
                if (selectedNPC == null) {
                    player.sendMessage(Component.translatable("commands.admin.npc.not-selected"));
                    return true;
                }
                
                String traitType = args[1].toLowerCase();
                
                switch (traitType) {
                    case "quest" -> {
                        if (args.length < 3) {
                            player.sendMessage(Component.translatable("commands.admin.npc.settrait.quest.usage"));
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
                            
                            player.sendMessage(Component.translatable("commands.admin.npc.settrait.quest.success",
                                Component.text(selectedNPC.getName()),
                                Component.text(questId.name())));
                        } catch (IllegalArgumentException e) {
                            player.sendMessage(Component.translatable("commands.admin.quest.invalid-id"));
                        }
                    }
                    
                    case "shop" -> {
                        // Shop trait 추가
                        if (!selectedNPC.hasTrait(com.febrie.rpg.npc.trait.RPGShopTrait.class)) {
                            selectedNPC.addTrait(com.febrie.rpg.npc.trait.RPGShopTrait.class);
                        }
                        player.sendMessage(Component.translatable("commands.admin.npc.settrait.shop.success",
                            Component.text(selectedNPC.getName())));
                    }
                    
                    case "dialog" -> {
                        if (args.length < 3) {
                            player.sendMessage(Component.translatable("commands.admin.npc.settrait.dialog.usage"));
                            return true;
                        }
                        
                        String dialogId = args[2];
                        // Dialog trait 추가
                        if (!selectedNPC.hasTrait(com.febrie.rpg.npc.trait.RPGDialogTrait.class)) {
                            selectedNPC.addTrait(com.febrie.rpg.npc.trait.RPGDialogTrait.class);
                        }
                        com.febrie.rpg.npc.trait.RPGDialogTrait dialogTrait = selectedNPC.getOrAddTrait(com.febrie.rpg.npc.trait.RPGDialogTrait.class);
                        dialogTrait.setDialogId(dialogId);
                        
                        player.sendMessage(Component.translatable("commands.admin.npc.settrait.dialog.success",
                            Component.text(selectedNPC.getName()),
                            Component.text(dialogId)));
                    }
                    
                    case "guide" -> {
                        // Guide trait 추가
                        if (!selectedNPC.hasTrait(com.febrie.rpg.npc.trait.RPGGuideTrait.class)) {
                            selectedNPC.addTrait(com.febrie.rpg.npc.trait.RPGGuideTrait.class);
                        }
                        player.sendMessage(Component.translatable("commands.admin.npc.settrait.guide.success",
                            Component.text(selectedNPC.getName())));
                    }
                    
                    default -> {
                        player.sendMessage(Component.translatable("commands.admin.npc.settrait.invalid"));
                    }
                }
                return true;
            }
            
            
            default -> {
                player.sendMessage(Component.text("사용법: /rpga npc <quest|reward|objective>", UnifiedColorUtil.ERROR));
                return false;
            }
        }
    }
    
    @Override
    public @NotNull List<String> tabComplete(@NotNull CommandSender sender, String @NotNull [] args) {
        if (args.length == 1) {
            return Stream.of("quest", "reward", "objective")
                .filter(cmd -> cmd.startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        } else if (args.length == 2) {
            if (args[0].equalsIgnoreCase("settrait")) {
                return Stream.of("quest", "shop", "dialog", "guide")
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