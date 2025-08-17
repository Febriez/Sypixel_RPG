package com.febrie.rpg.quest.trait;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.npc.trait.RPGQuestRewardTrait;
import com.febrie.rpg.npc.trait.base.ImprovedBaseTraitRegistrationItem;
import net.citizensnpcs.api.trait.Trait;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Reward Trait Registration Item
 * Allows attaching reward traits to NPCs by right-clicking
 */
public class RewardTraitRegistrationItem extends ImprovedBaseTraitRegistrationItem {
    
    public RewardTraitRegistrationItem(@NotNull RPGMain plugin) {
        super(plugin);
    }
    
    /**
     * Static factory method to create a reward trait registration item
     */
    @NotNull
    public static ItemStack create(@NotNull String traitId, @NotNull String displayName) {
        return new RewardTraitRegistrationItem(RPGMain.getPlugin()).createRegistrationItem(traitId, displayName);
    }
    
    @Override
    @NotNull
    protected String getTraitType() {
        return "reward";
    }
    
    @Override
    @NotNull
    protected String getItemDisplayPrefix() {
        return "Reward NPC";
    }
    
    @Override
    @NotNull
    protected NamedTextColor getItemColor() {
        return NamedTextColor.GOLD;
    }
    
    @Override
    @NotNull
    protected Class<? extends Trait> getTraitClass() {
        return RPGQuestRewardTrait.class;
    }
    
    @Override
    @NotNull
    protected Trait createTrait(@NotNull String traitId) {
        RPGQuestRewardTrait trait = new RPGQuestRewardTrait();
        trait.setRewardNpcId(traitId);
        return trait;
    }
    
    @Override
    @NotNull
    protected String getSuccessMessage(@NotNull String traitId, int npcId) {
        return "&aNPC #" + npcId + "에 보상 '" + traitId + "'를 설정했습니다";
    }
}