package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.lang.quest.side.ElementalChaosLangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * Side Quest: Elemental Chaos
 * Restore balance between the elemental planes
 *
 * @author Febrie
 */
public class ElementalChaosQuest extends Quest {

    /**
     * 기본 생성자
     */
    public ElementalChaosQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_ELEMENTAL_CHAOS)
                .objectives(List.of(
                        new InteractNPCObjective("talk_elemental_sage", "elemental_sage"),
                        new VisitLocationObjective("fire_rift", "Fire_Rift"),
                        new CollectItemObjective("blaze_rod_collect", Material.BLAZE_ROD, 8),
                        new VisitLocationObjective("water_rift", "Water_Rift"),
                        new CollectItemObjective("prismarine_shard_collect", Material.PRISMARINE_SHARD, 8),
                        new VisitLocationObjective("earth_rift", "Earth_Rift"),
                        new CollectItemObjective("emerald_collect", Material.EMERALD, 8),
                        new VisitLocationObjective("air_rift", "Air_Rift"),
                        new CollectItemObjective("feather_collect", Material.FEATHER, 8),
                        new KillMobObjective("elemental_guardians", EntityType.IRON_GOLEM, 4),
                        new InteractNPCObjective("restore_balance", "elemental_sage")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(2000)
                        .addCurrency(CurrencyType.GOLD, 500)
                        .addItem(new ItemStack(Material.CONDUIT, 1))
                        .addItem(new ItemStack(Material.BEACON, 1))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 3))
                        .build())
                .sequential(false)
                .category(QuestCategory.SIDE)
                .minLevel(20);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(ElementalChaosLangKey.QUEST_SIDE_ELEMENTAL_CHAOS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(ElementalChaosLangKey.QUEST_SIDE_ELEMENTAL_CHAOS_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_elemental_sage" -> LangManager.text(ElementalChaosLangKey.QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_TALK_ELEMENTAL_SAGE, who);
            case "fire_rift" -> LangManager.text(ElementalChaosLangKey.QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_FIRE_RIFT, who);
            case "blaze_rod_collect" -> LangManager.text(ElementalChaosLangKey.QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_BLAZE_ROD_COLLECT, who);
            case "water_rift" -> LangManager.text(ElementalChaosLangKey.QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_WATER_RIFT, who);
            case "prismarine_shard_collect" -> LangManager.text(ElementalChaosLangKey.QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_PRISMARINE_SHARD_COLLECT, who);
            case "earth_rift" -> LangManager.text(ElementalChaosLangKey.QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_EARTH_RIFT, who);
            case "emerald_collect" -> LangManager.text(ElementalChaosLangKey.QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_EMERALD_COLLECT, who);
            case "air_rift" -> LangManager.text(ElementalChaosLangKey.QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_AIR_RIFT, who);
            case "feather_collect" -> LangManager.text(ElementalChaosLangKey.QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_FEATHER_COLLECT, who);
            case "elemental_guardians" -> LangManager.text(ElementalChaosLangKey.QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_ELEMENTAL_GUARDIANS, who);
            case "restore_balance" -> LangManager.text(ElementalChaosLangKey.QUEST_SIDE_ELEMENTAL_CHAOS_OBJECTIVES_RESTORE_BALANCE, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(ElementalChaosLangKey.QUEST_SIDE_ELEMENTAL_CHAOS_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(ElementalChaosLangKey.QUEST_SIDE_ELEMENTAL_CHAOS_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(ElementalChaosLangKey.QUEST_SIDE_ELEMENTAL_CHAOS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(ElementalChaosLangKey.QUEST_SIDE_ELEMENTAL_CHAOS_DECLINE, who);
    }
}