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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import java.util.ArrayList;
import java.util.List;

public class MythicBeastQuest extends Quest {

    public MythicBeastQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SPECIAL_MYTHIC_BEAST)
                .objectives(List.of(
                        new InteractNPCObjective("beast_hunter", "legendary_hunter"),
                        new VisitLocationObjective("mythic_lair", "ancient_beast_den"),
                        new KillMobObjective("mythic_beast", EntityType.WITHER, 1),
                        new CollectItemObjective("nether_star_collect", Material.NETHER_STAR, 3),
                        new InteractNPCObjective("tame_companion", "mythic_pet")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 6000)
                        .addItem(new ItemStack(Material.DRAGON_EGG))
                        .addExperience(8000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SPECIAL)
                .minLevel(50);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_MYTHIC_BEAST_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SPECIAL_MYTHIC_BEAST_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "beast_hunter" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_MYTHIC_BEAST_OBJECTIVES_BEAST_HUNTER, who);
            case "mythic_lair" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_MYTHIC_BEAST_OBJECTIVES_MYTHIC_LAIR, who);
            case "mythic_beast" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_MYTHIC_BEAST_OBJECTIVES_MYTHIC_BEAST, who);
            case "nether_star_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_MYTHIC_BEAST_OBJECTIVES_NETHER_STAR_COLLECT, who);
            case "tame_companion" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_MYTHIC_BEAST_OBJECTIVES_TAME_COMPANION, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() { return 7; }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SPECIAL_MYTHIC_BEAST_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_MYTHIC_BEAST_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_MYTHIC_BEAST_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_MYTHIC_BEAST_DECLINE, who);
    }
}