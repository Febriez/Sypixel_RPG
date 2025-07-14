package com.febrie.rpg.quest.example;

import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * 모든 목표 타입을 포함하는 마스터 퀘스트 예시
 * 15개의 다양한 목표를 순차적으로 완료해야 하는 종합 퀘스트
 *
 * @author Febrie
 */
public class ExampleMasterQuest extends Quest {

    private static final String QUEST_ID = "master_quest_example";
    private static final String QUEST_NAME_KEY = "quest.example.master.name";
    private static final String QUEST_DESCRIPTION_KEY = "quest.example.master.description";

    private final List<QuestObjective> objectives;

    public ExampleMasterQuest() {
        super(QUEST_ID, QUEST_NAME_KEY, QUEST_DESCRIPTION_KEY);
        this.objectives = createAllObjectives();
    }

    /**
     * 모든 타입의 목표 생성
     */
    private List<QuestObjective> createAllObjectives() {
        List<QuestObjective> list = new ArrayList<>();

        // 1. 몹 처치 - 좀비 10마리
        list.add(new KillMobObjective(
                "obj_kill_zombie",
                EntityType.ZOMBIE,
                10
        ));

        // 2. 아이템 수집 - 다이아몬드 5개
        list.add(new CollectItemObjective(
                "obj_collect_diamond",
                Material.DIAMOND,
                5,
                false
        ));

        // 3. 아이템 제작 - 다이아몬드 검 1개
        list.add(new CraftItemObjective(
                "obj_craft_sword",
                Material.DIAMOND_SWORD,
                1
        ));

        // 4. NPC 방문 - 대장장이 방문
        list.add(new InteractNPCObjective(
                "obj_visit_blacksmith",
                "마을 대장장이",
                Villager.Profession.WEAPONSMITH
        ));

        // 5. 지역 방문 - 스폰 지점 방문
        Location spawnLocation = Bukkit.getWorlds().get(0).getSpawnLocation();
        list.add(new VisitLocationObjective(
                "obj_visit_spawn",
                spawnLocation,
                50.0,
                "스폰 지역"
        ));

        // 6. 블럭 파괴 - 돌 50개
        list.add(new BreakBlockObjective(
                "obj_break_stone",
                Material.STONE,
                50
        ));

        // 7. 블럭 설치 - 조약돌 30개
        list.add(new PlaceBlockObjective(
                "obj_place_cobble",
                Material.COBBLESTONE,
                30
        ));

        // 8. 아이템 전달 - NPC에게 빵 10개 전달
        list.add(new DeliverItemObjective(
                "obj_deliver_bread",
                "배고픈 주민",
                Material.BREAD,
                10
        ));

        // 9. 재화 지불 - 100골드 사용
        list.add(new PayCurrencyObjective(
                "obj_pay_gold",
                PayCurrencyObjective.CurrencyType.GOLD,
                100
        ));

        // 10. 플레이어 처치 - PvP 3킬
        list.add(new KillPlayerObjective(
                "obj_kill_players",
                3
        ));

        // 11. 낚시 - 물고기 5마리
        list.add(new FishingObjective(
                "obj_fishing",
                FishingObjective.FishType.FISH,
                5
        ));

        // 12. 농작물 수확 - 밀 20개
        list.add(new HarvestObjective(
                "obj_harvest_wheat",
                Material.WHEAT,
                20
        ));

        // 13. 탐험 - 3개 지역 탐험
        List<ExploreObjective.ExploreLocation> exploreLocations = Arrays.asList(
                new ExploreObjective.ExploreLocation("숲", new Location(Bukkit.getWorlds().get(0), 100, 70, 100), 30),
                new ExploreObjective.ExploreLocation("사막", new Location(Bukkit.getWorlds().get(0), -200, 65, 50), 30),
                new ExploreObjective.ExploreLocation("산", new Location(Bukkit.getWorlds().get(0), 0, 100, -150), 30)
        );
        list.add(new ExploreObjective(
                "obj_explore",
                exploreLocations
        ));

        // 14. 생존 - 300초(5분) 생존
        list.add(new SurviveObjective(
                "obj_survive",
                300
        ));

        // 15. 레벨 달성 - RPG 레벨 10 달성
        list.add(new ReachLevelObjective(
                "obj_reach_level",
                ReachLevelObjective.LevelType.RPG,
                10
        ));

        return list;
    }

    @Override
    public @NotNull List<QuestObjective> getObjectives() {
        return new ArrayList<>(objectives);
    }

    @Override
    public boolean isSequential() {
        // 순차적으로 진행해야 함
        return true;
    }

    @Override
    public @NotNull QuestReward getReward() {
        // 예시 보상 - 실제 구현은 보상 시스템에 따라 달라짐
        return new ExampleQuestReward();
    }

    @Override
    public boolean canStart(@NotNull UUID playerId) {
        // 시작 조건 - 기본적으로 모두 가능
        return true;
    }

    @Override
    public int getMinLevel() {
        return 1; // 최소 레벨 1
    }

    @Override
    public int getMaxLevel() {
        return 100; // 최대 레벨 100
    }

    /**
     * 예시 보상 클래스
     */
    private static class ExampleQuestReward implements QuestReward {
        private static final String DESCRIPTION_KEY = "quest.example.master.reward.description";

        @Override
        public void grant(@NotNull Player player) {
            // 보상 지급 로직
            // - 경험치 10000
            // - 골드 1000
            // - 다이아몬드 10개
            // - 특별 칭호

            // RPGPlayer 가져오기
            // RPGPlayer rpgPlayer = RPGMain.getPlugin().getRPGPlayerManager().getOrCreatePlayer(player);
            // rpgPlayer.addExperience(10000);
            // rpgPlayer.addCurrency("gold", 1000);

            // 아이템 지급
            // player.getInventory().addItem(new ItemStack(Material.DIAMOND, 10));
        }

        @Override
        public @NotNull String getDescriptionKey() {
            return DESCRIPTION_KEY;
        }

        @Override
        public @NotNull String[] getPreviewKeys() {
            return new String[]{
                    "quest.example.master.reward.description"
            };
        }

        @Override
        public @NotNull RewardType getType() {
            return RewardType.MIXED;
        }
    }
}