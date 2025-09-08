package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.FishingObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;

import com.febrie.rpg.util.LangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.ArrayList;

/**
 * 일일 낚시 - 일일 퀘스트
 * 매일 일정량의 물고기를 낚는 퀘스트
 *
 * @author Febrie
 */
public class DailyFishingQuest extends Quest {

    /**
     * 기본 생성자
     */
    public DailyFishingQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        List<QuestObjective> objectives = new ArrayList<>();
        
        // 낚시 목표
        objectives.add(new FishingObjective("catch_any_fish", FishingObjective.FishType.ANY, 10)); // 아무 물고기나 10마리
        objectives.add(new FishingObjective("catch_salmon", FishingObjective.FishType.SPECIFIC, 5, Material.SALMON)); // 연어 5마리
        objectives.add(new FishingObjective("catch_pufferfish", FishingObjective.FishType.SPECIFIC, 2, Material.PUFFERFISH)); // 복어 2마리

        return new QuestBuilder()
                .id(QuestID.DAILY_FISHING)
                .objectives(objectives)
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 200)
                        .addCurrency(CurrencyType.EMERALD, 5)
                        .addItem(new ItemStack(Material.FISHING_ROD)) // 낚싯대
                        .addItem(new ItemStack(Material.COOKED_SALMON, 16))
                        .addExperience(100)
                        .build())
                .sequential(false) // 순서 상관없이 진행 가능
                .category(QuestCategory.DAILY)
                .minLevel(3)
                .repeatable(true) // 24시간 쿨다운은 별도 관리
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_DAILY_FISHING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_DAILY_FISHING_INFO, who);
    }

        @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "catch_any_fish" -> LangManager.list(LangKey.QUEST_DAILY_FISHING_OBJECTIVES_CATCH_ANY_FISH, who);
            case "catch_salmon" -> LangManager.list(LangKey.QUEST_DAILY_FISHING_OBJECTIVES_CATCH_SALMON, who);
            case "catch_pufferfish" -> LangManager.list(LangKey.QUEST_DAILY_FISHING_OBJECTIVES_CATCH_PUFFERFISH, who);
            default -> new ArrayList<>();
        };
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_DAILY_FISHING_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_DAILY_FISHING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_DAILY_FISHING_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_DAILY_FISHING_DECLINE, who);
    }
}