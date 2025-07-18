package com.febrie.rpg.quest.impl.crafting;

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
 * 대장장이 마스터 - 제작 퀘스트
 * 대장장이의 길을 걷는 장인이 되는 퀘스트
 *
 * @author Febrie
 */
public class MasterBlacksmithQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class MasterBlacksmithBuilder extends Quest.Builder {
        @Override
        public Quest build() {
            return new MasterBlacksmithQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public MasterBlacksmithQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private MasterBlacksmithQuest(@NotNull Builder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static Builder createBuilder() {
        return new MasterBlacksmithBuilder()
                .id(QuestID.CRAFT_MASTER_BLACKSMITH)
                .objectives(Arrays.asList(
                        // 시작
                        new InteractNPCObjective("blacksmith_master", 31), // 대장장이 마스터
                        
                        // 재료 수집
                        new BreakBlockObjective("mine_iron", Material.IRON_ORE, 30),
                        new BreakBlockObjective("mine_gold", Material.GOLD_ORE, 20),
                        new BreakBlockObjective("mine_diamond", Material.DIAMOND_ORE, 10),
                        new CollectItemObjective("gather_coal", Material.COAL, 64),
                        
                        // 제련
                        new CollectItemObjective("smelt_iron", Material.IRON_INGOT, 30),
                        new CollectItemObjective("smelt_gold", Material.GOLD_INGOT, 20),
                        new CollectItemObjective("gather_diamonds", Material.DIAMOND, 10),
                        
                        // 기초 제작
                        new PlaceBlockObjective("setup_anvil", Material.ANVIL, 1),
                        new PlaceBlockObjective("setup_furnace", Material.BLAST_FURNACE, 1),
                        new CraftItemObjective("craft_iron_tools", Material.IRON_PICKAXE, 5),
                        new CraftItemObjective("craft_iron_armor", Material.IRON_CHESTPLATE, 3),
                        
                        // 중급 제작
                        new CraftItemObjective("craft_diamond_sword", Material.DIAMOND_SWORD, 2),
                        new CraftItemObjective("craft_diamond_armor", Material.DIAMOND_CHESTPLATE, 1),
                        
                        // 고급 제작 - 인챈트
                        new PlaceBlockObjective("setup_enchanting", Material.ENCHANTING_TABLE, 1),
                        new CollectItemObjective("enchanted_sword", Material.DIAMOND_SWORD, 1), // 인챈트된 검
                        
                        // 최종 작품
                        new CollectItemObjective("netherite_scrap", Material.NETHERITE_SCRAP, 4),
                        new CraftItemObjective("craft_netherite", Material.NETHERITE_INGOT, 1),
                        new CraftItemObjective("masterpiece", Material.NETHERITE_SWORD, 1),
                        
                        // 전달
                        new DeliverItemObjective("deliver_masterpiece", "blacksmith_master", Material.NETHERITE_SWORD, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 4000)
                        .addCurrency(CurrencyType.DIAMOND, 35)
                        .addItem(new ItemStack(Material.NETHERITE_INGOT, 2))
                        .addItem(new ItemStack(Material.SMITHING_TABLE))
                        .addItem(new ItemStack(Material.ANVIL))
                        .addExperience(2500)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(Quest.QuestCategory.CRAFTING)
                .minLevel(20)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "대장장이 마스터의 길" : "Path of the Master Blacksmith";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "대장장이 마스터가 되기 위한 수련을 시작하세요.",
                    "광물 채굴부터 네더라이트 장비 제작까지 모든 과정을 마스터하세요.",
                    "",
                    "🔨 수련 과정:",
                    "• 1단계: 광물 채굴과 제련",
                    "• 2단계: 기초 장비 제작",
                    "• 3단계: 고급 장비 제작",
                    "• 4단계: 인챈트 마스터",
                    "• 5단계: 네더라이트 걸작품",
                    "",
                    "목표:",
                    "• 대장장이 마스터와 대화",
                    "• 각종 광물 채굴 및 제련",
                    "• 대장간 시설 구축",
                    "• 철 장비 제작",
                    "• 다이아몬드 장비 제작",
                    "• 인챈트 테이블 설치",
                    "• 네더라이트 검 제작",
                    "• 걸작품 납품",
                    "",
                    "보상:",
                    "• 골드 4000",
                    "• 다이아몬드 35개",
                    "• 네더라이트 주괴 2개",
                    "• 대장장이 작업대",
                    "• 모루",
                    "• 경험치 2500"
            );
        } else {
            return Arrays.asList(
                    "Begin your training to become a Master Blacksmith.",
                    "Master everything from ore mining to Netherite equipment crafting.",
                    "",
                    "🔨 Training Process:",
                    "• Stage 1: Ore Mining and Smelting",
                    "• Stage 2: Basic Equipment Crafting",
                    "• Stage 3: Advanced Equipment Crafting",
                    "• Stage 4: Enchantment Mastery",
                    "• Stage 5: Netherite Masterpiece",
                    "",
                    "Objectives:",
                    "• Talk to the Master Blacksmith",
                    "• Mine and smelt various ores",
                    "• Build forge facilities",
                    "• Craft iron equipment",
                    "• Craft diamond equipment",
                    "• Set up enchanting table",
                    "• Craft Netherite sword",
                    "• Deliver masterpiece",
                    "",
                    "Rewards:",
                    "• 4000 Gold",
                    "• 35 Diamonds",
                    "• 2 Netherite Ingots",
                    "• Smithing Table",
                    "• Anvil",
                    "• 2500 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "blacksmith_master" -> isKorean ? "대장장이 마스터와 대화" : "Talk to the Master Blacksmith";
            case "mine_iron" -> isKorean ? "철광석 30개 채굴" : "Mine 30 Iron Ore";
            case "mine_gold" -> isKorean ? "금광석 20개 채굴" : "Mine 20 Gold Ore";
            case "mine_diamond" -> isKorean ? "다이아몬드 광석 10개 채굴" : "Mine 10 Diamond Ore";
            case "gather_coal" -> isKorean ? "석탄 64개 수집" : "Gather 64 Coal";
            case "smelt_iron" -> isKorean ? "철 주괴 30개 제련" : "Smelt 30 Iron Ingots";
            case "smelt_gold" -> isKorean ? "금 주괴 20개 제련" : "Smelt 20 Gold Ingots";
            case "gather_diamonds" -> isKorean ? "다이아몬드 10개 수집" : "Gather 10 Diamonds";
            case "setup_anvil" -> isKorean ? "모루 설치" : "Set up an Anvil";
            case "setup_furnace" -> isKorean ? "용광로 설치" : "Set up a Blast Furnace";
            case "craft_iron_tools" -> isKorean ? "철 곡괭이 5개 제작" : "Craft 5 Iron Pickaxes";
            case "craft_iron_armor" -> isKorean ? "철 흉갑 3개 제작" : "Craft 3 Iron Chestplates";
            case "craft_diamond_sword" -> isKorean ? "다이아몬드 검 2개 제작" : "Craft 2 Diamond Swords";
            case "craft_diamond_armor" -> isKorean ? "다이아몬드 흉갑 제작" : "Craft Diamond Chestplate";
            case "setup_enchanting" -> isKorean ? "마법 부여대 설치" : "Set up Enchanting Table";
            case "enchanted_sword" -> isKorean ? "인챈트된 다이아몬드 검 획득" : "Obtain Enchanted Diamond Sword";
            case "netherite_scrap" -> isKorean ? "네더라이트 파편 4개 수집" : "Gather 4 Netherite Scrap";
            case "craft_netherite" -> isKorean ? "네더라이트 주괴 제작" : "Craft Netherite Ingot";
            case "masterpiece" -> isKorean ? "네더라이트 검 제작" : "Craft Netherite Sword";
            case "deliver_masterpiece" -> isKorean ? "걸작품을 마스터에게 납품" : "Deliver masterpiece to Master";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("master_blacksmith_dialog");

        dialog.addLine("대장장이 마스터",
                "오, 젊은이! 대장장이의 길을 걷고 싶은가?",
                "Oh, young one! Do you wish to walk the path of the blacksmith?");

        dialog.addLine("대장장이 마스터",
                "이 길은 쉽지 않네. 뜨거운 열기와 무거운 망치, 그리고 끊임없는 노력이 필요하지.",
                "This path is not easy. It requires hot flames, heavy hammers, and endless effort.");

        dialog.addLine("플레이어",
                "준비되었습니다. 무엇부터 시작해야 하나요?",
                "I'm ready. Where should I start?");

        dialog.addLine("대장장이 마스터",
                "먼저 광물을 직접 채굴하고 제련하는 법부터 배워야 해. 좋은 재료가 좋은 작품을 만들지.",
                "First, you must learn to mine and smelt ores yourself. Good materials make good products.");

        dialog.addLine("대장장이 마스터",
                "그 다음엔 기초적인 도구와 갑옷을 만들고, 점차 고급 기술을 익혀나가게 될 거야.",
                "Then you'll make basic tools and armor, gradually learning advanced techniques.");

        dialog.addLine("플레이어",
                "최종 목표는 무엇인가요?",
                "What's the final goal?");

        dialog.addLine("대장장이 마스터",
                "네더라이트 검이야. 그것을 만들 수 있다면, 자네도 진정한 마스터라 할 수 있지!",
                "A Netherite sword. If you can craft that, you can truly call yourself a master!");

        return dialog;
    }
}