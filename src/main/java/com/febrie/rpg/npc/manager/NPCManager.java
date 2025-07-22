package com.febrie.rpg.npc.manager;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.npc.trait.RPGGuideTrait;
import com.febrie.rpg.npc.trait.RPGQuestTrait;
import com.febrie.rpg.npc.trait.RPGQuestRewardTrait;
import com.febrie.rpg.npc.trait.RPGShopTrait;
import com.febrie.rpg.util.LogUtil;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * RPG NPC 관리자
 * Citizens의 Trait 시스템을 통한 NPC 영구 저장 및 복구 관리
 *
 * @author Febrie
 */
public class NPCManager {

    private final RPGMain plugin;
    private final NPCRegistry npcRegistry;
    private boolean traitsRegistered = false;

    public NPCManager(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        this.npcRegistry = CitizensAPI.getNPCRegistry();
        
        // Citizens가 depend로 설정되어 있으므로 즉시 등록
        registerTraits();
    }

    /**
     * 커스텀 Trait들을 Citizens에 등록
     */
    private void registerTraits() {
        if (traitsRegistered) {
            return;
        }

        try {
            // Citizens가 활성화되었는지 확인
            if (!Bukkit.getPluginManager().isPluginEnabled("Citizens")) {
                LogUtil.warn("Citizens 플러그인이 비활성화되어 있습니다. NPC 기능을 사용할 수 없습니다.");
                return;
            }

            // 커스텀 Trait 등록
            CitizensAPI.getTraitFactory().registerTrait(
                net.citizensnpcs.api.trait.TraitInfo.create(RPGQuestTrait.class)
                    .withName("rpgquest")
            );
            
            CitizensAPI.getTraitFactory().registerTrait(
                net.citizensnpcs.api.trait.TraitInfo.create(RPGQuestRewardTrait.class)
                    .withName("rpgquestreward")
            );
            
            CitizensAPI.getTraitFactory().registerTrait(
                net.citizensnpcs.api.trait.TraitInfo.create(RPGShopTrait.class)
                    .withName("rpgshop")
            );
            
            CitizensAPI.getTraitFactory().registerTrait(
                net.citizensnpcs.api.trait.TraitInfo.create(RPGGuideTrait.class)
                    .withName("rpgguide")
            );

            traitsRegistered = true;
            LogUtil.info("RPG NPC Traits가 성공적으로 등록되었습니다.");

        } catch (Exception e) {
            LogUtil.error("RPG NPC Traits 등록 중 오류 발생", e);
        }
    }


    /**
     * RPG NPC 목록 조회
     */
    @NotNull
    public List<NPC> getRPGNPCs() {
        List<NPC> rpgNPCs = new ArrayList<>();

        for (NPC npc : npcRegistry) {
            if (npc != null && isRPGNPC(npc)) {
                rpgNPCs.add(npc);
            }
        }

        return rpgNPCs;
    }

    /**
     * NPC가 RPG NPC인지 확인
     */
    public boolean isRPGNPC(@NotNull NPC npc) {
        return npc.hasTrait(RPGQuestTrait.class) ||
               npc.hasTrait(RPGQuestRewardTrait.class) ||
               npc.hasTrait(RPGShopTrait.class) ||
               npc.hasTrait(RPGGuideTrait.class);
    }

    /**
     * 퀘스트 NPC 목록 조회
     */
    @NotNull
    public List<NPC> getQuestNPCs() {
        List<NPC> questNPCs = new ArrayList<>();

        for (NPC npc : npcRegistry) {
            if (npc != null && npc.hasTrait(RPGQuestTrait.class)) {
                questNPCs.add(npc);
            }
        }

        return questNPCs;
    }

    /**
     * 상점 NPC 목록 조회
     */
    @NotNull
    public List<NPC> getShopNPCs() {
        List<NPC> shopNPCs = new ArrayList<>();

        for (NPC npc : npcRegistry) {
            if (npc != null && npc.hasTrait(RPGShopTrait.class)) {
                shopNPCs.add(npc);
            }
        }

        return shopNPCs;
    }

    /**
     * 가이드 NPC 목록 조회
     */
    @NotNull
    public List<NPC> getGuideNPCs() {
        List<NPC> guideNPCs = new ArrayList<>();

        for (NPC npc : npcRegistry) {
            if (npc != null && npc.hasTrait(RPGGuideTrait.class)) {
                guideNPCs.add(npc);
            }
        }

        return guideNPCs;
    }

    /**
     * NPCManager 정리
     */
    public void shutdown() {
        LogUtil.info("NPCManager 종료 중...");
        // 특별한 정리 작업이 필요한 경우 여기에 추가
    }

    /**
     * Trait가 등록되었는지 확인
     */
    public boolean areTraitsRegistered() {
        return traitsRegistered;
    }
}