package com.febrie.rpg.quest.impl.main.chapter1;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 고대의 예언 - 메인 퀘스트 Chapter 1
 * 운명의 시작
 */
public class AncientProphecyQuest extends Quest {

    public AncientProphecyQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_ANCIENT_PROPHECY)
                .objectives(Arrays.asList(
                        new VisitLocationObjective("visit_elder", "ancient_temple"),
                        new InteractNPCObjective("talk_elder", "ancient_elder"), // 고대의 장로
                        new CollectItemObjective("collect_scrolls", Material.PAPER, 5),
                        new DeliverItemObjective("deliver_scrolls", "고대의 장로", Material.PAPER, 5)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 500)
                        .addCurrency(CurrencyType.DIAMOND, 10)
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK))
                        .addExperience(1000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .minLevel(10)
                .maxLevel(0)
                .addPrerequisite(QuestID.MAIN_HEROES_JOURNEY);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.get("quest.main.ancient_prophecy.name", who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.main.ancient_prophecy.info", who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String key = "quest.main.ancient_prophecy.objectives." + objective.getId();
        return LangManager.get(key, who);
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.main.ancient_prophecy.dialogs.0", who);
            case 1 -> LangManager.get("quest.main.ancient_prophecy.dialogs.1", who);
            case 2 -> LangManager.get("quest.main.ancient_prophecy.dialogs.2", who);
            case 3 -> LangManager.get("quest.main.ancient_prophecy.dialogs.3", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.main.ancient_prophecy.npc_name", who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.main.ancient_prophecy.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.main.ancient_prophecy.decline", who);
    }
}