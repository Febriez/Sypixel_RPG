package com.febrie.rpg.util;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * 사운드 재생 유틸리티
 * GuiService에서 분리
 *
 * @author Febrie, CoffeeTory
 */
public class SoundUtil {

    // 기본 사운드 설정
    private static final float DEFAULT_VOLUME = 0.5f;
    private static final float DEFAULT_PITCH = 1.0f;

    private SoundUtil() {
        throw new UnsupportedOperationException("유틸리티 클래스는 인스턴스화할 수 없습니다.");
    }

    /**
     * 클릭 사운드 재생 (버튼 클릭)
     */
    public static void playClickSound(@NotNull Player player) {
        playSound(player, Sound.UI_BUTTON_CLICK);
    }
    
    /**
     * 레버 사운드 재생 (GUI 네비게이션)
     */
    public static void playLeverSound(@NotNull Player player) {
        playSound(player, Sound.BLOCK_LEVER_CLICK, 0.3f, 1.2f);
    }
    
    /**
     * 레버 사운드 재생 (볼륨 조절)
     */
    public static void playLeverSound(@NotNull Player player, float volume) {
        playSound(player, Sound.BLOCK_LEVER_CLICK, volume, 1.2f);
    }
    
    /**
     * 책 넘기는 사운드 재생 (뒤로가기)
     */
    public static void playPageTurnSound(@NotNull Player player) {
        playSound(player, Sound.ITEM_BOOK_PAGE_TURN, 0.5f, 1.0f);
    }

    /**
     * 성공 사운드 재생
     */
    public static void playSuccessSound(@NotNull Player player) {
        playSound(player, Sound.ENTITY_PLAYER_LEVELUP);
    }
    
    /**
     * 성공 사운드 재생 (볼륨 조절)
     */
    public static void playSuccessSound(@NotNull Player player, float volume) {
        playSound(player, Sound.ENTITY_PLAYER_LEVELUP, volume);
    }

    /**
     * 에러 사운드 재생
     */
    public static void playErrorSound(@NotNull Player player) {
        playSound(player, Sound.ENTITY_VILLAGER_NO);
    }
    
    /**
     * 에러 사운드 재생 (볼륨 조절)
     */
    public static void playErrorSound(@NotNull Player player, float volume) {
        playSound(player, Sound.ENTITY_VILLAGER_NO, volume);
    }

    /**
     * 아이템 픽업 사운드 재생
     */
    public static void playPickupSound(@NotNull Player player) {
        playSound(player, Sound.ENTITY_ITEM_PICKUP);
    }

    /**
     * 경험치 사운드 재생
     */
    public static void playExpSound(@NotNull Player player) {
        playSound(player, Sound.ENTITY_EXPERIENCE_ORB_PICKUP);
    }

    /**
     * 엔더맨 텔레포트 사운드 재생
     */
    public static void playTeleportSound(@NotNull Player player) {
        playSound(player, Sound.ENTITY_ENDERMAN_TELEPORT);
    }

    /**
     * GUI 열기 사운드 재생
     */
    public static void playOpenSound(@NotNull Player player) {
        playSound(player, Sound.BLOCK_CHEST_OPEN);
    }
    
    /**
     * GUI 열기 사운드 재생 (볼륨 조절)
     */
    public static void playOpenSound(@NotNull Player player, float volume) {
        playSound(player, Sound.BLOCK_CHEST_OPEN, volume);
    }

    /**
     * GUI 닫기 사운드 재생
     */
    public static void playCloseSound(@NotNull Player player) {
        playSound(player, Sound.BLOCK_CHEST_CLOSE);
    }
    
    /**
     * GUI 닫기 사운드 재생 (볼륨 조절)
     */
    public static void playCloseSound(@NotNull Player player, float volume) {
        playSound(player, Sound.BLOCK_CHEST_CLOSE, volume);
    }

    /**
     * 커스텀 사운드 재생
     */
    public static void playSound(@NotNull Player player, @NotNull Sound sound) {
        playSound(player, sound, DEFAULT_VOLUME, DEFAULT_PITCH);
    }

    /**
     * 커스텀 사운드 재생 (볼륨 조절)
     */
    public static void playSound(@NotNull Player player, @NotNull Sound sound, float volume) {
        playSound(player, sound, volume, DEFAULT_PITCH);
    }

    /**
     * 커스텀 사운드 재생 (볼륨, 피치 조절)
     */
    public static void playSound(@NotNull Player player, @NotNull Sound sound, float volume, float pitch) {
        player.playSound(player.getLocation(), sound, volume, pitch);
    }
    
    /**
     * 아이템 픽업 사운드 재생 (보상 수령)
     */
    public static void playItemPickupSound(@NotNull Player player) {
        playSound(player, Sound.ENTITY_ITEM_PICKUP, 0.5f, 1.0f);
    }
    
    /**
     * 삭제 사운드 재생
     */
    public static void playDeleteSound(@NotNull Player player) {
        playSound(player, Sound.ENTITY_ITEM_BREAK, 0.5f, 0.8f);
    }
    
    /**
     * 보상 수령 사운드 재생
     */
    public static void playCompleteQuestSound(@NotNull Player player) {
        playSound(player, Sound.UI_TOAST_CHALLENGE_COMPLETE, 0.7f, 1.2f);
    }

    /**
     * 주변 플레이어들에게 사운드 재생
     */
    public static void playSoundNearby(@NotNull Player source, @NotNull Sound sound, double radius) {
        source.getWorld().getNearbyEntities(source.getLocation(), radius, radius, radius).stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .forEach(player -> playSound(player, sound));
    }
}