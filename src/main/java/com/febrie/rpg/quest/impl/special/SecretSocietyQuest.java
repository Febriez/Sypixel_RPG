package com.febrie.rpg.quest.impl.special;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import java.util.ArrayList;
import java.util.List;

public class SecretSocietyQuest extends Quest {

    public SecretSocietyQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SPECIAL_SECRET_SOCIETY)
                .objectives(List.of(
                        new InteractNPCObjective("secret_contact", "shadow_agent"),
                        new SurviveObjective("initiation_test", 600), // 10-minute loyalty trial
                        new CollectItemObjective("written_book_collect", Material.WRITTEN_BOOK, 8),
                        new VisitLocationObjective("covert_operations", "stealth_missions"),
                        new InteractNPCObjective("society_membership", "shadow_guild")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 4500)
                        .addItem(new ItemStack(Material.LEATHER_CHESTPLATE)) // 그림자 갑옷
                        .addExperience(6500)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SPECIAL)
                .minLevel(30);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_SECRET_SOCIETY_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SPECIAL_SECRET_SOCIETY_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "secret_contact" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_SECRET_SOCIETY_OBJECTIVES_SECRET_CONTACT, who);
            case "initiation_test" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_SECRET_SOCIETY_OBJECTIVES_INITIATION_TEST, who);
            case "written_book_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_SECRET_SOCIETY_OBJECTIVES_WRITTEN_BOOK_COLLECT, who);
            case "covert_operations" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_SECRET_SOCIETY_OBJECTIVES_COVERT_OPERATIONS, who);
            case "society_membership" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_SECRET_SOCIETY_OBJECTIVES_SOCIETY_MEMBERSHIP, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() { return 8; }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SPECIAL_SECRET_SOCIETY_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_SECRET_SOCIETY_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_SECRET_SOCIETY_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_SECRET_SOCIETY_DECLINE, who);
    }
}