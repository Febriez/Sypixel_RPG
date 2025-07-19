package com.febrie.rpg.quest.impl.main.chapter1;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
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
 * 원소의 돌 - 메인 퀘스트 Chapter 1
 * 네 가지 원소의 힘을 모으는 대서사시
 *
 * @author Febrie
 */
public class ElementalStonesQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class ElementalStonesBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new ElementalStonesQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public ElementalStonesQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private ElementalStonesQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new ElementalStonesBuilder()
                .id(QuestID.MAIN_ELEMENTAL_STONES)
                .objectives(Arrays.asList(
                        // 시작
                        new InteractNPCObjective("meet_sage", "elemental_sage"), // 원소의 현자
                        
                        // 불의 돌 - 용암 지대
                        new VisitLocationObjective("fire_temple", "fire_elemental_temple"),
                        new KillMobObjective("fire_elementals", EntityType.BLAZE, 15),
                        new KillMobObjective("magma_cubes", EntityType.MAGMA_CUBE, 10),
                        new CollectItemObjective("fire_essence", Material.BLAZE_POWDER, 20),
                        new KillMobObjective("fire_guardian", EntityType.WITHER_SKELETON, 1),
                        new CollectItemObjective("fire_stone", Material.BLAZE_ROD, 1),
                        
                        // 물의 돌 - 해저 신전
                        new VisitLocationObjective("water_temple", "water_elemental_temple"),
                        new KillMobObjective("water_elementals", EntityType.GUARDIAN, 15),
                        new KillMobObjective("drowned", EntityType.DROWNED, 20),
                        new CollectItemObjective("water_essence", Material.PRISMARINE_SHARD, 20),
                        new KillMobObjective("water_guardian", EntityType.ELDER_GUARDIAN, 1),
                        new CollectItemObjective("water_stone", Material.HEART_OF_THE_SEA, 1),
                        
                        // 대지의 돌 - 깊은 동굴
                        new VisitLocationObjective("earth_temple", "earth_elemental_temple"),
                        new BreakBlockObjective("mine_ores", Material.IRON_ORE, 30),
                        new KillMobObjective("earth_elementals", EntityType.IRON_GOLEM, 10),
                        new CollectItemObjective("earth_essence", Material.EMERALD, 15),
                        new KillMobObjective("earth_guardian", EntityType.RAVAGER, 1),
                        new CollectItemObjective("earth_stone", Material.EMERALD_BLOCK, 1),
                        
                        // 바람의 돌 - 하늘 섬
                        new VisitLocationObjective("air_temple", "air_elemental_temple"),
                        new KillMobObjective("air_elementals", EntityType.PHANTOM, 20),
                        new KillMobObjective("vexes", EntityType.VEX, 15),
                        new CollectItemObjective("air_essence", Material.FEATHER, 30),
                        new KillMobObjective("air_guardian", EntityType.EVOKER, 1),
                        new CollectItemObjective("air_stone", Material.ELYTRA, 1),
                        
                        // 최종 - 원소의 융합
                        new CraftItemObjective("elemental_core", Material.BEACON, 1),
                        new DeliverItemObjective("return_sage", "현자 아카테", Material.BEACON, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 5000)
                        .addCurrency(CurrencyType.DIAMOND, 50)
                        .addItem(new ItemStack(Material.BEACON))
                        .addItem(new ItemStack(Material.NETHER_STAR, 4))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 3))
                        .addExperience(5000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .minLevel(25)
                .maxLevel(0)
                .addPrerequisite(QuestID.MAIN_FIRST_TRIAL);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "네 원소의 돌" : "The Four Elemental Stones";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "세계의 균형을 유지하는 네 원소의 돌이 흩어졌습니다.",
                    "불, 물, 대지, 바람의 사원을 방문하여 각 원소의 돌을 찾으세요.",
                    "",
                    "각 사원에는 강력한 수호자가 있습니다:",
                    "• 불의 사원 - 용암과 화염의 시험",
                    "• 물의 사원 - 심해의 비밀과 고대의 수호자",
                    "• 대지의 사원 - 땅의 힘과 불굴의 의지",
                    "• 바람의 사원 - 하늘의 자유와 무한한 가능성",
                    "",
                    "네 돌을 모두 모으면 놀라운 힘을 얻게 될 것입니다.",
                    "",
                    "목표:",
                    "• 원소의 현자와 만나기",
                    "• 불의 사원에서 불의 돌 획득",
                    "• 물의 사원에서 물의 돌 획득",
                    "• 대지의 사원에서 대지의 돌 획득",
                    "• 바람의 사원에서 바람의 돌 획득",
                    "• 원소의 핵심 제작",
                    "• 원소의 현자에게 전달",
                    "",
                    "보상:",
                    "• 골드 5000",
                    "• 다이아몬드 50개",
                    "• 신호기",
                    "• 네더의 별 4개",
                    "• 마법이 부여된 황금 사과 3개",
                    "• 경험치 5000"
            );
        } else {
            return Arrays.asList(
                    "The four elemental stones that maintain world balance have been scattered.",
                    "Visit the temples of Fire, Water, Earth, and Air to find each stone.",
                    "",
                    "Each temple has a powerful guardian:",
                    "• Fire Temple - Trial of lava and flames",
                    "• Water Temple - Deep sea secrets and ancient guardians",
                    "• Earth Temple - Power of the land and unyielding will",
                    "• Air Temple - Freedom of sky and infinite possibilities",
                    "",
                    "Gather all four stones to gain incredible power.",
                    "",
                    "Objectives:",
                    "• Meet the Elemental Sage",
                    "• Obtain Fire Stone from Fire Temple",
                    "• Obtain Water Stone from Water Temple",
                    "• Obtain Earth Stone from Earth Temple",
                    "• Obtain Air Stone from Air Temple",
                    "• Craft Elemental Core",
                    "• Deliver to Elemental Sage",
                    "",
                    "Rewards:",
                    "• 5000 Gold",
                    "• 50 Diamonds",
                    "• Beacon",
                    "• 4 Nether Stars",
                    "• 3 Enchanted Golden Apples",
                    "• 5000 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "meet_sage" -> isKorean ? "원소의 현자와 대화" : "Talk to the Elemental Sage";
            // 불의 돌
            case "fire_temple" -> isKorean ? "불의 사원 방문" : "Visit the Fire Temple";
            case "fire_elementals" -> isKorean ? "불의 정령 15마리 처치" : "Defeat 15 Fire Elementals";
            case "magma_cubes" -> isKorean ? "마그마 큐브 10마리 처치" : "Defeat 10 Magma Cubes";
            case "fire_essence" -> isKorean ? "불의 정수 20개 수집" : "Collect 20 Fire Essences";
            case "fire_guardian" -> isKorean ? "불의 수호자 처치" : "Defeat the Fire Guardian";
            case "fire_stone" -> isKorean ? "불의 돌 획득" : "Obtain the Fire Stone";
            // 물의 돌
            case "water_temple" -> isKorean ? "물의 사원 방문" : "Visit the Water Temple";
            case "water_elementals" -> isKorean ? "물의 정령 15마리 처치" : "Defeat 15 Water Elementals";
            case "drowned" -> isKorean ? "드라운드 20마리 처치" : "Defeat 20 Drowned";
            case "water_essence" -> isKorean ? "물의 정수 20개 수집" : "Collect 20 Water Essences";
            case "water_guardian" -> isKorean ? "물의 수호자 처치" : "Defeat the Water Guardian";
            case "water_stone" -> isKorean ? "물의 돌 획득" : "Obtain the Water Stone";
            // 대지의 돌
            case "earth_temple" -> isKorean ? "대지의 사원 방문" : "Visit the Earth Temple";
            case "mine_ores" -> isKorean ? "철광석 30개 채굴" : "Mine 30 Iron Ores";
            case "earth_elementals" -> isKorean ? "대지의 정령 10마리 처치" : "Defeat 10 Earth Elementals";
            case "earth_essence" -> isKorean ? "대지의 정수 15개 수집" : "Collect 15 Earth Essences";
            case "earth_guardian" -> isKorean ? "대지의 수호자 처치" : "Defeat the Earth Guardian";
            case "earth_stone" -> isKorean ? "대지의 돌 획득" : "Obtain the Earth Stone";
            // 바람의 돌
            case "air_temple" -> isKorean ? "바람의 사원 방문" : "Visit the Air Temple";
            case "air_elementals" -> isKorean ? "바람의 정령 20마리 처치" : "Defeat 20 Air Elementals";
            case "vexes" -> isKorean ? "벡스 15마리 처치" : "Defeat 15 Vexes";
            case "air_essence" -> isKorean ? "바람의 정수 30개 수집" : "Collect 30 Air Essences";
            case "air_guardian" -> isKorean ? "바람의 수호자 처치" : "Defeat the Air Guardian";
            case "air_stone" -> isKorean ? "바람의 돌 획득" : "Obtain the Air Stone";
            // 최종
            case "elemental_core" -> isKorean ? "원소의 핵심 제작" : "Craft the Elemental Core";
            case "return_sage" -> isKorean ? "원소의 현자에게 전달" : "Return to the Elemental Sage";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("elemental_stones_dialog");

        dialog.addLine("원소의 현자",
                "시련의 메달을 가지고 왔군요. 이제 더 큰 임무를 맡을 준비가 되었습니다.",
                "You bring the Trial Medal. You're ready for a greater task.");

        dialog.addLine("원소의 현자",
                "천 년 전, 네 개의 원소의 돌이 세계의 균형을 유지했습니다.",
                "A thousand years ago, four elemental stones maintained the world's balance.");

        dialog.addLine("원소의 현자",
                "하지만 대재앙으로 인해 돌들이 각 원소의 사원으로 흩어졌죠.",
                "But a great catastrophe scattered the stones to their elemental temples.");

        dialog.addLine("플레이어",
                "제가 그 돌들을 찾아야 하는군요.",
                "I need to find those stones.");

        dialog.addLine("원소의 현자",
                "맞습니다. 불, 물, 대지, 바람... 각 사원의 수호자를 물리치고 돌을 가져오세요.",
                "Correct. Fire, Water, Earth, Air... Defeat each temple's guardian and bring the stones.");

        dialog.addLine("원소의 현자",
                "네 돌을 모두 모으면, 그것들을 융합하여 원소의 핵심을 만들 수 있을 것입니다.",
                "Once you gather all four, you can fuse them to create the Elemental Core.");

        dialog.addLine("플레이어",
                "위험한 여정이 되겠지만, 해내겠습니다.",
                "It will be a dangerous journey, but I'll do it.");

        dialog.addLine("원소의 현자",
                "믿음직스럽군요. 원소의 축복이 함께하기를!",
                "I trust you. May the blessing of the elements be with you!");

        return dialog;
    }
}