package com.febrie.rpg.quest.impl.main;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CraftItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.objective.impl.KillPlayerObjective;
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
 * 어둠의 길 - 선택 퀘스트 (악)
 * 빛의 길과 양자택일
 *
 * @author Febrie
 */
public class PathOfDarknessQuest extends Quest {

    /**
     * 기본 생성자
     */
    public PathOfDarknessQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_PATH_OF_DARKNESS)
                .objectives(Arrays.asList(
                        // 1. 마을 주민 처치 (어둠의 길...)
                        new KillMobObjective("eliminate_villagers", EntityType.VILLAGER, 10),

                        // 2. 플레이어 처치 (PvP)
                        new KillPlayerObjective("dominate_players", 5),

                        // 3. 위더 스켈레톤 처치하여 재료 수집
                        new KillMobObjective("hunt_wither_skeletons", EntityType.WITHER_SKELETON, 20),

                        // 4. 어둠의 아이템 제작
                        new CraftItemObjective("craft_tnt", Material.TNT, 10)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 1500)  // 더 많은 골드
                        .addCurrency(CurrencyType.GHAST_TEAR, 50)  // 특별 재화
                        .addItem(new ItemStack(Material.WITHER_SKELETON_SKULL, 3))  // 위더 소환 재료
                        .addItem(new ItemStack(Material.NETHERITE_SWORD))  // 네더라이트 검
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 5))  // 인챈트된 황금사과
                        .addExperience(2500)
                        .setDescriptionKey("quest.main.path_of_darkness.reward.description")
                        .build())
                .sequential(false)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .minLevel(20)
                .maxLevel(0)
                .addPrerequisite(QuestID.MAIN_HEROES_JOURNEY)  // 영웅의 여정 완료 필요
                .addExclusive(QuestID.MAIN_PATH_OF_LIGHT);  // 빛의 길과 양자택일
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.get("quest.main.path_of_darkness.name", who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.main.path_of_darkness.info", who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String key = "quest.main.path_of_darkness.objectives." + objective.getId();
        return LangManager.get(key, who);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.main.path_of_darkness.dialogs.0", who);
            case 1 -> LangManager.get("quest.main.path_of_darkness.dialogs.1", who);
            case 2 -> LangManager.get("quest.main.path_of_darkness.dialogs.2", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.main.path_of_darkness.npc_name", who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.main.path_of_darkness.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.main.path_of_darkness.decline", who);
    }
}