package com.febrie.rpg.quest.impl.main.chapter5;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Chapter 5: Sacrifice of Heroes Quest
 * Honor the fallen and ensure their sacrifice was not in vain
 *
 * @author Febrie
 */
public class SacrificeOfHeroesQuest extends Quest {
    
    /**
     * Quest builder
     */
    private static class SacrificeOfHeroesBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new SacrificeOfHeroesQuest(this);
        }
    }
    
    /**
     * Default constructor
     */
    public SacrificeOfHeroesQuest() {
        this(createBuilder());
    }
    
    /**
     * Builder constructor
     */
    private SacrificeOfHeroesQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new SacrificeOfHeroesBuilder()
                .id(QuestID.MAIN_SACRIFICE_OF_HEROES)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("memorial_keeper", "memorial_keeper"),
                        new VisitLocationObjective("heroes_memorial", "heroes_memorial_area"),
                        new CollectItemObjective("hero_medallion", Material.GOLDEN_APPLE, 10),
                        new VisitLocationObjective("eternal_flame_altar", "eternal_flame_altar_area"),
                        new CollectItemObjective("essence_of_valor", Material.GLOWSTONE_DUST, 50),
                        new KillMobObjective("kill_vexes", EntityType.VEX, 30),
                        new CollectItemObjective("spirit_crystal", Material.SOUL_LANTERN, 15),
                        new VisitLocationObjective("sanctuary_of_light", "sanctuary_of_light_area"),
                        new InteractNPCObjective("spirit_guide", "spirit_guide")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 12500)
                        .addItem(new ItemStack(Material.NETHERITE_SWORD))
                        .addItem(new ItemStack(Material.BEACON, 2))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 10))
                        .addExperience(25000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(46)
                .addPrerequisite(QuestID.MAIN_FINAL_BATTLE);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.main.sacrifice-of-heroes.name");
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            description.add(Component.translatable("quest.main.sacrifice-of-heroes.description." + i));
        }
        return description;
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return Component.translatable("quest.main.sacrifice-of-heroes.objectives." + objective.getId());
    }
    
    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.main.sacrifice-of-heroes.dialogs.0");
            case 1 -> Component.translatable("quest.main.sacrifice-of-heroes.dialogs.1");
            case 2 -> Component.translatable("quest.main.sacrifice-of-heroes.dialogs.2");
            case 3 -> Component.translatable("quest.main.sacrifice-of-heroes.dialogs.3");
            case 4 -> Component.translatable("quest.main.sacrifice-of-heroes.dialogs.4");
            case 5 -> Component.translatable("quest.main.sacrifice-of-heroes.dialogs.5");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.main.sacrifice-of-heroes.npc-name");
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.main.sacrifice-of-heroes.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.main.sacrifice-of-heroes.decline");
    }
}