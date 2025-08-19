package com.febrie.rpg.quest.impl.exploration;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 고대 유적 탐험 - 탐험 사이드 퀘스트
 * 숨겨진 고대 유적을 탐험하고 비밀을 밝혀내는 퀘스트
 *
 * @author Febrie
 */
public class AncientRuinsQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class AncientRuinsBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new AncientRuinsQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public AncientRuinsQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private AncientRuinsQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new AncientRuinsBuilder()
                .id(QuestID.SIDE_ANCIENT_RUINS)
                .objectives(Arrays.asList(
                        // 시작
                        new InteractNPCObjective("archaeologist_talk", "archaeologist_henry"), // 고고학자 헨리
                        new CollectItemObjective("gather_tools", Material.IRON_PICKAXE, 1),
                        new CollectItemObjective("gather_torches", Material.TORCH, 64),
                        
                        // 유적 입구 발견
                        new VisitLocationObjective("ruins_entrance", "ancient_ruins_entrance"),
                        new BreakBlockObjective("clear_entrance", Material.STONE, 30),
                        new BreakBlockObjective("excavate_dirt", Material.DIRT, 50),
                        
                        // 첫 번째 방 - 고대의 서고
                        new VisitLocationObjective("library_room", "ruins_library"),
                        new CollectItemObjective("ancient_books", Material.WRITTEN_BOOK, 3),
                        new KillMobObjective("library_guardians", EntityType.VEX, 10),
                        
                        // 두 번째 방 - 보물 창고
                        new VisitLocationObjective("treasure_room", "ruins_treasury"),
                        new BreakBlockObjective("break_pots", Material.DECORATED_POT, 10),
                        new CollectItemObjective("ancient_coins", Material.GOLD_NUGGET, 30),
                        new KillMobObjective("treasure_guardians", EntityType.SKELETON, 15),
                        
                        // 숨겨진 방 발견
                        new PlaceBlockObjective("place_lever", Material.LEVER, 1),
                        new VisitLocationObjective("secret_chamber", "ruins_secret_chamber"),
                        new KillMobObjective("ancient_sentinel", EntityType.IRON_GOLEM, 1),
                        new CollectItemObjective("ancient_artifact", Material.TOTEM_OF_UNDYING, 1),
                        
                        // 탈출
                        new SurviveObjective("escape_ruins", 180), // 3분
                        new DeliverItemObjective("return_artifact", "고고학자 헨리", Material.TOTEM_OF_UNDYING, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 3000)
                        .addCurrency(CurrencyType.DIAMOND, 25)
                        .addItem(new ItemStack(Material.BRUSH))
                        .addItem(new ItemStack(Material.SPYGLASS))
                        .addItem(new ItemStack(Material.MAP))
                        .addExperience(2000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.EXPLORATION)
                .minLevel(20)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.exploration.ancient_ruins.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return List.of() /* TODO: Convert LangManager.getList("quest.exploration.ancient_ruins.description") manually */;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return Component.translatable("quest.exploration.ancient_ruins.objectives.");
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("ancient_ruins_dialog");

        dialog.addLine("quest.exploration.ancient_ruins.dialog.henry1",
                "quest.exploration.ancient_ruins.dialog.henry1");

        dialog.addLine("quest.exploration.ancient_ruins.dialog.henry2",
                "quest.exploration.ancient_ruins.dialog.henry2");

        dialog.addLine("quest.exploration.ancient_ruins.dialog.player1",
                "quest.exploration.ancient_ruins.dialog.player1");

        dialog.addLine("quest.exploration.ancient_ruins.dialog.henry3",
                "quest.exploration.ancient_ruins.dialog.henry3");

        dialog.addLine("quest.exploration.ancient_ruins.dialog.henry4",
                "quest.exploration.ancient_ruins.dialog.henry4");

        dialog.addLine("quest.exploration.ancient_ruins.dialog.player2",
                "quest.exploration.ancient_ruins.dialog.player2");

        dialog.addLine("quest.exploration.ancient_ruins.dialog.henry5",
                "quest.exploration.ancient_ruins.dialog.henry5");

        return dialog;
    }
}