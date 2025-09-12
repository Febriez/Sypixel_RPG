package com.febrie.rpg.quest.impl.crafting;

import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 마법 부여 숙련 퀘스트
 * 플레이어가 마법 부여의 대가가 되는 퀘스트
 *
 * @author Febrie
 */
public class EnchantmentMasteryQuest extends Quest {

    /**
     * 기본 생성자
     */
    public EnchantmentMasteryQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.CRAFT_ENCHANTMENT_MASTERY)
                .objectives(List.of(
                        new InteractNPCObjective("enchantment_master", "grand_enchanter"),
                        new CollectItemObjective("experience_bottle_collect", Material.EXPERIENCE_BOTTLE, 20),
                        new CollectItemObjective("enchanted_book_collect", Material.ENCHANTED_BOOK, 15),
                        new CraftItemObjective("diamond_sword_craft", Material.DIAMOND_SWORD, 3),
                        new CraftItemObjective("diamond_chestplate_craft", Material.DIAMOND_CHESTPLATE, 2),
                        new CollectItemObjective("lapis_lazuli_collect", Material.LAPIS_LAZULI, 100),
                        new CollectItemObjective("book_collect", Material.BOOK, 5),
                        new CraftItemObjective("diamond_pickaxe_craft", Material.DIAMOND_PICKAXE, 4),
                        new CollectItemObjective("ghast_tear_collect", Material.GHAST_TEAR, 8),
                        new CraftItemObjective("netherite_sword_craft", Material.NETHERITE_SWORD, 1),
                        new DeliverItemObjective("netherite_sword_deliver", Material.NETHERITE_SWORD, 1, "grand_enchanter")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 8000)
                        .addExperience(2000)
                        .build())
                .sequential(true)
                .category(QuestCategory.CRAFTING)
                .minLevel(30)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_CRAFTING_ENCHANTMENT_MASTERY_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_CRAFTING_ENCHANTMENT_MASTERY_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "enchantment_master" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_ENCHANTMENT_MASTERY_OBJECTIVES_ENCHANTMENT_MASTER, who);
            case "experience_bottle_collect" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_ENCHANTMENT_MASTERY_OBJECTIVES_EXPERIENCE_BOTTLE_COLLECT, who);
            case "enchanted_book_collect" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_ENCHANTMENT_MASTERY_OBJECTIVES_ENCHANTED_BOOK_COLLECT, who);
            case "diamond_sword_craft" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_ENCHANTMENT_MASTERY_OBJECTIVES_DIAMOND_SWORD_CRAFT, who);
            case "diamond_chestplate_craft" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_ENCHANTMENT_MASTERY_OBJECTIVES_DIAMOND_CHESTPLATE_CRAFT, who);
            case "lapis_lazuli_collect" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_ENCHANTMENT_MASTERY_OBJECTIVES_LAPIS_LAZULI_COLLECT, who);
            case "book_collect" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_ENCHANTMENT_MASTERY_OBJECTIVES_BOOK_COLLECT, who);
            case "diamond_pickaxe_craft" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_ENCHANTMENT_MASTERY_OBJECTIVES_DIAMOND_PICKAXE_CRAFT, who);
            case "ghast_tear_collect" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_ENCHANTMENT_MASTERY_OBJECTIVES_GHAST_TEAR_COLLECT, who);
            case "netherite_sword_craft" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_ENCHANTMENT_MASTERY_OBJECTIVES_NETHERITE_SWORD_CRAFT, who);
            case "netherite_sword_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_ENCHANTMENT_MASTERY_OBJECTIVES_NETHERITE_SWORD_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_CRAFTING_ENCHANTMENT_MASTERY_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_CRAFTING_ENCHANTMENT_MASTERY_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_CRAFTING_ENCHANTMENT_MASTERY_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_CRAFTING_ENCHANTMENT_MASTERY_DECLINE, who);
    }
}