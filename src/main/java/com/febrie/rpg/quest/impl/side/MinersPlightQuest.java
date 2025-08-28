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
 * Side Quest: Miner's Plight
 * Help the mine foreman rescue trapped miners from a collapsed mine shaft.
 *
 * @author Febrie
 */
public class MinersPlightQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class MinersPlightBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new MinersPlightQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public MinersPlightQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private MinersPlightQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new MinersPlightBuilder()
                .id(QuestID.SIDE_MINERS_PLIGHT)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_mine_foreman", "mine_foreman"),
                        new VisitLocationObjective("visit_collapsed_mine", "collapsed_mine"),
                        new CollectItemObjective("collect_support_beams", Material.OAK_LOG, 12),
                        new KillMobObjective("kill_cave_spiders", EntityType.CAVE_SPIDER, 20),
                        new VisitLocationObjective("visit_trapped_miners", "trapped_miners"),
                        new CollectItemObjective("collect_mining_equipment", Material.IRON_PICKAXE, 5),
                        new InteractNPCObjective("return_mine_foreman", "mine_foreman")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(3200)
                        .addCurrency(CurrencyType.GOLD, 800)
                        .addItem(new ItemStack(Material.DIAMOND_PICKAXE, 1))
                        .addItem(new ItemStack(Material.TORCH, 64))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(22);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.side.miners-plight.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Component line = Component.translatable("quest.side.miners-plight.description." + i);
            description.add(line);
        }
        return description;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "talk_mine_foreman" -> Component.translatable("quest.side.miners-plight.objectives.talk_mine_foreman");
            case "visit_collapsed_mine" -> Component.translatable("quest.side.miners-plight.objectives.visit_collapsed_mine");
            case "collect_support_beams" -> Component.translatable("quest.side.miners-plight.objectives.collect_support_beams");
            case "kill_cave_spiders" -> Component.translatable("quest.side.miners-plight.objectives.kill_cave_spiders");
            case "visit_trapped_miners" -> Component.translatable("quest.side.miners-plight.objectives.visit_trapped_miners");
            case "collect_mining_equipment" -> Component.translatable("quest.side.miners-plight.objectives.collect_mining_equipment");
            case "return_mine_foreman" -> Component.translatable("quest.side.miners-plight.objectives.return_mine_foreman");
            default -> Component.translatable("quest.side.miners-plight.objectives." + id);
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.side.miners-plight.dialogs.0");
            case 1 -> Component.translatable("quest.side.miners-plight.dialogs.1");
            case 2 -> Component.translatable("quest.side.miners-plight.dialogs.2");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.side.miners-plight.npc-name");
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.side.miners-plight.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.side.miners-plight.decline");
    }
}