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
 * 하늘 요새 - 메인 스토리 퀘스트 (Chapter 3)
 * 용족의 하늘 요새를 공략하는 퀘스트
 *
 * @author Febrie
 */
public class SkyFortressQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class SkyFortressBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new SkyFortressQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public SkyFortressQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private SkyFortressQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new SkyFortressBuilder()
                .id(QuestID.MAIN_SKY_FORTRESS)
                .objectives(Arrays.asList(
                        // 하늘 요새 발견
                        new InteractNPCObjective("sky_navigator", "sky_navigator"),
                        new CollectItemObjective("flying_mount", Material.ELYTRA, 1),
                        new CollectItemObjective("fireworks", Material.FIREWORK_ROCKET, 64),
                        new VisitLocationObjective("cloud_peaks", "cloud_peaks"),
                        new CollectItemObjective("sky_map", Material.MAP, 1),
                        
                        // 요새 진입 준비
                        new CollectItemObjective("wind_crystals", Material.PRISMARINE_CRYSTALS, 30),
                        new CollectItemObjective("cloud_essence", Material.QUARTZ, 50),
                        new KillMobObjective("sky_guardians", EntityType.PHANTOM, 40),
                        new CollectItemObjective("phantom_wings", Material.PHANTOM_MEMBRANE, 20),
                        new InteractNPCObjective("fortress_scout", "fortress_scout"),
                        
                        // 외벽 돌파
                        new VisitLocationObjective("fortress_gates", "sky_fortress_gates"),
                        new BreakBlockObjective("destroy_barriers", Material.IRON_BARS, 100),
                        new KillMobObjective("gate_defenders", EntityType.IRON_GOLEM, 15),
                        new CollectItemObjective("gate_key", Material.TRIPWIRE_HOOK, 3),
                        new PlaceBlockObjective("place_explosives", Material.TNT, 20),
                        
                        // 첫 번째 탑 - 바람의 탑
                        new VisitLocationObjective("wind_tower", "wind_tower"),
                        new KillMobObjective("wind_elementals", EntityType.VEX, 50),
                        new CollectItemObjective("wind_orb", Material.ENDER_PEARL, 1),
                        new SurviveObjective("wind_trial", 300), // 5분 생존
                        new InteractNPCObjective("wind_keeper", "wind_keeper"),
                        
                        // 두 번째 탑 - 번개의 탑
                        new VisitLocationObjective("lightning_tower", "lightning_tower"),
                        new KillMobObjective("storm_creatures", EntityType.WITCH, 20),
                        new CollectItemObjective("lightning_rods", Material.LIGHTNING_ROD, 10),
                        new PlaceBlockObjective("place_rods", Material.LIGHTNING_ROD, 10),
                        new CollectItemObjective("storm_orb", Material.HEART_OF_THE_SEA, 1),
                        
                        // 세 번째 탑 - 구름의 탑
                        new VisitLocationObjective("cloud_tower", "cloud_tower"),
                        new CollectItemObjective("cloud_blocks", Material.WHITE_WOOL, 100),
                        new PlaceBlockObjective("build_cloud_bridge", Material.WHITE_WOOL, 50),
                        new KillMobObjective("cloud_sentinels", EntityType.POLAR_BEAR, 10),
                        new CollectItemObjective("cloud_orb", Material.SNOWBALL, 1),
                        
                        // 중앙 첨탑
                        new VisitLocationObjective("central_spire", "central_spire"),
                        new PlaceBlockObjective("activate_orbs", Material.BEACON, 3),
                        new KillMobObjective("spire_guardians", EntityType.ELDER_GUARDIAN, 8),
                        new SurviveObjective("spire_defense", 600), // 10분 방어
                        
                        // 요새 사령관과 대결
                        new InteractNPCObjective("fortress_commander", "fortress_commander"),
                        new KillMobObjective("commander_guards", EntityType.VINDICATOR, 30),
                        new KillMobObjective("sky_fortress_commander", EntityType.RAVAGER, 5),
                        new CollectItemObjective("commander_badge", Material.GOLDEN_APPLE, 1),
                        
                        // 요새 점령
                        new PlaceBlockObjective("place_banner", Material.WHITE_BANNER, 1),
                        new InteractNPCObjective("claim_fortress", "sky_navigator"),
                        new CollectItemObjective("fortress_deed", Material.WRITTEN_BOOK, 1),
                        new CollectItemObjective("sky_crown", Material.GOLDEN_HELMET, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 35000)
                        .addCurrency(CurrencyType.DIAMOND, 300)
                        .addItem(new ItemStack(Material.ELYTRA)) // 강화된 엘리트라
                        .addItem(new ItemStack(Material.TRIDENT))
                        .addItem(new ItemStack(Material.BEACON, 2))
                        .addItem(new ItemStack(Material.SHULKER_BOX, 5))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 3))
                        .addExperience(18000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_DRAGON_PACT)
                .minLevel(60)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.main.sky_fortress.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return List.of();
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return Component.translatable("quest.main.sky_fortress.objectives." + id);
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.main.sky-fortress.dialogs.0");
            case 1 -> Component.translatable("quest.main.sky-fortress.dialogs.1");
            case 2 -> Component.translatable("quest.main.sky-fortress.dialogs.2");
            case 3 -> Component.translatable("quest.main.sky-fortress.dialogs.3");
            case 4 -> Component.translatable("quest.main.sky-fortress.dialogs.4");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.main.sky-fortress.npc-name");
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.main.sky-fortress.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.main.sky-fortress.decline");
    }
}