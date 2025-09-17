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
 * 차원의 문 - 메인 스토리 퀘스트 (Chapter 4)
 * 다른 차원으로 통하는 포탈을 여는 퀘스트
 *
 * @author Febrie
 */
public class RealmPortalQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public RealmPortalQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_REALM_PORTAL)
                .objectives(List.of(
                        // 차원 연구
                        new InteractNPCObjective("dimension_scholar", "dimension_scholar"),
                        new CollectItemObjective("written_book_collect", Material.WRITTEN_BOOK, 10),
                        new VisitLocationObjective("observatory", "dimensional_observatory"),
                        new CollectItemObjective("map_collect", Material.MAP, 5),
                        
                        // 포탈 재료 수집
                        new CollectItemObjective("obsidian_collect", Material.OBSIDIAN, 50),
                        new CollectItemObjective("ender_pearl_collect", Material.ENDER_PEARL, 64),
                        new CollectItemObjective("blaze_powder_collect", Material.BLAZE_POWDER, 32),
                        new CollectItemObjective("chorus_fruit_collect", Material.CHORUS_FRUIT, 20),
                        new CollectItemObjective("end_crystal_collect", Material.END_CRYSTAL, 8),
                        
                        // 차원 키 제작
                        new InteractNPCObjective("portal_smith", "portal_smith"),
                        new CollectItemObjective("ender_eye_collect", Material.ENDER_EYE, 16),
                        new CollectItemObjective("prismarine_shard_collect", Material.PRISMARINE_SHARD, 64),
                        new DeliverItemObjective("nether_star_deliver", Material.NETHER_STAR, 1, "portal_smith"),
                        
                        // 포탈 프레임 건설
                        new VisitLocationObjective("portal_site", "realm_portal_site"),
                        new PlaceBlockObjective("place_obsidian_frame", Material.OBSIDIAN, 30),
                        new PlaceBlockObjective("place_end_rods", Material.END_ROD, 8),
                        new PlaceBlockObjective("place_beacons", Material.BEACON, 4),
                        
                        // 차원 안정화
                        new KillMobObjective("void_creatures", EntityType.ENDERMAN, 50),
                        new KillMobObjective("dimensional_rifts", EntityType.SHULKER, 20),
                        new CollectItemObjective("shulker_shell_collect", Material.SHULKER_SHELL, 10),
                        new SurviveObjective("stabilization", 600), // 10분
                        
                        // 포탈 활성화
                        new InteractNPCObjective("portal_activator", "dimension_scholar"),
                        new CollectItemObjective("dragon_breath_collect", Material.DRAGON_BREATH, 5),
                        new PlaceBlockObjective("activate_portal", Material.END_PORTAL_FRAME, 12),
                        new CollectItemObjective("end_portal_collect", Material.END_PORTAL, 1),
                        
                        // 첫 차원 여행
                        new VisitLocationObjective("enter_portal", "realm_portal_active"),
                        new VisitLocationObjective("void_dimension", "void_dimension_spawn"),
                        new KillMobObjective("void_guardians", EntityType.ELDER_GUARDIAN, 5),
                        new CollectItemObjective("heart_of_the_sea_collect", Material.HEART_OF_THE_SEA, 1),
                        
                        // 귀환
                        new VisitLocationObjective("return_portal", "realm_portal_return"),
                        new DeliverItemObjective("heart_of_the_sea_deliver", Material.HEART_OF_THE_SEA, 1, "dimension_scholar"),
                        new CollectItemObjective("compass_collect", Material.COMPASS, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 40000)
                        .addCurrency(CurrencyType.DIAMOND, 350)
                        .addItem(new ItemStack(Material.END_PORTAL_FRAME, 12))
                        .addItem(new ItemStack(Material.ENDER_CHEST, 3))
                        .addItem(new ItemStack(Material.SHULKER_BOX, 6))
                        .addItem(new ItemStack(Material.ELYTRA))
                        .addExperience(20000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_DRAGON_HEART)
                .minLevel(70)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "dimension_scholar" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_DIMENSION_SCHOLAR, who);
            case "written_book_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_WRITTEN_BOOK_COLLECT, who);
            case "observatory" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_OBSERVATORY, who);
            case "map_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_MAP_COLLECT, who);
            case "obsidian_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_OBSIDIAN_COLLECT, who);
            case "ender_pearl_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_ENDER_PEARL_COLLECT, who);
            case "blaze_powder_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_BLAZE_POWDER_COLLECT, who);
            case "chorus_fruit_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_CHORUS_FRUIT_COLLECT, who);
            case "end_crystal_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_END_CRYSTAL_COLLECT, who);
            case "portal_smith" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_PORTAL_SMITH, who);
            case "ender_eye_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_ENDER_EYE_COLLECT, who);
            case "prismarine_shard_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_PRISMARINE_SHARD_COLLECT, who);
            case "nether_star_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_NETHER_STAR_DELIVER, who);
            case "portal_site" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_PORTAL_SITE, who);
            case "place_obsidian_frame" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_PLACE_OBSIDIAN_FRAME, who);
            case "place_end_rods" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_PLACE_END_RODS, who);
            case "place_beacons" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_PLACE_BEACONS, who);
            case "void_creatures" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_VOID_CREATURES, who);
            case "dimensional_rifts" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_DIMENSIONAL_RIFTS, who);
            case "shulker_shell_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_SHULKER_SHELL_COLLECT, who);
            case "stabilization" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_STABILIZATION, who);
            case "portal_activator" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_PORTAL_ACTIVATOR, who);
            case "dragon_breath_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_DRAGON_BREATH_COLLECT, who);
            case "activate_portal" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_ACTIVATE_PORTAL, who);
            case "end_portal_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_END_PORTAL_COLLECT, who);
            case "enter_portal" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_ENTER_PORTAL, who);
            case "void_dimension" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_VOID_DIMENSION, who);
            case "void_guardians" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_VOID_GUARDIANS, who);
            case "heart_of_the_sea_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_HEART_OF_THE_SEA_COLLECT, who);
            case "return_portal" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_RETURN_PORTAL, who);
            case "heart_of_the_sea_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_HEART_OF_THE_SEA_DELIVER, who);
            case "compass_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_COMPASS_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_REALM_PORTAL_DECLINE, who);
    }
}