package com.febrie.rpg.quest.impl.tutorial;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
     * 퀘스트 빌더
     */
    private static class FirstStepsBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new FirstStepsQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public FirstStepsQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private FirstStepsQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new FirstStepsBuilder()
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
        return List.of() /* TODO: Convert LangManager.getList("quest.tutorial.first-steps.description") manually */;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "visit_hub" -> Component.translatable("quest.tutorial.first-steps.objectives.visit_hub");
            case "visit_merchant" -> Component.translatable("quest.tutorial.first-steps.objectives.visit_merchant");
            default -> Component.text(objective.getStatusInfo(null));
        };
    }


    @Override
    public int getDialogCount() {
        return 3; // 3개의 대화 페이지
    }
    
    @Override
    public String getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.tutorial.first-steps.dialogs.line1").toString();
            case 1 -> Component.translatable("quest.tutorial.first-steps.dialogs.line2").toString();
            case 2 -> Component.translatable("quest.tutorial.first-steps.dialogs.line3").toString();
            default -> null;
        };
    }
    
    @Override
    public String getNPCName(@NotNull Player who) {
        return Component.translatable("quest.tutorial.first-steps.npc-name").toString();
    }

    @Override
    public String getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.tutorial.first-steps.dialogs.accept").toString();
    }
    
    @Override
    public String getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.tutorial.first-steps.dialogs.decline").toString();
    }
}