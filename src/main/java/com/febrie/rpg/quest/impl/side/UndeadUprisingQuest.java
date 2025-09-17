package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * Side Quest: Undead Uprising
 * Stop the necromancer and put the undead to rest
 *
 * @author Febrie
 */
public class UndeadUprisingQuest extends Quest {

    /**
     * 기본 생성자
     */
    public UndeadUprisingQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_UNDEAD_UPRISING)
                .objectives(List.of(
                        new InteractNPCObjective("talk_town_guard", "town_guard"),
                        new VisitLocationObjective("cursed_graveyard", "Cursed_Graveyard"),
                        new KillMobObjective("undead_creatures", EntityType.ZOMBIE, 15),
                        new KillMobObjective("skeleton_warriors", EntityType.SKELETON, 12),
                        new CollectItemObjective("potion_collect", Material.POTION, 5),
                        new VisitLocationObjective("necromancer_tower", "Necromancer_Tower"),
                        new KillMobObjective("dark_necromancer", EntityType.WITCH, 1),
                        new CollectItemObjective("enchanted_book_collect", Material.ENCHANTED_BOOK, 1),
                        new InteractNPCObjective("purify_graveyard", "town_priest")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(2100)
                        .addCurrency(CurrencyType.GOLD, 520)
                        .addItem(new ItemStack(Material.DIAMOND_SWORD, 1))
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 5))
                        .addItem(new ItemStack(Material.SHIELD, 1))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(17);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.undead.uprising.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.undead.uprising.info"), who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_town_guard" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.undead.uprising.objectives.talk.town.guard"), who);
            case "cursed_graveyard" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.undead.uprising.objectives.cursed.graveyard"), who);
            case "undead_creatures" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.undead.uprising.objectives.undead.creatures"), who);
            case "skeleton_warriors" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.undead.uprising.objectives.skeleton.warriors"), who);
            case "potion_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.undead.uprising.objectives.potion.collect"), who);
            case "necromancer_tower" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.undead.uprising.objectives.necromancer.tower"), who);
            case "dark_necromancer" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.undead.uprising.objectives.dark.necromancer"), who);
            case "enchanted_book_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.undead.uprising.objectives.enchanted.book.collect"), who);
            case "purify_graveyard" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.undead.uprising.objectives.purify.graveyard"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.undead.uprising.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.undead.uprising.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.undead.uprising.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.undead.uprising.decline"), who);
    }
}