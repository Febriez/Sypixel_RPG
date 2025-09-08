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

import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;

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
                        new InteractNPCObjective("ancient_scholar", "ancient_scholar", 1), // 고대 학자
                        new VisitLocationObjective("library_archives", "ancient_library_archives"),
                        new CollectItemObjective("ancient_tome", Material.WRITTEN_BOOK, 3),
                        new CollectItemObjective("guardian_runes", Material.ENCHANTED_BOOK, 5),
                        
                        // 각성의 재료 수집
                        new KillMobObjective("elemental_cores", EntityType.BLAZE, 20),
                        new CollectItemObjective("fire_essence", Material.BLAZE_POWDER, 30),
                        new KillMobObjective("frost_spirits", EntityType.STRAY, 20),
                        new CollectItemObjective("ice_crystals", Material.PACKED_ICE, 20),
                        new BreakBlockObjective("earth_stones", Material.ANCIENT_DEBRIS, 5),
                        new CollectItemObjective("wind_feathers", Material.PHANTOM_MEMBRANE, 10),
                        
                        // 수호자의 신전 찾기
                        new VisitLocationObjective("guardian_temple", "guardian_temple_entrance"),
                        new PlaceBlockObjective("place_runes", Material.CHISELED_STONE_BRICKS, 4),
                        new CollectItemObjective("activate_altar", Material.ENDER_EYE, 1), // 제단 활성화
                        
                        // 시련 통과
                        new KillMobObjective("temple_guardians", EntityType.IRON_GOLEM, 4),
                        new SurviveObjective("elemental_storm", 300), // 5분
                        new CollectItemObjective("guardian_keys", Material.TRIPWIRE_HOOK, 4),
                        
                        // 수호자 각성
                        new VisitLocationObjective("inner_sanctum", "guardian_temple_sanctum"),
                        new InteractNPCObjective("sleeping_guardian", "sleeping_guardian", 1), // 잠든 수호자
                        new DeliverItemObjective("offer_essences", "sleeping_guardian", Material.NETHER_STAR, 1),
                        
                        // 수호자의 시험
                        new KillMobObjective("guardian_avatar", EntityType.ELDER_GUARDIAN, 1),
                        // 수호자 선택은 NPC 대화로 처리
                        
                        // 수호자의 축복 받기
                        new InteractNPCObjective("awakened_guardian", "awakened_guardian", 1),
                        new CollectItemObjective("guardian_blessing", Material.BEACON, 1)
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
        return LangManager.text(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "ancient_scholar" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_ANCIENT_SCHOLAR, who);
            case "library_archives" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_LIBRARY_ARCHIVES, who);
            case "ancient_tome" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_ANCIENT_TOME, who);
            case "guardian_runes" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_GUARDIAN_RUNES, who);
            case "elemental_cores" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_ELEMENTAL_CORES, who);
            case "fire_essence" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_FIRE_ESSENCE, who);
            case "frost_spirits" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_FROST_SPIRITS, who);
            case "ice_crystals" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_ICE_CRYSTALS, who);
            case "earth_stones" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_EARTH_STONES, who);
            case "wind_feathers" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_WIND_FEATHERS, who);
            case "guardian_temple" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_GUARDIAN_TEMPLE, who);
            case "place_runes" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_PLACE_RUNES, who);
            case "activate_altar" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_ACTIVATE_ALTAR, who);
            case "temple_guardians" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_TEMPLE_GUARDIANS, who);
            case "elemental_storm" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_ELEMENTAL_STORM, who);
            case "guardian_keys" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_GUARDIAN_KEYS, who);
            case "inner_sanctum" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_INNER_SANCTUM, who);
            case "sleeping_guardian" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_SLEEPING_GUARDIAN, who);
            case "offer_essences" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_OFFER_ESSENCES, who);
            case "guardian_avatar" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_GUARDIAN_AVATAR, who);
            case "awakened_guardian" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_AWAKENED_GUARDIAN, who);
            case "guardian_blessing" -> LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_OBJECTIVES_GUARDIAN_BLESSING, who);
            default -> List.of(Component.text("Objective: " + objective.getId()));
        };
    }

    @Override
    public int getDialogCount() {
        return 8;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_GUARDIAN_AWAKENING_DECLINE, who);
    }
}