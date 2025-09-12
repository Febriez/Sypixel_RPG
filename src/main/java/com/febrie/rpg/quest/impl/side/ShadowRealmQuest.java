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
import com.febrie.rpg.util.lang.quest.side.ShadowRealmLangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * Side Quest: Shadow Realm
 * Stop the shadow invasion by destroying the portal nexus
 *
 * @author Febrie
 */
public class ShadowRealmQuest extends Quest {

    /**
     * 기본 생성자
     */
    public ShadowRealmQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_SHADOW_REALM)
                .objectives(List.of(
                        new InteractNPCObjective("talk_light_mage", "light_mage"),
                        new VisitLocationObjective("shadow_portal_1", "Shadow_Portal_1"),
                        new KillMobObjective("shadow_creatures", EntityType.ENDERMAN, 15),
                        new VisitLocationObjective("shadow_portal_2", "Shadow_Portal_2"),
                        new CollectItemObjective("end_crystal_collect", Material.END_CRYSTAL, 8),
                        new KillMobObjective("portal_guardians", EntityType.WITHER_SKELETON, 6),
                        new VisitLocationObjective("portal_nexus", "Portal_Nexus"),
                        new KillMobObjective("shadow_lord", EntityType.WITHER, 1),
                        new InteractNPCObjective("seal_portals", "light_mage")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(2800)
                        .addCurrency(CurrencyType.GOLD, 700)
                        .addItem(new ItemStack(Material.NETHERITE_CHESTPLATE, 1))
                        .addItem(new ItemStack(Material.BEACON, 1))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 4))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(26);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(ShadowRealmLangKey.QUEST_SIDE_SHADOW_REALM_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(ShadowRealmLangKey.QUEST_SIDE_SHADOW_REALM_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_light_mage" -> LangManager.text(ShadowRealmLangKey.QUEST_SIDE_SHADOW_REALM_OBJECTIVES_TALK_LIGHT_MAGE, who);
            case "shadow_portal_1" -> LangManager.text(QuestCommonLangKey.QUEST_SIDE_SHADOW_REALM_OBJECTIVES_SHADOW_PORTAL_, who);
            case "shadow_creatures" -> LangManager.text(ShadowRealmLangKey.QUEST_SIDE_SHADOW_REALM_OBJECTIVES_SHADOW_CREATURES, who);
            case "shadow_portal_2" -> LangManager.text(QuestCommonLangKey.QUEST_SIDE_SHADOW_REALM_OBJECTIVES_SHADOW_PORTAL_2, who);
            case "end_crystal_collect" -> LangManager.text(ShadowRealmLangKey.QUEST_SIDE_SHADOW_REALM_OBJECTIVES_END_CRYSTAL_COLLECT, who);
            case "portal_guardians" -> LangManager.text(ShadowRealmLangKey.QUEST_SIDE_SHADOW_REALM_OBJECTIVES_PORTAL_GUARDIANS, who);
            case "portal_nexus" -> LangManager.text(ShadowRealmLangKey.QUEST_SIDE_SHADOW_REALM_OBJECTIVES_PORTAL_NEXUS, who);
            case "shadow_lord" -> LangManager.text(ShadowRealmLangKey.QUEST_SIDE_SHADOW_REALM_OBJECTIVES_SHADOW_LORD, who);
            case "seal_portals" -> LangManager.text(ShadowRealmLangKey.QUEST_SIDE_SHADOW_REALM_OBJECTIVES_SEAL_PORTALS, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(ShadowRealmLangKey.QUEST_SIDE_SHADOW_REALM_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(ShadowRealmLangKey.QUEST_SIDE_SHADOW_REALM_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(ShadowRealmLangKey.QUEST_SIDE_SHADOW_REALM_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(ShadowRealmLangKey.QUEST_SIDE_SHADOW_REALM_DECLINE, who);
    }
}