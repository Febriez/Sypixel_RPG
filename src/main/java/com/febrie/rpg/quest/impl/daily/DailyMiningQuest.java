package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.BreakBlockObjective;
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
 * 일일 채광 - 일일 퀘스트
 * 매일 리셋되는 채광 퀘스트
 *
 * @author Febrie
 */
public class DailyMiningQuest extends Quest {

    /**
     * 기본 생성자
     */
    public DailyMiningQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.DAILY_MINING)
                .objectives(Arrays.asList(new BreakBlockObjective("mine_stone", Material.STONE, 50), new BreakBlockObjective("mine_coal", Material.COAL_ORE, 20), new BreakBlockObjective("mine_iron", Material.IRON_ORE, 10)))
                .reward(new BasicReward.Builder().addCurrency(CurrencyType.GOLD, 150)
                        .addItem(new ItemStack(Material.IRON_PICKAXE)).addItem(new ItemStack(Material.TORCH, 32))
                        .addExperience(100).build()).sequential(false).daily(true)  // 일일 퀘스트 설정
                .category(QuestCategory.DAILY).minLevel(1).addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.get("quest.daily.mining.name", who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.daily.mining.info", who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String key = "quest.daily.mining.objectives." + objective.getId();
        return LangManager.get(key, who);
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.daily.mining.dialogs.0", who);
            case 1 -> LangManager.get("quest.daily.mining.dialogs.1", who);
            case 2 -> LangManager.get("quest.daily.mining.dialogs.2", who);
            case 3 -> LangManager.get("quest.daily.mining.dialogs.3", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.daily.mining.npc_name", who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.daily.mining.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.daily.mining.decline", who);
    }
}