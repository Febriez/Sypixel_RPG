package com.febrie.rpg.quest.impl.tutorial;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 기초 전투 - 튜토리얼 퀘스트 2
 * 전투의 기본을 배우는 퀘스트
 *
 * @author Febrie
 */
public class BasicCombatQuest extends Quest {

    /**
     * 기본 생성자
     */
    public BasicCombatQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.TUTORIAL_BASIC_COMBAT)
                .objectives(Arrays.asList(
                        new KillMobObjective("kill_zombies", EntityType.ZOMBIE, 5),
                        new KillMobObjective("kill_skeletons", EntityType.SKELETON, 3)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 200)
                        .addItem(new ItemStack(Material.IRON_SWORD))
                        .addItem(new ItemStack(Material.IRON_CHESTPLATE))
                        .addItem(new ItemStack(Material.COOKED_BEEF, 20))
                        .addExperience(100)
                        .build())
                .sequential(false)  // 순서 상관없이 진행 가능
                .category(QuestCategory.TUTORIAL)
                .minLevel(1)
                .addPrerequisite(QuestID.TUTORIAL_FIRST_STEPS);  // 첫 걸음 퀘스트 완료 필요
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.get("quest.tutorial.basic_combat.name", who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.tutorial.basic_combat.info", who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String key = "quest.tutorial.basic_combat.objectives." + objective.getId();
        return LangManager.get(key, who);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.tutorial.basic_combat.dialogs.0", who);
            case 1 -> LangManager.get("quest.tutorial.basic_combat.dialogs.1", who);
            case 2 -> LangManager.get("quest.tutorial.basic_combat.dialogs.2", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.tutorial.basic_combat.npc_name", who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.tutorial.basic_combat.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.tutorial.basic_combat.decline", who);
    }
}