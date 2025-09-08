package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.ArrayList;

/**
 * 일일 현상금 사냥 - 일일 퀘스트
 * 매일 갱신되는 현상금 목표를 추적하고 처치하는 퀘스트
 *
 * @author Febrie
 */
public class DailyBountyHunterQuest extends Quest {

    /**
     * 기본 생성자
     */
    public DailyBountyHunterQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder().id(QuestID.DAILY_BOUNTY_HUNTER).objectives(List.of(
                        // 현상금 사무소 방문
                        new InteractNPCObjective("bounty_officer", "bounty_officer", 1), // 현상금 담당관

                        // 첫 번째 현상금 - 일반 범죄자
                        new VisitLocationObjective("criminal_hideout", "bandit_camp"), new KillMobObjective("wanted_bandits", EntityType.PILLAGER, 15), new CollectItemObjective("bandit_badges", Material.IRON_NUGGET, 15),

                        // 두 번째 현상금 - 위험한 몬스터
                        new VisitLocationObjective("monster_lair", "dangerous_cave"), new KillMobObjective("alpha_spider", EntityType.CAVE_SPIDER, 20), new KillMobObjective("pack_leader", EntityType.WOLF, 10), new CollectItemObjective("monster_fangs", Material.SPIDER_EYE, 10),

                        // 세 번째 현상금 - 마법사 추적
                        new VisitLocationObjective("wizard_tower", "dark_wizard_tower"), new KillMobObjective("dark_wizards", EntityType.EVOKER, 5), new KillMobObjective("summoned_vex", EntityType.VEX, 20), new CollectItemObjective("wizard_staves", Material.STICK, 5), new CollectItemObjective("magic_essence", Material.LAPIS_LAZULI, 20),

                        // 네 번째 현상금 - 엘리트 표적
                        new InteractNPCObjective("informant", "bounty_informant", 1), // 정보원
                        new PayCurrencyObjective("buy_info", CurrencyType.GOLD, 500), new VisitLocationObjective("elite_location", "abandoned_fortress"), new KillMobObjective("elite_guard", EntityType.VINDICATOR, 8), new KillMobObjective("bounty_boss", EntityType.RAVAGER, 1), new CollectItemObjective("boss_head", Material.PLAYER_HEAD, 1),
                        // 증거 수집
                        new CollectItemObjective("evidence_documents", Material.PAPER, 10), new CollectItemObjective("stolen_goods", Material.EMERALD, 30),
                        // 보고 및 보상
                        new DeliverItemObjective("deliver_badges", "bounty_officer", Material.IRON_NUGGET, 15), new DeliverItemObjective("deliver_evidence", "bounty_officer", Material.PAPER, 10), new DeliverItemObjective("deliver_head", "bounty_officer", Material.PLAYER_HEAD, 1), new InteractNPCObjective("claim_bounty", "bounty_officer", 1)))
                .reward(new BasicReward.Builder().addCurrency(CurrencyType.GOLD, 2500)
                        .addCurrency(CurrencyType.DIAMOND, 15).addItem(new ItemStack(Material.CROSSBOW))
                        .addItem(new ItemStack(Material.ARROW, 64)).addItem(new ItemStack(Material.SPYGLASS))
                        .addItem(new ItemStack(Material.IRON_SWORD)).addExperience(1500).build())
                .sequential(false)  // 자유로운 순서로 진행 가능
                .repeatable(true).daily(true)       // 일일 퀘스트
                .category(QuestCategory.DAILY).minLevel(20).maxLevel(0).addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_DAILY_BOUNTY_HUNTER_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_INFO, who);
    }

        @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "bounty_officer" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_BOUNTY_OFFICER, who);
            case "criminal_hideout" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_CRIMINAL_HIDEOUT, who);
            case "wanted_bandits" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_WANTED_BANDITS, who);
            case "bandit_badges" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_BANDIT_BADGES, who);
            case "monster_lair" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_MONSTER_LAIR, who);
            case "alpha_spider" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_ALPHA_SPIDER, who);
            case "pack_leader" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_PACK_LEADER, who);
            case "monster_fangs" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_MONSTER_FANGS, who);
            case "wizard_tower" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_WIZARD_TOWER, who);
            case "dark_wizards" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_DARK_WIZARDS, who);
            case "summoned_vex" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_SUMMONED_VEX, who);
            case "wizard_staves" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_WIZARD_STAVES, who);
            case "magic_essence" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_MAGIC_ESSENCE, who);
            case "informant" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_INFORMANT, who);
            case "buy_info" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_BUY_INFO, who);
            case "elite_location" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_ELITE_LOCATION, who);
            case "elite_guard" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_ELITE_GUARD, who);
            case "bounty_boss" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_BOUNTY_BOSS, who);
            case "boss_head" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_BOSS_HEAD, who);
            case "evidence_documents" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_EVIDENCE_DOCUMENTS, who);
            case "stolen_goods" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_STOLEN_GOODS, who);
            case "deliver_badges" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_DELIVER_BADGES, who);
            case "deliver_evidence" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_DELIVER_EVIDENCE, who);
            case "deliver_head" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_DELIVER_HEAD, who);
            case "claim_bounty" -> LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_CLAIM_BOUNTY, who);
            default -> new ArrayList<>();
        };
    }

    @Override
    public int getDialogCount() {
        return 11;
    }

        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_DAILY_BOUNTY_HUNTER_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }

    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_DAILY_BOUNTY_HUNTER_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_DAILY_BOUNTY_HUNTER_ACCEPT, who);
    }

    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_DAILY_BOUNTY_HUNTER_DECLINE, who);
    }
}