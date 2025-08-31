package com.febrie.rpg.quest.impl.main.chapter3;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangHelper;
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
 * 용의 각성 - 메인 스토리 퀘스트 (Chapter 3)
 * 고대 용을 깨우고 동맹을 맺는 퀘스트
 *
 * @author Febrie
 */
public class DragonAwakeningQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public DragonAwakeningQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_DRAGON_AWAKENING)
                .objectives(Arrays.asList(
                        // 전설 조사
                        new InteractNPCObjective("dragon_sage", "dragon_sage"), // 용의 현자
                        new CollectItemObjective("ancient_scrolls", Material.WRITTEN_BOOK, 5),
                        new VisitLocationObjective("ancient_library", "dragon_library"),
                        new CollectItemObjective("dragon_lore", Material.ENCHANTED_BOOK, 3),
                        
                        // 용의 신전 찾기
                        new VisitLocationObjective("mountain_peak", "dragon_mountain_peak"),
                        new BreakBlockObjective("clear_path", Material.STONE, 50),
                        new KillMobObjective("mountain_guardians", EntityType.IRON_GOLEM, 5),
                        new VisitLocationObjective("dragon_shrine", "ancient_dragon_shrine"),
                        
                        // 각성 의식 준비
                        new CollectItemObjective("dragon_tears", Material.GHAST_TEAR, 3),
                        new CollectItemObjective("ancient_gold", Material.GOLD_BLOCK, 10),
                        new CollectItemObjective("emerald_offering", Material.EMERALD_BLOCK, 5),
                        new CollectItemObjective("dragon_breath", Material.DRAGON_BREATH, 3),
                        
                        // 각성 의식
                        new PlaceBlockObjective("place_gold", Material.GOLD_BLOCK, 10),
                        new PlaceBlockObjective("place_emerald", Material.EMERALD_BLOCK, 5),
                        new CollectItemObjective("ritual_catalyst", Material.END_CRYSTAL, 4),
                        // END_CRYSTAL은 엔티티이므로 PlaceBlockObjective를 사용할 수 없음
                        // 대신 obsidian을 놓는 것으로 변경
                        new PlaceBlockObjective("place_obsidian", Material.OBSIDIAN, 4),
                        new SurviveObjective("ritual_duration", 600), // 10분
                        
                        // 용의 시험
                        new KillMobObjective("flame_dragons", EntityType.BLAZE, 20),
                        new KillMobObjective("dragon_priests", EntityType.EVOKER, 10),
                        new CollectItemObjective("dragon_scales", Material.PRISMARINE_SHARD, 20),
                        new CollectItemObjective("dragon_bones", Material.BONE, 50),
                        
                        // 용의 둥지 진입
                        new VisitLocationObjective("dragon_lair_entrance", "dragon_lair_entrance"),
                        new KillMobObjective("lair_guardians", EntityType.ELDER_GUARDIAN, 3),
                        new CollectItemObjective("lair_key", Material.HEART_OF_THE_SEA, 1),
                        new VisitLocationObjective("inner_lair", "dragon_inner_lair"),
                        
                        // 용과의 만남
                        new InteractNPCObjective("sleeping_dragon", "ancient_dragon"), // 잠든 고대 용
                        new CollectItemObjective("dragon_egg", Material.DRAGON_EGG, 1),
                        new DeliverItemObjective("offer_treasures", "sleeping_dragon", Material.DIAMOND_BLOCK, 10),
                        
                        // 용과의 결투
                        new KillMobObjective("dragon_test", EntityType.ENDER_DRAGON, 1),
                        
                        // 동맹 체결
                        new InteractNPCObjective("dragon_pact", "ancient_dragon"),
                        new CollectItemObjective("dragon_heart", Material.NETHER_STAR, 1),
                        new CollectItemObjective("pact_scroll", Material.WRITTEN_BOOK, 1),
                        new DeliverItemObjective("complete_pact", "dragon_sage", Material.WRITTEN_BOOK, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 20000)
                        .addCurrency(CurrencyType.DIAMOND, 150)
                        .addItem(new ItemStack(Material.ELYTRA)) // 용의 날개
                        .addItem(new ItemStack(Material.DRAGON_HEAD))
                        .addItem(new ItemStack(Material.DRAGON_BREATH, 10))
                        .addItem(new ItemStack(Material.END_CRYSTAL, 4))
                        .addItem(new ItemStack(Material.SHULKER_BOX, 3))
                        .addExperience(10000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_CORRUPTED_LANDS)
                .minLevel(45)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_DRAGON_AWAKENING_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return LangManager.get("quest.main.dragon_awakening.objectives." + id, who);
    }

    @Override
    public int getDialogCount() {
        return 12;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangHelper.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_DIALOGS_0, who);
            case 1 -> LangHelper.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_DIALOGS_1, who);
            case 2 -> LangHelper.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_DIALOGS_2, who);
            case 3 -> LangHelper.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_DIALOGS_3, who);
            case 4 -> LangHelper.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_DIALOGS_4, who);
            case 5 -> LangHelper.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_DIALOGS_5, who);
            case 6 -> LangHelper.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_DIALOGS_6, who);
            case 7 -> LangHelper.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_DIALOGS_7, who);
            case 8 -> LangHelper.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_DIALOGS_8, who);
            case 9 -> LangHelper.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_DIALOGS_9, who);
            case 10 -> LangHelper.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_DIALOGS_10, who);
            case 11 -> LangHelper.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_DIALOGS_11, who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangHelper.text(LangKey.QUEST_MAIN_DRAGON_AWAKENING_DECLINE, who);
    }
}