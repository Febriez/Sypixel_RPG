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
import com.febrie.rpg.util.LangHelper;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangHelper;
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
        return LangHelper.text(LangKey.QUEST_DAILY_HUNTING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangHelper.list(LangKey.QUEST_DAILY_HUNTING_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "kill_zombies" -> LangHelper.text(LangKey.QUEST_DAILY_HUNTING_OBJECTIVES_KILL_ZOMBIES, who);
            case "kill_skeletons" -> LangHelper.text(LangKey.QUEST_DAILY_HUNTING_OBJECTIVES_KILL_SKELETONS, who);
            case "kill_creepers" -> LangHelper.text(LangKey.QUEST_DAILY_HUNTING_OBJECTIVES_KILL_CREEPERS, who);
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
            case 0 -> LangHelper.text(LangKey.QUEST_DAILY_HUNTING_DIALOGS_0, who);
            case 1 -> LangHelper.text(LangKey.QUEST_DAILY_HUNTING_DIALOGS_1, who);
            case 2 -> LangHelper.text(LangKey.QUEST_DAILY_HUNTING_DIALOGS_2, who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_DAILY_HUNTING_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_DAILY_HUNTING_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_DAILY_HUNTING_DECLINE, who);
    }
}