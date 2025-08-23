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
 * Dialog Trait Registration Item
 * NPC에 대화 ID를 설정하는 막대기
 */
public class DialogTraitRegistrationItem extends BaseTraitRegistrationItem {
    
    private static final NamespacedKey IS_DIALOG_ITEM = new NamespacedKey(RPGMain.getPlugin(), "is_dialog_trait_item");
    private static final NamespacedKey DIALOG_ID_KEY = new NamespacedKey(RPGMain.getPlugin(), "dialog_id");
    
    public DialogTraitRegistrationItem() {
        super(IS_DIALOG_ITEM, DIALOG_ID_KEY);
    }
    
    /**
     * Static factory method to create a dialog trait registration item
     */
    @NotNull
    public static ItemStack create(@NotNull String dialogId, @NotNull String displayName) {
        return new DialogTraitRegistrationItem().createRegistrationItem(dialogId, displayName);
    }
    
    @Override
    @NotNull
    protected Class<? extends Trait> getTraitClass() {
        return RPGDialogTrait.class;
    }
    
    @Override
    protected boolean hasTraitId(@NotNull Trait trait) {
        RPGDialogTrait dialogTrait = (RPGDialogTrait) trait;
        return dialogTrait.getDialogId() != null && !dialogTrait.getDialogId().isEmpty();
    }
    
    @Override
    @NotNull
    protected String getTraitId(@NotNull Trait trait) {
        RPGDialogTrait dialogTrait = (RPGDialogTrait) trait;
        return dialogTrait.getDialogId() != null ? dialogTrait.getDialogId() : "";
    }
    
    @Override
    protected void setTraitId(@NotNull Trait trait, @NotNull String traitId) {
        RPGDialogTrait dialogTrait = (RPGDialogTrait) trait;
        dialogTrait.setDialogId(traitId);
        
        // 손에 종이 아이템 설정
        var npc = trait.getNPC();
        if (npc != null) {
            var equipment = npc.getOrAddTrait(net.citizensnpcs.api.trait.trait.Equipment.class);
            equipment.set(net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot.HAND, 
                    new ItemStack(Material.PAPER));
        }
    }
    
    
    @Override
    @NotNull
    protected String getItemDisplayPrefix() {
        return "대화 NPC 설정기";
    }
    
    @Override
    @NotNull
    protected TextColor getItemColor() {
        return UnifiedColorUtil.COMMON;
    }
    
    @Override
    @NotNull
    protected String getIdType() {
        return "대화 ID";
    }
    
    @Override
    @NotNull
    protected String getIdDisplayName() {
        return "대화 ID";
    }
    
    @Override
    @NotNull
    protected TextColor getIdColor() {
        return UnifiedColorUtil.GRAY;
    }
    
    @Override
    @NotNull
    protected String getTraitName() {
        return "대화 Trait";
    }
    
    @Override
    @NotNull
    protected List<String> getDescription() {
        return List.of(
                "이 Trait가 부착된 NPC는",
                "lang 파일에 정의된 대화를",
                "랜덤하게 표시합니다."
        );
    }
    
    @Override
    @NotNull
    protected String getSuccessMessage() {
        return "이제 이 NPC는 lang 파일의 대화를 랜덤하게 표시합니다.";
    }
}