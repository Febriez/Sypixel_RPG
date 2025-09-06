package com.febrie.rpg.quest.impl.side;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.VisitLocationObjective;
import com.febrie.rpg.quest.objective.impl.KillMobObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.reward.impl.BasicReward;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;

import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

/**
 * 도적 길드 - 사이드 퀘스트
 * 경쟁 조직에 침투하여 중요한 정보를 회수하는 퀘스트
 *
 * @author Febrie
 */
public class ThievesGuildQuest extends Quest {

    /**
     * 기본 생성자
     */
    public ThievesGuildQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.SIDE_THIEVES_GUILD)
                .objectives(Arrays.asList(
                        new InteractNPCObjective("guild_contact", "guild_contact"),
                        new VisitLocationObjective("secret_entrance", "secret_entrance"),
                        new CollectItemObjective("lockpicks", Material.TRIPWIRE_HOOK, 5),
                        new VisitLocationObjective("rival_hideout", "rival_hideout"),
                        new KillMobObjective("kill_vindicators", EntityType.VINDICATOR, 8),
                        new CollectItemObjective("stolen_ledger", Material.BOOK, 1),
                        new InteractNPCObjective("guild_master", "guild_master")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 800)
                        .addItem(new ItemStack(Material.DIAMOND_SWORD))
                        .addItem(new ItemStack(Material.LEATHER_BOOTS))
                        .addExperience(2500)
                        .build())
                .sequential(true)
                .category(QuestCategory.SIDE)
                .minLevel(18);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_THIEVES_GUILD_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_SIDE_THIEVES_GUILD_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return LangManager.get("quest.side.thieves_guild.objectives." + objective.getId(), who);
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(LangKey.QUEST_SIDE_THIEVES_GUILD_DIALOGS, who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_THIEVES_GUILD_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_THIEVES_GUILD_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_SIDE_THIEVES_GUILD_DECLINE, who);
    }
}