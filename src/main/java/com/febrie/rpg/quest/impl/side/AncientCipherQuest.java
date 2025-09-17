package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import java.util.*;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

public class AncientCipherQuest extends Quest {
    public AncientCipherQuest() { super(createBuilder()); }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_ANCIENT_CIPHER)
                .objectives(List.of(
                        new InteractNPCObjective("scholar", "ancient_scholar"),
                        new CollectItemObjective("paper_collect", Material.PAPER, 10),
                        new VisitLocationObjective("study_ruins", "ancient_library"),
                        new DeliverItemObjective("book_deliver", Material.BOOK, 1, "ancient_scholar")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 300)
                        .addCurrency(CurrencyType.EMERALD, 8)
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK))
                        .addExperience(200)
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(15);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.ancient_cipher.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.ancient_cipher.info"), who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "scholar" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.ancient_cipher.objectives.scholar"), who);
            case "paper_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.ancient_cipher.objectives.paper_collect"), who);
            case "study_ruins" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.ancient_cipher.objectives.study_ruins"), who);
            case "book_deliver" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.ancient_cipher.objectives.book_deliver"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override public int getDialogCount() { return 7; }
    @Override public @NotNull List<Component> getDialogs(@NotNull Player who) { return LangManager.list(QuestCommonLangKey.fromString("quest.side.ancient_cipher.dialogs"), who); }
    @Override public @NotNull Component getDialog(int index, @NotNull Player who) { return getDialogs(who).get(index); }
    @Override public @NotNull Component getNPCName(@NotNull Player who) { return LangManager.text(QuestCommonLangKey.fromString("quest.side.ancient_cipher.npc_name"), who); }
    @Override public @NotNull Component getAcceptDialog(@NotNull Player who) { return LangManager.text(QuestCommonLangKey.fromString("quest.side.ancient_cipher.accept"), who); }
    @Override public @NotNull Component getDeclineDialog(@NotNull Player who) { return LangManager.text(QuestCommonLangKey.fromString("quest.side.ancient_cipher.decline"), who); }
}