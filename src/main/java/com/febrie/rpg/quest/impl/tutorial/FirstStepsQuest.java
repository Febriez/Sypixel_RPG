package com.febrie.rpg.quest.impl.tutorial;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
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
    private static class FirstStepsBuilder extends Quest.Builder {
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
    private FirstStepsQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static Builder createBuilder() {
        // 스폰 지점
        Location spawnLocation = Bukkit.getWorlds().getFirst().getSpawnLocation();

        return new FirstStepsBuilder()
                .id(QuestID.TUTORIAL_FIRST_STEPS)
                .objectives(Arrays.asList(
                        // 1. 스폰 지점 방문
                        new VisitLocationObjective("visit_spawn", spawnLocation, 10.0, "스폰 지점"),
                        // 2. 마을 상인 NPC 방문
                        new InteractNPCObjective("visit_merchant", "마을 상인")
                ))
                .reward(BasicReward.builder()
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
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "첫 걸음" : "First Steps";
    }

    @Override
    public @NotNull List<String> getDescription(boolean isKorean) {
        if (isKorean) {
            return """
                    서버에 오신 것을 환영합니다!
                    기본적인 이동과 상호작용을 배워봅시다.
                    
                    목표:
                    • 스폰 지점 방문
                    • 마을 상인과 대화
                    
                    보상:
                    • 골드 100
                    • 기본 도구 세트
                    • 빵 10개
                    • 경험치 50
                    """.lines().toList();
        } else {
            return """
                    Welcome to the server!
                    Let's learn basic movement and interaction.
                    
                    Objectives:
                    • Visit spawn point
                    • Talk to village merchant
                    
                    Rewards:
                    • 100 Gold
                    • Basic tool set
                    • 10 Bread
                    • 50 Experience
                    """.lines().toList();
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "visit_spawn" -> isKorean ? "스폰 지점 방문" : "Visit spawn point";
            case "visit_merchant" -> isKorean ? "마을 상인과 대화" : "Talk to village merchant";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("first_steps_dialog");

        dialog.addLine("마을 상인",
                "오, 새로운 모험가시군요! 환영합니다!",
                "Oh, a new adventurer! Welcome!");

        dialog.addLine("마을 상인",
                "이곳은 모험을 시작하기에 완벽한 장소입니다.",
                "This is the perfect place to start your adventure.");

        dialog.addLine("마을 상인",
                "제가 기본적인 도구들을 드리겠습니다. 앞으로의 여정에 도움이 될 거예요!",
                "Let me give you some basic tools. They'll help you on your journey!");

        return dialog;
    }
}