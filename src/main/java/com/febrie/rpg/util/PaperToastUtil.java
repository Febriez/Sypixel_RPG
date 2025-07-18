package com.febrie.rpg.util;

import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.progress.ObjectiveProgress;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * 퀘스트 진행도 Toast 알림 유틸리티 (Pure Paper API)
 * Paper 1.21.7+ API를 사용하여 퀘스트 진행 상황을 표시
 */
public class PaperToastUtil {
    
    private static final String TOAST_NAMESPACE = "sypixelrpg";
    private static final String TOAST_PREFIX = "quest_toast_";
    
    /**
     * 퀘스트 진행도 Toast 표시
     * 
     * @param player 플레이어
     * @param quest 퀘스트
     * @param progress 퀘스트 진행도
     */
    public static void showQuestProgressToast(Player player, Quest quest, QuestProgress progress) {
        boolean isKorean = player.locale().getLanguage().equals("ko");
        String questName = quest.getDisplayName(isKorean);
        
        // 진행도 정보 생성
        List<Component> descriptionLines = new ArrayList<>();
        List<QuestObjective> objectives = quest.getObjectives();
        
        for (QuestObjective objective : objectives) {
            ObjectiveProgress objProgress = progress.getObjectiveProgress(objective.getId());
            if (objProgress != null) {
                String objDesc = quest.getObjectiveDescription(objective, isKorean);
                Component progressLine;
                
                if (objProgress.isCompleted()) {
                    progressLine = Component.text("✓ ", NamedTextColor.GREEN)
                            .append(Component.text(objDesc, NamedTextColor.GRAY, TextDecoration.STRIKETHROUGH));
                } else {
                    progressLine = Component.text("• ", NamedTextColor.GRAY)
                            .append(Component.text(objDesc, NamedTextColor.WHITE))
                            .append(Component.text(String.format(" (%d/%d)", 
                                    objProgress.getProgress(), 
                                    objProgress.getRequiredAmount()), 
                                    NamedTextColor.GRAY));
                }
                descriptionLines.add(progressLine);
            }
        }
        
        // 전체 진행률
        int percentage = progress.getCompletionPercentage();
        descriptionLines.add(Component.empty());
        descriptionLines.add(Component.text(isKorean ? "전체 진행률: " : "Total Progress: ", NamedTextColor.AQUA)
                .append(Component.text(percentage + "%", NamedTextColor.WHITE)));
        
        // Description 조합
        Component description = Component.empty();
        for (int i = 0; i < descriptionLines.size(); i++) {
            if (i > 0) {
                description = description.append(Component.newline());
            }
            description = description.append(descriptionLines.get(i));
        }
        
        // Toast 표시
        showAdvancementToast(player, 
            Component.text(questName, NamedTextColor.GOLD, TextDecoration.BOLD),
            description,
            Material.WRITABLE_BOOK);
    }
    
    /**
     * 간단한 Toast 표시 (단일 메시지)
     * 
     * @param player 플레이어
     * @param title 제목
     * @param message 메시지
     * @param icon 아이콘 Material
     */
    public static void showSimpleToast(Player player, String title, String message, Material icon) {
        showAdvancementToast(player,
            Component.text(title, NamedTextColor.GOLD),
            Component.text(message, NamedTextColor.WHITE),
            icon);
    }
    
    /**
     * Advancement를 사용한 Toast 표시 (내부 메서드)
     */
    private static void showAdvancementToast(Player player, Component title, Component description, Material icon) {
        String toastId = TOAST_PREFIX + UUID.randomUUID().toString().substring(0, 8);
        NamespacedKey key = new NamespacedKey(TOAST_NAMESPACE, toastId);
        
        // Component를 JSON으로 변환
        String titleJson = GsonComponentSerializer.gson().serialize(title);
        String descJson = GsonComponentSerializer.gson().serialize(description);
        
        // Advancement JSON 생성
        JsonObject advancementJson = new JsonObject();
        
        // Display 섹션
        JsonObject display = new JsonObject();
        JsonObject iconObj = new JsonObject();
        iconObj.addProperty("item", "minecraft:" + icon.name().toLowerCase());
        display.add("icon", iconObj);
        display.add("title", JsonParser.parseString(titleJson));
        display.add("description", JsonParser.parseString(descJson));
        display.addProperty("frame", "task");
        display.addProperty("show_toast", true);
        display.addProperty("announce_to_chat", false);
        display.addProperty("hidden", true);
        
        advancementJson.add("display", display);
        advancementJson.addProperty("parent", "minecraft:story/root");
        
        // Criteria 섹션
        JsonObject criteria = new JsonObject();
        JsonObject impossibleCriteria = new JsonObject();
        impossibleCriteria.addProperty("trigger", "minecraft:impossible");
        criteria.add("impossible", impossibleCriteria);
        advancementJson.add("criteria", criteria);
        
        // 플레이어에게 Toast 표시
        Bukkit.getScheduler().runTask(com.febrie.rpg.RPGMain.getPlugin(), () -> {
            try {
                // Advancement 로드
                Bukkit.getUnsafe().loadAdvancement(key, advancementJson.toString());
                
                // Advancement 가져오기
                Advancement advancement = Bukkit.getAdvancement(key);
                if (advancement != null) {
                    // 플레이어에게 부여
                    AdvancementProgress advProgress = player.getAdvancementProgress(advancement);
                    
                    // 모든 criteria 완료 처리
                    for (String criterion : advProgress.getRemainingCriteria()) {
                        advProgress.awardCriteria(criterion);
                    }
                    
                    // 1초 후 제거 (Toast가 충분히 표시된 후)
                    Bukkit.getScheduler().runTaskLater(com.febrie.rpg.RPGMain.getPlugin(), () -> {
                        // Advancement 취소
                        for (String criterion : advProgress.getAwardedCriteria()) {
                            advProgress.revokeCriteria(criterion);
                        }
                        
                        // Advancement 제거
                        Bukkit.getUnsafe().removeAdvancement(key);
                        
                        // 클라이언트 동기화를 위한 리로드
                        Bukkit.reloadData();
                    }, 20L);
                }
            } catch (Exception e) {
                // Toast 표시 실패 시 로그
                com.febrie.rpg.RPGMain.getPlugin().getLogger().warning("Failed to show toast: " + e.getMessage());
            }
        });
    }
}