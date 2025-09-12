package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.lang.quest.side.MinersPlightLangKey;

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * Side Quest: Miner's Plight
 * Help the mine foreman rescue trapped miners from a collapsed mine shaft.
 *
 * @author Febrie
 */
public class MinersPlightQuest extends Quest {

    /**
     * 기본 생성자
     */
    public MinersPlightQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_MINERS_PLIGHT)
                .objectives(List.of(
                        new InteractNPCObjective("talk_mine_foreman", "mine_foreman"),
                        new VisitLocationObjective("visit_collapsed_mine", "collapsed_mine"),
                        new CollectItemObjective("oak_log_collect", Material.OAK_LOG, 12),
                        new KillMobObjective("kill_cave_spiders", EntityType.CAVE_SPIDER, 20),
                        new VisitLocationObjective("visit_trapped_miners", "trapped_miners"),
                        new CollectItemObjective("iron_pickaxe_collect", Material.IRON_PICKAXE, 5),
                        new InteractNPCObjective("return_mine_foreman", "mine_foreman")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(3200)
                        .addCurrency(CurrencyType.GOLD, 800)
                        .addItem(new ItemStack(Material.DIAMOND_PICKAXE, 1))
                        .addItem(new ItemStack(Material.TORCH, 64))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(22);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(MinersPlightLangKey.QUEST_SIDE_MINERS_PLIGHT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(MinersPlightLangKey.QUEST_SIDE_MINERS_PLIGHT_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_mine_foreman" -> LangManager.text(MinersPlightLangKey.QUEST_SIDE_MINERS_PLIGHT_OBJECTIVES_TALK_MINE_FOREMAN, who);
            case "visit_collapsed_mine" -> LangManager.text(MinersPlightLangKey.QUEST_SIDE_MINERS_PLIGHT_OBJECTIVES_VISIT_COLLAPSED_MINE, who);
            case "oak_log_collect" -> LangManager.text(MinersPlightLangKey.QUEST_SIDE_MINERS_PLIGHT_OBJECTIVES_OAK_LOG_COLLECT, who);
            case "kill_cave_spiders" -> LangManager.text(MinersPlightLangKey.QUEST_SIDE_MINERS_PLIGHT_OBJECTIVES_KILL_CAVE_SPIDERS, who);
            case "visit_trapped_miners" -> LangManager.text(MinersPlightLangKey.QUEST_SIDE_MINERS_PLIGHT_OBJECTIVES_VISIT_TRAPPED_MINERS, who);
            case "iron_pickaxe_collect" -> LangManager.text(MinersPlightLangKey.QUEST_SIDE_MINERS_PLIGHT_OBJECTIVES_IRON_PICKAXE_COLLECT, who);
            case "return_mine_foreman" -> LangManager.text(MinersPlightLangKey.QUEST_SIDE_MINERS_PLIGHT_OBJECTIVES_RETURN_MINE_FOREMAN, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(MinersPlightLangKey.QUEST_SIDE_MINERS_PLIGHT_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(MinersPlightLangKey.QUEST_SIDE_MINERS_PLIGHT_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(MinersPlightLangKey.QUEST_SIDE_MINERS_PLIGHT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(MinersPlightLangKey.QUEST_SIDE_MINERS_PLIGHT_DECLINE, who);
    }
}