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
 * 감사제 - 계절 이벤트 퀘스트
 * 수확의 계절을 맞아 감사를 표현하는 축제
 *
 * @author Febrie
 */
public class ThanksgivingQuest extends Quest {
    
    public ThanksgivingQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SEASON_THANKSGIVING)
                .objectives(List.of(
                        // 축제 시작
                        new InteractNPCObjective("harvest_elder", "village_elder", 1),
                        new VisitLocationObjective("harvest_festival", "town_square"),
                        
                        // 수확물 수집
                        new HarvestObjective("harvest_wheat", Material.WHEAT, 100),
                        new HarvestObjective("harvest_carrots", Material.CARROT, 80),
                        new HarvestObjective("harvest_potatoes", Material.POTATO, 80),
                        new HarvestObjective("harvest_pumpkins", Material.PUMPKIN, 50),
                        
                        // 동물 사육
                        new BreedAnimalsObjective("breed_cows", EntityType.COW, 10),
                        new BreedAnimalsObjective("breed_pigs", EntityType.PIG, 10),
                        new BreedAnimalsObjective("breed_chickens", EntityType.CHICKEN, 15),
                        new CollectItemObjective("collect_milk", Material.MILK_BUCKET, 20),
                        
                        // 감사제 요리 준비
                        new CraftItemObjective("bake_bread", Material.BREAD, 64),
                        new CraftItemObjective("make_pumpkin_pie", Material.PUMPKIN_PIE, 20),
                        new CraftItemObjective("bake_cookies", Material.COOKIE, 64),
                        new CraftItemObjective("make_cake", Material.CAKE, 8),
                        
                        // 마을 장식
                        new PlaceBlockObjective("decorate_hay", Material.HAY_BLOCK, 25),
                        new PlaceBlockObjective("place_pumpkins", Material.CARVED_PUMPKIN, 15),
                        new PlaceBlockObjective("autumn_leaves", Material.ORANGE_WOOL, 30),
                        new PlaceBlockObjective("harvest_altar", Material.HAY_BLOCK, 20),
                        
                        // 사냥 대회
                        new KillMobObjective("turkey_hunt", EntityType.CHICKEN, 30), // 칠면조 대신
                        new KillMobObjective("wild_boar", EntityType.PIG, 20),
                        new KillMobObjective("deer_hunt", EntityType.COW, 15),
                        new CollectItemObjective("hunting_trophies", Material.LEATHER, 40),
                        
                        // 감사 의식
                        new DeliverItemObjective("food_donation", "villager", Material.BREAD, 32),
                        new DeliverItemObjective("pie_sharing", "villager", Material.PUMPKIN_PIE, 10),
                        new PayCurrencyObjective("charity_donation", CurrencyType.GOLD, 500),
                        new InteractNPCObjective("gratitude_ceremony", "village_elder", 1),
                        
                        // 마을 잔치
                        new InteractNPCObjective("join_feast", "feast_organizer", 1),
                        new EatFoodObjective("thanksgiving_meal", Material.COOKED_BEEF, 10),
                        new CollectItemObjective("share_drinks", Material.MILK_BUCKET, 5),
                        
                        // 축제 마무리
                        new CollectItemObjective("leftover_food", Material.COOKED_CHICKEN, 20),
                        new DeliverItemObjective("help_cleanup", "villager", Material.BROWN_WOOL, 15),
                        new InteractNPCObjective("festival_thanks", "village_elder", 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 1200)
                        .addCurrency(CurrencyType.DIAMOND, 20)
                        .addItem(new ItemStack(Material.GOLDEN_CARROT, 32)) // 감사의 황금 당근
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 3)) // 풍요의 사과
                        .addItem(new ItemStack(Material.HAY_BLOCK, 64))
                        .addItem(new ItemStack(Material.PUMPKIN_PIE, 20))
                        .addExperience(1800)
                        .build())
                .sequential(true)
                .repeatable(true)  // 매년 반복 가능
                .category(QuestCategory.EVENT)
                .minLevel(8)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_THANKSGIVING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "harvest_elder" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_HARVEST_ELDER, who);
            case "harvest_festival" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_HARVEST_FESTIVAL, who);
            case "harvest_wheat" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_HARVEST_WHEAT, who);
            case "harvest_carrots" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_HARVEST_CARROTS, who);
            case "harvest_potatoes" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_HARVEST_POTATOES, who);
            case "harvest_pumpkins" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_HARVEST_PUMPKINS, who);
            case "breed_cows" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_BREED_COWS, who);
            case "breed_pigs" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_BREED_PIGS, who);
            case "breed_chickens" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_BREED_CHICKENS, who);
            case "collect_milk" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_COLLECT_MILK, who);
            case "bake_bread" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_BAKE_BREAD, who);
            case "make_pumpkin_pie" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_MAKE_PUMPKIN_PIE, who);
            case "bake_cookies" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_BAKE_COOKIES, who);
            case "make_cake" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_MAKE_CAKE, who);
            case "decorate_hay" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_DECORATE_HAY, who);
            case "place_pumpkins" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_PLACE_PUMPKINS, who);
            case "autumn_leaves" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_AUTUMN_LEAVES, who);
            case "harvest_altar" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_HARVEST_ALTAR, who);
            case "turkey_hunt" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_TURKEY_HUNT, who);
            case "wild_boar" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_WILD_BOAR, who);
            case "deer_hunt" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_DEER_HUNT, who);
            case "hunting_trophies" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_HUNTING_TROPHIES, who);
            case "food_donation" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_FOOD_DONATION, who);
            case "pie_sharing" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_PIE_SHARING, who);
            case "charity_donation" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_CHARITY_DONATION, who);
            case "gratitude_ceremony" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_GRATITUDE_CEREMONY, who);
            case "join_feast" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_JOIN_FEAST, who);
            case "thanksgiving_meal" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_THANKSGIVING_MEAL, who);
            case "share_drinks" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_SHARE_DRINKS, who);
            case "leftover_food" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_LEFTOVER_FOOD, who);
            case "help_cleanup" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_HELP_CLEANUP, who);
            case "festival_thanks" -> LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_OBJECTIVES_FESTIVAL_THANKS, who);
            default -> new ArrayList<>();
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SEASONAL_THANKSGIVING_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_THANKSGIVING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_THANKSGIVING_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_THANKSGIVING_DECLINE, who);
    }
}