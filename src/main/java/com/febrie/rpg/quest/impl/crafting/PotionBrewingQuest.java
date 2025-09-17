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

import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 물약 양조 숙련 퀘스트
 * 플레이어가 마스터 연금술사가 되는 퀘스트
 *
 * @author Febrie
 */
public class PotionBrewingQuest extends Quest {

    /**
     * 기본 생성자
     */
    public PotionBrewingQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.CRAFT_POTION_BREWING)
                .objectives(List.of(
                        new InteractNPCObjective("master_alchemist", "alchemist_master"),
                        new CollectItemObjective("potion_collect", Material.POTION, 10),
                        new CollectItemObjective("nether_wart_collect", Material.NETHER_WART, 50),
                        new CollectItemObjective("blaze_powder_collect", Material.BLAZE_POWDER, 25),
                        new CraftItemObjective("potion_craft", Material.POTION, 5),
                        new CollectItemObjective("fermented_spider_eye_collect", Material.FERMENTED_SPIDER_EYE, 8),
                        new CraftItemObjective("potion_craft", Material.POTION, 3),
                        new CollectItemObjective("phantom_membrane_collect", Material.PHANTOM_MEMBRANE, 12),
                        new CraftItemObjective("potion_craft", Material.POTION, 4),
                        new CollectItemObjective("dragon_breath_collect", Material.DRAGON_BREATH, 6),
                        new CraftItemObjective("potion_craft", Material.POTION, 1),
                        new DeliverItemObjective("potion_deliver", Material.POTION, 1, "alchemist_master")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 7500)
                        .addExperience(1800)
                        .build())
                .sequential(true)
                .category(QuestCategory.CRAFTING)
                .minLevel(25)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_CRAFTING_POTION_BREWING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_CRAFTING_POTION_BREWING_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "master_alchemist" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_POTION_BREWING_OBJECTIVES_MASTER_ALCHEMIST, who);
            case "potion_collect" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_POTION_BREWING_OBJECTIVES_POTION_COLLECT, who);
            case "nether_wart_collect" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_POTION_BREWING_OBJECTIVES_NETHER_WART_COLLECT, who);
            case "blaze_powder_collect" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_POTION_BREWING_OBJECTIVES_BLAZE_POWDER_COLLECT, who);
            case "potion_craft" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_POTION_BREWING_OBJECTIVES_POTION_CRAFT, who);
            case "fermented_spider_eye_collect" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_POTION_BREWING_OBJECTIVES_FERMENTED_SPIDER_EYE_COLLECT, who);
            case "phantom_membrane_collect" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_POTION_BREWING_OBJECTIVES_PHANTOM_MEMBRANE_COLLECT, who);
            case "dragon_breath_collect" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_POTION_BREWING_OBJECTIVES_DRAGON_BREATH_COLLECT, who);
            case "potion_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_CRAFT_POTION_BREWING_OBJECTIVES_POTION_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_CRAFTING_POTION_BREWING_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_CRAFTING_POTION_BREWING_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_CRAFTING_POTION_BREWING_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_CRAFTING_POTION_BREWING_DECLINE, who);
    }
}