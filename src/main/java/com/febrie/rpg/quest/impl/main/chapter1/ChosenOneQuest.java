package com.febrie.rpg.quest.impl.main.chapter1;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 선택받은 자 - 메인 퀘스트 Chapter 1
 * 영웅으로서의 자질을 시험받는 퀘스트
 *
 * @author Febrie
 */
public class ChosenOneQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public ChosenOneQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder().id(QuestID.MAIN_CHOSEN_ONE)
                .objectives(List.of(
                        // 시련의 동굴 입장
                        new VisitLocationObjective("enter_trial_cave", "trial_cave_entrance"),

                        // 첫 번째 시련: 용기
                        new KillMobObjective("trial_courage", EntityType.IRON_GOLEM, 3), new CollectItemObjective("iron_block_collect", Material.IRON_BLOCK, 1),

                        // 두 번째 시련: 지혜
                        new BreakBlockObjective("solve_puzzle", Material.REDSTONE_LAMP, 5), new CollectItemObjective("emerald_collect", Material.EMERALD, 1),

                        // 세 번째 시련: 희생
                        new PayCurrencyObjective("sacrifice_gold", CurrencyType.GOLD, 1000), new CollectItemObjective("diamond_collect", Material.DIAMOND, 1),

                        // 최종 시련
                        new KillMobObjective("final_guardian", EntityType.WITHER_SKELETON, 1), new CollectItemObjective("nether_star_collect", Material.NETHER_STAR, 1),

                        // 완료
                        new DeliverItemObjective("nether_star_deliver", Material.NETHER_STAR, 1, "고대의 장로")))
                .reward(new BasicReward.Builder().addCurrency(CurrencyType.GOLD, 2000)
                        .addCurrency(CurrencyType.DIAMOND, 20)
                        .addItem(new ItemStack(Material.NETHERITE_SWORD))
                        .addItem(new ItemStack(Material.ELYTRA))
                        .addExperience(2000)
                        .build())
                .sequential(true)  // 순차적으로 진행
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .minLevel(15)
                .maxLevel(0)
                .addPrerequisite(QuestID.MAIN_ANCIENT_PROPHECY);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHOSEN_ONE_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_CHOSEN_ONE_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "enter_trial_cave" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHOSEN_ONE_OBJECTIVES_ENTER_TRIAL_CAVE, who);
            case "trial_courage" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHOSEN_ONE_OBJECTIVES_TRIAL_COURAGE, who);
            case "iron_block_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHOSEN_ONE_OBJECTIVES_IRON_BLOCK_COLLECT, who);
            case "solve_puzzle" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHOSEN_ONE_OBJECTIVES_SOLVE_PUZZLE, who);
            case "emerald_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHOSEN_ONE_OBJECTIVES_EMERALD_COLLECT, who);
            case "sacrifice_gold" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHOSEN_ONE_OBJECTIVES_SACRIFICE_GOLD, who);
            case "diamond_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHOSEN_ONE_OBJECTIVES_DIAMOND_COLLECT, who);
            case "final_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHOSEN_ONE_OBJECTIVES_FINAL_GUARDIAN, who);
            case "nether_star_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHOSEN_ONE_OBJECTIVES_NETHER_STAR_COLLECT, who);
            case "nether_star_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHOSEN_ONE_OBJECTIVES_NETHER_STAR_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_CHOSEN_ONE_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHOSEN_ONE_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHOSEN_ONE_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_CHOSEN_ONE_DECLINE, who);
    }
}