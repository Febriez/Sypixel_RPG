package com.febrie.rpg.quest.util;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.objective.QuestObjective;
import com.febrie.rpg.quest.progress.QuestProgress;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.SoundUtil;
import com.febrie.rpg.util.ToastUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 퀘스트 관련 유틸리티 클래스
 * 알림, 메시지 등 공통 기능 제공
 * 
 * @author Febrie
 */
public final class QuestUtil {
    
    private QuestUtil() {
        // 유틸리티 클래스는 인스턴스화 방지
    }
    
    /**
     * 알림 타입
     */
    public enum NotificationType {
        QUEST_START("", ColorUtil.GOLD, ColorUtil.RARE, "quest.started", SoundUtil::playOpenSound),
        OBJECTIVE_COMPLETE("✓ ", ColorUtil.SUCCESS, null, null, SoundUtil::playSuccessSound),
        QUEST_COMPLETE("🎉 ", ColorUtil.GOLD, ColorUtil.LEGENDARY, "quest.completed", SoundUtil::playCompleteQuestSound),
        QUEST_CANCEL("❌ ", ColorUtil.ERROR, ColorUtil.COMMON, "quest.cancelled", SoundUtil::playCloseSound),
        REWARD_CLAIMED("💰 ", ColorUtil.GOLD, ColorUtil.RARE, "quest.reward-claimed", SoundUtil::playItemPickupSound);
        
        final String prefix;
        final TextColor prefixColor;
        final TextColor questNameColor;
        final String messageKey;
        final SoundPlayer soundPlayer;
        
        NotificationType(String prefix, TextColor prefixColor, TextColor questNameColor, 
                        String messageKey, SoundPlayer soundPlayer) {
            this.prefix = prefix;
            this.prefixColor = prefixColor;
            this.questNameColor = questNameColor;
            this.messageKey = messageKey;
            this.soundPlayer = soundPlayer;
        }
        
        @FunctionalInterface
        interface SoundPlayer {
            void play(Player player);
        }
    }
    
    /**
     * 퀘스트 시작 알림
     */
    public static void notifyQuestStart(@NotNull Player player, @NotNull Quest quest, 
                                       @NotNull QuestProgress progress, @NotNull RPGMain plugin) {
        notifyQuest(player, quest, progress, plugin, NotificationType.QUEST_START);
    }
    
    /**
     * 목표 완료 알림
     */
    public static void notifyObjectiveComplete(@NotNull Player player, @NotNull Quest quest,
                                              @NotNull QuestProgress progress, @NotNull QuestObjective objective,
                                              @NotNull RPGMain plugin) {
        // 목표 완료는 특별 처리 (목표 설명 표시)
        
        // 토스트 알림
        ToastUtil.showQuestProgressToast(player, quest, progress);
        
        // 채팅 메시지
        player.sendMessage(
            Component.text("✓ ", ColorUtil.SUCCESS).append(quest.getObjectiveDescription(objective, player))
        );
        
        // 효과음
        SoundUtil.playSuccessSound(player);
    }
    
    /**
     * 퀘스트 완료 알림
     */
    public static void notifyQuestComplete(@NotNull Player player, @NotNull Quest quest,
                                          @NotNull QuestProgress progress, @NotNull RPGMain plugin) {
        notifyQuest(player, quest, progress, plugin, NotificationType.QUEST_COMPLETE);
        
        // 추가 메시지 (보상 NPC 방문 안내)
        player.sendMessage(
            LangManager.getMessage(player, "quest.reward-npc-visit").color(ColorUtil.INFO)
        );
    }
    
    /**
     * 퀘스트 취소 알림
     */
    public static void notifyQuestCancel(@NotNull Player player, @NotNull Quest quest, @NotNull RPGMain plugin) {
        notifyQuest(player, quest, null, plugin, NotificationType.QUEST_CANCEL);
    }
    
    /**
     * 보상 수령 알림
     */
    public static void notifyRewardClaimed(@NotNull Player player, @NotNull Quest quest, @NotNull RPGMain plugin) {
        notifyQuest(player, quest, null, plugin, NotificationType.REWARD_CLAIMED);
    }
    
    /**
     * 공통 퀘스트 알림 처리 (내부 메소드)
     */
    private static void notifyQuest(@NotNull Player player, @NotNull Quest quest, 
                                   @Nullable QuestProgress progress, @NotNull RPGMain plugin,
                                   @NotNull NotificationType type) {
        // 토스트 알림 (progress가 있는 경우만)
        if (progress != null && (type == NotificationType.QUEST_START || type == NotificationType.QUEST_COMPLETE)) {
            ToastUtil.showQuestProgressToast(player, quest, progress);
        }
        
        // 채팅 메시지 구성
        Component message = Component.text(type.prefix, type.prefixColor);
        
        // 퀘스트 이름 추가 (색상이 지정된 경우)
        if (type.questNameColor != null) {
            message = message.append(quest.getDisplayName(player).color(type.questNameColor));
        }
        
        // 추가 메시지 (messageKey가 있는 경우)
        if (type.messageKey != null) {
            message = message.append(
                LangManager.getMessage(player, type.messageKey).color(
                    type == NotificationType.QUEST_CANCEL ? ColorUtil.ERROR : ColorUtil.SUCCESS
                )
            );
        }
        
        player.sendMessage(message);
        
        // 효과음 재생
        if (type.soundPlayer != null) {
            type.soundPlayer.play(player);
        }
    }
}