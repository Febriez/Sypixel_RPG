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
import com.febrie.rpg.util.LangHelper;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_head_librarian", "head_librarian"),
                        new VisitLocationObjective("visit_ancient_archives", "ancient_archives"),
                        new CollectItemObjective("collect_missing_tome", Material.ENCHANTED_BOOK, 3),
                        new CollectItemObjective("collect_cipher_key", Material.COMPASS, 1),
                        new VisitLocationObjective("visit_secret_chamber", "secret_chamber"),
                        new CollectItemObjective("collect_forbidden_knowledge", Material.WRITTEN_BOOK, 2),
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
        return LangHelper.text(LangKey.QUEST_SIDE_LIBRARIAN_MYSTERY_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SIDE_LIBRARIAN_MYSTERY_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.side.librarian_mystery.objectives." + objective.getId(), who);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangHelper.text(LangKey.QUEST_SIDE_LIBRARIAN_MYSTERY_DIALOGS_0, who);
            case 1 -> LangHelper.text(LangKey.QUEST_SIDE_LIBRARIAN_MYSTERY_DIALOGS_1, who);
            case 2 -> LangHelper.text(LangKey.QUEST_SIDE_LIBRARIAN_MYSTERY_DIALOGS_2, who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_SIDE_LIBRARIAN_MYSTERY_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_SIDE_LIBRARIAN_MYSTERY_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_SIDE_LIBRARIAN_MYSTERY_DECLINE, who);
    }
}