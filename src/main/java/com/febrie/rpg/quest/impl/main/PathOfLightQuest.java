package com.febrie.rpg.quest.impl.main;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CraftItemObjective;
import com.febrie.rpg.quest.objective.impl.DeliverItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 빛의 길 - 선택 퀘스트 (선)
 * 어둠의 길과 양자택일
 *
 * @author Febrie
 */
public class PathOfLightQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class PathOfLightBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new PathOfLightQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public PathOfLightQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private PathOfLightQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new PathOfLightBuilder()
                .id(QuestID.MAIN_PATH_OF_LIGHT)
                .objectives(Arrays.asList(
                        // 1. 언데드 몬스터 정화
                        new KillMobObjective("purify_undead_zombie", EntityType.ZOMBIE, 50),
                        new KillMobObjective("purify_undead_skeleton", EntityType.SKELETON, 30),
                        new KillMobObjective("purify_undead_phantom", EntityType.PHANTOM, 10),

                        // 2. 성스러운 아이템 제작
                        new CraftItemObjective("craft_golden_apple", Material.GOLDEN_APPLE, 5),

                        // 3. 마을 사람들 도와주기 (빵 전달)
                        new DeliverItemObjective("help_villagers", "굶주린 주민", Material.BREAD, 30)
                ))
                .reward(BasicReward.builder()
                        .addCurrency(CurrencyType.GOLD, 1000)
                        .addCurrency(CurrencyType.DIAMOND, 20)
                        .addItem(new ItemStack(Material.ELYTRA))  // 엘리트라 (천사의 날개)
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING))  // 불사의 토템
                        .addItem(new ItemStack(Material.BEACON))  // 신호기
                        .addExperience(2000)
                        .setDescriptionKey("quest.main.path_of_light.reward.description")
                        .build())
                .sequential(false)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .minLevel(20)
                .maxLevel(0)
                .addPrerequisite(QuestID.MAIN_HEROES_JOURNEY)  // 영웅의 여정 완료 필요
                .addExclusive(QuestID.MAIN_PATH_OF_DARKNESS);  // 어둠의 길과 양자택일
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "✦ 빛의 길" : "✦ Path of Light";
    }

    @Override
    public @NotNull List<String> getDescription(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "정의와 선의 길을 걸어갑니다.",
                    "어둠을 정화하고 사람들을 도우세요.",
                    "",
                    "⚠ 이 퀘스트를 선택하면",
                    "어둠의 길은 선택할 수 없습니다!",
                    "",
                    "목표:",
                    "• 좀비 50마리 정화",
                    "• 스켈레톤 30마리 정화",
                    "• 팬텀 10마리 정화",
                    "• 황금 사과 5개 제작",
                    "• 굶주린 주민에게 빵 30개 전달",
                    "",
                    "보상:",
                    "• 골드 1,000",
                    "• 다이아몬드 20개",
                    "• 엘리트라 (천사의 날개)",
                    "• 불사의 토템",
                    "• 신호기",
                    "• 경험치 2,000"
            );
        } else {
            return Arrays.asList(
                    "Walk the path of justice and goodness.",
                    "Purify darkness and help people.",
                    "",
                    "⚠ If you choose this quest,",
                    "Path of Darkness cannot be selected!",
                    "",
                    "Objectives:",
                    "• Purify 50 zombies",
                    "• Purify 30 skeletons",
                    "• Purify 10 phantoms",
                    "• Craft 5 golden apples",
                    "• Deliver 30 bread to hungry villagers",
                    "",
                    "Rewards:",
                    "• 1,000 Gold",
                    "• 20 Diamonds",
                    "• Elytra (Angel Wings)",
                    "• Totem of Undying",
                    "• Beacon",
                    "• 2,000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "purify_undead_zombie" -> isKorean ? "좀비 50마리 정화" : "Purify 50 zombies";
            case "purify_undead_skeleton" -> isKorean ? "스켈레톤 30마리 정화" : "Purify 30 skeletons";
            case "purify_undead_phantom" -> isKorean ? "팬텀 10마리 정화" : "Purify 10 phantoms";
            case "craft_golden_apple" -> isKorean ? "황금 사과 5개 제작" : "Craft 5 golden apples";
            case "help_villagers" -> isKorean ? "굶주린 주민에게 빵 30개 전달" : "Deliver 30 bread to hungry villagers";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("path_of_light_dialog");

        dialog.addLine("빛의 수호자",
                "당신의 마음속에 빛이 보입니다.",
                "I see the light within your heart.");

        dialog.addLine("빛의 수호자",
                "이 세상을 어둠으로부터 구하고 싶다면, 빛의 길을 선택하세요.",
                "If you wish to save this world from darkness, choose the path of light.");

        dialog.addLine("빛의 수호자",
                "하지만 기억하세요. 한 번 선택하면 돌이킬 수 없습니다.",
                "But remember, once chosen, there is no turning back.");

        return dialog;
    }
}