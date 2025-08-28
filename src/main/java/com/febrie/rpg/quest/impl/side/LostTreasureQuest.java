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
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_old_sailor", "old_sailor"),
                        new CollectItemObjective("collect_treasure_map", Material.MAP, 1),
                        new VisitLocationObjective("visit_cursed_cove", "cursed_cove"),
                        new KillMobObjective("kill_skeletons", EntityType.SKELETON, 12),
                        new VisitLocationObjective("visit_buried_treasure", "buried_treasure"),
                        new CollectItemObjective("collect_gold_coins", Material.GOLD_NUGGET, 25),
                        new CollectItemObjective("collect_ancient_artifact", Material.GOLDEN_APPLE, 1),
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
        return Component.translatable("quest.side.lost-treasure.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Component line = Component.translatable("quest.side.lost-treasure.description." + i);
            description.add(line);
        }
        return description;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "talk_old_sailor" -> Component.translatable("quest.side.lost-treasure.objectives.talk_old_sailor");
            case "collect_treasure_map" -> Component.translatable("quest.side.lost-treasure.objectives.collect_treasure_map");
            case "visit_cursed_cove" -> Component.translatable("quest.side.lost-treasure.objectives.visit_cursed_cove");
            case "kill_skeletons" -> Component.translatable("quest.side.lost-treasure.objectives.kill_skeletons");
            case "visit_buried_treasure" -> Component.translatable("quest.side.lost-treasure.objectives.visit_buried_treasure");
            case "collect_gold_coins" -> Component.translatable("quest.side.lost-treasure.objectives.collect_gold_coins");
            case "collect_ancient_artifact" -> Component.translatable("quest.side.lost-treasure.objectives.collect_ancient_artifact");
            case "return_old_sailor" -> Component.translatable("quest.side.lost-treasure.objectives.return_old_sailor");
            default -> Component.translatable("quest.side.lost-treasure.objectives." + id);
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.side.lost-treasure.dialogs.0");
            case 1 -> Component.translatable("quest.side.lost-treasure.dialogs.1");
            case 2 -> Component.translatable("quest.side.lost-treasure.dialogs.2");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.side.lost-treasure.npc-name");
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.side.lost-treasure.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.side.lost-treasure.decline");
    }
}