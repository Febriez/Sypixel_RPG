package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangHelper;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
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
 * Side Quest: Innkeeper Trouble
 * Help the innkeeper deal with pests in the basement and recover stolen supplies.
 *
 * @author Febrie
 */
public class InnkeeperTroubleQuest extends Quest {

    /**
     * 기본 생성자
     */
    public InnkeeperTroubleQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_INNKEEPER_TROUBLE)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_worried_innkeeper", "worried_innkeeper"),
                        new VisitLocationObjective("visit_inn_basement", "inn_basement"),
                        new KillMobObjective("kill_spiders", EntityType.SPIDER, 12),
                        new CollectItemObjective("collect_inn_supplies", Material.BREAD, 20),
                        new CollectItemObjective("collect_ale_barrels", Material.BARREL, 3),
                        new VisitLocationObjective("visit_storage_room", "storage_room"),
                        new InteractNPCObjective("return_worried_innkeeper", "worried_innkeeper")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(1000)
                        .addCurrency(CurrencyType.GOLD, 250)
                        .addItem(new ItemStack(Material.BREAD, 10))
                        .addItem(new ItemStack(Material.COOKED_BEEF, 5))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(8);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_SIDE_INNKEEPER_TROUBLE_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SIDE_INNKEEPER_TROUBLE_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.side.innkeeper_trouble.objectives." + objective.getId(), who);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangHelper.text(LangKey.QUEST_SIDE_INNKEEPER_TROUBLE_DIALOGS_0, who);
            case 1 -> LangHelper.text(LangKey.QUEST_SIDE_INNKEEPER_TROUBLE_DIALOGS_1, who);
            case 2 -> LangHelper.text(LangKey.QUEST_SIDE_INNKEEPER_TROUBLE_DIALOGS_2, who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_SIDE_INNKEEPER_TROUBLE_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_SIDE_INNKEEPER_TROUBLE_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_SIDE_INNKEEPER_TROUBLE_DECLINE, who);
    }
}