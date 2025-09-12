package com.febrie.rpg.quest.impl.weekly;

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
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import java.util.ArrayList;
import java.util.List;

/**
 * 주간 전설급 제작 - 주간 퀘스트
 * 매주 새로운 전설급 아이템을 제작하는 고난도 퀘스트
 *
 * @author Febrie
 */
public class LegendaryCraftQuest extends Quest {

    /**
     * 기본 생성자
     */
    public LegendaryCraftQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.WEEKLY_LEGENDARY_CRAFT)
                .objectives(List.of(
                    new CollectItemObjective("nether_star_collect", Material.NETHER_STAR, 5),
                    new CollectItemObjective("netherite_ingot_collect", Material.NETHERITE_INGOT, 8),
                    new InteractNPCObjective("find_blueprint", "legendary_architect"),
                    new InteractNPCObjective("prepare_forge", "ancient_forge"),
                    new CraftItemObjective("netherite_sword_craft", Material.NETHERITE_SWORD, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 7500)
                        .addItem(new ItemStack(Material.NETHERITE_BLOCK, 2))
                        .addItem(new ItemStack(Material.BEACON))
                        .addExperience(3000)
                        .build())
                .sequential(true)
                .weekly(true)
                .category(QuestCategory.WEEKLY)
                .minLevel(50)
                .completionLimit(1);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_LEGENDARY_CRAFT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_LEGENDARY_CRAFT_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "nether_star_collect" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_LEGENDARY_CRAFT_OBJECTIVES_NETHER_STAR_COLLECT, who);
            case "netherite_ingot_collect" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_LEGENDARY_CRAFT_OBJECTIVES_NETHERITE_INGOT_COLLECT, who);
            case "find_blueprint" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_LEGENDARY_CRAFT_OBJECTIVES_FIND_BLUEPRINT, who);
            case "prepare_forge" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_LEGENDARY_CRAFT_OBJECTIVES_PREPARE_FORGE, who);
            case "netherite_sword_craft" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_LEGENDARY_CRAFT_OBJECTIVES_NETHERITE_SWORD_CRAFT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }

    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_LEGENDARY_CRAFT_DIALOGS, who);
    }

    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }

    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_LEGENDARY_CRAFT_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_LEGENDARY_CRAFT_ACCEPT, who);
    }

    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_LEGENDARY_CRAFT_DECLINE, who);
    }
}