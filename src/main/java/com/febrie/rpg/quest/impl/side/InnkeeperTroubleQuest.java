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
 * Side Quest: Innkeeper Trouble
 * Help the innkeeper deal with pests in the basement and recover stolen supplies.
 *
 * @author Febrie
 */
public class InnkeeperTroubleQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class InnkeeperTroubleBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new InnkeeperTroubleQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public InnkeeperTroubleQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private InnkeeperTroubleQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new InnkeeperTroubleBuilder()
                .id(QuestID.SIDE_INNKEEPER_TROUBLE)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_worried_innkeeper", "worried_innkeeper"),
                        new VisitLocationObjective("visit_inn_basement", "inn_basement"),
                        new KillMobObjective("kill_spiders", EntityType.SPIDER, 12),
                        new CollectItemObjective("collect_inn_supplies", Material.BREAD, 20),
                        new CollectItemObjective("collect_ale_barrels", Material.BARREL, 3),
                        new VisitLocationObjective("visit_storage_room", "storage_room"),
                        new InteractNPCObjective("return_worried_innkeeper", "worried_innkeeper")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(1000)
                        .addCurrency(CurrencyType.GOLD, 250)
                        .addItem(new ItemStack(Material.BREAD, 10))
                        .addItem(new ItemStack(Material.COOKED_BEEF, 5))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(8);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.side.innkeeper-trouble.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Component line = Component.translatable("quest.side.innkeeper-trouble.description." + i);
            description.add(line);
        }
        return description;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "talk_worried_innkeeper" -> Component.translatable("quest.side.innkeeper-trouble.objectives.talk_worried_innkeeper");
            case "visit_inn_basement" -> Component.translatable("quest.side.innkeeper-trouble.objectives.visit_inn_basement");
            case "kill_spiders" -> Component.translatable("quest.side.innkeeper-trouble.objectives.kill_spiders");
            case "collect_inn_supplies" -> Component.translatable("quest.side.innkeeper-trouble.objectives.collect_inn_supplies");
            case "collect_ale_barrels" -> Component.translatable("quest.side.innkeeper-trouble.objectives.collect_ale_barrels");
            case "visit_storage_room" -> Component.translatable("quest.side.innkeeper-trouble.objectives.visit_storage_room");
            case "return_worried_innkeeper" -> Component.translatable("quest.side.innkeeper-trouble.objectives.return_worried_innkeeper");
            default -> Component.translatable("quest.side.innkeeper-trouble.objectives." + id);
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.side.innkeeper-trouble.dialogs.0");
            case 1 -> Component.translatable("quest.side.innkeeper-trouble.dialogs.1");
            case 2 -> Component.translatable("quest.side.innkeeper-trouble.dialogs.2");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.side.innkeeper-trouble.npc-name");
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.side.innkeeper-trouble.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.side.innkeeper-trouble.decline");
    }
}