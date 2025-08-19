package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
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
 * 약초 수집 - 사이드 퀘스트
 * 연금술사를 위해 특정 약초들을 수집
 *
 * @author Febrie
 */
public class CollectHerbsQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class CollectHerbsBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new CollectHerbsQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public CollectHerbsQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private CollectHerbsQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        List<QuestObjective> objectives = new ArrayList<>();
        
        // 약초 수집 목표
        objectives.add(new CollectItemObjective("collect_dandelions", Material.DANDELION, 15));
        objectives.add(new CollectItemObjective("collect_poppies", Material.POPPY, 10));
        objectives.add(new CollectItemObjective("collect_azure_bluets", Material.AZURE_BLUET, 10));
        objectives.add(new CollectItemObjective("collect_spider_eyes", Material.SPIDER_EYE, 5));
        
        // 완료 후 연금술사에게 전달 (NPC 코드 사용)
        objectives.add(new InteractNPCObjective("deliver_to_alchemist", "alchemist_mina"));

        return new CollectHerbsBuilder()
                .id(QuestID.SIDE_COLLECT_HERBS)
                .objectives(objectives)
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 250)
                        .addCurrency(CurrencyType.EMERALD, 8)
                        .addItem(new ItemStack(Material.POTION, 5)) // 치유 물약
                        .addItem(new ItemStack(Material.GLISTERING_MELON_SLICE, 3))
                        .addExperience(150)
                        .build())
                .sequential(true) // 순차적으로 진행 (수집 후 전달)
                .category(QuestCategory.SIDE)
                .minLevel(5)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.side.collect_herbs.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return List.of() /* TODO: Convert LangManager.getList("quest.side.collect_herbs.description") manually */;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return Component.translatable("quest.side.collect_herbs.objectives.");
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("collect_herbs_dialog");

        dialog.addLine("quest.side.collect_herbs.dialog.alchemist",
                "quest.side.collect_herbs.dialog.alchemist.line1");

        dialog.addLine("quest.side.collect_herbs.dialog.alchemist",
                "quest.side.collect_herbs.dialog.alchemist.line2");

        dialog.addLine("quest.side.collect_herbs.dialog.alchemist",
                "quest.side.collect_herbs.dialog.alchemist.line3");

        return dialog;
    }
}