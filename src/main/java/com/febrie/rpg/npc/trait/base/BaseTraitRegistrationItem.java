package com.febrie.rpg.npc.trait.base;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.SoundUtil;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.trait.Trait;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
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
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Base class for NPC trait registration items
 * Provides common functionality for creating and handling trait registration items
 * 
 * @author Febrie, CoffeeTory
 */
public abstract class BaseTraitRegistrationItem implements Listener {
    
    protected final NamespacedKey itemTypeKey;
    protected final NamespacedKey idKey;
    
    /**
     * Constructor
     * 
     * @param itemTypeKey Key to identify this type of registration item
     * @param idKey Key to store the trait ID
     */
    protected BaseTraitRegistrationItem(@NotNull NamespacedKey itemTypeKey, @NotNull NamespacedKey idKey) {
        this.itemTypeKey = itemTypeKey;
        this.idKey = idKey;
    }
    
    /**
     * Create a trait registration item
     */
    @NotNull
    public ItemStack createRegistrationItem(@NotNull String traitId, @NotNull String displayName) {
        // ID 유효성 검사
        if (!traitId.matches("^[a-z_]+$")) {
            throw new IllegalArgumentException(getIdType() + " ID must contain only lowercase letters and underscores: " + traitId);
        }
        
        ItemStack item = new ItemStack(Material.BLAZE_ROD);
        ItemMeta meta = item.getItemMeta();
        
        // Set display name
        meta.displayName(Component.text(getItemDisplayPrefix() + " - " + displayName, getItemColor())
                .decoration(TextDecoration.ITALIC, false));
        
        // Set lore
        List<Component> lore = createLore(traitId);
        meta.lore(lore);
        
        // Add enchantment glow
        meta.addEnchant(org.bukkit.enchantments.Enchantment.MENDING, 1, true);
        meta.addItemFlags(org.bukkit.inventory.ItemFlag.HIDE_ENCHANTS);
        
        // Set persistent data
        meta.getPersistentDataContainer().set(idKey, PersistentDataType.STRING, traitId);
        meta.getPersistentDataContainer().set(itemTypeKey, PersistentDataType.BOOLEAN, true);
        
        item.setItemMeta(meta);
        return item;
    }
    
    /**
     * Create lore for the registration item
     */
    @NotNull
    protected List<Component> createLore(@NotNull String traitId) {
        List<Component> lore = new ArrayList<>();
        lore.add(Component.text(getIdDisplayName() + ": ", ColorUtil.UNCOMMON)
                .append(Component.text(traitId, getIdColor())));
        lore.add(Component.empty());
        lore.add(Component.text("사용법:", ColorUtil.COMMON));
        lore.add(Component.text("아무 NPC를 우클릭하여 " + getTraitName() + "를", ColorUtil.GRAY));
        lore.add(Component.text("부착할 수 있습니다.", ColorUtil.GRAY));
        lore.add(Component.empty());
        lore.add(Component.text("설명:", ColorUtil.COMMON));
        
        // Add custom description lines
        for (String line : getDescription()) {
            lore.add(Component.text(line, ColorUtil.GRAY));
        }
        
        return lore;
    }
    
