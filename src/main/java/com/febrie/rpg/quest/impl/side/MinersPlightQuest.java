package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
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
 * Side Quest: Miner's Plight
 * Help the mine foreman rescue trapped miners from a collapsed mine shaft.
 *
 * @author Febrie
 */
public class MinersPlightQuest extends Quest {

    /**
     * 기본 생성자
     */
    public MinersPlightQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_MINERS_PLIGHT)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_mine_foreman", "mine_foreman"),
                        new VisitLocationObjective("visit_collapsed_mine", "collapsed_mine"),
                        new CollectItemObjective("collect_support_beams", Material.OAK_LOG, 12),
                        new KillMobObjective("kill_cave_spiders", EntityType.CAVE_SPIDER, 20),
                        new VisitLocationObjective("visit_trapped_miners", "trapped_miners"),
                        new CollectItemObjective("collect_mining_equipment", Material.IRON_PICKAXE, 5),
                        new InteractNPCObjective("return_mine_foreman", "mine_foreman")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(3200)
                        .addCurrency(CurrencyType.GOLD, 800)
                        .addItem(new ItemStack(Material.DIAMOND_PICKAXE, 1))
                        .addItem(new ItemStack(Material.TORCH, 64))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(22);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.get("quest.side.miners_plight.name", who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.side.miners_plight.info", who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.side.miners_plight.objectives." + objective.getId(), who);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.side.miners_plight.dialogs.0", who);
            case 1 -> LangManager.get("quest.side.miners_plight.dialogs.1", who);
            case 2 -> LangManager.get("quest.side.miners_plight.dialogs.2", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.side.miners_plight.npc_name", who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.side.miners_plight.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.side.miners_plight.decline", who);
    }
}