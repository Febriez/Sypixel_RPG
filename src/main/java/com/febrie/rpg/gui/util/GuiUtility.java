package com.febrie.rpg.gui.util;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.logging.Level;

/**
 * GUI 구현체들의 공통 기능을 제공하는 유틸리티 클래스
 * 반복적인 코드를 줄이고 일관성을 유지
 * <p>
 * 개선사항:
 * - 범위 검증 강화
 * - 에러 핸들링 추가
 * - 상수 정의로 매직 넘버 제거
 * - 더 명확한 메서드 이름과 문서화
 *
 * @author Febrie, CoffeeTory
 */
public final class GuiUtility {

    // GUI 관련 상수
    public static final int ROWS_PER_PAGE = 9;
    public static final int MIN_GUI_SIZE = 9;
    public static final int MAX_GUI_SIZE = 54;
    public static final int STANDARD_GUI_ROWS = 6;
    public static final int STANDARD_GUI_SIZE = STANDARD_GUI_ROWS * ROWS_PER_PAGE;

    private GuiUtility() {
        throw new UnsupportedOperationException("Utility class");
    }

    /**
     * 표준 6x9 GUI의 테두리 설정
     *
     * @param inventory 인벤토리
     * @param items     아이템 맵
     * @param setItem   아이템 설정 메소드
     */
    public static void setupStandardBorder(@NotNull Inventory inventory,
                                           @NotNull Map<Integer, GuiItem> items,
                                           @NotNull BiConsumer<Integer, GuiItem> setItem) {
        int size = inventory.getSize();
        int rows = size / ROWS_PER_PAGE;

        // 상단 테두리
        for (int i = 0; i < ROWS_PER_PAGE && i < size; i++) {
            setItem.accept(i, GuiFactory.createDecoration());
        }

        // 하단 테두리
        if (rows > 1) {
            int bottomStart = (rows - 1) * ROWS_PER_PAGE;
            for (int i = bottomStart; i < size && i < bottomStart + ROWS_PER_PAGE; i++) {
                setItem.accept(i, GuiFactory.createDecoration());
            }
        }

        // 좌우 테두리
        for (int row = 1; row < rows - 1; row++) {
            int leftSlot = row * ROWS_PER_PAGE;
            int rightSlot = leftSlot + 8;

            if (leftSlot < size) setItem.accept(leftSlot, GuiFactory.createDecoration());
            if (rightSlot < size) setItem.accept(rightSlot, GuiFactory.createDecoration());
        }
    }

    /**
     * 커스텀 테두리 설정 (특정 슬롯 제외 가능)
     *
     * @param inventory     인벤토리
     * @param items         아이템 맵
     * @param setItem       아이템 설정 메소드
     * @param excludedSlots 제외할 슬롯들
     */
    public static void setupBorderWithExclusions(@NotNull Inventory inventory,
                                                 @NotNull Map<Integer, GuiItem> items,
                                                 @NotNull BiConsumer<Integer, GuiItem> setItem,
                                                 int... excludedSlots) {
        Set<Integer> excluded = new HashSet<>();
        for (int slot : excludedSlots) {
            excluded.add(slot);
        }

        int size = inventory.getSize();
        int rows = size / ROWS_PER_PAGE;

        // 상단 테두리
        for (int i = 0; i < ROWS_PER_PAGE && i < size; i++) {
            if (!excluded.contains(i)) {
                setItem.accept(i, GuiFactory.createDecoration());
            }
        }

        // 하단 테두리
        if (rows > 1) {
            int bottomStart = (rows - 1) * ROWS_PER_PAGE;
            for (int i = bottomStart; i < size && i < bottomStart + ROWS_PER_PAGE; i++) {
                if (!excluded.contains(i)) {
                    setItem.accept(i, GuiFactory.createDecoration());
                }
            }
        }

        // 좌우 테두리
        for (int row = 1; row < rows - 1; row++) {
            int leftSlot = row * ROWS_PER_PAGE;
            int rightSlot = leftSlot + 8;

            if (leftSlot < size && !excluded.contains(leftSlot)) {
                setItem.accept(leftSlot, GuiFactory.createDecoration());
            }
            if (rightSlot < size && !excluded.contains(rightSlot)) {
                setItem.accept(rightSlot, GuiFactory.createDecoration());
            }
        }
    }

    /**
     * 전체 배경 채우기
     *
     * @param inventory 인벤토리
     * @param items     아이템 맵
     * @param setItem   아이템 설정 메소드
     * @param material  배경 재료
     */
    public static void fillBackground(@NotNull Inventory inventory,
                                      @NotNull Map<Integer, GuiItem> items,
                                      @NotNull BiConsumer<Integer, GuiItem> setItem,
                                      @NotNull Material material) {
        int size = inventory.getSize();
        for (int i = 0; i < size; i++) {
            setItem.accept(i, GuiFactory.createDecoration(material));
        }
    }

