package com.febrie.rpg.quest.impl.tutorial;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 기초 전투 - 튜토리얼 퀘스트 2
 * 전투의 기본을 배우는 퀘스트
 *
 * @author Febrie
 */
public class BasicCombatQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class BasicCombatBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new BasicCombatQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public BasicCombatQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private BasicCombatQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new BasicCombatBuilder()
                .id(QuestID.TUTORIAL_BASIC_COMBAT)
                .objectives(Arrays.asList(
                        new KillMobObjective("kill_zombies", EntityType.ZOMBIE, 5),
                        new KillMobObjective("kill_skeletons", EntityType.SKELETON, 3)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 200)
                        .addItem(new ItemStack(Material.IRON_SWORD))
                        .addItem(new ItemStack(Material.IRON_CHESTPLATE))
                        .addItem(new ItemStack(Material.COOKED_BEEF, 20))
                        .addExperience(100)
                        .build())
                .sequential(false)  // 순서 상관없이 진행 가능
                .category(QuestCategory.TUTORIAL)
                .minLevel(1)
                .addPrerequisite(QuestID.TUTORIAL_FIRST_STEPS);  // 첫 걸음 퀘스트 완료 필요
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "기초 전투" : "Basic Combat";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "몬스터와 싸우는 방법을 배워봅시다.",
                    "조심하세요, 몬스터들은 위험합니다!",
                    "",
                    "목표:",
                    "• 좀비 5마리 처치",
                    "• 스켈레톤 3마리 처치",
                    "",
                    "보상:",
                    "• 골드 200",
                    "• 철 검",
                    "• 철 흉갑",
                    "• 익힌 소고기 20개",
                    "• 경험치 100"
            );
        } else {
            return Arrays.asList(
                    "Learn how to fight monsters.",
                    "Be careful, monsters are dangerous!",
                    "",
                    "Objectives:",
                    "• Kill 5 zombies",
                    "• Kill 3 skeletons",
                    "",
                    "Rewards:",
                    "• 200 Gold",
                    "• Iron Sword",
                    "• Iron Chestplate",
                    "• 20 Cooked Beef",
                    "• 100 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "kill_zombies" -> isKorean ? "좀비 5마리 처치" : "Kill 5 zombies";
            case "kill_skeletons" -> isKorean ? "스켈레톤 3마리 처치" : "Kill 3 skeletons";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("basic_combat_dialog");

        dialog.addLine("전투 교관",
                "모험가가 되려면 전투 기술은 필수입니다!",
                "Combat skills are essential to become an adventurer!");

        dialog.addLine("전투 교관",
                "밤이 되면 몬스터들이 나타납니다. 준비하세요!",
                "Monsters appear at night. Be prepared!");

        dialog.addLine("전투 교관",
                "좀비와 스켈레톤을 처치하고 돌아오면, 더 나은 장비를 드리겠습니다.",
                "Defeat zombies and skeletons, then return for better equipment.");

        return dialog;
    }
}