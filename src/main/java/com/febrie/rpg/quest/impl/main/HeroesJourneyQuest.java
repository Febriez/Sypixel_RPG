package com.febrie.rpg.quest.impl.main;

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

import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

/**
 * 영웅의 여정 - 메인 퀘스트 1
 * 본격적인 모험의 시작
 *
 * @author Febrie
 */
public class HeroesJourneyQuest extends Quest {

    /**
     * 기본 생성자
     */
    public HeroesJourneyQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_HEROES_JOURNEY)
                .objectives(Arrays.asList(
                        // 1. 다양한 몬스터 처치
                        new KillMobObjective("kill_zombies", EntityType.ZOMBIE, 10),
                        new KillMobObjective("kill_skeletons", EntityType.SKELETON, 10),
                        new KillMobObjective("kill_spiders", EntityType.SPIDER, 5),

                        // 2. 자원 수집
                        new CollectItemObjective("collect_iron", Material.IRON_INGOT, 20),
                        new CollectItemObjective("collect_gold", Material.GOLD_INGOT, 10),

                        // 3. 장비 제작
                        new CraftItemObjective("craft_iron_sword", Material.IRON_SWORD, 1),
                        new CraftItemObjective("craft_iron_armor", Material.IRON_CHESTPLATE, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 500)
                        .addCurrency(CurrencyType.DIAMOND, 5)
                        .addItem(new ItemStack(Material.DIAMOND))
                        .addItem(new ItemStack(Material.ENCHANTING_TABLE))
                        .addExperience(500)
                        .build())
                .sequential(false)  // 자유롭게 진행 가능
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .minLevel(5)
                .maxLevel(0)  // 최대 레벨 제한 없음
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);  // 튜토리얼 전투 퀘스트 완료 필요
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_HEROES_JOURNEY_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_HEROES_JOURNEY_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String key = "quest.main.heroes_journey.objectives." + objective.getId();
        return LangManager.get(key, who);
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(LangKey.QUEST_MAIN_HEROES_JOURNEY_DIALOGS, who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_HEROES_JOURNEY_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_HEROES_JOURNEY_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_HEROES_JOURNEY_DECLINE, who);
    }
}