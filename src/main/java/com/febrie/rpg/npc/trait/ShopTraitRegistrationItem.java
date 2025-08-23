package com.febrie.rpg.npc.trait;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.npc.trait.base.BaseTraitRegistrationItem;
import com.febrie.rpg.util.UnifiedColorUtil;
import net.citizensnpcs.api.trait.Trait;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Shop Trait Registration Item
 * NPC를 상점으로 설정하는 막대기
 */
public class ShopTraitRegistrationItem extends BaseTraitRegistrationItem {
    
    private static final NamespacedKey IS_SHOP_ITEM = new NamespacedKey(RPGMain.getPlugin(), "is_shop_trait_item");
    private static final NamespacedKey SHOP_TYPE_KEY = new NamespacedKey(RPGMain.getPlugin(), "shop_type");
    
    public ShopTraitRegistrationItem() {
        super(IS_SHOP_ITEM, SHOP_TYPE_KEY);
    }
    
    /**
     * Static factory method to create a shop trait registration item
     */
    @NotNull
    public static ItemStack create(@NotNull String shopType, @NotNull String displayName) {
        return new ShopTraitRegistrationItem().createRegistrationItem(shopType, displayName);
    }
    
    @Override
    @NotNull
    protected Class<? extends Trait> getTraitClass() {
        return RPGShopTrait.class;
    }
    
    @Override
    protected boolean hasTraitId(@NotNull Trait trait) {
        RPGShopTrait shopTrait = (RPGShopTrait) trait;
        return shopTrait.getShopType() != null && !shopTrait.getShopType().isEmpty();
    }
    
    @Override
    @NotNull
    protected String getTraitId(@NotNull Trait trait) {
        RPGShopTrait shopTrait = (RPGShopTrait) trait;
        return shopTrait.getShopType() != null ? shopTrait.getShopType() : "";
    }
    
    @Override
    protected void setTraitId(@NotNull Trait trait, @NotNull String traitId) {
        RPGShopTrait shopTrait = (RPGShopTrait) trait;
        shopTrait.setNpcType("SHOP");
        shopTrait.setShopType(traitId);
        
        // 손에 에메랄드 아이템 설정
        var npc = trait.getNPC();
        if (npc != null) {
            var equipment = npc.getOrAddTrait(net.citizensnpcs.api.trait.trait.Equipment.class);
            equipment.set(net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot.HAND, 
                    new ItemStack(Material.EMERALD));
        }
    }
    
    @Override
    @NotNull
    protected String getItemDisplayPrefix() {
        return "상점 NPC 설정기";
    }
    
    @Override
    @NotNull
    protected TextColor getItemColor() {
        return UnifiedColorUtil.UNCOMMON;
    }
    
    @Override
    @NotNull
    protected String getIdType() {
        return "상점 타입";
    }
    
    @Override
    @NotNull
    protected String getIdDisplayName() {
        return "상점 타입";
    }
    
    @Override
    @NotNull
    protected TextColor getIdColor() {
        return UnifiedColorUtil.GOLD;
    }
    
    @Override
    @NotNull
    protected String getTraitName() {
        return "상점 Trait";
    }
    
    @Override
    @NotNull
    protected List<String> getDescription() {
        return List.of(
                "이 Trait가 부착된 NPC는",
                "지정된 타입의 상점이 됩니다."
        );
    }
    
    @Override
    @NotNull
    protected String getSuccessMessage() {
        return "이제 이 NPC는 상점으로 작동합니다.";
    }
}