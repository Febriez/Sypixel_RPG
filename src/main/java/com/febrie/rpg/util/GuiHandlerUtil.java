package com.febrie.rpg.util;

import com.febrie.rpg.dto.island.IslandDTO;
import com.febrie.rpg.dto.island.IslandSettingsDTO;
import com.febrie.rpg.dto.island.IslandSpawnDTO;
import com.febrie.rpg.gui.framework.BaseGui;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * GUI 핸들러 공통 유틸리티
 * 
 * @author Febrie, CoffeeTory
 */
public final class GuiHandlerUtil {
    
    private GuiHandlerUtil() {
        // 유틸리티 클래스
    }
    
    /**
     * 섬 설정 업데이트 헬퍼 - 설정만 변경
     */
    @NotNull
    public static IslandDTO updateIslandSettings(@NotNull IslandDTO island, @NotNull IslandSettingsDTO newSettings) {
        return IslandDTO.fromFields(
                island.core().islandId(),
                island.core().ownerUuid(),
                island.core().ownerName(),
                island.core().islandName(),
                island.core().size(),
                island.core().isPublic(),
                island.core().createdAt(),
                System.currentTimeMillis(), // lastActivity 업데이트
                island.membership().members(),
                island.membership().workers(),
                island.membership().contributions(),
                island.configuration().spawnData(),
                island.configuration().upgradeData(),
                island.configuration().permissions(),
                island.social().pendingInvites(),
                island.social().recentVisits(),
                island.core().totalResets(),
                island.core().deletionScheduledAt(),
                newSettings
        );
    }
    
    /**
     * 섬 스폰 업데이트 헬퍼 - 스폰 데이터만 변경
     */
    @NotNull
    public static IslandDTO updateIslandSpawn(@NotNull IslandDTO island, @NotNull IslandSpawnDTO newSpawnData) {
        return IslandDTO.fromFields(
                island.core().islandId(),
                island.core().ownerUuid(),
                island.core().ownerName(),
                island.core().islandName(),
                island.core().size(),
                island.core().isPublic(),
                island.core().createdAt(),
                System.currentTimeMillis(), // lastActivity 업데이트
                island.membership().members(),
                island.membership().workers(),
                island.membership().contributions(),
                newSpawnData,
                island.configuration().upgradeData(),
                island.configuration().permissions(),
                island.social().pendingInvites(),
                island.social().recentVisits(),
                island.core().totalResets(),
                island.core().deletionScheduledAt(),
                island.configuration().settings()
        );
    }
    
    /**
     * 섬 공개/비공개 토글 헬퍼
     */
    @NotNull
    public static IslandDTO toggleIslandPublic(@NotNull IslandDTO island) {
        return IslandDTO.fromFields(
                island.core().islandId(),
                island.core().ownerUuid(),
                island.core().ownerName(),
                island.core().islandName(),
                island.core().size(),
                !island.core().isPublic(), // 토글
                island.core().createdAt(),
                System.currentTimeMillis(), // lastActivity 업데이트
                island.membership().members(),
                island.membership().workers(),
                island.membership().contributions(),
                island.configuration().spawnData(),
                island.configuration().upgradeData(),
                island.configuration().permissions(),
                island.social().pendingInvites(),
                island.social().recentVisits(),
                island.core().totalResets(),
                island.core().deletionScheduledAt(),
                island.configuration().settings()
        );
    }
    
    /**
     * 안전한 GUI 실행 - 예외 처리 포함
     */
    public static void safeExecute(@NotNull Player player, @NotNull Runnable action, @Nullable String errorMessage) {
        try {
            action.run();
        } catch (Exception e) {
            String message = errorMessage != null ? errorMessage : "작업 중 오류가 발생했습니다.";
            player.sendMessage(Component.text(message, NamedTextColor.RED));
            LogUtil.error("GUI 작업 중 오류 발생", e);
        }
    }
    
    /**
     * 비동기 작업 후 GUI 업데이트
     */
    public static <T> void asyncUpdate(@NotNull Player player, @NotNull Supplier<T> asyncTask, 
                                      @NotNull Consumer<T> syncUpdate) {
        player.getServer().getScheduler().runTaskAsynchronously(
                player.getServer().getPluginManager().getPlugin("SypixelRPG"),
                () -> {
                    T result = asyncTask.get();
                    player.getServer().getScheduler().runTask(
                            player.getServer().getPluginManager().getPlugin("SypixelRPG"),
                            () -> syncUpdate.accept(result)
                    );
                }
        );
    }
    
