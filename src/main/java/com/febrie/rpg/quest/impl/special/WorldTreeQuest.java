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

public class WorldTreeQuest extends Quest {

    public WorldTreeQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SPECIAL_WORLD_TREE)
                .objectives(List.of(
                        new InteractNPCObjective("tree_guardian", "ancient_druid", 1),
                        new VisitLocationObjective("world_tree", "yggdrasil_location"),
                        new CollectItemObjective("life_essence", Material.GLOW_BERRIES, 50),
                        new PlantWorldTreeObjective("plant_sapling", "world_tree_seed"),
                        new NurtureTreeObjective("care_for_tree", "world_tree_growth", 30), // 30일
                        new ProtectFromEvilObjective("defend_tree", EntityType.WITHER_SKELETON, 100),
                        new HarvestWorldTreeObjective("tree_blessing", "world_tree_fruit")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 8000)
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 20)) // 세계수 열매
                        .addItem(new ItemStack(Material.OAK_SAPLING, 64)) // 세계수 묘목
                        .addExperience(12000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SPECIAL)
                .minLevel(60);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_WORLD_TREE_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SPECIAL_WORLD_TREE_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "tree_guardian" -> LangManager.list(LangKey.QUEST_SPECIAL_WORLD_TREE_OBJECTIVES_TREE_GUARDIAN, who);
            case "world_tree" -> LangManager.list(LangKey.QUEST_SPECIAL_WORLD_TREE_OBJECTIVES_WORLD_TREE, who);
            case "life_essence" -> LangManager.list(LangKey.QUEST_SPECIAL_WORLD_TREE_OBJECTIVES_LIFE_ESSENCE, who);
            case "plant_sapling" -> LangManager.list(LangKey.QUEST_SPECIAL_WORLD_TREE_OBJECTIVES_PLANT_SAPLING, who);
            case "care_for_tree" -> LangManager.list(LangKey.QUEST_SPECIAL_WORLD_TREE_OBJECTIVES_CARE_FOR_TREE, who);
            case "defend_tree" -> LangManager.list(LangKey.QUEST_SPECIAL_WORLD_TREE_OBJECTIVES_DEFEND_TREE, who);
            case "tree_blessing" -> LangManager.list(LangKey.QUEST_SPECIAL_WORLD_TREE_OBJECTIVES_TREE_BLESSING, who);
            default -> new ArrayList<>();
        };
    }

    @Override
    public int getDialogCount() { return 10; }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SPECIAL_WORLD_TREE_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_WORLD_TREE_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_WORLD_TREE_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SPECIAL_WORLD_TREE_DECLINE, who);
    }
}