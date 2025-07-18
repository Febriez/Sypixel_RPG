package com.febrie.rpg.quest.impl.branch;

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
 * 빛의 성기사 전직 퀘스트
 * 빛의 길을 선택한 플레이어가 성기사가 되는 퀘스트
 *
 * @author Febrie
 */
public class LightPaladinQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class LightPaladinBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new LightPaladinQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public LightPaladinQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private LightPaladinQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new LightPaladinBuilder()
                .id(QuestID.BRANCH_LIGHT_PALADIN)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("paladin_master", "light_paladin_master"),
                        new CollectItemObjective("holy_water", Material.POTION, 10),
                        new KillMobObjective("purge_undead", EntityType.ZOMBIE, 50),
                        new KillMobObjective("purge_skeletons", EntityType.SKELETON, 50),
                        new VisitLocationObjective("holy_shrine", "light_shrine"),
                        new SurviveObjective("meditation", 600), // 10 minutes
                        new CraftItemObjective("holy_sword", Material.GOLDEN_SWORD, 1),
                        new PlaceBlockObjective("build_altar", Material.GLOWSTONE, 9),
                        new KillMobObjective("defeat_darkness", EntityType.WITHER_SKELETON, 20),
                        new CollectItemObjective("light_essence", Material.GLOWSTONE_DUST, 30),
                        new DeliverItemObjective("oath_completion", "paladin_master", Material.GOLDEN_SWORD, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 12000)
                        .addExperience(2500)
                        .build())
                .sequential(true)
                .category(QuestCategory.BRANCH)
                .minLevel(40)
                .maxLevel(100)
                .addPrerequisite(QuestID.MAIN_PATH_OF_LIGHT)
                .addExclusive(QuestID.BRANCH_DARK_KNIGHT)
                .addExclusive(QuestID.BRANCH_NEUTRAL_GUARDIAN);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "빛의 성기사" : "Paladin of Light";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "빛의 길을 선택한 당신은 성기사가 되어",
                    "어둠과 맞서 싸울 운명입니다.",
                    "",
                    "⚠️ 주의: 이 선택은 되돌릴 수 없습니다!",
                    "",
                    "성기사의 특징:",
                    "• 강력한 치유 능력",
                    "• 언데드에 대한 특별한 공격력",
                    "• 빛의 가호를 받는 방어 기술",
                    "• 동료를 보호하는 수호 능력",
                    "",
                    "요구사항:",
                    "• 레벨 40 이상",
                    "• 빛의 길 메인 퀘스트 완료",
                    "• 어둠의 기사나 중립 수호자를 선택하지 않음"
            );
        } else {
            return Arrays.asList(
                    "Having chosen the path of light,",
                    "you are destined to become a paladin",
                    "and fight against darkness.",
                    "",
                    "⚠️ Warning: This choice cannot be undone!",
                    "",
                    "Paladin Features:",
                    "• Powerful healing abilities",
                    "• Special damage against undead",
                    "• Light-blessed defensive skills",
                    "• Ally protection abilities",
                    "",
                    "Requirements:",
                    "• Level 40 or higher",
                    "• Completed Path of Light main quest",
                    "• Not chosen Dark Knight or Neutral Guardian"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();
        
        return switch (id) {
            case "paladin_master" -> isKorean ? "성기사단장과 대화" : "Talk to the Paladin Master";
            case "holy_water" -> isKorean ? "성수 10개 수집" : "Collect 10 Holy Water";
            case "purge_undead" -> isKorean ? "언데드 좀비 50마리 정화" : "Purge 50 Undead Zombies";
            case "purge_skeletons" -> isKorean ? "언데드 스켈레톤 50마리 정화" : "Purge 50 Undead Skeletons";
            case "holy_shrine" -> isKorean ? "빛의 성소 방문" : "Visit the Holy Shrine";
            case "meditation" -> isKorean ? "10분간 명상" : "Meditate for 10 minutes";
            case "holy_sword" -> isKorean ? "성검 제작" : "Craft a Holy Sword";
            case "build_altar" -> isKorean ? "빛의 제단 건설 (발광석 9개)" : "Build Light Altar (9 Glowstone)";
            case "defeat_darkness" -> isKorean ? "어둠의 전사 20마리 처치" : "Defeat 20 Dark Warriors";
            case "light_essence" -> isKorean ? "빛의 정수 30개 수집" : "Collect 30 Light Essence";
            case "oath_completion" -> isKorean ? "성기사 서약 완료" : "Complete the Paladin Oath";
            default -> objective.getStatusInfo(null);
        };
    }
}