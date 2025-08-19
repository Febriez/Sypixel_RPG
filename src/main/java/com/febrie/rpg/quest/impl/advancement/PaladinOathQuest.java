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
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 성기사의 서약 - 직업 전직 퀘스트
 * 전사에서 성기사로 승급하는 신성한 서약 퀘스트
 *
 * @author Febrie
 */
public class PaladinOathQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class PaladinOathBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new PaladinOathQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public PaladinOathQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private PaladinOathQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new PaladinOathBuilder()
                .id(QuestID.CLASS_PALADIN_OATH)
                .objectives(Arrays.asList(
                        // 성기사의 길 시작
                        new InteractNPCObjective("paladin_mentor", "paladin_mentor"), // 성기사 스승
                        new ReachLevelObjective("warrior_mastery", 30),
                        new InteractNPCObjective("oath_preparation", "paladin_mentor"),
                        
                        // 첫 번째 미덕 - 용기
                        new VisitLocationObjective("courage_trial", "trial_of_courage"),
                        new KillMobObjective("face_fears", EntityType.WITHER_SKELETON, 50),
                        new KillMobObjective("defeat_champion", EntityType.IRON_GOLEM, 10),
                        new SurviveObjective("courage_test", 600), // 10분간 시련
                        new CollectItemObjective("courage_emblem", Material.IRON_INGOT, 30),
                        
                        // 두 번째 미덕 - 정의
                        new VisitLocationObjective("justice_court", "hall_of_justice"),
                        new InteractNPCObjective("judge_npc", "justice_judge"), // 정의의 심판관
                        new KillMobObjective("punish_evil", EntityType.PILLAGER, 30),
                        new KillMobObjective("destroy_undead", EntityType.ZOMBIE, 100),
                        new DeliverItemObjective("return_stolen", "피해자", Material.EMERALD, 20),
                        new CollectItemObjective("justice_scale", Material.GOLD_INGOT, 20),
                        
                        // 세 번째 미덕 - 자비
                        new VisitLocationObjective("mercy_temple", "temple_of_mercy"),
                        new CollectItemObjective("healing_herbs", Material.GLISTERING_MELON_SLICE, 10),
                        new CraftItemObjective("brew_potions", Material.POTION, 20),
                        new DeliverItemObjective("heal_wounded", "부상병", Material.POTION, 10),
                        new PayCurrencyObjective("charity", CurrencyType.GOLD, 5000),
                        new CollectItemObjective("mercy_tears", Material.GHAST_TEAR, 5),
                        
                        // 네 번째 미덕 - 희생
                        new VisitLocationObjective("sacrifice_altar", "altar_of_sacrifice"),
                        new CollectItemObjective("valuable_items", Material.DIAMOND, 30),
                        new PlaceBlockObjective("place_offering", Material.DIAMOND_BLOCK, 3),
                        new SurviveObjective("endure_pain", 300), // 5분간 고통 견디기
                        new PayCurrencyObjective("sacrifice_wealth", CurrencyType.DIAMOND, 50),
                        new CollectItemObjective("sacrifice_token", Material.TOTEM_OF_UNDYING, 1),
                        
                        // 신성한 무기 제작
                        new InteractNPCObjective("holy_weaponsmith", "holy_weaponsmith"), // 신성 대장장이
                        new CollectItemObjective("blessed_metal", Material.GOLD_BLOCK, 5),
                        new CollectItemObjective("holy_water", Material.POTION, 3),
                        new CraftItemObjective("forge_sword", Material.GOLDEN_SWORD, 1),
                        new DeliverItemObjective("bless_weapon", "성기사 스승", Material.GOLDEN_SWORD, 1),
                        
                        // 최종 서약 의식
                        new VisitLocationObjective("oath_cathedral", "sacred_cathedral"),
                        new PlaceBlockObjective("light_candles", Material.CANDLE, 7),
                        new InteractNPCObjective("begin_ceremony", "paladin_mentor"),
                        new KillMobObjective("final_trial", EntityType.WITHER, 1),
                        new SurviveObjective("divine_light", 900), // 15분간 신성한 빛
                        
                        // 성기사 승급
                        new CollectItemObjective("paladin_seal", Material.NETHER_STAR, 1),
                        new InteractNPCObjective("complete_oath", "paladin_mentor")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 10000)
                        .addCurrency(CurrencyType.DIAMOND, 75)
                        .addItem(new ItemStack(Material.GOLDEN_HELMET)) // 성기사 투구
                        .addItem(new ItemStack(Material.GOLDEN_CHESTPLATE)) // 성기사 갑옷
                        .addItem(new ItemStack(Material.SHIELD)) // 성기사 방패
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 5))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING))
                        .addExperience(5000)
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
        return Component.translatable("quest.advancement.paladin_oath.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return List.of(Component.translatable("quest.advancement.paladin_oath.description"));
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return Component.translatable("quest.advancement.paladin_oath.objectives." + id);
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("paladin_oath_dialog");

        // 시작
        dialog.addLine("quest.advancement.paladin_oath.dialog.mentor",
                "quest.advancement.paladin_oath.dialog.mentor.line1");

        dialog.addLine("quest.advancement.paladin_oath.dialog.mentor",
                "quest.advancement.paladin_oath.dialog.mentor.line2");

        dialog.addLine("quest.advancement.paladin_oath.dialog.player",
                "quest.advancement.paladin_oath.dialog.player.line1");

        dialog.addLine("quest.advancement.paladin_oath.dialog.mentor",
                "quest.advancement.paladin_oath.dialog.mentor.line3");

        // 용기의 시련
        dialog.addLine("quest.advancement.paladin_oath.dialog.mentor",
                "quest.advancement.paladin_oath.dialog.mentor.line4");

        // 정의의 시련
        dialog.addLine("quest.advancement.paladin_oath.dialog.judge",
                "quest.advancement.paladin_oath.dialog.judge.line1");

        dialog.addLine("quest.advancement.paladin_oath.dialog.judge",
                "quest.advancement.paladin_oath.dialog.judge.line2");

        // 자비의 시련
        dialog.addLine("quest.advancement.paladin_oath.dialog.mentor",
                "quest.advancement.paladin_oath.dialog.mentor.line5");

        // 희생의 시련
        dialog.addLine("quest.advancement.paladin_oath.dialog.mentor",
                "quest.advancement.paladin_oath.dialog.mentor.line6");

        // 신성한 무기
        dialog.addLine("quest.advancement.paladin_oath.dialog.weaponsmith",
                "quest.advancement.paladin_oath.dialog.weaponsmith.line1");

        // 최종 의식
        dialog.addLine("quest.advancement.paladin_oath.dialog.mentor",
                "quest.advancement.paladin_oath.dialog.mentor.line7");

        dialog.addLine("quest.advancement.paladin_oath.dialog.mentor",
                "quest.advancement.paladin_oath.dialog.mentor.line8");

        // 완료
        dialog.addLine("quest.advancement.paladin_oath.dialog.mentor",
                "quest.advancement.paladin_oath.dialog.mentor.line9");

        dialog.addLine("quest.advancement.paladin_oath.dialog.mentor",
                "quest.advancement.paladin_oath.dialog.mentor.line10");

        return dialog;
    }
}