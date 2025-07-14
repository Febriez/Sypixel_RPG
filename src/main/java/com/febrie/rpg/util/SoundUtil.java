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
     * 클릭 사운드 재생
     */
    public static void playClickSound(@NotNull Player player) {
        playSound(player, Sound.UI_BUTTON_CLICK);
    }

    /**
     * 성공 사운드 재생
     */
    public static void playSuccessSound(@NotNull Player player) {
        playSound(player, Sound.ENTITY_PLAYER_LEVELUP);
    }

    /**
     * 에러 사운드 재생
     */
    public static void playErrorSound(@NotNull Player player) {
        playSound(player, Sound.ENTITY_VILLAGER_NO);
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
     * GUI 닫기 사운드 재생
     */
    public static void playCloseSound(@NotNull Player player) {
        playSound(player, Sound.BLOCK_CHEST_CLOSE);
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
     * 주변 플레이어들에게 사운드 재생
     */
    public static void playSoundNearby(@NotNull Player source, @NotNull Sound sound, double radius) {
        source.getWorld().getNearbyEntities(source.getLocation(), radius, radius, radius).stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .forEach(player -> playSound(player, sound));
    }
}