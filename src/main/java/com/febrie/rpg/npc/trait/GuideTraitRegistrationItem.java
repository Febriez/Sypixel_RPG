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

import net.kyori.adventure.text.Component;
/**
 * Guide Trait Registration Item
 * NPC를 가이드로 설정하는 막대기
 */
public class GuideTraitRegistrationItem extends BaseTraitRegistrationItem {
    
    private static final NamespacedKey IS_GUIDE_ITEM = new NamespacedKey(RPGMain.getPlugin(), "is_guide_trait_item");
    private static final NamespacedKey GUIDE_TYPE_KEY = new NamespacedKey(RPGMain.getPlugin(), "guide_type");
    
    public GuideTraitRegistrationItem() {
        super(IS_GUIDE_ITEM, GUIDE_TYPE_KEY);
    }
    
    /**
     * Static factory method to create a guide trait registration item
     */
    @NotNull
    public static ItemStack create(@NotNull String guideType, @NotNull String displayName) {
        return new GuideTraitRegistrationItem().createRegistrationItem(guideType, displayName);
    }
    
    @Override
    @NotNull
    protected Class<? extends Trait> getTraitClass() {
        return RPGGuideTrait.class;
    }
    
    @Override
    protected boolean hasTraitId(@NotNull Trait trait) {
        RPGGuideTrait guideTrait = (RPGGuideTrait) trait;
        return guideTrait.getGuideType() != null && !guideTrait.getGuideType().isEmpty();
    }
    
    @Override
    @NotNull
    protected String getTraitId(@NotNull Trait trait) {
        RPGGuideTrait guideTrait = (RPGGuideTrait) trait;
        return guideTrait.getGuideType() != null ? guideTrait.getGuideType() : "";
    }
    
    @Override
    protected void setTraitId(@NotNull Trait trait, @NotNull String traitId) {
        RPGGuideTrait guideTrait = (RPGGuideTrait) trait;
        guideTrait.setNpcType("GUIDE");
        guideTrait.setGuideType(traitId);
        
        // 손에 나침반 아이템 설정
        var npc = trait.getNPC();
        if (npc != null) {
            var equipment = npc.getOrAddTrait(net.citizensnpcs.api.trait.trait.Equipment.class);
            equipment.set(net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot.HAND, 
                    new ItemStack(Material.COMPASS));
        }
    }
    
    @Override
    @NotNull
    protected String getItemDisplayPrefix() {
        return "가이드 NPC 설정기";
    }
    
    @Override
    @NotNull
    protected TextColor getItemColor() {
        return UnifiedColorUtil.RARE;
    }
    
    @Override
    @NotNull
    protected String getIdType() {
        return "가이드 타입";
    }
    
    @Override
    @NotNull
    protected String getIdDisplayName() {
        return "가이드 타입";
    }
    
    @Override
    @NotNull
    protected TextColor getIdColor() {
        return UnifiedColorUtil.UNCOMMON;
    }
    
    @Override
    @NotNull
    protected String getTraitName() {
        return "가이드 Trait";
    }
    
    @Override
    @NotNull
    protected List<String> getDescription() {
        return List.of(
                "이 Trait가 부착된 NPC는",
                "메인 메뉴를 여는 가이드가 됩니다."
        );
    }
    
    @Override
    @NotNull
    protected String getSuccessMessage() {
        return "이제 이 NPC는 가이드로 작동합니다.";
    }
}