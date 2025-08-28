package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
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
 * Blacksmith Apprentice - Side Quest
 * Help the master blacksmith by gathering materials and learning the basics of smithing.
 *
 * @author Febrie
 */
public class BlacksmithApprenticeQuest extends Quest {

    /**
     * 기본 생성자
     */
    public BlacksmithApprenticeQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_BLACKSMITH_APPRENTICE)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_master_blacksmith", "master_blacksmith"),
                        new CollectItemObjective("iron_ore", Material.IRON_ORE, 20),
                        new CollectItemObjective("coal", Material.COAL, 15),
                        new VisitLocationObjective("mining_site", "Mining_Site"),
                        new KillMobObjective("kill_skeletons", EntityType.SKELETON, 10),
                        new CollectItemObjective("refined_iron", Material.IRON_INGOT, 12),
                        new InteractNPCObjective("return_master_blacksmith", "master_blacksmith")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(1500)
                        .addCurrency(CurrencyType.GOLD, 400)
                        .addItem(new ItemStack(Material.IRON_SWORD))
                        .addItem(new ItemStack(Material.IRON_HELMET))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(12);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.side.blacksmith-apprentice.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Component line = Component.translatable("quest.side.blacksmith-apprentice.description." + i);
            description.add(line);
        }
        return description;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "talk_master_blacksmith" -> Component.translatable("quest.side.blacksmith-apprentice.objectives.talk_master_blacksmith");
            case "iron_ore" -> Component.translatable("quest.side.blacksmith-apprentice.objectives.iron_ore");
            case "coal" -> Component.translatable("quest.side.blacksmith-apprentice.objectives.coal");
            case "mining_site" -> Component.translatable("quest.side.blacksmith-apprentice.objectives.mining_site");
            case "kill_skeletons" -> Component.translatable("quest.side.blacksmith-apprentice.objectives.kill_skeletons");
            case "refined_iron" -> Component.translatable("quest.side.blacksmith-apprentice.objectives.refined_iron");
            case "return_master_blacksmith" -> Component.translatable("quest.side.blacksmith-apprentice.objectives.return_master_blacksmith");
            default -> Component.translatable("quest.side.blacksmith-apprentice.objectives." + id);
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.side.blacksmith-apprentice.dialogs.0");
            case 1 -> Component.translatable("quest.side.blacksmith-apprentice.dialogs.1");
            case 2 -> Component.translatable("quest.side.blacksmith-apprentice.dialogs.2");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.side.blacksmith-apprentice.npc-name");
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.side.blacksmith-apprentice.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.side.blacksmith-apprentice.decline");
    }
}