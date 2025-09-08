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
                        new InteractNPCObjective("weapon_master", "legendary_smith", 1),
                        new CollectItemObjective("rare_materials", Material.NETHERITE_INGOT, 20),
                        new CollectItemObjective("dragon_scales", Material.DRAGON_BREATH, 10),
                        new KillMobObjective("worthy_opponents", EntityType.ENDER_DRAGON, 1),
                        new CraftItemObjective("legendary_weapon", Material.NETHERITE_SWORD, 1),
                        new InteractNPCObjective("ultimate_enchant", "enchanter", 1)
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
        return LangManager.text(LangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "weapon_master" -> LangManager.list(LangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_OBJECTIVES_WEAPON_MASTER, who);
            case "rare_materials" -> LangManager.list(LangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_OBJECTIVES_RARE_MATERIALS, who);
            case "dragon_scales" -> LangManager.list(LangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_OBJECTIVES_DRAGON_SCALES, who);
            case "worthy_opponents" -> LangManager.list(LangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_OBJECTIVES_WORTHY_OPPONENTS, who);
            case "legendary_weapon" -> LangManager.list(LangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_OBJECTIVES_LEGENDARY_WEAPON, who);
            case "ultimate_enchant" -> LangManager.list(LangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_OBJECTIVES_ULTIMATE_ENCHANT, who);
            default -> new ArrayList<>();
        };
    }

    @Override
    public int getDialogCount() { return 8; }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_LEGENDARY_WEAPON_DECLINE, who);
    }
}