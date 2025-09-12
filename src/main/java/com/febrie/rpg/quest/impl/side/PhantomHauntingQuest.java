package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.lang.quest.side.PhantomHauntingLangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import com.febrie.rpg.util.lang.quest.QuestCommonLangKey;

/**
 * Side Quest: Phantom Haunting
 * End the haunting of an old mansion and bring peace to lost spirits
 *
 * @author Febrie
 */
public class PhantomHauntingQuest extends Quest {

    /**
     * 기본 생성자
     */
    public PhantomHauntingQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_PHANTOM_HAUNTING)
                .objectives(List.of(
                        new InteractNPCObjective("talk_village_priest", "village_priest"),
                        new VisitLocationObjective("haunted_mansion", "Haunted_Mansion"),
                        new CollectItemObjective("ectoplasm_collect", Material.PHANTOM_MEMBRANE, 10),
                        new CollectItemObjective("soul_torch_collect", Material.SOUL_TORCH, 8),
                        new VisitLocationObjective("family_crypt", "Family_Crypt"),
                        new CollectItemObjective("paper_collect", Material.PAPER, 15),
                        new KillMobObjective("restless_spirits", EntityType.VEX, 12),
                        new InteractNPCObjective("confront_lord_blackwood", "lord_blackwood"),
                        new InteractNPCObjective("perform_exorcism", "village_priest")
                ))
                .reward(new BasicReward.Builder()
                        .addExperience(1900)
                        .addCurrency(CurrencyType.GOLD, 470)
                        .addItem(new ItemStack(Material.SOUL_LANTERN, 5))
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 2))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 1))
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(16);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(PhantomHauntingLangKey.QUEST_SIDE_PHANTOM_HAUNTING_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(PhantomHauntingLangKey.QUEST_SIDE_PHANTOM_HAUNTING_INFO, who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_village_priest" -> LangManager.text(PhantomHauntingLangKey.QUEST_SIDE_PHANTOM_HAUNTING_OBJECTIVES_TALK_VILLAGE_PRIEST, who);
            case "haunted_mansion" -> LangManager.text(PhantomHauntingLangKey.QUEST_SIDE_PHANTOM_HAUNTING_OBJECTIVES_HAUNTED_MANSION, who);
            case "ectoplasm_collect" -> LangManager.text(PhantomHauntingLangKey.QUEST_SIDE_PHANTOM_HAUNTING_OBJECTIVES_ECTOPLASM_COLLECT, who);
            case "soul_torch_collect" -> LangManager.text(PhantomHauntingLangKey.QUEST_SIDE_PHANTOM_HAUNTING_OBJECTIVES_SOUL_TORCH_COLLECT, who);
            case "family_crypt" -> LangManager.text(PhantomHauntingLangKey.QUEST_SIDE_PHANTOM_HAUNTING_OBJECTIVES_FAMILY_CRYPT, who);
            case "paper_collect" -> LangManager.text(PhantomHauntingLangKey.QUEST_SIDE_PHANTOM_HAUNTING_OBJECTIVES_PAPER_COLLECT, who);
            case "restless_spirits" -> LangManager.text(PhantomHauntingLangKey.QUEST_SIDE_PHANTOM_HAUNTING_OBJECTIVES_RESTLESS_SPIRITS, who);
            case "confront_lord_blackwood" -> LangManager.text(PhantomHauntingLangKey.QUEST_SIDE_PHANTOM_HAUNTING_OBJECTIVES_CONFRONT_LORD_BLACKWOOD, who);
            case "perform_exorcism" -> LangManager.text(PhantomHauntingLangKey.QUEST_SIDE_PHANTOM_HAUNTING_OBJECTIVES_PERFORM_EXORCISM, who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(PhantomHauntingLangKey.QUEST_SIDE_PHANTOM_HAUNTING_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(PhantomHauntingLangKey.QUEST_SIDE_PHANTOM_HAUNTING_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(PhantomHauntingLangKey.QUEST_SIDE_PHANTOM_HAUNTING_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(PhantomHauntingLangKey.QUEST_SIDE_PHANTOM_HAUNTING_DECLINE, who);
    }
}