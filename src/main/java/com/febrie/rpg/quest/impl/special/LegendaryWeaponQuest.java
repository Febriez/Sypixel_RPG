package com.febrie.rpg.quest.impl.special;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;
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
 * 전설의 무기 - 특수 퀘스트
 * 고대의 전설 무기를 제작하는 대서사시 퀘스트
 *
 * @author Febrie
 */
public class LegendaryWeaponQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class LegendaryWeaponBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new LegendaryWeaponQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public LegendaryWeaponQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private LegendaryWeaponQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new LegendaryWeaponBuilder()
                .id(QuestID.SPECIAL_LEGENDARY_WEAPON)
                .objectives(Arrays.asList(
                        // 전설의 시작
                        new InteractNPCObjective("ancient_blacksmith", "ancient_blacksmith"), // 고대 대장장이
                        new CollectItemObjective("ancient_blueprints", Material.WRITTEN_BOOK, 1),
                        new VisitLocationObjective("forgotten_forge", "ancient_forge_location"),
                        
                        // 첫 번째 재료 - 별의 정수
                        new VisitLocationObjective("star_peak", "celestial_mountain_peak"),
                        new KillMobObjective("star_guardians", EntityType.BLAZE, 30),
                        new CollectItemObjective("star_fragments", Material.NETHER_STAR, 5),
                        new SurviveObjective("meteor_shower", 300), // 5분간 유성우 생존
                        new CollectItemObjective("celestial_core", Material.BEACON, 1),
                        
                        // 두 번째 재료 - 심연의 심장
                        new VisitLocationObjective("abyss_entrance", "deepest_cave_entrance"),
                        new BreakBlockObjective("mine_deep", Material.DEEPSLATE, 100),
                        new KillMobObjective("abyss_dwellers", EntityType.WARDEN, 3),
                        new CollectItemObjective("void_crystals", Material.ECHO_SHARD, 10),
                        new CollectItemObjective("heart_of_abyss", Material.SCULK_CATALYST, 1),
                        
                        // 세 번째 재료 - 용의 불꽃
                        new InteractNPCObjective("dragon_keeper", "dragon_keeper"), // 용의 수호자
                        new PayCurrencyObjective("dragon_tribute", CurrencyType.DIAMOND, 50),
                        new VisitLocationObjective("dragon_nest", "ancient_dragon_nest"),
                        new KillMobObjective("dragon_whelps", EntityType.PHANTOM, 25),
                        new CollectItemObjective("dragon_scales", Material.PHANTOM_MEMBRANE, 20),
                        new KillMobObjective("elder_dragon", EntityType.ENDER_DRAGON, 1),
                        new CollectItemObjective("dragon_flame", Material.DRAGON_BREATH, 5),
                        
                        // 네 번째 재료 - 신의 축복
                        new VisitLocationObjective("temple_of_gods", "divine_temple"),
                        new PlaceBlockObjective("offering_gold", Material.GOLD_BLOCK, 20),
                        new PlaceBlockObjective("offering_diamond", Material.DIAMOND_BLOCK, 10),
                        new SurviveObjective("divine_trial", 600), // 10분간 신의 시험
                        new KillPlayerObjective("prove_worth", 5), // PvP로 가치 증명
                        new CollectItemObjective("divine_blessing", Material.ENCHANTED_GOLDEN_APPLE, 1),
                        
                        // 대장정 제작 준비
                        new DeliverItemObjective("deliver_star", "ancient_blacksmith", Material.BEACON, 1),
                        new DeliverItemObjective("deliver_heart", "ancient_blacksmith", Material.SCULK_CATALYST, 1),
                        new DeliverItemObjective("deliver_flame", "ancient_blacksmith", Material.DRAGON_BREATH, 5),
                        new DeliverItemObjective("deliver_blessing", "ancient_blacksmith", Material.ENCHANTED_GOLDEN_APPLE, 1),
                        
                        // 제작 과정
                        new CollectItemObjective("mythril_ingots", Material.NETHERITE_INGOT, 10),
                        new PlaceBlockObjective("setup_anvil", Material.ANVIL, 1),
                        new CraftItemObjective("forge_base", Material.NETHERITE_SWORD, 1),
                        new PayCurrencyObjective("enchanting_cost", CurrencyType.GOLD, 20000),
                        
                        // 최종 각성
                        new VisitLocationObjective("awakening_altar", "legendary_altar"),
                        new PlaceBlockObjective("place_weapon", Material.NETHERITE_SWORD, 1),
                        new KillMobObjective("trial_of_legends", EntityType.WITHER, 3),
                        new SurviveObjective("final_awakening", 900), // 15분간 최종 각성
                        
                        // 완성
                        new CollectItemObjective("legendary_weapon", Material.NETHERITE_SWORD, 1),
                        new InteractNPCObjective("completion_ceremony", "ancient_blacksmith")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 50000)
                        .addCurrency(CurrencyType.DIAMOND, 500)
                        .addItem(new ItemStack(Material.NETHERITE_SWORD)) // 전설의 무기
                        .addItem(new ItemStack(Material.NETHER_STAR, 3))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 10))
                        .addItem(new ItemStack(Material.NETHERITE_INGOT, 5))
                        .addExperience(20000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SPECIAL)
                .minLevel(50)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.getMessage(who, "special.legendary_weapon.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList(who, "quest.special.legendary_weapon.description");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return LangManager.getMessage(who, "quest.special.legendary_weapon.objectives." + id);
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("legendary_weapon_dialog");

        // 시작
        dialog.addLine("quest.special.legendary_weapon.npc.ancient_blacksmith",
                "quest.special.legendary_weapon.dialog.blacksmith.line1");

        dialog.addLine("quest.special.legendary_weapon.npc.ancient_blacksmith",
                "quest.special.legendary_weapon.dialog.blacksmith.line2");

        dialog.addLine("quest.special.legendary_weapon.npc.player",
                "quest.special.legendary_weapon.dialog.player.line1");

        dialog.addLine("quest.special.legendary_weapon.npc.ancient_blacksmith",
                "quest.special.legendary_weapon.dialog.blacksmith.line3");

        // 재료 설명
        dialog.addLine("quest.special.legendary_weapon.npc.ancient_blacksmith",
                "quest.special.legendary_weapon.dialog.blacksmith.line4");

        dialog.addLine("quest.special.legendary_weapon.npc.ancient_blacksmith",
                "quest.special.legendary_weapon.dialog.blacksmith.line5");

        // 재료 수집 후
        dialog.addLine("quest.special.legendary_weapon.npc.ancient_blacksmith",
                "quest.special.legendary_weapon.dialog.blacksmith.line6");

        dialog.addLine("quest.special.legendary_weapon.npc.ancient_blacksmith",
                "quest.special.legendary_weapon.dialog.blacksmith.line7");

        // 제작 과정
        dialog.addLine("quest.special.legendary_weapon.npc.ancient_blacksmith",
                "quest.special.legendary_weapon.dialog.blacksmith.line8");

        // 각성 전
        dialog.addLine("quest.special.legendary_weapon.npc.ancient_blacksmith",
                "quest.special.legendary_weapon.dialog.blacksmith.line9");

        dialog.addLine("quest.special.legendary_weapon.npc.ancient_blacksmith",
                "quest.special.legendary_weapon.dialog.blacksmith.line10");

        // 완성
        dialog.addLine("quest.special.legendary_weapon.npc.ancient_blacksmith",
                "quest.special.legendary_weapon.dialog.blacksmith.line11");

        dialog.addLine("quest.special.legendary_weapon.npc.ancient_blacksmith",
                "quest.special.legendary_weapon.dialog.blacksmith.line12");

        return dialog;
    }
}