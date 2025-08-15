package com.febrie.rpg.quest.impl.combat;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 투기장의 검투사 - 전투 퀘스트
 * 투기장에서 명예와 부를 위해 싸우는 퀘스트
 *
 * @author Febrie
 */
public class ArenaGladiatorQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class ArenaGladiatorBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new ArenaGladiatorQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public ArenaGladiatorQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private ArenaGladiatorQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new ArenaGladiatorBuilder()
                .id(QuestID.COMBAT_ARENA_GLADIATOR)
                .objectives(Arrays.asList(
                        // 준비 단계
                        new InteractNPCObjective("arena_master", "arena_master"), // 투기장 관리인
                        new PayCurrencyObjective("entry_fee", CurrencyType.GOLD, 500),
                        new CollectItemObjective("prepare_armor", Material.IRON_CHESTPLATE, 1),
                        new CollectItemObjective("prepare_weapon", Material.IRON_SWORD, 1),
                        
                        // 투기장 입장
                        new VisitLocationObjective("enter_arena", "gladiator_arena"),
                        
                        // 1라운드 - 초보자전
                        new KillPlayerObjective("round1_pvp", 1),
                        new CollectItemObjective("round1_token", Material.GOLD_NUGGET, 1),
                        
                        // 2라운드 - 야수와의 대결
                        new KillMobObjective("round2_wolves", EntityType.WOLF, 5),
                        new KillMobObjective("round2_bears", EntityType.POLAR_BEAR, 3),
                        new CollectItemObjective("round2_token", Material.GOLD_NUGGET, 1),
                        
                        // 3라운드 - 팀전
                        new KillPlayerObjective("round3_team", 3),
                        new SurviveObjective("round3_survive", 300), // 5분
                        new CollectItemObjective("round3_token", Material.GOLD_NUGGET, 1),
                        
                        // 결승전 - 챔피언과의 대결
                        new InteractNPCObjective("challenge_champion", "arena_champion"), // 현 챔피언
                        new KillMobObjective("defeat_champion", EntityType.IRON_GOLEM, 1),
                        new CollectItemObjective("champion_belt", Material.GOLDEN_HELMET, 1),
                        
                        // 완료
                        new DeliverItemObjective("claim_victory", "arena_master", Material.GOLDEN_HELMET, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 5000)
                        .addCurrency(CurrencyType.DIAMOND, 40)
                        .addItem(new ItemStack(Material.DIAMOND_SWORD))
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 5))
                        .addItem(new ItemStack(Material.PLAYER_HEAD)) // 챔피언 트로피
                        .addExperience(3000)
                        .build())
                .sequential(true)
                .repeatable(true)  // 반복 가능
                .category(QuestCategory.COMBAT)
                .minLevel(25)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.arena_gladiator.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getList(who, "quest.arena_gladiator.info");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.arena_gladiator.objective." + id, objective.getStatusInfo(null));
    }

    @Override
    public QuestDialog getDialog(@NotNull Player player) {
        QuestDialog dialog = new QuestDialog("arena_gladiator_dialog");

        dialog.addLine("quest.arena_gladiator.npcs.arena_master", "quest.arena_gladiator.dialogs.intro1");

        dialog.addLine("quest.arena_gladiator.npcs.arena_master", "quest.arena_gladiator.dialogs.intro2");

        dialog.addLine("quest.dialog.player", "quest.arena_gladiator.dialogs.player_ready");

        dialog.addLine("quest.arena_gladiator.npcs.arena_master", "quest.arena_gladiator.dialogs.explain1");

        dialog.addLine("quest.arena_gladiator.npcs.arena_master", "quest.arena_gladiator.dialogs.explain2");

        dialog.addLine("quest.dialog.player", "quest.arena_gladiator.dialogs.player_question");

        dialog.addLine("quest.arena_gladiator.npcs.arena_master", "quest.arena_gladiator.dialogs.reward_info");

        return dialog;
    }
}