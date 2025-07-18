package com.febrie.rpg.quest.impl.repeatable;

import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
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
 * 몬스터 토벌 임무 - 반복 가능한 퀘스트
 * 마을 주변의 몬스터들을 토벌하는 퀘스트
 *
 * @author Febrie
 */
public class MonsterExterminationQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class MonsterExterminationBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new MonsterExterminationQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public MonsterExterminationQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private MonsterExterminationQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new MonsterExterminationBuilder()
                .id(QuestID.REPEAT_MONSTER_EXTERMINATION)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("guard_captain", 4),
                        new KillMobObjective("kill_zombies", EntityType.ZOMBIE, 30),
                        new KillMobObjective("kill_skeletons", EntityType.SKELETON, 25),
                        new KillMobObjective("kill_spiders", EntityType.SPIDER, 20),
                        new KillMobObjective("kill_creepers", EntityType.CREEPER, 15),
                        new CollectItemObjective("collect_proof", Material.ROTTEN_FLESH, 20),
                        new DeliverItemObjective("deliver_proof", "guard_captain", Material.ROTTEN_FLESH, 20)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 1500)
                        .addCurrency(CurrencyType.EXP, 300)
                        .addExperience(200)
                        .build())
                .sequential(false)
                .repeatable(true)
                .category(QuestCategory.REPEATABLE)
                .minLevel(10)
                .maxLevel(100)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "몬스터 토벌 임무" : "Monster Extermination Mission";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "마을 주변의 몬스터들을 토벌하여",
                    "주민들의 안전을 지켜주세요.",
                    "",
                    "언제든지 다시 도전할 수 있습니다.",
                    "",
                    "목표:",
                    "• 좀비 30마리 처치",
                    "• 스켈레톤 25마리 처치",
                    "• 거미 20마리 처치",
                    "• 크리퍼 15마리 처치",
                    "• 토벌 증거 20개 수집 및 전달",
                    "",
                    "보상:",
                    "• 골드 1500",
                    "• EXP 300",
                    "• 경험치 200"
            );
        } else {
            return Arrays.asList(
                    "Exterminate monsters around the village",
                    "to keep the residents safe.",
                    "",
                    "You can challenge this quest again anytime.",
                    "",
                    "Objectives:",
                    "• Kill 30 Zombies",
                    "• Kill 25 Skeletons",
                    "• Kill 20 Spiders",
                    "• Kill 15 Creepers",
                    "• Collect and deliver 20 proof items",
                    "",
                    "Rewards:",
                    "• 1500 Gold",
                    "• 300 EXP",
                    "• 200 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();
        
        return switch (id) {
            case "guard_captain" -> isKorean ? "경비대장과 대화" : "Talk to the Guard Captain";
            case "kill_zombies" -> isKorean ? "좀비 30마리 처치" : "Kill 30 Zombies";
            case "kill_skeletons" -> isKorean ? "스켈레톤 25마리 처치" : "Kill 25 Skeletons";
            case "kill_spiders" -> isKorean ? "거미 20마리 처치" : "Kill 20 Spiders";
            case "kill_creepers" -> isKorean ? "크리퍼 15마리 처치" : "Kill 15 Creepers";
            case "collect_proof" -> isKorean ? "토벌 증거 20개 수집" : "Collect 20 Proof of Extermination";
            case "deliver_proof" -> isKorean ? "경비대장에게 증거 전달" : "Deliver proof to Guard Captain";
            default -> objective.getStatusInfo(null);
        };
    }
}