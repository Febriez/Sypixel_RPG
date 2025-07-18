package com.febrie.rpg.quest.impl.main;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.CraftItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 영웅의 여정 - 메인 퀘스트 1
 * 본격적인 모험의 시작
 *
 * @author Febrie
 */
public class HeroesJourneyQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class HeroesJourneyBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new HeroesJourneyQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public HeroesJourneyQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private HeroesJourneyQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new HeroesJourneyBuilder()
                .id(QuestID.MAIN_HEROES_JOURNEY)
                .objectives(Arrays.asList(
                        // 1. 다양한 몬스터 처치
                        new KillMobObjective("kill_zombies", EntityType.ZOMBIE, 10),
                        new KillMobObjective("kill_skeletons", EntityType.SKELETON, 10),
                        new KillMobObjective("kill_spiders", EntityType.SPIDER, 5),

                        // 2. 자원 수집
                        new CollectItemObjective("collect_iron", Material.IRON_INGOT, 20),
                        new CollectItemObjective("collect_gold", Material.GOLD_INGOT, 10),

                        // 3. 장비 제작
                        new CraftItemObjective("craft_iron_sword", Material.IRON_SWORD, 1),
                        new CraftItemObjective("craft_iron_armor", Material.IRON_CHESTPLATE, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 500)
                        .addCurrency(CurrencyType.DIAMOND, 5)
                        .addItem(new ItemStack(Material.DIAMOND))
                        .addItem(new ItemStack(Material.ENCHANTING_TABLE))
                        .addExperience(500)
                        .build())
                .sequential(false)  // 자유롭게 진행 가능
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .minLevel(5)
                .maxLevel(0)  // 최대 레벨 제한 없음
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);  // 튜토리얼 전투 퀘스트 완료 필요
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "영웅의 여정" : "Hero's Journey";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "진정한 영웅이 되기 위한 첫 걸음입니다.",
                    "다양한 도전을 통해 실력을 증명하세요!",
                    "",
                    "이 퀘스트는 여러 목표를 동시에 진행할 수 있습니다.",
                    "각자의 속도로 완성해 나가세요.",
                    "",
                    "목표:",
                    "• 좀비 10마리 처치",
                    "• 스켈레톤 10마리 처치",
                    "• 거미 5마리 처치",
                    "• 철괴 20개 수집",
                    "• 금괴 10개 수집",
                    "• 철 검 1개 제작",
                    "• 철 흉갑 1개 제작",
                    "",
                    "보상:",
                    "• 골드 500",
                    "• 다이아몬드 5개",
                    "• 다이아몬드 1개",
                    "• 마법부여대",
                    "• 경험치 500"
            );
        } else {
            return Arrays.asList(
                    "The first step to becoming a true hero.",
                    "Prove your skills through various challenges!",
                    "",
                    "This quest allows multiple objectives to progress simultaneously.",
                    "Complete them at your own pace.",
                    "",
                    "Objectives:",
                    "• Kill 10 zombies",
                    "• Kill 10 skeletons",
                    "• Kill 5 spiders",
                    "• Collect 20 iron ingots",
                    "• Collect 10 gold ingots",
                    "• Craft 1 iron sword",
                    "• Craft 1 iron chestplate",
                    "",
                    "Rewards:",
                    "• 500 Gold",
                    "• 5 Diamonds",
                    "• 1 Diamond",
                    "• Enchanting Table",
                    "• 500 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "kill_zombies" -> isKorean ? "좀비 10마리 처치" : "Kill 10 zombies";
            case "kill_skeletons" -> isKorean ? "스켈레톤 10마리 처치" : "Kill 10 skeletons";
            case "kill_spiders" -> isKorean ? "거미 5마리 처치" : "Kill 5 spiders";
            case "collect_iron" -> isKorean ? "철괴 20개 수집" : "Collect 20 iron ingots";
            case "collect_gold" -> isKorean ? "금괴 10개 수집" : "Collect 10 gold ingots";
            case "craft_iron_sword" -> isKorean ? "철 검 1개 제작" : "Craft 1 iron sword";
            case "craft_iron_armor" -> isKorean ? "철 흉갑 1개 제작" : "Craft 1 iron chestplate";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("heroes_journey_dialog");

        dialog.addLine("모험가 길드장",
                "드디어 진정한 모험을 시작할 준비가 되셨군요!",
                "Finally, you're ready to begin your true adventure!");

        dialog.addLine("모험가 길드장",
                "영웅이 되는 길은 험난합니다. 하지만 당신이라면 할 수 있을 거예요.",
                "The path to becoming a hero is challenging. But I believe you can do it.");

        dialog.addLine("모험가 길드장",
                "다양한 몬스터를 처치하고, 자원을 모아 장비를 만드세요.",
                "Defeat various monsters, gather resources, and craft equipment.");

        dialog.addLine("모험가 길드장",
                "모든 목표를 달성하면 특별한 보상이 기다리고 있습니다. 행운을 빕니다!",
                "Special rewards await when you complete all objectives. Good luck!");

        return dialog;
    }
}