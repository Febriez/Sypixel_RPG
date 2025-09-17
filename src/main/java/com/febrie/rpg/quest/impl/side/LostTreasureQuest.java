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
 * Side Quest: Lost Treasure
 * Find a pirate's lost treasure using an old map
 *
 * @author Febrie
 */
public class LostTreasureQuest extends Quest {

    /**
     * 기본 생성자
     */
    public LostTreasureQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_LOST_TREASURE)
                .objectives(List.of(
                        new InteractNPCObjective("talk_old_sailor", "old_sailor"),
                        new CollectItemObjective("map_collect", Material.MAP, 1),
                        new VisitLocationObjective("visit_cursed_cove", "cursed_cove"),
                        new KillMobObjective("kill_skeletons", EntityType.SKELETON, 12),
                        new VisitLocationObjective("visit_buried_treasure", "buried_treasure"),
                        new CollectItemObjective("gold_nugget_collect", Material.GOLD_NUGGET, 25),
                        new CollectItemObjective("golden_apple_collect", Material.GOLDEN_APPLE, 1),
                        new InteractNPCObjective("return_old_sailor", "old_sailor")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(1500)
                        .addCurrency(CurrencyType.GOLD, 400)
                        .addItem(new ItemStack(Material.EMERALD, 5))
                        .addItem(new ItemStack(Material.DIAMOND, 2))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(12);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.lost.treasure.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.lost.treasure.info"), who);
    }

        @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_old_sailor" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.lost.treasure.objectives.talk.old.sailor"), who);
            case "map_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.lost.treasure.objectives.map.collect"), who);
            case "visit_cursed_cove" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.lost.treasure.objectives.visit.cursed.cove"), who);
            case "kill_skeletons" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.lost.treasure.objectives.kill.skeletons"), who);
            case "visit_buried_treasure" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.lost.treasure.objectives.visit.buried.treasure"), who);
            case "gold_nugget_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.lost.treasure.objectives.gold.nugget.collect"), who);
            case "golden_apple_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.lost.treasure.objectives.golden.apple.collect"), who);
            case "return_old_sailor" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.lost.treasure.objectives.return.old.sailor"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.lost.treasure.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.lost.treasure.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.lost.treasure.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.lost.treasure.decline"), who);
    }
}