    /**
     * GUI 닫고 다른 GUI 열기
     */
    public static void switchGui(@NotNull Player player, @NotNull BaseGui newGui) {
        player.closeInventory();
        player.getServer().getScheduler().runTaskLater(
                player.getServer().getPluginManager().getPlugin("SypixelRPG"),
                () -> newGui.open(player),
                1L
        );
    }
    
    /**
     * 성공 메시지 표시 후 GUI 전환
     */
    public static void successAndSwitch(@NotNull Player player, @NotNull String message, @NotNull BaseGui newGui) {
        player.sendMessage(UnifiedColorUtil.parse("&a" + message));
        switchGui(player, newGui);
    }
    
    /**
     * 에러 메시지 표시 후 GUI 닫기
     */
    public static void errorAndClose(@NotNull Player player, @NotNull String message) {
        player.sendMessage(UnifiedColorUtil.parse("&c" + message));
        player.closeInventory();
    }
    
    /**
     * 권한 체크 헬퍼
     */
    public static boolean checkPermissionAndNotify(@NotNull Player player, @NotNull String permission, 
                                                  @Nullable String errorMessage) {
        if (!player.hasPermission(permission)) {
            String message = errorMessage != null ? errorMessage : "이 작업을 수행할 권한이 없습니다.";
            player.sendMessage(UnifiedColorUtil.parse("&c" + message));
            return false;
        }
        return true;
    }
    
    /**
     * 바이옴 표시 이름 변환
     */
    @NotNull
    public static String getBiomeDisplayName(@NotNull String biome) {
        return switch (biome.toUpperCase()) {
            case "PLAINS" -> "평원";
            case "FOREST" -> "숲";
            case "DESERT" -> "사막";
            case "SNOWY_TAIGA" -> "눈 덮인 타이가";
            case "JUNGLE" -> "정글";
            case "OCEAN" -> "바다";
            case "MUSHROOM_FIELDS" -> "버섯 들판";
            case "SAVANNA" -> "사바나";
            case "SWAMP" -> "늪지대";
            case "BEACH" -> "해변";
            case "MOUNTAIN", "MOUNTAINS" -> "산";
            case "CHERRY_GROVE" -> "벚꽃 숲";
            case "BAMBOO_JUNGLE" -> "대나무 정글";
            default -> biome.replace("_", " ");
        };
    }
    
    /**
     * GUI의 빈 슬롯을 장식 아이템으로 채우기
     * 
     * @param gui GUI 인스턴스
     * @param decorationSupplier 장식 아이템 공급자
     * @param contentStartSlot 컨텐츠 영역 시작 슬롯
     * @param contentEndSlot 컨텐츠 영역 끝 슬롯
     * @param guiSize GUI 전체 크기
     */
    public static void fillEmptySlots(@NotNull Object gui, @NotNull Supplier<Object> decorationSupplier,
                                     int contentStartSlot, int contentEndSlot, int guiSize) {
        // 리플렉션을 사용하여 GUI의 getItem과 setItem 메소드 호출
        try {
            var getItemMethod = gui.getClass().getMethod("getItem", int.class);
            var setItemMethod = gui.getClass().getMethod("setItem", int.class, Object.class);
            
            // 컨텐츠 영역 이전 슬롯들 채우기
            for (int i = 0; i < contentStartSlot; i++) {
                if (getItemMethod.invoke(gui, i) == null) {
                    setItemMethod.invoke(gui, i, decorationSupplier.get());
                }
            }
            
            // 컨텐츠 영역 이후 슬롯들 채우기
            for (int i = contentEndSlot + 1; i < guiSize; i++) {
                if (getItemMethod.invoke(gui, i) == null) {
                    setItemMethod.invoke(gui, i, decorationSupplier.get());
                }
            }
        } catch (Exception e) {
            LogUtil.warning("GUI 빈 슬롯 채우기 실패: " + e.getMessage());
        }
    }
}