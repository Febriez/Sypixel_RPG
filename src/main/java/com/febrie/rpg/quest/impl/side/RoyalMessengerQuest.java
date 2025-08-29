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
     * 기본 생성자
     */
    public RoyalMessengerQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
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
        return LangManager.get("quest.side.royal_messenger.name", who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.side.royal_messenger.info", who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.side.royal_messenger.objectives." + objective.getId(), who);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.side.royal_messenger.dialogs.0", who);
            case 1 -> LangManager.get("quest.side.royal_messenger.dialogs.1", who);
            case 2 -> LangManager.get("quest.side.royal_messenger.dialogs.2", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.side.royal_messenger.npc_name", who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.side.royal_messenger.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.side.royal_messenger.decline", who);
    }
}