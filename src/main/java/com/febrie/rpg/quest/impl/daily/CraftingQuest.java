package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.ArrayList;

/**
 * 일일 제작 - 일일 퀘스트
 * 매일 아이템을 제작하고 제공하는 퀘스트
 *
 * @author Febrie
 */
public class CraftingQuest extends Quest {

    /**
     * 기본 생성자
     */
    public CraftingQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        List<QuestObjective> objectives = new ArrayList<>();
        
        // 제작 목표
        objectives.add(new InteractNPCObjective("craftsman", "master_craftsman"));
        objectives.add(new CraftItemObjective("iron_pickaxe_craft", Material.IRON_PICKAXE, 3));
        objectives.add(new CraftItemObjective("iron_chestplate_craft", Material.IRON_CHESTPLATE, 2));
        objectives.add(new DeliverItemObjective("iron_pickaxe_deliver", Material.IRON_PICKAXE, 2, "master_craftsman"));

        return new QuestBuilder()
                .id(QuestID.DAILY_CRAFTING)
                .objectives(objectives)
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 160)
                        .addCurrency(CurrencyType.EMERALD, 4)
                        .addItem(new ItemStack(Material.CRAFTING_TABLE))
                        .addItem(new ItemStack(Material.IRON_INGOT, 12))
                        .addExperience(80)
                        .build())
                .sequential(false)
                .category(QuestCategory.DAILY)
                .minLevel(4)
                .repeatable(true)
                .daily(true)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_CRAFTING_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "craftsman" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_CRAFTSMAN, who);
            case "iron_pickaxe_craft" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_IRON_PICKAXE_CRAFT, who);
            case "iron_chestplate_craft" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_IRON_CHESTPLATE_CRAFT, who);
            case "iron_pickaxe_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_IRON_PICKAXE_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_CRAFTING_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_DECLINE, who);
    }
}