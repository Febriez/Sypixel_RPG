package com.febrie.rpg.quest.impl.crafting;

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
 * 대장장이 마스터 - 제작 퀘스트
 * 대장장이의 길을 걷는 장인이 되는 퀘스트
 *
 * @author Febrie
 */
public class MasterBlacksmithQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class MasterBlacksmithBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new MasterBlacksmithQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public MasterBlacksmithQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private MasterBlacksmithQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new MasterBlacksmithBuilder()
                .id(QuestID.CRAFT_MASTER_BLACKSMITH)
                .objectives(Arrays.asList(
                        // 시작
                        new InteractNPCObjective("blacksmith_master", "master_blacksmith"), // 대장장이 마스터
                        
                        // 재료 수집
                        new BreakBlockObjective("mine_iron", Material.IRON_ORE, 30),
                        new BreakBlockObjective("mine_gold", Material.GOLD_ORE, 20),
                        new BreakBlockObjective("mine_diamond", Material.DIAMOND_ORE, 10),
                        new CollectItemObjective("gather_coal", Material.COAL, 64),
                        
                        // 제련
                        new CollectItemObjective("smelt_iron", Material.IRON_INGOT, 30),
                        new CollectItemObjective("smelt_gold", Material.GOLD_INGOT, 20),
                        new CollectItemObjective("gather_diamonds", Material.DIAMOND, 10),
                        
                        // 기초 제작
                        new PlaceBlockObjective("setup_anvil", Material.ANVIL, 1),
                        new PlaceBlockObjective("setup_furnace", Material.BLAST_FURNACE, 1),
                        new CraftItemObjective("craft_iron_tools", Material.IRON_PICKAXE, 5),
                        new CraftItemObjective("craft_iron_armor", Material.IRON_CHESTPLATE, 3),
                        
                        // 중급 제작
                        new CraftItemObjective("craft_diamond_sword", Material.DIAMOND_SWORD, 2),
                        new CraftItemObjective("craft_diamond_armor", Material.DIAMOND_CHESTPLATE, 1),
                        
                        // 고급 제작 - 인챈트
                        new PlaceBlockObjective("setup_enchanting", Material.ENCHANTING_TABLE, 1),
                        new CollectItemObjective("enchanted_sword", Material.DIAMOND_SWORD, 1), // 인챈트된 검
                        
                        // 최종 작품
                        new CollectItemObjective("netherite_scrap", Material.NETHERITE_SCRAP, 4),
                        new CraftItemObjective("craft_netherite", Material.NETHERITE_INGOT, 1),
                        new CraftItemObjective("masterpiece", Material.NETHERITE_SWORD, 1),
                        
                        // 전달
                        new DeliverItemObjective("deliver_masterpiece", "blacksmith_master", Material.NETHERITE_SWORD, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 4000)
                        .addCurrency(CurrencyType.DIAMOND, 35)
                        .addItem(new ItemStack(Material.NETHERITE_INGOT, 2))
                        .addItem(new ItemStack(Material.SMITHING_TABLE))
                        .addItem(new ItemStack(Material.ANVIL))
                        .addExperience(2500)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.CRAFTING)
                .minLevel(20)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.crafting.master_blacksmith.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getList(who, "quest.crafting.master_blacksmith.description");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.crafting.master_blacksmith.objectives." + id);
    }

    public QuestDialog getDialog() {
        QuestDialog dialog = new QuestDialog("master_blacksmith_dialog");

        dialog.addLine("quest.crafting.master_blacksmith.dialog.npc1",
                "quest.crafting.master_blacksmith.dialog.npc1");

        dialog.addLine("quest.crafting.master_blacksmith.dialog.npc2",
                "quest.crafting.master_blacksmith.dialog.npc2");

        dialog.addLine("quest.crafting.master_blacksmith.dialog.player1",
                "quest.crafting.master_blacksmith.dialog.player1");

        dialog.addLine("quest.crafting.master_blacksmith.dialog.npc3",
                "quest.crafting.master_blacksmith.dialog.npc3");

        dialog.addLine("quest.crafting.master_blacksmith.dialog.npc4",
                "quest.crafting.master_blacksmith.dialog.npc4");

        dialog.addLine("quest.crafting.master_blacksmith.dialog.player2",
                "quest.crafting.master_blacksmith.dialog.player2");

        dialog.addLine("quest.crafting.master_blacksmith.dialog.npc5",
                "quest.crafting.master_blacksmith.dialog.npc5");

        return dialog;
    }
}