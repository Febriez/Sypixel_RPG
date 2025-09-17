package com.febrie.rpg.quest.impl.main.chapter3;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 용의 계약 - 메인 스토리 퀘스트 (Chapter 3)
 * 고대 용과 영원한 계약을 맺는 퀘스트
 *
 * @author Febrie
 */
public class DragonPactQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public DragonPactQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_DRAGON_PACT)
                .objectives(List.of(
                        // 계약 준비
                        new InteractNPCObjective("ancient_dragon", "ancient_dragon"),
                        new CollectItemObjective("redstone_block_collect", Material.REDSTONE_BLOCK, 10),
                        new CollectItemObjective("end_crystal_collect", Material.END_CRYSTAL, 5),
                        new CollectItemObjective("blaze_powder_collect", Material.BLAZE_POWDER, 30),
                        
                        // 계약서 작성
                        new InteractNPCObjective("contract_scribe", "dragon_scribe"),
                        new CollectItemObjective("ink_sac_collect", Material.INK_SAC, 20),
                        new CollectItemObjective("paper_collect", Material.PAPER, 50),
                        new CollectItemObjective("experience_bottle_collect", Material.EXPERIENCE_BOTTLE, 30),
                        new DeliverItemObjective("written_book_deliver", Material.WRITTEN_BOOK, 1, "dragon_scribe"),
                        
                        // 영혼 결속 의식
                        new VisitLocationObjective("soul_altar", "dragon_soul_altar"),
                        new PlaceBlockObjective("place_soul_stones", Material.SOUL_SAND, 20),
                        new PlaceBlockObjective("place_soul_fire", Material.SOUL_LANTERN, 4),
                        new KillMobObjective("soul_guardians", EntityType.VEX, 30),
                        new CollectItemObjective("phantom_membrane_collect", Material.PHANTOM_MEMBRANE, 15),
                        
                        // 용의 축복 받기
                        new InteractNPCObjective("dragon_priest", "dragon_priest"),
                        new CollectItemObjective("gold_nugget_collect", Material.GOLD_NUGGET, 100),
                        new DeliverItemObjective("diamond_deliver", Material.DIAMOND, 64, "dragon_priest"),
                        new SurviveObjective("blessing_ritual", 300), // 5분 의식
                        
                        // 계약 서명
                        new InteractNPCObjective("ancient_dragon_signing", "ancient_dragon"),
                        new CollectItemObjective("nether_star_collect", Material.NETHER_STAR, 1),
                        new CollectItemObjective("redstone_collect", Material.REDSTONE, 10),
                        new DeliverItemObjective("written_book_deliver", Material.WRITTEN_BOOK, 1, "ancient_dragon"),
                        
                        // 계약 완성
                        new PlaceBlockObjective("place_contract_altar", Material.ENCHANTING_TABLE, 1),
                        new CollectItemObjective("chain_collect", Material.CHAIN, 30),
                        new KillMobObjective("contract_witnesses", EntityType.EVOKER, 5),
                        new CollectItemObjective("clay_ball_collect", Material.CLAY_BALL, 5),
                        
                        // 용과 하나되기
                        new VisitLocationObjective("dragon_heart_chamber", "dragon_heart_chamber"),
                        new CollectItemObjective("dragon_breath_collect", Material.DRAGON_BREATH, 10),
                        new SurviveObjective("soul_merge", 600), // 10분 영혼 융합
                        new InteractNPCObjective("merged_dragon", "ancient_dragon"),
                        
                        // 계약 효력 발동
                        new CollectItemObjective("netherite_ingot_collect", Material.NETHERITE_INGOT, 1),
                        new CollectItemObjective("dragon_egg_collect", Material.DRAGON_EGG, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 30000)
                        .addCurrency(CurrencyType.DIAMOND, 250)
                        .addItem(new ItemStack(Material.DRAGON_EGG))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 5))
                        .addItem(new ItemStack(Material.NETHERITE_HELMET))
                        .addItem(new ItemStack(Material.NETHERITE_BOOTS))
                        .addExperience(15000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_DRAGON_TRIALS)
                .minLevel(55)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "ancient_dragon" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_ANCIENT_DRAGON, who);
            case "redstone_block_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_REDSTONE_BLOCK_COLLECT, who);
            case "end_crystal_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_END_CRYSTAL_COLLECT, who);
            case "blaze_powder_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_BLAZE_POWDER_COLLECT, who);
            case "contract_scribe" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_CONTRACT_SCRIBE, who);
            case "ink_sac_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_INK_SAC_COLLECT, who);
            case "paper_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_PAPER_COLLECT, who);
            case "experience_bottle_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_EXPERIENCE_BOTTLE_COLLECT, who);
            case "written_book_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_WRITTEN_BOOK_DELIVER, who);
            case "soul_altar" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_SOUL_ALTAR, who);
            case "place_soul_stones" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_PLACE_SOUL_STONES, who);
            case "place_soul_fire" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_PLACE_SOUL_FIRE, who);
            case "soul_guardians" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_SOUL_GUARDIANS, who);
            case "phantom_membrane_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_PHANTOM_MEMBRANE_COLLECT, who);
            case "dragon_priest" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_DRAGON_PRIEST, who);
            case "gold_nugget_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_GOLD_NUGGET_COLLECT, who);
            case "diamond_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_DIAMOND_DELIVER, who);
            case "blessing_ritual" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_BLESSING_RITUAL, who);
            case "ancient_dragon_signing" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_ANCIENT_DRAGON_SIGNING, who);
            case "nether_star_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_NETHER_STAR_COLLECT, who);
            case "redstone_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_REDSTONE_COLLECT, who);
            case "place_contract_altar" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_PLACE_CONTRACT_ALTAR, who);
            case "chain_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_CHAIN_COLLECT, who);
            case "contract_witnesses" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_CONTRACT_WITNESSES, who);
            case "clay_ball_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_CLAY_BALL_COLLECT, who);
            case "dragon_heart_chamber" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_DRAGON_HEART_CHAMBER, who);
            case "dragon_breath_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_DRAGON_BREATH_COLLECT, who);
            case "soul_merge" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_SOUL_MERGE, who);
            case "merged_dragon" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_MERGED_DRAGON, who);
            case "netherite_ingot_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_NETHERITE_INGOT_COLLECT, who);
            case "dragon_egg_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_OBJECTIVES_DRAGON_EGG_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_PACT_DECLINE, who);
    }
}