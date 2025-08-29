package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 일일 사냥 - 일일 퀘스트
 * 매일 리셋되는 사냥 퀘스트
 *
 * @author Febrie
 */
public class DailyHuntingQuest extends Quest {

    /**
     * 기본 생성자
     */
    public DailyHuntingQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.DAILY_HUNTING)
                .objectives(Arrays.asList(new KillMobObjective("kill_zombies", EntityType.ZOMBIE, 20), new KillMobObjective("kill_skeletons", EntityType.SKELETON, 15), new KillMobObjective("kill_creepers", EntityType.CREEPER, 10)))
                .reward(new BasicReward.Builder().addCurrency(CurrencyType.GOLD, 200)
                        .addItem(new ItemStack(Material.ARROW, 64)).addItem(new ItemStack(Material.COOKED_BEEF, 32))
                        .addExperience(150).build()).sequential(false)
                .daily(true)  // daily 설정하면 자동으로 repeatable도 true가 됨
                .category(QuestCategory.DAILY).minLevel(5).addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.get("quest.daily.hunting.name", who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.daily.hunting.info", who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "kill_zombies" -> LangManager.get("quest.daily.hunting.objectives.kill_zombies", who);
            case "kill_skeletons" -> LangManager.get("quest.daily.hunting.objectives.kill_skeletons", who);
            case "kill_creepers" -> LangManager.get("quest.daily.hunting.objectives.kill_creepers", who);
            default -> LangManager.get("quest.daily.hunting.objectives." + id, who);
        };
    }
    
    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.daily.hunting.dialogs.0", who);
            case 1 -> LangManager.get("quest.daily.hunting.dialogs.1", who);
            case 2 -> LangManager.get("quest.daily.hunting.dialogs.2", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.daily.hunting.npc_name", who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.daily.hunting.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.daily.hunting.decline", who);
    }
}