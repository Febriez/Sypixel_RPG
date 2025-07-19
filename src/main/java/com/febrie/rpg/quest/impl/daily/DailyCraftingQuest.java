package com.febrie.rpg.quest.impl.daily;

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
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 일일 제작 - 일일 퀘스트
 * 매일 리셋되는 아이템 제작 퀘스트
 *
 * @author Febrie
 */
public class DailyCraftingQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class DailyCraftingBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new DailyCraftingQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public DailyCraftingQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private DailyCraftingQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new DailyCraftingBuilder()
                .id(QuestID.DAILY_CRAFTING)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("blacksmith", "daily_blacksmith"), // 대장장이
                        // 기본 도구 제작
                        new CraftItemObjective("craft_wood_tools", Material.WOODEN_PICKAXE, 3),
                        new CraftItemObjective("craft_stone_tools", Material.STONE_SWORD, 5),
                        new CraftItemObjective("craft_iron_tools", Material.IRON_AXE, 2),
                        // 방어구 제작
                        new CraftItemObjective("craft_leather_armor", Material.LEATHER_CHESTPLATE, 3),
                        new CraftItemObjective("craft_chainmail", Material.CHAINMAIL_HELMET, 1),
                        // 유용한 아이템 제작
                        new CraftItemObjective("craft_furnace", Material.FURNACE, 5),
                        new CraftItemObjective("craft_chest", Material.CHEST, 10),
                        new CraftItemObjective("craft_torches", Material.TORCH, 64),
                        new CraftItemObjective("craft_ladder", Material.LADDER, 20),
                        // 음식 제작
                        new CraftItemObjective("craft_bread", Material.BREAD, 20),
                        new CraftItemObjective("craft_cookies", Material.COOKIE, 32),
                        // 전달
                        new DeliverItemObjective("deliver_tools", "blacksmith", Material.IRON_AXE, 2),
                        new DeliverItemObjective("deliver_armor", "blacksmith", Material.LEATHER_CHESTPLATE, 3),
                        new InteractNPCObjective("report_complete", "daily_blacksmith")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 600)
                        .addCurrency(CurrencyType.DIAMOND, 8)
                        .addItem(new ItemStack(Material.CRAFTING_TABLE, 3))
                        .addItem(new ItemStack(Material.ANVIL))
                        .addItem(new ItemStack(Material.SMITHING_TABLE))
                        .addItem(new ItemStack(Material.IRON_INGOT, 32))
                        .addExperience(400)
                        .build())
                .sequential(false)
                .daily(true)  // 일일 퀘스트 설정
                .repeatable(true)
                .category(QuestCategory.DAILY)
                .minLevel(10)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull String getDisplayName(boolean isKorean) {
        return isKorean ? "일일 장인의 제작" : "Daily Craftsman's Work";
    }

    @Override
    public @NotNull List<String> getDisplayInfo(boolean isKorean) {
        if (isKorean) {
            return Arrays.asList(
                    "오늘의 제작 주문을 완료하세요!",
                    "대장장이가 다양한 아이템 제작을 의뢰했습니다.",
                    "매일 자정에 리셋됩니다.",
                    "",
                    "제작 목표:",
                    "• 나무 공구 3개",
                    "• 돌 검 5개",
                    "• 철 도끼 2개",
                    "• 가죽 흉갑 3개",
                    "• 사슬 투구 1개",
                    "• 화로 5개",
                    "• 상자 10개",
                    "• 효불 64개",
                    "• 사다리 20개",
                    "• 빵 20개",
                    "• 쿠키 32개",
                    "",
                    "팁:",
                    "• 제작대를 여러 개 준비하세요",
                    "• 재료를 미리 준비하세요",
                    "• 화로를 사용해 광물을 제련하세요",
                    "",
                    "보상:",
                    "• 골드 600",
                    "• 다이아몬드 8개",
                    "• 제작대 3개",
                    "• 모루",
                    "• 대장간",
                    "• 철 주괴 32개",
                    "• 경험치 400"
            );
        } else {
            return Arrays.asList(
                    "Complete today's crafting orders!",
                    "The blacksmith has requested various item crafts.",
                    "Resets daily at midnight.",
                    "",
                    "Crafting Objectives:",
                    "• 3 Wooden Tools",
                    "• 5 Stone Swords",
                    "• 2 Iron Axes",
                    "• 3 Leather Chestplates",
                    "• 1 Chainmail Helmet",
                    "• 5 Furnaces",
                    "• 10 Chests",
                    "• 64 Torches",
                    "• 20 Ladders",
                    "• 20 Bread",
                    "• 32 Cookies",
                    "",
                    "Tips:",
                    "• Prepare multiple crafting tables",
                    "• Gather materials in advance",
                    "• Use furnaces to smelt ores",
                    "",
                    "Rewards:",
                    "• 600 Gold",
                    "• 8 Diamonds",
                    "• 3 Crafting Tables",
                    "• Anvil",
                    "• Smithing Table",
                    "• 32 Iron Ingots",
                    "• 400 Experience"
            );
        }
    }

    @Override
    public @NotNull String getObjectiveDescription(@NotNull QuestObjective objective, boolean isKorean) {
        String id = objective.getId();

        return switch (id) {
            case "blacksmith" -> isKorean ? "대장장이와 대화" : "Talk to Blacksmith";
            case "craft_wood_tools" -> isKorean ? "나무 공구 3개 제작" : "Craft 3 wooden tools";
            case "craft_stone_tools" -> isKorean ? "돌 검 5개 제작" : "Craft 5 stone swords";
            case "craft_iron_tools" -> isKorean ? "철 도끼 2개 제작" : "Craft 2 iron axes";
            case "craft_leather_armor" -> isKorean ? "가죽 흉갑 3개 제작" : "Craft 3 leather chestplates";
            case "craft_chainmail" -> isKorean ? "사슬 투구 1개 제작" : "Craft 1 chainmail helmet";
            case "craft_furnace" -> isKorean ? "화로 5개 제작" : "Craft 5 furnaces";
            case "craft_chest" -> isKorean ? "상자 10개 제작" : "Craft 10 chests";
            case "craft_torches" -> isKorean ? "효불 64개 제작" : "Craft 64 torches";
            case "craft_ladder" -> isKorean ? "사다리 20개 제작" : "Craft 20 ladders";
            case "craft_bread" -> isKorean ? "빵 20개 제작" : "Craft 20 bread";
            case "craft_cookies" -> isKorean ? "쿠키 32개 제작" : "Craft 32 cookies";
            case "deliver_tools" -> isKorean ? "도구 전달" : "Deliver tools";
            case "deliver_armor" -> isKorean ? "방어구 전달" : "Deliver armor";
            case "report_complete" -> isKorean ? "제작 완료 보고" : "Report crafting complete";
            default -> objective.getStatusInfo(null);
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("daily_crafting_dialog");

        // 시작
        dialog.addLine("대장장이",
                "오늘도 주문이 밀려있네. 도와주겠나?",
                "Orders are piling up again today. Can you help?");

        dialog.addLine("플레이어",
                "물론이죠! 무엇을 만들면 되나요?",
                "Of course! What should I make?");

        dialog.addLine("대장장이",
                "도구, 방어구, 그리고 일상 용품들이 필요해. 자세한 목록을 줄게.",
                "I need tools, armor, and daily necessities. I'll give you a detailed list.");

        // 진행 중
        dialog.addLine("대장장이",
                "제작은 잘 되고 있나? 재료가 부족하면 말해주게.",
                "How's the crafting going? Let me know if you need materials.");

        dialog.addLine("플레이어",
                "사슬 갑옷은 어떻게 만드나요?",
                "How do I craft chainmail armor?");

        dialog.addLine("대장장이",
                "철괴를 특수한 패턴으로 배치해야 해. 어려운 작업이지.",
                "You need to arrange iron nuggets in a special pattern. It's difficult work.");

        // 완료
        dialog.addLine("대장장이",
                "대단해! 모든 주문을 완벽히 처리했군. 진정한 장인이야!",
                "Amazing! You've completed all orders perfectly. You're a true craftsman!");

        dialog.addLine("대장장이",
                "이 도구들을 가져가게. 내일도 도와주면 고맙겠네!",
                "Take these tools. I'd appreciate your help again tomorrow!");

        return dialog;
    }
}