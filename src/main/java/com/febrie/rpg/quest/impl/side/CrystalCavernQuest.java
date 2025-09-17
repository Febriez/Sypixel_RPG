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

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

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
                .objectives(List.of(
                        new InteractNPCObjective("talk_crystal_miner", "crystal_miner"),
                        new VisitLocationObjective("cavern_entrance", "Cavern_Entrance"),
                        new KillMobObjective("kill_spiders", EntityType.SPIDER, 18),
                        new CollectItemObjective("amethyst_shard_collect", Material.AMETHYST_SHARD, 20),
                        new VisitLocationObjective("crystal_chamber", "Crystal_Chamber"),
                        new CollectItemObjective("amethyst_cluster_collect", Material.AMETHYST_CLUSTER, 5)
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
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.crystal.cavern.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.crystal.cavern.info"), who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_crystal_miner" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.crystal.cavern.objectives.talk.crystal.miner"), who);
            case "cavern_entrance" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.crystal.cavern.objectives.cavern.entrance"), who);
            case "kill_spiders" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.crystal.cavern.objectives.kill.spiders"), who);
            case "amethyst_shard_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.crystal.cavern.objectives.amethyst.shard.collect"), who);
            case "crystal_chamber" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.crystal.cavern.objectives.crystal.chamber"), who);
            case "amethyst_cluster_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.crystal.cavern.objectives.amethyst.cluster.collect"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.crystal.cavern.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.crystal.cavern.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.crystal.cavern.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.crystal.cavern.decline"), who);
    }
}