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
 * 발렌타인 사랑 축제 - 계절 이벤트 퀘스트
 * 사랑과 우정을 기념하는 로맨틱한 축제
 *
 * @author Febrie
 */
public class ValentineLoveQuest extends Quest {
    
    public ValentineLoveQuest() {
        super(createBuilder());
    }

    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SEASON_VALENTINE_LOVE)
                .objectives(List.of(
                        // 축제 시작
                        new InteractNPCObjective("cupid_messenger", "love_cupid", 1),
                        new VisitLocationObjective("love_plaza", "valentine_square"),
                        
                        // 사랑의 꽃 수집
                        new CollectItemObjective("red_roses", Material.ROSE_BUSH, 20),
                        new CollectItemObjective("pink_tulips", Material.PINK_TULIP, 30),
                        new CollectItemObjective("love_petals", Material.RED_DYE, 50),
                        new CraftItemObjective("flower_bouquet", Material.FLOWER_POT, 10),
                        
                        // 사랑의 초콜릿 만들기
                        new CollectItemObjective("cocoa_beans", Material.COCOA_BEANS, 64),
                        new CollectItemObjective("sugar_cane", Material.SUGAR, 32),
                        new CollectItemObjective("milk_buckets", Material.MILK_BUCKET, 10),
                        new CraftItemObjective("chocolate_bars", Material.COOKIE, 64),
                        new CraftItemObjective("heart_cake", Material.CAKE, 5),
                        
                        // 하트 장식품 제작
                        new CollectItemObjective("red_wool", Material.RED_WOOL, 30),
                        new CollectItemObjective("pink_wool", Material.PINK_WOOL, 30),
                        new CraftItemObjective("love_banners", Material.RED_BANNER, 8),
                        new PlaceBlockObjective("heart_decorations", Material.RED_WOOL, 25),
                        
                        // 사랑의 편지 배달
                        new CraftItemObjective("love_letters", Material.PAPER, 20),
                        new DeliverItemObjective("letter_delivery_1", "villager", Material.PAPER, 5),
                        new DeliverItemObjective("letter_delivery_2", "villager", Material.PAPER, 5),
                        new InteractNPCObjective("postman_helper", "village_postman", 1),
                        
                        // 커플 댄스 파티
                        new VisitLocationObjective("dance_hall", "love_ballroom"),
                        new InteractNPCObjective("dance_partner", "elegant_dancer", 1),
                        new InteractNPCObjective("romantic_dance", "dance", 10),
                        new CollectItemObjective("party_fireworks", Material.FIREWORK_ROCKET, 15),
                        
                        // 사랑의 물약 제조
                        new CollectItemObjective("love_herbs", Material.SWEET_BERRIES, 40),
                        new CollectItemObjective("romantic_mushrooms", Material.RED_MUSHROOM, 20),
                        new CraftItemObjective("brew_love_potion", Material.POTION, 8),
                        new DeliverItemObjective("potion_delivery", "villager", Material.POTION, 3),
                        
                        // 결혼식 도움
                        new InteractNPCObjective("bride_and_groom", "wedding_couple", 1),
                        new PlaceBlockObjective("wedding_aisle", Material.WHITE_CARPET, 20),
                        new PlaceBlockObjective("flower_arch", Material.ROSE_BUSH, 6),
                        new DeliverItemObjective("wedding_cake", "villager", Material.CAKE, 2),
                        
                        // 사랑의 시
                        new CraftItemObjective("poem_scrolls", Material.PAPER, 10),
                        new InteractNPCObjective("poetry_reading", "village_poet", 1),
                        new DeliverItemObjective("poem_gift", "villager", Material.PAPER, 3),
                        
                        // 축제 마무리
                        new CollectItemObjective("friendship_tokens", Material.GOLD_NUGGET, 20),
                        new InteractNPCObjective("love_blessing", "love_cupid", 1),
                        new InteractNPCObjective("festival_end", "love_cupid", 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 1000)
                        .addCurrency(CurrencyType.DIAMOND, 15)
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 5)) // 사랑의 황금 사과
                        .addItem(new ItemStack(Material.ROSE_BUSH, 20))
                        .addItem(new ItemStack(Material.CAKE, 8))
                        .addItem(new ItemStack(Material.FIREWORK_ROCKET, 32))
                        .addExperience(1500)
                        .build())
                .sequential(true)
                .repeatable(true)  // 매년 반복 가능
                .category(QuestCategory.EVENT)
                .minLevel(5)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "cupid_messenger" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_CUPID_MESSENGER, who);
            case "love_plaza" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_LOVE_PLAZA, who);
            case "red_roses" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_RED_ROSES, who);
            case "pink_tulips" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_PINK_TULIPS, who);
            case "love_petals" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_LOVE_PETALS, who);
            case "flower_bouquet" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_FLOWER_BOUQUET, who);
            case "cocoa_beans" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_COCOA_BEANS, who);
            case "sugar_cane" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_SUGAR_CANE, who);
            case "milk_buckets" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_MILK_BUCKETS, who);
            case "chocolate_bars" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_CHOCOLATE_BARS, who);
            case "heart_cake" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_HEART_CAKE, who);
            case "red_wool" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_RED_WOOL, who);
            case "pink_wool" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_PINK_WOOL, who);
            case "love_banners" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_LOVE_BANNERS, who);
            case "heart_decorations" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_HEART_DECORATIONS, who);
            case "love_letters" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_LOVE_LETTERS, who);
            case "letter_delivery_1" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_LETTER_DELIVERY_1, who);
            case "letter_delivery_2" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_LETTER_DELIVERY_2, who);
            case "postman_helper" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_POSTMAN_HELPER, who);
            case "dance_hall" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_DANCE_HALL, who);
            case "dance_partner" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_DANCE_PARTNER, who);
            case "romantic_dance" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_ROMANTIC_DANCE, who);
            case "party_fireworks" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_PARTY_FIREWORKS, who);
            case "love_herbs" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_LOVE_HERBS, who);
            case "romantic_mushrooms" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_ROMANTIC_MUSHROOMS, who);
            case "brew_love_potion" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_BREW_LOVE_POTION, who);
            case "potion_delivery" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_POTION_DELIVERY, who);
            case "bride_and_groom" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_BRIDE_AND_GROOM, who);
            case "wedding_aisle" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_WEDDING_AISLE, who);
            case "flower_arch" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_FLOWER_ARCH, who);
            case "wedding_cake" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_WEDDING_CAKE, who);
            case "poem_scrolls" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_POEM_SCROLLS, who);
            case "poetry_reading" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_POETRY_READING, who);
            case "poem_gift" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_POEM_GIFT, who);
            case "friendship_tokens" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_FRIENDSHIP_TOKENS, who);
            case "love_blessing" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_LOVE_BLESSING, who);
            case "festival_end" -> LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_OBJECTIVES_FESTIVAL_END, who);
            default -> new ArrayList<>();
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SEASONAL_VALENTINE_LOVE_DECLINE, who);
    }
}