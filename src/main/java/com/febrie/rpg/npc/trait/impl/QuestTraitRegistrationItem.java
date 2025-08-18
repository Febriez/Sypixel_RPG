package com.febrie.rpg.npc.trait.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.npc.trait.base.ImprovedBaseTraitRegistrationItem;
import com.febrie.rpg.npc.trait.RPGQuestTrait;
import net.citizensnpcs.api.trait.Trait;
import net.kyori.adventure.text.format.NamedTextColor;
import org.jetbrains.annotations.NotNull;

/**
 * 퀘스트 NPC Trait 등록 아이템
 * ImprovedBaseTraitRegistrationItem을 사용한 구현
 *
 * @author Febrie, CoffeeTory
 */
public class QuestTraitRegistrationItem extends ImprovedBaseTraitRegistrationItem {
    
    private QuestTraitRegistrationItem(@NotNull RPGMain plugin) {
        super(plugin);
    }
    
    public static QuestTraitRegistrationItem create(@NotNull RPGMain plugin) {
        QuestTraitRegistrationItem item = new QuestTraitRegistrationItem(plugin);
        plugin.getServer().getPluginManager().registerEvents(item, plugin);
        return item;
    }
    
    @Override
    @NotNull
    protected String getTraitType() {
        return "quest";
    }
    
    @Override
    @NotNull
    protected String getItemDisplayPrefix() {
        return "퀘스트 NPC";
    }
    
    @Override
    @NotNull
    protected NamedTextColor getItemColor() {
        return NamedTextColor.YELLOW;
    }
    
    @Override
    @NotNull
    protected Class<? extends Trait> getTraitClass() {
        return RPGQuestTrait.class;
    }
    
    @Override
    @NotNull
    protected Trait createTrait(@NotNull String traitId) {
        RPGQuestTrait trait = new RPGQuestTrait();
        // Store the traitId in the trait's data if needed
        return trait;
    }
    
    @Override
    @NotNull
    protected String getSuccessMessage(@NotNull String traitId, int npcId) {
        return String.format("&aNPC #%d를 퀘스트 NPC '%s'로 설정했습니다", npcId, traitId);
    }
}