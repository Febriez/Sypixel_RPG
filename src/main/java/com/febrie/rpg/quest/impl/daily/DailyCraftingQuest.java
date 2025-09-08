package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
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
                        new InteractNPCObjective("blacksmith", "daily_blacksmith", 1), // 대장장이
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
                        new InteractNPCObjective("report_complete", "daily_blacksmith", 1)
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
        return LangManager.text(LangKey.QUEST_DAILY_CRAFTING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_DAILY_CRAFTING_INFO, who);
    }

        @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "blacksmith" -> LangManager.list(LangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_BLACKSMITH, who);
            case "craft_wood_tools" -> LangManager.list(LangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_CRAFT_WOOD_TOOLS, who);
            case "craft_stone_tools" -> LangManager.list(LangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_CRAFT_STONE_TOOLS, who);
            case "craft_iron_tools" -> LangManager.list(LangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_CRAFT_IRON_TOOLS, who);
            case "craft_leather_armor" -> LangManager.list(LangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_CRAFT_LEATHER_ARMOR, who);
            case "craft_chainmail" -> LangManager.list(LangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_CRAFT_CHAINMAIL, who);
            case "craft_furnace" -> LangManager.list(LangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_CRAFT_FURNACE, who);
            case "craft_chest" -> LangManager.list(LangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_CRAFT_CHEST, who);
            case "craft_torches" -> LangManager.list(LangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_CRAFT_TORCHES, who);
            case "craft_ladder" -> LangManager.list(LangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_CRAFT_LADDER, who);
            case "craft_bread" -> LangManager.list(LangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_CRAFT_BREAD, who);
            case "craft_cookies" -> LangManager.list(LangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_CRAFT_COOKIES, who);
            case "deliver_tools" -> LangManager.list(LangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_DELIVER_TOOLS, who);
            case "deliver_armor" -> LangManager.list(LangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_DELIVER_ARMOR, who);
            case "report_complete" -> LangManager.list(LangKey.QUEST_DAILY_CRAFTING_OBJECTIVES_REPORT_COMPLETE, who);
            default -> new ArrayList<>();
        };
    }

    @Override
    public int getDialogCount() {
        return 8;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_DAILY_CRAFTING_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_DAILY_CRAFTING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_DAILY_CRAFTING_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_DAILY_CRAFTING_DECLINE, who);
    }
}