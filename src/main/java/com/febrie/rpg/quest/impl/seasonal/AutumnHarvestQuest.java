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
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
                        new CollectItemObjective("harvest_wheat", Material.WHEAT, 100),
                        new CollectItemObjective("collect_pumpkins", Material.PUMPKIN, 50),
                        new CollectItemObjective("collect_apples", Material.APPLE, 64),
                        new CraftItemObjective("craft_pumpkin_pie", Material.PUMPKIN_PIE, 20)
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
        return LangManager.text(LangKey.QUEST_SEASONAL_AUTUMN_HARVEST_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SEASONAL_AUTUMN_HARVEST_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "harvest_wheat" -> LangManager.list(LangKey.QUEST_SEASONAL_AUTUMN_HARVEST_OBJECTIVES_HARVEST_WHEAT, who);
            case "collect_pumpkins" -> LangManager.list(LangKey.QUEST_SEASONAL_AUTUMN_HARVEST_OBJECTIVES_COLLECT_PUMPKINS, who);
            case "collect_apples" -> LangManager.list(LangKey.QUEST_SEASONAL_AUTUMN_HARVEST_OBJECTIVES_COLLECT_APPLES, who);
            case "craft_pumpkin_pie" -> LangManager.list(LangKey.QUEST_SEASONAL_AUTUMN_HARVEST_OBJECTIVES_CRAFT_PUMPKIN_PIE, who);
            default -> new ArrayList<>();
        };
    }
    
    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SEASONAL_AUTUMN_HARVEST_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_AUTUMN_HARVEST_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_AUTUMN_HARVEST_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_AUTUMN_HARVEST_DECLINE, who);
    }
}