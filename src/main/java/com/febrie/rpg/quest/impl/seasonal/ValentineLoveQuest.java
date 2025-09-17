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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

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
                        new InteractNPCObjective("cupid_messenger", "love_cupid"),
                        new VisitLocationObjective("love_plaza", "valentine_square"),
                        
                        // 사랑의 꽃 수집
                        new CollectItemObjective("rose_bush_collect", Material.ROSE_BUSH, 20),
                        new CollectItemObjective("pink_tulip_collect", Material.PINK_TULIP, 30),
                        new CollectItemObjective("red_dye_collect", Material.RED_DYE, 50),
                        new CraftItemObjective("flower_pot_craft", Material.FLOWER_POT, 10),
                        
                        // 사랑의 초콜릿 만들기
                        new CollectItemObjective("cocoa_beans_collect", Material.COCOA_BEANS, 64),
                        new CollectItemObjective("sugar_collect", Material.SUGAR, 32),
                        new CollectItemObjective("milk_bucket_collect", Material.MILK_BUCKET, 10),
                        new CraftItemObjective("cookie_craft", Material.COOKIE, 64),
                        new CraftItemObjective("cake_craft", Material.CAKE, 5),
                        
                        // 하트 장식품 제작
                        new CollectItemObjective("red_wool_collect", Material.RED_WOOL, 30),
                        new CollectItemObjective("pink_wool_collect", Material.PINK_WOOL, 30),
                        new CraftItemObjective("red_banner_craft", Material.RED_BANNER, 8),
                        new PlaceBlockObjective("heart_decorations", Material.RED_WOOL, 25),
                        
                        // 사랑의 편지 배달
                        new CraftItemObjective("paper_craft", Material.PAPER, 20),
                        new DeliverItemObjective("paper_deliver", Material.PAPER, 5, "villager"),
                        new DeliverItemObjective("paper_deliver", Material.PAPER, 5, "villager"),
                        new InteractNPCObjective("postman_helper", "village_postman"),
                        
                        // 커플 댄스 파티
                        new VisitLocationObjective("dance_hall", "love_ballroom"),
                        new InteractNPCObjective("dance_partner", "elegant_dancer"),
                        new InteractNPCObjective("romantic_dance", "dance"),
                        new CollectItemObjective("firework_rocket_collect", Material.FIREWORK_ROCKET, 15),
                        
                        // 사랑의 물약 제조
                        new CollectItemObjective("sweet_berries_collect", Material.SWEET_BERRIES, 40),
                        new CollectItemObjective("red_mushroom_collect", Material.RED_MUSHROOM, 20),
                        new CraftItemObjective("potion_craft", Material.POTION, 8),
                        new DeliverItemObjective("potion_deliver", Material.POTION, 3, "villager"),
                        
                        // 결혼식 도움
                        new InteractNPCObjective("bride_and_groom", "wedding_couple"),
                        new PlaceBlockObjective("wedding_aisle", Material.WHITE_CARPET, 20),
                        new PlaceBlockObjective("flower_arch", Material.ROSE_BUSH, 6),
                        new DeliverItemObjective("cake_deliver", Material.CAKE, 2, "villager"),
                        
                        // 사랑의 시
                        new CraftItemObjective("paper_craft", Material.PAPER, 10),
                        new InteractNPCObjective("poetry_reading", "village_poet"),
                        new DeliverItemObjective("paper_deliver", Material.PAPER, 3, "villager"),
                        
                        // 축제 마무리
                        new CollectItemObjective("gold_nugget_collect", Material.GOLD_NUGGET, 20),
                        new InteractNPCObjective("love_blessing", "love_cupid"),
                        new InteractNPCObjective("festival_end", "love_cupid")
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
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_VALENTINE_LOVE_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SEASONAL_VALENTINE_LOVE_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "cupid_messenger" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_CUPID_MESSENGER, who);
            case "love_plaza" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_LOVE_PLAZA, who);
            case "rose_bush_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_ROSE_BUSH_COLLECT, who);
            case "pink_tulip_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_PINK_TULIP_COLLECT, who);
            case "red_dye_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_RED_DYE_COLLECT, who);
            case "flower_pot_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_FLOWER_POT_CRAFT, who);
            case "cocoa_beans_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_COCOA_BEANS_COLLECT, who);
            case "sugar_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_SUGAR_COLLECT, who);
            case "milk_bucket_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_MILK_BUCKET_COLLECT, who);
            case "cookie_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_COOKIE_CRAFT, who);
            case "cake_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_CAKE_CRAFT, who);
            case "red_wool_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_RED_WOOL_COLLECT, who);
            case "pink_wool_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_PINK_WOOL_COLLECT, who);
            case "red_banner_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_RED_BANNER_CRAFT, who);
            case "heart_decorations" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_HEART_DECORATIONS, who);
            case "paper_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_PAPER_CRAFT, who);
            case "paper_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_PAPER_DELIVER, who);
            case "postman_helper" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_POSTMAN_HELPER, who);
            case "dance_hall" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_DANCE_HALL, who);
            case "dance_partner" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_DANCE_PARTNER, who);
            case "romantic_dance" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_ROMANTIC_DANCE, who);
            case "firework_rocket_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_FIREWORK_ROCKET_COLLECT, who);
            case "sweet_berries_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_SWEET_BERRIES_COLLECT, who);
            case "red_mushroom_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_RED_MUSHROOM_COLLECT, who);
            case "potion_craft" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_POTION_CRAFT, who);
            case "potion_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_POTION_DELIVER, who);
            case "bride_and_groom" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_BRIDE_AND_GROOM, who);
            case "wedding_aisle" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_WEDDING_AISLE, who);
            case "flower_arch" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_FLOWER_ARCH, who);
            case "cake_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_CAKE_DELIVER, who);
            case "poetry_reading" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_POETRY_READING, who);
            case "gold_nugget_collect" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_GOLD_NUGGET_COLLECT, who);
            case "love_blessing" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_LOVE_BLESSING, who);
            case "festival_end" -> LangManager.text(QuestCommonLangKey.QUEST_SEASON_VALENTINE_LOVE_OBJECTIVES_FESTIVAL_END, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_SEASONAL_VALENTINE_LOVE_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_VALENTINE_LOVE_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_VALENTINE_LOVE_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_SEASONAL_VALENTINE_LOVE_DECLINE, who);
    }
}