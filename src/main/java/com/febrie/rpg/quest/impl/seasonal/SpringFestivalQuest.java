package com.febrie.rpg.quest.impl.seasonal;

import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.QuestCategory;
import org.jetbrains.annotations.NotNull;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.economy.CurrencyType;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.List;

public class SpringFestivalQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class SpringFestivalBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new SpringFestivalQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public SpringFestivalQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private SpringFestivalQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 빌더 생성 메서드
     */
    private static Builder createBuilder() {
        return new SpringFestivalBuilder()
                .id(QuestID.SEASON_SPRING_FESTIVAL)
                .category(QuestCategory.EVENT)
                .sequential(false)
                .repeatable(true)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("festival_host", 100),
                        new CollectItemObjective("collect_flowers", Material.DANDELION, 20),
                        new CollectItemObjective("collect_tulips", Material.RED_TULIP, 15),
                        new CollectItemObjective("collect_seeds", Material.WHEAT_SEEDS, 30),
                        new PlaceBlockObjective("plant_flowers", Material.DANDELION, 10),
                        new PlaceBlockObjective("plant_tulips", Material.RED_TULIP, 10),
                        new HarvestObjective("harvest_crops", Material.WHEAT, 20),
                        new CraftItemObjective("make_dyes", Material.YELLOW_DYE, 10),
                        new KillMobObjective("protect_festival", EntityType.ZOMBIE, 15),
                        new DeliverItemObjective("deliver_decorations", "축제_진행자", Material.YELLOW_DYE, 10)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 3000)
                        .addExperience(1000)
                        .build())
                .minLevel(10)
                .maxLevel(100)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public String getDisplayName(boolean isKorean) {
        return isKorean ? "봄 축제의 기쁨" : "Spring Festival Joy";
    }

    @Override
    public List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                "봄 축제를 준비하는데 도움을 주세요!",
                "꽃을 수집하고 축제장을 꾸며",
                "모두가 즐거운 시간을 보낼 수 있도록 해주세요."
            );
        } else {
            return Arrays.asList(
                "Help prepare for the Spring Festival!",
                "Collect flowers and decorate the festival grounds",
                "so everyone can have a good time."
            );
        }
    }

    @Override
    public String getObjectiveDescription(QuestObjective objective, boolean isKorean) {
        String objId = objective.getId();
        
        if ("festival_host".equals(objId)) {
            return isKorean ? "축제 진행자와 대화" : "Talk to the Festival Host";
        } else if ("collect_flowers".equals(objId)) {
            return isKorean ? "민들레 20개 수집" : "Collect 20 Dandelions";
        } else if ("collect_tulips".equals(objId)) {
            return isKorean ? "튤립 15개 수집" : "Collect 15 Tulips";
        } else if ("collect_seeds".equals(objId)) {
            return isKorean ? "씨앗 30개 수집" : "Collect 30 Seeds";
        } else if ("plant_flowers".equals(objId)) {
            return isKorean ? "민들레 10개 심기" : "Plant 10 Dandelions";
        } else if ("plant_tulips".equals(objId)) {
            return isKorean ? "튤립 10개 심기" : "Plant 10 Tulips";
        } else if ("harvest_crops".equals(objId)) {
            return isKorean ? "작물 20개 수확" : "Harvest 20 Crops";
        } else if ("make_dyes".equals(objId)) {
            return isKorean ? "노란색 염료 10개 제작" : "Craft 10 Yellow Dyes";
        } else if ("protect_festival".equals(objId)) {
            return isKorean ? "축제장을 위협하는 좀비 15마리 처치" : "Kill 15 Zombies threatening the festival";
        } else if ("deliver_decorations".equals(objId)) {
            return isKorean ? "축제 진행자에게 장식품 전달" : "Deliver decorations to the Festival Host";
        }
        
        return isKorean ? "알 수 없는 목표" : "Unknown objective";
    }
}