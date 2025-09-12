package com.febrie.rpg.quest.impl.guild;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.DeliverItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import java.util.ArrayList;
import java.util.List;

/**
 * 자원 전쟁 - 길드 퀘스트
 * 길드간 자원을 두고 벌이는 경쟁 퀘스트
 *
 * @author Febrie
 */
public class ResourceWarQuest extends Quest {

    /**
     * 기본 생성자
     */
    public ResourceWarQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.GUILD_RESOURCE_WAR)
                .objectives(List.of(
                        new VisitLocationObjective("control_mining_nodes", "mining_node"),
                        new CollectItemObjective("netherite_ingot_collect", Material.NETHERITE_INGOT, 20),
                        new KillMobObjective("defend_resource_nodes", EntityType.ZOMBIE, 15),
                        new DeliverItemObjective("gold_ingot_deliver", Material.GOLD_INGOT, 32, "guild_treasurer"),
                        new VisitLocationObjective("control_strategic_locations", "strategic_location")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 1200)
                        .addItem(new ItemStack(Material.DIAMOND_PICKAXE, 3))
                        .addItem(new ItemStack(Material.SHULKER_BOX, 5))
                        .addItem(new ItemStack(Material.ANCIENT_DEBRIS, 10))
                        .addExperience(1800)
                        .build())
                .sequential(false)
                .category(QuestCategory.GUILD)
                .minLevel(28);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_GUILD_RESOURCE_WAR_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_GUILD_RESOURCE_WAR_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "control_mining_nodes" -> LangManager.text(QuestCommonLangKey.QUEST_GUILD_RESOURCE_WAR_OBJECTIVES_CONTROL_MINING_NODES, who);
            case "netherite_ingot_collect" -> LangManager.text(QuestCommonLangKey.QUEST_GUILD_RESOURCE_WAR_OBJECTIVES_NETHERITE_INGOT_COLLECT, who);
            case "defend_resource_nodes" -> LangManager.text(QuestCommonLangKey.QUEST_GUILD_RESOURCE_WAR_OBJECTIVES_DEFEND_RESOURCE_NODES, who);
            case "gold_ingot_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_GUILD_RESOURCE_WAR_OBJECTIVES_GOLD_INGOT_DELIVER, who);
            case "control_strategic_locations" -> LangManager.text(QuestCommonLangKey.QUEST_GUILD_RESOURCE_WAR_OBJECTIVES_CONTROL_STRATEGIC_LOCATIONS, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_GUILD_RESOURCE_WAR_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_GUILD_RESOURCE_WAR_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_GUILD_RESOURCE_WAR_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_GUILD_RESOURCE_WAR_DECLINE, who);
    }
}