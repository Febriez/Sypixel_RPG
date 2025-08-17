package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * 잃어버린 보물 - 사이드 퀘스트
 * 모험가가 잃어버린 보물을 찾아주는 퀘스트
 *
 * @author Febrie
 */
public class LostTreasureQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class LostTreasureBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new LostTreasureQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public LostTreasureQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private LostTreasureQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        List<QuestObjective> objectives = new ArrayList<>();
        
        // 위치 탐색 목표 (순차적으로 진행)
        // 주의: 실제 서버에서는 월드 이름을 확인해야 함
        objectives.add(new VisitLocationObjective("visit_old_ruins", 
                new Location(Bukkit.getWorld("world"), 100, 70, -200), 10.0, "오래된 유적지"));
        objectives.add(new VisitLocationObjective("visit_ancient_cave", 
                new Location(Bukkit.getWorld("world"), -150, 50, 300), 10.0, "고대 동굴"));
        objectives.add(new VisitLocationObjective("visit_hidden_shrine", 
                new Location(Bukkit.getWorld("world"), 250, 80, 150), 10.0, "숨겨진 신전"));
        
        // NPC와 상호작용 (보물 수호자)
        objectives.add(new InteractNPCObjective("talk_to_guardian", 
                "treasure_guardian")); // 보물 수호자 NPC

        return new LostTreasureBuilder()
                .id(QuestID.SIDE_LOST_TREASURE)
                .objectives(objectives)
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 500)
                        .addCurrency(CurrencyType.DIAMOND, 5)
                        .addItem(new ItemStack(Material.GOLDEN_APPLE, 2))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK))
                        .addExperience(300)
                        .build())
                .sequential(true) // 순차적으로 진행
                .category(QuestCategory.SIDE)
                .minLevel(10)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.getMessage(who, "quest.side.lost_treasure.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList(who, "quest.side.lost_treasure.description");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return LangManager.getMessage(who, "quest.side.lost_treasure.objectives." + id);
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("lost_treasure_dialog");

        dialog.addLine("quest.lost_treasure.npcs.explorer", "quest.lost_treasure.dialogs.line1");
        dialog.addLine("quest.lost_treasure.npcs.explorer", "quest.lost_treasure.dialogs.line2");
        dialog.addLine("quest.lost_treasure.npcs.explorer", "quest.lost_treasure.dialogs.line3");
        dialog.addLine("quest.lost_treasure.npcs.explorer", "quest.lost_treasure.dialogs.line4");

        return dialog;
    }
}