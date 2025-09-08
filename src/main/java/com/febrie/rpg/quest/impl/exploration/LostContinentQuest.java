package com.febrie.rpg.quest.impl.exploration;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.PlaceBlockObjective;
import com.febrie.rpg.quest.objective.impl.BreakBlockObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
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
 * 잃어버린 대륙 - 탐험 퀘스트
 * 새로운 땅과 대륙을 발견하는 퀘스트
 *
 * @author Febrie
 */
public class LostContinentQuest extends Quest {

    /**
     * 기본 생성자
     */
    public LostContinentQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.EXPLORE_LOST_CONTINENT)
                .objectives(List.of(
                        new BreakBlockObjective("break_ocean_blocks", Material.PRISMARINE, 100),
                        new PlaceBlockObjective("build_expedition_camp", Material.OAK_PLANKS, 64),
                        new VisitLocationObjective("explore_mysterious_lands", "lost_continent", 1),
                        new CollectItemObjective("collect_exotic_materials", Material.PRISMARINE_CRYSTALS, 20),
                        new KillMobObjective("defeat_continent_guardians", EntityType.GUARDIAN, 15)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 800)
                        .addItem(new ItemStack(Material.ELYTRA, 1))
                        .addItem(new ItemStack(Material.FIREWORK_ROCKET, 64))
                        .addItem(new ItemStack(Material.TRIDENT, 1))
                        .addExperience(1000)
                        .build())
                .sequential(true)
                .category(QuestCategory.EXPLORATION)
                .minLevel(25);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_EXPLORATION_LOST_CONTINENT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_EXPLORATION_LOST_CONTINENT_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "break_ocean_blocks" -> LangManager.list(LangKey.QUEST_EXPLORATION_LOST_CONTINENT_OBJECTIVES_BREAK_OCEAN_BLOCKS, who);
            case "build_expedition_camp" -> LangManager.list(LangKey.QUEST_EXPLORATION_LOST_CONTINENT_OBJECTIVES_BUILD_EXPEDITION_CAMP, who);
            case "explore_mysterious_lands" -> LangManager.list(LangKey.QUEST_EXPLORATION_LOST_CONTINENT_OBJECTIVES_EXPLORE_MYSTERIOUS_LANDS, who);
            case "collect_exotic_materials" -> LangManager.list(LangKey.QUEST_EXPLORATION_LOST_CONTINENT_OBJECTIVES_COLLECT_EXOTIC_MATERIALS, who);
            case "defeat_continent_guardians" -> LangManager.list(LangKey.QUEST_EXPLORATION_LOST_CONTINENT_OBJECTIVES_DEFEAT_CONTINENT_GUARDIANS, who);
            default -> new ArrayList<>();
        };
    }
    
    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_EXPLORATION_LOST_CONTINENT_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_EXPLORATION_LOST_CONTINENT_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_EXPLORATION_LOST_CONTINENT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_EXPLORATION_LOST_CONTINENT_DECLINE, who);
    }
}