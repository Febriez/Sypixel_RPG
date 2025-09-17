package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
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
 * Side Quest: Giant Slayer
 * Face legendary giants that walk the earth once more
 *
 * @author Febrie
 */
public class GiantSlayerQuest extends Quest {

    /**
     * 기본 생성자
     */
    public GiantSlayerQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_GIANT_SLAYER)
                .objectives(List.of(
                        new InteractNPCObjective("talk_giant_researcher", "giant_researcher"),
                        new VisitLocationObjective("giant_tracks", "Giant_Tracks"),
                        new CollectItemObjective("clay_ball_collect", Material.CLAY_BALL, 12),
                        new CollectItemObjective("written_book_collect", Material.WRITTEN_BOOK, 3),
                        new VisitLocationObjective("ancient_battlefield", "Ancient_Battlefield"),
                        new KillMobObjective("stone_giants", EntityType.IRON_GOLEM, 3),
                        new KillMobObjective("frost_giants", EntityType.SNOW_GOLEM, 2),
                        new CollectItemObjective("totem_of_undying_collect", Material.TOTEM_OF_UNDYING, 2),
                        new InteractNPCObjective("claim_victory", "giant_researcher")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(3000)
                        .addCurrency(CurrencyType.GOLD, 750)
                        .addItem(new ItemStack(Material.NETHERITE_AXE, 1))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 2))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 3))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(28);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.giant.slayer.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.giant.slayer.info"), who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_giant_researcher" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.giant.slayer.objectives.talk.giant.researcher"), who);
            case "giant_tracks" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.giant.slayer.objectives.giant.tracks"), who);
            case "clay_ball_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.giant.slayer.objectives.clay.ball.collect"), who);
            case "written_book_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.giant.slayer.objectives.written.book.collect"), who);
            case "ancient_battlefield" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.giant.slayer.objectives.ancient.battlefield"), who);
            case "stone_giants" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.giant.slayer.objectives.stone.giants"), who);
            case "frost_giants" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.giant.slayer.objectives.frost.giants"), who);
            case "totem_of_undying_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.giant.slayer.objectives.totem.of.undying.collect"), who);
            case "claim_victory" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.giant.slayer.objectives.claim.victory"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.giant.slayer.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.giant.slayer.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.giant.slayer.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.giant.slayer.decline"), who);
    }
}