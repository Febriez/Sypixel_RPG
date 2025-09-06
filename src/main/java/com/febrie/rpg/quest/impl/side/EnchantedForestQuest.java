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

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * Enchanted Forest - Side Quest
 * Help a druid restore balance to an enchanted forest plagued by dark magic.
 *
 * @author Febrie
 */
public class EnchantedForestQuest extends Quest {

    /**
     * 기본 생성자
     */
    public EnchantedForestQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_ENCHANTED_FOREST)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_forest_druid", "forest_druid"),
                        new VisitLocationObjective("magical_grove", "Magical_Grove"),
                        new KillMobObjective("kill_witches", EntityType.WITCH, 6),
                        new CollectItemObjective("enchanted_saplings", Material.OAK_SAPLING, 8),
                        new VisitLocationObjective("fairy_circle", "Fairy_Circle"),
                        new CollectItemObjective("fairy_dust", Material.GLOWSTONE_DUST, 15)
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(1800)
                        .addCurrency(CurrencyType.GOLD, 450)
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(14);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_ENCHANTED_FOREST_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SIDE_ENCHANTED_FOREST_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.side.enchanted_forest.objectives." + objective.getId(), who);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(LangKey.QUEST_SIDE_ENCHANTED_FOREST_DIALOGS, who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_ENCHANTED_FOREST_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_ENCHANTED_FOREST_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_ENCHANTED_FOREST_DECLINE, who);
    }
}