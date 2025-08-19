package com.febrie.rpg.quest.impl.guild;

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
 * 길드 요새 공성전 - 길드 퀘스트
 * 길드원들과 함께 적대 요새를 공략하는 대규모 전투 퀘스트
 *
 * @author Febrie
 */
public class GuildFortressSiegeQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class GuildFortressSiegeBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new GuildFortressSiegeQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public GuildFortressSiegeQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private GuildFortressSiegeQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new GuildFortressSiegeBuilder()
                .id(QuestID.GUILD_FORTRESS_SIEGE)
                .objectives(Arrays.asList(
                        // 공성전 준비
                        new InteractNPCObjective("siege_commander", "siege_commander"), // 공성 사령관
                        new ReachLevelObjective("guild_requirement", 35),
                        new PayCurrencyObjective("siege_registration", CurrencyType.GOLD, 15000),
                        
                        // 공성 무기 준비
                        new CollectItemObjective("siege_ladders", Material.LADDER, 50),
                        new CollectItemObjective("battering_ram", Material.OAK_LOG, 100),
                        new CollectItemObjective("catapult_parts", Material.IRON_BLOCK, 20),
                        new CraftItemObjective("craft_tnt", Material.TNT, 30),
                        new CollectItemObjective("arrows_supply", Material.ARROW, 500),
                        
                        // 전진 기지 구축
                        new VisitLocationObjective("siege_camp", "fortress_outskirts"),
                        new PlaceBlockObjective("build_camp", Material.WHITE_WOOL, 20),
                        new PlaceBlockObjective("place_banners", Material.WHITE_BANNER, 5),
                        new PlaceBlockObjective("setup_supplies", Material.CHEST, 10),
                        
                        // 외벽 공략
                        new VisitLocationObjective("outer_walls", "fortress_outer_walls"),
                        new KillMobObjective("wall_archers", EntityType.SKELETON, 50),
                        new KillMobObjective("wall_guards", EntityType.IRON_GOLEM, 15),
                        new BreakBlockObjective("breach_wall", Material.STONE_BRICKS, 100),
                        new PlaceBlockObjective("place_ladders", Material.LADDER, 20),
                        
                        // 외부 정원 전투
                        new VisitLocationObjective("fortress_gardens", "fortress_courtyard"),
                        new KillMobObjective("garden_defenders", EntityType.VINDICATOR, 30),
                        new KillMobObjective("war_hounds", EntityType.WOLF, 40),
                        new KillPlayerObjective("enemy_players", 10), // 적대 길드원
                        new SurviveObjective("hold_gardens", 600), // 10분간 점령
                        
                        // 내부 성채 침투
                        new VisitLocationObjective("inner_keep", "fortress_inner_keep"),
                        new KillMobObjective("elite_knights", EntityType.PIGLIN_BRUTE, 25),
                        new KillMobObjective("battle_mages", EntityType.EVOKER, 15),
                        new CollectItemObjective("keep_keys", Material.TRIPWIRE_HOOK, 3),
                        
                        // 보물고 약탈
                        new VisitLocationObjective("treasure_vault", "fortress_treasury"),
                        new BreakBlockObjective("break_vault", Material.IRON_BARS, 30),
                        new CollectItemObjective("gold_treasures", Material.GOLD_BLOCK, 50),
                        new CollectItemObjective("guild_artifacts", Material.ENCHANTED_BOOK, 10),
                        
                        // 왕좌의 방 최종전
                        new VisitLocationObjective("throne_room", "fortress_throne_room"),
                        new KillMobObjective("fortress_champion", EntityType.RAVAGER, 3),
                        new KillMobObjective("fortress_lord", EntityType.WITHER, 1),
                        new CollectItemObjective("lord_crown", Material.GOLDEN_HELMET, 1),
                        
                        // 요새 점령
                        new PlaceBlockObjective("plant_flag", Material.WHITE_BANNER, 1),
                        new InteractNPCObjective("claim_fortress", "fortress_keeper"), // 요새 관리인
                        new PayCurrencyObjective("fortress_tax", CurrencyType.GOLD, 5000),
                        
                        // 승리 보고
                        new DeliverItemObjective("deliver_crown", "siege_commander", Material.GOLDEN_HELMET, 1),
                        new DeliverItemObjective("deliver_artifacts", "guild_master", Material.ENCHANTED_BOOK, 10),
                        new InteractNPCObjective("victory_report", "siege_commander")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 50000)
                        .addCurrency(CurrencyType.DIAMOND, 300)
                        .addItem(new ItemStack(Material.BEACON, 3))
                        .addItem(new ItemStack(Material.NETHERITE_BLOCK, 2))
                        .addItem(new ItemStack(Material.SHULKER_BOX, 5))
                        .addItem(new ItemStack(Material.ELYTRA))
                        .addItem(new ItemStack(Material.TRIDENT))
                        .addExperience(12000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.GUILD)
                .addPrerequisite(QuestID.GUILD_ESTABLISHMENT)
                .minLevel(35)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.guild.fortress_siege.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return List.of() /* TODO: Convert LangManager.getList("quest.guild.fortress_siege.description") manually */;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return Component.translatable("quest.guild.fortress_siege.objectives.");
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("guild_fortress_siege_dialog");

        // 시작
        dialog.addLine("quest.guild.fortress_siege.npc.siege_commander",
                "quest.guild.fortress_siege.dialog.commander.line1");

        dialog.addLine("quest.guild.fortress_siege.npc.siege_commander",
                "quest.guild.fortress_siege.dialog.commander.line2");

        dialog.addLine("quest.guild.fortress_siege.npc.player",
                "quest.guild.fortress_siege.dialog.player.line1");

        dialog.addLine("quest.guild.fortress_siege.npc.siege_commander",
                "quest.guild.fortress_siege.dialog.commander.line3");

        // 전투 중
        dialog.addLine("quest.guild.fortress_siege.npc.siege_commander",
                "quest.guild.fortress_siege.dialog.commander.line4");

        dialog.addLine("quest.guild.fortress_siege.npc.guild_member",
                "quest.guild.fortress_siege.dialog.guild_member.line1");

        dialog.addLine("quest.guild.fortress_siege.npc.siege_commander",
                "quest.guild.fortress_siege.dialog.commander.line5");

        // 내부 진입
        dialog.addLine("quest.guild.fortress_siege.npc.siege_commander",
                "quest.guild.fortress_siege.dialog.commander.line6");

        // 최종전
        dialog.addLine("quest.guild.fortress_siege.npc.fortress_lord",
                "quest.guild.fortress_siege.dialog.fortress_lord.line1");

        dialog.addLine("quest.guild.fortress_siege.npc.player",
                "quest.guild.fortress_siege.dialog.player.line2");

        // 승리
        dialog.addLine("quest.guild.fortress_siege.npc.siege_commander",
                "quest.guild.fortress_siege.dialog.commander.line7");

        dialog.addLine("quest.guild.fortress_siege.npc.fortress_keeper",
                "quest.guild.fortress_siege.dialog.fortress_keeper.line1");

        dialog.addLine("quest.guild.fortress_siege.npc.siege_commander",
                "quest.guild.fortress_siege.dialog.commander.line8");

        return dialog;
    }
}