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
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.miners.plight.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.miners.plight.info"), who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_mine_foreman" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.miners.plight.objectives.talk.mine.foreman"), who);
            case "visit_collapsed_mine" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.miners.plight.objectives.visit.collapsed.mine"), who);
            case "oak_log_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.miners.plight.objectives.oak.log.collect"), who);
            case "kill_cave_spiders" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.miners.plight.objectives.kill.cave.spiders"), who);
            case "visit_trapped_miners" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.miners.plight.objectives.visit.trapped.miners"), who);
            case "iron_pickaxe_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.miners.plight.objectives.iron.pickaxe.collect"), who);
            case "return_mine_foreman" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.miners.plight.objectives.return.mine.foreman"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.miners.plight.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.miners.plight.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.miners.plight.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.miners.plight.decline"), who);
    }
}