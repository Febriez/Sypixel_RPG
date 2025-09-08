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

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

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
                        new InteractNPCObjective("talk_cave_explorer", "cave_explorer", 1),
                        new VisitLocationObjective("visit_dark_cave_entrance", "dark_cave_entrance"),
                        new KillMobObjective("kill_bats", EntityType.BAT, 15),
                        new CollectItemObjective("collect_glowing_moss", Material.GLOW_LICHEN, 10),
                        new VisitLocationObjective("visit_underground_lake", "underground_lake"),
                        new CollectItemObjective("collect_cave_pearl", Material.ENDER_PEARL, 2)
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
        return LangManager.text(LangKey.QUEST_SIDE_MYSTERIOUS_CAVE_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SIDE_MYSTERIOUS_CAVE_INFO, who);
    }

        @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "cave_explorer" -> LangManager.list(LangKey.QUEST_SIDE_MYSTERIOUS_CAVE_OBJECTIVES_CAVE_EXPLORER, who);
            case "enter_cave" -> LangManager.list(LangKey.QUEST_SIDE_MYSTERIOUS_CAVE_OBJECTIVES_ENTER_CAVE, who);
            case "light_torches" -> LangManager.list(LangKey.QUEST_SIDE_MYSTERIOUS_CAVE_OBJECTIVES_LIGHT_TORCHES, who);
            case "defeat_bats" -> LangManager.list(LangKey.QUEST_SIDE_MYSTERIOUS_CAVE_OBJECTIVES_DEFEAT_BATS, who);
            case "find_crystal" -> LangManager.list(LangKey.QUEST_SIDE_MYSTERIOUS_CAVE_OBJECTIVES_FIND_CRYSTAL, who);
            default -> new ArrayList<>();
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SIDE_MYSTERIOUS_CAVE_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_MYSTERIOUS_CAVE_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_MYSTERIOUS_CAVE_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_MYSTERIOUS_CAVE_DECLINE, who);
    }
}