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
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.lang.quest.side.DreamWalkerLangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * Side Quest: Dream Walker
 * Enter the dream realm to save trapped souls from nightmares
 *
 * @author Febrie
 */
public class DreamWalkerQuest extends Quest {

    /**
     * 기본 생성자
     */
    public DreamWalkerQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_DREAM_WALKER)
                .objectives(List.of(
                        new InteractNPCObjective("talk_dream_keeper", "dream_keeper"),
                        new VisitLocationObjective("sleeping_village", "Sleeping_Village"),
                        new CollectItemObjective("string_collect", Material.STRING, 15),
                        new CollectItemObjective("sweet_berries_collect", Material.SWEET_BERRIES, 10),
                        new VisitLocationObjective("dream_realm", "Dream_Realm"),
                        new CollectItemObjective("soul_torch_collect", Material.SOUL_TORCH, 8),
                        new CollectItemObjective("phantom_membrane_collect", Material.PHANTOM_MEMBRANE, 12),
                        new KillMobObjective("nightmare_lord", EntityType.PHANTOM, 1),
                        new InteractNPCObjective("awaken_villagers", "dream_keeper")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(2200)
                        .addCurrency(CurrencyType.GOLD, 550)
                        .addItem(new ItemStack(Material.PHANTOM_MEMBRANE, 5))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 2))
                        .addItem(new ItemStack(Material.RED_BED, 3))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(18);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(DreamWalkerLangKey.QUEST_SIDE_DREAM_WALKER_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(DreamWalkerLangKey.QUEST_SIDE_DREAM_WALKER_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_dream_keeper" -> LangManager.text(DreamWalkerLangKey.QUEST_SIDE_DREAM_WALKER_OBJECTIVES_TALK_DREAM_KEEPER, who);
            case "sleeping_village" -> LangManager.text(DreamWalkerLangKey.QUEST_SIDE_DREAM_WALKER_OBJECTIVES_SLEEPING_VILLAGE, who);
            case "string_collect" -> LangManager.text(DreamWalkerLangKey.QUEST_SIDE_DREAM_WALKER_OBJECTIVES_STRING_COLLECT, who);
            case "sweet_berries_collect" -> LangManager.text(DreamWalkerLangKey.QUEST_SIDE_DREAM_WALKER_OBJECTIVES_SWEET_BERRIES_COLLECT, who);
            case "dream_realm" -> LangManager.text(DreamWalkerLangKey.QUEST_SIDE_DREAM_WALKER_OBJECTIVES_DREAM_REALM, who);
            case "soul_torch_collect" -> LangManager.text(DreamWalkerLangKey.QUEST_SIDE_DREAM_WALKER_OBJECTIVES_SOUL_TORCH_COLLECT, who);
            case "phantom_membrane_collect" -> LangManager.text(DreamWalkerLangKey.QUEST_SIDE_DREAM_WALKER_OBJECTIVES_PHANTOM_MEMBRANE_COLLECT, who);
            case "nightmare_lord" -> LangManager.text(DreamWalkerLangKey.QUEST_SIDE_DREAM_WALKER_OBJECTIVES_NIGHTMARE_LORD, who);
            case "awaken_villagers" -> LangManager.text(DreamWalkerLangKey.QUEST_SIDE_DREAM_WALKER_OBJECTIVES_AWAKEN_VILLAGERS, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(DreamWalkerLangKey.QUEST_SIDE_DREAM_WALKER_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(DreamWalkerLangKey.QUEST_SIDE_DREAM_WALKER_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(DreamWalkerLangKey.QUEST_SIDE_DREAM_WALKER_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(DreamWalkerLangKey.QUEST_SIDE_DREAM_WALKER_DECLINE, who);
    }
}