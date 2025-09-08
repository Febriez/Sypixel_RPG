package com.febrie.rpg.quest.impl.seasonal;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.PlaceBlockObjective;
import com.febrie.rpg.quest.objective.impl.CraftItemObjective;
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

public class SpringFestivalQuest extends Quest {
    public SpringFestivalQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SEASON_SPRING_FESTIVAL)
                .objectives(List.of(
                        new CollectItemObjective("collect_flowers", Material.POPPY, 40),
                        new PlaceBlockObjective("plant_seeds", Material.WHEAT, 50),
                        new CraftItemObjective("craft_flower_crown", Material.LEATHER_HELMET, 5)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 200)
                        .addItem(new ItemStack(Material.BONE_MEAL, 64))
                        .addExperience(250)
                        .build())
                .category(QuestCategory.EVENT)
                .minLevel(5);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_SPRING_FESTIVAL_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SEASONAL_SPRING_FESTIVAL_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "collect_flowers" -> LangManager.list(LangKey.QUEST_SEASONAL_SPRING_FESTIVAL_OBJECTIVES_COLLECT_FLOWERS, who);
            case "plant_seeds" -> LangManager.list(LangKey.QUEST_SEASONAL_SPRING_FESTIVAL_OBJECTIVES_PLANT_SEEDS, who);
            case "craft_flower_crown" -> LangManager.list(LangKey.QUEST_SEASONAL_SPRING_FESTIVAL_OBJECTIVES_CRAFT_FLOWER_CROWN, who);
            default -> new ArrayList<>();
        };
    }
    
    @Override
    public int getDialogCount() { return 3; }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SEASONAL_SPRING_FESTIVAL_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_SPRING_FESTIVAL_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_SPRING_FESTIVAL_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_SPRING_FESTIVAL_DECLINE, who);
    }
}