    /**
     * 특정 영역 채우기
     *
     * @param items     아이템 맵
     * @param setItem   아이템 설정 메소드
     * @param startSlot 시작 슬롯
     * @param endSlot   끝 슬롯
     * @param material  재료
     * @param maxSize   최대 인벤토리 크기 (범위 검증용)
     */
    public static void fillArea(@NotNull Map<Integer, GuiItem> items,
                                @NotNull BiConsumer<Integer, GuiItem> setItem,
                                int startSlot, int endSlot,
                                @NotNull Material material,
                                int maxSize) {
        // 범위 검증
        if (startSlot < 0 || endSlot >= maxSize || startSlot > endSlot) {
            Bukkit.getLogger().warning(String.format(
                    "Invalid fill area: start=%d, end=%d, maxSize=%d",
                    startSlot, endSlot, maxSize
            ));
            return;
        }

        for (int i = startSlot; i <= endSlot; i++) {
            setItem.accept(i, GuiFactory.createDecoration(material));
        }
    }

    /**
     * 표준 네비게이션 버튼 설정 (동적 위치 계산)
     *
     * @param inventory     인벤토리
     * @param items         아이템 맵
     * @param setItem       아이템 설정 메소드
     * @param backButton    뒤로가기 버튼 (null 가능)
     * @param closeButton   닫기 버튼
     * @param refreshButton 새로고침 버튼 (null 가능)
     */
    public static void setupDynamicNavigationButtons(@NotNull Inventory inventory,
                                                     @NotNull Map<Integer, GuiItem> items,
                                                     @NotNull BiConsumer<Integer, GuiItem> setItem,
                                                     @Nullable GuiItem backButton,
                                                     @NotNull GuiItem closeButton,
                                                     @Nullable GuiItem refreshButton) {
        int size = inventory.getSize();
        int lastRowStart = size - ROWS_PER_PAGE;

        // 최소 크기 검증
        if (size < MIN_GUI_SIZE) {
            Bukkit.getLogger().warning("Inventory too small for navigation buttons: " + size);
            return;
        }

        // 표준 위치: 좌측(0), 중앙(4), 우측(8)
        if (backButton != null) {
            int backSlot = lastRowStart;
            if (isValidSlot(backSlot, size)) {
                setItem.accept(backSlot, backButton);
            }
        }

        // 닫기 버튼은 항상 우측 끝
        int closeSlot = size - 1;
        if (isValidSlot(closeSlot, size)) {
            setItem.accept(closeSlot, closeButton);
        }

        if (refreshButton != null) {
            int refreshSlot = lastRowStart + 4; // 중앙
            if (isValidSlot(refreshSlot, size)) {
                setItem.accept(refreshSlot, refreshButton);
            }
        }
    }

    /**
     * 레거시 네비게이션 버튼 설정 (고정 위치)
     *
     * @deprecated Use setupDynamicNavigationButtons instead
     */
    @Deprecated
    public static void setupNavigationButtons(@NotNull Map<Integer, GuiItem> items,
                                              @NotNull BiConsumer<Integer, GuiItem> setItem,
                                              @Nullable GuiItem backButton,
                                              @NotNull GuiItem closeButton,
                                              @Nullable GuiItem refreshButton) {
        // 표준 위치: 48(뒤로), 49(닫기), 50(새로고침)
        if (backButton != null) {
            setItem.accept(48, backButton);
        }

        setItem.accept(49, closeButton);

        if (refreshButton != null) {
            setItem.accept(50, refreshButton);
        }
    }

    /**
     * 진행률 바 생성 (일반 문자)
     *
     * @param percentage 백분율 (0-100)
     * @param length     바 길이
     * @param filledChar 채워진 문자
     * @param emptyChar  빈 문자
     * @return 진행률 바 문자열
     */
    public static String createProgressBar(double percentage, int length,
                                           String filledChar, String emptyChar) {
        percentage = Math.max(0, Math.min(100, percentage)); // 0-100 범위로 제한
        int filled = (int) Math.round(percentage / 100.0 * length);
        return filledChar.repeat(Math.max(0, filled)) +
                emptyChar.repeat(Math.max(0, length - filled));
    }

    /**
     * 색상 진행률 바 생성
     *
     * @param percentage 백분율 (0-100)
     * @param length     바 길이
     * @return 색상이 적용된 진행률 바
     */
    public static String createColoredProgressBar(double percentage, int length) {
        return createProgressBar(percentage, length, "█", "░");
    }

    /**
     * 슬롯 위치를 행/열로 변환
     *
     * @param slot 슬롯 번호
     * @return [행, 열] 배열
     */
    public static int[] slotToRowCol(int slot) {
        return new int[]{slot / ROWS_PER_PAGE, slot % ROWS_PER_PAGE};
    }

