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
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

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
                        new CollectItemObjective("snowball_collect", Material.SNOWBALL, 100),
                        new CraftItemObjective("shulker_box_craft", Material.SHULKER_BOX, 10),
                        new DeliverItemObjective("chest_deliver", Material.CHEST, 15, "christmas_gift_npc"),
                        new DeliverItemObjective("cookie_deliver", Material.COOKIE, 50, "christmas_npc"),
                        new CraftItemObjective("spruce_sapling_craft", Material.SPRUCE_SAPLING, 5)
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
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_CHRISTMAS_SPIRIT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SEASONAL_CHRISTMAS_SPIRIT_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "snowball_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_CHRISTMAS_SPIRIT_OBJECTIVES_SNOWBALL_COLLECT, who);
            case "shulker_box_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_CHRISTMAS_SPIRIT_OBJECTIVES_SHULKER_BOX_CRAFT, who);
            case "chest_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_CHRISTMAS_SPIRIT_OBJECTIVES_CHEST_DELIVER, who);
            case "cookie_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_CHRISTMAS_SPIRIT_OBJECTIVES_COOKIE_DELIVER, who);
            case "spruce_sapling_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_CHRISTMAS_SPIRIT_OBJECTIVES_SPRUCE_SAPLING_CRAFT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SEASONAL_CHRISTMAS_SPIRIT_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_CHRISTMAS_SPIRIT_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_CHRISTMAS_SPIRIT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_CHRISTMAS_SPIRIT_DECLINE, who);
    }
}