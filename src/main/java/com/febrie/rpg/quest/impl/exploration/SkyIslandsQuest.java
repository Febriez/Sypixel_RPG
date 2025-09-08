package com.febrie.rpg.quest.impl.exploration;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.SurviveObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.objective.impl.PlaceBlockObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 하늘섬 탐험 - 탐험 퀘스트
 * 하늘 높은 곳의 섬들을 탐험하는 퀘스트
 *
 * @author Febrie
 */
public class SkyIslandsQuest extends Quest {

    /**
     * 기본 생성자
     */
    public SkyIslandsQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.EXPLORE_SKY_ISLANDS)
                .objectives(List.of(
                        new SurviveObjective("reach_sky_height", 5), // Survive 5 attempts at high altitude
                        new VisitLocationObjective("explore_floating_islands", "sky_island", 5),
                        new CollectItemObjective("collect_sky_crystals", Material.AMETHYST_SHARD, 30),
                        new KillMobObjective("defeat_sky_guardians", EntityType.PHANTOM, 25),
                        new PlaceBlockObjective("build_sky_bridge", Material.COBBLESTONE, 50)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 600)
                        .addItem(new ItemStack(Material.FEATHER, 64))
                        .addItem(new ItemStack(Material.WIND_CHARGE, 32))
                        .addItem(new ItemStack(Material.PHANTOM_MEMBRANE, 10))
                        .addExperience(750)
                        .build())
                .sequential(false)
                .category(QuestCategory.EXPLORATION)
                .minLevel(20);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_EXPLORATION_SKY_ISLANDS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_EXPLORATION_SKY_ISLANDS_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "reach_sky_height" -> LangManager.list(LangKey.QUEST_EXPLORATION_SKY_ISLANDS_OBJECTIVES_REACH_SKY_HEIGHT, who);
            case "explore_floating_islands" -> LangManager.list(LangKey.QUEST_EXPLORATION_SKY_ISLANDS_OBJECTIVES_EXPLORE_FLOATING_ISLANDS, who);
            case "collect_sky_crystals" -> LangManager.list(LangKey.QUEST_EXPLORATION_SKY_ISLANDS_OBJECTIVES_COLLECT_SKY_CRYSTALS, who);
            case "defeat_sky_guardians" -> LangManager.list(LangKey.QUEST_EXPLORATION_SKY_ISLANDS_OBJECTIVES_DEFEAT_SKY_GUARDIANS, who);
            case "build_sky_bridge" -> LangManager.list(LangKey.QUEST_EXPLORATION_SKY_ISLANDS_OBJECTIVES_BUILD_SKY_BRIDGE, who);
            default -> new ArrayList<>();
        };
    }
    
    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_EXPLORATION_SKY_ISLANDS_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_EXPLORATION_SKY_ISLANDS_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_EXPLORATION_SKY_ISLANDS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_EXPLORATION_SKY_ISLANDS_DECLINE, who);
    }
}