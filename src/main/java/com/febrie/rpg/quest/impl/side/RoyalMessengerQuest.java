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
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.lang.quest.side.RoyalMessengerLangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

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
                .objectives(List.of(
                        new InteractNPCObjective("talk_royal_courier", "royal_courier"),
                        new CollectItemObjective("emerald_collect", Material.EMERALD, 1),
                        new VisitLocationObjective("visit_northern_outpost", "northern_outpost"),
                        new KillMobObjective("kill_bandits", EntityType.PILLAGER, 15), // Using PILLAGER instead of BANDIT
                        new CollectItemObjective("paper_collect", Material.PAPER, 1),
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
        return LangManager.text(RoyalMessengerLangKey.QUEST_SIDE_ROYAL_MESSENGER_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(RoyalMessengerLangKey.QUEST_SIDE_ROYAL_MESSENGER_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_royal_courier" -> LangManager.text(RoyalMessengerLangKey.QUEST_SIDE_ROYAL_MESSENGER_OBJECTIVES_TALK_ROYAL_COURIER, who);
            case "emerald_collect" -> LangManager.text(RoyalMessengerLangKey.QUEST_SIDE_ROYAL_MESSENGER_OBJECTIVES_EMERALD_COLLECT, who);
            case "visit_northern_outpost" -> LangManager.text(RoyalMessengerLangKey.QUEST_SIDE_ROYAL_MESSENGER_OBJECTIVES_VISIT_NORTHERN_OUTPOST, who);
            case "kill_bandits" -> LangManager.text(RoyalMessengerLangKey.QUEST_SIDE_ROYAL_MESSENGER_OBJECTIVES_KILL_BANDITS, who);
            case "paper_collect" -> LangManager.text(RoyalMessengerLangKey.QUEST_SIDE_ROYAL_MESSENGER_OBJECTIVES_PAPER_COLLECT, who);
            case "visit_royal_castle" -> LangManager.text(RoyalMessengerLangKey.QUEST_SIDE_ROYAL_MESSENGER_OBJECTIVES_VISIT_ROYAL_CASTLE, who);
            case "talk_castle_guard" -> LangManager.text(RoyalMessengerLangKey.QUEST_SIDE_ROYAL_MESSENGER_OBJECTIVES_TALK_CASTLE_GUARD, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(RoyalMessengerLangKey.QUEST_SIDE_ROYAL_MESSENGER_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(RoyalMessengerLangKey.QUEST_SIDE_ROYAL_MESSENGER_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(RoyalMessengerLangKey.QUEST_SIDE_ROYAL_MESSENGER_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(RoyalMessengerLangKey.QUEST_SIDE_ROYAL_MESSENGER_DECLINE, who);
    }
}