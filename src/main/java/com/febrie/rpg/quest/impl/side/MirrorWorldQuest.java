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
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.mirror.world.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.mirror.world.info"), who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_mirror_guardian" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.mirror.world.objectives.talk.mirror.guardian"), who);
            case "shattered_mirror" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.mirror.world.objectives.shattered.mirror"), who);
            case "glass_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.mirror.world.objectives.glass.collect"), who);
            case "mirror_portal" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.mirror.world.objectives.mirror.portal"), who);
            case "shadow_doubles" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.mirror.world.objectives.shadow.doubles"), who);
            case "emerald_block_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.mirror.world.objectives.emerald.block.collect"), who);
            case "dark_reflection" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.mirror.world.objectives.dark.reflection"), who);
            case "mirror_master" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.mirror.world.objectives.mirror.master"), who);
            case "destroy_mirror" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.mirror.world.objectives.destroy.mirror"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.mirror.world.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.mirror.world.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.mirror.world.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.mirror.world.decline"), who);
    }
}