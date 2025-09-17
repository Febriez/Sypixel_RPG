package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;

import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * Side Quest: Librarian Mystery
 * Help the head librarian solve the mystery of missing ancient tomes and forbidden texts.
 *
 * @author Febrie
 */
public class LibrarianMysteryQuest extends Quest {

    /**
     * 기본 생성자
     */
    public LibrarianMysteryQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_LIBRARIAN_MYSTERY)
                .objectives(List.of(
                        new InteractNPCObjective("talk_head_librarian", "head_librarian"),
                        new VisitLocationObjective("visit_ancient_archives", "ancient_archives"),
                        new CollectItemObjective("enchanted_book_collect", Material.ENCHANTED_BOOK, 3),
                        new CollectItemObjective("compass_collect", Material.COMPASS, 1),
                        new VisitLocationObjective("visit_secret_chamber", "secret_chamber"),
                        new CollectItemObjective("written_book_collect", Material.WRITTEN_BOOK, 2),
                        new InteractNPCObjective("return_head_librarian", "head_librarian")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(2200)
                        .addCurrency(CurrencyType.GOLD, 650)
                        .addItem(new ItemStack(Material.BOOKSHELF, 5))
                        .addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 15))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(16);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.librarian.mystery.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.librarian.mystery.info"), who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_head_librarian" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.librarian.mystery.objectives.talk.head.librarian"), who);
            case "visit_ancient_archives" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.librarian.mystery.objectives.visit.ancient.archives"), who);
            case "enchanted_book_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.librarian.mystery.objectives.enchanted.book.collect"), who);
            case "compass_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.librarian.mystery.objectives.compass.collect"), who);
            case "visit_secret_chamber" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.librarian.mystery.objectives.visit.secret.chamber"), who);
            case "written_book_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.librarian.mystery.objectives.written.book.collect"), who);
            case "return_head_librarian" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.librarian.mystery.objectives.return.head.librarian"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.librarian.mystery.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.librarian.mystery.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.librarian.mystery.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.librarian.mystery.decline"), who);
    }
}