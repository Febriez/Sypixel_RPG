package com.febrie.rpg.quest.impl.main.chapter3;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 용의 시련 - 메인 스토리 퀘스트 (Chapter 3)
 * 고대 용이 부여한 시련을 통과하는 퀘스트
 *
 * @author Febrie
 */
public class DragonTrialsQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public DragonTrialsQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_DRAGON_TRIALS)
                .objectives(List.of(
                        // 첫 번째 시련: 힘의 시련
                        new InteractNPCObjective("ancient_dragon", "ancient_dragon"),
                        new VisitLocationObjective("trial_arena", "dragon_trial_arena"),
                        new KillMobObjective("stone_guardians", EntityType.IRON_GOLEM, 10),
                        new KillMobObjective("lava_elementals", EntityType.MAGMA_CUBE, 20),
                        new KillMobObjective("trial_champion", EntityType.RAVAGER, 3),
                        new CollectItemObjective("iron_ingot_collect", Material.IRON_INGOT, 1),
                        
                        // 두 번째 시련: 지혜의 시련
                        new VisitLocationObjective("wisdom_temple", "dragon_wisdom_temple"),
                        new CollectItemObjective("written_book_collect", Material.WRITTEN_BOOK, 10),
                        new InteractNPCObjective("wisdom_keeper", "wisdom_keeper"),
                        new CollectItemObjective("prismarine_crystals_collect", Material.PRISMARINE_CRYSTALS, 15),
                        new PlaceBlockObjective("solve_puzzle", Material.SEA_LANTERN, 5),
                        new CollectItemObjective("lapis_lazuli_collect", Material.LAPIS_LAZULI, 1),
                        
                        // 세 번째 시련: 용기의 시련
                        new VisitLocationObjective("void_realm", "dragon_void_realm"),
                        new SurviveObjective("survive_void", 300), // 5분 생존
                        new KillMobObjective("void_creatures", EntityType.ENDERMAN, 30),
                        new KillMobObjective("void_guards", EntityType.ENDERMITE, 50),
                        new CollectItemObjective("ender_pearl_collect", Material.ENDER_PEARL, 20),
                        new CollectItemObjective("ender_eye_collect", Material.ENDER_EYE, 1),
                        
                        // 네 번째 시련: 희생의 시련
                        new DeliverItemObjective("gold_block_deliver", Material.GOLD_BLOCK, 30, "ancient_dragon"),
                        new DeliverItemObjective("diamond_block_deliver", Material.DIAMOND_BLOCK, 20, "ancient_dragon"),
                        new DeliverItemObjective("emerald_block_deliver", Material.EMERALD_BLOCK, 10, "ancient_dragon"),
                        new CollectItemObjective("netherite_ingot_collect", Material.NETHERITE_INGOT, 1),
                        
                        // 최종 시련: 용의 도전
                        new VisitLocationObjective("dragon_sanctum", "dragon_sanctum"),
                        new PlaceBlockObjective("place_tokens", Material.BEACON, 4),
                        new KillMobObjective("dragon_avatar", EntityType.PHANTOM, 50),
                        new KillMobObjective("elder_dragon", EntityType.ELDER_GUARDIAN, 5),
                        new SurviveObjective("final_trial", 600), // 10분 생존
                        
                        // 시련 완료
                        new InteractNPCObjective("ancient_dragon_final", "ancient_dragon"),
                        new CollectItemObjective("nether_star_collect", Material.NETHER_STAR, 1),
                        new CollectItemObjective("netherite_chestplate_collect", Material.NETHERITE_CHESTPLATE, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 25000)
                        .addCurrency(CurrencyType.DIAMOND, 200)
                        .addItem(new ItemStack(Material.NETHERITE_SWORD))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 2))
                        .addItem(new ItemStack(Material.BEACON))
                        .addItem(new ItemStack(Material.NETHER_STAR, 2))
                        .addExperience(12000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_DRAGON_AWAKENING)
                .minLevel(50)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "ancient_dragon" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_ANCIENT_DRAGON, who);
            case "trial_arena" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_TRIAL_ARENA, who);
            case "stone_guardians" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_STONE_GUARDIANS, who);
            case "lava_elementals" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_LAVA_ELEMENTALS, who);
            case "trial_champion" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_TRIAL_CHAMPION, who);
            case "iron_ingot_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_IRON_INGOT_COLLECT, who);
            case "wisdom_temple" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_WISDOM_TEMPLE, who);
            case "written_book_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_WRITTEN_BOOK_COLLECT, who);
            case "wisdom_keeper" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_WISDOM_KEEPER, who);
            case "prismarine_crystals_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_PRISMARINE_CRYSTALS_COLLECT, who);
            case "solve_puzzle" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_SOLVE_PUZZLE, who);
            case "lapis_lazuli_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_LAPIS_LAZULI_COLLECT, who);
            case "void_realm" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_VOID_REALM, who);
            case "survive_void" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_SURVIVE_VOID, who);
            case "void_creatures" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_VOID_CREATURES, who);
            case "void_guards" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_VOID_GUARDS, who);
            case "ender_pearl_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_ENDER_PEARL_COLLECT, who);
            case "ender_eye_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_ENDER_EYE_COLLECT, who);
            case "gold_block_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_GOLD_BLOCK_DELIVER, who);
            case "diamond_block_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_DIAMOND_BLOCK_DELIVER, who);
            case "emerald_block_deliver" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_EMERALD_BLOCK_DELIVER, who);
            case "netherite_ingot_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_NETHERITE_INGOT_COLLECT, who);
            case "dragon_sanctum" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_DRAGON_SANCTUM, who);
            case "place_tokens" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_PLACE_TOKENS, who);
            case "dragon_avatar" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_DRAGON_AVATAR, who);
            case "elder_dragon" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_ELDER_DRAGON, who);
            case "final_trial" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_FINAL_TRIAL, who);
            case "ancient_dragon_final" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_ANCIENT_DRAGON_FINAL, who);
            case "nether_star_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_NETHER_STAR_COLLECT, who);
            case "netherite_chestplate_collect" -> LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_OBJECTIVES_NETHERITE_CHESTPLATE_COLLECT, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
        @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.QUEST_MAIN_DRAGON_TRIALS_DECLINE, who);
    }
}