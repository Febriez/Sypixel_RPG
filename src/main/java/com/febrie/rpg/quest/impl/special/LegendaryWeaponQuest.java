package com.febrie.rpg.quest.impl.special;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
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

public class LegendaryWeaponQuest extends Quest {

    public LegendaryWeaponQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SPECIAL_LEGENDARY_WEAPON)
                .objectives(List.of(
                        new InteractNPCObjective("weapon_master", "legendary_smith"),
                        new CollectItemObjective("netherite_ingot_collect", Material.NETHERITE_INGOT, 20),
                        new CollectItemObjective("dragon_breath_collect", Material.DRAGON_BREATH, 10),
                        new KillMobObjective("worthy_opponents", EntityType.ENDER_DRAGON, 1),
                        new CraftItemObjective("netherite_sword_craft", Material.NETHERITE_SWORD, 1),
                        new InteractNPCObjective("ultimate_enchant", "enchanter")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 5000)
                        .addItem(new ItemStack(Material.NETHERITE_SWORD))
                        .addExperience(7000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SPECIAL)
                .minLevel(45);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "weapon_master" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_OBJECTIVES_WEAPON_MASTER, who);
            case "netherite_ingot_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_OBJECTIVES_NETHERITE_INGOT_COLLECT, who);
            case "dragon_breath_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_OBJECTIVES_DRAGON_BREATH_COLLECT, who);
            case "worthy_opponents" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_OBJECTIVES_WORTHY_OPPONENTS, who);
            case "netherite_sword_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_OBJECTIVES_NETHERITE_SWORD_CRAFT, who);
            case "ultimate_enchant" -> LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_OBJECTIVES_ULTIMATE_ENCHANT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() { return 8; }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_DECLINE, who);
    }
}