package com.febrie.rpg.quest.impl.daily;

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
 * 일일 탐험 - 일일 퀘스트
 * 매일 새로운 지역을 탐험하고 발견하는 모험 퀘스트
 *
 * @author Febrie
 */
public class DailyExplorationQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class DailyExplorationBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new DailyExplorationQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public DailyExplorationQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private DailyExplorationQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new DailyExplorationBuilder()
                .id(QuestID.DAILY_EXPLORATION)
                .objectives(Arrays.asList(
                        // 탐험 시작
                        new InteractNPCObjective("explorer_guild", "explorer_guild_master"), // 탐험가 길드장
                        new CollectItemObjective("prepare_supplies", Material.BREAD, 10),
                        new CollectItemObjective("prepare_torches", Material.TORCH, 32),
                        new CollectItemObjective("prepare_tools", Material.IRON_PICKAXE, 1),
                        
                        // 첫 번째 지역 - 버려진 광산
                        new VisitLocationObjective("abandoned_mine", "old_mineshaft_entrance"),
                        new PlaceBlockObjective("light_mine", Material.TORCH, 10),
                        new BreakBlockObjective("mine_ores", Material.IRON_ORE, 20),
                        new CollectItemObjective("find_relics", Material.GOLD_NUGGET, 15),
                        new KillMobObjective("mine_creatures", EntityType.CAVE_SPIDER, 20),
                        
                        // 두 번째 지역 - 숨겨진 폭포
                        new VisitLocationObjective("hidden_waterfall", "secret_waterfall"),
                        new CollectItemObjective("waterfall_treasure", Material.PRISMARINE_SHARD, 10),
                        new FishingObjective("waterfall_fishing", 10),
                        new CollectItemObjective("rare_fish", Material.TROPICAL_FISH, 5),
                        new KillMobObjective("water_guardians", EntityType.DROWNED, 15),
                        
                        // 세 번째 지역 - 고대 유적
                        new VisitLocationObjective("ancient_ruins", "forgotten_temple"),
                        new BreakBlockObjective("clear_rubble", Material.COBBLESTONE, 30),
                        new CollectItemObjective("ancient_pottery", Material.FLOWER_POT, 5),
                        new CollectItemObjective("temple_treasure", Material.EMERALD, 10),
                        new KillMobObjective("ruin_guardians", EntityType.SKELETON, 25),
                        
                        // 네 번째 지역 - 신비한 숲
                        new VisitLocationObjective("mystic_forest", "enchanted_grove"),
                        new HarvestObjective("gather_herbs", Material.WHEAT, 20),
                        new CollectItemObjective("mystic_flowers", Material.AZURE_BLUET, 10),
                        new CollectItemObjective("magic_mushrooms", Material.RED_MUSHROOM, 15),
                        new KillMobObjective("forest_spirits", EntityType.ZOMBIE, 20),
                        
                        // 다섯 번째 지역 - 용암 동굴
                        new VisitLocationObjective("lava_cavern", "volcanic_cave"),
                        new PlaceBlockObjective("build_bridge", Material.COBBLESTONE, 20),
                        new CollectItemObjective("obsidian_shards", Material.OBSIDIAN, 10),
                        new CollectItemObjective("magma_cream", Material.MAGMA_CREAM, 5),
                        new KillMobObjective("lava_creatures", EntityType.MAGMA_CUBE, 15),
                        new SurviveObjective("heat_survival", 300), // 5분간 열기 견디기
                        
                        // 지도 작성
                        new CraftItemObjective("create_maps", Material.MAP, 5),
                        new CollectItemObjective("mark_locations", Material.FILLED_MAP, 5),
                        
                        // 보고서 작성
                        new CollectItemObjective("write_report", Material.WRITTEN_BOOK, 1),
                        new DeliverItemObjective("deliver_relics", "explorer_guild", Material.GOLD_NUGGET, 15),
                        new DeliverItemObjective("deliver_maps", "explorer_guild", Material.FILLED_MAP, 5),
                        new DeliverItemObjective("deliver_report", "explorer_guild", Material.WRITTEN_BOOK, 1),
                        new InteractNPCObjective("exploration_complete", "explorer_guild_master")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 3000)
                        .addCurrency(CurrencyType.DIAMOND, 20)
                        .addItem(new ItemStack(Material.COMPASS))
                        .addItem(new ItemStack(Material.MAP, 5))
                        .addItem(new ItemStack(Material.SPYGLASS))
                        .addItem(new ItemStack(Material.LEATHER_BOOTS)) // 탐험가 부츠
                        .addItem(new ItemStack(Material.ENDER_PEARL, 3))
                        .addExperience(2000)
                        .build())
                .sequential(false)  // 자유로운 탐험
                .repeatable(true)
                .daily(true)       // 일일 퀘스트
                .category(QuestCategory.DAILY)
                .minLevel(15)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.daily.exploration.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getList(who, "quest.daily.exploration.description");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String key = "quest.daily.exploration.objectives." + objective.getId();
        return com.febrie.rpg.util.LangManager.getMessage(who, key);
    }

    @Override
    public QuestDialog getDialog(@NotNull Player player) {
        QuestDialog dialog = new QuestDialog("daily_exploration_dialog");
        
        // 시작 대화
        dialog.addLine("quest.daily.exploration.npcs.guild_master", "quest.daily.exploration.dialogs.start1");
        dialog.addLine("quest.daily.exploration.npcs.guild_master", "quest.daily.exploration.dialogs.start2");
        dialog.addLine("quest.dialog.player", "quest.daily.exploration.dialogs.player_question");
        dialog.addLine("quest.daily.exploration.npcs.guild_master", "quest.daily.exploration.dialogs.regions");
        
        // 탐험 중 대화
        dialog.addLine("quest.daily.exploration.npcs.guild_master", "quest.daily.exploration.dialogs.supplies");
        dialog.addLine("quest.dialog.player", "quest.daily.exploration.dialogs.discovery");
        dialog.addLine("quest.daily.exploration.npcs.guild_master", "quest.daily.exploration.dialogs.record");
        
        // 완료 대화
        dialog.addLine("quest.daily.exploration.npcs.guild_master", "quest.daily.exploration.dialogs.complete1");
        dialog.addLine("quest.daily.exploration.npcs.guild_master", "quest.daily.exploration.dialogs.complete2");
        dialog.addLine("quest.daily.exploration.npcs.guild_master", "quest.daily.exploration.dialogs.complete3");
        
        return dialog;
    }
}