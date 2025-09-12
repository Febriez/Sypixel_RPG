package com.febrie.rpg.quest.impl.main;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CraftItemObjective;
import com.febrie.rpg.quest.objective.impl.DeliverItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 빛의 길 - 선택 퀘스트 (선)
 * 어둠의 길과 양자택일
 *
 * @author Febrie
 */
public class PathOfLightQuest extends Quest {

    /**
     * 기본 생성자
     */
    public PathOfLightQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_PATH_OF_LIGHT)
                .objectives(List.of(
                        // 1. 언데드 몬스터 정화
                        new KillMobObjective("purify_undead_zombie", EntityType.ZOMBIE, 50),
                        new KillMobObjective("purify_undead_skeleton", EntityType.SKELETON, 30),
                        new KillMobObjective("purify_undead_phantom", EntityType.PHANTOM, 10),

                        // 2. 성스러운 아이템 제작
                        new CraftItemObjective("golden_apple_craft", Material.GOLDEN_APPLE, 5),

                        // 3. 마을 사람들 도와주기 (빵 전달)
                        new DeliverItemObjective("bread_deliver", Material.BREAD, 30, "굶주린 주민")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 1000)
                        .addCurrency(CurrencyType.DIAMOND, 20)
                        .addItem(new ItemStack(Material.ELYTRA))  // 엘리트라 (천사의 날개)
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING))  // 불사의 토템
                        .addItem(new ItemStack(Material.BEACON))  // 신호기
                        .addExperience(2000)
                        .setDescriptionKey("quest.main.path_of_light.reward.description")
                        .build())
                .sequential(false)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .minLevel(20)
                .maxLevel(0)
                .addPrerequisite(QuestID.MAIN_HEROES_JOURNEY)  // 영웅의 여정 완료 필요
                .addExclusive(QuestID.MAIN_PATH_OF_DARKNESS);  // 어둠의 길과 양자택일
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_PATH_OF_LIGHT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_PATH_OF_LIGHT_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "purify_undead_zombie" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_PATH_OF_LIGHT_OBJECTIVES_PURIFY_UNDEAD_ZOMBIE, who);
            case "purify_undead_skeleton" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_PATH_OF_LIGHT_OBJECTIVES_PURIFY_UNDEAD_SKELETON, who);
            case "purify_undead_phantom" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_PATH_OF_LIGHT_OBJECTIVES_PURIFY_UNDEAD_PHANTOM, who);
            case "golden_apple_craft" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_PATH_OF_LIGHT_OBJECTIVES_GOLDEN_APPLE_CRAFT, who);
            case "bread_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_PATH_OF_LIGHT_OBJECTIVES_BREAD_DELIVER, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_PATH_OF_LIGHT_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_PATH_OF_LIGHT_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_PATH_OF_LIGHT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_PATH_OF_LIGHT_DECLINE, who);
    }
}