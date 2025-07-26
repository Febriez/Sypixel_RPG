package com.febrie.rpg.quest.trait;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.npc.trait.RPGQuestRewardTrait;
import com.febrie.rpg.npc.trait.base.BaseTraitRegistrationItem;
import com.febrie.rpg.util.ColorUtil;
import net.citizensnpcs.api.trait.Trait;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Reward Trait Registration Item
 * Allows attaching quest reward traits to NPCs by right-clicking
 */
public class RewardTraitRegistrationItem extends BaseTraitRegistrationItem {
    
    private static final NamespacedKey IS_REWARD_ITEM = new NamespacedKey(RPGMain.getPlugin(), "is_reward_trait_item");
    private static final NamespacedKey REWARD_NPC_ID_KEY = new NamespacedKey(RPGMain.getPlugin(), "reward_npc_id");
    
    public RewardTraitRegistrationItem() {
        super(IS_REWARD_ITEM, REWARD_NPC_ID_KEY);
    }
    
    /**
     * Static factory method to create a reward trait registration item
     */
    @NotNull
    public static ItemStack create(@NotNull String traitId, @NotNull String displayName) {
        return new RewardTraitRegistrationItem().createRegistrationItem(traitId, displayName);
    }
    
    // Implement abstract methods from base class
    
    @Override
    @NotNull
    protected Class<? extends Trait> getTraitClass() {
        return RPGQuestRewardTrait.class;
    }
    
    @Override
    protected boolean hasTraitId(@NotNull Trait trait) {
        return ((RPGQuestRewardTrait) trait).hasRewardNpcId();
    }
    
    @Override
    @NotNull
    protected String getTraitId(@NotNull Trait trait) {
        return ((RPGQuestRewardTrait) trait).getRewardNpcId();
    }
    
    @Override
    protected void setTraitId(@NotNull Trait trait, @NotNull String traitId) {
        ((RPGQuestRewardTrait) trait).setRewardNpcId(traitId);
    }
    
    @Override
    @NotNull
    protected String getItemDisplayPrefix() {
        return "보상 NPC 등록기";
    }
    
    @Override
    @NotNull
    protected TextColor getItemColor() {
        return ColorUtil.EPIC;
    }
    
    @Override
    @NotNull
    protected String getIdType() {
        return "보상 ID";
    }
    
    @Override
    @NotNull
    protected String getIdDisplayName() {
        return "보상 NPC ID";
    }
    
    @Override
    @NotNull
    protected TextColor getIdColor() {
        return ColorUtil.EPIC;
    }
    
    @Override
    @NotNull
    protected String getTraitName() {
        return "보상 Trait";
    }
    
    @Override
    @NotNull
    protected List<String> getDescription() {
        return List.of(
                "이 Trait가 부착된 NPC는 완료된",
                "퀘스트의 보상을 지급합니다."
        );
    }
    
    @Override
    @Nullable
    protected String getSuccessMessage() {
        return "이제 이 NPC는 완료된 퀘스트의 보상을 지급합니다.";
    }
}