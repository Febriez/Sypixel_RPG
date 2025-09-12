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
import com.febrie.rpg.util.lang.quest.side.DemonHuntersLangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * Side Quest: Demon Hunters
 * Join the demon hunters in their sacred hunt against dark forces
 *
 * @author Febrie
 */
public class DemonHuntersQuest extends Quest {

    /**
     * 기본 생성자
     */
    public DemonHuntersQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_DEMON_HUNTERS)
                .objectives(List.of(
                        new InteractNPCObjective("talk_demon_hunter_captain", "demon_hunter_captain"),
                        new CollectItemObjective("iron_ingot_collect", Material.IRON_INGOT, 12),
                        new VisitLocationObjective("demon_portal", "Demon_Portal"),
                        new KillMobObjective("lesser_demons", EntityType.BLAZE, 10),
                        new CollectItemObjective("blaze_powder_collect", Material.BLAZE_POWDER, 8),
                        new CollectItemObjective("diamond_sword_collect", Material.DIAMOND_SWORD, 1),
                        new VisitLocationObjective("demon_realm", "Demon_Realm"),
                        new KillMobObjective("portal_guardian", EntityType.WITHER_SKELETON, 3),
                        new InteractNPCObjective("seal_portal", "portal_sealer")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(2500)
                        .addCurrency(CurrencyType.GOLD, 600)
                        .addItem(new ItemStack(Material.NETHERITE_SWORD, 1))
                        .addItem(new ItemStack(Material.SHIELD, 1))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 2))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(25);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(DemonHuntersLangKey.QUEST_SIDE_DEMON_HUNTERS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(DemonHuntersLangKey.QUEST_SIDE_DEMON_HUNTERS_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_demon_hunter_captain" -> LangManager.text(DemonHuntersLangKey.QUEST_SIDE_DEMON_HUNTERS_OBJECTIVES_TALK_DEMON_HUNTER_CAPTAIN, who);
            case "iron_ingot_collect" -> LangManager.text(DemonHuntersLangKey.QUEST_SIDE_DEMON_HUNTERS_OBJECTIVES_IRON_INGOT_COLLECT, who);
            case "demon_portal" -> LangManager.text(DemonHuntersLangKey.QUEST_SIDE_DEMON_HUNTERS_OBJECTIVES_DEMON_PORTAL, who);
            case "lesser_demons" -> LangManager.text(DemonHuntersLangKey.QUEST_SIDE_DEMON_HUNTERS_OBJECTIVES_LESSER_DEMONS, who);
            case "blaze_powder_collect" -> LangManager.text(DemonHuntersLangKey.QUEST_SIDE_DEMON_HUNTERS_OBJECTIVES_BLAZE_POWDER_COLLECT, who);
            case "diamond_sword_collect" -> LangManager.text(DemonHuntersLangKey.QUEST_SIDE_DEMON_HUNTERS_OBJECTIVES_DIAMOND_SWORD_COLLECT, who);
            case "demon_realm" -> LangManager.text(DemonHuntersLangKey.QUEST_SIDE_DEMON_HUNTERS_OBJECTIVES_DEMON_REALM, who);
            case "portal_guardian" -> LangManager.text(DemonHuntersLangKey.QUEST_SIDE_DEMON_HUNTERS_OBJECTIVES_PORTAL_GUARDIAN, who);
            case "seal_portal" -> LangManager.text(DemonHuntersLangKey.QUEST_SIDE_DEMON_HUNTERS_OBJECTIVES_SEAL_PORTAL, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(DemonHuntersLangKey.QUEST_SIDE_DEMON_HUNTERS_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(DemonHuntersLangKey.QUEST_SIDE_DEMON_HUNTERS_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(DemonHuntersLangKey.QUEST_SIDE_DEMON_HUNTERS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(DemonHuntersLangKey.QUEST_SIDE_DEMON_HUNTERS_DECLINE, who);
    }
}