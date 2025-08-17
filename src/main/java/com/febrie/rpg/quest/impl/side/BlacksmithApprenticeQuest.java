package com.febrie.rpg.quest.impl.side;

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
 * 대장장이의 제자 - 사이드 퀘스트
 * 마을 대장장이의 제자가 되어 기술을 배우는 퀘스트
 *
 * @author Febrie
 */
public class BlacksmithApprenticeQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class BlacksmithApprenticeBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new BlacksmithApprenticeQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public BlacksmithApprenticeQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private BlacksmithApprenticeQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new BlacksmithApprenticeBuilder()
                .id(QuestID.SIDE_BLACKSMITH_APPRENTICE)
                .objectives(Arrays.asList(
                        // 대장장이 만나기
                        new InteractNPCObjective("meet_blacksmith", "village_blacksmith"), // 마을 대장장이
                        
                        // 기초 재료 수집
                        new CollectItemObjective("gather_coal", Material.COAL, 32),
                        new BreakBlockObjective("mine_iron", Material.IRON_ORE, 15),
                        new CollectItemObjective("gather_iron", Material.IRON_INGOT, 15),
                        
                        // 작업장 준비
                        new PlaceBlockObjective("setup_anvil", Material.ANVIL, 1),
                        new PlaceBlockObjective("setup_furnace", Material.FURNACE, 2),
                        new CollectItemObjective("gather_water", Material.WATER_BUCKET, 2),
                        
                        // 첫 번째 작품 - 도구
                        new CraftItemObjective("craft_pickaxe", Material.IRON_PICKAXE, 1),
                        new CraftItemObjective("craft_shovel", Material.IRON_SHOVEL, 1),
                        new CraftItemObjective("craft_axe", Material.IRON_AXE, 1),
                        new DeliverItemObjective("deliver_tools", "blacksmith", Material.IRON_PICKAXE, 1),
                        
                        // 고급 재료 수집
                        new KillMobObjective("hunt_skeletons", EntityType.SKELETON, 10),
                        new CollectItemObjective("gather_bones", Material.BONE, 20),
                        new CollectItemObjective("gather_string", Material.STRING, 10),
                        
                        // 두 번째 작품 - 무기
                        new CraftItemObjective("craft_sword", Material.IRON_SWORD, 2),
                        new CraftItemObjective("craft_bow", Material.BOW, 1),
                        new CraftItemObjective("craft_arrows", Material.ARROW, 64),
                        
                        // 품질 테스트
                        new KillMobObjective("test_weapons", EntityType.ZOMBIE, 15),
                        new InteractNPCObjective("report_test", "village_blacksmith"),
                        
                        // 최종 시험 - 특별 주문
                        new CollectItemObjective("special_material", Material.DIAMOND, 3),
                        new CraftItemObjective("craft_special", Material.DIAMOND_SWORD, 1),
                        new DeliverItemObjective("deliver_special", "knight_captain", Material.DIAMOND_SWORD, 1), // 기사단장
                        
                        // 졸업
                        new InteractNPCObjective("graduation", "village_blacksmith")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 2500)
                        .addCurrency(CurrencyType.DIAMOND, 20)
                        .addItem(new ItemStack(Material.SMITHING_TABLE))
                        .addItem(new ItemStack(Material.IRON_CHESTPLATE))
                        .addItem(new ItemStack(Material.WRITTEN_BOOK)) // 대장장이 기술서
                        .addExperience(1500)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SIDE)
                .minLevel(10)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.side.blacksmith_apprentice.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getList(who, "quest.side.blacksmith_apprentice.description");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.side.blacksmith_apprentice.objectives." + id);
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("blacksmith_apprentice_dialog");

        dialog.addLine("quest.blacksmith_apprentice.npcs.blacksmith", "quest.blacksmith_apprentice.dialogs.line1");
        dialog.addLine("quest.blacksmith_apprentice.npcs.blacksmith", "quest.blacksmith_apprentice.dialogs.line2");
        dialog.addLine("quest.dialog.player", "quest.blacksmith_apprentice.dialogs.player_line1");
        dialog.addLine("quest.blacksmith_apprentice.npcs.blacksmith", "quest.blacksmith_apprentice.dialogs.line3");
        dialog.addLine("quest.blacksmith_apprentice.npcs.blacksmith", "quest.blacksmith_apprentice.dialogs.line4");
        dialog.addLine("quest.blacksmith_apprentice.npcs.blacksmith", "quest.blacksmith_apprentice.dialogs.line5");
        dialog.addLine("quest.blacksmith_apprentice.npcs.blacksmith", "quest.blacksmith_apprentice.dialogs.line6");
        dialog.addLine("quest.blacksmith_apprentice.npcs.blacksmith", "quest.blacksmith_apprentice.dialogs.line7");
        dialog.addLine("quest.blacksmith_apprentice.npcs.blacksmith", "quest.blacksmith_apprentice.dialogs.line8");
        dialog.addLine("quest.blacksmith_apprentice.npcs.blacksmith", "quest.blacksmith_apprentice.dialogs.line9");

        return dialog;
    }
}