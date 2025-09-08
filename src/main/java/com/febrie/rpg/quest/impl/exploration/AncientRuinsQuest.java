package com.febrie.rpg.quest.impl.exploration;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.BreakBlockObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 고대 유적 - 탐험 퀘스트
 * 고대 유적을 탐험하고 발굴하는 퀘스트
 *
 * @author Febrie
 */
public class AncientRuinsQuest extends Quest {

    /**
     * 기본 생성자
     */
    public AncientRuinsQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.EXPLORE_ANCIENT_RUINS)
                .objectives(List.of(
                        new VisitLocationObjective("explore_desert_ruins", "desert_ruins", 1),
                        new BreakBlockObjective("break_ancient_blocks", Material.SANDSTONE, 50),
                        new CollectItemObjective("collect_artifacts", Material.POTTERY_SHERD, 12),
                        new InteractNPCObjective("talk_to_archaeologist", "archaeologist", 1),
                        new VisitLocationObjective("explore_underground_ruins", "underground_ruins", 2)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 400)
                        .addItem(new ItemStack(Material.COMPASS, 1))
                        .addItem(new ItemStack(Material.MAP, 3))
                        .addItem(new ItemStack(Material.ANCIENT_DEBRIS, 2))
                        .addExperience(600)
                        .build())
                .sequential(true)
                .category(QuestCategory.EXPLORATION)
                .minLevel(15);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_EXPLORATION_ANCIENT_RUINS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_EXPLORATION_ANCIENT_RUINS_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "explore_desert_ruins" -> LangManager.list(LangKey.QUEST_EXPLORATION_ANCIENT_RUINS_OBJECTIVES_EXPLORE_DESERT_RUINS, who);
            case "break_ancient_blocks" -> LangManager.list(LangKey.QUEST_EXPLORATION_ANCIENT_RUINS_OBJECTIVES_BREAK_ANCIENT_BLOCKS, who);
            case "collect_artifacts" -> LangManager.list(LangKey.QUEST_EXPLORATION_ANCIENT_RUINS_OBJECTIVES_COLLECT_ARTIFACTS, who);
            case "talk_to_archaeologist" -> LangManager.list(LangKey.QUEST_EXPLORATION_ANCIENT_RUINS_OBJECTIVES_TALK_TO_ARCHAEOLOGIST, who);
            case "explore_underground_ruins" -> LangManager.list(LangKey.QUEST_EXPLORATION_ANCIENT_RUINS_OBJECTIVES_EXPLORE_UNDERGROUND_RUINS, who);
            default -> new ArrayList<>();
        };
    }
    
    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_EXPLORATION_ANCIENT_RUINS_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_EXPLORATION_ANCIENT_RUINS_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_EXPLORATION_ANCIENT_RUINS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_EXPLORATION_ANCIENT_RUINS_DECLINE, who);
    }
}