    @EventHandler
    public void onNPCRightClick(PlayerInteractEntityEvent event) {
        // Check if it's the main hand to avoid double events
        if (event.getHand() != EquipmentSlot.HAND) return;
        
        // Check if the entity is a Citizens NPC
        if (!CitizensAPI.getNPCRegistry().isNPC(event.getRightClicked())) return;
        
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();
        
        // Check if it's a trait registration item
        if (item.getType() != Material.BLAZE_ROD) return;
        
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        
        if (!meta.getPersistentDataContainer().has(itemTypeKey, PersistentDataType.BOOLEAN)) return;
        
        event.setCancelled(true);
        
        // Get trait ID
        String traitId = meta.getPersistentDataContainer().get(idKey, PersistentDataType.STRING);
        if (traitId == null) {
            player.sendMessage(Component.text("오류: " + getIdType() + " ID를 찾을 수 없습니다.", ColorUtil.ERROR));
            return;
        }
        
        // Get NPC
        NPC npc = CitizensAPI.getNPCRegistry().getNPC(event.getRightClicked());
        int citizensNpcId = npc.getId();
        
        // Register trait
        Class<? extends Trait> traitClass = getTraitClass();
        if (!npc.hasTrait(traitClass)) {
            npc.addTrait(traitClass);
        }
        
        Trait trait = npc.getTraitNullable(traitClass);
        
        // Check if trait already has an ID
        if (hasTraitId(trait)) {
            String existingId = getTraitId(trait);
            if (existingId.equals(traitId)) {
                player.sendMessage(Component.text("✗ ", ColorUtil.ERROR)
                        .append(Component.text("이미 동일한 " + getIdType() + " ID가 등록되어 있습니다: ", ColorUtil.WARNING))
                        .append(Component.text(existingId, getIdColor())));
            } else {
                player.sendMessage(Component.text("✗ ", ColorUtil.ERROR)
                        .append(Component.text("이 NPC에는 이미 다른 " + getIdType() + "가 등록되어 있습니다: ", ColorUtil.WARNING))
                        .append(Component.text(existingId, getIdColor())));
                player.sendMessage(Component.text("현재 ID를 덮어쓰려면 Shift+우클릭하세요.", ColorUtil.GRAY));
                
                // Shift 클릭 시 덮어쓰기
                if (player.isSneaking()) {
                    setTraitId(trait, traitId);
                    player.sendMessage(Component.text("✓ ", ColorUtil.SUCCESS)
                            .append(Component.text(getIdType() + " ID를 ", ColorUtil.COMMON))
                            .append(Component.text(existingId, ColorUtil.GRAY))
                            .append(Component.text("에서 ", ColorUtil.COMMON))
                            .append(Component.text(traitId, getIdColor()))
                            .append(Component.text("(으)로 변경했습니다.", ColorUtil.COMMON)));
                    
                    // 막대기 제거 (크리에이티브 모드에서도 제거)
                    item.setAmount(item.getAmount() - 1);
                    // 성공 소리 재생
                    SoundUtil.playSuccessSound(player);
                }
            }
            return;
        }
        
        // 새롭게 등록
        setTraitId(trait, traitId);
        
        player.sendMessage(Component.text("✓ ", ColorUtil.SUCCESS)
                .append(Component.text("NPC ", ColorUtil.COMMON))
                .append(Component.text(npc.getName(), ColorUtil.YELLOW))
                .append(Component.text(" (Citizens ID: " + citizensNpcId + ")에게 ", ColorUtil.GRAY))
                .append(Component.text(getIdType() + " '", ColorUtil.COMMON))
                .append(Component.text(traitId, getIdColor()))
                .append(Component.text("'를 등록했습니다.", ColorUtil.COMMON)));
        
        // Send additional success message if needed
        String additionalMessage = getSuccessMessage();
        if (additionalMessage != null && !additionalMessage.isEmpty()) {
            player.sendMessage(Component.text("ℹ ", ColorUtil.INFO)
                    .append(Component.text(additionalMessage, ColorUtil.GRAY)));
        }
        
        // 막대기 제거 (크리에이티브 모드에서도 제거)
        item.setAmount(item.getAmount() - 1);
        // 성공 소리 재생
        SoundUtil.playSuccessSound(player);
    }
    
    // Abstract methods to be implemented by subclasses
    
    /**
     * Get the trait class to register
     */
    @NotNull
    protected abstract Class<? extends Trait> getTraitClass();
    
    /**
     * Check if the trait has an ID set
     */
    protected abstract boolean hasTraitId(@NotNull Trait trait);
    
    /**
     * Get the trait ID from the trait
     */
    @NotNull
    protected abstract String getTraitId(@NotNull Trait trait);
    
    /**
     * Set the trait ID on the trait
     */
    protected abstract void setTraitId(@NotNull Trait trait, @NotNull String traitId);
    
    /**
     * Get the display prefix for the item name
     */
    @NotNull
    protected abstract String getItemDisplayPrefix();
    
    /**
     * Get the color for the item name
     */
    @NotNull
    protected abstract TextColor getItemColor();
    
    /**
     * Get the type name for IDs (e.g., "NPC", "보상 NPC")
     */
    @NotNull
    protected abstract String getIdType();
    
    /**
     * Get the display name for the ID field in lore
     */
    @NotNull
    protected abstract String getIdDisplayName();
    
    /**
     * Get the color for ID display
     */
    @NotNull
    protected abstract TextColor getIdColor();
    
    /**
     * Get the trait name for display
     */
    @NotNull
    protected abstract String getTraitName();
    
    /**
     * Get the description lines for the lore
     */
    @NotNull
    protected abstract List<String> getDescription();
    
    /**
     * Get an optional success message to display after registration
     */
    @Nullable
    protected String getSuccessMessage() {
        return null;
    }
}