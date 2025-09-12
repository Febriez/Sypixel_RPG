package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.lang.quest.side.VolcanicDepthsLangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * 화산 깊이 - 사이드 퀘스트
 * 위험한 화산 깊은 곳에서 연구자를 도와 용암 형성을 연구하는 퀘스트
 *
 * @author Febrie
 */
public class VolcanicDepthsQuest extends Quest {

    /**
     * 기본 생성자
     */
    public VolcanicDepthsQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_VOLCANIC_DEPTHS)
                .objectives(List.of(
                        new InteractNPCObjective("volcano_researcher", "volcano_researcher"),
                        new VisitLocationObjective("volcano_rim", "volcano_rim"),
                        new KillMobObjective("kill_magma_cubes", EntityType.MAGMA_CUBE, 25),
                        new CollectItemObjective("obsidian_collect", Material.OBSIDIAN, 15),
                        new VisitLocationObjective("lava_chamber", "lava_chamber"),
                        new CollectItemObjective("magma_cream_collect", Material.MAGMA_CREAM, 10)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 1500)
                        .addItem(new ItemStack(Material.FIRE_CHARGE, 20))
                        .addExperience(5000)
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(30);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(VolcanicDepthsLangKey.QUEST_SIDE_VOLCANIC_DEPTHS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(VolcanicDepthsLangKey.QUEST_SIDE_VOLCANIC_DEPTHS_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "volcano_researcher" -> LangManager.text(VolcanicDepthsLangKey.QUEST_SIDE_VOLCANIC_DEPTHS_OBJECTIVES_VOLCANO_RESEARCHER, who);
            case "volcano_rim" -> LangManager.text(VolcanicDepthsLangKey.QUEST_SIDE_VOLCANIC_DEPTHS_OBJECTIVES_VOLCANO_RIM, who);
            case "kill_magma_cubes" -> LangManager.text(VolcanicDepthsLangKey.QUEST_SIDE_VOLCANIC_DEPTHS_OBJECTIVES_KILL_MAGMA_CUBES, who);
            case "obsidian_collect" -> LangManager.text(VolcanicDepthsLangKey.QUEST_SIDE_VOLCANIC_DEPTHS_OBJECTIVES_OBSIDIAN_COLLECT, who);
            case "lava_chamber" -> LangManager.text(VolcanicDepthsLangKey.QUEST_SIDE_VOLCANIC_DEPTHS_OBJECTIVES_LAVA_CHAMBER, who);
            case "magma_cream_collect" -> LangManager.text(VolcanicDepthsLangKey.QUEST_SIDE_VOLCANIC_DEPTHS_OBJECTIVES_MAGMA_CREAM_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(VolcanicDepthsLangKey.QUEST_SIDE_VOLCANIC_DEPTHS_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(VolcanicDepthsLangKey.QUEST_SIDE_VOLCANIC_DEPTHS_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(VolcanicDepthsLangKey.QUEST_SIDE_VOLCANIC_DEPTHS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(VolcanicDepthsLangKey.QUEST_SIDE_VOLCANIC_DEPTHS_DECLINE, who);
    }
}