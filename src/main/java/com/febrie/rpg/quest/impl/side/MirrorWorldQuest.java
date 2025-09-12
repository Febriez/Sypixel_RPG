package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.lang.quest.side.MirrorWorldLangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * Side Quest: Mirror World
 * Retrieve stolen artifacts from the twisted mirror dimension
 *
 * @author Febrie
 */
public class MirrorWorldQuest extends Quest {

    /**
     * 기본 생성자
     */
    public MirrorWorldQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_MIRROR_WORLD)
                .objectives(List.of(
                        new InteractNPCObjective("talk_mirror_guardian", "mirror_guardian"),
                        new VisitLocationObjective("shattered_mirror", "Shattered_Mirror"),
                        new CollectItemObjective("glass_collect", Material.GLASS, 20),
                        new VisitLocationObjective("mirror_portal", "Mirror_Portal"),
                        new KillMobObjective("shadow_doubles", EntityType.VEX, 8),
                        new CollectItemObjective("emerald_block_collect", Material.EMERALD_BLOCK, 5),
                        new VisitLocationObjective("dark_reflection", "Dark_Reflection"),
                        new KillMobObjective("mirror_master", EntityType.EVOKER, 1),
                        new InteractNPCObjective("destroy_mirror", "mirror_guardian")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(2300)
                        .addCurrency(CurrencyType.GOLD, 580)
                        .addItem(new ItemStack(Material.GLASS, 64))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 3))
                        .addItem(new ItemStack(Material.DIAMOND_BLOCK, 2))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(21);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(MirrorWorldLangKey.QUEST_SIDE_MIRROR_WORLD_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(MirrorWorldLangKey.QUEST_SIDE_MIRROR_WORLD_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_mirror_guardian" -> LangManager.text(MirrorWorldLangKey.QUEST_SIDE_MIRROR_WORLD_OBJECTIVES_TALK_MIRROR_GUARDIAN, who);
            case "shattered_mirror" -> LangManager.text(MirrorWorldLangKey.QUEST_SIDE_MIRROR_WORLD_OBJECTIVES_SHATTERED_MIRROR, who);
            case "glass_collect" -> LangManager.text(MirrorWorldLangKey.QUEST_SIDE_MIRROR_WORLD_OBJECTIVES_GLASS_COLLECT, who);
            case "mirror_portal" -> LangManager.text(MirrorWorldLangKey.QUEST_SIDE_MIRROR_WORLD_OBJECTIVES_MIRROR_PORTAL, who);
            case "shadow_doubles" -> LangManager.text(MirrorWorldLangKey.QUEST_SIDE_MIRROR_WORLD_OBJECTIVES_SHADOW_DOUBLES, who);
            case "emerald_block_collect" -> LangManager.text(MirrorWorldLangKey.QUEST_SIDE_MIRROR_WORLD_OBJECTIVES_EMERALD_BLOCK_COLLECT, who);
            case "dark_reflection" -> LangManager.text(MirrorWorldLangKey.QUEST_SIDE_MIRROR_WORLD_OBJECTIVES_DARK_REFLECTION, who);
            case "mirror_master" -> LangManager.text(MirrorWorldLangKey.QUEST_SIDE_MIRROR_WORLD_OBJECTIVES_MIRROR_MASTER, who);
            case "destroy_mirror" -> LangManager.text(MirrorWorldLangKey.QUEST_SIDE_MIRROR_WORLD_OBJECTIVES_DESTROY_MIRROR, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(MirrorWorldLangKey.QUEST_SIDE_MIRROR_WORLD_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(MirrorWorldLangKey.QUEST_SIDE_MIRROR_WORLD_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(MirrorWorldLangKey.QUEST_SIDE_MIRROR_WORLD_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(MirrorWorldLangKey.QUEST_SIDE_MIRROR_WORLD_DECLINE, who);
    }
}