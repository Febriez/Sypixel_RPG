package com.febrie.rpg.quest.impl.daily;

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
import java.util.ArrayList;

/**
 * 일일 탐험 - 일일 퀘스트
 * 매일 새로운 지역을 탐험하고 발견하는 모험 퀘스트
 *
 * @author Febrie
 */
public class DailyExplorationQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public DailyExplorationQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.DAILY_EXPLORATION)
                .objectives(List.of(
                        // 탐험 시작
                        new InteractNPCObjective("explorer_guild", "explorer_guild_master"), // 탐험가 길드장
                        new CollectItemObjective("bread_collect", Material.BREAD, 10),
                        new CollectItemObjective("torch_collect", Material.TORCH, 32),
                        new CollectItemObjective("iron_pickaxe_collect", Material.IRON_PICKAXE, 1),
                        
                        // 첫 번째 지역 - 버려진 광산
                        new VisitLocationObjective("abandoned_mine", "old_mineshaft_entrance"),
                        new PlaceBlockObjective("light_mine", Material.TORCH, 10),
                        new BreakBlockObjective("mine_ores", Material.IRON_ORE, 20),
                        new CollectItemObjective("gold_nugget_collect", Material.GOLD_NUGGET, 15),
                        new KillMobObjective("mine_creatures", EntityType.CAVE_SPIDER, 20),
                        
                        // 두 번째 지역 - 숨겨진 폭포
                        new VisitLocationObjective("hidden_waterfall", "secret_waterfall"),
                        new CollectItemObjective("prismarine_shard_collect", Material.PRISMARINE_SHARD, 10),
                        new FishingObjective("waterfall_fishing", 10),
                        new CollectItemObjective("tropical_fish_collect", Material.TROPICAL_FISH, 5),
                        new KillMobObjective("water_guardians", EntityType.DROWNED, 15),
                        
                        // 세 번째 지역 - 고대 유적
                        new VisitLocationObjective("ancient_ruins", "forgotten_temple"),
                        new BreakBlockObjective("clear_rubble", Material.COBBLESTONE, 30),
                        new CollectItemObjective("flower_pot_collect", Material.FLOWER_POT, 5),
                        new CollectItemObjective("emerald_collect", Material.EMERALD, 10),
                        new KillMobObjective("ruin_guardians", EntityType.SKELETON, 25),
                        
                        // 네 번째 지역 - 신비한 숲
                        new VisitLocationObjective("mystic_forest", "enchanted_grove"),
                        new HarvestObjective("gather_herbs", Material.WHEAT, 20),
                        new CollectItemObjective("azure_bluet_collect", Material.AZURE_BLUET, 10),
                        new CollectItemObjective("red_mushroom_collect", Material.RED_MUSHROOM, 15),
                        new KillMobObjective("forest_spirits", EntityType.ZOMBIE, 20),
                        
                        // 다섯 번째 지역 - 용암 동굴
                        new VisitLocationObjective("lava_cavern", "volcanic_cave"),
                        new PlaceBlockObjective("build_bridge", Material.COBBLESTONE, 20),
                        new CollectItemObjective("obsidian_collect", Material.OBSIDIAN, 10),
                        new CollectItemObjective("magma_cream_collect", Material.MAGMA_CREAM, 5),
                        new KillMobObjective("lava_creatures", EntityType.MAGMA_CUBE, 15),
                        new SurviveObjective("heat_survival", 300), // 5분간 열기 견디기
                        
                        // 지도 작성
                        new CraftItemObjective("map_craft", Material.MAP, 5),
                        new CollectItemObjective("filled_map_collect", Material.FILLED_MAP, 5),
                        
                        // 보고서 작성
                        new CollectItemObjective("written_book_collect", Material.WRITTEN_BOOK, 1),
                        new DeliverItemObjective("gold_nugget_deliver", Material.GOLD_NUGGET, 15, "explorer_guild"),
                        new DeliverItemObjective("filled_map_deliver", Material.FILLED_MAP, 5, "explorer_guild"),
                        new DeliverItemObjective("written_book_deliver", Material.WRITTEN_BOOK, 1, "explorer_guild"),
                        new InteractNPCObjective("exploration_complete", "explorer_guild_master")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 3000)
                        .addCurrency(CurrencyType.DIAMOND, 20)
                        .addItem(new ItemStack(Material.COMPASS))
                        .addItem(new ItemStack(Material.MAP, 5))
                        .addItem(new ItemStack(Material.SPYGLASS))
                        .addItem(new ItemStack(Material.LEATHER_BOOTS)) // 탐험가 부츠
                        .addItem(new ItemStack(Material.ENDER_PEARL, 3))
                        .addExperience(2000)
                        .build())
                .sequential(false)  // 자유로운 탐험
                .repeatable(true)
                .daily(true)       // 일일 퀘스트
                .category(QuestCategory.DAILY)
                .minLevel(15)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_INFO, who);
    }

        @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "explorer_guild" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_EXPLORER_GUILD, who);
            case "bread_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_BREAD_COLLECT, who);
            case "torch_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_TORCH_COLLECT, who);
            case "iron_pickaxe_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_IRON_PICKAXE_COLLECT, who);
            case "abandoned_mine" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_ABANDONED_MINE, who);
            case "light_mine" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_LIGHT_MINE, who);
            case "mine_ores" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_MINE_ORES, who);
            case "gold_nugget_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_GOLD_NUGGET_COLLECT, who);
            case "mine_creatures" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_MINE_CREATURES, who);
            case "hidden_waterfall" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_HIDDEN_WATERFALL, who);
            case "prismarine_shard_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_PRISMARINE_SHARD_COLLECT, who);
            case "waterfall_fishing" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_WATERFALL_FISHING, who);
            case "tropical_fish_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_TROPICAL_FISH_COLLECT, who);
            case "water_guardians" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_WATER_GUARDIANS, who);
            case "ancient_ruins" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_ANCIENT_RUINS, who);
            case "clear_rubble" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_CLEAR_RUBBLE, who);
            case "flower_pot_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_FLOWER_POT_COLLECT, who);
            case "emerald_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_EMERALD_COLLECT, who);
            case "ruin_guardians" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_RUIN_GUARDIANS, who);
            case "mystic_forest" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_MYSTIC_FOREST, who);
            case "gather_herbs" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_GATHER_HERBS, who);
            case "azure_bluet_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_AZURE_BLUET_COLLECT, who);
            case "red_mushroom_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_RED_MUSHROOM_COLLECT, who);
            case "forest_spirits" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_FOREST_SPIRITS, who);
            case "lava_cavern" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_LAVA_CAVERN, who);
            case "build_bridge" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_BUILD_BRIDGE, who);
            case "obsidian_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_OBSIDIAN_COLLECT, who);
            case "magma_cream_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_MAGMA_CREAM_COLLECT, who);
            case "lava_creatures" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_LAVA_CREATURES, who);
            case "heat_survival" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_HEAT_SURVIVAL, who);
            case "map_craft" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_MAP_CRAFT, who);
            case "filled_map_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_FILLED_MAP_COLLECT, who);
            case "written_book_collect" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_WRITTEN_BOOK_COLLECT, who);
            case "gold_nugget_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_GOLD_NUGGET_DELIVER, who);
            case "filled_map_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_FILLED_MAP_DELIVER, who);
            case "written_book_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_WRITTEN_BOOK_DELIVER, who);
            case "exploration_complete" -> LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_OBJECTIVES_EXPLORATION_COMPLETE, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 10;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_DAILY_EXPLORATION_DECLINE, who);
    }
}