    /**
     * 행/열을 슬롯 위치로 변환
     *
     * @param row 행 (0부터 시작)
     * @param col 열 (0-8)
     * @return 슬롯 번호
     */
    public static int rowColToSlot(int row, int col) {
        if (col < 0 || col >= ROWS_PER_PAGE) {
            throw new IllegalArgumentException("Column must be between 0 and 8: " + col);
        }
        return row * ROWS_PER_PAGE + col;
    }

    /**
     * 중앙 정렬된 아이템 배치 계산
     *
     * @param itemCount  아이템 개수
     * @param maxColumns 최대 열 수
     * @param startRow   시작 행
     * @return 각 아이템의 슬롯 위치 배열
     */
    public static int[] getCenteredSlots(int itemCount, int maxColumns, int startRow) {
        if (itemCount <= 0 || maxColumns <= 0 || maxColumns > ROWS_PER_PAGE) {
            return new int[0];
        }

        int[] slots = new int[itemCount];
        int columns = Math.min(itemCount, maxColumns);
        int startCol = (ROWS_PER_PAGE - columns) / 2;

        for (int i = 0; i < itemCount; i++) {
            int row = startRow + (i / maxColumns);
            int col = startCol + (i % columns);
            slots[i] = rowColToSlot(row, col);
        }

        return slots;
    }

    /**
     * 세로 중앙 정렬 계산
     *
     * @param itemCount 아이템 개수
     * @param maxRows   최대 행 수
     * @param totalRows 전체 행 수
     * @return 시작 행
     */
    public static int getVerticalCenteredStartRow(int itemCount, int maxRows, int totalRows) {
        int neededRows = Math.min((itemCount + ROWS_PER_PAGE - 1) / ROWS_PER_PAGE, maxRows);
        return Math.max(0, (totalRows - neededRows) / 2);
    }

    /**
     * 아이템 설정 헬퍼 메소드 (범위 검증 포함)
     * Map과 Inventory에 동시에 설정
     *
     * @param slot      슬롯
     * @param item      아이템
     * @param items     아이템 맵
     * @param inventory 인벤토리
     * @return 성공 여부
     */
    public static boolean setItem(int slot, @NotNull GuiItem item,
                                  @NotNull Map<Integer, GuiItem> items,
                                  @NotNull Inventory inventory) {
        // 범위 검증
        if (!isValidSlot(slot, inventory.getSize())) {
            if (Bukkit.getLogger().isLoggable(Level.WARNING)) {
                Bukkit.getLogger().warning(String.format(
                        "Attempted to set item at invalid slot %d (inventory size: %d)",
                        slot, inventory.getSize()
                ));
            }
            return false;
        }

        items.put(slot, item);
        inventory.setItem(slot, item.getItemStack());
        return true;
    }

    /**
     * 슬롯이 유효한지 검증
     */
    public static boolean isValidSlot(int slot, int inventorySize) {
        return slot >= 0 && slot < inventorySize;
    }

    /**
     * 여러 슬롯에 동일한 아이템 설정
     *
     * @param slots   슬롯 배열
     * @param item    아이템
     * @param items   아이템 맵
     * @param setItem 아이템 설정 메소드
     */
    public static void setItemsInSlots(int[] slots, @NotNull GuiItem item,
                                       @NotNull Map<Integer, GuiItem> items,
                                       @NotNull BiConsumer<Integer, GuiItem> setItem) {
        for (int slot : slots) {
            setItem.accept(slot, item);
        }
    }

    /**
     * 페이지 정보 텍스트 생성
     *
     * @param currentPage 현재 페이지 (1부터 시작)
     * @param totalPages  전체 페이지
     * @return 페이지 정보 텍스트
     */
    public static String formatPageInfo(int currentPage, int totalPages) {
        return String.format("%d / %d", currentPage, totalPages);
    }

    /**
     * 시간 포맷팅 (간단 버전)
     *
     * @param seconds 초
     * @return 포맷된 시간 문자열
     */
    public static String formatSimpleTime(long seconds) {
        if (seconds < 0) {
            return "0초";
        }

        if (seconds < 60) {
            return seconds + "초";
        } else if (seconds < 3600) {
            return (seconds / 60) + "분 " + (seconds % 60) + "초";
        } else {
            long hours = seconds / 3600;
            long minutes = (seconds % 3600) / 60;
            return hours + "시간 " + minutes + "분";
        }
    }

    /**
     * 인벤토리 크기 검증 및 조정
     *
     * @param size 요청된 크기
     * @return 조정된 크기 (9의 배수, 9-54 범위)
     */
    public static int validateInventorySize(int size) {
        // 9의 배수로 올림
        int adjusted = ((size + 8) / 9) * 9;

        // 범위 제한
        if (adjusted < MIN_GUI_SIZE) {
            return MIN_GUI_SIZE;
        } else if (adjusted > MAX_GUI_SIZE) {
            return MAX_GUI_SIZE;
        }

        return adjusted;
    }
}