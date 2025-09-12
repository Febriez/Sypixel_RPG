package com.febrie.rpg.quest.impl.main;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * 수호자의 각성 - 메인 스토리 퀘스트
 * 고대 수호자를 깨우고 그들의 힘을 얻는 퀘스트
 *
 * @author Febrie
 */
public class GuardianAwakeningQuest extends Quest {

    /**
     * 기본 생성자
     */
    public GuardianAwakeningQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_GUARDIAN_AWAKENING)
                .objectives(List.of(
                        // 고대 서적 발견
                        new InteractNPCObjective("ancient_scholar", "ancient_scholar"), // 고대 학자
                        new VisitLocationObjective("library_archives", "ancient_library_archives"),
                        new CollectItemObjective("written_book_collect", Material.WRITTEN_BOOK, 3),
                        new CollectItemObjective("enchanted_book_collect", Material.ENCHANTED_BOOK, 5),
                        
                        // 각성의 재료 수집
                        new KillMobObjective("elemental_cores", EntityType.BLAZE, 20),
                        new CollectItemObjective("blaze_powder_collect", Material.BLAZE_POWDER, 30),
                        new KillMobObjective("frost_spirits", EntityType.STRAY, 20),
                        new CollectItemObjective("packed_ice_collect", Material.PACKED_ICE, 20),
                        new BreakBlockObjective("earth_stones", Material.ANCIENT_DEBRIS, 5),
                        new CollectItemObjective("phantom_membrane_collect", Material.PHANTOM_MEMBRANE, 10),
                        
                        // 수호자의 신전 찾기
                        new VisitLocationObjective("guardian_temple", "guardian_temple_entrance"),
                        new PlaceBlockObjective("place_runes", Material.CHISELED_STONE_BRICKS, 4),
                        new CollectItemObjective("ender_eye_collect", Material.ENDER_EYE, 1), // 제단 활성화
                        
                        // 시련 통과
                        new KillMobObjective("temple_guardians", EntityType.IRON_GOLEM, 4),
                        new SurviveObjective("elemental_storm", 300), // 5분
                        new CollectItemObjective("tripwire_hook_collect", Material.TRIPWIRE_HOOK, 4),
                        
                        // 수호자 각성
                        new VisitLocationObjective("inner_sanctum", "guardian_temple_sanctum"),
                        new InteractNPCObjective("sleeping_guardian", "sleeping_guardian"), // 잠든 수호자
                        new DeliverItemObjective("nether_star_deliver", Material.NETHER_STAR, 1, "sleeping_guardian"),
                        
                        // 수호자의 시험
                        new KillMobObjective("guardian_avatar", EntityType.ELDER_GUARDIAN, 1),
                        // 수호자 선택은 NPC 대화로 처리
                        
                        // 수호자의 축복 받기
                        new InteractNPCObjective("awakened_guardian", "awakened_guardian"),
                        new CollectItemObjective("beacon_collect", Material.BEACON, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 10000)
                        .addCurrency(CurrencyType.DIAMOND, 75)
                        .addItem(new ItemStack(Material.BEACON))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING))
                        .addItem(new ItemStack(Material.NETHERITE_CHESTPLATE))
                        .addExperience(5000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_ELEMENTAL_STONES)
                .minLevel(30)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "ancient_scholar" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_ANCIENT_SCHOLAR, who);
            case "library_archives" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_LIBRARY_ARCHIVES, who);
            case "written_book_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_WRITTEN_BOOK_COLLECT, who);
            case "enchanted_book_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_ENCHANTED_BOOK_COLLECT, who);
            case "elemental_cores" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_ELEMENTAL_CORES, who);
            case "blaze_powder_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_BLAZE_POWDER_COLLECT, who);
            case "frost_spirits" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_FROST_SPIRITS, who);
            case "packed_ice_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_PACKED_ICE_COLLECT, who);
            case "earth_stones" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_EARTH_STONES, who);
            case "phantom_membrane_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_PHANTOM_MEMBRANE_COLLECT, who);
            case "guardian_temple" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_GUARDIAN_TEMPLE, who);
            case "place_runes" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_PLACE_RUNES, who);
            case "ender_eye_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_ENDER_EYE_COLLECT, who);
            case "temple_guardians" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_TEMPLE_GUARDIANS, who);
            case "elemental_storm" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_ELEMENTAL_STORM, who);
            case "tripwire_hook_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_TRIPWIRE_HOOK_COLLECT, who);
            case "inner_sanctum" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_INNER_SANCTUM, who);
            case "sleeping_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_SLEEPING_GUARDIAN, who);
            case "nether_star_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_NETHER_STAR_DELIVER, who);
            case "guardian_avatar" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_GUARDIAN_AVATAR, who);
            case "awakened_guardian" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_AWAKENED_GUARDIAN, who);
            case "beacon_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_BEACON_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 8;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_GUARDIAN_AWAKENING_DECLINE, who);
    }
}