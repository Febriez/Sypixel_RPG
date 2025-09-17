package com.febrie.rpg.quest.impl.main;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * 고대 예언 퀘스트
 * 고대의 예언을 밝혀내는 메인 스토리 퀘스트
 *
 * @author Febrie
 */
public class AncientProphecyQuest extends Quest {

    /**
     * 기본 생성자
     */
    public AncientProphecyQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_ANCIENT_PROPHECY)
                .objectives(List.of(
                        new VisitLocationObjective("visit_elder", "ancient_temple"),
                        new InteractNPCObjective("talk_elder", "elder_sage"),
                        new CollectItemObjective("paper_collect", Material.PAPER, 5),
                        new DeliverItemObjective("paper_deliver", Material.PAPER, 5, "elder_sage")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 500)
                        .addCurrency(CurrencyType.EMERALD, 10)
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK))
                        .addExperience(300)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(1)
                .maxLevel(0);
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