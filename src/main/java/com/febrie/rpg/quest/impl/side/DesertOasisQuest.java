package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
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
 * Desert Oasis - Side Quest
 * Guide a desert nomad to find a legendary hidden oasis in the vast wasteland.
 *
 * @author Febrie
 */
public class DesertOasisQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class DesertOasisBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new DesertOasisQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public DesertOasisQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private DesertOasisQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new DesertOasisBuilder()
                .id(QuestID.SIDE_DESERT_OASIS)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_desert_nomad", "desert_nomad"),
                        new VisitLocationObjective("mirages_edge", "Mirages_Edge"),
                        new KillMobObjective("kill_husks", EntityType.HUSK, 20),
                        new CollectItemObjective("desert_blooms", Material.CACTUS, 12),
                        new VisitLocationObjective("hidden_oasis", "Hidden_Oasis"),
                        new CollectItemObjective("oasis_water", Material.WATER_BUCKET, 3)
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(4000)
                        .addCurrency(CurrencyType.GOLD, 1000)
                        .addItem(new ItemStack(Material.DIAMOND_HELMET))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(25);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.side.desert-oasis.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Component line = Component.translatable("quest.side.desert-oasis.description." + i);
            description.add(line);
        }
        return description;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "talk_desert_nomad" -> Component.translatable("quest.side.desert-oasis.objectives.talk_desert_nomad");
            case "mirages_edge" -> Component.translatable("quest.side.desert-oasis.objectives.mirages_edge");
            case "kill_husks" -> Component.translatable("quest.side.desert-oasis.objectives.kill_husks");
            case "desert_blooms" -> Component.translatable("quest.side.desert-oasis.objectives.desert_blooms");
            case "hidden_oasis" -> Component.translatable("quest.side.desert-oasis.objectives.hidden_oasis");
            case "oasis_water" -> Component.translatable("quest.side.desert-oasis.objectives.oasis_water");
            default -> Component.translatable("quest.side.desert-oasis.objectives." + id);
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.side.desert-oasis.dialogs.0");
            case 1 -> Component.translatable("quest.side.desert-oasis.dialogs.1");
            case 2 -> Component.translatable("quest.side.desert-oasis.dialogs.2");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.side.desert-oasis.npc-name");
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.side.desert-oasis.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.side.desert-oasis.decline");
    }
}