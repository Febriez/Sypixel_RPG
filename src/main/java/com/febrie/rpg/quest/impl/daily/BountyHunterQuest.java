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

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.ArrayList;

/**
 * 일일 현상금 사냥꾼 - 일일 퀘스트
 * 매일 현상금이 걸린 몬스터들을 사냥하는 퀘스트
 *
 * @author Febrie
 */
public class BountyHunterQuest extends Quest {

    /**
     * 기본 생성자
     */
    public BountyHunterQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        List<QuestObjective> objectives = new ArrayList<>();
        
        // 현상금 사냥 목표
        objectives.add(new InteractNPCObjective("bounty_board", "bounty_officer"));
        objectives.add(new KillMobObjective("hunt_bandits", EntityType.PILLAGER, 6));
        objectives.add(new KillMobObjective("eliminate_threats", EntityType.RAVAGER, 3));
        objectives.add(new CollectItemObjective("emerald_collect", Material.EMERALD, 8));

        return new QuestBuilder()
                .id(QuestID.DAILY_BOUNTY_HUNTER)
                .objectives(objectives)
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 300)
                        .addCurrency(CurrencyType.EMERALD, 7)
                        .addItem(new ItemStack(Material.CROSSBOW))
                        .addItem(new ItemStack(Material.ARROW, 32))
                        .addExperience(140)
                        .build())
                .sequential(false)
                .category(QuestCategory.DAILY)
                .minLevel(10)
                .repeatable(true)
                .daily(true)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "bounty_board" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_BOUNTY_BOARD, who);
            case "hunt_bandits" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_HUNT_BANDITS, who);
            case "eliminate_threats" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_ELIMINATE_THREATS, who);
            case "emerald_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_EMERALD_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_DECLINE, who);
    }
}