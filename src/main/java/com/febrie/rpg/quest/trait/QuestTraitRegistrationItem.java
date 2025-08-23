package com.febrie.rpg.quest.trait;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.npc.trait.RPGQuestTrait;
import com.febrie.rpg.npc.trait.base.ImprovedBaseTraitRegistrationItem;
import net.citizensnpcs.api.trait.Trait;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Quest Trait Registration Item
 * Allows attaching quest traits to NPCs by right-clicking
 */
public class QuestTraitRegistrationItem extends ImprovedBaseTraitRegistrationItem {
    
    public QuestTraitRegistrationItem(@NotNull RPGMain plugin) {
        super(plugin);
    }
    
    /**
     * Static factory method to create a quest trait registration item
     */
    @NotNull
    public static ItemStack create(@NotNull String traitId, @NotNull String displayName) {
        return new QuestTraitRegistrationItem(RPGMain.getPlugin()).createRegistrationItem(traitId, displayName);
    }
    
    @Override
    @NotNull
    protected String getTraitType() {
        return "quest";
    }
    
    @Override
    @NotNull
    protected String getItemDisplayPrefix() {
        return "Quest NPC";
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
        trait.setNpcId(traitId);
        return trait;
    }
    
    @Override
    @NotNull
    protected String getSuccessMessage(@NotNull String traitId, int npcId) {
        return "&aNPC #" + npcId + "에 퀘스트 '" + traitId + "'를 설정했습니다";
    }
}