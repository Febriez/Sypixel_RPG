package com.febrie.rpg.npc.trait.base;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.StandardItemBuilder;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
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
 * 개선된 NPC Trait 등록 아이템 기본 클래스
 * Template Method 패턴으로 중복 제거
 *
 * @author Febrie, CoffeeTory
 */
public abstract class ImprovedBaseTraitRegistrationItem implements Listener {
    
    protected final RPGMain plugin;
    protected final NamespacedKey itemTypeKey;
    protected final NamespacedKey idKey;
    
    protected ImprovedBaseTraitRegistrationItem(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        this.itemTypeKey = new NamespacedKey(plugin, getTraitType() + "_trait_item");
        this.idKey = new NamespacedKey(plugin, getTraitType() + "_trait_id");
    }
    
    /**
     * Trait 타입 (quest, reward, shop 등)
     */
    @NotNull
    protected abstract String getTraitType();
    
    /**
     * 아이템 표시 이름 접두사
     */
    @NotNull
    protected abstract String getItemDisplayPrefix();
    
    /**
     * 아이템 색상
     */
    @NotNull
    protected abstract NamedTextColor getItemColor();
    
    /**
     * Trait 클래스
     */
    @NotNull
    protected abstract Class<? extends Trait> getTraitClass();
    
    /**
     * Trait 생성
     */
    @NotNull
    protected abstract Trait createTrait(@NotNull String traitId);
    
    /**
     * 설정 성공 메시지
     */
    @NotNull
    protected abstract String getSuccessMessage(@NotNull String traitId, int npcId);
    
    /**
     * 등록 아이템 생성 (Template Method)
     */
    @NotNull
    public final ItemStack createRegistrationItem(@NotNull String traitId, @NotNull String displayName) {
        // ID 유효성 검사
        if (!traitId.matches("^[a-z_]+$")) {
            throw new IllegalArgumentException(getTraitType() + " ID must contain only lowercase letters and underscores: " + traitId);
        }
        
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        
        // 표시 이름 설정
        meta.displayName(Component.text(getItemDisplayPrefix() + " - " + displayName, getItemColor())
                .decoration(TextDecoration.ITALIC, false));
        
        // 설명 설정
        meta.lore(createLore(traitId));
        
        // 반짝임 효과
        meta.addEnchant(org.bukkit.enchantments.Enchantment.MENDING, 1, true);
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        
        // 데이터 저장
        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, traitId);
        meta.getPersistentDataContainer().set(itemTypeKey, PersistentDataType.BOOLEAN, true);
        
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * 아이템 설명 생성
     */
    @NotNull
    protected List<Component> createLore(@NotNull String traitId) {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.empty());
        lore.add(Component.text("Type: ", NamedTextColor.GRAY)
                .append(Component.text(getTraitType().toUpperCase(), NamedTextColor.YELLOW)));
        lore.add(Component.text("ID: ", NamedTextColor.GRAY)
                .append(Component.text(traitId, NamedTextColor.YELLOW)));
        lore.add(Component.empty());
        lore.add(Component.text("우클릭으로 NPC에 설정", NamedTextColor.GREEN)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("Shift + 우클릭으로 제거", NamedTextColor.RED)
                .decoration(TextDecoration.ITALIC, false));
        return lore;
    }
    
    /**
     * NPC 상호작용 처리
     */
    @EventHandler
    public final void onNPCRightClick(PlayerInteractEntityEvent event) {
        if (event.getHand() != EquipmentSlot.HAND) return;
        if (!(event.getRightClicked() instanceof org.bukkit.entity.Entity entity)) return;
        
        NPC npc = CitizensAPI.getNPCRegistry().getNPC(entity);
        if (npc == null) return;
        
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        if (item.getType() != Material.BLAZE_ROD) return;
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        
        // 이 타입의 등록 아이템인지 확인
        if (!meta.getPersistentDataContainer().has(itemTypeKey, PersistentDataType.BOOLEAN)) {
            return;
        }
        
        event.setCancelled(true);
        
        String traitId = meta.getPersistentDataContainer().get(idKey, PersistentDataType.STRING);
        if (traitId == null) return;
        
        if (player.isSneaking()) {
            // Trait 제거
            removeTrait(npc, player);
        } else {
            // Trait 설정
            setTrait(npc, traitId, player);
        }
    }
    
    /**
     * Trait 설정
     */
    protected void setTrait(@NotNull NPC npc, @NotNull String traitId, @NotNull Player player) {
        if (npc.hasTrait(getTraitClass())) {
            npc.removeTrait(getTraitClass());
        }
        
        // 새 Trait 추가
        Trait trait = createTrait(traitId);
        npc.addTrait(trait);
        
        player.sendMessage(UnifiedColorUtil.parse(getSuccessMessage(traitId, npc.getId())));
    }
    
    /**
     * Trait 제거
     */
    protected void removeTrait(@NotNull NPC npc, @NotNull Player player) {
        if (npc.hasTrait(getTraitClass())) {
            npc.removeTrait(getTraitClass());
            player.sendMessage(UnifiedColorUtil.parse("&cNPC #" + npc.getId() + "에서 " + 
                getTraitType() + " trait를 제거했습니다"));
        } else {
            player.sendMessage(UnifiedColorUtil.parse("&c이 NPC는 " + 
                getTraitType() + " trait를 가지고 있지 않습니다"));
        }
    }
}