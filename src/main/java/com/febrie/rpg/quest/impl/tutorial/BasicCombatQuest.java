package com.febrie.rpg.quest.impl.tutorial;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
     * 퀘스트 빌더
     */
    private static class BasicCombatBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new BasicCombatQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public BasicCombatQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private BasicCombatQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new BasicCombatBuilder()
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
        return Component.translatable("quest.tutorial.basic-combat.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return List.of() /* TODO: Convert LangManager.getList("quest.tutorial.basic-combat.description") manually */;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String key = "quest.tutorial.basic-combat.objectives." + objective.getId();
        return Component.translatable(key);
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("basic_combat_dialog");

        dialog.addLine(
                "quest.tutorial.basic-combat.npcs.combat_instructor",
                "quest.tutorial.basic-combat.dialogs.essential_skills"
        );

        dialog.addLine(
                "quest.tutorial.basic-combat.npcs.combat_instructor",
                "quest.tutorial.basic-combat.dialogs.night_monsters"
        );

        dialog.addLine(
                "quest.tutorial.basic-combat.npcs.combat_instructor",
                "quest.tutorial.basic-combat.dialogs.return_reward"
        );

        return dialog;
    }
}