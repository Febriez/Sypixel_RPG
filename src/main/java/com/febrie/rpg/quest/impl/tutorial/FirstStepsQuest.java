package com.febrie.rpg.quest.impl.tutorial;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 첫 걸음 - 튜토리얼 퀘스트 1
 * 기본적인 이동과 상호작용을 배우는 퀘스트
 *
 * @author Febrie
 */
public class FirstStepsQuest extends Quest {

    /**
     * 기본 생성자
     */
    public FirstStepsQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.TUTORIAL_FIRST_STEPS)
                .objectives(Arrays.asList(
                        // 1. 허브 구역 방문 (WorldGuard 영역 이름: Hub)
                        new VisitLocationObjective("visit_hub", "Hub"),
                        // 2. 마을 상인 NPC 방문 (NPC Code 기반)
                        new InteractNPCObjective("visit_merchant", "village_merchant")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 100)
                        .addItem(new ItemStack(Material.WOODEN_SWORD))
                        .addItem(new ItemStack(Material.WOODEN_PICKAXE))
                        .addItem(new ItemStack(Material.WOODEN_AXE))
                        .addItem(new ItemStack(Material.BREAD, 10))
                        .addExperience(50)
                        .build())
                .sequential(true)  // 순차적으로 진행
                .category(QuestCategory.TUTORIAL)
                .minLevel(1);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.tutorial.first-steps.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        // description이 배열이므로 각 줄을 개별적으로 가져옴
        List<Component> description = new ArrayList<>();
        // JSON 배열의 각 요소를 순차적으로 가져옴
        for (int i = 0; i < 4; i++) { // description 배열이 4개 요소를 가짐
            Component line = Component.translatable("quest.tutorial.first-steps.description." + i);
            description.add(line);
        }
        return description;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "visit_hub" -> Component.translatable("quest.tutorial.first-steps.objectives.visit_hub");
            case "visit_merchant" -> Component.translatable("quest.tutorial.first-steps.objectives.visit_merchant");
            default -> Component.translatable("quest.tutorial.first-steps.objectives." + id);
        };
    }


    @Override
    public int getDialogCount() {
        return 3; // 3개의 대화 페이지
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        // 올바른 키 형식 사용: dialogs.0, dialogs.1, ...
        return switch (index) {
            case 0 -> Component.translatable("quest.tutorial.first-steps.dialogs.0");
            case 1 -> Component.translatable("quest.tutorial.first-steps.dialogs.1");
            case 2 -> Component.translatable("quest.tutorial.first-steps.dialogs.2");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.tutorial.first-steps.npc-name");
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.tutorial.first-steps.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.tutorial.first-steps.decline");
    }
}