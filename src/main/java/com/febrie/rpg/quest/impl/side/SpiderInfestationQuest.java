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
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.lang.quest.side.SpiderInfestationLangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * Side Quest: Spider Infestation
 * Clear the warehouse of monstrous spiders and their queen
 *
 * @author Febrie
 */
public class SpiderInfestationQuest extends Quest {

    /**
     * 기본 생성자
     */
    public SpiderInfestationQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_SPIDER_INFESTATION)
                .objectives(List.of(
                        new InteractNPCObjective("talk_warehouse_foreman", "warehouse_foreman"),
                        new VisitLocationObjective("infested_warehouse", "Infested_Warehouse"),
                        new KillMobObjective("giant_spiders", EntityType.SPIDER, 20),
                        new CollectItemObjective("string_collect", Material.STRING, 25),
                        new VisitLocationObjective("spider_nests", "Spider_Nests"),
                        new CollectItemObjective("cobweb_collect", Material.COBWEB, 15),
                        new VisitLocationObjective("queens_chamber", "Queens_Chamber"),
                        new KillMobObjective("spider_queen", EntityType.CAVE_SPIDER, 1),
                        new InteractNPCObjective("report_completion", "warehouse_foreman")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(1700)
                        .addCurrency(CurrencyType.GOLD, 420)
                        .addItem(new ItemStack(Material.STRING, 32))
                        .addItem(new ItemStack(Material.SPIDER_EYE, 10))
                        .addItem(new ItemStack(Material.BOW, 1))
                        .build())
                .sequential(false)
                .category(QuestCategory.SIDE)
                .minLevel(13);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(SpiderInfestationLangKey.QUEST_SIDE_SPIDER_INFESTATION_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(SpiderInfestationLangKey.QUEST_SIDE_SPIDER_INFESTATION_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_warehouse_foreman" -> LangManager.text(SpiderInfestationLangKey.QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_TALK_WAREHOUSE_FOREMAN, who);
            case "infested_warehouse" -> LangManager.text(SpiderInfestationLangKey.QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_INFESTED_WAREHOUSE, who);
            case "giant_spiders" -> LangManager.text(SpiderInfestationLangKey.QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_GIANT_SPIDERS, who);
            case "string_collect" -> LangManager.text(SpiderInfestationLangKey.QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_STRING_COLLECT, who);
            case "spider_nests" -> LangManager.text(SpiderInfestationLangKey.QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_SPIDER_NESTS, who);
            case "cobweb_collect" -> LangManager.text(SpiderInfestationLangKey.QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_COBWEB_COLLECT, who);
            case "queens_chamber" -> LangManager.text(SpiderInfestationLangKey.QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_QUEENS_CHAMBER, who);
            case "spider_queen" -> LangManager.text(SpiderInfestationLangKey.QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_SPIDER_QUEEN, who);
            case "report_completion" -> LangManager.text(SpiderInfestationLangKey.QUEST_SIDE_SPIDER_INFESTATION_OBJECTIVES_REPORT_COMPLETION, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(SpiderInfestationLangKey.QUEST_SIDE_SPIDER_INFESTATION_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(SpiderInfestationLangKey.QUEST_SIDE_SPIDER_INFESTATION_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(SpiderInfestationLangKey.QUEST_SIDE_SPIDER_INFESTATION_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(SpiderInfestationLangKey.QUEST_SIDE_SPIDER_INFESTATION_DECLINE, who);
    }
}