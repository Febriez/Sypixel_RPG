package com.febrie.rpg.quest.impl.advancement;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.dialog.QuestDialog;
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
 * 마법사의 깨달음 - 직업 전직 퀘스트
 * 견습 마법사에서 대마법사로 승급하는 깨달음의 여정
 *
 * @author Febrie
 */
public class MageEnlightenmentQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class MageEnlightenmentBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new MageEnlightenmentQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public MageEnlightenmentQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private MageEnlightenmentQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new MageEnlightenmentBuilder()
                .id(QuestID.CLASS_MAGE_ENLIGHTENMENT)
                .objectives(Arrays.asList(
                        // 마법사의 길 시작
                        new InteractNPCObjective("archmage_mentor", "archmage_mentor"), // 대마법사 스승
                        new ReachLevelObjective("mage_mastery", 30),
                        new CollectItemObjective("magic_essence", Material.LAPIS_LAZULI, 64),
                        
                        // 첫 번째 시험 - 원소 마법
                        new VisitLocationObjective("elemental_sanctum", "elemental_magic_hall"),
                        new InteractNPCObjective("fire_elemental", "fire_elemental"), // 불의 정령
                        new KillMobObjective("fire_test", EntityType.BLAZE, 30),
                        new CollectItemObjective("fire_essence", Material.BLAZE_POWDER, 20),
                        
                        new InteractNPCObjective("water_elemental", "water_elemental"), // 물의 정령
                        new CollectItemObjective("water_essence", Material.PRISMARINE_CRYSTALS, 20),
                        new CollectItemObjective("ice_shards", Material.PACKED_ICE, 10),
                        
                        new InteractNPCObjective("earth_elemental", "earth_elemental"), // 대지의 정령
                        new BreakBlockObjective("earth_test", Material.STONE, 100),
                        new CollectItemObjective("earth_essence", Material.CLAY_BALL, 30),
                        
                        new InteractNPCObjective("air_elemental", "air_elemental"), // 바람의 정령
                        new KillMobObjective("air_test", EntityType.PHANTOM, 20),
                        new CollectItemObjective("air_essence", Material.PHANTOM_MEMBRANE, 15),
                        
                        // 두 번째 시험 - 마나 제어
                        new VisitLocationObjective("mana_chamber", "arcane_meditation_room"),
                        new PlaceBlockObjective("mana_crystals", Material.SEA_LANTERN, 8),
                        new SurviveObjective("mana_overflow", 600), // 10분간 마나 폭주 견디기
                        new CollectItemObjective("pure_mana", Material.GLOWSTONE, 30),
                        new CraftItemObjective("mana_potion", Material.POTION, 20),
                        
                        // 세 번째 시험 - 금지된 지식
                        new VisitLocationObjective("forbidden_library", "restricted_magic_archive"),
                        new InteractNPCObjective("knowledge_keeper", "knowledge_keeper"), // 지식의 수호자
                        new CollectItemObjective("ancient_tomes", Material.ENCHANTED_BOOK, 10),
                        new KillMobObjective("knowledge_guardians", EntityType.VEX, 50),
                        new CollectItemObjective("forbidden_scroll", Material.WRITTEN_BOOK, 1),
                        new PayCurrencyObjective("knowledge_price", CurrencyType.DIAMOND, 30),
                        
                        // 네 번째 시험 - 마법 창조
                        new VisitLocationObjective("creation_altar", "spell_creation_altar"),
                        new CollectItemObjective("spell_components", Material.ENDER_PEARL, 10),
                        new CollectItemObjective("magic_ink", Material.INK_SAC, 20),
                        new CraftItemObjective("create_wand", Material.STICK, 1),
                        new PlaceBlockObjective("enchant_altar", Material.ENCHANTING_TABLE, 1),
                        new CollectItemObjective("new_spell", Material.ENCHANTED_BOOK, 1),
                        
                        // 최종 시험 - 마법 대결
                        new VisitLocationObjective("arcane_arena", "magical_duel_arena"),
                        new InteractNPCObjective("rival_mage", "rival_mage"), // 라이벌 마법사
                        new KillMobObjective("illusion_army", EntityType.EVOKER, 10),
                        new KillMobObjective("summoned_vex", EntityType.VEX, 100),
                        new SurviveObjective("magic_duel", 900), // 15분간 마법 대결
                        new KillMobObjective("rival_defeated", EntityType.WITCH, 5),
                        
                        // 깨달음의 순간
                        new VisitLocationObjective("enlightenment_peak", "mystic_mountain_peak"),
                        new PlaceBlockObjective("meditation_circle", Material.WHITE_CARPET, 9),
                        new SurviveObjective("final_meditation", 600), // 10분간 최종 명상
                        new CollectItemObjective("enlightenment_orb", Material.NETHER_STAR, 1),
                        
                        // 대마법사 승급
                        new DeliverItemObjective("deliver_orb", "archmage_mentor", Material.NETHER_STAR, 1),
                        new InteractNPCObjective("graduation_ceremony", "archmage_mentor")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 15000)
                        .addCurrency(CurrencyType.DIAMOND, 100)
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 10)) // 고급 마법서
                        .addItem(new ItemStack(Material.BLAZE_ROD)) // 대마법사 지팡이
                        .addItem(new ItemStack(Material.ELYTRA)) // 마법사 날개
                        .addItem(new ItemStack(Material.ENDER_EYE, 16))
                        .addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 64))
                        .addExperience(8000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.ADVANCEMENT)
                .minLevel(30)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.advancement.mage_enlightenment.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return List.of(Component.translatable("quest.advancement.mage_enlightenment.description"));
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return Component.translatable("quest.advancement.mage_enlightenment.objectives." + id);
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("mage_enlightenment_dialog");

        // 시작
        dialog.addLine("quest.advancement.mage_enlightenment.dialog.mentor",
                "quest.advancement.mage_enlightenment.dialog.mentor.line1");

        dialog.addLine("quest.advancement.mage_enlightenment.dialog.mentor",
                "quest.advancement.mage_enlightenment.dialog.mentor.line2");

        dialog.addLine("quest.advancement.mage_enlightenment.dialog.player",
                "quest.advancement.mage_enlightenment.dialog.player.line1");

        dialog.addLine("quest.advancement.mage_enlightenment.dialog.mentor",
                "quest.advancement.mage_enlightenment.dialog.mentor.line3");

        // 원소 정령들
        dialog.addLine("quest.advancement.mage_enlightenment.dialog.fire_elemental",
                "quest.advancement.mage_enlightenment.dialog.fire_elemental.line1");

        dialog.addLine("quest.advancement.mage_enlightenment.dialog.water_elemental",
                "quest.advancement.mage_enlightenment.dialog.water_elemental.line1");

        dialog.addLine("quest.advancement.mage_enlightenment.dialog.earth_elemental",
                "quest.advancement.mage_enlightenment.dialog.earth_elemental.line1");

        dialog.addLine("quest.advancement.mage_enlightenment.dialog.air_elemental",
                "quest.advancement.mage_enlightenment.dialog.air_elemental.line1");

        // 마나 제어
        dialog.addLine("quest.advancement.mage_enlightenment.dialog.mentor",
                "quest.advancement.mage_enlightenment.dialog.mentor.line4");

        // 금지된 지식
        dialog.addLine("quest.advancement.mage_enlightenment.dialog.knowledge_keeper",
                "quest.advancement.mage_enlightenment.dialog.knowledge_keeper.line1");

        // 마법 대결
        dialog.addLine("quest.advancement.mage_enlightenment.dialog.rival_mage",
                "quest.advancement.mage_enlightenment.dialog.rival_mage.line1");

        // 깨달음
        dialog.addLine("quest.advancement.mage_enlightenment.dialog.mentor",
                "quest.advancement.mage_enlightenment.dialog.mentor.line5");

        // 완료
        dialog.addLine("quest.advancement.mage_enlightenment.dialog.mentor",
                "quest.advancement.mage_enlightenment.dialog.mentor.line6");

        dialog.addLine("quest.advancement.mage_enlightenment.dialog.mentor",
                "quest.advancement.mage_enlightenment.dialog.mentor.line7");

        return dialog;
    }
}