package com.febrie.rpg.quest.impl.guild;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.DeliverItemObjective;
import com.febrie.rpg.quest.objective.impl.PayCurrencyObjective;
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
 * 동맹 결성 - 길드 퀘스트
 * 다른 길드와 동맹을 맺는 퀘스트
 *
 * @author Febrie
 */
public class AllianceFormationQuest extends Quest {

    /**
     * 기본 생성자
     */
    public AllianceFormationQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.GUILD_ALLIANCE_FORMATION)
                .objectives(List.of(
                        new InteractNPCObjective("negotiate_with_guilds", "guild_diplomat", 5),
                        new CollectItemObjective("collect_treaty_materials", Material.PAPER, 50),
                        new DeliverItemObjective("form_alliances", "alliance_council", Material.PAPER, 15),
                        new PayCurrencyObjective("complete_joint_quests", CurrencyType.GOLD, 500)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 1000)
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 10))
                        .addItem(new ItemStack(Material.BEACON, 1))
                        .addExperience(1500)
                        .build())
                .sequential(true)
                .category(QuestCategory.GUILD)
                .minLevel(30);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_GUILD_ALLIANCE_FORMATION_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_GUILD_ALLIANCE_FORMATION_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "negotiate_with_guilds" -> LangManager.list(LangKey.QUEST_GUILD_ALLIANCE_FORMATION_OBJECTIVES_NEGOTIATE_WITH_GUILDS, who);
            case "collect_treaty_materials" -> LangManager.list(LangKey.QUEST_GUILD_ALLIANCE_FORMATION_OBJECTIVES_COLLECT_TREATY_MATERIALS, who);
            case "form_alliances" -> LangManager.list(LangKey.QUEST_GUILD_ALLIANCE_FORMATION_OBJECTIVES_FORM_ALLIANCES, who);
            case "complete_joint_quests" -> LangManager.list(LangKey.QUEST_GUILD_ALLIANCE_FORMATION_OBJECTIVES_COMPLETE_JOINT_QUESTS, who);
            default -> new ArrayList<>();
        };
    }
    
    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_GUILD_ALLIANCE_FORMATION_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_GUILD_ALLIANCE_FORMATION_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_GUILD_ALLIANCE_FORMATION_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_GUILD_ALLIANCE_FORMATION_DECLINE, who);
    }
}