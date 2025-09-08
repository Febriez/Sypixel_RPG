package com.febrie.rpg.quest.impl.main.chapter3;

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
                        new InteractNPCObjective("sky_navigator", "sky_navigator", 1),
                        new CollectItemObjective("flying_mount", Material.ELYTRA, 1),
                        new CollectItemObjective("fireworks", Material.FIREWORK_ROCKET, 64),
                        new VisitLocationObjective("cloud_peaks", "cloud_peaks"),
                        new CollectItemObjective("sky_map", Material.MAP, 1),
                        
                        // 요새 진입 준비
                        new CollectItemObjective("wind_crystals", Material.PRISMARINE_CRYSTALS, 30),
                        new CollectItemObjective("cloud_essence", Material.QUARTZ, 50),
                        new KillMobObjective("sky_guardians", EntityType.PHANTOM, 40),
                        new CollectItemObjective("phantom_wings", Material.PHANTOM_MEMBRANE, 20),
                        new InteractNPCObjective("fortress_scout", "fortress_scout", 1),
                        
                        // 외벽 돌파
                        new VisitLocationObjective("fortress_gates", "sky_fortress_gates"),
                        new BreakBlockObjective("destroy_barriers", Material.IRON_BARS, 100),
                        new KillMobObjective("gate_defenders", EntityType.IRON_GOLEM, 15),
                        new CollectItemObjective("gate_key", Material.TRIPWIRE_HOOK, 3),
                        new PlaceBlockObjective("place_explosives", Material.TNT, 20),
                        
                        // 첫 번째 탑 - 바람의 탑
                        new VisitLocationObjective("wind_tower", "wind_tower"),
                        new KillMobObjective("wind_elementals", EntityType.VEX, 50),
                        new CollectItemObjective("wind_orb", Material.ENDER_PEARL, 1),
                        new SurviveObjective("wind_trial", 300), // 5분 생존
                        new InteractNPCObjective("wind_keeper", "wind_keeper", 1),
                        
                        // 두 번째 탑 - 번개의 탑
                        new VisitLocationObjective("lightning_tower", "lightning_tower"),
                        new KillMobObjective("storm_creatures", EntityType.WITCH, 20),
                        new CollectItemObjective("lightning_rods", Material.LIGHTNING_ROD, 10),
                        new PlaceBlockObjective("place_rods", Material.LIGHTNING_ROD, 10),
                        new CollectItemObjective("storm_orb", Material.HEART_OF_THE_SEA, 1),
                        
                        // 세 번째 탑 - 구름의 탑
                        new VisitLocationObjective("cloud_tower", "cloud_tower"),
                        new CollectItemObjective("cloud_blocks", Material.WHITE_WOOL, 100),
                        new PlaceBlockObjective("build_cloud_bridge", Material.WHITE_WOOL, 50),
                        new KillMobObjective("cloud_sentinels", EntityType.POLAR_BEAR, 10),
                        new CollectItemObjective("cloud_orb", Material.SNOWBALL, 1),
                        
                        // 중앙 첨탑
                        new VisitLocationObjective("central_spire", "central_spire"),
                        new PlaceBlockObjective("activate_orbs", Material.BEACON, 3),
                        new KillMobObjective("spire_guardians", EntityType.ELDER_GUARDIAN, 8),
                        new SurviveObjective("spire_defense", 600), // 10분 방어
                        
                        // 요새 사령관과 대결
                        new InteractNPCObjective("fortress_commander", "fortress_commander", 1),
                        new KillMobObjective("commander_guards", EntityType.VINDICATOR, 30),
                        new KillMobObjective("sky_fortress_commander", EntityType.RAVAGER, 5),
                        new CollectItemObjective("commander_badge", Material.GOLDEN_APPLE, 1),
                        
                        // 요새 점령
                        new PlaceBlockObjective("place_banner", Material.WHITE_BANNER, 1),
                        new InteractNPCObjective("claim_fortress", "sky_navigator", 1),
                        new CollectItemObjective("fortress_deed", Material.WRITTEN_BOOK, 1),
                        new CollectItemObjective("sky_crown", Material.GOLDEN_HELMET, 1)
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
        return LangManager.text(LangKey.QUEST_MAIN_SKY_FORTRESS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "sky_navigator" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_SKY_NAVIGATOR, who);
            case "flying_mount" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_FLYING_MOUNT, who);
            case "fireworks" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_FIREWORKS, who);
            case "cloud_peaks" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_CLOUD_PEAKS, who);
            case "sky_map" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_SKY_MAP, who);
            case "wind_crystals" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_WIND_CRYSTALS, who);
            case "cloud_essence" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_CLOUD_ESSENCE, who);
            case "sky_guardians" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_SKY_GUARDIANS, who);
            case "phantom_wings" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_PHANTOM_WINGS, who);
            case "fortress_scout" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_FORTRESS_SCOUT, who);
            case "fortress_gates" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_FORTRESS_GATES, who);
            case "destroy_barriers" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_DESTROY_BARRIERS, who);
            case "gate_defenders" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_GATE_DEFENDERS, who);
            case "gate_key" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_GATE_KEY, who);
            case "place_explosives" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_PLACE_EXPLOSIVES, who);
            case "wind_tower" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_WIND_TOWER, who);
            case "wind_elementals" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_WIND_ELEMENTALS, who);
            case "wind_orb" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_WIND_ORB, who);
            case "wind_trial" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_WIND_TRIAL, who);
            case "wind_keeper" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_WIND_KEEPER, who);
            case "lightning_tower" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_LIGHTNING_TOWER, who);
            case "storm_creatures" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_STORM_CREATURES, who);
            case "lightning_rods" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_LIGHTNING_RODS, who);
            case "place_rods" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_PLACE_RODS, who);
            case "storm_orb" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_STORM_ORB, who);
            case "cloud_tower" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_CLOUD_TOWER, who);
            case "cloud_blocks" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_CLOUD_BLOCKS, who);
            case "build_cloud_bridge" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_BUILD_CLOUD_BRIDGE, who);
            case "cloud_sentinels" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_CLOUD_SENTINELS, who);
            case "cloud_orb" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_CLOUD_ORB, who);
            case "central_spire" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_CENTRAL_SPIRE, who);
            case "activate_orbs" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_ACTIVATE_ORBS, who);
            case "spire_guardians" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_SPIRE_GUARDIANS, who);
            case "spire_defense" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_SPIRE_DEFENSE, who);
            case "fortress_commander" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_FORTRESS_COMMANDER, who);
            case "commander_guards" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_COMMANDER_GUARDS, who);
            case "sky_fortress_commander" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_SKY_FORTRESS_COMMANDER, who);
            case "commander_badge" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_COMMANDER_BADGE, who);
            case "place_banner" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_PLACE_BANNER, who);
            case "claim_fortress" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_CLAIM_FORTRESS, who);
            case "fortress_deed" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_FORTRESS_DEED, who);
            case "sky_crown" -> LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_OBJECTIVES_SKY_CROWN, who);
            default -> List.of(Component.text("Objective: " + objective.getId()));
        };
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_SKY_FORTRESS_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_SKY_FORTRESS_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_SKY_FORTRESS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_SKY_FORTRESS_DECLINE, who);
    }
}