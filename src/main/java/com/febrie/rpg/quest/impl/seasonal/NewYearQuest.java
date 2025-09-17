package com.febrie.rpg.quest.impl.seasonal;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.CraftItemObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import java.util.ArrayList;
import java.util.List;

/**
 * 신년 축제 - 계절 퀘스트
 * 새해를 축하하는 퀘스트
 *
 * @author Febrie
 */
public class NewYearQuest extends Quest {

    /**
     * 기본 생성자
     */
    public NewYearQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SEASON_NEW_YEAR)
                .objectives(List.of(
                        new CollectItemObjective("firework_rocket_collect", Material.FIREWORK_ROCKET, 50),
                        new CraftItemObjective("cake_craft", Material.CAKE, 10),
                        new VisitLocationObjective("visit_celebration_area", "celebration_plaza"),
                        new InteractNPCObjective("make_new_year_resolution", "resolution_npc")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 400)
                        .addItem(new ItemStack(Material.FIREWORK_ROCKET, 64))
                        .addItem(new ItemStack(Material.GOLD_INGOT, 20))
                        .addExperience(500)
                        .build())
                .sequential(false)
                .category(QuestCategory.EVENT)
                .minLevel(8);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_NEW_YEAR_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SEASONAL_NEW_YEAR_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "firework_rocket_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_NEW_YEAR_OBJECTIVES_FIREWORK_ROCKET_COLLECT, who);
            case "cake_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_NEW_YEAR_OBJECTIVES_CAKE_CRAFT, who);
            case "visit_celebration_area" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_NEW_YEAR_OBJECTIVES_VISIT_CELEBRATION_AREA, who);
            case "make_new_year_resolution" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_NEW_YEAR_OBJECTIVES_MAKE_NEW_YEAR_RESOLUTION, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SEASONAL_NEW_YEAR_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_NEW_YEAR_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_NEW_YEAR_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_NEW_YEAR_DECLINE, who);
    }
}