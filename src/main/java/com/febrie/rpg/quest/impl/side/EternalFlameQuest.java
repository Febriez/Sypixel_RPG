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
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * Side Quest: Eternal Flame
 * Rekindle the eternal flame that protects the city from darkness
 *
 * @author Febrie
 */
public class EternalFlameQuest extends Quest {

    /**
     * 기본 생성자
     */
    public EternalFlameQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_ETERNAL_FLAME)
                .objectives(List.of(
                        new InteractNPCObjective("talk_flame_keeper", "flame_keeper"),
                        new VisitLocationObjective("ancient_brazier", "Ancient_Brazier"),
                        new CollectItemObjective("fire_charge_collect", Material.FIRE_CHARGE, 10),
                        new VisitLocationObjective("starlight_peak", "Starlight_Peak"),
                        new CollectItemObjective("glowstone_dust_collect", Material.GLOWSTONE_DUST, 15),
                        new CollectItemObjective("honey_bottle_collect", Material.HONEY_BOTTLE, 8),
                        new VisitLocationObjective("flame_altar", "Flame_Altar"),
                        new InteractNPCObjective("rekindle_flame", "flame_keeper")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(1900)
                        .addCurrency(CurrencyType.GOLD, 480)
                        .addItem(new ItemStack(Material.FIRE_CHARGE, 10))
                        .addItem(new ItemStack(Material.TORCH, 64))
                        .addItem(new ItemStack(Material.BEACON, 1))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(17);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.eternal.flame.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.eternal.flame.info"), who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_flame_keeper" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.eternal.flame.objectives.talk.flame.keeper"), who);
            case "ancient_brazier" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.eternal.flame.objectives.ancient.brazier"), who);
            case "fire_charge_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.eternal.flame.objectives.fire.charge.collect"), who);
            case "starlight_peak" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.eternal.flame.objectives.starlight.peak"), who);
            case "glowstone_dust_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.eternal.flame.objectives.glowstone.dust.collect"), who);
            case "honey_bottle_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.eternal.flame.objectives.honey.bottle.collect"), who);
            case "flame_altar" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.eternal.flame.objectives.flame.altar"), who);
            case "rekindle_flame" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.eternal.flame.objectives.rekindle.flame"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.eternal.flame.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.eternal.flame.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.eternal.flame.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.eternal.flame.decline"), who);
    }
}