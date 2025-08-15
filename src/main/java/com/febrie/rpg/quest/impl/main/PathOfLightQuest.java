package com.febrie.rpg.quest.impl.main;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CraftItemObjective;
import com.febrie.rpg.quest.objective.impl.DeliverItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 빛의 길 - 선택 퀘스트 (선)
 * 어둠의 길과 양자택일
 *
 * @author Febrie
 */
public class PathOfLightQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class PathOfLightBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new PathOfLightQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public PathOfLightQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private PathOfLightQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new PathOfLightBuilder()
                .id(QuestID.MAIN_PATH_OF_LIGHT)
                .objectives(Arrays.asList(
                        // 1. 언데드 몬스터 정화
                        new KillMobObjective("purify_undead_zombie", EntityType.ZOMBIE, 50),
                        new KillMobObjective("purify_undead_skeleton", EntityType.SKELETON, 30),
                        new KillMobObjective("purify_undead_phantom", EntityType.PHANTOM, 10),

                        // 2. 성스러운 아이템 제작
                        new CraftItemObjective("craft_golden_apple", Material.GOLDEN_APPLE, 5),

                        // 3. 마을 사람들 도와주기 (빵 전달)
                        new DeliverItemObjective("help_villagers", "굶주린 주민", Material.BREAD, 30)
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
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.main.path_of_light.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getList(who, "quest.main.path_of_light.description");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "purify_undead_zombie" -> com.febrie.rpg.util.LangManager.getMessage(who, "quest.main.path_of_light.objectives.purify_undead_zombie");
            case "purify_undead_skeleton" -> com.febrie.rpg.util.LangManager.getMessage(who, "quest.main.path_of_light.objectives.purify_undead_skeleton");
            case "purify_undead_phantom" -> com.febrie.rpg.util.LangManager.getMessage(who, "quest.main.path_of_light.objectives.purify_undead_phantom");
            case "craft_golden_apple" -> com.febrie.rpg.util.LangManager.getMessage(who, "quest.main.path_of_light.objectives.craft_golden_apple");
            case "help_villagers" -> com.febrie.rpg.util.LangManager.getMessage(who, "quest.main.path_of_light.objectives.help_villagers");
            default -> Component.text(objective.getStatusInfo(null));
        };
    }

    @Override
    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("path_of_light_dialog");

        dialog.addLine("quest.main.path_of_light.dialog.guardian1",
                "quest.main.path_of_light.dialog.guardian1");

        dialog.addLine("quest.main.path_of_light.dialog.guardian2",
                "quest.main.path_of_light.dialog.guardian2");

        dialog.addLine("quest.main.path_of_light.dialog.guardian3",
                "quest.main.path_of_light.dialog.guardian3");

        return dialog;
    }
}