package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.lang.quest.side.SoulFragmentsLangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * Side Quest: Soul Fragments
 * Gather scattered soul fragments to mend the spiritual realm
 *
 * @author Febrie
 */
public class SoulFragmentsQuest extends Quest {

    /**
     * 기본 생성자
     */
    public SoulFragmentsQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_SOUL_FRAGMENTS)
                .objectives(List.of(
                        new InteractNPCObjective("talk_spirit_guide", "spirit_guide"),
                        new VisitLocationObjective("spiritual_barrier", "Spiritual_Barrier"),
                        new CollectItemObjective("ghast_tear_collect", Material.GHAST_TEAR, 15),
                        new CollectItemObjective("glowstone_dust_collect", Material.GLOWSTONE_DUST, 12),
                        new VisitLocationObjective("soul_nexus", "Soul_Nexus"),
                        new CollectItemObjective("soul_torch_collect", Material.SOUL_TORCH, 10),
                        new VisitLocationObjective("spirit_shrine", "Spirit_Shrine"),
                        new InteractNPCObjective("restore_souls", "spirit_guide")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(2200)
                        .addCurrency(CurrencyType.GOLD, 550)
                        .addItem(new ItemStack(Material.SOUL_LANTERN, 8))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 3))
                        .addItem(new ItemStack(Material.GHAST_TEAR, 5))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(18);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(SoulFragmentsLangKey.QUEST_SIDE_SOUL_FRAGMENTS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(SoulFragmentsLangKey.QUEST_SIDE_SOUL_FRAGMENTS_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_spirit_guide" -> LangManager.text(SoulFragmentsLangKey.QUEST_SIDE_SOUL_FRAGMENTS_OBJECTIVES_TALK_SPIRIT_GUIDE, who);
            case "spiritual_barrier" -> LangManager.text(SoulFragmentsLangKey.QUEST_SIDE_SOUL_FRAGMENTS_OBJECTIVES_SPIRITUAL_BARRIER, who);
            case "ghast_tear_collect" -> LangManager.text(SoulFragmentsLangKey.QUEST_SIDE_SOUL_FRAGMENTS_OBJECTIVES_GHAST_TEAR_COLLECT, who);
            case "glowstone_dust_collect" -> LangManager.text(SoulFragmentsLangKey.QUEST_SIDE_SOUL_FRAGMENTS_OBJECTIVES_GLOWSTONE_DUST_COLLECT, who);
            case "soul_nexus" -> LangManager.text(SoulFragmentsLangKey.QUEST_SIDE_SOUL_FRAGMENTS_OBJECTIVES_SOUL_NEXUS, who);
            case "soul_torch_collect" -> LangManager.text(SoulFragmentsLangKey.QUEST_SIDE_SOUL_FRAGMENTS_OBJECTIVES_SOUL_TORCH_COLLECT, who);
            case "spirit_shrine" -> LangManager.text(SoulFragmentsLangKey.QUEST_SIDE_SOUL_FRAGMENTS_OBJECTIVES_SPIRIT_SHRINE, who);
            case "restore_souls" -> LangManager.text(SoulFragmentsLangKey.QUEST_SIDE_SOUL_FRAGMENTS_OBJECTIVES_RESTORE_SOULS, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(SoulFragmentsLangKey.QUEST_SIDE_SOUL_FRAGMENTS_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(SoulFragmentsLangKey.QUEST_SIDE_SOUL_FRAGMENTS_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(SoulFragmentsLangKey.QUEST_SIDE_SOUL_FRAGMENTS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(SoulFragmentsLangKey.QUEST_SIDE_SOUL_FRAGMENTS_DECLINE, who);
    }
}