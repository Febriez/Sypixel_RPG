package com.febrie.rpg.quest.impl.main.chapter3;

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
 * 하늘 요새 - 메인 스토리 퀘스트 (Chapter 3)
 * 용족의 하늘 요새를 공략하는 퀘스트
 *
 * @author Febrie
 */
public class SkyFortressQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public SkyFortressQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_SKY_FORTRESS)
                .objectives(List.of(
                        // 하늘 요새 발견
                        new InteractNPCObjective("sky_navigator", "sky_navigator"),
                        new CollectItemObjective("elytra_collect", Material.ELYTRA, 1),
                        new CollectItemObjective("firework_rocket_collect", Material.FIREWORK_ROCKET, 64),
                        new VisitLocationObjective("cloud_peaks", "cloud_peaks"),
                        new CollectItemObjective("map_collect", Material.MAP, 1),
                        
                        // 요새 진입 준비
                        new CollectItemObjective("prismarine_crystals_collect", Material.PRISMARINE_CRYSTALS, 30),
                        new CollectItemObjective("quartz_collect", Material.QUARTZ, 50),
                        new KillMobObjective("sky_guardians", EntityType.PHANTOM, 40),
                        new CollectItemObjective("phantom_membrane_collect", Material.PHANTOM_MEMBRANE, 20),
                        new InteractNPCObjective("fortress_scout", "fortress_scout"),
                        
                        // 외벽 돌파
                        new VisitLocationObjective("fortress_gates", "sky_fortress_gates"),
                        new BreakBlockObjective("destroy_barriers", Material.IRON_BARS, 100),
                        new KillMobObjective("gate_defenders", EntityType.IRON_GOLEM, 15),
                        new CollectItemObjective("tripwire_hook_collect", Material.TRIPWIRE_HOOK, 3),
                        new PlaceBlockObjective("place_explosives", Material.TNT, 20),
                        
                        // 첫 번째 탑 - 바람의 탑
                        new VisitLocationObjective("wind_tower", "wind_tower"),
                        new KillMobObjective("wind_elementals", EntityType.VEX, 50),
                        new CollectItemObjective("ender_pearl_collect", Material.ENDER_PEARL, 1),
                        new SurviveObjective("wind_trial", 300), // 5분 생존
                        new InteractNPCObjective("wind_keeper", "wind_keeper"),
                        
                        // 두 번째 탑 - 번개의 탑
                        new VisitLocationObjective("lightning_tower", "lightning_tower"),
                        new KillMobObjective("storm_creatures", EntityType.WITCH, 20),
                        new CollectItemObjective("lightning_rod_collect", Material.LIGHTNING_ROD, 10),
                        new PlaceBlockObjective("place_rods", Material.LIGHTNING_ROD, 10),
                        new CollectItemObjective("heart_of_the_sea_collect", Material.HEART_OF_THE_SEA, 1),
                        
                        // 세 번째 탑 - 구름의 탑
                        new VisitLocationObjective("cloud_tower", "cloud_tower"),
                        new CollectItemObjective("white_wool_collect", Material.WHITE_WOOL, 100),
                        new PlaceBlockObjective("build_cloud_bridge", Material.WHITE_WOOL, 50),
                        new KillMobObjective("cloud_sentinels", EntityType.POLAR_BEAR, 10),
                        new CollectItemObjective("snowball_collect", Material.SNOWBALL, 1),
                        
                        // 중앙 첨탑
                        new VisitLocationObjective("central_spire", "central_spire"),
                        new PlaceBlockObjective("activate_orbs", Material.BEACON, 3),
                        new KillMobObjective("spire_guardians", EntityType.ELDER_GUARDIAN, 8),
                        new SurviveObjective("spire_defense", 600), // 10분 방어
                        
                        // 요새 사령관과 대결
                        new InteractNPCObjective("fortress_commander", "fortress_commander"),
                        new KillMobObjective("commander_guards", EntityType.VINDICATOR, 30),
                        new KillMobObjective("sky_fortress_commander", EntityType.RAVAGER, 5),
                        new CollectItemObjective("golden_apple_collect", Material.GOLDEN_APPLE, 1),
                        
                        // 요새 점령
                        new PlaceBlockObjective("place_banner", Material.WHITE_BANNER, 1),
                        new InteractNPCObjective("claim_fortress", "sky_navigator"),
                        new CollectItemObjective("written_book_collect", Material.WRITTEN_BOOK, 1),
                        new CollectItemObjective("golden_helmet_collect", Material.GOLDEN_HELMET, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 35000)
                        .addCurrency(CurrencyType.DIAMOND, 300)
                        .addItem(new ItemStack(Material.ELYTRA)) // 강화된 엘리트라
                        .addItem(new ItemStack(Material.TRIDENT))
                        .addItem(new ItemStack(Material.BEACON, 2))
                        .addItem(new ItemStack(Material.SHULKER_BOX, 5))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 3))
                        .addExperience(18000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_DRAGON_PACT)
                .minLevel(60)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "sky_navigator" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_SKY_NAVIGATOR, who);
            case "elytra_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_ELYTRA_COLLECT, who);
            case "firework_rocket_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_FIREWORK_ROCKET_COLLECT, who);
            case "cloud_peaks" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_CLOUD_PEAKS, who);
            case "map_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_MAP_COLLECT, who);
            case "prismarine_crystals_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_PRISMARINE_CRYSTALS_COLLECT, who);
            case "quartz_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_QUARTZ_COLLECT, who);
            case "sky_guardians" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_SKY_GUARDIANS, who);
            case "phantom_membrane_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_PHANTOM_MEMBRANE_COLLECT, who);
            case "fortress_scout" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_FORTRESS_SCOUT, who);
            case "fortress_gates" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_FORTRESS_GATES, who);
            case "destroy_barriers" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_DESTROY_BARRIERS, who);
            case "gate_defenders" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_GATE_DEFENDERS, who);
            case "tripwire_hook_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_TRIPWIRE_HOOK_COLLECT, who);
            case "place_explosives" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_PLACE_EXPLOSIVES, who);
            case "wind_tower" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_WIND_TOWER, who);
            case "wind_elementals" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_WIND_ELEMENTALS, who);
            case "ender_pearl_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_ENDER_PEARL_COLLECT, who);
            case "wind_trial" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_WIND_TRIAL, who);
            case "wind_keeper" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_WIND_KEEPER, who);
            case "lightning_tower" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_LIGHTNING_TOWER, who);
            case "storm_creatures" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_STORM_CREATURES, who);
            case "lightning_rod_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_LIGHTNING_ROD_COLLECT, who);
            case "place_rods" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_PLACE_RODS, who);
            case "heart_of_the_sea_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_HEART_OF_THE_SEA_COLLECT, who);
            case "cloud_tower" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_CLOUD_TOWER, who);
            case "white_wool_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_WHITE_WOOL_COLLECT, who);
            case "build_cloud_bridge" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_BUILD_CLOUD_BRIDGE, who);
            case "cloud_sentinels" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_CLOUD_SENTINELS, who);
            case "snowball_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_SNOWBALL_COLLECT, who);
            case "central_spire" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_CENTRAL_SPIRE, who);
            case "activate_orbs" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_ACTIVATE_ORBS, who);
            case "spire_guardians" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_SPIRE_GUARDIANS, who);
            case "spire_defense" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_SPIRE_DEFENSE, who);
            case "fortress_commander" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_FORTRESS_COMMANDER, who);
            case "commander_guards" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_COMMANDER_GUARDS, who);
            case "sky_fortress_commander" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_SKY_FORTRESS_COMMANDER, who);
            case "golden_apple_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_GOLDEN_APPLE_COLLECT, who);
            case "place_banner" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_PLACE_BANNER, who);
            case "claim_fortress" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_CLAIM_FORTRESS, who);
            case "written_book_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_WRITTEN_BOOK_COLLECT, who);
            case "golden_helmet_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_GOLDEN_HELMET_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_SKY_FORTRESS_DECLINE, who);
    }
}