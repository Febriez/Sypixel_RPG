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
 * 부활절 달걀 사냥 - 계절 퀘스트
 * 부활절 달걀을 찾는 퀘스트
 *
 * @author Febrie
 */
public class EasterEggsQuest extends Quest {

    /**
     * 기본 생성자
     */
    public EasterEggsQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SEASON_EASTER_EGGS)
                .objectives(List.of(
                        new CollectItemObjective("egg_collect", Material.EGG, 20),
                        new CollectItemObjective("dandelion_collect", Material.DANDELION, 30),
                        new CraftItemObjective("cyan_dye_craft", Material.CYAN_DYE, 10),
                        new CollectItemObjective("golden_apple_collect", Material.GOLDEN_APPLE, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 250)
                        .addItem(new ItemStack(Material.RABBIT_SPAWN_EGG, 1))
                        .addItem(new ItemStack(Material.CARROT, 32))
                        .addExperience(300)
                        .build())
                .sequential(false)
                .category(QuestCategory.EVENT)
                .minLevel(5);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_EASTER_EGGS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SEASONAL_EASTER_EGGS_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "egg_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_EASTER_EGGS_OBJECTIVES_EGG_COLLECT, who);
            case "dandelion_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_EASTER_EGGS_OBJECTIVES_DANDELION_COLLECT, who);
            case "cyan_dye_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_EASTER_EGGS_OBJECTIVES_CYAN_DYE_CRAFT, who);
            case "golden_apple_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_EASTER_EGGS_OBJECTIVES_GOLDEN_APPLE_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SEASONAL_EASTER_EGGS_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_EASTER_EGGS_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_EASTER_EGGS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_EASTER_EGGS_DECLINE, who);
    }
}