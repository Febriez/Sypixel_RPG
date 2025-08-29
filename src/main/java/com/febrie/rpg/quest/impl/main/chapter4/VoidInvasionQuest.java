package com.febrie.rpg.quest.impl.main.chapter4;

import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.builder.QuestBuilder;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.QuestCategory;
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
 * 공허의 침공 - 메인 스토리 퀘스트 (Chapter 4)
 * 공허 차원에서의 대규모 침공을 막는 퀘스트
 *
 * @author Febrie
 */
public class VoidInvasionQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public VoidInvasionQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_VOID_INVASION)
                .objectives(Arrays.asList(
                        // 침공 경고
                        new InteractNPCObjective("realm_guardian", "realm_guardian"),
                        new VisitLocationObjective("invasion_site", "void_invasion_site"),
                        new KillMobObjective("void_scouts", EntityType.ENDERMAN, 30),
                        new CollectItemObjective("invasion_intel", Material.WRITTEN_BOOK, 3),
                        
                        // 방어 준비
                        new InteractNPCObjective("defense_commander", "defense_commander"),
                        new CollectItemObjective("defense_supplies", Material.IRON_BLOCK, 50),
                        new PlaceBlockObjective("build_barricades", Material.IRON_BARS, 100),
                        new CollectItemObjective("void_weapons", Material.NETHERITE_SWORD, 5),
                        
                        // 첫 번째 파도
                        new KillMobObjective("void_soldiers", EntityType.ENDERMAN, 100),
                        new KillMobObjective("void_mages", EntityType.EVOKER, 30),
                        new SurviveObjective("first_wave", 600), // 10분
                        new CollectItemObjective("void_cores", Material.ENDER_PEARL, 50),
                        
                        // 두 번째 파도
                        new KillMobObjective("void_knights", EntityType.IRON_GOLEM, 40),
                        new KillMobObjective("void_beasts", EntityType.RAVAGER, 20),
                        new SurviveObjective("second_wave", 900), // 15분
                        new CollectItemObjective("void_essence", Material.ENDER_EYE, 30),
                        
                        // 보스 전투
                        new InteractNPCObjective("void_general", "void_general"),
                        new KillMobObjective("void_general_battle", EntityType.WITHER, 3),
                        new CollectItemObjective("general_crown", Material.WITHER_SKELETON_SKULL, 1),
                        
                        // 포탈 봉인
                        new VisitLocationObjective("void_portal", "void_portal_location"),
                        new PlaceBlockObjective("seal_portal", Material.BEDROCK, 20),
                        new CollectItemObjective("sealing_stone", Material.NETHER_STAR, 3),
                        new SurviveObjective("sealing_ritual", 300), // 5분
                        
                        // 승리
                        new InteractNPCObjective("realm_guardian_victory", "realm_guardian"),
                        new CollectItemObjective("victory_medal", Material.GOLDEN_APPLE, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 45000)
                        .addCurrency(CurrencyType.DIAMOND, 400)
                        .addItem(new ItemStack(Material.NETHERITE_HELMET))
                        .addItem(new ItemStack(Material.NETHERITE_LEGGINGS))
                        .addItem(new ItemStack(Material.TOTEM_OF_UNDYING, 5))
                        .addItem(new ItemStack(Material.NETHER_STAR, 3))
                        .addExperience(22000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_REALM_PORTAL)
                .minLevel(75)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.get("quest.main.void_invasion.name", who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.main.void_invasion.info", who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return LangManager.get("quest.main.void_invasion.objectives." + id, who);
    }

    @Override
    public int getDialogCount() {
        return 4;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.main.void_invasion.dialogs.0", who);
            case 1 -> LangManager.get("quest.main.void_invasion.dialogs.1", who);
            case 2 -> LangManager.get("quest.main.void_invasion.dialogs.2", who);
            case 3 -> LangManager.get("quest.main.void_invasion.dialogs.3", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.main.void_invasion.npc_name", who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.main.void_invasion.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.main.void_invasion.decline", who);
    }
}