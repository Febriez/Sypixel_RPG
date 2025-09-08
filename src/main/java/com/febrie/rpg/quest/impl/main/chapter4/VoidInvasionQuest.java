package com.febrie.rpg.quest.impl.main.chapter4;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;

import com.febrie.rpg.util.LangKey;
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
 * 공허의 침공 - 메인 스토리 퀘스트 (Chapter 4)
 * 공허 차원에서의 대규모 침공을 막는 퀘스트
 *
 * @author Febrie
 */
public class VoidInvasionQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public VoidInvasionQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_VOID_INVASION)
                .objectives(List.of(
                        // 침공 경고
                        new InteractNPCObjective("realm_guardian", "realm_guardian", 1),
                        new VisitLocationObjective("invasion_site", "void_invasion_site"),
                        new KillMobObjective("void_scouts", EntityType.ENDERMAN, 30),
                        new CollectItemObjective("invasion_intel", Material.WRITTEN_BOOK, 3),
                        
                        // 방어 준비
                        new InteractNPCObjective("defense_commander", "defense_commander", 1),
                        new CollectItemObjective("defense_supplies", Material.IRON_BLOCK, 50),
                        new PlaceBlockObjective("build_barricades", Material.IRON_BARS, 100),
                        new CollectItemObjective("void_weapons", Material.NETHERITE_SWORD, 5),
                        
                        // 첫 번째 파도
                        new KillMobObjective("void_soldiers", EntityType.ENDERMAN, 100),
                        new KillMobObjective("void_mages", EntityType.EVOKER, 30),
                        new SurviveObjective("first_wave", 600), // 10분
                        new CollectItemObjective("void_cores", Material.ENDER_PEARL, 50),
                        
                        // 두 번째 파도
                        new KillMobObjective("void_knights", EntityType.IRON_GOLEM, 40),
                        new KillMobObjective("void_beasts", EntityType.RAVAGER, 20),
                        new SurviveObjective("second_wave", 900), // 15분
                        new CollectItemObjective("void_essence", Material.ENDER_EYE, 30),
                        
                        // 보스 전투
                        new InteractNPCObjective("void_general", "void_general", 1),
                        new KillMobObjective("void_general_battle", EntityType.WITHER, 3),
                        new CollectItemObjective("general_crown", Material.WITHER_SKELETON_SKULL, 1),
                        
                        // 포탈 봉인
                        new VisitLocationObjective("void_portal", "void_portal_location"),
                        new PlaceBlockObjective("seal_portal", Material.BEDROCK, 20),
                        new CollectItemObjective("sealing_stone", Material.NETHER_STAR, 3),
                        new SurviveObjective("sealing_ritual", 300), // 5분
                        
                        // 승리
                        new InteractNPCObjective("realm_guardian_victory", "realm_guardian", 1),
                        new CollectItemObjective("victory_medal", Material.GOLDEN_APPLE, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 45000)
                        .addCurrency(CurrencyType.DIAMOND, 400)
                        .addItem(new ItemStack(Material.NETHERITE_HELMET))
                        .addItem(new ItemStack(Material.NETHERITE_LEGGINGS))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 5))
                        .addItem(new ItemStack(Material.NETHER_STAR, 3))
                        .addExperience(22000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_REALM_PORTAL)
                .minLevel(75)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_VOID_INVASION_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "realm_guardian" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_REALM_GUARDIAN, who);
            case "invasion_site" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_INVASION_SITE, who);
            case "void_scouts" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_VOID_SCOUTS, who);
            case "invasion_intel" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_INVASION_INTEL, who);
            case "defense_commander" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_DEFENSE_COMMANDER, who);
            case "defense_supplies" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_DEFENSE_SUPPLIES, who);
            case "build_barricades" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_BUILD_BARRICADES, who);
            case "void_weapons" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_VOID_WEAPONS, who);
            case "void_soldiers" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_VOID_SOLDIERS, who);
            case "void_mages" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_VOID_MAGES, who);
            case "first_wave" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_FIRST_WAVE, who);
            case "void_cores" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_VOID_CORES, who);
            case "void_knights" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_VOID_KNIGHTS, who);
            case "void_beasts" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_VOID_BEASTS, who);
            case "second_wave" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_SECOND_WAVE, who);
            case "void_essence" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_VOID_ESSENCE, who);
            case "void_general" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_VOID_GENERAL, who);
            case "void_general_battle" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_VOID_GENERAL_BATTLE, who);
            case "general_crown" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_GENERAL_CROWN, who);
            case "void_portal" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_VOID_PORTAL, who);
            case "seal_portal" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_SEAL_PORTAL, who);
            case "sealing_stone" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_SEALING_STONE, who);
            case "sealing_ritual" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_SEALING_RITUAL, who);
            case "realm_guardian_victory" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_REALM_GUARDIAN_VICTORY, who);
            case "victory_medal" -> LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_OBJECTIVES_VICTORY_MEDAL, who);
            default -> List.of(Component.text("Objective: " + objective.getId()));
        };
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_VOID_INVASION_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_VOID_INVASION_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_VOID_INVASION_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_VOID_INVASION_DECLINE, who);
    }
}