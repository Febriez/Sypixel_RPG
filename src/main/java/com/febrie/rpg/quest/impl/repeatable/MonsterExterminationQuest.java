package com.febrie.rpg.quest.impl.repeatable;

import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 몬스터 토벌 임무 - 반복 가능한 퀘스트
 * 마을 주변의 몬스터들을 토벌하는 퀘스트
 *
 * @author Febrie
 */
public class MonsterExterminationQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class MonsterExterminationBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new MonsterExterminationQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public MonsterExterminationQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private MonsterExterminationQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new MonsterExterminationBuilder()
                .id(QuestID.REPEAT_MONSTER_EXTERMINATION)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("guard_captain", "village_guard_captain"),
                        new KillMobObjective("kill_zombies", EntityType.ZOMBIE, 30),
                        new KillMobObjective("kill_skeletons", EntityType.SKELETON, 25),
                        new KillMobObjective("kill_spiders", EntityType.SPIDER, 20),
                        new KillMobObjective("kill_creepers", EntityType.CREEPER, 15),
                        new CollectItemObjective("collect_proof", Material.ROTTEN_FLESH, 20),
                        new DeliverItemObjective("deliver_proof", "guard_captain", Material.ROTTEN_FLESH, 20)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 1500)
                        .addCurrency(CurrencyType.EXP, 300)
                        .addExperience(200)
                        .build())
                .sequential(false)
                .repeatable(true)
                .category(QuestCategory.REPEATABLE)
                .minLevel(10)
                .maxLevel(100)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.monster_extermination.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getList(who, "quest.monster_extermination.info");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.monster_extermination.objective." + id);
    }
    
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("monster_extermination_dialog");
        
        dialog.addLine("quest.repeatable_monster_extermination.npcs.guard_captain", "quest.repeatable_monster_extermination.dialogs.line1");
                
        return dialog;
    }
}