package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 화산 깊이 - 사이드 퀘스트
 * 위험한 화산 깊은 곳에서 연구자를 도와 용암 형성을 연구하는 퀘스트
 *
 * @author Febrie
 */
public class VolcanicDepthsQuest extends Quest {

    /**
     * 기본 생성자
     */
    public VolcanicDepthsQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_VOLCANIC_DEPTHS)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("volcano_researcher", "volcano_researcher"),
                        new VisitLocationObjective("volcano_rim", "volcano_rim"),
                        new KillMobObjective("kill_magma_cubes", EntityType.MAGMA_CUBE, 25),
                        new CollectItemObjective("volcanic_glass", Material.OBSIDIAN, 15),
                        new VisitLocationObjective("lava_chamber", "lava_chamber"),
                        new CollectItemObjective("fire_essence", Material.MAGMA_CREAM, 10)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 1500)
                        .addItem(new ItemStack(Material.FIRE_CHARGE, 20))
                        .addExperience(5000)
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(30);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.get("quest.side.volcanic_depths.name", who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.side.volcanic_depths.info", who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.side.volcanic_depths.objectives." + objective.getId(), who);
    }

    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.side.volcanic_depths.dialogs.0", who);
            case 1 -> LangManager.get("quest.side.volcanic_depths.dialogs.1", who);
            case 2 -> LangManager.get("quest.side.volcanic_depths.dialogs.2", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.side.volcanic_depths.npc_name", who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.side.volcanic_depths.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.side.volcanic_depths.decline", who);
    }
}