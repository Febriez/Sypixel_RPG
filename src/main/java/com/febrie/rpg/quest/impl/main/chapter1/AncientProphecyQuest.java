package com.febrie.rpg.quest.impl.main.chapter1;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 고대의 예언 - 메인 퀘스트 Chapter 1
 * 운명의 시작
 */
public class AncientProphecyQuest extends Quest {

    public AncientProphecyQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_ANCIENT_PROPHECY)
                .objectives(List.of(
                        new VisitLocationObjective("visit_elder", "ancient_temple"),
                        new InteractNPCObjective("talk_elder", "ancient_elder"), // 고대의 장로
                        new CollectItemObjective("paper_collect", Material.PAPER, 5),
                        new DeliverItemObjective("paper_deliver", Material.PAPER, 5, "고대의 장로")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 500)
                        .addCurrency(CurrencyType.DIAMOND, 10)
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK))
                        .addExperience(1000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .minLevel(10)
                .maxLevel(0)
                .addPrerequisite(QuestID.MAIN_HEROES_JOURNEY);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_PROPHECY_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_ANCIENT_PROPHECY_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "visit_elder" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_PROPHECY_OBJECTIVES_VISIT_ELDER, who);
            case "talk_elder" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_PROPHECY_OBJECTIVES_TALK_ELDER, who);
            case "paper_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_PROPHECY_OBJECTIVES_PAPER_COLLECT, who);
            case "paper_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_PROPHECY_OBJECTIVES_PAPER_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_ANCIENT_PROPHECY_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_PROPHECY_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_PROPHECY_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_ANCIENT_PROPHECY_DECLINE, who);
    }
}