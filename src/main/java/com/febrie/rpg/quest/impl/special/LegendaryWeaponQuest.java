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
 * 전설의 무기 - 특수 퀘스트
 * 고대의 전설 무기를 제작하는 대서사시 퀘스트
 *
 * @author Febrie
 */
public class LegendaryWeaponQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class LegendaryWeaponBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new LegendaryWeaponQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public LegendaryWeaponQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private LegendaryWeaponQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new LegendaryWeaponBuilder()
                .id(QuestID.SPECIAL_LEGENDARY_WEAPON)
                .objectives(Arrays.asList(
                        // 전설의 시작
                        new InteractNPCObjective("ancient_blacksmith", "ancient_blacksmith"), // 고대 대장장이
                        new CollectItemObjective("ancient_blueprints", Material.WRITTEN_BOOK, 1),
                        new VisitLocationObjective("forgotten_forge", "ancient_forge_location"),
                        
                        // 첫 번째 재료 - 별의 정수
                        new VisitLocationObjective("star_peak", "celestial_mountain_peak"),
                        new KillMobObjective("star_guardians", EntityType.BLAZE, 30),
                        new CollectItemObjective("star_fragments", Material.NETHER_STAR, 5),
                        new SurviveObjective("meteor_shower", 300), // 5분간 유성우 생존
                        new CollectItemObjective("celestial_core", Material.BEACON, 1),
                        
                        // 두 번째 재료 - 심연의 심장
                        new VisitLocationObjective("abyss_entrance", "deepest_cave_entrance"),
                        new BreakBlockObjective("mine_deep", Material.DEEPSLATE, 100),
                        new KillMobObjective("abyss_dwellers", EntityType.WARDEN, 3),
                        new CollectItemObjective("void_crystals", Material.ECHO_SHARD, 10),
                        new CollectItemObjective("heart_of_abyss", Material.SCULK_CATALYST, 1),
                        
                        // 세 번째 재료 - 용의 불꽃
                        new InteractNPCObjective("dragon_keeper", "dragon_keeper"), // 용의 수호자
                        new PayCurrencyObjective("dragon_tribute", CurrencyType.DIAMOND, 50),
                        new VisitLocationObjective("dragon_nest", "ancient_dragon_nest"),
                        new KillMobObjective("dragon_whelps", EntityType.PHANTOM, 25),
                        new CollectItemObjective("dragon_scales", Material.PHANTOM_MEMBRANE, 20),
                        new KillMobObjective("elder_dragon", EntityType.ENDER_DRAGON, 1),
                        new CollectItemObjective("dragon_flame", Material.DRAGON_BREATH, 5),
                        
                        // 네 번째 재료 - 신의 축복
                        new VisitLocationObjective("temple_of_gods", "divine_temple"),
                        new PlaceBlockObjective("offering_gold", Material.GOLD_BLOCK, 20),
                        new PlaceBlockObjective("offering_diamond", Material.DIAMOND_BLOCK, 10),
                        new SurviveObjective("divine_trial", 600), // 10분간 신의 시험
                        new KillPlayerObjective("prove_worth", 5), // PvP로 가치 증명
                        new CollectItemObjective("divine_blessing", Material.ENCHANTED_GOLDEN_APPLE, 1),
                        
                        // 대장정 제작 준비
                        new DeliverItemObjective("deliver_star", "ancient_blacksmith", Material.BEACON, 1),
                        new DeliverItemObjective("deliver_heart", "ancient_blacksmith", Material.SCULK_CATALYST, 1),
                        new DeliverItemObjective("deliver_flame", "ancient_blacksmith", Material.DRAGON_BREATH, 5),
                        new DeliverItemObjective("deliver_blessing", "ancient_blacksmith", Material.ENCHANTED_GOLDEN_APPLE, 1),
                        
                        // 제작 과정
                        new CollectItemObjective("mythril_ingots", Material.NETHERITE_INGOT, 10),
                        new PlaceBlockObjective("setup_anvil", Material.ANVIL, 1),
                        new CraftItemObjective("forge_base", Material.NETHERITE_SWORD, 1),
                        new PayCurrencyObjective("enchanting_cost", CurrencyType.GOLD, 20000),
                        
                        // 최종 각성
                        new VisitLocationObjective("awakening_altar", "legendary_altar"),
                        new PlaceBlockObjective("place_weapon", Material.NETHERITE_SWORD, 1),
                        new KillMobObjective("trial_of_legends", EntityType.WITHER, 3),
                        new SurviveObjective("final_awakening", 900), // 15분간 최종 각성
                        
                        // 완성
                        new CollectItemObjective("legendary_weapon", Material.NETHERITE_SWORD, 1),
                        new InteractNPCObjective("completion_ceremony", "ancient_blacksmith")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 50000)
                        .addCurrency(CurrencyType.DIAMOND, 500)
                        .addItem(new ItemStack(Material.NETHERITE_SWORD)) // 전설의 무기
                        .addItem(new ItemStack(Material.NETHER_STAR, 3))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 10))
                        .addItem(new ItemStack(Material.NETHERITE_INGOT, 5))
                        .addExperience(20000)
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
        return isKorean ? "전설의 무기 제작" : "Forging the Legendary Weapon";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "신화 속에만 존재하던 전설의 무기를 제작하는 대장정입니다.",
                    "네 가지 신성한 재료를 모아 최강의 무기를 만들어보세요.",
                    "",
                    "⚔️ 전설의 무기 특성:",
                    "• 모든 적에게 추가 피해",
                    "• 특별한 스킬 발동",
                    "• 업그레이드 가능",
                    "• 거래 불가, 귀속 아이템",
                    "",
                    "필요한 재료:",
                    "• 별의 정수 - 천상의 산 정상에서",
                    "• 심연의 심장 - 가장 깊은 동굴에서",
                    "• 용의 불꽃 - 고대 용의 둥지에서",
                    "• 신의 축복 - 신전의 시험을 통해",
                    "",
                    "제작 단계:",
                    "• 1단계: 고대 설계도 획득",
                    "• 2단계: 네 가지 신성한 재료 수집",
                    "• 3단계: 고대 대장간에서 제작",
                    "• 4단계: 전설의 제단에서 각성",
                    "",
                    "주요 도전:",
                    "• 별의 수호자 30마리 처치",
                    "• 워든 3마리 사냥",
                    "• 엔더 드래곤 처치",
                    "• 위더 3마리 처치",
                    "• 다수의 생존 시험",
                    "",
                    "보상:",
                    "• 전설의 무기 (네더라이트 검)",
                    "• 골드 50,000",
                    "• 다이아몬드 500개",
                    "• 네더의 별 3개",
                    "• 마법이 부여된 책 10개",
                    "• 네더라이트 주괴 5개",
                    "• 경험치 20,000"
            );
        } else {
            return Arrays.asList(
                    "Embark on an epic journey to forge the legendary weapon of myths.",
                    "Gather four divine materials to create the ultimate weapon.",
                    "",
                    "⚔️ Legendary Weapon Features:",
                    "• Extra damage to all enemies",
                    "• Special skill activation",
                    "• Upgradeable",
                    "• Untradeable, Soulbound",
                    "",
                    "Required Materials:",
                    "• Star Essence - From Celestial Mountain Peak",
                    "• Heart of Abyss - From Deepest Cave",
                    "• Dragon's Flame - From Ancient Dragon Nest",
                    "• Divine Blessing - Through Temple Trial",
                    "",
                    "Crafting Stages:",
                    "• Stage 1: Obtain Ancient Blueprints",
                    "• Stage 2: Collect Four Divine Materials",
                    "• Stage 3: Forge at Ancient Smithy",
                    "• Stage 4: Awaken at Legendary Altar",
                    "",
                    "Major Challenges:",
                    "• Defeat 30 Star Guardians",
                    "• Hunt 3 Wardens",
                    "• Slay the Ender Dragon",
                    "• Defeat 3 Withers",
                    "• Multiple survival trials",
                    "",
                    "Rewards:",
                    "• Legendary Weapon (Netherite Sword)",
                    "• 50,000 Gold",
                    "• 500 Diamonds",
                    "• 3 Nether Stars",
                    "• 10 Enchanted Books",
                    "• 5 Netherite Ingots",
                    "• 20,000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "ancient_blacksmith" -> isKorean ? "고대 대장장이와 대화" : "Talk to the Ancient Blacksmith";
            case "ancient_blueprints" -> isKorean ? "고대 설계도 획득" : "Obtain Ancient Blueprints";
            case "forgotten_forge" -> isKorean ? "잊혀진 대장간 방문" : "Visit the Forgotten Forge";
            case "star_peak" -> isKorean ? "천상의 산 정상 도달" : "Reach Celestial Mountain Peak";
            case "star_guardians" -> isKorean ? "별의 수호자 30마리 처치" : "Defeat 30 Star Guardians";
            case "star_fragments" -> isKorean ? "별의 파편 5개 수집" : "Collect 5 Star Fragments";
            case "meteor_shower" -> isKorean ? "유성우 5분간 생존" : "Survive meteor shower for 5 minutes";
            case "celestial_core" -> isKorean ? "천상의 핵 획득" : "Obtain Celestial Core";
            case "abyss_entrance" -> isKorean ? "심연의 입구 도달" : "Reach Abyss Entrance";
            case "mine_deep" -> isKorean ? "심층암 100개 채굴" : "Mine 100 Deepslate";
            case "abyss_dwellers" -> isKorean ? "심연의 거주자(워든) 3마리 처치" : "Defeat 3 Abyss Dwellers (Wardens)";
            case "void_crystals" -> isKorean ? "공허의 수정 10개 수집" : "Collect 10 Void Crystals";
            case "heart_of_abyss" -> isKorean ? "심연의 심장 획득" : "Obtain Heart of Abyss";
            case "dragon_keeper" -> isKorean ? "용의 수호자와 대화" : "Talk to the Dragon Keeper";
            case "dragon_tribute" -> isKorean ? "용에게 공물 바치기 (다이아몬드 50개)" : "Pay dragon tribute (50 Diamonds)";
            case "dragon_nest" -> isKorean ? "고대 용의 둥지 방문" : "Visit Ancient Dragon Nest";
            case "dragon_whelps" -> isKorean ? "용의 새끼 25마리 처치" : "Defeat 25 Dragon Whelps";
            case "dragon_scales" -> isKorean ? "용의 비늘 20개 수집" : "Collect 20 Dragon Scales";
            case "elder_dragon" -> isKorean ? "고대 용 처치" : "Slay the Elder Dragon";
            case "dragon_flame" -> isKorean ? "용의 불꽃 5개 수집" : "Collect 5 Dragon's Flame";
            case "temple_of_gods" -> isKorean ? "신들의 신전 방문" : "Visit Temple of Gods";
            case "offering_gold" -> isKorean ? "금 블록 20개 바치기" : "Offer 20 Gold Blocks";
            case "offering_diamond" -> isKorean ? "다이아몬드 블록 10개 바치기" : "Offer 10 Diamond Blocks";
            case "divine_trial" -> isKorean ? "신의 시험 10분간 통과" : "Pass Divine Trial for 10 minutes";
            case "prove_worth" -> isKorean ? "PvP로 5명 처치하여 가치 증명" : "Prove worth by defeating 5 players in PvP";
            case "divine_blessing" -> isKorean ? "신의 축복 받기" : "Receive Divine Blessing";
            case "deliver_star" -> isKorean ? "천상의 핵 전달" : "Deliver Celestial Core";
            case "deliver_heart" -> isKorean ? "심연의 심장 전달" : "Deliver Heart of Abyss";
            case "deliver_flame" -> isKorean ? "용의 불꽃 전달" : "Deliver Dragon's Flame";
            case "deliver_blessing" -> isKorean ? "신의 축복 전달" : "Deliver Divine Blessing";
            case "mythril_ingots" -> isKorean ? "미스릴 주괴 10개 수집" : "Collect 10 Mythril Ingots";
            case "setup_anvil" -> isKorean ? "모루 설치" : "Place Anvil";
            case "forge_base" -> isKorean ? "기본 무기 제작" : "Forge base weapon";
            case "enchanting_cost" -> isKorean ? "인챈트 비용 20,000골드 지불" : "Pay 20,000 gold for enchanting";
            case "awakening_altar" -> isKorean ? "각성의 제단 도달" : "Reach Awakening Altar";
            case "place_weapon" -> isKorean ? "제단에 무기 배치" : "Place weapon on altar";
            case "trial_of_legends" -> isKorean ? "전설의 시험 - 위더 3마리 처치" : "Trial of Legends - Defeat 3 Withers";
            case "final_awakening" -> isKorean ? "15분간 최종 각성 의식" : "Final awakening ritual for 15 minutes";
            case "legendary_weapon" -> isKorean ? "전설의 무기 획득" : "Obtain Legendary Weapon";
            case "completion_ceremony" -> isKorean ? "완성 의식" : "Completion ceremony";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("legendary_weapon_dialog");

        // 시작
        dialog.addLine("고대 대장장이",
                "오... 당신이군요. 전설의 무기를 찾는 자.",
                "Oh... it's you. The one seeking the legendary weapon.");

        dialog.addLine("고대 대장장이",
                "천 년 전, 나는 신들을 위해 무기를 만들었소. 그 지식은 아직 내게 있지.",
                "A thousand years ago, I forged weapons for the gods. That knowledge still remains with me.");

        dialog.addLine("플레이어",
                "그 무기를 만들 수 있나요?",
                "Can you forge that weapon?");

        dialog.addLine("고대 대장장이",
                "가능하오. 하지만 네 가지 신성한 재료가 필요하지. 각각은 치명적인 위험을 동반하오.",
                "It's possible. But you'll need four divine materials. Each comes with deadly dangers.");

        // 재료 설명
        dialog.addLine("고대 대장장이",
                "별의 정수는 천상의 산에서, 심연의 심장은 세상의 가장 깊은 곳에서...",
                "Star Essence from Celestial Mountain, Heart of Abyss from the world's deepest depths...");

        dialog.addLine("고대 대장장이",
                "용의 불꽃은 고대 용의 숨결에서, 신의 축복은 신전의 시험을 통해서만 얻을 수 있소.",
                "Dragon's Flame from ancient dragon's breath, Divine Blessing only through temple trials.");

        // 재료 수집 후
        dialog.addLine("고대 대장장이",
                "놀랍군! 모든 재료를 모았구려. 이제 제작을 시작할 수 있소.",
                "Amazing! You've gathered all materials. Now we can begin crafting.");

        dialog.addLine("고대 대장장이",
                "하지만 기억하시오. 이 무기는 단순한 도구가 아니라 당신의 영혼과 하나가 될 것이오.",
                "But remember. This weapon is not just a tool, it will become one with your soul.");

        // 제작 과정
        dialog.addLine("고대 대장장이",
                "신성한 재료들이 하나로 융합되고 있소... 고대의 마법이 깨어나는군!",
                "The divine materials are fusing as one... Ancient magic is awakening!");

        // 각성 전
        dialog.addLine("고대 대장장이",
                "무기는 완성되었지만 아직 잠들어 있소. 전설의 제단에서 각성시켜야 하오.",
                "The weapon is complete but still dormant. You must awaken it at the Legendary Altar.");

        dialog.addLine("고대 대장장이",
                "위더들이 당신을 시험할 것이오. 그들을 물리치면 무기가 진정한 힘을 발휘할 것이오.",
                "Withers will test you. Defeat them and the weapon will reveal its true power.");

        // 완성
        dialog.addLine("고대 대장장이",
                "해냈군요! 천 년 만에 다시 전설의 무기가 탄생했소!",
                "You did it! After a thousand years, a legendary weapon is born again!");

        dialog.addLine("고대 대장장이",
                "이 무기는 당신과 영원히 함께할 것이오. 현명하게 사용하시길...",
                "This weapon will be with you forever. Use it wisely...");

        return dialog;
    }
}