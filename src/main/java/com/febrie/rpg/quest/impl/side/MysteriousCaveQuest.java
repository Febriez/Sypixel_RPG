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
import java.util.ArrayList;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * Side Quest: Mysterious Cave
 * Explore a mysterious cave system filled with strange glowing moss and hidden treasures.
 *
 * @author Febrie
 */
public class MysteriousCaveQuest extends Quest {

    /**
     * 기본 생성자
     */
    public MysteriousCaveQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_MYSTERIOUS_CAVE)
                .objectives(List.of(
                        new InteractNPCObjective("talk_cave_explorer", "cave_explorer"),
                        new VisitLocationObjective("visit_dark_cave_entrance", "dark_cave_entrance"),
                        new KillMobObjective("kill_bats", EntityType.BAT, 15),
                        new CollectItemObjective("glow_lichen_collect", Material.GLOW_LICHEN, 10),
                        new VisitLocationObjective("visit_underground_lake", "underground_lake"),
                        new CollectItemObjective("ender_pearl_collect", Material.ENDER_PEARL, 2)
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(1200)
                        .addCurrency(CurrencyType.GOLD, 250)
                        .addItem(new ItemStack(Material.TORCH, 32))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(10);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.mysterious.cave.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.mysterious.cave.info"), who);
    }

        @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_cave_explorer" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.mysterious.cave.objectives.talk.cave.explorer"), who);
            case "visit_dark_cave_entrance" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.mysterious.cave.objectives.visit.dark.cave.entrance"), who);
            case "kill_bats" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.mysterious.cave.objectives.kill.bats"), who);
            case "glow_lichen_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.mysterious.cave.objectives.glow.lichen.collect"), who);
            case "visit_underground_lake" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.mysterious.cave.objectives.visit.underground.lake"), who);
            case "ender_pearl_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.mysterious.cave.objectives.ender.pearl.collect"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.mysterious.cave.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.mysterious.cave.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.mysterious.cave.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.mysterious.cave.decline"), who);
    }
}