package com.febrie.rpg.quest.impl.life;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.HarvestObjective;
import com.febrie.rpg.quest.objective.impl.PlaceBlockObjective;
import com.febrie.rpg.quest.objective.impl.CraftItemObjective;
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
 * 농업 전문가 - 생활 퀘스트
 * 농업 기술의 모든 면을 마스터하는 퀘스트
 *
 * @author Febrie
 */
public class FarmingExpertQuest extends Quest {

    /**
     * 기본 생성자
     */
    public FarmingExpertQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        List<QuestObjective> objectives = new ArrayList<>();
        
        // 농업 관련 목표들
        objectives.add(new HarvestObjective("harvest_wheat", Material.WHEAT, 100)); // 밀 100개 수확
        objectives.add(new HarvestObjective("harvest_carrots", Material.CARROT, 50)); // 당근 50개 수확
        objectives.add(new HarvestObjective("harvest_potatoes", Material.POTATO, 50)); // 감자 50개 수확
        objectives.add(new CollectItemObjective("wheat_seeds_collect", Material.WHEAT_SEEDS, 200)); // 밀 씨앗 200개 수집
        objectives.add(new PlaceBlockObjective("plant_farmland", Material.FARMLAND, 50)); // 경작지 50개 조성
        objectives.add(new CraftItemObjective("bread_craft", Material.BREAD, 64)); // 빵 64개 제작
        objectives.add(new CollectItemObjective("bone_meal_collect", Material.BONE_MEAL, 32)); // 뼛가루 32개 수집

        return new QuestBuilder()
                .id(QuestID.LIFE_FARMING_EXPERT)
                .objectives(objectives)
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 1000)
                        .addCurrency(CurrencyType.EMERALD, 50)
                        .addItem(new ItemStack(Material.DIAMOND_HOE)) // 다이아몬드 괭이
                        .addItem(new ItemStack(Material.WHEAT_SEEDS, 128))
                        .addItem(new ItemStack(Material.CARROT, 64))
                        .addItem(new ItemStack(Material.POTATO, 64))
                        .addItem(new ItemStack(Material.BONE_MEAL, 64))
                        .addExperience(500)
                        .build())
                .sequential(false) // 순서 상관없이 진행 가능
                .category(QuestCategory.LIFE)
                .minLevel(15)
                .repeatable(false)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_LIFE_FARMING_EXPERT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_LIFE_FARMING_EXPERT_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "harvest_wheat" -> LangManager.text(QuestCommonLangKey.QUEST_LIFE_FARMING_EXPERT_OBJECTIVES_HARVEST_WHEAT, who);
            case "harvest_carrots" -> LangManager.text(QuestCommonLangKey.QUEST_LIFE_FARMING_EXPERT_OBJECTIVES_HARVEST_CARROTS, who);
            case "harvest_potatoes" -> LangManager.text(QuestCommonLangKey.QUEST_LIFE_FARMING_EXPERT_OBJECTIVES_HARVEST_POTATOES, who);
            case "wheat_seeds_collect" -> LangManager.text(QuestCommonLangKey.QUEST_LIFE_FARMING_EXPERT_OBJECTIVES_WHEAT_SEEDS_COLLECT, who);
            case "plant_farmland" -> LangManager.text(QuestCommonLangKey.QUEST_LIFE_FARMING_EXPERT_OBJECTIVES_PLANT_FARMLAND, who);
            case "bread_craft" -> LangManager.text(QuestCommonLangKey.QUEST_LIFE_FARMING_EXPERT_OBJECTIVES_BREAD_CRAFT, who);
            case "bone_meal_collect" -> LangManager.text(QuestCommonLangKey.QUEST_LIFE_FARMING_EXPERT_OBJECTIVES_BONE_MEAL_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_LIFE_FARMING_EXPERT_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_LIFE_FARMING_EXPERT_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_LIFE_FARMING_EXPERT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_LIFE_FARMING_EXPERT_DECLINE, who);
    }
}