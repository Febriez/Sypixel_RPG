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
 * 주간 자원 수집 - 주간 퀘스트
 * 왕국의 주간 건설 프로젝트를 위한 희귀 자원을 수집하는 퀘스트
 *
 * @author Febrie
 */
public class ResourceGatheringQuest extends Quest {

    /**
     * 기본 생성자
     */
    public ResourceGatheringQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.WEEKLY_RESOURCE_GATHERING)
                .objectives(List.of(
                    new BreakBlockObjective("mine_ores", Material.DIAMOND_ORE, 100),
                    new BreakBlockObjective("harvest_wood", Material.DARK_OAK_LOG, 80),
                    new CollectItemObjective("sweet_berries_collect", Material.SWEET_BERRIES, 60),
                    new CollectItemObjective("amethyst_shard_collect", Material.AMETHYST_SHARD, 40),
                    new DeliverItemObjective("chest_deliver", Material.CHEST, 1, "kingdom_resource_manager")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 4500)
                        .addItem(new ItemStack(Material.DIAMOND_PICKAXE))
                        .addItem(new ItemStack(Material.SHULKER_BOX, 2))
                        .addExperience(2800)
                        .build())
                .sequential(false)
                .weekly(true)
                .category(QuestCategory.WEEKLY)
                .minLevel(25)
                .completionLimit(1);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_RESOURCE_GATHERING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_RESOURCE_GATHERING_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "mine_ores" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_RESOURCE_GATHERING_OBJECTIVES_MINE_ORES, who);
            case "harvest_wood" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_RESOURCE_GATHERING_OBJECTIVES_HARVEST_WOOD, who);
            case "sweet_berries_collect" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_RESOURCE_GATHERING_OBJECTIVES_SWEET_BERRIES_COLLECT, who);
            case "amethyst_shard_collect" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_RESOURCE_GATHERING_OBJECTIVES_AMETHYST_SHARD_COLLECT, who);
            case "chest_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_RESOURCE_GATHERING_OBJECTIVES_CHEST_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }

    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_WEEKLY_RESOURCE_GATHERING_DIALOGS, who);
    }

    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }

    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_RESOURCE_GATHERING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_RESOURCE_GATHERING_ACCEPT, who);
    }

    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_WEEKLY_RESOURCE_GATHERING_DECLINE, who);
    }
}