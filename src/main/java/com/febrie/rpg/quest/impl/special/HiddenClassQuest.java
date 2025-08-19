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
 * 숨겨진 직업 - 특수 퀘스트
 * 비밀스러운 방법으로만 얻을 수 있는 특별한 직업 전직 퀘스트
 *
 * @author Febrie
 */
public class HiddenClassQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class HiddenClassBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new HiddenClassQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public HiddenClassQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private HiddenClassQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new HiddenClassBuilder()
                .id(QuestID.SPECIAL_HIDDEN_CLASS)
                .objectives(Arrays.asList(
                        // 숨겨진 단서 발견
                        new CollectItemObjective("mysterious_letter", Material.PAPER, 1),
                        new VisitLocationObjective("secret_location", "shadow_guild_entrance"),
                        new CollectItemObjective("use_secret_knock", Material.STICK, 1), // 비밀 노크용 막대기
                        
                        // 그림자 길드 입단 시험
                        new InteractNPCObjective("shadow_master", "shadow_master"), // 그림자 마스터
                        new SurviveObjective("darkness_test", 600), // 10분간 어둠 속 생존
                        new KillPlayerObjective("pvp_test", 1), // PvP 시험
                        
                        // 첫 번째 시련 - 은신술
                        new VisitLocationObjective("stealth_course", "shadow_training_ground"),
                        new SurviveObjective("stealth_test", 300), // 5분간 은신 생존
                        new CollectItemObjective("shadow_essence", Material.BLACK_DYE, 10),
                        new KillMobObjective("silent_kills", EntityType.ZOMBIE, 20), // 소리없이 처치
                        
                        // 두 번째 시련 - 독술
                        new CollectItemObjective("poison_ingredients", Material.SPIDER_EYE, 15),
                        new CollectItemObjective("fermented_eyes", Material.FERMENTED_SPIDER_EYE, 10),
                        new CraftItemObjective("craft_poisons", Material.POTION, 5),
                        new KillMobObjective("poison_test", EntityType.CAVE_SPIDER, 30),
                        
                        // 세 번째 시련 - 정보 수집
                        new InteractNPCObjective("spy_merchant", "spy_merchant"), // 정보상
                        new PayCurrencyObjective("bribe_informant", CurrencyType.GOLD, 2000),
                        new CollectItemObjective("secret_documents", Material.WRITTEN_BOOK, 3),
                        new DeliverItemObjective("deliver_intel", "shadow_master", Material.WRITTEN_BOOK, 3),
                        
                        // 네 번째 시련 - 암살 임무
                        new VisitLocationObjective("target_location", "noble_mansion"),
                        new KillMobObjective("corrupt_noble", EntityType.VINDICATOR, 1),
                        new CollectItemObjective("noble_seal", Material.GOLD_NUGGET, 1),
                        new SurviveObjective("escape_guards", 180), // 3분간 경비 회피
                        
                        // 최종 시련 - 그림자와의 계약
                        new VisitLocationObjective("shadow_sanctum", "shadow_guild_sanctum"),
                        new CollectItemObjective("shadow_materials", Material.OBSIDIAN, 20),
                        new PlaceBlockObjective("shadow_altar", Material.OBSIDIAN, 9),
                        new CollectItemObjective("shadow_sacrifice", Material.ENDER_PEARL, 10), // 희생물
                        new KillMobObjective("shadow_guardian", EntityType.ENDERMAN, 1),
                        
                        // 전직 의식
                        // 전직 선택은 NPC 대화로 처리
                        new InteractNPCObjective("final_ceremony", "shadow_master"),
                        new CollectItemObjective("shadow_mark", Material.PLAYER_HEAD, 1) // 그림자의 표식
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 7500)
                        .addCurrency(CurrencyType.DIAMOND, 50)
                        .addItem(new ItemStack(Material.NETHERITE_SWORD)) // 그림자의 검
                        .addItem(new ItemStack(Material.LEATHER_CHESTPLATE)) // 은신 갑옷
                        .addItem(new ItemStack(Material.POTION, 10)) // 특수 포션
                        .addItem(new ItemStack(Material.ENDER_PEARL, 16))
                        .addExperience(7500)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SPECIAL)
                .minLevel(35)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return Component.translatable("quest.special.hidden_class.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return List.of() /* TODO: Convert LangManager.getList("quest.special.hidden_class.description") manually */;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return Component.translatable("quest.special.hidden_class.objectives.");
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("hidden_class_dialog");
        
        // Use string keys for dialog
        dialog.addLine("quest.special.hidden_class.npc.unknown",
                "quest.special.hidden_class.dialog.start.line1");

        dialog.addLine("quest.special.hidden_class.npc.unknown",
                "quest.special.hidden_class.dialog.start.line2");

        dialog.addLine("quest.special.hidden_class.npc.shadow_master",
                "quest.special.hidden_class.dialog.master.line1");

        dialog.addLine("quest.special.hidden_class.npc.shadow_master",
                "quest.special.hidden_class.dialog.master.line2");

        dialog.addLine("quest.special.hidden_class.npc.player",
                "quest.special.hidden_class.dialog.player.line1");

        dialog.addLine("quest.special.hidden_class.npc.shadow_master",
                "quest.special.hidden_class.dialog.master.line3");

        dialog.addLine("quest.special.hidden_class.npc.shadow_master",
                "quest.special.hidden_class.dialog.master.line4");

        dialog.addLine("quest.special.hidden_class.npc.shadow_master",
                "quest.special.hidden_class.dialog.master.line5");

        dialog.addLine("quest.special.hidden_class.npc.shadow_master",
                "quest.special.hidden_class.dialog.master.line6");

        dialog.addLine("quest.special.hidden_class.npc.shadow_master",
                "quest.special.hidden_class.dialog.master.line7");

        dialog.addLine("quest.special.hidden_class.npc.shadow_master",
                "quest.special.hidden_class.dialog.complete.line1");

        dialog.addLine("quest.special.hidden_class.npc.shadow_master",
                "quest.special.hidden_class.dialog.complete.line2");

        return dialog;
    }
}