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
 * Blacksmith Apprentice - Side Quest
 * Help the master blacksmith by gathering materials and learning the basics of smithing.
 *
 * @author Febrie
 */
public class BlacksmithApprenticeQuest extends Quest {

    /**
     * 기본 생성자
     */
    public BlacksmithApprenticeQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_BLACKSMITH_APPRENTICE)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("talk_master_blacksmith", "master_blacksmith"),
                        new CollectItemObjective("iron_ore", Material.IRON_ORE, 20),
                        new CollectItemObjective("coal", Material.COAL, 15),
                        new VisitLocationObjective("mining_site", "Mining_Site"),
                        new KillMobObjective("kill_skeletons", EntityType.SKELETON, 10),
                        new CollectItemObjective("refined_iron", Material.IRON_INGOT, 12),
                        new InteractNPCObjective("return_master_blacksmith", "master_blacksmith")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(1500)
                        .addCurrency(CurrencyType.GOLD, 400)
                        .addItem(new ItemStack(Material.IRON_SWORD))
                        .addItem(new ItemStack(Material.IRON_HELMET))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(12);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_SIDE_BLACKSMITH_APPRENTICE_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SIDE_BLACKSMITH_APPRENTICE_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.side.blacksmith_apprentice.objectives." + objective.getId(), who);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangHelper.text(LangKey.QUEST_SIDE_BLACKSMITH_APPRENTICE_DIALOGS_0, who);
            case 1 -> LangHelper.text(LangKey.QUEST_SIDE_BLACKSMITH_APPRENTICE_DIALOGS_1, who);
            case 2 -> LangHelper.text(LangKey.QUEST_SIDE_BLACKSMITH_APPRENTICE_DIALOGS_2, who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_SIDE_BLACKSMITH_APPRENTICE_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_SIDE_BLACKSMITH_APPRENTICE_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_SIDE_BLACKSMITH_APPRENTICE_DECLINE, who);
    }
}