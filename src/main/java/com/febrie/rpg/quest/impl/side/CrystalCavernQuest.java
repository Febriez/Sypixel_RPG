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
 * Crystal Cavern - Side Quest
 * Help a miner explore a crystal-filled cavern and harvest precious gems.
 *
 * @author Febrie
 */
public class CrystalCavernQuest extends Quest {

    /**
     * 기본 생성자
     */
    public CrystalCavernQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
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
        return LangManager.get("quest.side.crystal_cavern.name", who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.side.crystal_cavern.info", who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.side.crystal_cavern.objectives." + objective.getId(), who);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.side.crystal_cavern.dialogs.0", who);
            case 1 -> LangManager.get("quest.side.crystal_cavern.dialogs.1", who);
            case 2 -> LangManager.get("quest.side.crystal_cavern.dialogs.2", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.side.crystal_cavern.npc_name", who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.side.crystal_cavern.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.side.crystal_cavern.decline", who);
    }
}