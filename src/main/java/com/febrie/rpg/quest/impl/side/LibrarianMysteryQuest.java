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
     * 퀘스트 빌더
     */
    private static class LibrarianMysteryBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new LibrarianMysteryQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public LibrarianMysteryQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private LibrarianMysteryQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new LibrarianMysteryBuilder()
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
        return Component.translatable("quest.side.librarian-mystery.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Component line = Component.translatable("quest.side.librarian-mystery.description." + i);
            description.add(line);
        }
        return description;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "talk_head_librarian" -> Component.translatable("quest.side.librarian-mystery.objectives.talk_head_librarian");
            case "visit_ancient_archives" -> Component.translatable("quest.side.librarian-mystery.objectives.visit_ancient_archives");
            case "collect_missing_tome" -> Component.translatable("quest.side.librarian-mystery.objectives.collect_missing_tome");
            case "collect_cipher_key" -> Component.translatable("quest.side.librarian-mystery.objectives.collect_cipher_key");
            case "visit_secret_chamber" -> Component.translatable("quest.side.librarian-mystery.objectives.visit_secret_chamber");
            case "collect_forbidden_knowledge" -> Component.translatable("quest.side.librarian-mystery.objectives.collect_forbidden_knowledge");
            case "return_head_librarian" -> Component.translatable("quest.side.librarian-mystery.objectives.return_head_librarian");
            default -> Component.translatable("quest.side.librarian-mystery.objectives." + id);
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.side.librarian-mystery.dialogs.0");
            case 1 -> Component.translatable("quest.side.librarian-mystery.dialogs.1");
            case 2 -> Component.translatable("quest.side.librarian-mystery.dialogs.2");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.side.librarian-mystery.npc-name");
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.side.librarian-mystery.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.side.librarian-mystery.decline");
    }
}