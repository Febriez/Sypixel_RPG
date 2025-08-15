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
 * 고대 암호 - 미스터리/퍼즐 사이드 퀘스트
 * 고대 문명의 비밀을 풀어내는 탐정 스타일 퀘스트
 *
 * @author Febrie
 */
public class AncientCipherQuest extends Quest {

    /**
     * 퀘스트 빌더
     */
    private static class AncientCipherBuilder extends QuestBuilder {
        @Override
        public Quest build() {
            return new AncientCipherQuest(this);
        }
    }

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public AncientCipherQuest() {
        this(createBuilder());
    }

    /**
     * 빌더 생성자
     */
    private AncientCipherQuest(@NotNull QuestBuilder builder) {
        super(builder);
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new AncientCipherBuilder()
                .id(QuestID.SIDE_ANCIENT_CIPHER)
                .objectives(Arrays.asList(
                        // 미스터리 시작
                        new InteractNPCObjective("archaeologist", "cipher_archaeologist"), // 고고학자
                        new CollectItemObjective("ancient_tablet", Material.CHISELED_STONE_BRICKS, 1),
                        new VisitLocationObjective("research_lab", "archaeology_lab"),
                        
                        // 첫 번째 단서 - 도서관
                        new VisitLocationObjective("ancient_library", "forbidden_library"),
                        new InteractNPCObjective("librarian", "ancient_librarian"), // 사서
                        new CollectItemObjective("old_books", Material.WRITTEN_BOOK, 5),
                        new BreakBlockObjective("search_shelves", Material.BOOKSHELF, 20),
                        new CollectItemObjective("cipher_key_1", Material.PAPER, 1),
                        
                        // 두 번째 단서 - 천문대
                        new VisitLocationObjective("observatory", "ancient_observatory"),
                        new PlaceBlockObjective("align_telescopes", Material.SPYGLASS, 3),
                        new SurviveObjective("night_observation", 600), // 10분간 밤 관측
                        new CollectItemObjective("star_map", Material.MAP, 1),
                        new KillMobObjective("phantom_guards", EntityType.PHANTOM, 20),
                        new CollectItemObjective("cipher_key_2", Material.COMPASS, 1),
                        
                        // 세 번째 단서 - 지하 묘지
                        new VisitLocationObjective("catacombs", "underground_crypt"),
                        new KillMobObjective("crypt_keepers", EntityType.HUSK, 30),
                        new KillMobObjective("ancient_guardians", EntityType.WITHER_SKELETON, 15),
                        new BreakBlockObjective("open_tombs", Material.STONE_BRICKS, 50),
                        new CollectItemObjective("burial_relics", Material.GOLD_NUGGET, 20),
                        new CollectItemObjective("cipher_key_3", Material.CLOCK, 1),
                        
                        // 네 번째 단서 - 연금술사의 탑
                        new VisitLocationObjective("alchemist_tower", "abandoned_alchemy_tower"),
                        new InteractNPCObjective("ghost_alchemist", "ghost_alchemist"), // 유령 연금술사
                        new CollectItemObjective("rare_ingredients", Material.GLISTERING_MELON_SLICE, 5),
                        new CollectItemObjective("mystic_dust", Material.GLOWSTONE_DUST, 20),
                        new CraftItemObjective("brew_potion", Material.POTION, 10),
                        new CollectItemObjective("cipher_key_4", Material.BREWING_STAND, 1),
                        
                        // 암호 해독
                        new DeliverItemObjective("deliver_key1", "archaeologist", Material.PAPER, 1),
                        new DeliverItemObjective("deliver_key2", "archaeologist", Material.COMPASS, 1),
                        new DeliverItemObjective("deliver_key3", "archaeologist", Material.CLOCK, 1),
                        new DeliverItemObjective("deliver_key4", "archaeologist", Material.BREWING_STAND, 1),
                        new InteractNPCObjective("decode_cipher", "cipher_archaeologist"),
                        
                        // 숨겨진 방 발견
                        new VisitLocationObjective("hidden_chamber", "secret_ancient_vault"),
                        new PlaceBlockObjective("insert_keys", Material.LEVER, 4),
                        new SurviveObjective("puzzle_room", 300), // 5분간 퍼즐 룸
                        
                        // 보물 방 진입
                        new BreakBlockObjective("break_seal", Material.OBSIDIAN, 20),
                        new KillMobObjective("treasure_guardian", EntityType.ELDER_GUARDIAN, 1),
                        new CollectItemObjective("ancient_artifact", Material.HEART_OF_THE_SEA, 1),
                        new CollectItemObjective("wisdom_scrolls", Material.ENCHANTED_BOOK, 5),
                        
                        // 탈출
                        new KillMobObjective("awakened_mummies", EntityType.ZOMBIE_VILLAGER, 40),
                        new SurviveObjective("escape_trap", 600), // 10분간 함정 탈출
                        new VisitLocationObjective("escape_route", "archaeology_lab"),
                        
                        // 완료
                        new DeliverItemObjective("deliver_artifact", "archaeologist", Material.HEART_OF_THE_SEA, 1),
                        new InteractNPCObjective("quest_complete", "cipher_archaeologist")
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 8000)
                        .addCurrency(CurrencyType.DIAMOND, 50)
                        .addItem(new ItemStack(Material.ENCHANTED_BOOK, 5))
                        .addItem(new ItemStack(Material.SPYGLASS))
                        .addItem(new ItemStack(Material.COMPASS))
                        .addItem(new ItemStack(Material.CLOCK))
                        .addItem(new ItemStack(Material.MAP))
                        .addExperience(4000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.SIDE)
                .minLevel(25)
                .maxLevel(0)
                .addPrerequisite(QuestID.TUTORIAL_BASIC_COMBAT);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.side.ancient_cipher.name");
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return com.febrie.rpg.util.LangManager.getComponentList(who, "quest.side.ancient_cipher.description");
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return com.febrie.rpg.util.LangManager.getMessage(who, "quest.side.ancient_cipher.objectives." + id);
    }

    @Override
    public QuestDialog getDialog(@NotNull Player player) {
        QuestDialog dialog = new QuestDialog("ancient_cipher_dialog");

        dialog.addLine("quest.ancient_cipher.npcs.archaeologist", "quest.ancient_cipher.dialogs.line1");
        dialog.addLine("quest.ancient_cipher.npcs.archaeologist", "quest.ancient_cipher.dialogs.line2");
        dialog.addLine("quest.dialog.player", "quest.ancient_cipher.dialogs.player_line1");
        dialog.addLine("quest.ancient_cipher.npcs.archaeologist", "quest.ancient_cipher.dialogs.line3");
        dialog.addLine("quest.ancient_cipher.npcs.librarian", "quest.ancient_cipher.dialogs.librarian_line1");
        dialog.addLine("quest.ancient_cipher.npcs.librarian", "quest.ancient_cipher.dialogs.librarian_line2");
        dialog.addLine("quest.ancient_cipher.npcs.ghost_alchemist", "quest.ancient_cipher.dialogs.ghost_alchemist_line1");
        dialog.addLine("quest.ancient_cipher.npcs.ghost_alchemist", "quest.ancient_cipher.dialogs.ghost_alchemist_line2");
        dialog.addLine("quest.ancient_cipher.npcs.archaeologist", "quest.ancient_cipher.dialogs.line4");
        dialog.addLine("quest.ancient_cipher.npcs.archaeologist", "quest.ancient_cipher.dialogs.line5");
        dialog.addLine("quest.dialog.player", "quest.ancient_cipher.dialogs.player_line2");
        dialog.addLine("quest.ancient_cipher.npcs.archaeologist", "quest.ancient_cipher.dialogs.line6");
        dialog.addLine("quest.ancient_cipher.npcs.archaeologist", "quest.ancient_cipher.dialogs.line7");
        dialog.addLine("quest.ancient_cipher.npcs.archaeologist", "quest.ancient_cipher.dialogs.line8");

        return dialog;
    }
}