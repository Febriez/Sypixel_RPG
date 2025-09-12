package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import com.febrie.rpg.util.LangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.ArrayList;

/**
 * 일일 연금술 - 일일 퀘스트
 * 매일 연금술 재료를 수집하고 물약을 제조하는 퀘스트
 *
 * @author Febrie
 */
public class AlchemyQuest extends Quest {

    /**
     * 기본 생성자
     */
    public AlchemyQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        List<QuestObjective> objectives = new ArrayList<>();
        
        // 연금술 목표
        objectives.add(new InteractNPCObjective("alchemist", "grand_alchemist"));
        objectives.add(new CraftItemObjective("potion_craft", Material.POTION, 5));
        objectives.add(new CollectItemObjective("nether_wart_collect", Material.NETHER_WART, 10));
        objectives.add(new DeliverItemObjective("potion_deliver", Material.POTION, 3, "grand_alchemist"));

        return new QuestBuilder()
                .id(QuestID.DAILY_ALCHEMY)
                .objectives(objectives)
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 180)
                        .addCurrency(CurrencyType.EMERALD, 4)
                        .addItem(new ItemStack(Material.BREWING_STAND))
                        .addItem(new ItemStack(Material.BLAZE_POWDER, 8))
                        .addExperience(90)
                        .build())
                .sequential(false)
                .category(QuestCategory.DAILY)
                .minLevel(5)
                .repeatable(true)
                .daily(true)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_ALCHEMY_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_ALCHEMY_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "alchemist" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_ALCHEMY_OBJECTIVES_ALCHEMIST, who);
            case "potion_craft" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_ALCHEMY_OBJECTIVES_POTION_CRAFT, who);
            case "nether_wart_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_ALCHEMY_OBJECTIVES_NETHER_WART_COLLECT, who);
            case "potion_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_ALCHEMY_OBJECTIVES_POTION_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 9;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_ALCHEMY_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_ALCHEMY_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_ALCHEMY_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_ALCHEMY_DECLINE, who);
    }
}