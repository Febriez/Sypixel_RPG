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
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
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
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.get("quest.daily.crafting.name", who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.daily.crafting.info", who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String key = "quest.daily.crafting.objectives." + objective.getId();
        return LangManager.get(key, who);
    }

    @Override
    public int getDialogCount() {
        return 8;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.daily.crafting.dialogs.0", who);
            case 1 -> LangManager.get("quest.daily.crafting.dialogs.1", who);
            case 2 -> LangManager.get("quest.daily.crafting.dialogs.2", who);
            case 3 -> LangManager.get("quest.daily.crafting.dialogs.3", who);
            case 4 -> LangManager.get("quest.daily.crafting.dialogs.4", who);
            case 5 -> LangManager.get("quest.daily.crafting.dialogs.5", who);
            case 6 -> LangManager.get("quest.daily.crafting.dialogs.6", who);
            case 7 -> LangManager.get("quest.daily.crafting.dialogs.7", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.daily.crafting.npc_name", who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.daily.crafting.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.daily.crafting.decline", who);
    }
}