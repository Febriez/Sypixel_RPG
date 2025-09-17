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
 * Side Quest: Dragon Scout
 * Investigate dragon sightings and assess the threat level
 *
 * @author Febrie
 */
public class DragonScoutQuest extends Quest {

    /**
     * 기본 생성자
     */
    public DragonScoutQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_DRAGON_SCOUT)
                .objectives(List.of(
                        new InteractNPCObjective("talk_town_guard", "town_guard"),
                        new VisitLocationObjective("mountain_peaks", "Mountain_Peaks"),
                        new CollectItemObjective("scute_collect", Material.TURTLE_SCUTE, 5),
                        new CollectItemObjective("spyglass_collect", Material.SPYGLASS, 2),
                        new VisitLocationObjective("dragon_lair", "Dragon_Lair"),
                        new CollectItemObjective("dragon_breath_collect", Material.DRAGON_BREATH, 3),
                        new VisitLocationObjective("observation_post", "Observation_Post"),
                        new InteractNPCObjective("report_findings", "commander")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(1800)
                        .addCurrency(CurrencyType.GOLD, 450)
                        .addItem(new ItemStack(Material.DRAGON_EGG, 1))
                        .addItem(new ItemStack(Material.ELYTRA, 1))
                        .addItem(new ItemStack(Material.SPYGLASS, 1))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(22);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.dragon.scout.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.dragon.scout.info"), who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_town_guard" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.dragon.scout.objectives.talk.town.guard"), who);
            case "mountain_peaks" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.dragon.scout.objectives.mountain.peaks"), who);
            case "scute_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.dragon.scout.objectives.scute.collect"), who);
            case "spyglass_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.dragon.scout.objectives.spyglass.collect"), who);
            case "dragon_lair" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.dragon.scout.objectives.dragon.lair"), who);
            case "dragon_breath_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.dragon.scout.objectives.dragon.breath.collect"), who);
            case "observation_post" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.dragon.scout.objectives.observation.post"), who);
            case "report_findings" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.dragon.scout.objectives.report.findings"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.dragon.scout.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.dragon.scout.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.dragon.scout.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.dragon.scout.decline"), who);
    }
}