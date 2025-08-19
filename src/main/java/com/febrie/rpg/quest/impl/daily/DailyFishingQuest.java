package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.FishingObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import java.util.List;
/**
 * 일일 낚시 - 일일 퀘스트
 * 매일 일정량의 물고기를 낚는 퀘스트
 *
 * @author Febrie
 */
public class DailyFishingQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class DailyFishingBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new DailyFishingQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public DailyFishingQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private DailyFishingQuest(@NotNull QuestBuilder builder) {
        super(builder);
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

        return new DailyFishingBuilder()
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
        return Component.translatable("quest.daily.fishing.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return List.of() /* TODO: Convert LangManager.getList("quest.daily.fishing.description") manually */;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String key = "quest.daily.fishing.objectives." + objective.getId();
        return Component.translatable(key);
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("daily_fishing_dialog");
        
        // 시작 대화
        dialog.addLine("quest.daily.fishing.npcs.fisherman", "quest.daily.fishing.dialogs.greeting");
        dialog.addLine("quest.daily.fishing.npcs.fisherman", "quest.daily.fishing.dialogs.daily_reward");
        dialog.addLine("quest.daily.fishing.npcs.fisherman", "quest.daily.fishing.dialogs.targets");
        dialog.addLine("quest.daily.fishing.npcs.fisherman", "quest.daily.fishing.dialogs.come_back");
        
        return dialog;
    }
}