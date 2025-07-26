package com.febrie.rpg.quest.trait;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.npc.trait.RPGQuestTrait;
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
 * Quest Trait Registration Item
 * Allows attaching quest traits to NPCs by right-clicking
 */
public class QuestTraitRegistrationItem extends BaseTraitRegistrationItem {
    
    private static final NamespacedKey IS_QUEST_ITEM = new NamespacedKey(RPGMain.getPlugin(), "is_quest_trait_item");
    private static final NamespacedKey NPC_ID_KEY = new NamespacedKey(RPGMain.getPlugin(), "npc_id");
    
    public QuestTraitRegistrationItem() {
        super(IS_QUEST_ITEM, NPC_ID_KEY);
    }
    
    /**
     * Static factory method to create a quest trait registration item
     */
    @NotNull
    public static ItemStack create(@NotNull String traitId, @NotNull String displayName) {
        return new QuestTraitRegistrationItem().createRegistrationItem(traitId, displayName);
    }
    
    // Implement abstract methods from base class
    
    @Override
    @NotNull
    protected Class<? extends Trait> getTraitClass() {
        return RPGQuestTrait.class;
    }
    
    @Override
    protected boolean hasTraitId(@NotNull Trait trait) {
        return ((RPGQuestTrait) trait).hasNpcId();
    }
    
    @Override
    @NotNull
    protected String getTraitId(@NotNull Trait trait) {
        return ((RPGQuestTrait) trait).getNpcId();
    }
    
    @Override
    protected void setTraitId(@NotNull Trait trait, @NotNull String traitId) {
        ((RPGQuestTrait) trait).setNpcId(traitId);
    }
    
    @Override
    @NotNull
    protected String getItemDisplayPrefix() {
        return "NPC Trait 등록기";
    }
    
    @Override
    @NotNull
    protected TextColor getItemColor() {
        return ColorUtil.LEGENDARY;
    }
    
    @Override
    @NotNull
    protected String getIdType() {
        return "NPC ID";
    }
    
    @Override
    @NotNull
    protected String getIdDisplayName() {
        return "NPC ID";
    }
    
    @Override
    @NotNull
    protected TextColor getIdColor() {
        return ColorUtil.RARE;
    }
    
    @Override
    @NotNull
    protected String getTraitName() {
        return "Trait";
    }
    
    @Override
    @NotNull
    protected List<String> getDescription() {
        return List.of(
                "이 ID가 부착된 NPC와 상호작용 시",
                "관련 퀘스트 목표가 달성됩니다."
        );
    }
}