package com.febrie.rpg.quest.impl.guild;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.InteractNPCObjective;
import com.febrie.rpg.quest.objective.impl.CollectItemObjective;
import com.febrie.rpg.quest.objective.impl.PlaceBlockObjective;
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
 * 길드 설립 - 길드 퀘스트
 * 새로운 길드를 만드는 퀘스트
 *
 * @author Febrie
 */
public class GuildEstablishmentQuest extends Quest {

    /**
     * 기본 생성자
     */
    public GuildEstablishmentQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.GUILD_ESTABLISHMENT)
                .objectives(List.of(
                        new CollectItemObjective("collect_foundation_materials", Material.STONE_BRICKS, 1000),
                        new CollectItemObjective("collect_gold_for_charter", Material.GOLD_INGOT, 100),
                        new InteractNPCObjective("create_guild", "guild_registrar", 1),
                        new InteractNPCObjective("recruit_initial_members", "guild_members", 10),
                        new PlaceBlockObjective("build_guild_hall", Material.STONE_BRICKS, 500)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 2000)
                        .addItem(new ItemStack(Material.BANNER, 5))
                        .addItem(new ItemStack(Material.LECTERN, 3))
                        .addExperience(2000)
                        .build())
                .sequential(true)
                .category(QuestCategory.GUILD)
                .minLevel(25);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_GUILD_ESTABLISHMENT_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_GUILD_ESTABLISHMENT_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        return switch (objective.getId()) {
            case "collect_foundation_materials" -> LangManager.list(LangKey.QUEST_GUILD_ESTABLISHMENT_OBJECTIVES_COLLECT_FOUNDATION_MATERIALS, who);
            case "collect_gold_for_charter" -> LangManager.list(LangKey.QUEST_GUILD_ESTABLISHMENT_OBJECTIVES_COLLECT_GOLD_FOR_CHARTER, who);
            case "create_guild" -> LangManager.list(LangKey.QUEST_GUILD_ESTABLISHMENT_OBJECTIVES_CREATE_GUILD, who);
            case "recruit_initial_members" -> LangManager.list(LangKey.QUEST_GUILD_ESTABLISHMENT_OBJECTIVES_RECRUIT_INITIAL_MEMBERS, who);
            case "build_guild_hall" -> LangManager.list(LangKey.QUEST_GUILD_ESTABLISHMENT_OBJECTIVES_BUILD_GUILD_HALL, who);
            default -> new ArrayList<>();
        };
    }
    
    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public @NotNull List<Component> getDialogs(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_GUILD_ESTABLISHMENT_DIALOGS, who);
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_GUILD_ESTABLISHMENT_NPC_NAME, who);
    }
    
    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_GUILD_ESTABLISHMENT_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_GUILD_ESTABLISHMENT_DECLINE, who);
    }
}