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
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 길드 설립 - 길드 퀘스트
 * 자신만의 길드를 창설하고 길드 마스터가 되는 퀘스트
 *
 * @author Febrie
 */
public class GuildEstablishmentQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class GuildEstablishmentBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new GuildEstablishmentQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public GuildEstablishmentQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private GuildEstablishmentQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new GuildEstablishmentBuilder()
                .id(QuestID.GUILD_ESTABLISHMENT)
                .objectives(Arrays.asList(
                        // 준비 단계
                        new InteractNPCObjective("guild_registrar", "guild_registrar"), // 길드 등록관
                        new ReachLevelObjective("level_requirement", 25),
                        new PayCurrencyObjective("registration_fee", CurrencyType.GOLD, 5000),
                        
                        // 길드 깃발 제작
                        new CollectItemObjective("gather_wool", Material.WHITE_WOOL, 6),
                        new CollectItemObjective("gather_stick", Material.STICK, 1),
                        new CollectItemObjective("gather_dyes", Material.LAPIS_LAZULI, 3),
                        new CraftItemObjective("craft_banner", Material.WHITE_BANNER, 1),
                        
                        // 길드 홀 준비
                        new VisitLocationObjective("guild_district", "guild_district"),
                        new CollectItemObjective("gather_gold", Material.GOLD_BLOCK, 10),
                        new CollectItemObjective("gather_emerald", Material.EMERALD_BLOCK, 5),
                        new PayCurrencyObjective("hall_rental", CurrencyType.GOLD, 10000),
                        
                        // 길드 멤버 모집
                        new InteractNPCObjective("recruit_npc1", "guild_recruit_warrior"), // 첫 번째 지원자
                        new InteractNPCObjective("recruit_npc2", "guild_recruit_mage"), // 두 번째 지원자
                        new InteractNPCObjective("recruit_npc3", "guild_recruit_archer"), // 세 번째 지원자
                        new CollectItemObjective("member_contracts", Material.PAPER, 5),
                        
                        // 길드 창설 문서
                        new CollectItemObjective("guild_seal", Material.GOLD_NUGGET, 1),
                        new CraftItemObjective("guild_charter", Material.WRITTEN_BOOK, 1),
                        new DeliverItemObjective("submit_charter", "guild_registrar", Material.WRITTEN_BOOK, 1),
                        
                        // 길드 홀 설치
                        new VisitLocationObjective("guild_hall", "your_guild_hall"),
                        new PlaceBlockObjective("place_banner", Material.WHITE_BANNER, 1),
                        new PlaceBlockObjective("place_chest", Material.CHEST, 3),
                        new PlaceBlockObjective("place_furnace", Material.FURNACE, 2),
                        new PlaceBlockObjective("place_table", Material.CRAFTING_TABLE, 2),
                        
                        // 첫 길드 미션
                        new KillMobObjective("first_mission", EntityType.PILLAGER, 20),
                        new CollectItemObjective("mission_reward", Material.EMERALD, 30),
                        new DeliverItemObjective("complete_mission", "guild_registrar", Material.EMERALD, 30),
                        
                        // 길드 승인
                        new InteractNPCObjective("final_approval", "guild_registrar")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 20000)
                        .addCurrency(CurrencyType.DIAMOND, 100)
                        .addItem(new ItemStack(Material.BEACON)) // 길드 신호기
                        .addItem(new ItemStack(Material.ENDER_CHEST, 3))
                        .addItem(new ItemStack(Material.SHULKER_BOX, 2))
                        .addItem(new ItemStack(Material.WRITTEN_BOOK)) // 길드 마스터 가이드
                        .addExperience(5000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.GUILD)
                .minLevel(25)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.guild_establishment.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return List.of() /* TODO: Convert LangManager.getList("quest.guild_establishment.description") manually */;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return Component.translatable("quest.guild_establishment.objectives.");
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("guild_establishment_dialog");

        dialog.addLine("quest.guild_establishment.dialog.registrar",
                "quest.guild_establishment.dialog.registrar.line1");

        dialog.addLine("quest.guild_establishment.dialog.registrar",
                "quest.guild_establishment.dialog.registrar.line2");

        dialog.addLine("quest.guild_establishment.dialog.player",
                "quest.guild_establishment.dialog.player.line1");

        dialog.addLine("quest.guild_establishment.dialog.registrar",
                "quest.guild_establishment.dialog.registrar.line3");

        dialog.addLine("quest.guild_establishment.dialog.registrar",
                "quest.guild_establishment.dialog.registrar.line4");

        dialog.addLine("quest.guild_establishment.dialog.applicant1",
                "quest.guild_establishment.dialog.applicant1.line1");

        dialog.addLine("quest.guild_establishment.dialog.applicant2",
                "quest.guild_establishment.dialog.applicant2.line1");

        dialog.addLine("quest.guild_establishment.dialog.applicant3",
                "quest.guild_establishment.dialog.applicant3.line1");

        dialog.addLine("quest.guild_establishment.dialog.registrar",
                "quest.guild_establishment.dialog.registrar.line5");

        dialog.addLine("quest.guild_establishment.dialog.registrar",
                "quest.guild_establishment.dialog.registrar.line6");

        dialog.addLine("quest.guild_establishment.dialog.registrar",
                "quest.guild_establishment.dialog.registrar.line7");

        dialog.addLine("quest.guild_establishment.dialog.registrar",
                "quest.guild_establishment.dialog.registrar.line8");

        return dialog;
    }
}
