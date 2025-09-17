package com.febrie.rpg.quest.impl.repeatable;

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
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import java.util.ArrayList;
import java.util.List;

/**
 * 몬스터 퇴치 - 반복 퀘스트
 * 다양한 몬스터를 처치하는 퀘스트
 *
 * @author Febrie
 */
public class MonsterExterminationQuest extends Quest {

    /**
     * 기본 생성자
     */
    public MonsterExterminationQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.REPEATABLE_MONSTER_EXTERMINATION)
                .objectives(List.of(
                        new KillMobObjective("kill_hostile_mobs", EntityType.ZOMBIE, 50),
                        new KillMobObjective("kill_spiders", EntityType.SPIDER, 30),
                        new KillMobObjective("kill_endermen", EntityType.ENDERMAN, 10),
                        new KillMobObjective("kill_witches", EntityType.WITCH, 15),
                        new KillMobObjective("kill_boss_monsters", EntityType.WITHER, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 300)
                        .addItem(new ItemStack(Material.DIAMOND_SWORD, 1))
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 5))
                        .addExperience(400)
                        .build())
                .sequential(false)
                .repeatable(true)
                .category(QuestCategory.REPEATABLE)
                .minLevel(15);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_REPEATABLE_MONSTER_EXTERMINATION_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_REPEATABLE_MONSTER_EXTERMINATION_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "kill_hostile_mobs" -> LangManager.text(QuestCommonLangKey.QUEST_REPEATABLE_MONSTER_EXTERMINATION_OBJECTIVES_KILL_HOSTILE_MOBS, who);
            case "kill_spiders" -> LangManager.text(QuestCommonLangKey.QUEST_REPEATABLE_MONSTER_EXTERMINATION_OBJECTIVES_KILL_SPIDERS, who);
            case "kill_endermen" -> LangManager.text(QuestCommonLangKey.QUEST_REPEATABLE_MONSTER_EXTERMINATION_OBJECTIVES_KILL_ENDERMEN, who);
            case "kill_witches" -> LangManager.text(QuestCommonLangKey.QUEST_REPEATABLE_MONSTER_EXTERMINATION_OBJECTIVES_KILL_WITCHES, who);
            case "kill_boss_monsters" -> LangManager.text(QuestCommonLangKey.QUEST_REPEATABLE_MONSTER_EXTERMINATION_OBJECTIVES_KILL_BOSS_MONSTERS, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_REPEATABLE_MONSTER_EXTERMINATION_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_REPEATABLE_MONSTER_EXTERMINATION_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_REPEATABLE_MONSTER_EXTERMINATION_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_REPEATABLE_MONSTER_EXTERMINATION_DECLINE, who);
    }
}