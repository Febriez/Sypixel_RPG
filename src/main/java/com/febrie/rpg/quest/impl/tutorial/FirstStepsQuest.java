package com.febrie.rpg.quest.impl.tutorial;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangHelper;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 첫 걸음 - 튜토리얼 퀘스트 1
 * 기본적인 이동과 상호작용을 배우는 퀘스트
 *
 * @author Febrie
 */
public class FirstStepsQuest extends Quest {

    /**
     * 기본 생성자
     */
    public FirstStepsQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.TUTORIAL_FIRST_STEPS)
                .objectives(Arrays.asList(
                        // 1. 허브 구역 방문 (WorldGuard 영역 이름: Hub)
                        new VisitLocationObjective("visit_hub", "Hub"),
                        // 2. 마을 상인 NPC 방문 (NPC Code 기반)
                        new InteractNPCObjective("visit_merchant", "village_merchant")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 100)
                        .addItem(new ItemStack(Material.WOODEN_SWORD))
                        .addItem(new ItemStack(Material.WOODEN_PICKAXE))
                        .addItem(new ItemStack(Material.WOODEN_AXE))
                        .addItem(new ItemStack(Material.BREAD, 10))
                        .addExperience(50)
                        .build())
                .sequential(true)  // 순차적으로 진행
                .category(QuestCategory.TUTORIAL)
                .minLevel(1);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_TUTORIAL_FIRST_STEPS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_TUTORIAL_FIRST_STEPS_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "visit_hub" -> LangHelper.text(LangKey.QUEST_TUTORIAL_FIRST_STEPS_OBJECTIVES_VISIT_HUB, who);
            case "visit_merchant" -> LangHelper.text(LangKey.QUEST_TUTORIAL_FIRST_STEPS_OBJECTIVES_VISIT_MERCHANT, who);
            default -> Component.empty();
        };
    }


    @Override
    public int getDialogCount() {
        return 3; // 3개의 대화 페이지
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        // 올바른 키 형식 사용: dialogs.0, dialogs.1, ...
        return switch (index) {
            case 0 -> LangHelper.text(LangKey.QUEST_TUTORIAL_FIRST_STEPS_DIALOGS_0, who);
            case 1 -> LangHelper.text(LangKey.QUEST_TUTORIAL_FIRST_STEPS_DIALOGS_1, who);
            case 2 -> LangHelper.text(LangKey.QUEST_TUTORIAL_FIRST_STEPS_DIALOGS_2, who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_TUTORIAL_FIRST_STEPS_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_TUTORIAL_FIRST_STEPS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_TUTORIAL_FIRST_STEPS_DECLINE, who);
    }
}