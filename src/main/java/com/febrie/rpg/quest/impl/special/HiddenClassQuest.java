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

import java.util.ArrayList;
import java.util.List;

public class HiddenClassQuest extends Quest {

    public HiddenClassQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SPECIAL_HIDDEN_CLASS)
                .objectives(List.of(
                        new InteractNPCObjective("class_master", "hidden_master", 1),
                        new CollectItemObjective("hidden_knowledge", Material.ENCHANTED_BOOK, 10),
                        new CompleteTrialObjective("mastery_trial", "class_trial"),
                        new UnlockAbilityObjective("hidden_abilities", "secret_skills")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 4000)
                        .addItem(new ItemStack(Material.NETHERITE_HELMET))
                        .addExperience(6000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SPECIAL)
                .minLevel(40);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_HIDDEN_CLASS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SPECIAL_HIDDEN_CLASS_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "class_master" -> LangManager.list(LangKey.QUEST_SPECIAL_HIDDEN_CLASS_OBJECTIVES_CLASS_MASTER, who);
            case "hidden_knowledge" -> LangManager.list(LangKey.QUEST_SPECIAL_HIDDEN_CLASS_OBJECTIVES_HIDDEN_KNOWLEDGE, who);
            case "mastery_trial" -> LangManager.list(LangKey.QUEST_SPECIAL_HIDDEN_CLASS_OBJECTIVES_MASTERY_TRIAL, who);
            case "hidden_abilities" -> LangManager.list(LangKey.QUEST_SPECIAL_HIDDEN_CLASS_OBJECTIVES_HIDDEN_ABILITIES, who);
            default -> new ArrayList<>();
        };
    }

    @Override
    public int getDialogCount() { return 6; }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SPECIAL_HIDDEN_CLASS_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_HIDDEN_CLASS_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_HIDDEN_CLASS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_HIDDEN_CLASS_DECLINE, who);
    }
}