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
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import java.util.ArrayList;
import java.util.List;

/**
 * 주간 엘리트 사냥 - 주간 퀘스트
 * 매주 등장하는 강력한 엘리트 몬스터를 사냥하는 퀘스트
 *
 * @author Febrie
 */
public class EliteHuntingQuest extends Quest {

    /**
     * 기본 생성자
     */
    public EliteHuntingQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.WEEKLY_ELITE_HUNTING)
                .objectives(List.of(
                    new VisitLocationObjective("find_elite", "elite_spawn_area"),
                    new KillMobObjective("defeat_elite", EntityType.RAVAGER, 1),
                    new CollectItemObjective("ghast_tear_collect", Material.GHAST_TEAR, 1),
                    new InteractNPCObjective("return_hunter", "hunter_guild_master")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 4000)
                        .addItem(new ItemStack(Material.DIAMOND_SWORD))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 2))
                        .addExperience(1800)
                        .build())
                .sequential(true)
                .weekly(true)
                .category(QuestCategory.WEEKLY)
                .minLevel(35)
                .completionLimit(1);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_ELITE_HUNTING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_ELITE_HUNTING_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "find_elite" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_ELITE_HUNTING_OBJECTIVES_FIND_ELITE, who);
            case "defeat_elite" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_ELITE_HUNTING_OBJECTIVES_DEFEAT_ELITE, who);
            case "ghast_tear_collect" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_ELITE_HUNTING_OBJECTIVES_GHAST_TEAR_COLLECT, who);
            case "return_hunter" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_ELITE_HUNTING_OBJECTIVES_RETURN_HUNTER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }

    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_ELITE_HUNTING_DIALOGS, who);
    }

    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }

    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_ELITE_HUNTING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_ELITE_HUNTING_ACCEPT, who);
    }

    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_ELITE_HUNTING_DECLINE, who);
    }
}