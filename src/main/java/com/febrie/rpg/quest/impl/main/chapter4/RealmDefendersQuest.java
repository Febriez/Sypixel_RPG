package com.febrie.rpg.quest.impl.main.chapter4;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
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

/**
 * 차원의 수호자들 - 메인 스토리 퀘스트 (Chapter 4)
 * 다양한 차원의 수호자들과 동맹을 맺는 퀘스트
 *
 * @author Febrie
 */
public class RealmDefendersQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public RealmDefendersQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_REALM_DEFENDERS)
                .objectives(List.of(
                        // 동맹 제안
                        new InteractNPCObjective("alliance_emissary", "alliance_emissary", 1),
                        new CollectItemObjective("alliance_treaty", Material.WRITTEN_BOOK, 5),
                        new VisitLocationObjective("defender_council", "realm_defender_council"),
                        
                        // 불의 차원 수호자
                        new VisitLocationObjective("fire_realm", "fire_realm_portal"),
                        new InteractNPCObjective("fire_guardian", "fire_realm_guardian", 1),
                        new KillMobObjective("fire_trial", EntityType.BLAZE, 50),
                        new CollectItemObjective("fire_alliance_token", Material.BLAZE_POWDER, 1),
                        
                        // 얼음 차원 수호자
                        new VisitLocationObjective("ice_realm", "ice_realm_portal"),
                        new InteractNPCObjective("ice_guardian", "ice_realm_guardian", 1),
                        new KillMobObjective("ice_trial", EntityType.STRAY, 50),
                        new CollectItemObjective("ice_alliance_token", Material.PACKED_ICE, 1),
                        
                        // 대지 차원 수호자
                        new VisitLocationObjective("earth_realm", "earth_realm_portal"),
                        new InteractNPCObjective("earth_guardian", "earth_realm_guardian", 1),
                        new KillMobObjective("earth_trial", EntityType.IRON_GOLEM, 30),
                        new CollectItemObjective("earth_alliance_token", Material.DIRT, 1),
                        
                        // 풍의 차원 수호자
                        new VisitLocationObjective("wind_realm", "wind_realm_portal"),
                        new InteractNPCObjective("wind_guardian", "wind_realm_guardian", 1),
                        new KillMobObjective("wind_trial", EntityType.PHANTOM, 40),
                        new CollectItemObjective("wind_alliance_token", Material.FEATHER, 1),
                        
                        // 에테르 차원 수호자
                        new VisitLocationObjective("ether_realm", "ether_realm_portal"),
                        new InteractNPCObjective("ether_guardian", "ether_realm_guardian", 1),
                        new KillMobObjective("ether_trial", EntityType.VEX, 60),
                        new CollectItemObjective("ether_alliance_token", Material.GLOWSTONE_DUST, 1),
                        
                        // 동맹 의식
                        new VisitLocationObjective("alliance_ceremony", "grand_alliance_hall"),
                        new PlaceBlockObjective("place_tokens", Material.BEACON, 5),
                        new InteractNPCObjective("alliance_leader", "supreme_guardian", 1),
                        new SurviveObjective("alliance_ritual", 600), // 10분
                        
                        // 통합 시험
                        new KillMobObjective("combined_trial", EntityType.ELDER_GUARDIAN, 10),
                        new CollectItemObjective("unity_crystal", Material.NETHER_STAR, 5),
                        new DeliverItemObjective("complete_alliance", "alliance_emissary", Material.NETHER_STAR, 5),
                        
                        // 동맹 완성
                        new CollectItemObjective("defender_badge", Material.GOLDEN_APPLE, 5),
                        new CollectItemObjective("alliance_banner", Material.WHITE_BANNER, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 50000)
                        .addCurrency(CurrencyType.DIAMOND, 450)
                        .addItem(new ItemStack(Material.NETHERITE_CHESTPLATE))
                        .addItem(new ItemStack(Material.BEACON, 5))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 5))
                        .addItem(new ItemStack(Material.CONDUIT))
                        .addExperience(25000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_VOID_INVASION)
                .minLevel(80)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_REALM_DEFENDERS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "alliance_emissary" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ALLIANCE_EMISSARY, who);
            case "alliance_treaty" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ALLIANCE_TREATY, who);
            case "defender_council" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_DEFENDER_COUNCIL, who);
            case "fire_realm" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_FIRE_REALM, who);
            case "fire_guardian" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_FIRE_GUARDIAN, who);
            case "fire_trial" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_FIRE_TRIAL, who);
            case "fire_alliance_token" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_FIRE_ALLIANCE_TOKEN, who);
            case "ice_realm" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ICE_REALM, who);
            case "ice_guardian" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ICE_GUARDIAN, who);
            case "ice_trial" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ICE_TRIAL, who);
            case "ice_alliance_token" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ICE_ALLIANCE_TOKEN, who);
            case "earth_realm" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_EARTH_REALM, who);
            case "earth_guardian" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_EARTH_GUARDIAN, who);
            case "earth_trial" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_EARTH_TRIAL, who);
            case "earth_alliance_token" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_EARTH_ALLIANCE_TOKEN, who);
            case "wind_realm" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_WIND_REALM, who);
            case "wind_guardian" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_WIND_GUARDIAN, who);
            case "wind_trial" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_WIND_TRIAL, who);
            case "wind_alliance_token" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_WIND_ALLIANCE_TOKEN, who);
            case "ether_realm" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ETHER_REALM, who);
            case "ether_guardian" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ETHER_GUARDIAN, who);
            case "ether_trial" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ETHER_TRIAL, who);
            case "ether_alliance_token" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ETHER_ALLIANCE_TOKEN, who);
            case "alliance_ceremony" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ALLIANCE_CEREMONY, who);
            case "place_tokens" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_PLACE_TOKENS, who);
            case "alliance_leader" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ALLIANCE_LEADER, who);
            case "alliance_ritual" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ALLIANCE_RITUAL, who);
            case "combined_trial" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_COMBINED_TRIAL, who);
            case "unity_crystal" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_UNITY_CRYSTAL, who);
            case "complete_alliance" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_COMPLETE_ALLIANCE, who);
            case "defender_badge" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_DEFENDER_BADGE, who);
            case "alliance_banner" -> LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ALLIANCE_BANNER, who);
            default -> List.of(Component.text("Objective: " + objective.getId()));
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_REALM_DEFENDERS_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_REALM_DEFENDERS_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_REALM_DEFENDERS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_REALM_DEFENDERS_DECLINE, who);
    }
}