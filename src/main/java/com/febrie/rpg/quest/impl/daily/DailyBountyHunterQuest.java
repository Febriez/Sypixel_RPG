package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
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
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 일일 현상금 사냥 - 일일 퀘스트
 * 매일 갱신되는 현상금 목표를 추적하고 처치하는 퀘스트
 *
 * @author Febrie
 */
public class DailyBountyHunterQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class DailyBountyHunterBuilder extends QuestBuilder {
        @Contract(" -> new")
        @Override
        public @NotNull Quest build() {
            return new DailyBountyHunterQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public DailyBountyHunterQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private DailyBountyHunterQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new DailyBountyHunterBuilder().id(QuestID.DAILY_BOUNTY_HUNTER).objectives(Arrays.asList(
                        // 현상금 사무소 방문
                        new InteractNPCObjective("bounty_officer", "bounty_officer"), // 현상금 담당관

                        // 첫 번째 현상금 - 일반 범죄자
                        new VisitLocationObjective("criminal_hideout", "bandit_camp"),
                        new KillMobObjective("wanted_bandits", EntityType.PILLAGER, 15),
                        new CollectItemObjective("bandit_badges", Material.IRON_NUGGET, 15),

                        // 두 번째 현상금 - 위험한 몬스터
                        new VisitLocationObjective("monster_lair", "dangerous_cave"),
                        new KillMobObjective("alpha_spider", EntityType.CAVE_SPIDER, 20),
                        new KillMobObjective("pack_leader", EntityType.WOLF, 10),
                        new CollectItemObjective("monster_fangs", Material.SPIDER_EYE, 10),

                        // 세 번째 현상금 - 마법사 추적
                        new VisitLocationObjective("wizard_tower", "dark_wizard_tower"),
                        new KillMobObjective("dark_wizards", EntityType.EVOKER, 5),
                        new KillMobObjective("summoned_vex", EntityType.VEX, 20),
                        new CollectItemObjective("wizard_staves", Material.STICK, 5),
                        new CollectItemObjective("magic_essence", Material.LAPIS_LAZULI, 20),

                        // 네 번째 현상금 - 엘리트 표적
                        new InteractNPCObjective("informant", "bounty_informant"), // 정보원
                        new PayCurrencyObjective("buy_info", CurrencyType.GOLD, 500),
                        new VisitLocationObjective("elite_location", "abandoned_fortress"),
                        new KillMobObjective("elite_guard", EntityType.VINDICATOR, 8),
                        new KillMobObjective("bounty_boss", EntityType.RAVAGER, 1),
                        new CollectItemObjective("boss_head", Material.PLAYER_HEAD, 1),
                        // 증거 수집
                        new CollectItemObjective("evidence_documents", Material.PAPER, 10),
                        new CollectItemObjective("stolen_goods", Material.EMERALD, 30),
                        // 보고 및 보상
                        new DeliverItemObjective("deliver_badges", "bounty_officer", Material.IRON_NUGGET, 15),
                        new DeliverItemObjective("deliver_evidence", "bounty_officer", Material.PAPER, 10),
                        new DeliverItemObjective("deliver_head", "bounty_officer", Material.PLAYER_HEAD, 1),
                        new InteractNPCObjective("claim_bounty", "bounty_officer")))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 2500)
                        .addCurrency(CurrencyType.DIAMOND, 15)
                        .addItem(new ItemStack(Material.CROSSBOW))
                        .addItem(new ItemStack(Material.ARROW, 64))
                        .addItem(new ItemStack(Material.SPYGLASS))
                        .addItem(new ItemStack(Material.IRON_SWORD))
                        .addExperience(1500).build())
                .sequential(false)  // 자유로운 순서로 진행 가능
                .repeatable(true).daily(true)       // 일일 퀘스트
                .category(QuestCategory.DAILY).minLevel(20).maxLevel(0).addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.daily.bounty-hunter.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getList(who, "quest.daily.bounty-hunter.description");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String key = "quest.daily.bounty-hunter.objectives." + objective.getId();
        return com.febrie.rpg.util.LangManager.getMessage(who, key);
    }

    @Override
    public QuestDialog getDialog(@NotNull Player player) {
        QuestDialog dialog = new QuestDialog("daily_bounty_hunter_dialog");
        // 시작
        dialog.addLine("quest.daily.bounty-hunter.npcs.bounty_officer", "quest.daily.bounty-hunter.dialogs.start1");
        dialog.addLine("quest.daily.bounty-hunter.npcs.bounty_officer", "quest.daily.bounty-hunter.dialogs.start2");
        dialog.addLine("quest.dialog.player", "quest.daily.bounty-hunter.dialogs.player_question");
        dialog.addLine("quest.daily.bounty-hunter.npcs.bounty_officer", "quest.daily.bounty-hunter.dialogs.targets");

        // 정보원
        dialog.addLine("quest.daily.bounty-hunter.npcs.bounty_informant", "quest.daily.bounty-hunter.dialogs.informant1");
        dialog.addLine("quest.daily.bounty-hunter.npcs.bounty_informant", "quest.daily.bounty-hunter.dialogs.informant2");

        // 현상금 수령
        dialog.addLine("quest.daily.bounty-hunter.npcs.bounty_officer", "quest.daily.bounty-hunter.dialogs.complete1");
        dialog.addLine("quest.dialog.player", "quest.daily.bounty-hunter.dialogs.player_complete");
        dialog.addLine("quest.daily.bounty-hunter.npcs.bounty_officer", "quest.daily.bounty-hunter.dialogs.complete2");
        dialog.addLine("quest.daily.bounty-hunter.npcs.bounty_officer", "quest.daily.bounty-hunter.dialogs.complete3");
        return dialog;
    }
}