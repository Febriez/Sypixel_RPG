package com.febrie.rpg.quest.impl.life;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.CraftItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 마스터 셰프 - 생활 퀘스트
 * 요리의 모든 기술을 마스터하는 퀘스트
 *
 * @author Febrie
 */
public class MasterChefQuest extends Quest {

    /**
     * 기본 생성자
     */
    public MasterChefQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        List<QuestObjective> objectives = new ArrayList<>();
        
        // 요리 관련 목표들
        objectives.add(new CraftItemObjective("craft_bread", Material.BREAD, 32)); // 빵 32개 제작
        objectives.add(new CraftItemObjective("craft_cake", Material.CAKE, 5)); // 케이크 5개 제작
        objectives.add(new CraftItemObjective("craft_pumpkin_pie", Material.PUMPKIN_PIE, 16)); // 호박 파이 16개 제작
        objectives.add(new CraftItemObjective("craft_cookies", Material.COOKIE, 64)); // 쿠키 64개 제작
        objectives.add(new CollectItemObjective("collect_cooked_beef", Material.COOKED_BEEF, 32)); // 구운 쇠고기 32개 수집
        objectives.add(new CollectItemObjective("collect_cooked_pork", Material.COOKED_PORKCHOP, 32)); // 구운 돼지고기 32개 수집
        objectives.add(new CollectItemObjective("collect_cooked_chicken", Material.COOKED_CHICKEN, 32)); // 구운 닭고기 32개 수집
        objectives.add(new CollectItemObjective("collect_cooked_fish", Material.COOKED_SALMON, 24)); // 구운 연어 24개 수집
        objectives.add(new KillMobObjective("hunt_cows", EntityType.COW, 20)); // 소 20마리 사냥 (고기 확보)
        objectives.add(new CollectItemObjective("collect_milk", Material.MILK_BUCKET, 10)); // 우유 10개 수집

        return new QuestBuilder()
                .id(QuestID.LIFE_MASTER_CHEF)
                .objectives(objectives)
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 1200)
                        .addCurrency(CurrencyType.EMERALD, 60)
                        .addItem(new ItemStack(Material.FURNACE)) // 화로
                        .addItem(new ItemStack(Material.SMOKER)) // 훈연기
                        .addItem(new ItemStack(Material.CAMPFIRE)) // 모닥불
                        .addItem(new ItemStack(Material.CAKE, 3)) // 케이크 3개
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 5)) // 황금 사과 5개
                        .addExperience(600)
                        .build())
                .sequential(false) // 순서 상관없이 진행 가능
                .category(QuestCategory.LIFE)
                .minLevel(20)
                .repeatable(false)
                .addPrerequisite(QuestID.LIFE_FARMING_EXPERT); // 농업 전문가 선행
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_LIFE_MASTER_CHEF_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_LIFE_MASTER_CHEF_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "craft_bread" -> LangManager.list(LangKey.QUEST_LIFE_MASTER_CHEF_OBJECTIVES_CRAFT_BREAD, who);
            case "craft_cake" -> LangManager.list(LangKey.QUEST_LIFE_MASTER_CHEF_OBJECTIVES_CRAFT_CAKE, who);
            case "craft_pumpkin_pie" -> LangManager.list(LangKey.QUEST_LIFE_MASTER_CHEF_OBJECTIVES_CRAFT_PUMPKIN_PIE, who);
            case "craft_cookies" -> LangManager.list(LangKey.QUEST_LIFE_MASTER_CHEF_OBJECTIVES_CRAFT_COOKIES, who);
            case "collect_cooked_beef" -> LangManager.list(LangKey.QUEST_LIFE_MASTER_CHEF_OBJECTIVES_COLLECT_COOKED_BEEF, who);
            case "collect_cooked_pork" -> LangManager.list(LangKey.QUEST_LIFE_MASTER_CHEF_OBJECTIVES_COLLECT_COOKED_PORK, who);
            case "collect_cooked_chicken" -> LangManager.list(LangKey.QUEST_LIFE_MASTER_CHEF_OBJECTIVES_COLLECT_COOKED_CHICKEN, who);
            case "collect_cooked_fish" -> LangManager.list(LangKey.QUEST_LIFE_MASTER_CHEF_OBJECTIVES_COLLECT_COOKED_FISH, who);
            case "hunt_cows" -> LangManager.list(LangKey.QUEST_LIFE_MASTER_CHEF_OBJECTIVES_HUNT_COWS, who);
            case "collect_milk" -> LangManager.list(LangKey.QUEST_LIFE_MASTER_CHEF_OBJECTIVES_COLLECT_MILK, who);
            default -> new ArrayList<>();
        };
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_LIFE_MASTER_CHEF_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_LIFE_MASTER_CHEF_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_LIFE_MASTER_CHEF_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_LIFE_MASTER_CHEF_DECLINE, who);
    }
}