package com.febrie.rpg.quest.impl.life;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.PayCurrencyObjective;
import com.febrie.rpg.quest.objective.impl.DeliverItemObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import java.util.ArrayList;
import java.util.List;

/**
 * 상인 거물 - 생활 퀘스트
 * 거대한 상업 제국을 건설하는 퀘스트
 *
 * @author Febrie
 */
public class MerchantTycoonQuest extends Quest {

    /**
     * 기본 생성자
     */
    public MerchantTycoonQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        List<QuestObjective> objectives = new ArrayList<>();
        
        // 상업 관련 목표들
        objectives.add(new CollectItemObjective("emerald_collect", Material.EMERALD, 100)); // 에메랄드 100개 수집
        objectives.add(new CollectItemObjective("diamond_collect", Material.DIAMOND, 32)); // 다이아몬드 32개 수집
        objectives.add(new CollectItemObjective("gold_ingot_collect", Material.GOLD_INGOT, 64)); // 금 주괴 64개 수집
        objectives.add(new PayCurrencyObjective("invest_gold", CurrencyType.GOLD, 5000)); // 골드 5000개 투자
        objectives.add(new DeliverItemObjective("diamond_deliver", Material.DIAMOND, 16, "luxury_merchant")); // 고급 상품 배달
        objectives.add(new DeliverItemObjective("bread_deliver", Material.BREAD, 128, "food_merchant")); // 식료품 배달
        objectives.add(new DeliverItemObjective("oak_planks_deliver", Material.OAK_PLANKS, 256, "construction_merchant")); // 건축 자재 배달
        objectives.add(new InteractNPCObjective("establish_trade_routes", "caravan_leader")); // 무역 루트 확립
        objectives.add(new CollectItemObjective("nether_star_collect", Material.NETHER_STAR, 3)); // 희귀 아이템 수집
        objectives.add(new PayCurrencyObjective("expand_business", CurrencyType.EMERALD, 200)); // 사업 확장 투자

        return new QuestBuilder()
                .id(QuestID.LIFE_MERCHANT_TYCOON)
                .objectives(objectives)
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 10000)
                        .addCurrency(CurrencyType.EMERALD, 500)
                        .addItem(new ItemStack(Material.DIAMOND_BLOCK, 5)) // 다이아몬드 블록 5개
                        .addItem(new ItemStack(Material.EMERALD_BLOCK, 10)) // 에메랄드 블록 10개
                        .addItem(new ItemStack(Material.GOLD_BLOCK, 15)) // 금 블록 15개
                        .addItem(new ItemStack(Material.CHEST, 32)) // 상자 32개 (창고용)
                        .addItem(new ItemStack(Material.ENDER_CHEST, 5)) // 엔더 상자 5개
                        .addExperience(1000)
                        .build())
                .sequential(false) // 순서 상관없이 진행 가능
                .category(QuestCategory.LIFE)
                .minLevel(30)
                .repeatable(false)
                .addPrerequisite(QuestID.LIFE_MASTER_CHEF); // 마스터 셰프 선행
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_LIFE_MERCHANT_TYCOON_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_LIFE_MERCHANT_TYCOON_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "emerald_collect" -> LangManager.text(QuestCommonLangKey.QUEST_LIFE_MERCHANT_TYCOON_OBJECTIVES_EMERALD_COLLECT, who);
            case "diamond_collect" -> LangManager.text(QuestCommonLangKey.QUEST_LIFE_MERCHANT_TYCOON_OBJECTIVES_DIAMOND_COLLECT, who);
            case "gold_ingot_collect" -> LangManager.text(QuestCommonLangKey.QUEST_LIFE_MERCHANT_TYCOON_OBJECTIVES_GOLD_INGOT_COLLECT, who);
            case "invest_gold" -> LangManager.text(QuestCommonLangKey.QUEST_LIFE_MERCHANT_TYCOON_OBJECTIVES_INVEST_GOLD, who);
            case "diamond_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_LIFE_MERCHANT_TYCOON_OBJECTIVES_DIAMOND_DELIVER, who);
            case "bread_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_LIFE_MERCHANT_TYCOON_OBJECTIVES_BREAD_DELIVER, who);
            case "oak_planks_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_LIFE_MERCHANT_TYCOON_OBJECTIVES_OAK_PLANKS_DELIVER, who);
            case "establish_trade_routes" -> LangManager.text(QuestCommonLangKey.QUEST_LIFE_MERCHANT_TYCOON_OBJECTIVES_ESTABLISH_TRADE_ROUTES, who);
            case "nether_star_collect" -> LangManager.text(QuestCommonLangKey.QUEST_LIFE_MERCHANT_TYCOON_OBJECTIVES_NETHER_STAR_COLLECT, who);
            case "expand_business" -> LangManager.text(QuestCommonLangKey.QUEST_LIFE_MERCHANT_TYCOON_OBJECTIVES_EXPAND_BUSINESS, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_LIFE_MERCHANT_TYCOON_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_LIFE_MERCHANT_TYCOON_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_LIFE_MERCHANT_TYCOON_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_LIFE_MERCHANT_TYCOON_DECLINE, who);
    }
}