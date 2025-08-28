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
 * Enchanted Forest - Side Quest
 * Help a druid restore balance to an enchanted forest plagued by dark magic.
 *
 * @author Febrie
 */
public class EnchantedForestQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class EnchantedForestBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new EnchantedForestQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public EnchantedForestQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private EnchantedForestQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new EnchantedForestBuilder()
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
        return Component.translatable("quest.side.enchanted-forest.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Component line = Component.translatable("quest.side.enchanted-forest.description." + i);
            description.add(line);
        }
        return description;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "talk_forest_druid" -> Component.translatable("quest.side.enchanted-forest.objectives.talk_forest_druid");
            case "magical_grove" -> Component.translatable("quest.side.enchanted-forest.objectives.magical_grove");
            case "kill_witches" -> Component.translatable("quest.side.enchanted-forest.objectives.kill_witches");
            case "enchanted_saplings" -> Component.translatable("quest.side.enchanted-forest.objectives.enchanted_saplings");
            case "fairy_circle" -> Component.translatable("quest.side.enchanted-forest.objectives.fairy_circle");
            case "fairy_dust" -> Component.translatable("quest.side.enchanted-forest.objectives.fairy_dust");
            default -> Component.translatable("quest.side.enchanted-forest.objectives." + id);
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.side.enchanted-forest.dialogs.0");
            case 1 -> Component.translatable("quest.side.enchanted-forest.dialogs.1");
            case 2 -> Component.translatable("quest.side.enchanted-forest.dialogs.2");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.side.enchanted-forest.npc-name");
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.side.enchanted-forest.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.side.enchanted-forest.decline");
    }
}