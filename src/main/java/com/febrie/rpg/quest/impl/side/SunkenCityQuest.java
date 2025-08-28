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
 * 침몰한 도시 - 사이드 퀘스트
 * 고대 침몰한 도시의 유적을 탐험하는 퀘스트
 *
 * @author Febrie
 */
public class SunkenCityQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class SunkenCityBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new SunkenCityQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public SunkenCityQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private SunkenCityQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new SunkenCityBuilder()
                .id(QuestID.SIDE_SUNKEN_CITY)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("deep_sea_diver", "deep_sea_diver"),
                        new VisitLocationObjective("underwater_ruins", "underwater_ruins"),
                        new KillMobObjective("kill_guardians", EntityType.GUARDIAN, 8),
                        new CollectItemObjective("sea_crystals", Material.PRISMARINE_CRYSTALS, 12),
                        new VisitLocationObjective("sunken_palace", "sunken_palace"),
                        new CollectItemObjective("atlantean_artifact", Material.HEART_OF_THE_SEA, 1)
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
        return Component.translatable("quest.side.sunken-city.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Component line = Component.translatable("quest.side.sunken-city.description." + i);
            description.add(line);
        }
        return description;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "deep_sea_diver" -> Component.translatable("quest.side.sunken-city.objectives.deep_sea_diver");
            case "underwater_ruins" -> Component.translatable("quest.side.sunken-city.objectives.underwater_ruins");
            case "kill_guardians" -> Component.translatable("quest.side.sunken-city.objectives.kill_guardians");
            case "sea_crystals" -> Component.translatable("quest.side.sunken-city.objectives.sea_crystals");
            case "sunken_palace" -> Component.translatable("quest.side.sunken-city.objectives.sunken_palace");
            case "atlantean_artifact" -> Component.translatable("quest.side.sunken-city.objectives.atlantean_artifact");
            default -> Component.translatable("quest.side.sunken-city.objectives." + id);
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.side.sunken-city.dialogs.0");
            case 1 -> Component.translatable("quest.side.sunken-city.dialogs.1");
            case 2 -> Component.translatable("quest.side.sunken-city.dialogs.2");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.side.sunken-city.npc-name");
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.side.sunken-city.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.side.sunken-city.decline");
    }
}