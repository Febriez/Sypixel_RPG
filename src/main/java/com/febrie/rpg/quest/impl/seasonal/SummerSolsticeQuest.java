package com.febrie.rpg.quest.impl.seasonal;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import java.util.ArrayList;
import java.util.List;

/**
 * 여름 하지 축제 - 계절 이벤트 퀘스트
 * 여름 최대 낮 시간을 기념하는 태양 축제
 *
 * @author Febrie
 */
public class SummerSolsticeQuest extends Quest {
    
    public SummerSolsticeQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SEASON_SUMMER_SOLSTICE)
                .objectives(List.of(
                        // 축제 시작
                        new InteractNPCObjective("festival_coordinator", "summer_coordinator"),
                        new VisitLocationObjective("festival_grounds", "summer_festival_plaza"),
                        
                        // 태양 제단 준비
                        new CollectItemObjective("sunflower_collect", Material.SUNFLOWER, 25),
                        new CollectItemObjective("gold_ingot_collect", Material.GOLD_INGOT, 10),
                        new CraftItemObjective("golden_apple_craft", Material.GOLDEN_APPLE, 5),
                        new PlaceBlockObjective("build_altar", Material.GOLD_BLOCK, 9),
                        
                        // 불꽃 축제
                        new CollectItemObjective("gunpowder_collect", Material.GUNPOWDER, 64),
                        new CollectItemObjective("red_dye_collect", Material.RED_DYE, 16),
                        new CraftItemObjective("firework_rocket_craft", Material.FIREWORK_ROCKET, 32),
                        new CollectItemObjective("firework_rocket_collect", Material.FIREWORK_ROCKET, 20),
                        
                        // 여름 음식 제작
                        new CollectItemObjective("melon_slice_collect", Material.MELON_SLICE, 50),
                        new CollectItemObjective("ice_collect", Material.ICE, 20),
                        new CraftItemObjective("melon_craft", Material.MELON, 10),
                        new CraftItemObjective("potion_craft", Material.POTION, 8),
                        
                        // 태양의 시험
                        new VisitLocationObjective("desert_temple", "scorching_desert"),
                        new SurviveObjective("heat_endurance", 300), // 5분간 생존
                        new KillMobObjective("defeat_blazes", EntityType.BLAZE, 15),
                        new CollectItemObjective("blaze_powder_collect", Material.BLAZE_POWDER, 10),
                        
                        // 수영 대회
                        new VisitLocationObjective("beach_contest", "sunny_beach"),
                        new SurviveObjective("swimming_race", 200), // 200초 생존 (수영 대회)
                        new CollectItemObjective("prismarine_shard_collect", Material.PRISMARINE_SHARD, 15),
                        new KillMobObjective("guardian_challenge", EntityType.GUARDIAN, 5),
                        
                        // 여름 꽃 축제
                        new CollectItemObjective("orange_tulip_collect", Material.ORANGE_TULIP, 20),
                        new CollectItemObjective("dandelion_collect", Material.DANDELION, 30),
                        new PlaceBlockObjective("flower_garden", Material.SUNFLOWER, 15),
                        new DeliverItemObjective("poppy_deliver", Material.POPPY, 25, "villager"),
                        
                        // 태양 의식
                        new PayCurrencyObjective("sun_offering", CurrencyType.GOLD, 1000),
                        new CollectItemObjective("golden_apple_collect", Material.GOLDEN_APPLE, 3),
                        new InteractNPCObjective("sun_priest", "solar_priest"),
                        
                        // 축제 마무리
                        new DeliverItemObjective("cake_deliver", Material.CAKE, 3, "villager"),
                        new InteractNPCObjective("festival_complete", "summer_coordinator")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 1500)
                        .addCurrency(CurrencyType.DIAMOND, 25)
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 10)) // 태양의 축복 사과
                        .addItem(new ItemStack(Material.SUNFLOWER, 32))
                        .addItem(new ItemStack(Material.FIRE_CHARGE, 20))
                        .addItem(new ItemStack(Material.BLAZE_ROD, 5))
                        .addExperience(2000)
                        .build())
                .sequential(true)
                .repeatable(true)  // 매년 반복 가능
                .category(QuestCategory.EVENT)
                .minLevel(10)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "festival_coordinator" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_FESTIVAL_COORDINATOR, who);
            case "festival_grounds" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_FESTIVAL_GROUNDS, who);
            case "sunflower_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_SUNFLOWER_COLLECT, who);
            case "gold_ingot_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_GOLD_INGOT_COLLECT, who);
            case "golden_apple_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_GOLDEN_APPLE_CRAFT, who);
            case "build_altar" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_BUILD_ALTAR, who);
            case "gunpowder_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_GUNPOWDER_COLLECT, who);
            case "red_dye_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_RED_DYE_COLLECT, who);
            case "firework_rocket_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_FIREWORK_ROCKET_CRAFT, who);
            case "firework_rocket_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_FIREWORK_ROCKET_COLLECT, who);
            case "melon_slice_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_MELON_SLICE_COLLECT, who);
            case "ice_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_ICE_COLLECT, who);
            case "melon_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_MELON_CRAFT, who);
            case "potion_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_POTION_CRAFT, who);
            case "desert_temple" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_DESERT_TEMPLE, who);
            case "heat_endurance" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_HEAT_ENDURANCE, who);
            case "defeat_blazes" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_DEFEAT_BLAZES, who);
            case "blaze_powder_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_BLAZE_POWDER_COLLECT, who);
            case "beach_contest" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_BEACH_CONTEST, who);
            case "swimming_race" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_SWIMMING_RACE, who);
            case "prismarine_shard_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_PRISMARINE_SHARD_COLLECT, who);
            case "guardian_challenge" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_GUARDIAN_CHALLENGE, who);
            case "orange_tulip_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_ORANGE_TULIP_COLLECT, who);
            case "dandelion_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_DANDELION_COLLECT, who);
            case "flower_garden" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_FLOWER_GARDEN, who);
            case "poppy_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_POPPY_DELIVER, who);
            case "sun_offering" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_SUN_OFFERING, who);
            case "golden_apple_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_GOLDEN_APPLE_COLLECT, who);
            case "sun_priest" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_SUN_PRIEST, who);
            case "cake_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_CAKE_DELIVER, who);
            case "festival_complete" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_SUMMER_SOLSTICE_OBJECTIVES_FESTIVAL_COMPLETE, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 8;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_DECLINE, who);
    }
}