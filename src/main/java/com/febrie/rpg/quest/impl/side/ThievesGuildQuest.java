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
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
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
     * 퀘스트 빌더
     */
    private static class ThievesGuildBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new ThievesGuildQuest(this);
        }
    }

    /**
     * 기본 생성자
     */
    public ThievesGuildQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private ThievesGuildQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new ThievesGuildBuilder()
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
        return Component.translatable("quest.side.thieves-guild.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        List<Component> description = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Component line = Component.translatable("quest.side.thieves-guild.description." + i);
            description.add(line);
        }
        return description;
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();

        return switch (id) {
            case "guild_contact" -> Component.translatable("quest.side.thieves-guild.objectives.guild_contact");
            case "secret_entrance" -> Component.translatable("quest.side.thieves-guild.objectives.secret_entrance");
            case "lockpicks" -> Component.translatable("quest.side.thieves-guild.objectives.lockpicks");
            case "rival_hideout" -> Component.translatable("quest.side.thieves-guild.objectives.rival_hideout");
            case "kill_vindicators" -> Component.translatable("quest.side.thieves-guild.objectives.kill_vindicators");
            case "stolen_ledger" -> Component.translatable("quest.side.thieves-guild.objectives.stolen_ledger");
            case "guild_master" -> Component.translatable("quest.side.thieves-guild.objectives.guild_master");
            default -> Component.translatable("quest.side.thieves-guild.objectives." + id);
        };
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> Component.translatable("quest.side.thieves-guild.dialogs.0");
            case 1 -> Component.translatable("quest.side.thieves-guild.dialogs.1");
            case 2 -> Component.translatable("quest.side.thieves-guild.dialogs.2");
            case 3 -> Component.translatable("quest.side.thieves-guild.dialogs.3");
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return Component.translatable("quest.side.thieves-guild.npc-name");
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return Component.translatable("quest.side.thieves-guild.accept");
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return Component.translatable("quest.side.thieves-guild.decline");
    }
}