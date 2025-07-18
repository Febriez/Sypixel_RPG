package com.febrie.rpg.quest.impl.clazz;

import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestNPC;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.economy.CurrencyType;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 전사 승급 퀘스트
 * 전사 클래스로 전직하기 위한 퀘스트
 *
 * @author Febrie
 */
public class WarriorAdvancementQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class WarriorAdvancementBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new WarriorAdvancementQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public WarriorAdvancementQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private WarriorAdvancementQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new WarriorAdvancementBuilder()
                .id(QuestID.CLASS_WARRIOR_ADVANCEMENT)
                .objectives(Arrays.asList(
                        new ReachLevelObjective("warrior_level", 30),
                        new InteractNPCObjective("warrior_master", QuestNPC.WARRIOR_MASTER),
                        new KillMobObjective("prove_combat", EntityType.IRON_GOLEM, 20),
                        new KillPlayerObjective("prove_pvp", 10),
                        new CollectItemObjective("warrior_emblem", Material.IRON_INGOT, 50),
                        new CraftItemObjective("forge_weapon", Material.DIAMOND_SWORD, 1),
                        new CraftItemObjective("forge_armor", Material.DIAMOND_CHESTPLATE, 1),
                        new SurviveObjective("endurance_test", 600), // 10 minutes
                        new KillMobObjective("final_trial", EntityType.RAVAGER, 5),
                        new DeliverItemObjective("return_emblem", "warrior_master", Material.DIAMOND_SWORD, 1)
                ))
                .reward(BasicReward.builder()
                        .addCurrency(CurrencyType.GOLD, 10000)
                        .addCurrency(CurrencyType.EXP, 2500)
                        .addExperience(3000)
                        .build())
                .sequential(true)
                .category(QuestCategory.ADVANCEMENT)
                .minLevel(30)
                .maxLevel(100);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "전사의 승급 시험" : "Warrior Advancement Trial";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "진정한 전사가 되기 위한 승급 시험입니다.",
                    "당신의 전투 기술과 용맹함을 증명하세요.",
                    "",
                    "전사의 특징:",
                    "• 강력한 근접 전투 능력",
                    "• 높은 체력과 방어력",
                    "• 다양한 무기 숙련도",
                    "• 전투 중 분노 게이지 활용",
                    "",
                    "요구사항:",
                    "• 레벨 30 이상",
                    "• 전투 실력 증명",
                    "• PvP 능력 검증"
            );
        } else {
            return Arrays.asList(
                    "The advancement trial to become a true warrior.",
                    "Prove your combat skills and bravery.",
                    "",
                    "Warrior Features:",
                    "• Powerful melee combat abilities",
                    "• High health and defense",
                    "• Diverse weapon proficiency",
                    "• Rage gauge utilization in combat",
                    "",
                    "Requirements:",
                    "• Level 30 or higher",
                    "• Prove combat skills",
                    "• Verify PvP abilities"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();
        
        return switch (id) {
            case "warrior_level" -> isKorean ? "전사 레벨 30 달성" : "Reach Warrior Level 30";
            case "warrior_master" -> isKorean ? "전사 대가와 대화" : "Talk to the Warrior Master";
            case "prove_combat" -> isKorean ? "전투 실력 증명: 철 골렘 20마리 처치" : "Prove Combat: Kill 20 Iron Golems";
            case "prove_pvp" -> isKorean ? "PvP 실력 증명: 플레이어 10명 처치" : "Prove PvP: Kill 10 Players";
            case "warrior_emblem" -> isKorean ? "전사의 증표 50개 수집" : "Collect 50 Warrior Emblems";
            case "forge_weapon" -> isKorean ? "전사의 검 단조" : "Forge a Warrior's Sword";
            case "forge_armor" -> isKorean ? "전사의 갑옷 단조" : "Forge a Warrior's Armor";
            case "endurance_test" -> isKorean ? "지구력 시험: 10분간 생존" : "Endurance Test: Survive 10 minutes";
            case "final_trial" -> isKorean ? "최종 시험: 파괴수 5마리 처치" : "Final Trial: Kill 5 Ravagers";
            case "return_emblem" -> isKorean ? "전사 대가에게 증표 전달" : "Return emblem to Warrior Master";
            default -> objective.getStatusInfo(null);
        };
    }
}