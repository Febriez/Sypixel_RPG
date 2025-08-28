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
 * Crystal Cavern - Side Quest
 * Help a miner explore a crystal-filled cavern and harvest precious gems.
 *
 * @author Febrie
 */
public class CrystalCavernQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class CrystalCavernBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new CrystalCavernQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public CrystalCavernQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private CrystalCavernQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new CrystalCavernBuilder()
                .id(QuestID.SIDE_CRYSTAL_CAVERN)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_crystal_miner", "crystal_miner"),
                        new VisitLocationObjective("cavern_entrance", "Cavern_Entrance"),
                        new KillMobObjective("kill_spiders", EntityType.SPIDER, 18),
                        new CollectItemObjective("raw_crystals", Material.AMETHYST_SHARD, 20),
                        new VisitLocationObjective("crystal_chamber", "Crystal_Chamber"),
                        new CollectItemObjective("pure_crystal", Material.AMETHYST_CLUSTER, 5)
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(2200)
                        .addCurrency(CurrencyType.GOLD, 550)
                        .addItem(new ItemStack(Material.DIAMOND_PICKAXE))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(16);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.side.crystal-cavern.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Component line = Component.translatable("quest.side.crystal-cavern.description." + i);
            description.add(line);
        }
        return description;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "talk_crystal_miner" -> Component.translatable("quest.side.crystal-cavern.objectives.talk_crystal_miner");
            case "cavern_entrance" -> Component.translatable("quest.side.crystal-cavern.objectives.cavern_entrance");
            case "kill_spiders" -> Component.translatable("quest.side.crystal-cavern.objectives.kill_spiders");
            case "raw_crystals" -> Component.translatable("quest.side.crystal-cavern.objectives.raw_crystals");
            case "crystal_chamber" -> Component.translatable("quest.side.crystal-cavern.objectives.crystal_chamber");
            case "pure_crystal" -> Component.translatable("quest.side.crystal-cavern.objectives.pure_crystal");
            default -> Component.translatable("quest.side.crystal-cavern.objectives." + id);
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.side.crystal-cavern.dialogs.0");
            case 1 -> Component.translatable("quest.side.crystal-cavern.dialogs.1");
            case 2 -> Component.translatable("quest.side.crystal-cavern.dialogs.2");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.side.crystal-cavern.npc-name");
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.side.crystal-cavern.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.side.crystal-cavern.decline");
    }
}