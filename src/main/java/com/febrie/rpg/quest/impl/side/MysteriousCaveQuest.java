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
 * Side Quest: Mysterious Cave
 * Explore a mysterious cave system filled with strange glowing moss and hidden treasures.
 *
 * @author Febrie
 */
public class MysteriousCaveQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class MysteriousCaveBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new MysteriousCaveQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public MysteriousCaveQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private MysteriousCaveQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new MysteriousCaveBuilder()
                .id(QuestID.SIDE_MYSTERIOUS_CAVE)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_cave_explorer", "cave_explorer"),
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
        return Component.translatable("quest.side.mysterious-cave.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Component line = Component.translatable("quest.side.mysterious-cave.description." + i);
            description.add(line);
        }
        return description;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "talk_cave_explorer" -> Component.translatable("quest.side.mysterious-cave.objectives.talk_cave_explorer");
            case "visit_dark_cave_entrance" -> Component.translatable("quest.side.mysterious-cave.objectives.visit_dark_cave_entrance");
            case "kill_bats" -> Component.translatable("quest.side.mysterious-cave.objectives.kill_bats");
            case "collect_glowing_moss" -> Component.translatable("quest.side.mysterious-cave.objectives.collect_glowing_moss");
            case "visit_underground_lake" -> Component.translatable("quest.side.mysterious-cave.objectives.visit_underground_lake");
            case "collect_cave_pearl" -> Component.translatable("quest.side.mysterious-cave.objectives.collect_cave_pearl");
            default -> Component.translatable("quest.side.mysterious-cave.objectives." + id);
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.side.mysterious-cave.dialogs.0");
            case 1 -> Component.translatable("quest.side.mysterious-cave.dialogs.1");
            case 2 -> Component.translatable("quest.side.mysterious-cave.dialogs.2");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.side.mysterious-cave.npc-name");
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.side.mysterious-cave.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.side.mysterious-cave.decline");
    }
}