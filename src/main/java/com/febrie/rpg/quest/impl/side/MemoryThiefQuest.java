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

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * Side Quest: Memory Thief
 * Stop the entity stealing precious memories from townspeople
 *
 * @author Febrie
 */
public class MemoryThiefQuest extends Quest {

    /**
     * 기본 생성자
     */
    public MemoryThiefQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_MEMORY_THIEF)
                .objectives(List.of(
                        new InteractNPCObjective("talk_concerned_villager", "concerned_villager"),
                        new InteractNPCObjective("interview_victims", "memory_victim"),
                        new VisitLocationObjective("memory_shrine", "Memory_Shrine"),
                        new CollectItemObjective("prismarine_crystals_collect", Material.PRISMARINE_CRYSTALS, 12),
                        new CollectItemObjective("phantom_membrane_collect", Material.PHANTOM_MEMBRANE, 8),
                        new VisitLocationObjective("thiefs_lair", "Thiefs_Lair"),
                        new KillMobObjective("memory_thief", EntityType.ILLUSIONER, 1),
                        new CollectItemObjective("experience_bottle_collect", Material.EXPERIENCE_BOTTLE, 20),
                        new InteractNPCObjective("restore_memories", "village_elder")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(2100)
                        .addCurrency(CurrencyType.GOLD, 520)
                        .addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 15))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 2))
                        .addItem(new ItemStack(Material.PRISMARINE_CRYSTALS, 5))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(19);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.memory.thief.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.memory.thief.info"), who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_concerned_villager" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.memory.thief.objectives.talk.concerned.villager"), who);
            case "interview_victims" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.memory.thief.objectives.interview.victims"), who);
            case "memory_shrine" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.memory.thief.objectives.memory.shrine"), who);
            case "prismarine_crystals_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.memory.thief.objectives.prismarine.crystals.collect"), who);
            case "phantom_membrane_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.memory.thief.objectives.phantom.membrane.collect"), who);
            case "thiefs_lair" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.memory.thief.objectives.thiefs.lair"), who);
            case "memory_thief" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.memory.thief.objectives.memory.thief"), who);
            case "experience_bottle_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.memory.thief.objectives.experience.bottle.collect"), who);
            case "restore_memories" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.memory.thief.objectives.restore.memories"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.memory.thief.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.memory.thief.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.memory.thief.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.memory.thief.decline"), who);
    }
}