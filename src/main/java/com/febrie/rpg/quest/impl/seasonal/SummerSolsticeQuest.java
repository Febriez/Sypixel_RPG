package com.febrie.rpg.quest.impl.seasonal;

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
                        new InteractNPCObjective("festival_coordinator", "summer_coordinator", 1),
                        new VisitLocationObjective("festival_grounds", "summer_festival_plaza"),
                        
                        // 태양 제단 준비
                        new CollectItemObjective("gather_sunflowers", Material.SUNFLOWER, 25),
                        new CollectItemObjective("collect_gold_ingots", Material.GOLD_INGOT, 10),
                        new CraftItemObjective("craft_golden_apple", Material.GOLDEN_APPLE, 5),
                        new PlaceBlockObjective("build_altar", Material.GOLD_BLOCK, 9),
                        
                        // 불꽃 축제
                        new CollectItemObjective("gather_gunpowder", Material.GUNPOWDER, 64),
                        new CollectItemObjective("collect_dyes", Material.RED_DYE, 16),
                        new CraftItemObjective("craft_fireworks", Material.FIREWORK_ROCKET, 32),
                        new CollectItemObjective("launch_fireworks", Material.FIREWORK_ROCKET, 20),
                        
                        // 여름 음식 제작
                        new CollectItemObjective("harvest_melons", Material.MELON_SLICE, 50),
                        new CollectItemObjective("collect_ice", Material.ICE, 20),
                        new CraftItemObjective("make_melon_blocks", Material.MELON, 10),
                        new CraftItemObjective("brew_cooling_potions", Material.POTION, 8),
                        
                        // 태양의 시험
                        new VisitLocationObjective("desert_temple", "scorching_desert"),
                        new SurviveObjective("heat_endurance", 300), // 5분간 생존
                        new KillMobObjective("defeat_blazes", EntityType.BLAZE, 15),
                        new CollectItemObjective("solar_essence", Material.BLAZE_POWDER, 10),
                        
                        // 수영 대회
                        new VisitLocationObjective("beach_contest", "sunny_beach"),
                        new SwimObjective("swimming_race", 200), // 200블록 수영
                        new CollectItemObjective("underwater_treasures", Material.PRISMARINE_SHARD, 15),
                        new KillMobObjective("guardian_challenge", EntityType.GUARDIAN, 5),
                        
                        // 여름 꽃 축제
                        new CollectItemObjective("summer_flowers", Material.ORANGE_TULIP, 20),
                        new CollectItemObjective("yellow_flowers", Material.DANDELION, 30),
                        new PlaceBlockObjective("flower_garden", Material.SUNFLOWER, 15),
                        new DeliverItemObjective("flower_crown_delivery", "villager", Material.POPPY, 25),
                        
                        // 태양 의식
                        new PayCurrencyObjective("sun_offering", CurrencyType.GOLD, 1000),
                        new CollectItemObjective("sun_blessing", Material.GOLDEN_APPLE, 3),
                        new InteractNPCObjective("sun_priest", "solar_priest", 1),
                        
                        // 축제 마무리
                        new DeliverItemObjective("festival_treats", "villager", Material.CAKE, 3),
                        new InteractNPCObjective("festival_complete", "summer_coordinator", 1)
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
        return LangManager.text(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "festival_coordinator" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_FESTIVAL_COORDINATOR, who);
            case "festival_grounds" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_FESTIVAL_GROUNDS, who);
            case "gather_sunflowers" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_GATHER_SUNFLOWERS, who);
            case "collect_gold_ingots" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_COLLECT_GOLD_INGOTS, who);
            case "craft_golden_apple" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_CRAFT_GOLDEN_APPLE, who);
            case "build_altar" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_BUILD_ALTAR, who);
            case "gather_gunpowder" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_GATHER_GUNPOWDER, who);
            case "collect_dyes" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_COLLECT_DYES, who);
            case "craft_fireworks" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_CRAFT_FIREWORKS, who);
            case "launch_fireworks" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_LAUNCH_FIREWORKS, who);
            case "harvest_melons" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_HARVEST_MELONS, who);
            case "collect_ice" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_COLLECT_ICE, who);
            case "make_melon_blocks" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_MAKE_MELON_BLOCKS, who);
            case "brew_cooling_potions" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_BREW_COOLING_POTIONS, who);
            case "desert_temple" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_DESERT_TEMPLE, who);
            case "heat_endurance" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_HEAT_ENDURANCE, who);
            case "defeat_blazes" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_DEFEAT_BLAZES, who);
            case "solar_essence" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_SOLAR_ESSENCE, who);
            case "beach_contest" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_BEACH_CONTEST, who);
            case "swimming_race" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_SWIMMING_RACE, who);
            case "underwater_treasures" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_UNDERWATER_TREASURES, who);
            case "guardian_challenge" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_GUARDIAN_CHALLENGE, who);
            case "summer_flowers" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_SUMMER_FLOWERS, who);
            case "yellow_flowers" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_YELLOW_FLOWERS, who);
            case "flower_garden" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_FLOWER_GARDEN, who);
            case "flower_crown_delivery" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_FLOWER_CROWN_DELIVERY, who);
            case "sun_offering" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_SUN_OFFERING, who);
            case "sun_blessing" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_SUN_BLESSING, who);
            case "sun_priest" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_SUN_PRIEST, who);
            case "festival_treats" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_FESTIVAL_TREATS, who);
            case "festival_complete" -> LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_OBJECTIVES_FESTIVAL_COMPLETE, who);
            default -> new ArrayList<>();
        };
    }

    @Override
    public int getDialogCount() {
        return 8;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_SUMMER_SOLSTICE_DECLINE, who);
    }
}