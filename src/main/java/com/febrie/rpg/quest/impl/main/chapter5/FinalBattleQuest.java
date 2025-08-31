package com.febrie.rpg.quest.impl.main.chapter5;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangHelper;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
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
 * Chapter 5: Final Battle Quest
 * The ultimate confrontation with the forces of darkness
 *
 * @author Febrie
 */
public class FinalBattleQuest extends Quest {
    
    /**
     * Default constructor
     */
    public FinalBattleQuest() {
        super(createBuilder());
    }
    
    /**
     * Quest configuration
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_FINAL_BATTLE)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("chosen_champion", "chosen_champion"),
                        new VisitLocationObjective("battlefield_gates", "battlefield_gates_area"),
                        new KillMobObjective("kill_withers", EntityType.WITHER, 3),
                        new CollectItemObjective("heart_of_darkness", Material.WITHER_SKELETON_SKULL, 5),
                        new KillMobObjective("kill_ender_dragon", EntityType.ENDER_DRAGON, 1),
                        new CollectItemObjective("dragon_essence", Material.DRAGON_EGG, 1),
                        new VisitLocationObjective("void_nexus", "void_nexus_area"),
                        new KillMobObjective("kill_endermen", EntityType.ENDERMAN, 50),
                        new InteractNPCObjective("ancient_oracle", "ancient_oracle")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 10000)
                        .addItem(new ItemStack(Material.NETHERITE_BOOTS))
                        .addItem(new ItemStack(Material.ELYTRA))
                        .addItem(new ItemStack(Material.DRAGON_HEAD))
                        .addExperience(20000)
                        .build())
                .sequential(true)
                .category(QuestCategory.MAIN)
                .minLevel(44)
                .addPrerequisite(QuestID.MAIN_LAST_STAND);
    }
    
    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_MAIN_FINAL_BATTLE_NAME, who);
    }
    
    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_FINAL_BATTLE_INFO, who);
    }
    
    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.main.final_battle.objectives." + objective.getId(), who);
    }
    
    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangHelper.text(LangKey.QUEST_MAIN_FINAL_BATTLE_DIALOGS_0, who);
            case 1 -> LangHelper.text(LangKey.QUEST_MAIN_FINAL_BATTLE_DIALOGS_1, who);
            case 2 -> LangHelper.text(LangKey.QUEST_MAIN_FINAL_BATTLE_DIALOGS_2, who);
            case 3 -> LangHelper.text(LangKey.QUEST_MAIN_FINAL_BATTLE_DIALOGS_3, who);
            case 4 -> LangHelper.text(LangKey.QUEST_MAIN_FINAL_BATTLE_DIALOGS_4, who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_MAIN_FINAL_BATTLE_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_MAIN_FINAL_BATTLE_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_MAIN_FINAL_BATTLE_DECLINE, who);
    }
}