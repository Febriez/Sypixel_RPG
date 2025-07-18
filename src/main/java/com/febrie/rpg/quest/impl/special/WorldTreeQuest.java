package com.febrie.rpg.quest.impl.special;

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
 * 세계수 - 특수 퀘스트
 * 죽어가는 세계수를 되살리는 자연의 대서사시
 *
 * @author Febrie
 */
public class WorldTreeQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class WorldTreeBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new WorldTreeQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public WorldTreeQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private WorldTreeQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new WorldTreeBuilder()
                .id(QuestID.SPECIAL_WORLD_TREE)
                .objectives(Arrays.asList(
                        // 세계수의 위기
                        new InteractNPCObjective("tree_guardian", 250), // 세계수 수호자
                        new VisitLocationObjective("world_tree", "yggdrasil_base"),
                        new CollectItemObjective("dead_leaves", Material.DEAD_BUSH, 20),
                        new InteractNPCObjective("dying_tree", 251), // 죽어가는 세계수
                        
                        // 첫 번째 뿌리 - 생명의 샘
                        new VisitLocationObjective("life_spring", "root_of_life"),
                        new BreakBlockObjective("clear_corruption", Material.NETHERRACK, 100),
                        new KillMobObjective("corruption_spirits", EntityType.WITHER_SKELETON, 50),
                        new CollectItemObjective("pure_water", Material.WATER_BUCKET, 10),
                        new PlaceBlockObjective("purify_spring", Material.WATER, 20),
                        new CollectItemObjective("life_essence", Material.GLISTERING_MELON_SLICE, 30),
                        
                        // 두 번째 뿌리 - 지혜의 우물
                        new VisitLocationObjective("wisdom_well", "root_of_wisdom"),
                        new InteractNPCObjective("well_keeper", 252), // 우물 지기
                        new CollectItemObjective("ancient_runes", Material.ENCHANTED_BOOK, 15),
                        new PayCurrencyObjective("wisdom_price", CurrencyType.DIAMOND, 50),
                        new CraftItemObjective("wisdom_potion", Material.POTION, 20),
                        new SurviveObjective("meditation", 600), // 10분 명상
                        new CollectItemObjective("wisdom_water", Material.EXPERIENCE_BOTTLE, 64),
                        
                        // 세 번째 뿌리 - 운명의 가닥
                        new VisitLocationObjective("fate_threads", "root_of_fate"),
                        new CollectItemObjective("fate_strings", Material.STRING, 100),
                        new KillMobObjective("fate_weavers", EntityType.SPIDER, 60),
                        new KillMobObjective("cave_spiders", EntityType.CAVE_SPIDER, 40),
                        new PlaceBlockObjective("weave_fate", Material.COBWEB, 30),
                        new CollectItemObjective("destiny_thread", Material.LEAD, 10),
                        
                        // 네 번째 뿌리 - 시간의 강
                        new VisitLocationObjective("time_river", "root_of_time"),
                        new CollectItemObjective("time_crystals", Material.PRISMARINE_CRYSTALS, 50),
                        new BreakBlockObjective("time_stones", Material.END_STONE, 50),
                        new SurviveObjective("time_flux", 900), // 15분 시간 왜곡
                        new CollectItemObjective("temporal_sand", Material.SOUL_SAND, 30),
                        new CollectItemObjective("chronos_dust", Material.GLOWSTONE_DUST, 50),
                        
                        // 다섯 번째 뿌리 - 꿈의 차원
                        new VisitLocationObjective("dream_dimension", "root_of_dreams"),
                        new KillMobObjective("nightmares", EntityType.PHANTOM, 100),
                        new CollectItemObjective("dream_shards", Material.AMETHYST_SHARD, 30),
                        new PlaceBlockObjective("dream_catchers", Material.AMETHYST_BLOCK, 10),
                        new SurviveObjective("lucid_dream", 600), // 10분 자각몽
                        new CollectItemObjective("dream_essence", Material.ECHO_SHARD, 20),
                        
                        // 세계수 정화 의식
                        new VisitLocationObjective("tree_crown", "yggdrasil_crown"),
                        new DeliverItemObjective("place_life", "tree_altar", Material.GLISTERING_MELON_SLICE, 30),
                        new DeliverItemObjective("place_wisdom", "tree_altar", Material.EXPERIENCE_BOTTLE, 64),
                        new DeliverItemObjective("place_fate", "tree_altar", Material.LEAD, 10),
                        new DeliverItemObjective("place_time", "tree_altar", Material.GLOWSTONE_DUST, 50),
                        new DeliverItemObjective("place_dream", "tree_altar", Material.ECHO_SHARD, 20),
                        
                        // 세계수 부활
                        new PlaceBlockObjective("plant_seeds", Material.OAK_SAPLING, 50),
                        new HarvestObjective("grow_forest", Material.OAK_LOG, 100),
                        new CollectItemObjective("golden_apples", Material.GOLDEN_APPLE, 10),
                        new PayCurrencyObjective("revival_cost", CurrencyType.GOLD, 50000),
                        new SurviveObjective("tree_awakening", 1800), // 30분 각성
                        
                        // 세계수의 선물
                        new InteractNPCObjective("revived_tree", 251),
                        new CollectItemObjective("world_fruit", Material.ENCHANTED_GOLDEN_APPLE, 3),
                        new CollectItemObjective("eternal_leaves", Material.OAK_LEAVES, 100),
                        new InteractNPCObjective("tree_blessing", 250)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 100000)
                        .addCurrency(CurrencyType.DIAMOND, 1000)
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 5)) // 세계수의 열매
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 3)) // 불멸의 토템
                        .addItem(new ItemStack(Material.ELYTRA)) // 세계수의 날개
                        .addItem(new ItemStack(Material.OAK_SAPLING, 64)) // 세계수 묘목
                        .addItem(new ItemStack(Material.BONE_MEAL, 128)) // 성장 촉진제
                        .addExperience(50000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SPECIAL)
                .minLevel(50)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "세계수의 부활" : "Revival of the World Tree";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "죽어가는 세계수 이그드라실을 되살리는 대서사시!",
                    "다섯 뿌리를 정화하고 생명의 힘을 되돌려주세요.",
                    "",
                    "🌳 세계수 이그드라실:",
                    "• 모든 세계를 연결하는 거대한 나무",
                    "• 9개 세계의 중심",
                    "• 생명과 운명의 근원",
                    "• 현재 부패로 죽어가는 중",
                    "",
                    "5개의 신성한 뿌리:",
                    "• 생명의 샘 - 모든 생명의 원천",
                    "• 지혜의 우물 - 무한한 지식",
                    "• 운명의 가닥 - 모든 운명의 실",
                    "• 시간의 강 - 과거와 미래의 흐름",
                    "• 꿈의 차원 - 현실과 환상의 경계",
                    "",
                    "정화 과정:",
                    "• 1단계: 각 뿌리의 부패 제거",
                    "• 2단계: 신성한 정수 수집",
                    "• 3단계: 세계수 정상에서 의식",
                    "• 4단계: 30분간 부활 의식",
                    "• 5단계: 새로운 숲 조성",
                    "",
                    "특별 도전:",
                    "• 부패 정령 50마리 처치",
                    "• 10분간 명상",
                    "• 15분간 시간 왜곡 견디기",
                    "• 100마리 악몽 처치",
                    "• 100그루 나무 심기",
                    "",
                    "필요 자원:",
                    "• 다이아몬드 50개 (지혜의 대가)",
                    "• 골드 50,000 (부활 비용)",
                    "• 각종 정수와 재료들",
                    "",
                    "전설의 보상:",
                    "• 골드 100,000",
                    "• 다이아몬드 1,000개",
                    "• 세계수의 열매 5개",
                    "• 불멸의 토템 3개",
                    "• 세계수의 날개",
                    "• 세계수 묘목 64개",
                    "• 뼛가루 128개",
                    "• 경험치 50,000"
            );
        } else {
            return Arrays.asList(
                    "An epic saga to revive the dying World Tree Yggdrasil!",
                    "Purify the five roots and restore the power of life.",
                    "",
                    "🌳 World Tree Yggdrasil:",
                    "• Giant tree connecting all worlds",
                    "• Center of nine worlds",
                    "• Source of life and fate",
                    "• Currently dying from corruption",
                    "",
                    "5 Sacred Roots:",
                    "• Spring of Life - Source of all life",
                    "• Well of Wisdom - Infinite knowledge",
                    "• Threads of Fate - Strings of all destinies",
                    "• River of Time - Flow of past and future",
                    "• Dream Dimension - Border of reality and fantasy",
                    "",
                    "Purification Process:",
                    "• Stage 1: Remove corruption from each root",
                    "• Stage 2: Collect sacred essences",
                    "• Stage 3: Ritual at tree crown",
                    "• Stage 4: 30-minute revival ritual",
                    "• Stage 5: Create new forest",
                    "",
                    "Special Challenges:",
                    "• Defeat 50 corruption spirits",
                    "• Meditate for 10 minutes",
                    "• Endure time flux for 15 minutes",
                    "• Defeat 100 nightmares",
                    "• Plant 100 trees",
                    "",
                    "Required Resources:",
                    "• 50 Diamonds (price of wisdom)",
                    "• 50,000 Gold (revival cost)",
                    "• Various essences and materials",
                    "",
                    "Legendary Rewards:",
                    "• 100,000 Gold",
                    "• 1,000 Diamonds",
                    "• 5 World Tree Fruits",
                    "• 3 Totems of Undying",
                    "• World Tree Wings",
                    "• 64 World Tree Saplings",
                    "• 128 Bone Meal",
                    "• 50,000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "tree_guardian" -> isKorean ? "세계수 수호자와 대화" : "Talk to World Tree Guardian";
            case "world_tree" -> isKorean ? "세계수 밑동 도착" : "Arrive at World Tree base";
            case "dead_leaves" -> isKorean ? "죽은 잎 20개 수집" : "Collect 20 dead leaves";
            case "dying_tree" -> isKorean ? "죽어가는 세계수와 교감" : "Commune with dying tree";
            case "life_spring" -> isKorean ? "생명의 샘 도달" : "Reach Spring of Life";
            case "clear_corruption" -> isKorean ? "부패 제거 (네더랙 100개)" : "Clear corruption (100 netherrack)";
            case "corruption_spirits" -> isKorean ? "부패 정령 50마리 처치" : "Defeat 50 corruption spirits";
            case "pure_water" -> isKorean ? "정화수 10통 수집" : "Collect 10 pure water";
            case "purify_spring" -> isKorean ? "샘 정화 (물 20블록)" : "Purify spring (20 water blocks)";
            case "life_essence" -> isKorean ? "생명 정수 30개 수집" : "Collect 30 life essence";
            case "wisdom_well" -> isKorean ? "지혜의 우물 도달" : "Reach Well of Wisdom";
            case "well_keeper" -> isKorean ? "우물 지기와 대화" : "Talk to Well Keeper";
            case "ancient_runes" -> isKorean ? "고대 룬 15개 수집" : "Collect 15 ancient runes";
            case "wisdom_price" -> isKorean ? "지혜의 대가 (다이아몬드 50개)" : "Price of wisdom (50 diamonds)";
            case "wisdom_potion" -> isKorean ? "지혜의 물약 20개 제조" : "Brew 20 wisdom potions";
            case "meditation" -> isKorean ? "10분간 명상" : "Meditate for 10 minutes";
            case "wisdom_water" -> isKorean ? "지혜의 물 64병 수집" : "Collect 64 wisdom water";
            case "fate_threads" -> isKorean ? "운명의 가닥 도달" : "Reach Threads of Fate";
            case "fate_strings" -> isKorean ? "운명의 실 100개 수집" : "Collect 100 fate strings";
            case "fate_weavers" -> isKorean ? "운명 직조자 60마리 처치" : "Defeat 60 fate weavers";
            case "cave_spiders" -> isKorean ? "동굴 거미 40마리 처치" : "Defeat 40 cave spiders";
            case "weave_fate" -> isKorean ? "운명 직조 (거미줄 30개)" : "Weave fate (30 cobwebs)";
            case "destiny_thread" -> isKorean ? "운명의 실타래 10개 수집" : "Collect 10 destiny threads";
            case "time_river" -> isKorean ? "시간의 강 도달" : "Reach River of Time";
            case "time_crystals" -> isKorean ? "시간 수정 50개 수집" : "Collect 50 time crystals";
            case "time_stones" -> isKorean ? "시간석 50개 파괴" : "Break 50 time stones";
            case "time_flux" -> isKorean ? "15분간 시간 왜곡 견디기" : "Endure time flux for 15 minutes";
            case "temporal_sand" -> isKorean ? "시간의 모래 30개 수집" : "Collect 30 temporal sand";
            case "chronos_dust" -> isKorean ? "크로노스 가루 50개 수집" : "Collect 50 chronos dust";
            case "dream_dimension" -> isKorean ? "꿈의 차원 진입" : "Enter Dream Dimension";
            case "nightmares" -> isKorean ? "악몽 100마리 처치" : "Defeat 100 nightmares";
            case "dream_shards" -> isKorean ? "꿈의 조각 30개 수집" : "Collect 30 dream shards";
            case "dream_catchers" -> isKorean ? "드림캐처 10개 설치" : "Place 10 dream catchers";
            case "lucid_dream" -> isKorean ? "10분간 자각몽" : "Lucid dream for 10 minutes";
            case "dream_essence" -> isKorean ? "꿈의 정수 20개 수집" : "Collect 20 dream essence";
            case "tree_crown" -> isKorean ? "세계수 정상 도달" : "Reach World Tree crown";
            case "place_life" -> isKorean ? "생명 정수 배치" : "Place life essence";
            case "place_wisdom" -> isKorean ? "지혜의 물 배치" : "Place wisdom water";
            case "place_fate" -> isKorean ? "운명의 실타래 배치" : "Place destiny threads";
            case "place_time" -> isKorean ? "크로노스 가루 배치" : "Place chronos dust";
            case "place_dream" -> isKorean ? "꿈의 정수 배치" : "Place dream essence";
            case "plant_seeds" -> isKorean ? "묘목 50그루 심기" : "Plant 50 saplings";
            case "grow_forest" -> isKorean ? "나무 100그루 수확" : "Harvest 100 trees";
            case "golden_apples" -> isKorean ? "황금 사과 10개 수집" : "Collect 10 golden apples";
            case "revival_cost" -> isKorean ? "부활 비용 50,000골드" : "Revival cost 50,000 gold";
            case "tree_awakening" -> isKorean ? "30분간 세계수 각성" : "30 minutes tree awakening";
            case "revived_tree" -> isKorean ? "부활한 세계수와 교감" : "Commune with revived tree";
            case "world_fruit" -> isKorean ? "세계수 열매 3개 획득" : "Obtain 3 World Tree fruits";
            case "eternal_leaves" -> isKorean ? "영원의 잎 100개 수집" : "Collect 100 eternal leaves";
            case "tree_blessing" -> isKorean ? "세계수의 축복 받기" : "Receive World Tree blessing";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("world_tree_dialog");

        // 시작
        dialog.addLine("세계수 수호자",
                "세계수가... 죽어가고 있습니다. 부패가 뿌리부터 퍼지고 있어요.",
                "The World Tree is... dying. Corruption spreads from the roots.");

        dialog.addLine("세계수 수호자",
                "당신만이 세계수를 구할 수 있습니다. 다섯 뿌리를 정화해주세요.",
                "Only you can save the World Tree. Please purify the five roots.");

        dialog.addLine("죽어가는 세계수",
                "아... 아프다... 내 뿌리가... 썩어가고 있어...",
                "Ah... it hurts... my roots... are rotting...");

        dialog.addLine("플레이어",
                "걱정 마세요. 제가 당신을 구하겠습니다.",
                "Don't worry. I will save you.");

        // 생명의 샘
        dialog.addLine("세계수 수호자",
                "생명의 샘이 네더의 부패로 오염되었습니다. 정화가 필요해요.",
                "The Spring of Life is contaminated with Nether corruption. It needs purification.");

        // 지혜의 우물
        dialog.addLine("우물 지기",
                "지혜를 원하는가? 그럼 대가를 치러야지. 다이아몬드 50개다.",
                "You seek wisdom? Then pay the price. 50 diamonds.");

        dialog.addLine("우물 지기",
                "지혜의 물은 세계수에게 생각하는 힘을 준다. 잊지 마라.",
                "The water of wisdom gives the World Tree power to think. Don't forget.");

        // 운명의 가닥
        dialog.addLine("세계수 수호자",
                "운명의 가닥들이 끊어지고 있어요. 거미들이 실을 훔쳐갔습니다.",
                "The threads of fate are breaking. Spiders have stolen the strings.");

        // 시간의 강
        dialog.addLine("세계수 수호자",
                "시간의 강이 역류하고 있습니다. 과거와 미래가 뒤섞이고 있어요.",
                "The River of Time is flowing backward. Past and future are mixing.");

        // 꿈의 차원
        dialog.addLine("세계수 수호자",
                "악몽들이 세계수의 꿈을 침식하고 있습니다. 그들을 물리쳐주세요.",
                "Nightmares are eroding the World Tree's dreams. Please defeat them.");

        // 부활 의식
        dialog.addLine("세계수 수호자",
                "모든 정수를 모았군요! 이제 세계수 정상에서 부활 의식을 시작합시다.",
                "You've gathered all essences! Now let's begin the revival ritual at the tree crown.");

        // 부활
        dialog.addLine("부활한 세계수",
                "아... 다시 숨을 쉴 수 있어... 생명이 돌아왔어...",
                "Ah... I can breathe again... life has returned...");

        dialog.addLine("부활한 세계수",
                "감사합니다, 구원자여. 이 열매를 받으세요. 영원한 생명의 선물입니다.",
                "Thank you, savior. Take these fruits. They are gifts of eternal life.");

        // 완료
        dialog.addLine("세계수 수호자",
                "정말 해냈군요! 세계수가 다시 살아났습니다! 온 세계가 당신께 감사할 겁니다.",
                "You really did it! The World Tree lives again! The whole world will thank you.");

        dialog.addLine("세계수 수호자",
                "이 날개는 세계수의 축복입니다. 하늘을 날며 세계를 지켜주세요.",
                "These wings are the World Tree's blessing. Fly and protect the world.");

        return dialog;
    }
}