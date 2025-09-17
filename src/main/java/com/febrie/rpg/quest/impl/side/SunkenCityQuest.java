package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
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
 * 침몰한 도시 - 사이드 퀘스트
 * 고대 침몰한 도시의 유적을 탐험하는 퀘스트
 *
 * @author Febrie
 */
public class SunkenCityQuest extends Quest {

    /**
     * 기본 생성자
     */
    public SunkenCityQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_SUNKEN_CITY)
                .objectives(List.of(
                        new InteractNPCObjective("deep_sea_diver", "deep_sea_diver"),
                        new VisitLocationObjective("underwater_ruins", "underwater_ruins"),
                        new KillMobObjective("kill_guardians", EntityType.GUARDIAN, 8),
                        new CollectItemObjective("prismarine_crystals_collect", Material.PRISMARINE_CRYSTALS, 12),
                        new VisitLocationObjective("sunken_palace", "sunken_palace"),
                        new CollectItemObjective("heart_of_the_sea_collect", Material.HEART_OF_THE_SEA, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 900)
                        .addItem(new ItemStack(Material.TRIDENT))
                        .addExperience(3500)
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(22);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.sunken.city.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.sunken.city.info"), who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "deep_sea_diver" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.sunken.city.objectives.deep.sea.diver"), who);
            case "underwater_ruins" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.sunken.city.objectives.underwater.ruins"), who);
            case "kill_guardians" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.sunken.city.objectives.kill.guardians"), who);
            case "prismarine_crystals_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.sunken.city.objectives.prismarine.crystals.collect"), who);
            case "sunken_palace" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.sunken.city.objectives.sunken.palace"), who);
            case "heart_of_the_sea_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.sunken.city.objectives.heart.of.the.sea.collect"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.sunken.city.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.sunken.city.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.sunken.city.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.sunken.city.decline"), who);
    }
}