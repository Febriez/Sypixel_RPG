package com.febrie.rpg.npc.trait.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.npc.trait.base.ImprovedBaseTraitRegistrationItem;
import com.febrie.rpg.npc.trait.RPGQuestRewardTrait;
import net.citizensnpcs.api.trait.Trait;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

import net.kyori.adventure.text.Component;
/**
 * 보상 NPC Trait 등록 아이템
 * ImprovedBaseTraitRegistrationItem을 사용한 구현
 *
 * @author Febrie, CoffeeTory
 */
public class RewardTraitRegistrationItem extends ImprovedBaseTraitRegistrationItem {
    
    private RewardTraitRegistrationItem(@NotNull RPGMain plugin) {
        super(plugin);
    }
    
    public static RewardTraitRegistrationItem create(@NotNull RPGMain plugin) {
        RewardTraitRegistrationItem item = new RewardTraitRegistrationItem(plugin);
        plugin.getServer().getPluginManager().registerEvents(item, plugin);
        return item;
    }
    
    @Override
    @NotNull
    protected String getTraitType() {
        return "reward";
    }
    
    @Override
    @NotNull
    protected String getItemDisplayPrefix() {
        return "보상 NPC";
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
        // Store the traitId in the trait's data if needed
        return trait;
    }
    
    @Override
    @NotNull
    protected String getSuccessMessage(@NotNull String traitId, int npcId) {
        return String.format("&aNPC #%d를 보상 NPC '%s'로 설정했습니다", npcId, traitId);
    }
}