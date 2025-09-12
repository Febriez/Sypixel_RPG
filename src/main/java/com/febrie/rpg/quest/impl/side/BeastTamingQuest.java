package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.lang.quest.side.BeastTamingLangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * Side Quest: Beast Taming
 * Learn the ancient arts of beast taming and understanding wild creatures
 *
 * @author Febrie
 */
public class BeastTamingQuest extends Quest {

    /**
     * 기본 생성자
     */
    public BeastTamingQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_BEAST_TAMING)
                .objectives(List.of(
                        new InteractNPCObjective("talk_beast_master", "beast_master"),
                        new VisitLocationObjective("wild_plains", "Wild_Plains"),
                        new CollectItemObjective("wheat_collect", Material.WHEAT, 20),
                        new CollectItemObjective("compass_collect", Material.COMPASS, 3),
                        new KillMobObjective("observe_wolves", EntityType.WOLF, 5),
                        new VisitLocationObjective("pack_territory", "Pack_Territory"),
                        new CollectItemObjective("bone_collect", Material.BONE, 15),
                        new InteractNPCObjective("return_beast_master", "beast_master")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(1500)
                        .addCurrency(CurrencyType.GOLD, 350)
                        .addItem(new ItemStack(Material.LEAD, 5))
                        .addItem(new ItemStack(Material.WOLF_SPAWN_EGG, 1))
                        .addItem(new ItemStack(Material.BONE_BLOCK, 2))
                        .build())
                .sequential(false)
                .category(QuestCategory.SIDE)
                .minLevel(12);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(BeastTamingLangKey.QUEST_SIDE_BEAST_TAMING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(BeastTamingLangKey.QUEST_SIDE_BEAST_TAMING_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_beast_master" -> LangManager.text(BeastTamingLangKey.QUEST_SIDE_BEAST_TAMING_OBJECTIVES_TALK_BEAST_MASTER, who);
            case "wild_plains" -> LangManager.text(BeastTamingLangKey.QUEST_SIDE_BEAST_TAMING_OBJECTIVES_WILD_PLAINS, who);
            case "wheat_collect" -> LangManager.text(BeastTamingLangKey.QUEST_SIDE_BEAST_TAMING_OBJECTIVES_WHEAT_COLLECT, who);
            case "compass_collect" -> LangManager.text(BeastTamingLangKey.QUEST_SIDE_BEAST_TAMING_OBJECTIVES_COMPASS_COLLECT, who);
            case "observe_wolves" -> LangManager.text(BeastTamingLangKey.QUEST_SIDE_BEAST_TAMING_OBJECTIVES_OBSERVE_WOLVES, who);
            case "pack_territory" -> LangManager.text(BeastTamingLangKey.QUEST_SIDE_BEAST_TAMING_OBJECTIVES_PACK_TERRITORY, who);
            case "bone_collect" -> LangManager.text(BeastTamingLangKey.QUEST_SIDE_BEAST_TAMING_OBJECTIVES_BONE_COLLECT, who);
            case "return_beast_master" -> LangManager.text(BeastTamingLangKey.QUEST_SIDE_BEAST_TAMING_OBJECTIVES_RETURN_BEAST_MASTER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(BeastTamingLangKey.QUEST_SIDE_BEAST_TAMING_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(BeastTamingLangKey.QUEST_SIDE_BEAST_TAMING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(BeastTamingLangKey.QUEST_SIDE_BEAST_TAMING_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(BeastTamingLangKey.QUEST_SIDE_BEAST_TAMING_DECLINE, who);
    }
}