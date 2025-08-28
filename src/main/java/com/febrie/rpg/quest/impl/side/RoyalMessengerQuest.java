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
 * Side Quest: Royal Messenger
 * Deliver urgent royal messages across dangerous territories for the kingdom.
 *
 * @author Febrie
 */
public class RoyalMessengerQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class RoyalMessengerBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new RoyalMessengerQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public RoyalMessengerQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private RoyalMessengerQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new RoyalMessengerBuilder()
                .id(QuestID.SIDE_ROYAL_MESSENGER)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_royal_courier", "royal_courier"),
                        new CollectItemObjective("collect_royal_seal", Material.EMERALD, 1),
                        new VisitLocationObjective("visit_northern_outpost", "northern_outpost"),
                        new KillMobObjective("kill_bandits", EntityType.PILLAGER, 15), // Using PILLAGER instead of BANDIT
                        new CollectItemObjective("collect_urgent_message", Material.PAPER, 1),
                        new VisitLocationObjective("visit_royal_castle", "royal_castle"),
                        new InteractNPCObjective("talk_castle_guard", "castle_guard")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(3000)
                        .addCurrency(CurrencyType.GOLD, 900)
                        .addItem(new ItemStack(Material.GOLDEN_HORSE_ARMOR, 1))
                        .addItem(new ItemStack(Material.SADDLE, 1))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(20);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.side.royal-messenger.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Component line = Component.translatable("quest.side.royal-messenger.description." + i);
            description.add(line);
        }
        return description;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "talk_royal_courier" -> Component.translatable("quest.side.royal-messenger.objectives.talk_royal_courier");
            case "collect_royal_seal" -> Component.translatable("quest.side.royal-messenger.objectives.collect_royal_seal");
            case "visit_northern_outpost" -> Component.translatable("quest.side.royal-messenger.objectives.visit_northern_outpost");
            case "kill_bandits" -> Component.translatable("quest.side.royal-messenger.objectives.kill_bandits");
            case "collect_urgent_message" -> Component.translatable("quest.side.royal-messenger.objectives.collect_urgent_message");
            case "visit_royal_castle" -> Component.translatable("quest.side.royal-messenger.objectives.visit_royal_castle");
            case "talk_castle_guard" -> Component.translatable("quest.side.royal-messenger.objectives.talk_castle_guard");
            default -> Component.translatable("quest.side.royal-messenger.objectives." + id);
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.side.royal-messenger.dialogs.0");
            case 1 -> Component.translatable("quest.side.royal-messenger.dialogs.1");
            case 2 -> Component.translatable("quest.side.royal-messenger.dialogs.2");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.side.royal-messenger.npc-name");
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.side.royal-messenger.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.side.royal-messenger.decline");
    }
}