package com.febrie.rpg.quest.impl.seasonal;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.DeliverItemObjective;
import com.febrie.rpg.quest.objective.impl.CraftItemObjective;
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
 * 크리스마스 정신 - 계절 퀘스트
 * 크리스마스 시즌 테마 퀘스트
 *
 * @author Febrie
 */
public class ChristmasSpiritQuest extends Quest {

    /**
     * 기본 생성자
     */
    public ChristmasSpiritQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SEASON_CHRISTMAS_SPIRIT)
                .objectives(List.of(
                        new CollectItemObjective("collect_snow", Material.SNOWBALL, 100),
                        new CraftItemObjective("craft_presents", Material.SHULKER_BOX, 10),
                        new DeliverItemObjective("give_gifts_to_players", "christmas_gift_npc", Material.CHEST, 15),
                        new DeliverItemObjective("deliver_cookies", "christmas_npc", Material.COOKIE, 50),
                        new CraftItemObjective("craft_christmas_tree", Material.SPRUCE_SAPLING, 5)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 500)
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 3))
                        .addItem(new ItemStack(Material.FIREWORK_ROCKET, 32))
                        .addExperience(600)
                        .build())
                .sequential(false)
                .category(QuestCategory.EVENT)
                .minLevel(5);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_CHRISTMAS_SPIRIT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SEASONAL_CHRISTMAS_SPIRIT_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "collect_snow" -> LangManager.list(LangKey.QUEST_SEASONAL_CHRISTMAS_SPIRIT_OBJECTIVES_COLLECT_SNOW, who);
            case "craft_presents" -> LangManager.list(LangKey.QUEST_SEASONAL_CHRISTMAS_SPIRIT_OBJECTIVES_CRAFT_PRESENTS, who);
            case "give_gifts_to_players" -> LangManager.list(LangKey.QUEST_SEASONAL_CHRISTMAS_SPIRIT_OBJECTIVES_GIVE_GIFTS_TO_PLAYERS, who);
            case "deliver_cookies" -> LangManager.list(LangKey.QUEST_SEASONAL_CHRISTMAS_SPIRIT_OBJECTIVES_DELIVER_COOKIES, who);
            case "craft_christmas_tree" -> LangManager.list(LangKey.QUEST_SEASONAL_CHRISTMAS_SPIRIT_OBJECTIVES_CRAFT_CHRISTMAS_TREE, who);
            default -> new ArrayList<>();
        };
    }
    
    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SEASONAL_CHRISTMAS_SPIRIT_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_CHRISTMAS_SPIRIT_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_CHRISTMAS_SPIRIT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_CHRISTMAS_SPIRIT_DECLINE, who);
    }
}