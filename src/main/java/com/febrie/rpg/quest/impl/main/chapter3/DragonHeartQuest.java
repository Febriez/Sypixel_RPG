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
 * 용의 심장 - 메인 스토리 퀘스트 (Chapter 3 Finale)
 * 전설의 용의 심장을 획득하는 최종 퀘스트
 *
 * @author Febrie
 */
public class DragonHeartQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public DragonHeartQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_DRAGON_HEART)
                .objectives(List.of(
                        // 전설의 시작
                        new InteractNPCObjective("ancient_dragon", "ancient_dragon", 1),
                        new CollectItemObjective("prophecy_scroll", Material.WRITTEN_BOOK, 1),
                        new InteractNPCObjective("elder_sage", "elder_sage", 1),
                        new VisitLocationObjective("heart_shrine", "dragon_heart_shrine"),
                        
                        // 세 가지 열쇠 수집
                        // 첫 번째 열쇠 - 용암의 열쇠
                        new VisitLocationObjective("lava_depths", "volcanic_depths"),
                        new KillMobObjective("lava_dragons", EntityType.MAGMA_CUBE, 50),
                        new CollectItemObjective("lava_crystals", Material.MAGMA_BLOCK, 30),
                        new KillMobObjective("fire_lord", EntityType.BLAZE, 25),
                        new CollectItemObjective("fire_key", Material.BLAZE_ROD, 1),
                        
                        // 두 번째 열쇠 - 얼음의 열쇠
                        new VisitLocationObjective("frozen_peaks", "ice_dragon_peaks"),
                        new KillMobObjective("ice_dragons", EntityType.POLAR_BEAR, 30),
                        new CollectItemObjective("ice_crystals", Material.PACKED_ICE, 50),
                        new KillMobObjective("frost_guardian", EntityType.STRAY, 40),
                        new CollectItemObjective("ice_key", Material.BLUE_ICE, 1),
                        
                        // 세 번째 열쇠 - 폭풍의 열쇠
                        new VisitLocationObjective("storm_nexus", "storm_dragon_nexus"),
                        new KillMobObjective("storm_dragons", EntityType.PHANTOM, 60),
                        new CollectItemObjective("storm_crystals", Material.END_ROD, 20),
                        new KillMobObjective("thunder_lord", EntityType.WITCH, 30),
                        new CollectItemObjective("storm_key", Material.LIGHTNING_ROD, 1),
                        
                        // 심장의 방 진입
                        new VisitLocationObjective("heart_chamber_entrance", "heart_chamber_entrance"),
                        new PlaceBlockObjective("place_fire_key", Material.REDSTONE_BLOCK, 1),
                        new PlaceBlockObjective("place_ice_key", Material.DIAMOND_BLOCK, 1),
                        new PlaceBlockObjective("place_storm_key", Material.EMERALD_BLOCK, 1),
                        new SurviveObjective("key_ritual", 300), // 5분 의식
                        
                        // 수호자들과의 전투
                        new VisitLocationObjective("guardian_arena", "heart_guardian_arena"),
                        new KillMobObjective("fire_guardian", EntityType.ELDER_GUARDIAN, 3),
                        new KillMobObjective("ice_guardian", EntityType.ELDER_GUARDIAN, 3),
                        new KillMobObjective("storm_guardian", EntityType.ELDER_GUARDIAN, 3),
                        new CollectItemObjective("guardian_essence", Material.NETHER_STAR, 3),
                        
                        // 고대 용왕과의 대결
                        new InteractNPCObjective("dragon_emperor", "ancient_dragon_emperor", 1),
                        new KillMobObjective("dragon_emperor_battle", EntityType.ENDER_DRAGON, 3),
                        new SurviveObjective("emperor_wrath", 900), // 15분 생존
                        new CollectItemObjective("emperor_crown", Material.DRAGON_HEAD, 1),
                        
                        // 용의 심장 획득
                        new VisitLocationObjective("heart_core", "dragon_heart_core"),
                        new InteractNPCObjective("heart_guardian", "heart_guardian_spirit", 1),
                        new CollectItemObjective("dragon_heart_fragment", Material.NETHER_STAR, 5),
                        new PlaceBlockObjective("assemble_heart", Material.BEACON, 1),
                        new CollectItemObjective("true_dragon_heart", Material.DRAGON_EGG, 1),
                        
                        // 챕터 완결
                        new InteractNPCObjective("ancient_dragon_finale", "ancient_dragon", 1),
                        new CollectItemObjective("dragon_blessing_eternal", Material.ENCHANTED_GOLDEN_APPLE, 1),
                        new CollectItemObjective("dragon_lord_title", Material.WRITTEN_BOOK, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 50000)
                        .addCurrency(CurrencyType.DIAMOND, 500)
                        .addItem(new ItemStack(Material.DRAGON_EGG))
                        .addItem(new ItemStack(Material.NETHER_STAR, 5))
                        .addItem(new ItemStack(Material.NETHERITE_INGOT, 10))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 10))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 5))
                        .addItem(new ItemStack(Material.BEACON, 3))
                        .addExperience(25000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_SKY_FORTRESS)
                .minLevel(65)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_DRAGON_HEART_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "ancient_dragon" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_ANCIENT_DRAGON, who);
            case "prophecy_scroll" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_PROPHECY_SCROLL, who);
            case "elder_sage" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_ELDER_SAGE, who);
            case "heart_shrine" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_HEART_SHRINE, who);
            case "lava_depths" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_LAVA_DEPTHS, who);
            case "lava_dragons" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_LAVA_DRAGONS, who);
            case "lava_crystals" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_LAVA_CRYSTALS, who);
            case "fire_lord" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_FIRE_LORD, who);
            case "fire_key" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_FIRE_KEY, who);
            case "frozen_peaks" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_FROZEN_PEAKS, who);
            case "ice_dragons" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_ICE_DRAGONS, who);
            case "ice_crystals" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_ICE_CRYSTALS, who);
            case "frost_guardian" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_FROST_GUARDIAN, who);
            case "ice_key" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_ICE_KEY, who);
            case "storm_nexus" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_STORM_NEXUS, who);
            case "storm_dragons" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_STORM_DRAGONS, who);
            case "storm_crystals" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_STORM_CRYSTALS, who);
            case "thunder_lord" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_THUNDER_LORD, who);
            case "storm_key" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_STORM_KEY, who);
            case "heart_chamber_entrance" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_HEART_CHAMBER_ENTRANCE, who);
            case "place_fire_key" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_PLACE_FIRE_KEY, who);
            case "place_ice_key" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_PLACE_ICE_KEY, who);
            case "place_storm_key" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_PLACE_STORM_KEY, who);
            case "key_ritual" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_KEY_RITUAL, who);
            case "guardian_arena" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_GUARDIAN_ARENA, who);
            case "fire_guardian" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_FIRE_GUARDIAN, who);
            case "ice_guardian" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_ICE_GUARDIAN, who);
            case "storm_guardian" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_STORM_GUARDIAN, who);
            case "guardian_essence" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_GUARDIAN_ESSENCE, who);
            case "dragon_emperor" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_DRAGON_EMPEROR, who);
            case "dragon_emperor_battle" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_DRAGON_EMPEROR_BATTLE, who);
            case "emperor_wrath" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_EMPEROR_WRATH, who);
            case "emperor_crown" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_EMPEROR_CROWN, who);
            case "heart_core" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_HEART_CORE, who);
            case "heart_guardian" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_HEART_GUARDIAN, who);
            case "dragon_heart_fragment" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_DRAGON_HEART_FRAGMENT, who);
            case "assemble_heart" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_ASSEMBLE_HEART, who);
            case "true_dragon_heart" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_TRUE_DRAGON_HEART, who);
            case "ancient_dragon_finale" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_ANCIENT_DRAGON_FINALE, who);
            case "dragon_blessing_eternal" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_DRAGON_BLESSING_ETERNAL, who);
            case "dragon_lord_title" -> LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_OBJECTIVES_DRAGON_LORD_TITLE, who);
            default -> List.of(Component.text("Objective: " + objective.getId()));
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_DRAGON_HEART_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_DRAGON_HEART_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_DRAGON_HEART_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_DRAGON_HEART_DECLINE, who);
    }
}