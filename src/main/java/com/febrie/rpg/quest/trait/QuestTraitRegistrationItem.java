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
    private static final NamespacedKey NPC_ID_KEY = new NamespacedKey(RPGMain.getPlugin(), "npc_id");
    
    /**
     * Create a quest trait registration item for a specific NPC ID
     */
    @NotNull
    public static ItemStack createRegistrationItem(@NotNull String npcId, @NotNull String displayName) {
        // NPC ID 유효성 검사
        if (!npcId.matches("^[a-z_]+$")) {
            throw new IllegalArgumentException("NPC ID must contain only lowercase letters and underscores: " + npcId);
        }
        
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        
        // Set display name
        meta.displayName(Component.text("NPC Trait 등록기 - " + displayName, ColorUtil.LEGENDARY)
                .decoration(TextDecoration.ITALIC, false));
        
        // Set lore
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("NPC ID: ", ColorUtil.UNCOMMON)
                .append(Component.text(npcId, ColorUtil.RARE)));
        lore.add(Component.empty());
        lore.add(Component.text("사용법:", ColorUtil.COMMON));
        lore.add(Component.text("아무 NPC를 우클릭하여 Trait를", ColorUtil.GRAY));
        lore.add(Component.text("부착할 수 있습니다.", ColorUtil.GRAY));
        lore.add(Component.empty());
        lore.add(Component.text("설명:", ColorUtil.COMMON));
        lore.add(Component.text("이 ID가 부착된 NPC와 상호작용 시", ColorUtil.GRAY));
        lore.add(Component.text("관련 퀘스트 목표가 달성됩니다.", ColorUtil.GRAY));
        
        meta.lore(lore);
        
        // Add enchantment glow
        meta.addEnchant(org.bukkit.enchantments.Enchantment.MENDING, 1, true);
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        
        // Set persistent data
        meta.getPersistentDataContainer().set(NPC_ID_KEY, PersistentDataType.STRING, npcId);
        meta.getPersistentDataContainer().set(IS_QUEST_ITEM, PersistentDataType.BOOLEAN, true);
        
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Create a quest trait registration item for a specific quest
     * @deprecated Use createRegistrationItem(npcCode, displayName) instead
     */
    @Deprecated
    @NotNull
    public static ItemStack createRegistrationItem(@NotNull Quest quest) {
        // 기본적으로 퀘스트 ID를 NPC 코드로 사용
        return createRegistrationItem("quest_" + quest.getId().name().toLowerCase(), quest.getDisplayName(true));
    }
    
    /**
     * Create a quest trait registration item for a specific NPC
     * @deprecated Use createRegistrationItem instead - NPC ID dependency is being removed
     */
    @Deprecated
    @NotNull
    public static ItemStack createRegistrationItemForNPC(@NotNull Quest quest, int npcId) {
        // 기존 NPC ID 특정 아이템 대신 일반 등록기 반환
        return createRegistrationItem(quest);
    }
    
    /**
     * Get all NPC IDs related to a quest
     * @deprecated NPC ID dependency is being removed
     */
    @Deprecated
    @NotNull
    private static List<Integer> getRelatedNPCIds(@NotNull Quest quest) {
        return new ArrayList<>();
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
        
        // Get NPC ID
        String npcId = meta.getPersistentDataContainer().get(NPC_ID_KEY, PersistentDataType.STRING);
        
        // Legacy support - Get quest ID if npc id not found
        if (npcId == null) {
            String questIdStr = meta.getPersistentDataContainer().get(QUEST_ID_KEY, PersistentDataType.STRING);
            if (questIdStr != null) {
                npcId = "quest_" + questIdStr.toLowerCase();
            } else {
                player.sendMessage(Component.text("오류: NPC ID를 찾을 수 없습니다.", ColorUtil.ERROR));
                return;
            }
        }
        
        // Get NPC
        NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getRightClicked());
        int citizensNpcId = npc.getId();
        
        // 모든 NPC에 trait 등록 가능 - NPC ID 체크 제거
        
        // Register trait
        if (!npc.hasTrait(RPGQuestTrait.class)) {
            npc.addTrait(RPGQuestTrait.class);
        }
        
        RPGQuestTrait trait = npc.getTrait(RPGQuestTrait.class);
        
        // 이미 NPC ID가 등록되어 있는지 확인
        if (trait.hasNpcId()) {
            String existingId = trait.getNpcId();
            if (existingId.equals(npcId)) {
                player.sendMessage(Component.text("✗ ", ColorUtil.ERROR)
                        .append(Component.text("이미 동일한 NPC ID가 등록되어 있습니다: ", ColorUtil.WARNING))
                        .append(Component.text(existingId, ColorUtil.AQUA)));
            } else {
                player.sendMessage(Component.text("✗ ", ColorUtil.ERROR)
                        .append(Component.text("이 NPC에는 이미 다른 ID가 등록되어 있습니다: ", ColorUtil.WARNING))
                        .append(Component.text(existingId, ColorUtil.AQUA)));
                player.sendMessage(Component.text("현재 ID를 덮어쓰려면 Shift+우클릭하세요.", ColorUtil.GRAY));
                
                // Shift 클릭 시 덮어쓰기
                if (player.isSneaking()) {
                    trait.setNpcId(npcId);
                    player.sendMessage(Component.text("✓ ", ColorUtil.SUCCESS)
                            .append(Component.text("NPC ID를 ", ColorUtil.COMMON))
                            .append(Component.text(existingId, ColorUtil.GRAY))
                            .append(Component.text("에서 ", ColorUtil.COMMON))
                            .append(Component.text(npcId, ColorUtil.RARE))
                            .append(Component.text("(으)로 변경했습니다.", ColorUtil.COMMON)));
                    
                    // 막대기 제거 (크리에이티브 모드에서도 제거)
                    item.setAmount(item.getAmount() - 1);
                }
            }
            return;
        }
        
        // 새롭게 등록
        trait.setNpcId(npcId);
        
        player.sendMessage(Component.text("✓ ", ColorUtil.SUCCESS)
                .append(Component.text("NPC ", ColorUtil.COMMON))
                .append(Component.text(npc.getName(), ColorUtil.YELLOW))
                .append(Component.text(" (Citizens ID: " + citizensNpcId + ")에게 ", ColorUtil.GRAY))
                .append(Component.text("NPC ID '", ColorUtil.COMMON))
                .append(Component.text(npcId, ColorUtil.RARE))
                .append(Component.text("'를 등록했습니다.", ColorUtil.COMMON)));
        
        // 막대기 제거 (크리에이티브 모드에서도 제거)
        item.setAmount(item.getAmount() - 1);
    }
}