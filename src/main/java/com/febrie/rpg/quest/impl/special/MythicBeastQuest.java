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
 * 신화의 야수 - 특수 퀘스트
 * 전설적인 4대 신수를 추적하고 계약하는 대서사시 퀘스트
 *
 * @author Febrie
 */
public class MythicBeastQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class MythicBeastBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new MythicBeastQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public MythicBeastQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private MythicBeastQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new MythicBeastBuilder()
                .id(QuestID.SPECIAL_MYTHIC_BEAST)
                .objectives(Arrays.asList(
                        // 전설의 시작
                        new InteractNPCObjective("beast_scholar", "mythic_beast_scholar"), // 신수 학자
                        new CollectItemObjective("ancient_compass", Material.COMPASS, 1),
                        new CollectItemObjective("beast_chronicles", Material.WRITTEN_BOOK, 4),
                        
                        // 첫 번째 신수 - 청룡 (동쪽의 수호자)
                        new VisitLocationObjective("eastern_shrine", "azure_dragon_shrine"),
                        new PlaceBlockObjective("offering_sapphire", Material.LAPIS_BLOCK, 9),
                        new CollectItemObjective("dragon_scales", Material.PRISMARINE_SHARD, 50),
                        new SurviveObjective("storm_trial", 600), // 10분간 폭풍 시련
                        new KillMobObjective("storm_elementals", EntityType.PHANTOM, 50),
                        new KillMobObjective("lightning_spirits", EntityType.BLAZE, 30),
                        new InteractNPCObjective("azure_dragon", "azure_dragon"), // 청룡
                        new CollectItemObjective("dragon_pearl", Material.HEART_OF_THE_SEA, 1),
                        new DeliverItemObjective("dragon_contract", "beast_scholar", Material.HEART_OF_THE_SEA, 1),
                        
                        // 두 번째 신수 - 백호 (서쪽의 수호자)
                        new VisitLocationObjective("western_shrine", "white_tiger_shrine"),
                        new PlaceBlockObjective("offering_quartz", Material.QUARTZ_BLOCK, 9),
                        new CollectItemObjective("tiger_fangs", Material.IRON_NUGGET, 100),
                        new KillMobObjective("jungle_predators", EntityType.OCELOT, 30),
                        new KillMobObjective("spirit_tigers", EntityType.SNOW_GOLEM, 20),
                        new SurviveObjective("hunt_trial", 900), // 15분간 사냥 시련
                        new KillPlayerObjective("prove_warrior", 5), // 전사의 증명
                        new InteractNPCObjective("white_tiger", "white_tiger"), // 백호
                        new CollectItemObjective("tiger_claw", Material.BONE, 1),
                        new DeliverItemObjective("tiger_contract", "beast_scholar", Material.BONE, 1),
                        
                        // 세 번째 신수 - 주작 (남쪽의 수호자)
                        new VisitLocationObjective("southern_shrine", "vermillion_bird_shrine"),
                        new PlaceBlockObjective("offering_redstone", Material.REDSTONE_BLOCK, 9),
                        new CollectItemObjective("phoenix_feathers", Material.FEATHER, 100),
                        new BreakBlockObjective("break_ice", Material.ICE, 50),
                        new KillMobObjective("fire_phoenixes", EntityType.BLAZE, 40),
                        new KillMobObjective("lava_spirits", EntityType.MAGMA_CUBE, 30),
                        new CraftItemObjective("fire_resistance", Material.POTION, 10),
                        new SurviveObjective("rebirth_trial", 600), // 10분간 재생 시련
                        new InteractNPCObjective("vermillion_bird", "vermillion_bird"), // 주작
                        new CollectItemObjective("phoenix_egg", Material.DRAGON_EGG, 1),
                        new DeliverItemObjective("bird_contract", "beast_scholar", Material.DRAGON_EGG, 1),
                        
                        // 네 번째 신수 - 현무 (북쪽의 수호자)
                        new VisitLocationObjective("northern_shrine", "black_tortoise_shrine"),
                        new PlaceBlockObjective("offering_obsidian", Material.OBSIDIAN, 9),
                        new CollectItemObjective("turtle_shells", Material.TURTLE_SCUTE, 20),
                        new KillMobObjective("sea_guardians", EntityType.GUARDIAN, 40),
                        new KillMobObjective("elder_guardians", EntityType.ELDER_GUARDIAN, 3),
                        new CollectItemObjective("ancient_coral", Material.BRAIN_CORAL_BLOCK, 10),
                        new SurviveObjective("depth_trial", 1200), // 20분간 심해 시련
                        new InteractNPCObjective("black_tortoise", "black_tortoise"), // 현무
                        new CollectItemObjective("turtle_shell", Material.TURTLE_HELMET, 1),
                        new DeliverItemObjective("tortoise_contract", "beast_scholar", Material.TURTLE_HELMET, 1),
                        
                        // 4신수 각성 의식
                        new VisitLocationObjective("convergence_shrine", "four_beasts_altar"),
                        new PlaceBlockObjective("place_contracts", Material.BEACON, 4),
                        new PayCurrencyObjective("ritual_cost", CurrencyType.DIAMOND, 200),
                        new SurviveObjective("awakening_ritual", 1800), // 30분간 각성 의식
                        
                        // 최종 시험 - 4신수 동시 전투
                        new KillMobObjective("dragon_avatar", EntityType.ENDER_DRAGON, 1),
                        new KillMobObjective("tiger_avatar", EntityType.RAVAGER, 1),
                        new KillMobObjective("bird_avatar", EntityType.PHANTOM, 100),
                        new KillMobObjective("tortoise_avatar", EntityType.ELDER_GUARDIAN, 5),
                        
                        // 신수의 가호 획득
                        new CollectItemObjective("beast_blessing", Material.NETHER_STAR, 4),
                        new InteractNPCObjective("final_contract", "mythic_beast_scholar")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 80000)
                        .addCurrency(CurrencyType.DIAMOND, 800)
                        .addItem(new ItemStack(Material.ELYTRA)) // 신수의 날개
                        .addItem(new ItemStack(Material.TRIDENT)) // 신수의 삼지창
                        .addItem(new ItemStack(Material.TURTLE_HELMET)) // 현무의 투구
                        .addItem(new ItemStack(Material.HEART_OF_THE_SEA)) // 청룡의 진주
                        .addItem(new ItemStack(Material.DRAGON_EGG)) // 주작의 알
                        .addItem(new ItemStack(Material.NETHER_STAR, 4)) // 4신수의 별
                        .addExperience(40000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SPECIAL)
                .minLevel(55)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.getMessage(who, "quest.special.mythic_beast.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList(who, "quest.special.mythic_beast.description");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return LangManager.getMessage(who, "quest.special.mythic_beast.objectives." + id);
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("mythic_beast_dialog");

        // 시작
        dialog.addLine("quest.special.mythic_beast.npc.beast_scholar",
                "quest.special.mythic_beast.dialog.scholar.line1");

        dialog.addLine("quest.special.mythic_beast.npc.beast_scholar",
                "quest.special.mythic_beast.dialog.scholar.line2");

        dialog.addLine("quest.special.mythic_beast.npc.player",
                "quest.special.mythic_beast.dialog.player.line1");

        dialog.addLine("quest.special.mythic_beast.npc.beast_scholar",
                "quest.special.mythic_beast.dialog.scholar.line3");

        // 청룡
        dialog.addLine("quest.special.mythic_beast.npc.azure_dragon",
                "quest.special.mythic_beast.dialog.dragon.line1");

        dialog.addLine("quest.special.mythic_beast.npc.azure_dragon",
                "quest.special.mythic_beast.dialog.dragon.line2");

        // 백호
        dialog.addLine("quest.special.mythic_beast.npc.white_tiger",
                "quest.special.mythic_beast.dialog.tiger.line1");

        dialog.addLine("quest.special.mythic_beast.npc.white_tiger",
                "quest.special.mythic_beast.dialog.tiger.line2");

        // 주작
        dialog.addLine("quest.special.mythic_beast.npc.vermillion_bird",
                "quest.special.mythic_beast.dialog.bird.line1");

        dialog.addLine("quest.special.mythic_beast.npc.vermillion_bird",
                "quest.special.mythic_beast.dialog.bird.line2");

        // 현무
        dialog.addLine("quest.special.mythic_beast.npc.black_tortoise",
                "quest.special.mythic_beast.dialog.tortoise.line1");

        dialog.addLine("quest.special.mythic_beast.npc.black_tortoise",
                "quest.special.mythic_beast.dialog.tortoise.line2");

        // 최종 의식
        dialog.addLine("quest.special.mythic_beast.npc.beast_scholar",
                "quest.special.mythic_beast.dialog.scholar.line4");

        dialog.addLine("quest.special.mythic_beast.npc.beast_scholar",
                "quest.special.mythic_beast.dialog.scholar.line5");

        // 완료
        dialog.addLine("quest.special.mythic_beast.npc.beast_scholar",
                "quest.special.mythic_beast.dialog.scholar.line6");

        dialog.addLine("quest.special.mythic_beast.npc.beast_scholar",
                "quest.special.mythic_beast.dialog.scholar.line7");

        return dialog;
    }
}
