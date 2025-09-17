package com.febrie.rpg.quest.impl.daily;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

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
                        new InteractNPCObjective("bounty_officer", "bounty_officer"), // 현상금 담당관

                        // 첫 번째 현상금 - 일반 범죄자
                        new VisitLocationObjective("criminal_hideout", "bandit_camp"), new KillMobObjective("wanted_bandits", EntityType.PILLAGER, 15), new CollectItemObjective("iron_nugget_collect", Material.IRON_NUGGET, 15),

                        // 두 번째 현상금 - 위험한 몬스터
                        new VisitLocationObjective("monster_lair", "dangerous_cave"), new KillMobObjective("alpha_spider", EntityType.CAVE_SPIDER, 20), new KillMobObjective("pack_leader", EntityType.WOLF, 10), new CollectItemObjective("spider_eye_collect", Material.SPIDER_EYE, 10),

                        // 세 번째 현상금 - 마법사 추적
                        new VisitLocationObjective("wizard_tower", "dark_wizard_tower"), new KillMobObjective("dark_wizards", EntityType.EVOKER, 5), new KillMobObjective("summoned_vex", EntityType.VEX, 20), new CollectItemObjective("stick_collect", Material.STICK, 5), new CollectItemObjective("lapis_lazuli_collect", Material.LAPIS_LAZULI, 20),

                        // 네 번째 현상금 - 엘리트 표적
                        new InteractNPCObjective("informant", "bounty_informant"), // 정보원
                        new PayCurrencyObjective("buy_info", CurrencyType.GOLD, 500), new VisitLocationObjective("elite_location", "abandoned_fortress"), new KillMobObjective("elite_guard", EntityType.VINDICATOR, 8), new KillMobObjective("bounty_boss", EntityType.RAVAGER, 1), new CollectItemObjective("player_head_collect", Material.PLAYER_HEAD, 1),
                        // 증거 수집
                        new CollectItemObjective("paper_collect", Material.PAPER, 10), new CollectItemObjective("emerald_collect", Material.EMERALD, 30),
                        // 보고 및 보상
                        new DeliverItemObjective("iron_nugget_deliver", Material.IRON_NUGGET, 15, "bounty_officer"), new DeliverItemObjective("paper_deliver", Material.PAPER, 10, "bounty_officer"), new DeliverItemObjective("player_head_deliver", Material.PLAYER_HEAD, 1, "bounty_officer"), new InteractNPCObjective("claim_bounty", "bounty_officer")))
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
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_INFO, who);
    }

        @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "bounty_officer" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_BOUNTY_OFFICER, who);
            case "criminal_hideout" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_CRIMINAL_HIDEOUT, who);
            case "wanted_bandits" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_WANTED_BANDITS, who);
            case "iron_nugget_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_IRON_NUGGET_COLLECT, who);
            case "monster_lair" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_MONSTER_LAIR, who);
            case "alpha_spider" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_ALPHA_SPIDER, who);
            case "pack_leader" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_PACK_LEADER, who);
            case "spider_eye_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_SPIDER_EYE_COLLECT, who);
            case "wizard_tower" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_WIZARD_TOWER, who);
            case "dark_wizards" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_DARK_WIZARDS, who);
            case "summoned_vex" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_SUMMONED_VEX, who);
            case "stick_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_STICK_COLLECT, who);
            case "lapis_lazuli_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_LAPIS_LAZULI_COLLECT, who);
            case "informant" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_INFORMANT, who);
            case "buy_info" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_BUY_INFO, who);
            case "elite_location" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_ELITE_LOCATION, who);
            case "elite_guard" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_ELITE_GUARD, who);
            case "bounty_boss" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_BOUNTY_BOSS, who);
            case "player_head_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_PLAYER_HEAD_COLLECT, who);
            case "paper_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_PAPER_COLLECT, who);
            case "emerald_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_EMERALD_COLLECT, who);
            case "iron_nugget_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_IRON_NUGGET_DELIVER, who);
            case "paper_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_PAPER_DELIVER, who);
            case "player_head_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_PLAYER_HEAD_DELIVER, who);
            case "claim_bounty" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_OBJECTIVES_CLAIM_BOUNTY, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 11;
    }

        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }

    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_ACCEPT, who);
    }

    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_BOUNTY_HUNTER_DECLINE, who);
    }
}