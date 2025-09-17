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
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.phantom.haunting.name"), who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.phantom.haunting.info"), who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "talk_village_priest" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.phantom.haunting.objectives.talk.village.priest"), who);
            case "haunted_mansion" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.phantom.haunting.objectives.haunted.mansion"), who);
            case "ectoplasm_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.phantom.haunting.objectives.ectoplasm.collect"), who);
            case "soul_torch_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.phantom.haunting.objectives.soul.torch.collect"), who);
            case "family_crypt" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.phantom.haunting.objectives.family.crypt"), who);
            case "paper_collect" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.phantom.haunting.objectives.paper.collect"), who);
            case "restless_spirits" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.phantom.haunting.objectives.restless.spirits"), who);
            case "confront_lord_blackwood" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.phantom.haunting.objectives.confront.lord.blackwood"), who);
            case "perform_exorcism" -> LangManager.text(QuestCommonLangKey.fromString("quest.side.phantom.haunting.objectives.perform.exorcism"), who);
            default -> LangManager.text(QuestCommonLangKey.QUEST_UNKNOWN_OBJECTIVE, who, objective.getId());
        };
    }

    @Override
    public int getDialogCount() {
        return 7;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(QuestCommonLangKey.fromString("quest.side.phantom.haunting.dialogs"), who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.phantom.haunting.npc.name"), who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.phantom.haunting.accept"), who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(QuestCommonLangKey.fromString("quest.side.phantom.haunting.decline"), who);
    }
}