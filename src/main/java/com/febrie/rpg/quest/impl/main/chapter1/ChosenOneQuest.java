package com.febrie.rpg.quest.impl.main.chapter1;

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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 선택받은 자 - 메인 퀘스트 Chapter 1
 * 영웅으로서의 자질을 시험받는 퀘스트
 *
 * @author Febrie
 */
public class ChosenOneQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public ChosenOneQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder().id(QuestID.MAIN_CHOSEN_ONE)
                .objectives(Arrays.asList(
                        // 시련의 동굴 입장
                        new VisitLocationObjective("enter_trial_cave", "trial_cave_entrance"),

                        // 첫 번째 시련: 용기
                        new KillMobObjective("trial_courage", EntityType.IRON_GOLEM, 3), new CollectItemObjective("courage_proof", Material.IRON_BLOCK, 1),

                        // 두 번째 시련: 지혜
                        new BreakBlockObjective("solve_puzzle", Material.REDSTONE_LAMP, 5), new CollectItemObjective("wisdom_proof", Material.EMERALD, 1),

                        // 세 번째 시련: 희생
                        new PayCurrencyObjective("sacrifice_gold", CurrencyType.GOLD, 1000), new CollectItemObjective("sacrifice_proof", Material.DIAMOND, 1),

                        // 최종 시련
                        new KillMobObjective("final_guardian", EntityType.WITHER_SKELETON, 1), new CollectItemObjective("chosen_emblem", Material.NETHER_STAR, 1),

                        // 완료
                        new DeliverItemObjective("return_elder", "고대의 장로", Material.NETHER_STAR, 1)))
                .reward(new BasicReward.Builder().addCurrency(CurrencyType.GOLD, 2000)
                        .addCurrency(CurrencyType.DIAMOND, 20)
                        .addItem(new ItemStack(Material.NETHERITE_SWORD))
                        .addItem(new ItemStack(Material.ELYTRA))
                        .addExperience(2000)
                        .build())
                .sequential(true)  // 순차적으로 진행
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .minLevel(15)
                .maxLevel(0)
                .addPrerequisite(QuestID.MAIN_ANCIENT_PROPHECY);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.get("quest.main.chosen_one.name", who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.main.chosen_one.info", who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return LangManager.get("quest.main.chosen_one.objectives." + id, who);
    }

    @Override
    public int getDialogCount() {
        return 6;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.main.chosen_one.dialogs.0", who);
            case 1 -> LangManager.get("quest.main.chosen_one.dialogs.1", who);
            case 2 -> LangManager.get("quest.main.chosen_one.dialogs.2", who);
            case 3 -> LangManager.get("quest.main.chosen_one.dialogs.3", who);
            case 4 -> LangManager.get("quest.main.chosen_one.dialogs.4", who);
            case 5 -> LangManager.get("quest.main.chosen_one.dialogs.5", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.main.chosen_one.npc_name", who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.main.chosen_one.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.main.chosen_one.decline", who);
    }
}