package com.febrie.rpg.quest.impl.main.chapter4;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
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
                        new InteractNPCObjective("alliance_emissary", "alliance_emissary"),
                        new CollectItemObjective("written_book_collect", Material.WRITTEN_BOOK, 5),
                        new VisitLocationObjective("defender_council", "realm_defender_council"),
                        
                        // 불의 차원 수호자
                        new VisitLocationObjective("fire_realm", "fire_realm_portal"),
                        new InteractNPCObjective("fire_guardian", "fire_realm_guardian"),
                        new KillMobObjective("fire_trial", EntityType.BLAZE, 50),
                        new CollectItemObjective("blaze_powder_collect", Material.BLAZE_POWDER, 1),
                        
                        // 얼음 차원 수호자
                        new VisitLocationObjective("ice_realm", "ice_realm_portal"),
                        new InteractNPCObjective("ice_guardian", "ice_realm_guardian"),
                        new KillMobObjective("ice_trial", EntityType.STRAY, 50),
                        new CollectItemObjective("packed_ice_collect", Material.PACKED_ICE, 1),
                        
                        // 대지 차원 수호자
                        new VisitLocationObjective("earth_realm", "earth_realm_portal"),
                        new InteractNPCObjective("earth_guardian", "earth_realm_guardian"),
                        new KillMobObjective("earth_trial", EntityType.IRON_GOLEM, 30),
                        new CollectItemObjective("dirt_collect", Material.DIRT, 1),
                        
                        // 풍의 차원 수호자
                        new VisitLocationObjective("wind_realm", "wind_realm_portal"),
                        new InteractNPCObjective("wind_guardian", "wind_realm_guardian"),
                        new KillMobObjective("wind_trial", EntityType.PHANTOM, 40),
                        new CollectItemObjective("feather_collect", Material.FEATHER, 1),
                        
                        // 에테르 차원 수호자
                        new VisitLocationObjective("ether_realm", "ether_realm_portal"),
                        new InteractNPCObjective("ether_guardian", "ether_realm_guardian"),
                        new KillMobObjective("ether_trial", EntityType.VEX, 60),
                        new CollectItemObjective("glowstone_dust_collect", Material.GLOWSTONE_DUST, 1),
                        
                        // 동맹 의식
                        new VisitLocationObjective("alliance_ceremony", "grand_alliance_hall"),
                        new PlaceBlockObjective("place_tokens", Material.BEACON, 5),
                        new InteractNPCObjective("alliance_leader", "supreme_guardian"),
                        new SurviveObjective("alliance_ritual", 600), // 10분
                        
                        // 통합 시험
                        new KillMobObjective("combined_trial", EntityType.ELDER_GUARDIAN, 10),
                        new CollectItemObjective("nether_star_collect", Material.NETHER_STAR, 5),
                        new DeliverItemObjective("nether_star_deliver", Material.NETHER_STAR, 5, "alliance_emissary"),
                        
                        // 동맹 완성
                        new CollectItemObjective("golden_apple_collect", Material.GOLDEN_APPLE, 5),
                        new CollectItemObjective("white_banner_collect", Material.WHITE_BANNER, 1)
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
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "alliance_emissary" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ALLIANCE_EMISSARY, who);
            case "written_book_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_WRITTEN_BOOK_COLLECT, who);
            case "defender_council" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_DEFENDER_COUNCIL, who);
            case "fire_realm" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_FIRE_REALM, who);
            case "fire_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_FIRE_GUARDIAN, who);
            case "fire_trial" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_FIRE_TRIAL, who);
            case "blaze_powder_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_BLAZE_POWDER_COLLECT, who);
            case "ice_realm" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ICE_REALM, who);
            case "ice_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ICE_GUARDIAN, who);
            case "ice_trial" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ICE_TRIAL, who);
            case "packed_ice_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_PACKED_ICE_COLLECT, who);
            case "earth_realm" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_EARTH_REALM, who);
            case "earth_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_EARTH_GUARDIAN, who);
            case "earth_trial" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_EARTH_TRIAL, who);
            case "dirt_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_DIRT_COLLECT, who);
            case "wind_realm" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_WIND_REALM, who);
            case "wind_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_WIND_GUARDIAN, who);
            case "wind_trial" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_WIND_TRIAL, who);
            case "feather_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_FEATHER_COLLECT, who);
            case "ether_realm" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ETHER_REALM, who);
            case "ether_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ETHER_GUARDIAN, who);
            case "ether_trial" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ETHER_TRIAL, who);
            case "glowstone_dust_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_GLOWSTONE_DUST_COLLECT, who);
            case "alliance_ceremony" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ALLIANCE_CEREMONY, who);
            case "place_tokens" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_PLACE_TOKENS, who);
            case "alliance_leader" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ALLIANCE_LEADER, who);
            case "alliance_ritual" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_ALLIANCE_RITUAL, who);
            case "combined_trial" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_COMBINED_TRIAL, who);
            case "nether_star_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_NETHER_STAR_COLLECT, who);
            case "nether_star_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_NETHER_STAR_DELIVER, who);
            case "golden_apple_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_GOLDEN_APPLE_COLLECT, who);
            case "white_banner_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_OBJECTIVES_WHITE_BANNER_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_DEFENDERS_DECLINE, who);
    }
}