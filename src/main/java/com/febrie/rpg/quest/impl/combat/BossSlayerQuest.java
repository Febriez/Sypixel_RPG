package com.febrie.rpg.quest.impl.combat;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 보스 슬레이어 - 전투 퀘스트
 * 강력한 보스 몬스터를 처치하는 퀘스트
 *
 * @author Febrie
 */
public class BossSlayerQuest extends Quest {

    /**
     * 기본 생성자
     */
    public BossSlayerQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.COMBAT_BOSS_SLAYER)
                .objectives(List.of(
                        new KillMobObjective("kill_mini_bosses", EntityType.WITHER, 3),
                        new KillMobObjective("kill_wither_skeletons", EntityType.WITHER_SKELETON, 20),
                        new KillMobObjective("kill_dungeon_bosses", EntityType.ELDER_GUARDIAN, 5),
                        new CollectItemObjective("collect_boss_drops", Material.NETHER_STAR, 3),
                        new KillMobObjective("kill_world_boss", EntityType.ENDER_DRAGON, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 1000)
                        .addItem(new ItemStack(Material.NETHERITE_AXE, 1))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 3))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 5))
                        .addExperience(1200)
                        .build())
                .sequential(true)
                .category(QuestCategory.COMBAT)
                .minLevel(30);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_COMBAT_BOSS_SLAYER_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_COMBAT_BOSS_SLAYER_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "kill_mini_bosses" -> LangManager.list(LangKey.QUEST_COMBAT_BOSS_SLAYER_OBJECTIVES_KILL_MINI_BOSSES, who);
            case "kill_wither_skeletons" -> LangManager.list(LangKey.QUEST_COMBAT_BOSS_SLAYER_OBJECTIVES_KILL_WITHER_SKELETONS, who);
            case "kill_dungeon_bosses" -> LangManager.list(LangKey.QUEST_COMBAT_BOSS_SLAYER_OBJECTIVES_KILL_DUNGEON_BOSSES, who);
            case "collect_boss_drops" -> LangManager.list(LangKey.QUEST_COMBAT_BOSS_SLAYER_OBJECTIVES_COLLECT_BOSS_DROPS, who);
            case "kill_world_boss" -> LangManager.list(LangKey.QUEST_COMBAT_BOSS_SLAYER_OBJECTIVES_KILL_WORLD_BOSS, who);
            default -> new ArrayList<>();
        };
    }
    
    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_COMBAT_BOSS_SLAYER_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_COMBAT_BOSS_SLAYER_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_COMBAT_BOSS_SLAYER_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_COMBAT_BOSS_SLAYER_DECLINE, who);
    }
}