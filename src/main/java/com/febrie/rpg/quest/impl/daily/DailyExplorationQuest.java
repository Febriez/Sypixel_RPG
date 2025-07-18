package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 일일 탐험 - 일일 퀘스트
 * 매일 새로운 지역을 탐험하고 발견하는 모험 퀘스트
 *
 * @author Febrie
 */
public class DailyExplorationQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class DailyExplorationBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new DailyExplorationQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public DailyExplorationQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private DailyExplorationQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new DailyExplorationBuilder()
                .id(QuestID.DAILY_EXPLORATION)
                .objectives(Arrays.asList(
                        // 탐험 시작
                        new InteractNPCObjective("explorer_guild", "explorer_guild_master"), // 탐험가 길드장
                        new CollectItemObjective("prepare_supplies", Material.BREAD, 10),
                        new CollectItemObjective("prepare_torches", Material.TORCH, 32),
                        new CollectItemObjective("prepare_tools", Material.IRON_PICKAXE, 1),
                        
                        // 첫 번째 지역 - 버려진 광산
                        new VisitLocationObjective("abandoned_mine", "old_mineshaft_entrance"),
                        new PlaceBlockObjective("light_mine", Material.TORCH, 10),
                        new BreakBlockObjective("mine_ores", Material.IRON_ORE, 20),
                        new CollectItemObjective("find_relics", Material.GOLD_NUGGET, 15),
                        new KillMobObjective("mine_creatures", EntityType.CAVE_SPIDER, 20),
                        
                        // 두 번째 지역 - 숨겨진 폭포
                        new VisitLocationObjective("hidden_waterfall", "secret_waterfall"),
                        new CollectItemObjective("waterfall_treasure", Material.PRISMARINE_SHARD, 10),
                        new FishingObjective("waterfall_fishing", 10),
                        new CollectItemObjective("rare_fish", Material.TROPICAL_FISH, 5),
                        new KillMobObjective("water_guardians", EntityType.DROWNED, 15),
                        
                        // 세 번째 지역 - 고대 유적
                        new VisitLocationObjective("ancient_ruins", "forgotten_temple"),
                        new BreakBlockObjective("clear_rubble", Material.COBBLESTONE, 30),
                        new CollectItemObjective("ancient_pottery", Material.FLOWER_POT, 5),
                        new CollectItemObjective("temple_treasure", Material.EMERALD, 10),
                        new KillMobObjective("ruin_guardians", EntityType.SKELETON, 25),
                        
                        // 네 번째 지역 - 신비한 숲
                        new VisitLocationObjective("mystic_forest", "enchanted_grove"),
                        new HarvestObjective("gather_herbs", Material.WHEAT, 20),
                        new CollectItemObjective("mystic_flowers", Material.AZURE_BLUET, 10),
                        new CollectItemObjective("magic_mushrooms", Material.RED_MUSHROOM, 15),
                        new KillMobObjective("forest_spirits", EntityType.ZOMBIE, 20),
                        
                        // 다섯 번째 지역 - 용암 동굴
                        new VisitLocationObjective("lava_cavern", "volcanic_cave"),
                        new PlaceBlockObjective("build_bridge", Material.COBBLESTONE, 20),
                        new CollectItemObjective("obsidian_shards", Material.OBSIDIAN, 10),
                        new CollectItemObjective("magma_cream", Material.MAGMA_CREAM, 5),
                        new KillMobObjective("lava_creatures", EntityType.MAGMA_CUBE, 15),
                        new SurviveObjective("heat_survival", 300), // 5분간 열기 견디기
                        
                        // 지도 작성
                        new CraftItemObjective("create_maps", Material.MAP, 5),
                        new CollectItemObjective("mark_locations", Material.FILLED_MAP, 5),
                        
                        // 보고서 작성
                        new CollectItemObjective("write_report", Material.WRITTEN_BOOK, 1),
                        new DeliverItemObjective("deliver_relics", "explorer_guild", Material.GOLD_NUGGET, 15),
                        new DeliverItemObjective("deliver_maps", "explorer_guild", Material.FILLED_MAP, 5),
                        new DeliverItemObjective("deliver_report", "explorer_guild", Material.WRITTEN_BOOK, 1),
                        new InteractNPCObjective("exploration_complete", "explorer_guild_master")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 3000)
                        .addCurrency(CurrencyType.DIAMOND, 20)
                        .addItem(new ItemStack(Material.COMPASS))
                        .addItem(new ItemStack(Material.MAP, 5))
                        .addItem(new ItemStack(Material.SPYGLASS))
                        .addItem(new ItemStack(Material.LEATHER_BOOTS)) // 탐험가 부츠
                        .addItem(new ItemStack(Material.ENDER_PEARL, 3))
                        .addExperience(2000)
                        .build())
                .sequential(false)  // 자유로운 탐험
                .repeatable(true)
                .daily(true)       // 일일 퀘스트
                .category(QuestCategory.DAILY)
                .minLevel(15)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "일일 미지의 땅 탐험" : "Daily Unknown Lands Exploration";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "탐험가 길드의 의뢰를 받아 미지의 땅을 탐험하세요!",
                    "5개의 서로 다른 지역을 탐험하고 보고서를 작성하세요.",
                    "",
                    "🗺️ 탐험 지역:",
                    "• 버려진 광산 - 오래된 광물과 유물",
                    "• 숨겨진 폭포 - 희귀한 물고기와 보물",
                    "• 고대 유적 - 잊혀진 문명의 흔적",
                    "• 신비한 숲 - 마법 식물과 약초",
                    "• 용암 동굴 - 화산 광물과 위험",
                    "",
                    "탐험 팁:",
                    "• 충분한 보급품 준비 필수",
                    "• 각 지역의 특징을 파악하세요",
                    "• 발견한 것들을 기록하세요",
                    "• 위험 지역에서는 조심하세요",
                    "",
                    "목표:",
                    "• 탐험가 길드장과 대화",
                    "• 탐험 보급품 준비",
                    "• 5개 지역 모두 탐험",
                    "• 각 지역에서 특산품 수집",
                    "• 지역 몬스터 처치",
                    "• 탐험 지도 5장 작성",
                    "• 탐험 보고서 작성",
                    "• 수집품과 보고서 제출",
                    "",
                    "특별 도전:",
                    "• 용암 동굴에서 5분간 생존",
                    "• 숨겨진 폭포에서 희귀 물고기 낚기",
                    "• 고대 유적의 보물 찾기",
                    "",
                    "보상:",
                    "• 골드 3,000",
                    "• 다이아몬드 20개",
                    "• 나침반",
                    "• 지도 5장",
                    "• 망원경",
                    "• 탐험가 부츠",
                    "• 엔더 진주 3개",
                    "• 경험치 2,000"
            );
        } else {
            return Arrays.asList(
                    "Accept the Explorer's Guild request and explore unknown lands!",
                    "Explore 5 different regions and write a report.",
                    "",
                    "🗺️ Exploration Areas:",
                    "• Abandoned Mine - Old minerals and relics",
                    "• Hidden Waterfall - Rare fish and treasures",
                    "• Ancient Ruins - Traces of forgotten civilization",
                    "• Mystic Forest - Magic plants and herbs",
                    "• Lava Cavern - Volcanic minerals and danger",
                    "",
                    "Exploration Tips:",
                    "• Prepare sufficient supplies",
                    "• Understand each region's features",
                    "• Record your discoveries",
                    "• Be careful in dangerous areas",
                    "",
                    "Objectives:",
                    "• Talk to Explorer Guild Master",
                    "• Prepare exploration supplies",
                    "• Explore all 5 regions",
                    "• Collect specialties from each region",
                    "• Defeat regional monsters",
                    "• Create 5 exploration maps",
                    "• Write exploration report",
                    "• Submit collectibles and report",
                    "",
                    "Special Challenges:",
                    "• Survive 5 minutes in lava cavern",
                    "• Catch rare fish at hidden waterfall",
                    "• Find treasures in ancient ruins",
                    "",
                    "Rewards:",
                    "• 3,000 Gold",
                    "• 20 Diamonds",
                    "• Compass",
                    "• 5 Maps",
                    "• Spyglass",
                    "• Explorer Boots",
                    "• 3 Ender Pearls",
                    "• 2,000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "explorer_guild" -> isKorean ? "탐험가 길드장과 대화" : "Talk to Explorer Guild Master";
            case "prepare_supplies" -> isKorean ? "빵 10개 준비" : "Prepare 10 bread";
            case "prepare_torches" -> isKorean ? "횃불 32개 준비" : "Prepare 32 torches";
            case "prepare_tools" -> isKorean ? "철 곡괭이 준비" : "Prepare iron pickaxe";
            case "abandoned_mine" -> isKorean ? "버려진 광산 도착" : "Arrive at abandoned mine";
            case "light_mine" -> isKorean ? "광산에 횃불 10개 설치" : "Place 10 torches in mine";
            case "mine_ores" -> isKorean ? "철 광석 20개 채굴" : "Mine 20 iron ore";
            case "find_relics" -> isKorean ? "유물 조각 15개 발견" : "Find 15 relic fragments";
            case "mine_creatures" -> isKorean ? "광산 생물 20마리 처치" : "Kill 20 mine creatures";
            case "hidden_waterfall" -> isKorean ? "숨겨진 폭포 발견" : "Discover hidden waterfall";
            case "waterfall_treasure" -> isKorean ? "폭포 보물 10개 수집" : "Collect 10 waterfall treasures";
            case "waterfall_fishing" -> isKorean ? "폭포에서 10마리 낚시" : "Fish 10 times at waterfall";
            case "rare_fish" -> isKorean ? "희귀 물고기 5마리 수집" : "Collect 5 rare fish";
            case "water_guardians" -> isKorean ? "물의 수호자 15마리 처치" : "Kill 15 water guardians";
            case "ancient_ruins" -> isKorean ? "고대 유적 도달" : "Reach ancient ruins";
            case "clear_rubble" -> isKorean ? "잔해 30개 제거" : "Clear 30 rubble";
            case "ancient_pottery" -> isKorean ? "고대 도자기 5개 수집" : "Collect 5 ancient pottery";
            case "temple_treasure" -> isKorean ? "신전 보물 10개 획득" : "Obtain 10 temple treasures";
            case "ruin_guardians" -> isKorean ? "유적 수호자 25마리 처치" : "Kill 25 ruin guardians";
            case "mystic_forest" -> isKorean ? "신비한 숲 진입" : "Enter mystic forest";
            case "gather_herbs" -> isKorean ? "약초 20개 채집" : "Gather 20 herbs";
            case "mystic_flowers" -> isKorean ? "신비한 꽃 10개 수집" : "Collect 10 mystic flowers";
            case "magic_mushrooms" -> isKorean ? "마법 버섯 15개 수집" : "Collect 15 magic mushrooms";
            case "forest_spirits" -> isKorean ? "숲의 정령 20마리 처치" : "Kill 20 forest spirits";
            case "lava_cavern" -> isKorean ? "용암 동굴 진입" : "Enter lava cavern";
            case "build_bridge" -> isKorean ? "용암 위 다리 건설 (20블록)" : "Build bridge over lava (20 blocks)";
            case "obsidian_shards" -> isKorean ? "흑요석 조각 10개 수집" : "Collect 10 obsidian shards";
            case "magma_cream" -> isKorean ? "마그마 크림 5개 수집" : "Collect 5 magma cream";
            case "lava_creatures" -> isKorean ? "용암 생물 15마리 처치" : "Kill 15 lava creatures";
            case "heat_survival" -> isKorean ? "5분간 열기 견디기" : "Survive heat for 5 minutes";
            case "create_maps" -> isKorean ? "지도 5장 제작" : "Create 5 maps";
            case "mark_locations" -> isKorean ? "위치 표시된 지도 5장 완성" : "Complete 5 marked maps";
            case "write_report" -> isKorean ? "탐험 보고서 작성" : "Write exploration report";
            case "deliver_relics" -> isKorean ? "유물 조각 제출" : "Deliver relic fragments";
            case "deliver_maps" -> isKorean ? "탐험 지도 제출" : "Deliver exploration maps";
            case "deliver_report" -> isKorean ? "보고서 제출" : "Deliver report";
            case "exploration_complete" -> isKorean ? "탐험 완료 보고" : "Report exploration complete";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("daily_exploration_dialog");

        // 시작
        dialog.addLine("탐험가 길드장",
                "모험가여! 오늘도 미지의 땅을 탐험할 준비가 되었나?",
                "Adventurer! Are you ready to explore unknown lands today?");

        dialog.addLine("탐험가 길드장",
                "5개의 새로운 지역이 발견되었네. 각 지역을 탐험하고 보고서를 작성해주게.",
                "5 new regions have been discovered. Explore each area and write a report.");

        dialog.addLine("플레이어",
                "어떤 지역들인가요?",
                "What kind of regions?");

        dialog.addLine("탐험가 길드장",
                "버려진 광산, 숨겨진 폭포, 고대 유적, 신비한 숲, 그리고 용암 동굴이네.",
                "Abandoned mine, hidden waterfall, ancient ruins, mystic forest, and lava cavern.");

        // 탐험 중
        dialog.addLine("탐험가 길드장",
                "보급품은 충분한가? 특히 용암 동굴은 매우 위험하니 조심하게.",
                "Do you have enough supplies? Be especially careful in the lava cavern.");

        // 발견 보고
        dialog.addLine("플레이어",
                "폭포에서 희귀한 물고기를 발견했어요!",
                "I found rare fish at the waterfall!");

        dialog.addLine("탐험가 길드장",
                "훌륭해! 그런 발견들을 모두 보고서에 기록해주게.",
                "Excellent! Record all such discoveries in your report.");

        // 완료
        dialog.addLine("탐험가 길드장",
                "모든 지역을 탐험했군! 자네의 보고서는 매우 가치있는 정보야.",
                "You've explored all regions! Your report contains very valuable information.");

        dialog.addLine("탐험가 길드장",
                "이 지도들은 다른 탐험가들에게도 큰 도움이 될 거네. 수고했네!",
                "These maps will be of great help to other explorers. Well done!");

        dialog.addLine("탐험가 길드장",
                "내일도 새로운 지역들이 기다리고 있을 거야. 계속 탐험해주게!",
                "New regions will be waiting tomorrow too. Keep exploring!");

        return dialog;
    }
}