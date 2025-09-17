package com.febrie.rpg.quest.impl.daily;

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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.ArrayList;

/**
 * 일일 제작 - 일일 퀘스트
 * 매일 리셋되는 아이템 제작 퀘스트
 *
 * @author Febrie
 */
public class DailyCraftingQuest extends Quest {

    /**
     * 기본 생성자
     */
    public DailyCraftingQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.DAILY_CRAFTING)
                .objectives(List.of(
                        new InteractNPCObjective("blacksmith", "daily_blacksmith"), // 대장장이
                        // 기본 도구 제작
                        new CraftItemObjective("wooden_pickaxe_craft", Material.WOODEN_PICKAXE, 3),
                        new CraftItemObjective("stone_sword_craft", Material.STONE_SWORD, 5),
                        new CraftItemObjective("iron_axe_craft", Material.IRON_AXE, 2),
                        // 방어구 제작
                        new CraftItemObjective("leather_chestplate_craft", Material.LEATHER_CHESTPLATE, 3),
                        new CraftItemObjective("chainmail_helmet_craft", Material.CHAINMAIL_HELMET, 1),
                        // 유용한 아이템 제작
                        new CraftItemObjective("furnace_craft", Material.FURNACE, 5),
                        new CraftItemObjective("chest_craft", Material.CHEST, 10),
                        new CraftItemObjective("torch_craft", Material.TORCH, 64),
                        new CraftItemObjective("ladder_craft", Material.LADDER, 20),
                        // 음식 제작
                        new CraftItemObjective("bread_craft", Material.BREAD, 20),
                        new CraftItemObjective("cookie_craft", Material.COOKIE, 32),
                        // 전달
                        new DeliverItemObjective("iron_axe_deliver", Material.IRON_AXE, 2, "blacksmith"),
                        new DeliverItemObjective("leather_chestplate_deliver", Material.LEATHER_CHESTPLATE, 3, "blacksmith"),
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
            case "blacksmith" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_BLACKSMITH, who);
            case "wooden_pickaxe_craft" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_WOODEN_PICKAXE_CRAFT, who);
            case "stone_sword_craft" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_STONE_SWORD_CRAFT, who);
            case "iron_axe_craft" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_IRON_AXE_CRAFT, who);
            case "leather_chestplate_craft" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_LEATHER_CHESTPLATE_CRAFT, who);
            case "chainmail_helmet_craft" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_CHAINMAIL_HELMET_CRAFT, who);
            case "furnace_craft" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_FURNACE_CRAFT, who);
            case "chest_craft" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_CHEST_CRAFT, who);
            case "torch_craft" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_TORCH_CRAFT, who);
            case "ladder_craft" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_LADDER_CRAFT, who);
            case "bread_craft" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_BREAD_CRAFT, who);
            case "cookie_craft" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_COOKIE_CRAFT, who);
            case "iron_axe_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_IRON_AXE_DELIVER, who);
            case "leather_chestplate_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_LEATHER_CHESTPLATE_DELIVER, who);
            case "report_complete" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_REPORT_COMPLETE, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 8;
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