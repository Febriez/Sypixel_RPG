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
 * 화산 깊이 - 사이드 퀘스트
 * 위험한 화산 깊은 곳에서 연구자를 도와 용암 형성을 연구하는 퀘스트
 *
 * @author Febrie
 */
public class VolcanicDepthsQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class VolcanicDepthsBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new VolcanicDepthsQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public VolcanicDepthsQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private VolcanicDepthsQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new VolcanicDepthsBuilder()
                .id(QuestID.SIDE_VOLCANIC_DEPTHS)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("volcano_researcher", "volcano_researcher"),
                        new VisitLocationObjective("volcano_rim", "volcano_rim"),
                        new KillMobObjective("kill_magma_cubes", EntityType.MAGMA_CUBE, 25),
                        new CollectItemObjective("volcanic_glass", Material.OBSIDIAN, 15),
                        new VisitLocationObjective("lava_chamber", "lava_chamber"),
                        new CollectItemObjective("fire_essence", Material.MAGMA_CREAM, 10)
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
        return Component.translatable("quest.side.volcanic-depths.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Component line = Component.translatable("quest.side.volcanic-depths.description." + i);
            description.add(line);
        }
        return description;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "volcano_researcher" -> Component.translatable("quest.side.volcanic-depths.objectives.volcano_researcher");
            case "volcano_rim" -> Component.translatable("quest.side.volcanic-depths.objectives.volcano_rim");
            case "kill_magma_cubes" -> Component.translatable("quest.side.volcanic-depths.objectives.kill_magma_cubes");
            case "volcanic_glass" -> Component.translatable("quest.side.volcanic-depths.objectives.volcanic_glass");
            case "lava_chamber" -> Component.translatable("quest.side.volcanic-depths.objectives.lava_chamber");
            case "fire_essence" -> Component.translatable("quest.side.volcanic-depths.objectives.fire_essence");
            default -> Component.translatable("quest.side.volcanic-depths.objectives." + id);
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.side.volcanic-depths.dialogs.0");
            case 1 -> Component.translatable("quest.side.volcanic-depths.dialogs.1");
            case 2 -> Component.translatable("quest.side.volcanic-depths.dialogs.2");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.side.volcanic-depths.npc-name");
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.side.volcanic-depths.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.side.volcanic-depths.decline");
    }
}