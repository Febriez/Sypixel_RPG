package com.febrie.rpg.quest.impl.repeatable;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.CraftItemObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 장비 업그레이드 - 반복 퀘스트
 * 장비를 수리하고 인챈트하는 퀘스트
 *
 * @author Febrie
 */
public class EquipmentUpgradeQuest extends Quest {

    /**
     * 기본 생성자
     */
    public EquipmentUpgradeQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.REPEATABLE_EQUIPMENT_UPGRADE)
                .objectives(List.of(
                        new CollectItemObjective("collect_iron", Material.IRON_INGOT, 32),
                        new CraftItemObjective("craft_tools", Material.IRON_PICKAXE, 3),
                        new InteractNPCObjective("repair_items", "blacksmith", 5),
                        new InteractNPCObjective("enchant_items", "enchanter", 3)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 150)
                        .addItem(new ItemStack(Material.EXPERIENCE_BOTTLE, 10))
                        .addItem(new ItemStack(Material.ANVIL, 1))
                        .addExperience(200)
                        .build())
                .sequential(false)
                .repeatable(true)
                .category(QuestCategory.REPEATABLE)
                .minLevel(10);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_REPEATABLE_EQUIPMENT_UPGRADE_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_REPEATABLE_EQUIPMENT_UPGRADE_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "collect_iron" -> LangManager.list(LangKey.QUEST_REPEATABLE_EQUIPMENT_UPGRADE_OBJECTIVES_COLLECT_IRON, who);
            case "craft_tools" -> LangManager.list(LangKey.QUEST_REPEATABLE_EQUIPMENT_UPGRADE_OBJECTIVES_CRAFT_TOOLS, who);
            case "repair_items" -> LangManager.list(LangKey.QUEST_REPEATABLE_EQUIPMENT_UPGRADE_OBJECTIVES_REPAIR_ITEMS, who);
            case "enchant_items" -> LangManager.list(LangKey.QUEST_REPEATABLE_EQUIPMENT_UPGRADE_OBJECTIVES_ENCHANT_ITEMS, who);
            default -> new ArrayList<>();
        };
    }
    
    @Override
    public int getDialogCount() {
        return 3;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_REPEATABLE_EQUIPMENT_UPGRADE_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_REPEATABLE_EQUIPMENT_UPGRADE_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_REPEATABLE_EQUIPMENT_UPGRADE_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_REPEATABLE_EQUIPMENT_UPGRADE_DECLINE, who);
    }
}