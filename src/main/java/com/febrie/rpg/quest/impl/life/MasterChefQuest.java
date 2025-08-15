package com.febrie.rpg.quest.impl.life;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;

import java.util.Arrays;
import java.util.List;

/**
 * 요리 달인 - 생활 퀘스트
 * 최고의 요리사가 되기 위한 수련 퀘스트
 *
 * @author Febrie
 */
public class MasterChefQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class MasterChefBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new MasterChefQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public MasterChefQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private MasterChefQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new MasterChefBuilder()
                .id(QuestID.LIFE_MASTER_CHEF)
                .objectives(Arrays.asList(
                        // 시작
                        new InteractNPCObjective("master_chef", "master_chef"), // 요리 마스터
                        
                        // 기초 재료 수집
                        new HarvestObjective("harvest_wheat", Material.WHEAT, 20),
                        new CollectItemObjective("gather_wheat", Material.WHEAT, 20),
                        new CollectItemObjective("gather_eggs", Material.EGG, 12),
                        new CollectItemObjective("gather_sugar", Material.SUGAR, 10),
                        new CollectItemObjective("gather_milk", Material.MILK_BUCKET, 3),
                        
                        // 고기와 해산물
                        new KillMobObjective("hunt_cows", EntityType.COW, 10),
                        new CollectItemObjective("gather_beef", Material.BEEF, 15),
                        new KillMobObjective("hunt_pigs", EntityType.PIG, 10),
                        new CollectItemObjective("gather_pork", Material.PORKCHOP, 15),
                        new FishingObjective("catch_fish", 20),
                        new CollectItemObjective("gather_fish", Material.COD, 10),
                        new CollectItemObjective("gather_salmon", Material.SALMON, 10),
                        
                        // 채소와 과일
                        new HarvestObjective("harvest_vegetables", Material.CARROTS, 30),
                        new CollectItemObjective("gather_carrots", Material.CARROT, 20),
                        new CollectItemObjective("gather_potatoes", Material.POTATO, 20),
                        new CollectItemObjective("gather_beetroot", Material.BEETROOT, 15),
                        new CollectItemObjective("gather_apples", Material.APPLE, 10),
                        new CollectItemObjective("gather_melons", Material.MELON_SLICE, 16),
                        
                        // 주방 설치
                        new PlaceBlockObjective("setup_furnace", Material.FURNACE, 3),
                        new PlaceBlockObjective("setup_smoker", Material.SMOKER, 2),
                        new PlaceBlockObjective("setup_campfire", Material.CAMPFIRE, 1),
                        new PlaceBlockObjective("setup_cauldron", Material.CAULDRON, 2),
                        
                        // 기초 요리
                        new CraftItemObjective("bake_bread", Material.BREAD, 20),
                        new CraftItemObjective("cook_beef", Material.COOKED_BEEF, 15),
                        new CraftItemObjective("cook_pork", Material.COOKED_PORKCHOP, 15),
                        new CraftItemObjective("cook_fish", Material.COOKED_COD, 10),
                        new CraftItemObjective("bake_potato", Material.BAKED_POTATO, 20),
                        
                        // 고급 요리
                        new CraftItemObjective("make_cookies", Material.COOKIE, 32),
                        new CraftItemObjective("make_pie", Material.PUMPKIN_PIE, 5),
                        new CraftItemObjective("make_cake", Material.CAKE, 3),
                        new CraftItemObjective("make_stew", Material.RABBIT_STEW, 5),
                        new CraftItemObjective("make_soup", Material.MUSHROOM_STEW, 5),
                        
                        // 특별 요리 - 황금 사과
                        new CollectItemObjective("special_ingredient", Material.GOLD_INGOT, 8),
                        new CraftItemObjective("golden_apple", Material.GOLDEN_APPLE, 2),
                        
                        // 완성
                        new DeliverItemObjective("deliver_feast", "요리 마스터", Material.CAKE, 1),
                        new DeliverItemObjective("deliver_golden", "요리 마스터", Material.GOLDEN_APPLE, 1),
                        new InteractNPCObjective("graduation", "master_chef")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 3500)
                        .addCurrency(CurrencyType.DIAMOND, 30)
                        .addItem(new ItemStack(Material.GOLDEN_CARROT, 16))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE))
                        .addItem(new ItemStack(Material.WRITTEN_BOOK)) // 요리 레시피북
                        .addItem(new ItemStack(Material.CAMPFIRE))
                        .addExperience(2000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.LIFE)
                .minLevel(15)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.life.master_chef.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getList(who, "quest.life.master_chef.description");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.life.master_chef.objectives." + id);
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("master_chef_dialog");

        dialog.addLine("quest.life.master_chef.npc.master_chef",
                "quest.life.master_chef.dialog.master_chef.line1");

        dialog.addLine("quest.life.master_chef.npc.master_chef",
                "quest.life.master_chef.dialog.master_chef.line2");

        dialog.addLine("quest.life.master_chef.npc.player",
                "quest.life.master_chef.dialog.player.line1");

        dialog.addLine("quest.life.master_chef.npc.master_chef",
                "quest.life.master_chef.dialog.master_chef.line3");

        // 중간 대화
        dialog.addLine("quest.life.master_chef.npc.master_chef",
                "quest.life.master_chef.dialog.master_chef.line4");

        dialog.addLine("quest.life.master_chef.npc.master_chef",
                "quest.life.master_chef.dialog.master_chef.line5");

        // 고급 요리
        dialog.addLine("quest.life.master_chef.npc.master_chef",
                "quest.life.master_chef.dialog.master_chef.line6");

        dialog.addLine("quest.life.master_chef.npc.master_chef",
                "quest.life.master_chef.dialog.master_chef.line7");

        // 특별 요리
        dialog.addLine("quest.life.master_chef.npc.master_chef",
                "quest.life.master_chef.dialog.master_chef.line8");

        dialog.addLine("quest.life.master_chef.npc.master_chef",
                "quest.life.master_chef.dialog.master_chef.line9");

        // 완료
        dialog.addLine("quest.life.master_chef.npc.master_chef",
                "quest.life.master_chef.dialog.master_chef.line10");

        dialog.addLine("quest.life.master_chef.npc.master_chef",
                "quest.life.master_chef.dialog.master_chef.line11");

        return dialog;
    }
}