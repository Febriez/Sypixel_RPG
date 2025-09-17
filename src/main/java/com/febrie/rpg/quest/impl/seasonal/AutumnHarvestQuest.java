package com.febrie.rpg.quest.impl.seasonal;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.CraftItemObjective;
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
 * 가을 수확제 - 계절 퀘스트
 * 가을 수확에 관련된 퀘스트
 *
 * @author Febrie
 */
public class AutumnHarvestQuest extends Quest {

    /**
     * 기본 생성자
     */
    public AutumnHarvestQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SEASON_AUTUMN_HARVEST)
                .objectives(List.of(
                        new CollectItemObjective("wheat_collect", Material.WHEAT, 100),
                        new CollectItemObjective("pumpkin_collect", Material.PUMPKIN, 50),
                        new CollectItemObjective("apple_collect", Material.APPLE, 64),
                        new CraftItemObjective("pumpkin_pie_craft", Material.PUMPKIN_PIE, 20)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 300)
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 5))
                        .addItem(new ItemStack(Material.CAKE, 3))
                        .addExperience(400)
                        .build())
                .sequential(false)
                .category(QuestCategory.EVENT)
                .minLevel(10);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_AUTUMN_HARVEST_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SEASONAL_AUTUMN_HARVEST_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "wheat_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_AUTUMN_HARVEST_OBJECTIVES_WHEAT_COLLECT, who);
            case "pumpkin_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_AUTUMN_HARVEST_OBJECTIVES_PUMPKIN_COLLECT, who);
            case "apple_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_AUTUMN_HARVEST_OBJECTIVES_APPLE_COLLECT, who);
            case "pumpkin_pie_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_AUTUMN_HARVEST_OBJECTIVES_PUMPKIN_PIE_CRAFT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SEASONAL_AUTUMN_HARVEST_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_AUTUMN_HARVEST_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_AUTUMN_HARVEST_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_AUTUMN_HARVEST_DECLINE, who);
    }
}