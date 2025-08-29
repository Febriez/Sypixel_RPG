package com.febrie.rpg.quest.impl.main.chapter3;

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
 * 용의 계약 - 메인 스토리 퀘스트 (Chapter 3)
 * 고대 용과 영원한 계약을 맺는 퀘스트
 *
 * @author Febrie
 */
public class DragonPactQuest extends Quest {

    /**
     * 기본 생성자 - 퀘스트 설정
     */
    public DragonPactQuest() {
        super(createBuilder());
    }

    /**
     * 퀘스트 빌더 생성 및 설정
     */
    private static QuestBuilder createBuilder() {
        return new QuestBuilder()
                .id(QuestID.MAIN_DRAGON_PACT)
                .objectives(Arrays.asList(
                        // 계약 준비
                        new InteractNPCObjective("ancient_dragon", "ancient_dragon"),
                        new CollectItemObjective("dragon_blood", Material.REDSTONE_BLOCK, 10),
                        new CollectItemObjective("soul_crystals", Material.END_CRYSTAL, 5),
                        new CollectItemObjective("eternal_flame", Material.BLAZE_POWDER, 30),
                        
                        // 계약서 작성
                        new InteractNPCObjective("contract_scribe", "dragon_scribe"),
                        new CollectItemObjective("ancient_ink", Material.INK_SAC, 20),
                        new CollectItemObjective("magic_paper", Material.PAPER, 50),
                        new CollectItemObjective("binding_essence", Material.EXPERIENCE_BOTTLE, 30),
                        new DeliverItemObjective("deliver_materials", "dragon_scribe", Material.WRITTEN_BOOK, 1),
                        
                        // 영혼 결속 의식
                        new VisitLocationObjective("soul_altar", "dragon_soul_altar"),
                        new PlaceBlockObjective("place_soul_stones", Material.SOUL_SAND, 20),
                        new PlaceBlockObjective("place_soul_fire", Material.SOUL_LANTERN, 4),
                        new KillMobObjective("soul_guardians", EntityType.VEX, 30),
                        new CollectItemObjective("soul_fragments", Material.PHANTOM_MEMBRANE, 15),
                        
                        // 용의 축복 받기
                        new InteractNPCObjective("dragon_priest", "dragon_priest"),
                        new CollectItemObjective("blessing_tokens", Material.GOLD_NUGGET, 100),
                        new DeliverItemObjective("offer_tribute", "dragon_priest", Material.DIAMOND, 64),
                        new SurviveObjective("blessing_ritual", 300), // 5분 의식
                        
                        // 계약 서명
                        new InteractNPCObjective("ancient_dragon_signing", "ancient_dragon"),
                        new CollectItemObjective("dragon_sigil", Material.NETHER_STAR, 1),
                        new CollectItemObjective("player_blood", Material.REDSTONE, 10),
                        new DeliverItemObjective("sign_contract", "ancient_dragon", Material.WRITTEN_BOOK, 1),
                        
                        // 계약 완성
                        new PlaceBlockObjective("place_contract_altar", Material.ENCHANTING_TABLE, 1),
                        new CollectItemObjective("binding_chains", Material.CHAIN, 30),
                        new KillMobObjective("contract_witnesses", EntityType.EVOKER, 5),
                        new CollectItemObjective("witness_seals", Material.CLAY_BALL, 5),
                        
                        // 용과 하나되기
                        new VisitLocationObjective("dragon_heart_chamber", "dragon_heart_chamber"),
                        new CollectItemObjective("dragon_essence", Material.DRAGON_BREATH, 10),
                        new SurviveObjective("soul_merge", 600), // 10분 영혼 융합
                        new InteractNPCObjective("merged_dragon", "ancient_dragon"),
                        
                        // 계약 효력 발동
                        new CollectItemObjective("pact_medallion", Material.NETHERITE_INGOT, 1),
                        new CollectItemObjective("dragon_companion", Material.DRAGON_EGG, 1)
                ))
                .reward(new BasicReward.Builder()
                        .addCurrency(CurrencyType.GOLD, 30000)
                        .addCurrency(CurrencyType.DIAMOND, 250)
                        .addItem(new ItemStack(Material.DRAGON_EGG))
                        .addItem(new ItemStack(Material.ENCHANTED_GOLDEN_APPLE, 5))
                        .addItem(new ItemStack(Material.NETHERITE_HELMET))
                        .addItem(new ItemStack(Material.NETHERITE_BOOTS))
                        .addExperience(15000)
                        .build())
                .sequential(true)
                .repeatable(false)
                .category(QuestCategory.MAIN)
                .addPrerequisite(QuestID.MAIN_DRAGON_TRIALS)
                .minLevel(55)
                .maxLevel(0);
    }

    @Override
    public @NotNull Component getDisplayName(@NotNull Player who) {
        return LangManager.get("quest.main.dragon_pact.name", who);
    }

    @Override
    public @NotNull List<Component> getDisplayInfo(@NotNull Player who) {
        return LangManager.getList("quest.main.dragon_pact.info", who);
    }

    @Override
    public @NotNull Component getObjectiveDescription(@NotNull QuestObjective objective, @NotNull Player who) {
        String id = objective.getId();
        return LangManager.get("quest.main.dragon_pact.objectives." + id, who);
    }

    @Override
    public int getDialogCount() {
        return 5;
    }
    
    @Override
    public Component getDialog(int index, @NotNull Player who) {
        return switch (index) {
            case 0 -> LangManager.get("quest.main.dragon_pact.dialogs.0", who);
            case 1 -> LangManager.get("quest.main.dragon_pact.dialogs.1", who);
            case 2 -> LangManager.get("quest.main.dragon_pact.dialogs.2", who);
            case 3 -> LangManager.get("quest.main.dragon_pact.dialogs.3", who);
            case 4 -> LangManager.get("quest.main.dragon_pact.dialogs.4", who);
            default -> null;
        };
    }
    
    @Override
    public @NotNull Component getNPCName(@NotNull Player who) {
        return LangManager.get("quest.main.dragon_pact.npc_name", who);
    }

    @Override
    public @NotNull Component getAcceptDialog(@NotNull Player who) {
        return LangManager.get("quest.main.dragon_pact.accept", who);
    }
    
    @Override
    public @NotNull Component getDeclineDialog(@NotNull Player who) {
        return LangManager.get("quest.main.dragon_pact.decline", who);
    }
}