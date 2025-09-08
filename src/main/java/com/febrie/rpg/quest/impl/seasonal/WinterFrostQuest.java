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
 * 겨울 서리 축제 - 계절 이벤트 퀘스트
 * 가장 추운 겨울을 맞아 얼음과 눈의 마법을 다루는 축제
 *
 * @author Febrie
 */
public class WinterFrostQuest extends Quest {
    
    public WinterFrostQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SEASON_WINTER_FROST)
                .objectives(List.of(
                        // 축제 시작
                        new InteractNPCObjective("winter_guardian", "frost_guardian", 1),
                        new VisitLocationObjective("frozen_plaza", "winter_wonderland"),
                        
                        // 얼음 수집
                        new CollectItemObjective("harvest_ice", Material.ICE, 64),
                        new CollectItemObjective("packed_ice", Material.PACKED_ICE, 32),
                        new CollectItemObjective("blue_ice", Material.BLUE_ICE, 16),
                        new CollectItemObjective("snow_blocks", Material.SNOW_BLOCK, 50),
                        
                        // 눈사람 만들기 대회
                        new PlaceBlockObjective("snowman_small", Material.SNOW_BLOCK, 3),
                        new PlaceBlockObjective("snowman_large", Material.SNOW_BLOCK, 10),
                        new PlaceBlockObjective("snowman_decoration", Material.CARVED_PUMPKIN, 5),
                        new CollectItemObjective("snowman_arms", Material.STICK, 10),
                        
                        // 얼음 조각상 제작
                        new CraftItemObjective("ice_sculptures", Material.ICE, 25),
                        new PlaceBlockObjective("ice_art_gallery", Material.BLUE_ICE, 20),
                        new PlaceBlockObjective("ice_castle", Material.ICE, 50),
                        new PlaceBlockObjective("crystal_lights", Material.SEA_LANTERN, 15),
                        
                        // 겨울 동물들과 교감
                        new InteractNPCObjective("polar_bears", "arctic_bear", 1),
                        new FeedAnimalsObjective("feed_wolves", EntityType.WOLF, 10),
                        new TameAnimalsObjective("tame_foxes", EntityType.FOX, 5),
                        new CollectItemObjective("arctic_fish", Material.COD, 30),
                        
                        // 얼음 낚시
                        new VisitLocationObjective("frozen_lake", "ice_fishing_spot"),
                        new BreakBlockObjective("break_ice_holes", Material.ICE, 10),
                        new FishingObjective("ice_fishing", FishingObjective.FishType.SPECIFIC, 25, Material.SALMON),
                        new CollectItemObjective("winter_treasures", Material.PRISMARINE_SHARD, 10),
                        
                        // 서리 마법 수련
                        new CollectItemObjective("frost_crystals", Material.QUARTZ, 40),
                        new CraftItemObjective("brew_frost_potion", Material.POTION, 8),
                        new CraftItemObjective("ice_weapons", Material.DIAMOND_SWORD, 3),
                        new InteractNPCObjective("frost_enchantment", "frost_enchanter", 1),
                        
                        // 겨울 음식 준비
                        new CollectItemObjective("winter_vegetables", Material.CARROT, 50),
                        new CraftItemObjective("hot_soup", Material.MUSHROOM_STEW, 15),
                        new CraftItemObjective("warm_bread", Material.BREAD, 32),
                        new CollectItemObjective("share_warmth", Material.MUSHROOM_STEW, 5),
                        
                        // 서리 거인과의 대결
                        new VisitLocationObjective("frost_arena", "glacial_battlefield"),
                        new KillMobObjective("ice_elementals", EntityType.SNOW_GOLEM, 20),
                        new KillMobObjective("frost_spiders", EntityType.SPIDER, 25),
                        new KillMobObjective("winter_boss", EntityType.IRON_GOLEM, 1),
                        
                        // 축제 마무리
                        new DeliverItemObjective("winter_gifts", "villager", Material.SNOW_BLOCK, 10),
                        new PlaceBlockObjective("celebration_bonfire", Material.CAMPFIRE, 3),
                        new InteractNPCObjective("winter_blessing", "frost_guardian", 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 1300)
                        .addCurrency(CurrencyType.DIAMOND, 22)
                        .addItem(new ItemStack(Material.DIAMOND_SWORD)) // 서리의 검
                        .addItem(new ItemStack(Material.BLUE_ICE, 32))
                        .addItem(new ItemStack(Material.PACKED_ICE, 64))
                        .addItem(new ItemStack(Material.SNOW_BLOCK, 64))
                        .addExperience(1900)
                        .build())
                .sequential(true)
                .repeatable(true)  // 매년 반복 가능
                .category(QuestCategory.EVENT)
                .minLevel(12)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_WINTER_FROST_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "winter_guardian" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_WINTER_GUARDIAN, who);
            case "frozen_plaza" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_FROZEN_PLAZA, who);
            case "harvest_ice" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_HARVEST_ICE, who);
            case "packed_ice" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_PACKED_ICE, who);
            case "blue_ice" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_BLUE_ICE, who);
            case "snow_blocks" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_SNOW_BLOCKS, who);
            case "snowman_small" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_SNOWMAN_SMALL, who);
            case "snowman_large" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_SNOWMAN_LARGE, who);
            case "snowman_decoration" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_SNOWMAN_DECORATION, who);
            case "snowman_arms" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_SNOWMAN_ARMS, who);
            case "ice_sculptures" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_ICE_SCULPTURES, who);
            case "ice_art_gallery" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_ICE_ART_GALLERY, who);
            case "ice_castle" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_ICE_CASTLE, who);
            case "crystal_lights" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_CRYSTAL_LIGHTS, who);
            case "polar_bears" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_POLAR_BEARS, who);
            case "feed_wolves" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_FEED_WOLVES, who);
            case "tame_foxes" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_TAME_FOXES, who);
            case "arctic_fish" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_ARCTIC_FISH, who);
            case "frozen_lake" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_FROZEN_LAKE, who);
            case "break_ice_holes" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_BREAK_ICE_HOLES, who);
            case "ice_fishing" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_ICE_FISHING, who);
            case "winter_treasures" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_WINTER_TREASURES, who);
            case "frost_crystals" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_FROST_CRYSTALS, who);
            case "brew_frost_potion" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_BREW_FROST_POTION, who);
            case "ice_weapons" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_ICE_WEAPONS, who);
            case "frost_enchantment" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_FROST_ENCHANTMENT, who);
            case "winter_vegetables" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_WINTER_VEGETABLES, who);
            case "hot_soup" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_HOT_SOUP, who);
            case "warm_bread" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_WARM_BREAD, who);
            case "share_warmth" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_SHARE_WARMTH, who);
            case "frost_arena" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_FROST_ARENA, who);
            case "ice_elementals" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_ICE_ELEMENTALS, who);
            case "frost_spiders" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_FROST_SPIDERS, who);
            case "winter_boss" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_WINTER_BOSS, who);
            case "winter_gifts" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_WINTER_GIFTS, who);
            case "celebration_bonfire" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_CELEBRATION_BONFIRE, who);
            case "winter_blessing" -> LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_OBJECTIVES_WINTER_BLESSING, who);
            default -> new ArrayList<>();
        };
    }

    @Override
    public int getDialogCount() {
        return 8;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SEASONAL_WINTER_FROST_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_WINTER_FROST_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_WINTER_FROST_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_WINTER_FROST_DECLINE, who);
    }
}