package com.febrie.rpg.quest.trait;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.npc.trait.RPGQuestTrait;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.util.ColorUtil;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Quest Trait Registration Item
 * Allows attaching quest traits to NPCs by right-clicking
 */
public class QuestTraitRegistrationItem implements Listener {
    
    private static final NamespacedKey QUEST_ID_KEY = new NamespacedKey(RPGMain.getPlugin(), "quest_id");
    private static final NamespacedKey IS_QUEST_ITEM = new NamespacedKey(RPGMain.getPlugin(), "is_quest_trait_item");
    private static final NamespacedKey TARGET_NPC_ID = new NamespacedKey(RPGMain.getPlugin(), "target_npc_id");
    
    /**
     * Create a quest trait registration item for a specific quest
     */
    @NotNull
    public static ItemStack createRegistrationItem(@NotNull Quest quest) {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        
        // Set display name
        meta.displayName(Component.text("퀘스트 Trait 등록기", ColorUtil.LEGENDARY)
                .decoration(TextDecoration.ITALIC, false));
        
        // Set lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("퀘스트: ", ColorUtil.UNCOMMON)
                .append(Component.text(quest.getDisplayName(true), ColorUtil.RARE)));
        lore.add(Component.empty());
        lore.add(Component.text("사용법:", ColorUtil.COMMON));
        lore.add(Component.text("NPC를 우클릭하여 퀘스트 목표에 맞는", ColorUtil.GRAY));
        lore.add(Component.text("Trait를 자동으로 부착합니다.", ColorUtil.GRAY));
        lore.add(Component.empty());
        
        // Add NPC list
        List<Integer> relatedNPCIds = getRelatedNPCIds(quest);
        if (!relatedNPCIds.isEmpty()) {
            lore.add(Component.text("관련 NPC ID:", ColorUtil.COMMON));
            for (Integer npcId : relatedNPCIds) {
                lore.add(Component.text("- NPC ID: " + npcId, ColorUtil.GRAY));
            }
        }
        
        meta.lore(lore);
        
        // Add enchantment glow
        meta.addEnchant(org.bukkit.enchantments.Enchantment.MENDING, 1, true);
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        
        // Set persistent data
        meta.getPersistentDataContainer().set(QUEST_ID_KEY, PersistentDataType.STRING, quest.getId().name());
        meta.getPersistentDataContainer().set(IS_QUEST_ITEM, PersistentDataType.BOOLEAN, true);
        
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Create a quest trait registration item for a specific NPC
     */
    @NotNull
    public static ItemStack createRegistrationItemForNPC(@NotNull Quest quest, int npcId) {
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        
        // Get NPC info
        String npcName = "Unknown NPC";
        String npcInfo = "";
        
        // Try to get NPC name from Citizens
        if (CitizensAPI.getNPCRegistry() != null && CitizensAPI.getNPCRegistry().getById(npcId) != null) {
            NPC npc = CitizensAPI.getNPCRegistry().getById(npcId);
            npcName = npc.getName();
            npcInfo = npc.getEntity() != null ? npc.getEntity().getType().name() : "Unknown Type";
        }
        
        // Set display name
        meta.displayName(Component.text("퀘스트 Trait 등록기 - NPC #" + npcId, ColorUtil.LEGENDARY)
                .decoration(TextDecoration.ITALIC, false));
        
        // Set lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("퀘스트: ", ColorUtil.UNCOMMON)
                .append(Component.text(quest.getDisplayName(true), ColorUtil.RARE)));
        lore.add(Component.empty());
        lore.add(Component.text("대상 NPC:", ColorUtil.GOLD));
        lore.add(Component.text("  ID: " + npcId, ColorUtil.YELLOW));
        lore.add(Component.text("  이름: " + npcName, ColorUtil.YELLOW));
        if (!npcInfo.isEmpty()) {
            lore.add(Component.text("  타입: " + npcInfo, ColorUtil.GRAY));
        }
        lore.add(Component.empty());
        lore.add(Component.text("사용법:", ColorUtil.COMMON));
        lore.add(Component.text("지정된 NPC를 우클릭하여", ColorUtil.GRAY));
        lore.add(Component.text("퀘스트 Trait를 부착합니다.", ColorUtil.GRAY));
        lore.add(Component.empty());
        lore.add(Component.text("⚠ 주의: ID #" + npcId + " NPC에만 사용 가능", ColorUtil.ERROR));
        
        meta.lore(lore);
        
