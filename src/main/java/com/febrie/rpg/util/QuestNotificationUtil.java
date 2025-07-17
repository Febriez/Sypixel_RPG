package com.febrie.rpg.util;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.quest.objective.QuestObjective;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.sound.Sound;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * 퀘스트 알림 유틸리티
 * 퀘스트 목표 달성 및 완료 시 패킷으로 알림을 표시
 *
 * @author Febrie
 */
public class QuestNotificationUtil {

    /**
     * 퀘스트 목표 달성 알림
     * 화면 우측 상단에 제작법 해제처럼 표시
     *
     * @param player 플레이어
     * @param quest 퀘스트
     * @param objective 달성한 목표
     */
    public static void notifyObjectiveComplete(@NotNull Player player, @NotNull Quest quest, @NotNull QuestObjective objective) {
        boolean isKorean = RPGMain.getPlugin().getLangManager().getPlayerLanguage(player).startsWith("ko");
        
        // 제목
        Component title = Component.text(isKorean ? "퀘스트 목표 달성!" : "Quest Objective Complete!")
                .color(ColorUtil.UNCOMMON)
                .decoration(TextDecoration.BOLD, false);
        
        // 설명 - 퀘스트 이름과 목표
        Component description = Component.text("")
                .append(Component.text(quest.getDisplayName(isKorean), ColorUtil.RARE))
                .append(Component.text(" - ", NamedTextColor.GRAY))
                .append(Component.text(quest.getObjectiveDescription(objective, isKorean), NamedTextColor.WHITE));
        
        // Toast 알림은 QuestProgress 업데이트 시 자동으로 표시됨
        // 여기서는 소리만 재생
        
        // 소리 재생 - 경험치 획듍 소리
        player.playSound(Sound.sound(
                Key.key("entity.experience_orb.pickup"),
                Sound.Source.MASTER,
                1.0f,
                1.0f
        ));
        
        // 추가 효과음 - 벨 소리
        player.playSound(Sound.sound(
                Key.key("block.note_block.bell"),
                Sound.Source.MASTER,
                0.8f,
                1.2f
        ));
    }
    
    /**
     * 퀘스트 완료 알림
     * 화면 우측 상단에 도전과제 달성처럼 표시
     *
     * @param player 플레이어
     * @param quest 완료한 퀘스트
     */
    public static void notifyQuestComplete(@NotNull Player player, @NotNull Quest quest) {
        boolean isKorean = RPGMain.getPlugin().getLangManager().getPlayerLanguage(player).startsWith("ko");
        
        // 제목
        Component title = Component.text(isKorean ? "퀘스트 완료!" : "Quest Complete!")
                .color(ColorUtil.LEGENDARY)
                .decoration(TextDecoration.BOLD, true);
        
        // 설명 - 퀘스트 이름
        Component description = Component.text(quest.getDisplayName(isKorean), ColorUtil.EPIC);
        
        // Toast 알림은 QuestProgress 업데이트 시 자동으로 표시됨
        // 여기서는 소리만 재생
        
        // 소리 재생 - 레벨업 소리
        player.playSound(Sound.sound(
                Key.key("entity.player.levelup"),
                Sound.Source.MASTER,
                1.0f,
                1.0f
        ));
        
        // 추가 효과음 - 불꽃놀이
        player.playSound(Sound.sound(
                Key.key("entity.firework_rocket.launch"),
                Sound.Source.MASTER,
                0.5f,
                1.0f
        ));
    }
    
    /**
     * 퀘스트 시작 알림
     * 화면 우측 상단에 제작법 해제처럼 표시
     *
     * @param player 플레이어
     * @param quest 시작한 퀘스트
     */
    public static void notifyQuestStart(@NotNull Player player, @NotNull Quest quest) {
        boolean isKorean = RPGMain.getPlugin().getLangManager().getPlayerLanguage(player).startsWith("ko");
        
        // 제목
        Component title = Component.text(isKorean ? "새로운 퀘스트!" : "New Quest!")
                .color(ColorUtil.RARE)
                .decoration(TextDecoration.BOLD, true);
        
        // 설명 - 퀘스트 이름
        Component description = Component.text(quest.getDisplayName(isKorean), ColorUtil.UNCOMMON);
        
        // Toast 알림은 QuestProgress 업데이트 시 자동으로 표시됨
        // 여기서는 소리만 재생
        
        // 소리 재생 - 책 페이지 넘기는 소리
        player.playSound(Sound.sound(
                Key.key("item.book.page_turn"),
                Sound.Source.MASTER,
                1.0f,
                1.0f
        ));
    }
}