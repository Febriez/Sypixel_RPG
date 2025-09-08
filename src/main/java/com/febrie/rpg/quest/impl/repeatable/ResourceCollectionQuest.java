package com.febrie.rpg.quest.impl.repeatable;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.BreakBlockObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 자원 수집 - 반복 퀘스트
 * 다양한 자원을 수집하는 퀘스트
 *
 * @author Febrie
 */
public class ResourceCollectionQuest extends Quest {

    /**
     * 기본 생성자
     */
    public ResourceCollectionQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.REPEATABLE_RESOURCE_COLLECTION)
                .objectives(List.of(
                        new BreakBlockObjective("mine_coal", Material.COAL_ORE, 64),
                        new BreakBlockObjective("mine_iron", Material.IRON_ORE, 32),
                        new CollectItemObjective("collect_wood", Material.OAK_LOG, 128),
                        new CollectItemObjective("collect_stone", Material.COBBLESTONE, 256),
                        new CollectItemObjective("catch_fish", Material.SALMON, 20),
                        new CollectItemObjective("collect_gems", Material.DIAMOND, 5)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 250)
                        .addItem(new ItemStack(Material.DIAMOND_PICKAXE, 1))
                        .addItem(new ItemStack(Material.FISHING_ROD, 1))
                        .addExperience(300)
                        .build())
                .sequential(false)
                .repeatable(true)
                .category(QuestCategory.REPEATABLE)
                .minLevel(8);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_REPEATABLE_RESOURCE_COLLECTION_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_REPEATABLE_RESOURCE_COLLECTION_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "mine_coal" -> LangManager.list(LangKey.QUEST_REPEATABLE_RESOURCE_COLLECTION_OBJECTIVES_MINE_COAL, who);
            case "mine_iron" -> LangManager.list(LangKey.QUEST_REPEATABLE_RESOURCE_COLLECTION_OBJECTIVES_MINE_IRON, who);
            case "collect_wood" -> LangManager.list(LangKey.QUEST_REPEATABLE_RESOURCE_COLLECTION_OBJECTIVES_COLLECT_WOOD, who);
            case "collect_stone" -> LangManager.list(LangKey.QUEST_REPEATABLE_RESOURCE_COLLECTION_OBJECTIVES_COLLECT_STONE, who);
            case "catch_fish" -> LangManager.list(LangKey.QUEST_REPEATABLE_RESOURCE_COLLECTION_OBJECTIVES_CATCH_FISH, who);
            case "collect_gems" -> LangManager.list(LangKey.QUEST_REPEATABLE_RESOURCE_COLLECTION_OBJECTIVES_COLLECT_GEMS, who);
            default -> new ArrayList<>();
        };
    }
    
    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_REPEATABLE_RESOURCE_COLLECTION_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_REPEATABLE_RESOURCE_COLLECTION_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_REPEATABLE_RESOURCE_COLLECTION_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_REPEATABLE_RESOURCE_COLLECTION_DECLINE, who);
    }
}