        // Add enchantment glow
        meta.addEnchant(org.bukkit.enchantments.Enchantment.MENDING, 1, true);
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        
        // Set persistent data with Paper PDC
        meta.getPersistentDataContainer().set(QUEST_ID_KEY, PersistentDataType.STRING, quest.getId().name());
        meta.getPersistentDataContainer().set(IS_QUEST_ITEM, PersistentDataType.BOOLEAN, true);
        meta.getPersistentDataContainer().set(TARGET_NPC_ID, PersistentDataType.INTEGER, npcId);
        
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Get all NPC IDs related to a quest
     */
    @NotNull
    private static List<Integer> getRelatedNPCIds(@NotNull Quest quest) {
        List<Integer> npcIds = new ArrayList<>();
        
        for (QuestObjective objective : quest.getObjectives()) {
            if (objective instanceof InteractNPCObjective interactObj) {
                Integer npcId = interactObj.getNpcId();
                if (npcId != null && !npcIds.contains(npcId)) {
                    npcIds.add(npcId);
                }
            }
        }
        
        return npcIds;
    }
    
    @EventHandler
    public void onNPCRightClick(PlayerInteractEntityEvent event) {
        // Check if it's the main hand to avoid double events
        if (event.getHand() != EquipmentSlot.HAND) return;
        
        // Check if the entity is a Citizens NPC
        if (!CitizensAPI.getNPCRegistry().isNPC(event.getRightClicked())) return;
        
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        // Check if it's a quest trait registration item
        if (item.getType() != Material.BLAZE_ROD) return;
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        
        if (!meta.getPersistentDataContainer().has(IS_QUEST_ITEM, PersistentDataType.BOOLEAN)) return;
        
        event.setCancelled(true);
        
        // Get quest ID
        String questIdStr = meta.getPersistentDataContainer().get(QUEST_ID_KEY, PersistentDataType.STRING);
        if (questIdStr == null) {
            player.sendMessage(Component.text("오류: 퀘스트 ID를 찾을 수 없습니다.", ColorUtil.ERROR));
            return;
        }
        
        // Get quest
        QuestID questId;
        try {
            questId = QuestID.valueOf(questIdStr);
        } catch (IllegalArgumentException e) {
            player.sendMessage(Component.text("오류: 잘못된 퀘스트 ID입니다.", ColorUtil.ERROR));
            return;
        }
        
        Quest quest = RPGMain.getPlugin().getQuestManager().getQuest(questId);
        if (quest == null) {
            player.sendMessage(Component.text("오류: 퀘스트를 찾을 수 없습니다.", ColorUtil.ERROR));
            return;
        }
        
        // Get NPC
        NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getRightClicked());
        int npcId = npc.getId();
        
        // Check if this item has a specific target NPC ID
        Integer targetNpcId = meta.getPersistentDataContainer().get(TARGET_NPC_ID, PersistentDataType.INTEGER);
        
        if (targetNpcId != null) {
            // This item is for a specific NPC
            if (npcId != targetNpcId) {
                player.sendMessage(Component.text("❌ 이 아이템은 NPC #" + targetNpcId + "에만 사용할 수 있습니다.", ColorUtil.ERROR));
                player.sendMessage(Component.text("   현재 NPC: #" + npcId + " (" + npc.getName() + ")", ColorUtil.GRAY));
                return;
            }
        } else {
            // Legacy item - check if NPC is used in quest
            boolean npcUsedInQuest = false;
            for (QuestObjective objective : quest.getObjectives()) {
                if (objective instanceof InteractNPCObjective interactObj) {
                    Integer objNpcId = interactObj.getNpcId();
                    if (objNpcId != null && objNpcId == npcId) {
                        npcUsedInQuest = true;
                        break;
                    }
                }
            }
            
            if (!npcUsedInQuest) {
                player.sendMessage(Component.text("이 NPC는 " + quest.getDisplayName(true) + " 퀘스트와 관련이 없습니다.", ColorUtil.WARNING));
                return;
            }
        }
        
        // Register trait
        if (!npc.hasTrait(RPGQuestTrait.class)) {
            npc.addTrait(RPGQuestTrait.class);
        }
        
        RPGQuestTrait trait = npc.getTrait(RPGQuestTrait.class);
        trait.addQuest(questId);
        
        player.sendMessage(Component.text("✓ ", ColorUtil.SUCCESS)
                .append(Component.text("NPC (ID: " + npcId + ")에게 ", ColorUtil.COMMON))
                .append(Component.text(quest.getDisplayName(true), ColorUtil.RARE))
                .append(Component.text(" 퀘스트 Trait를 등록했습니다.", ColorUtil.COMMON)));
        
        // Remove item if not creative
        if (!player.getGameMode().name().equals("CREATIVE")) {
            item.setAmount(item.getAmount() - 1);
        }
    }
}