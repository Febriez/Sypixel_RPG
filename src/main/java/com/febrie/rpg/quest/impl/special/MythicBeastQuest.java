package com.febrie.rpg.quest.impl.special;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
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
 * 신화의 야수 - 특수 퀘스트
 * 전설적인 4대 신수를 추적하고 계약하는 대서사시 퀘스트
 *
 * @author Febrie
 */
public class MythicBeastQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class MythicBeastBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new MythicBeastQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public MythicBeastQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private MythicBeastQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new MythicBeastBuilder()
                .id(QuestID.SPECIAL_MYTHIC_BEAST)
                .objectives(Arrays.asList(
                        // 전설의 시작
                        new InteractNPCObjective("beast_scholar", 240), // 신수 학자
                        new CollectItemObjective("ancient_compass", Material.COMPASS, 1),
                        new CollectItemObjective("beast_chronicles", Material.WRITTEN_BOOK, 4),
                        
                        // 첫 번째 신수 - 청룡 (동쪽의 수호자)
                        new VisitLocationObjective("eastern_shrine", "azure_dragon_shrine"),
                        new PlaceBlockObjective("offering_sapphire", Material.LAPIS_BLOCK, 9),
                        new CollectItemObjective("dragon_scales", Material.PRISMARINE_SHARD, 50),
                        new SurviveObjective("storm_trial", 600), // 10분간 폭풍 시련
                        new KillMobObjective("storm_elementals", EntityType.PHANTOM, 50),
                        new KillMobObjective("lightning_spirits", EntityType.BLAZE, 30),
                        new InteractNPCObjective("azure_dragon", 241), // 청룡
                        new CollectItemObjective("dragon_pearl", Material.HEART_OF_THE_SEA, 1),
                        new DeliverItemObjective("dragon_contract", "beast_scholar", Material.HEART_OF_THE_SEA, 1),
                        
                        // 두 번째 신수 - 백호 (서쪽의 수호자)
                        new VisitLocationObjective("western_shrine", "white_tiger_shrine"),
                        new PlaceBlockObjective("offering_quartz", Material.QUARTZ_BLOCK, 9),
                        new CollectItemObjective("tiger_fangs", Material.IRON_NUGGET, 100),
                        new KillMobObjective("jungle_predators", EntityType.OCELOT, 30),
                        new KillMobObjective("spirit_tigers", EntityType.SNOW_GOLEM, 20),
                        new SurviveObjective("hunt_trial", 900), // 15분간 사냥 시련
                        new KillPlayerObjective("prove_warrior", 5), // 전사의 증명
                        new InteractNPCObjective("white_tiger", 242), // 백호
                        new CollectItemObjective("tiger_claw", Material.BONE, 1),
                        new DeliverItemObjective("tiger_contract", "beast_scholar", Material.BONE, 1),
                        
                        // 세 번째 신수 - 주작 (남쪽의 수호자)
                        new VisitLocationObjective("southern_shrine", "vermillion_bird_shrine"),
                        new PlaceBlockObjective("offering_redstone", Material.REDSTONE_BLOCK, 9),
                        new CollectItemObjective("phoenix_feathers", Material.FEATHER, 100),
                        new BreakBlockObjective("break_ice", Material.ICE, 50),
                        new KillMobObjective("fire_phoenixes", EntityType.BLAZE, 40),
                        new KillMobObjective("lava_spirits", EntityType.MAGMA_CUBE, 30),
                        new CraftItemObjective("fire_resistance", Material.POTION, 10),
                        new SurviveObjective("rebirth_trial", 600), // 10분간 재생 시련
                        new InteractNPCObjective("vermillion_bird", 243), // 주작
                        new CollectItemObjective("phoenix_egg", Material.DRAGON_EGG, 1),
                        new DeliverItemObjective("bird_contract", "beast_scholar", Material.DRAGON_EGG, 1),
                        
                        // 네 번째 신수 - 현무 (북쪽의 수호자)
                        new VisitLocationObjective("northern_shrine", "black_tortoise_shrine"),
                        new PlaceBlockObjective("offering_obsidian", Material.OBSIDIAN, 9),
                        new CollectItemObjective("turtle_shells", Material.TURTLE_SCUTE, 20),
                        new KillMobObjective("sea_guardians", EntityType.GUARDIAN, 40),
                        new KillMobObjective("elder_guardians", EntityType.ELDER_GUARDIAN, 3),
                        new CollectItemObjective("ancient_coral", Material.BRAIN_CORAL_BLOCK, 10),
                        new SurviveObjective("depth_trial", 1200), // 20분간 심해 시련
                        new InteractNPCObjective("black_tortoise", 244), // 현무
                        new CollectItemObjective("turtle_shell", Material.TURTLE_HELMET, 1),
                        new DeliverItemObjective("tortoise_contract", "beast_scholar", Material.TURTLE_HELMET, 1),
                        
                        // 4신수 각성 의식
                        new VisitLocationObjective("convergence_shrine", "four_beasts_altar"),
                        new PlaceBlockObjective("place_contracts", Material.BEACON, 4),
                        new PayCurrencyObjective("ritual_cost", CurrencyType.DIAMOND, 200),
                        new SurviveObjective("awakening_ritual", 1800), // 30분간 각성 의식
                        
                        // 최종 시험 - 4신수 동시 전투
                        new KillMobObjective("dragon_avatar", EntityType.ENDER_DRAGON, 1),
                        new KillMobObjective("tiger_avatar", EntityType.RAVAGER, 1),
                        new KillMobObjective("bird_avatar", EntityType.PHANTOM, 100),
                        new KillMobObjective("tortoise_avatar", EntityType.ELDER_GUARDIAN, 5),
                        
                        // 신수의 가호 획득
                        new CollectItemObjective("beast_blessing", Material.NETHER_STAR, 4),
                        new InteractNPCObjective("final_contract", 240)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 80000)
                        .addCurrency(CurrencyType.DIAMOND, 800)
                        .addItem(new ItemStack(Material.ELYTRA)) // 신수의 날개
                        .addItem(new ItemStack(Material.TRIDENT)) // 신수의 삼지창
                        .addItem(new ItemStack(Material.TURTLE_HELMET)) // 현무의 투구
                        .addItem(new ItemStack(Material.HEART_OF_THE_SEA)) // 청룡의 진주
                        .addItem(new ItemStack(Material.DRAGON_EGG)) // 주작의 알
                        .addItem(new ItemStack(Material.NETHER_STAR, 4)) // 4신수의 별
                        .addExperience(40000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SPECIAL)
                .minLevel(55)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "사신수의 계약" : "Contract of Four Divine Beasts";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "동서남북을 수호하는 4대 신수와 계약을 맺는 전설의 여정!",
                    "청룡, 백호, 주작, 현무의 시련을 통과하고 그들의 가호를 받으세요.",
                    "",
                    "🐉 4대 신수:",
                    "• 청룡 - 동쪽의 수호자, 폭풍과 번개의 주인",
                    "• 백호 - 서쪽의 수호자, 전투와 용맹의 화신",
                    "• 주작 - 남쪽의 수호자, 불사와 재생의 상징",
                    "• 현무 - 북쪽의 수호자, 지혜와 수명의 거북",
                    "",
                    "각 신수의 시련:",
                    "• 청룡 - 10분간 폭풍 속에서 생존",
                    "• 백호 - 15분간 사냥의 시련",
                    "• 주작 - 10분간 불사조의 재생",
                    "• 현무 - 20분간 심해의 시련",
                    "",
                    "특별 도전:",
                    "• 각 신수의 성소 방문",
                    "• 신수별 특별 제물 바치기",
                    "• 4신수 동시 전투",
                    "• 30분간 각성 의식",
                    "",
                    "필요 준비물:",
                    "• 각 신수별 제물 (블록 9개씩)",
                    "• 다이아몬드 200개 (의식 비용)",
                    "• 높은 전투력과 생존 능력",
                    "",
                    "경고:",
                    "• 극도로 어려운 난이도",
                    "• 레벨 55 이상 필수",
                    "• 한 번만 도전 가능",
                    "",
                    "전설적인 보상:",
                    "• 골드 80,000",
                    "• 다이아몬드 800개",
                    "• 신수의 날개 (겉날개)",
                    "• 신수의 삼지창",
                    "• 현무의 투구",
                    "• 청룡의 진주",
                    "• 주작의 알",
                    "• 4신수의 별 4개",
                    "• 경험치 40,000"
            );
        } else {
            return Arrays.asList(
                    "A legendary journey to form contracts with the Four Divine Beasts!",
                    "Pass the trials of Azure Dragon, White Tiger, Vermillion Bird, and Black Tortoise.",
                    "",
                    "🐉 Four Divine Beasts:",
                    "• Azure Dragon - Eastern Guardian, Master of Storm and Lightning",
                    "• White Tiger - Western Guardian, Avatar of Battle and Valor",
                    "• Vermillion Bird - Southern Guardian, Symbol of Immortality and Rebirth",
                    "• Black Tortoise - Northern Guardian, Turtle of Wisdom and Longevity",
                    "",
                    "Each Beast's Trial:",
                    "• Azure Dragon - Survive 10 minutes in storm",
                    "• White Tiger - 15 minutes hunting trial",
                    "• Vermillion Bird - 10 minutes phoenix rebirth",
                    "• Black Tortoise - 20 minutes deep sea trial",
                    "",
                    "Special Challenges:",
                    "• Visit each beast's shrine",
                    "• Offer special tributes to each beast",
                    "• Simultaneous battle with 4 beasts",
                    "• 30-minute awakening ritual",
                    "",
                    "Requirements:",
                    "• Tribute blocks for each beast (9 each)",
                    "• 200 Diamonds (ritual cost)",
                    "• High combat and survival ability",
                    "",
                    "Warning:",
                    "• Extremely difficult",
                    "• Level 55+ required",
                    "• One attempt only",
                    "",
                    "Legendary Rewards:",
                    "• 80,000 Gold",
                    "• 800 Diamonds",
                    "• Divine Beast Wings (Elytra)",
                    "• Divine Beast Trident",
                    "• Black Tortoise Helmet",
                    "• Azure Dragon Pearl",
                    "• Vermillion Bird Egg",
                    "• 4 Divine Beast Stars",
                    "• 40,000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "beast_scholar" -> isKorean ? "신수 학자와 대화" : "Talk to Beast Scholar";
            case "ancient_compass" -> isKorean ? "고대 나침반 획득" : "Obtain ancient compass";
            case "beast_chronicles" -> isKorean ? "신수 연대기 4권 수집" : "Collect 4 beast chronicles";
            case "eastern_shrine" -> isKorean ? "동방 청룡 신전 방문" : "Visit Eastern Azure Dragon Shrine";
            case "offering_sapphire" -> isKorean ? "청금석 블록 9개 바치기" : "Offer 9 lapis blocks";
            case "dragon_scales" -> isKorean ? "용의 비늘 50개 수집" : "Collect 50 dragon scales";
            case "storm_trial" -> isKorean ? "10분간 폭풍 시련 생존" : "Survive storm trial for 10 minutes";
            case "storm_elementals" -> isKorean ? "폭풍 정령 50마리 처치" : "Kill 50 storm elementals";
            case "lightning_spirits" -> isKorean ? "번개 정령 30마리 처치" : "Kill 30 lightning spirits";
            case "azure_dragon" -> isKorean ? "청룡과 대면" : "Face Azure Dragon";
            case "dragon_pearl" -> isKorean ? "청룡의 진주 획득" : "Obtain Dragon Pearl";
            case "dragon_contract" -> isKorean ? "청룡 계약서 전달" : "Deliver dragon contract";
            case "western_shrine" -> isKorean ? "서방 백호 신전 방문" : "Visit Western White Tiger Shrine";
            case "offering_quartz" -> isKorean ? "석영 블록 9개 바치기" : "Offer 9 quartz blocks";
            case "tiger_fangs" -> isKorean ? "호랑이 송곳니 100개 수집" : "Collect 100 tiger fangs";
            case "jungle_predators" -> isKorean ? "정글 포식자 30마리 처치" : "Kill 30 jungle predators";
            case "spirit_tigers" -> isKorean ? "영혼 호랑이 20마리 처치" : "Kill 20 spirit tigers";
            case "hunt_trial" -> isKorean ? "15분간 사냥 시련" : "15 minutes hunting trial";
            case "prove_warrior" -> isKorean ? "전사의 증명 (PvP 5승)" : "Prove as warrior (5 PvP wins)";
            case "white_tiger" -> isKorean ? "백호와 대면" : "Face White Tiger";
            case "tiger_claw" -> isKorean ? "백호의 발톱 획득" : "Obtain Tiger Claw";
            case "tiger_contract" -> isKorean ? "백호 계약서 전달" : "Deliver tiger contract";
            case "southern_shrine" -> isKorean ? "남방 주작 신전 방문" : "Visit Southern Vermillion Bird Shrine";
            case "offering_redstone" -> isKorean ? "레드스톤 블록 9개 바치기" : "Offer 9 redstone blocks";
            case "phoenix_feathers" -> isKorean ? "불사조 깃털 100개 수집" : "Collect 100 phoenix feathers";
            case "break_ice" -> isKorean ? "얼음 50개 파괴" : "Break 50 ice";
            case "fire_phoenixes" -> isKorean ? "화염 불사조 40마리 처치" : "Kill 40 fire phoenixes";
            case "lava_spirits" -> isKorean ? "용암 정령 30마리 처치" : "Kill 30 lava spirits";
            case "fire_resistance" -> isKorean ? "화염 저항 물약 10개 제조" : "Brew 10 fire resistance potions";
            case "rebirth_trial" -> isKorean ? "10분간 재생 시련" : "10 minutes rebirth trial";
            case "vermillion_bird" -> isKorean ? "주작과 대면" : "Face Vermillion Bird";
            case "phoenix_egg" -> isKorean ? "불사조의 알 획득" : "Obtain Phoenix Egg";
            case "bird_contract" -> isKorean ? "주작 계약서 전달" : "Deliver bird contract";
            case "northern_shrine" -> isKorean ? "북방 현무 신전 방문" : "Visit Northern Black Tortoise Shrine";
            case "offering_obsidian" -> isKorean ? "흑요석 9개 바치기" : "Offer 9 obsidian";
            case "turtle_shells" -> isKorean ? "거북 등딱지 20개 수집" : "Collect 20 turtle scutes";
            case "sea_guardians" -> isKorean ? "바다 수호자 40마리 처치" : "Kill 40 sea guardians";
            case "elder_guardians" -> isKorean ? "엘더 가디언 3마리 처치" : "Kill 3 elder guardians";
            case "ancient_coral" -> isKorean ? "고대 산호 10개 수집" : "Collect 10 ancient coral";
            case "depth_trial" -> isKorean ? "20분간 심해 시련" : "20 minutes deep sea trial";
            case "black_tortoise" -> isKorean ? "현무와 대면" : "Face Black Tortoise";
            case "turtle_shell" -> isKorean ? "현무의 투구 획득" : "Obtain Turtle Helmet";
            case "tortoise_contract" -> isKorean ? "현무 계약서 전달" : "Deliver tortoise contract";
            case "convergence_shrine" -> isKorean ? "사신수 제단 도달" : "Reach Four Beasts Altar";
            case "place_contracts" -> isKorean ? "계약서 4개 배치" : "Place 4 contracts";
            case "ritual_cost" -> isKorean ? "의식 비용 다이아몬드 200개" : "Ritual cost 200 diamonds";
            case "awakening_ritual" -> isKorean ? "30분간 각성 의식" : "30 minutes awakening ritual";
            case "dragon_avatar" -> isKorean ? "청룡의 화신 처치" : "Defeat Dragon Avatar";
            case "tiger_avatar" -> isKorean ? "백호의 화신 처치" : "Defeat Tiger Avatar";
            case "bird_avatar" -> isKorean ? "주작의 화신 100마리 처치" : "Defeat 100 Bird Avatars";
            case "tortoise_avatar" -> isKorean ? "현무의 화신 5마리 처치" : "Defeat 5 Tortoise Avatars";
            case "beast_blessing" -> isKorean ? "신수의 축복 4개 획득" : "Obtain 4 Beast Blessings";
            case "final_contract" -> isKorean ? "최종 계약 완료" : "Complete final contract";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("mythic_beast_dialog");

        // 시작
        dialog.addLine("신수 학자",
                "당신이 바로 예언에 나온 자인가요? 4신수와 계약할 운명을 지닌...",
                "Are you the one from the prophecy? Destined to contract with the Four Divine Beasts...");

        dialog.addLine("신수 학자",
                "청룡, 백호, 주작, 현무... 세상의 균형을 지키는 네 신수가 당신을 기다립니다.",
                "Azure Dragon, White Tiger, Vermillion Bird, Black Tortoise... The four beasts maintaining world balance await you.");

        dialog.addLine("플레이어",
                "어떻게 그들을 만날 수 있나요?",
                "How can I meet them?");

        dialog.addLine("신수 학자",
                "각 신수의 신전으로 가서 그들의 시련을 통과해야 합니다. 쉽지 않을 겁니다.",
                "Go to each beast's shrine and pass their trials. It won't be easy.");

        // 청룡
        dialog.addLine("청룡",
                "폭풍을 다스리는 자여, 나의 힘을 원하는가?",
                "One who controls storms, do you seek my power?");

        dialog.addLine("청룡",
                "번개와 비바람 속에서 살아남아라. 그것이 나의 시련이다!",
                "Survive in lightning and storms. That is my trial!");

        // 백호
        dialog.addLine("백호",
                "전사여, 네 용맹을 증명하라!",
                "Warrior, prove your valor!");

        dialog.addLine("백호",
                "사냥꾼이 되어 먹이를 쫓고, 적을 물리쳐라!",
                "Become a hunter, chase prey, and defeat enemies!");

        // 주작
        dialog.addLine("주작",
                "불사의 비밀을 알고자 하는가? 먼저 죽음을 경험하라!",
                "Do you wish to know the secret of immortality? First experience death!");

        dialog.addLine("주작",
                "재에서 다시 태어나는 자만이 진정한 불사조가 될 수 있다.",
                "Only those reborn from ashes can become a true phoenix.");

        // 현무
        dialog.addLine("현무",
                "깊은 바다의 지혜를 구하는구나. 심연의 압박을 견딜 수 있겠나?",
                "You seek wisdom of deep seas. Can you withstand the pressure of the abyss?");

        dialog.addLine("현무",
                "천 년의 인내가 필요하다. 시간은 나의 편이니까.",
                "A thousand years of patience is needed. Time is on my side.");

        // 최종 의식
        dialog.addLine("신수 학자",
                "놀랍습니다! 4신수 모두와 계약을 맺었군요!",
                "Amazing! You've made contracts with all four divine beasts!");

        dialog.addLine("신수 학자",
                "이제 마지막 의식입니다. 4신수의 힘을 하나로 합쳐야 합니다.",
                "Now for the final ritual. We must unite the power of the four beasts.");

        // 완료
        dialog.addLine("신수 학자",
                "해냈습니다! 당신은 이제 4신수의 가호를 받은 전설의 존재입니다!",
                "You did it! You are now a legendary being blessed by the Four Divine Beasts!");

        dialog.addLine("신수 학자",
                "이 날개와 무기들은 신수들의 선물입니다. 세상의 균형을 지켜주세요.",
                "These wings and weapons are gifts from the divine beasts. Please maintain the world's balance.");

        return dialog;
    }
}