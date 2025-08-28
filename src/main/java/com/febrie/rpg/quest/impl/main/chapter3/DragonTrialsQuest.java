package com.febrie.rpg.quest.impl.main.chapter3;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 용의 시련 - 메인 스토리 퀘스트 (Chapter 3)
 * 고대 용이 부여한 시련을 통과하는 퀘스트
 *
 * @author Febrie
 */
public class DragonTrialsQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class DragonTrialsBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new DragonTrialsQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public DragonTrialsQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private DragonTrialsQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new DragonTrialsBuilder()
                .id(QuestID.MAIN_DRAGON_TRIALS)
                .objectives(Arrays.asList(
                        // 첫 번째 시련: 힘의 시련
                        new InteractNPCObjective("ancient_dragon", "ancient_dragon"),
                        new VisitLocationObjective("trial_arena", "dragon_trial_arena"),
                        new KillMobObjective("stone_guardians", EntityType.IRON_GOLEM, 10),
                        new KillMobObjective("lava_elementals", EntityType.MAGMA_CUBE, 20),
                        new KillMobObjective("trial_champion", EntityType.RAVAGER, 3),
                        new CollectItemObjective("trial_token_strength", Material.IRON_INGOT, 1),
                        
                        // 두 번째 시련: 지혜의 시련
                        new VisitLocationObjective("wisdom_temple", "dragon_wisdom_temple"),
                        new CollectItemObjective("ancient_books", Material.WRITTEN_BOOK, 10),
                        new InteractNPCObjective("wisdom_keeper", "wisdom_keeper"),
                        new CollectItemObjective("puzzle_pieces", Material.PRISMARINE_CRYSTALS, 15),
                        new PlaceBlockObjective("solve_puzzle", Material.SEA_LANTERN, 5),
                        new CollectItemObjective("trial_token_wisdom", Material.LAPIS_LAZULI, 1),
                        
                        // 세 번째 시련: 용기의 시련
                        new VisitLocationObjective("void_realm", "dragon_void_realm"),
                        new SurviveObjective("survive_void", 300), // 5분 생존
                        new KillMobObjective("void_creatures", EntityType.ENDERMAN, 30),
                        new KillMobObjective("void_guards", EntityType.ENDERMITE, 50),
                        new CollectItemObjective("void_essence", Material.ENDER_PEARL, 20),
                        new CollectItemObjective("trial_token_courage", Material.ENDER_EYE, 1),
                        
                        // 네 번째 시련: 희생의 시련
                        new DeliverItemObjective("sacrifice_gold", "ancient_dragon", Material.GOLD_BLOCK, 30),
                        new DeliverItemObjective("sacrifice_diamonds", "ancient_dragon", Material.DIAMOND_BLOCK, 20),
                        new DeliverItemObjective("sacrifice_emeralds", "ancient_dragon", Material.EMERALD_BLOCK, 10),
                        new CollectItemObjective("trial_token_sacrifice", Material.NETHERITE_INGOT, 1),
                        
                        // 최종 시련: 용의 도전
                        new VisitLocationObjective("dragon_sanctum", "dragon_sanctum"),
                        new PlaceBlockObjective("place_tokens", Material.BEACON, 4),
                        new KillMobObjective("dragon_avatar", EntityType.PHANTOM, 50),
                        new KillMobObjective("elder_dragon", EntityType.ELDER_GUARDIAN, 5),
                        new SurviveObjective("final_trial", 600), // 10분 생존
                        
                        // 시련 완료
                        new InteractNPCObjective("ancient_dragon_final", "ancient_dragon"),
                        new CollectItemObjective("dragon_blessing", Material.NETHER_STAR, 1),
                        new CollectItemObjective("dragon_scale_armor", Material.NETHERITE_CHESTPLATE, 1)
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
        return Component.translatable("quest.main.dragon_trials.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return List.of();
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return Component.translatable("quest.main.dragon_trials.objectives." + id);
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.main.dragon-trials.dialogs.0");
            case 1 -> Component.translatable("quest.main.dragon-trials.dialogs.1");
            case 2 -> Component.translatable("quest.main.dragon-trials.dialogs.2");
            case 3 -> Component.translatable("quest.main.dragon-trials.dialogs.3");
            case 4 -> Component.translatable("quest.main.dragon-trials.dialogs.4");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.main.dragon-trials.npc-name");
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.main.dragon-trials.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.main.dragon-trials.decline");
    }
}