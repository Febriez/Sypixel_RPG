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
                        new InteractNPCObjective("dimension_scholar", "dimension_scholar", 1),
                        new CollectItemObjective("ancient_texts", Material.WRITTEN_BOOK, 10),
                        new VisitLocationObjective("observatory", "dimensional_observatory"),
                        new CollectItemObjective("star_charts", Material.MAP, 5),
                        
                        // 포탈 재료 수집
                        new CollectItemObjective("obsidian_blocks", Material.OBSIDIAN, 50),
                        new CollectItemObjective("ender_pearls", Material.ENDER_PEARL, 64),
                        new CollectItemObjective("blaze_powder", Material.BLAZE_POWDER, 32),
                        new CollectItemObjective("chorus_fruit", Material.CHORUS_FRUIT, 20),
                        new CollectItemObjective("end_crystals", Material.END_CRYSTAL, 8),
                        
                        // 차원 키 제작
                        new InteractNPCObjective("portal_smith", "portal_smith", 1),
                        new CollectItemObjective("void_essence", Material.ENDER_EYE, 16),
                        new CollectItemObjective("dimensional_shards", Material.PRISMARINE_SHARD, 64),
                        new DeliverItemObjective("forge_key", "portal_smith", Material.NETHER_STAR, 1),
                        
                        // 포탈 프레임 건설
                        new VisitLocationObjective("portal_site", "realm_portal_site"),
                        new PlaceBlockObjective("place_obsidian_frame", Material.OBSIDIAN, 30),
                        new PlaceBlockObjective("place_end_rods", Material.END_ROD, 8),
                        new PlaceBlockObjective("place_beacons", Material.BEACON, 4),
                        
                        // 차원 안정화
                        new KillMobObjective("void_creatures", EntityType.ENDERMAN, 50),
                        new KillMobObjective("dimensional_rifts", EntityType.SHULKER, 20),
                        new CollectItemObjective("stability_cores", Material.SHULKER_SHELL, 10),
                        new SurviveObjective("stabilization", 600), // 10분
                        
                        // 포탈 활성화
                        new InteractNPCObjective("portal_activator", "dimension_scholar", 1),
                        new CollectItemObjective("activation_catalyst", Material.DRAGON_BREATH, 5),
                        new PlaceBlockObjective("activate_portal", Material.END_PORTAL_FRAME, 12),
                        new CollectItemObjective("portal_key", Material.END_PORTAL, 1),
                        
                        // 첫 차원 여행
                        new VisitLocationObjective("enter_portal", "realm_portal_active"),
                        new VisitLocationObjective("void_dimension", "void_dimension_spawn"),
                        new KillMobObjective("void_guardians", EntityType.ELDER_GUARDIAN, 5),
                        new CollectItemObjective("dimension_proof", Material.HEART_OF_THE_SEA, 1),
                        
                        // 귀환
                        new VisitLocationObjective("return_portal", "realm_portal_return"),
                        new DeliverItemObjective("report_success", "dimension_scholar", Material.HEART_OF_THE_SEA, 1),
                        new CollectItemObjective("realm_navigator", Material.COMPASS, 1)
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
        return LangManager.text(LangKey.QUEST_MAIN_REALM_PORTAL_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "dimension_scholar" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_DIMENSION_SCHOLAR, who);
            case "ancient_texts" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_ANCIENT_TEXTS, who);
            case "observatory" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_OBSERVATORY, who);
            case "star_charts" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_STAR_CHARTS, who);
            case "obsidian_blocks" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_OBSIDIAN_BLOCKS, who);
            case "ender_pearls" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_ENDER_PEARLS, who);
            case "blaze_powder" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_BLAZE_POWDER, who);
            case "chorus_fruit" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_CHORUS_FRUIT, who);
            case "end_crystals" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_END_CRYSTALS, who);
            case "portal_smith" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_PORTAL_SMITH, who);
            case "void_essence" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_VOID_ESSENCE, who);
            case "dimensional_shards" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_DIMENSIONAL_SHARDS, who);
            case "forge_key" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_FORGE_KEY, who);
            case "portal_site" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_PORTAL_SITE, who);
            case "place_obsidian_frame" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_PLACE_OBSIDIAN_FRAME, who);
            case "place_end_rods" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_PLACE_END_RODS, who);
            case "place_beacons" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_PLACE_BEACONS, who);
            case "void_creatures" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_VOID_CREATURES, who);
            case "dimensional_rifts" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_DIMENSIONAL_RIFTS, who);
            case "stability_cores" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_STABILITY_CORES, who);
            case "stabilization" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_STABILIZATION, who);
            case "portal_activator" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_PORTAL_ACTIVATOR, who);
            case "activation_catalyst" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_ACTIVATION_CATALYST, who);
            case "activate_portal" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_ACTIVATE_PORTAL, who);
            case "portal_key" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_PORTAL_KEY, who);
            case "enter_portal" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_ENTER_PORTAL, who);
            case "void_dimension" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_VOID_DIMENSION, who);
            case "void_guardians" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_VOID_GUARDIANS, who);
            case "dimension_proof" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_DIMENSION_PROOF, who);
            case "return_portal" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_RETURN_PORTAL, who);
            case "report_success" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_REPORT_SUCCESS, who);
            case "realm_navigator" -> LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_OBJECTIVES_REALM_NAVIGATOR, who);
            default -> List.of(Component.text("Objective: " + objective.getId()));
        };
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_REALM_PORTAL_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_REALM_PORTAL_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_REALM_PORTAL_DECLINE, who);
    }
}