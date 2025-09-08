package com.febrie.rpg.quest.impl.guild;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.BreakBlockObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.KillPlayerObjective;
import com.febrie.rpg.quest.objective.impl.SurviveObjective;
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
 * 길드 요새 공성 - 길드 퀘스트
 * 다른 길드의 요새를 공격하거나 방어하는 퀘스트
 *
 * @author Febrie
 */
public class GuildFortressSiegeQuest extends Quest {

    /**
     * 기본 생성자
     */
    public GuildFortressSiegeQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.GUILD_FORTRESS_SIEGE)
                .objectives(List.of(
                        new InteractNPCObjective("participate_in_siege", "siege_commander", 1),
                        new BreakBlockObjective("destroy_enemy_walls", Material.STONE_BRICK_WALL, 5),
                        new KillPlayerObjective("defeat_enemy_players", 20),
                        new VisitLocationObjective("capture_control_points", "control_point"),
                        new SurviveObjective("defend_fortress", 1800) // 30 minutes
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 1500)
                        .addItem(new ItemStack(Material.NETHERITE_SWORD, 1))
                        .addItem(new ItemStack(Material.TNT, 64))
                        .addItem(new ItemStack(Material.CROSSBOW, 5))
                        .addExperience(2000)
                        .build())
                .sequential(false)
                .category(QuestCategory.GUILD)
                .minLevel(35);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_GUILD_FORTRESS_SIEGE_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_GUILD_FORTRESS_SIEGE_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "participate_in_siege" -> LangManager.list(LangKey.QUEST_GUILD_FORTRESS_SIEGE_OBJECTIVES_PARTICIPATE_IN_SIEGE, who);
            case "destroy_enemy_walls" -> LangManager.list(LangKey.QUEST_GUILD_FORTRESS_SIEGE_OBJECTIVES_DESTROY_ENEMY_WALLS, who);
            case "defeat_enemy_players" -> LangManager.list(LangKey.QUEST_GUILD_FORTRESS_SIEGE_OBJECTIVES_DEFEAT_ENEMY_PLAYERS, who);
            case "capture_control_points" -> LangManager.list(LangKey.QUEST_GUILD_FORTRESS_SIEGE_OBJECTIVES_CAPTURE_CONTROL_POINTS, who);
            case "defend_fortress" -> LangManager.list(LangKey.QUEST_GUILD_FORTRESS_SIEGE_OBJECTIVES_DEFEND_FORTRESS, who);
            default -> new ArrayList<>();
        };
    }
    
    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_GUILD_FORTRESS_SIEGE_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_GUILD_FORTRESS_SIEGE_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_GUILD_FORTRESS_SIEGE_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_GUILD_FORTRESS_SIEGE_DECLINE, who);
    }
}