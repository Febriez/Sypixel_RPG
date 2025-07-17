package com.febrie.rpg.quest.impl.tutorial;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.ExploreObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.quest.QuestCategory;
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
        return new FirstStepsBuilder()
                .id(QuestID.TUTORIAL_FIRST_STEPS)
                .objectives(Arrays.asList(
                        // 1. 허브 구역 방문 (WorldGuard 영역 이름: Hub)
                        new ExploreObjective("visit_hub", "Hub"),
                        // 2. 마을 상인 NPC 방문
                        new InteractNPCObjective("visit_merchant", 1) // Citizens NPC ID 1번 사용
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
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return """
                    서버에 오신 것을 환영합니다!
                    기본적인 이동과 상호작용을 배워봅시다.
                    """.lines().toList();
        } else {
            return """
                    Welcome to the server!
                    Let's learn basic movement and interaction.
                    """.lines().toList();
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "visit_hub" -> isKorean ? "허브 구역 방문" : "Visit the Hub area";
            case "visit_merchant" -> isKorean ? "마을 상인과 대화" : "Talk to village merchant";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("first_steps_dialog");

        dialog.addLine("튜토리얼 가이드",
                "안녕하세요! 새로운 모험가님! 이곳 Sypixel RPG 세계에 오신 것을 환영합니다.",
                "Hello, new adventurer! Welcome to the world of Sypixel RPG!");

        dialog.addLine("튜토리얼 가이드",
                "저는 여러분의 첫 걸음을 도와드릴 가이드입니다. 함께 이 세계의 기본을 배워보시죠!",
                "I'm your guide to help with your first steps. Let's learn the basics of this world together!");

        dialog.addLine("튜토리얼 가이드",
                "먼저 기본적인 이동과 상호작용 방법을 익혀보겠습니다. 준비되셨나요?",
                "First, let's learn basic movement and interaction. Are you ready?");

        return dialog;
    }

    @Override
    public String getDialog(int index) {
        String[] dialogs = {
            "안녕하세요! 새로운 모험가님! 이곳 Sypixel RPG 세계에 오신 것을 환영합니다.",
            "저는 여러분의 첫 걸음을 도와드릴 가이드입니다. 함께 이 세계의 기본을 배워보시죠!",
            "먼저 기본적인 이동과 상호작용 방법을 익혀보겠습니다. 준비되셨나요?"
        };
        
        if (index >= 0 && index < dialogs.length) {
            return dialogs[index];
        }
        return null;
    }

    @Override
    public int getDialogCount() {
        return 3;
    }

    @Override
    public @NotNull String getNPCName() {
        return "튜토리얼 가이드";
    }
    
    @Override
    public String getAcceptDialog() {
        return "좋습니다! 함께 모험을 시작해봅시다. 먼저 허브 구역으로 가서 저를 찾아주세요!";
    }
    
    @Override
    public String getDeclineDialog() {
        return "아직 준비가 안 되셨나요? 준비가 되시면 언제든 다시 찾아와주세요!";
    }
}