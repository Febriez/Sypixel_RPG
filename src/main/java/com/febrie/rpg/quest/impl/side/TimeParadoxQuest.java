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

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * Side Quest: Time Paradox
 * Fix the temporal rifts before reality collapses
 *
 * @author Febrie
 */
public class TimeParadoxQuest extends Quest {

    /**
     * 기본 생성자
     */
    public TimeParadoxQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_TIME_PARADOX)
                .objectives(List.of(
                        new InteractNPCObjective("talk_chrono_mage", "chrono_mage"),
                        new VisitLocationObjective("temporal_rift_1", "Temporal_Rift_1"),
                        new CollectItemObjective("clock_collect", Material.CLOCK, 10),
                        new VisitLocationObjective("temporal_rift_2", "Temporal_Rift_2"),
                        new CollectItemObjective("amethyst_shard_collect", Material.AMETHYST_SHARD, 15),
                        new VisitLocationObjective("temporal_rift_3", "Temporal_Rift_3"),
                        new CollectItemObjective("experience_bottle_collect", Material.EXPERIENCE_BOTTLE, 12),
                        new VisitLocationObjective("time_nexus", "Time_Nexus"),
                        new InteractNPCObjective("stabilize_timeline", "chrono_mage")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(2600)
                        .addCurrency(CurrencyType.GOLD, 650)
                        .addItem(new ItemStack(Material.CLOCK, 5))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 4))
                        .addItem(new ItemStack(Material.AMETHYST_BLOCK, 3))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(24);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.time.paradox.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.time.paradox.info"), who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_chrono_mage" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.time.paradox.objectives.talk.chrono.mage"), who);
            case "temporal_rift_1" -> LangManager.text(QuestCommonLangKey.QUEST_SIDE_TIME_PARADOX_OBJECTIVES_TEMPORAL_RIFT_, who);
            case "clock_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.time.paradox.objectives.clock.collect"), who);
            case "temporal_rift_2" -> LangManager.text(QuestCommonLangKey.QUEST_SIDE_TIME_PARADOX_OBJECTIVES_TEMPORAL_RIFT_2, who);
            case "amethyst_shard_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.time.paradox.objectives.amethyst.shard.collect"), who);
            case "temporal_rift_3" -> LangManager.text(QuestCommonLangKey.QUEST_SIDE_TIME_PARADOX_OBJECTIVES_TEMPORAL_RIFT_3, who);
            case "experience_bottle_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.time.paradox.objectives.experience.bottle.collect"), who);
            case "time_nexus" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.time.paradox.objectives.time.nexus"), who);
            case "stabilize_timeline" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.time.paradox.objectives.stabilize.timeline"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.time.paradox.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.time.paradox.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.time.paradox.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.time.paradox.decline"), who);
    }
}