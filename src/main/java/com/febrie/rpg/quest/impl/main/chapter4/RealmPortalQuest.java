package com.febrie.rpg.quest.impl.main.chapter4;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.objective.impl.*;
import com.febrie.rpg.quest.reward.impl.BasicReward;

import com.febrie.rpg.util.LangKey;
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
 * 차원의 문 - 메인 스토리 퀘스트 (Chapter 4)
 * 다른 차원으로 통하는 포탈을 여는 퀘스트
 *
 * @author Febrie
 */
public class RealmPortalQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public RealmPortalQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_REALM_PORTAL)
                .objectives(Arrays.asList(
                        // 차원 연구
                        new InteractNPCObjective("dimension_scholar", "dimension_scholar"),
                        new CollectItemObjective("ancient_texts", Material.WRITTEN_BOOK, 10),
                        new VisitLocationObjective("observatory", "dimensional_observatory"),
                        new CollectItemObjective("star_charts", Material.MAP, 5),
                        
                        // 포탈 재료 수집
                        new CollectItemObjective("obsidian_blocks", Material.OBSIDIAN, 50),
                        new CollectItemObjective("ender_pearls", Material.ENDER_PEARL, 64),
                        new CollectItemObjective("blaze_powder", Material.BLAZE_POWDER, 32),
                        new CollectItemObjective("chorus_fruit", Material.CHORUS_FRUIT, 20),
                        new CollectItemObjective("end_crystals", Material.END_CRYSTAL, 8),
                        
                        // 차원 키 제작
                        new InteractNPCObjective("portal_smith", "portal_smith"),
                        new CollectItemObjective("void_essence", Material.ENDER_EYE, 16),
                        new CollectItemObjective("dimensional_shards", Material.PRISMARINE_SHARD, 64),
                        new DeliverItemObjective("forge_key", "portal_smith", Material.NETHER_STAR, 1),
                        
                        // 포탈 프레임 건설
                        new VisitLocationObjective("portal_site", "realm_portal_site"),
                        new PlaceBlockObjective("place_obsidian_frame", Material.OBSIDIAN, 30),
                        new PlaceBlockObjective("place_end_rods", Material.END_ROD, 8),
                        new PlaceBlockObjective("place_beacons", Material.BEACON, 4),
                        
                        // 차원 안정화
                        new KillMobObjective("void_creatures", EntityType.ENDERMAN, 50),
                        new KillMobObjective("dimensional_rifts", EntityType.SHULKER, 20),
                        new CollectItemObjective("stability_cores", Material.SHULKER_SHELL, 10),
                        new SurviveObjective("stabilization", 600), // 10분
                        
                        // 포탈 활성화
                        new InteractNPCObjective("portal_activator", "dimension_scholar"),
                        new CollectItemObjective("activation_catalyst", Material.DRAGON_BREATH, 5),
                        new PlaceBlockObjective("activate_portal", Material.END_PORTAL_FRAME, 12),
                        new CollectItemObjective("portal_key", Material.END_PORTAL, 1),
                        
                        // 첫 차원 여행
                        new VisitLocationObjective("enter_portal", "realm_portal_active"),
                        new VisitLocationObjective("void_dimension", "void_dimension_spawn"),
                        new KillMobObjective("void_guardians", EntityType.ELDER_GUARDIAN, 5),
                        new CollectItemObjective("dimension_proof", Material.HEART_OF_THE_SEA, 1),
                        
                        // 귀환
                        new VisitLocationObjective("return_portal", "realm_portal_return"),
                        new DeliverItemObjective("report_success", "dimension_scholar", Material.HEART_OF_THE_SEA, 1),
                        new CollectItemObjective("realm_navigator", Material.COMPASS, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 40000)
                        .addCurrency(CurrencyType.DIAMOND, 350)
                        .addItem(new ItemStack(Material.END_PORTAL_FRAME, 12))
                        .addItem(new ItemStack(Material.ENDER_CHEST, 3))
                        .addItem(new ItemStack(Material.SHULKER_BOX, 6))
                        .addItem(new ItemStack(Material.ELYTRA))
                        .addExperience(20000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_DRAGON_HEART)
                .minLevel(70)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_REALM_PORTAL_NAME, who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.list(LangKey.QUEST_MAIN_REALM_PORTAL_INFO, who);
    }

    @Override
    public @NotNull List<Component> getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return LangManager.get("quest.main.realm_portal.objectives." + id, who);
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public @NotNull Component getDialog(int index, @NotNull Player who) {
        return getDialogs(LangKey.QUEST_MAIN_REALM_PORTAL_DIALOGS, who).get(index);
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_REALM_PORTAL_NPC_NAME, who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_REALM_PORTAL_ACCEPT, who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.text(LangKey.QUEST_MAIN_REALM_PORTAL_DECLINE, who);
    }
}