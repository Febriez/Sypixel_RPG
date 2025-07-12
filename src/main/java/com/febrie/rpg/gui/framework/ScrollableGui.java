package com.febrie.rpg.gui.framework;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * 스크롤 가능한 GUI 추상 클래스
 * BaseGui를 상속받아 표준 기능 활용
 * 버튼을 통해 위아래로 스크롤할 수 있는 기능 제공
 * <p>
 * 개선사항:
 * - BaseGui 상속으로 표준 helper 메소드 사용
 * - 동적 크기 지원
 * - 범위 검증 강화
 * - 상수 사용으로 매직 넘버 제거
 *
 * @author Febrie, CoffeeTory
 */
public abstract class ScrollableGui extends BaseGui {

    // 스크롤 영역 설정 (상대적 위치)
    protected static final int SCROLL_START_ROW = 1;
    protected static final int SCROLL_END_ROW_OFFSET = 2; // 마지막에서 2줄 위
    protected static final int SCROLL_START_COL = 1;
    protected static final int SCROLL_END_COL = 7;

    protected int currentScroll = 0;
    protected List<GuiItem> scrollableItems = new ArrayList<>();

    // 동적 계산 값들
    protected final int totalRows;
    protected final int scrollEndRow;
    protected final int scrollRows;
    protected final int scrollCols;
    protected final int scrollSize;

    // 스크롤 버튼 위치 (동적)
    protected final int scrollUpSlot;
    protected final int scrollDownSlot;
    protected final int scrollBarStart;
    protected final int scrollBarEnd;

    /**
     * ScrollableGui 생성자 - GuiManager와 LangManager 필수
     */
    public ScrollableGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                         @NotNull LangManager langManager, int requestedSize,
                         @NotNull String titleKey, @NotNull String... titleArgs) {
        super(viewer, guiManager, langManager, requestedSize, titleKey, titleArgs);

        // 스크롤 관련 값 계산
        this.totalRows = size / ROWS_PER_PAGE;
        this.scrollEndRow = Math.max(SCROLL_START_ROW + 1, totalRows - SCROLL_END_ROW_OFFSET);
        this.scrollRows = scrollEndRow - SCROLL_START_ROW + 1;
        this.scrollCols = SCROLL_END_COL - SCROLL_START_COL + 1;
        this.scrollSize = scrollRows * scrollCols;

        // 스크롤 버튼 위치 계산
        this.scrollUpSlot = 8; // 상단 우측
        this.scrollDownSlot = size - 9 + 8; // 하단 우측

        // 스크롤바 위치 계산 (우측 중간 영역)
        this.scrollBarStart = SCROLL_START_ROW * 9 + 8;
        this.scrollBarEnd = scrollEndRow * 9 + 8;
    }

    /**
     * 스크롤 가능한 아이템 목록 가져오기
     * 하위 클래스에서 구현해야 함
     */
    protected abstract List<GuiItem> getScrollableItems();

    /**
     * 스크롤 영역 외의 클릭 처리
     * 하위 클래스에서 구현
     */
    protected abstract void handleNonScrollClick(@NotNull InventoryClickEvent event,
                                                 @NotNull Player player, int slot,
                                                 @NotNull ClickType click);

    @Override
    public void onSlotClick(@NotNull InventoryClickEvent event, @NotNull Player player,
                            int slot, @NotNull ClickType click) {
        if (!isValidSlot(slot)) {
            return;
        }

        // 기본적으로 LEFT_CLICK만 처리 (더블클릭 방지)
        if (!isAllowedClickType(click)) {
            return;
        }

        // 스크롤 버튼 클릭 처리
        if (slot == scrollUpSlot) {
            scrollUp();
            return;
        }
        if (slot == scrollDownSlot) {
            scrollDown();
            return;
        }

        // 스크롤 영역 클릭 처리
        if (isScrollSlot(slot)) {
            int itemIndex = getScrollItemIndex(slot);
            if (itemIndex >= 0 && itemIndex < scrollableItems.size()) {
                GuiItem item = scrollableItems.get(itemIndex);
                if (item != null && item.hasActions()) {
                    item.executeAction(player, click);
                }
            }
        } else {
            // 기타 슬롯은 하위 클래스에서 처리
            handleNonScrollClick(event, player, slot, click);
        }
    }

    /**
     * 스크롤 업데이트
     */
    protected void updateScroll() {
        scrollableItems = getScrollableItems();
    }

    /**
     * 현재 스크롤 위치에서 보여질 아이템들 가져오기
     */
    protected List<GuiItem> getVisibleItems() {
        int startIndex = currentScroll * scrollCols;
        int endIndex = Math.min(startIndex + scrollSize, scrollableItems.size());

        if (startIndex >= scrollableItems.size()) {
            return new ArrayList<>();
        }

        return scrollableItems.subList(startIndex, endIndex);
    }

    /**
     * 최대 스크롤 값 계산
     */
    protected int getMaxScroll() {
        if (scrollableItems.isEmpty()) {
            return 0;
        }

        int totalRows = (int) Math.ceil((double) scrollableItems.size() / scrollCols);
        return Math.max(0, totalRows - scrollRows);
    }

    /**
     * 스크롤 이동
     */
    protected void scrollUp() {
        if (currentScroll > 0) {
            currentScroll--;
            updateScroll();
            refresh();
            playClickSound(viewer);
        }
    }

    protected void scrollDown() {
        if (currentScroll < getMaxScroll()) {
            currentScroll++;
            updateScroll();
            refresh();
            playClickSound(viewer);
        }
    }

    /**
     * 스크롤 버튼 생성
     */
    protected GuiItem createScrollUpButton() {
        boolean canScroll = currentScroll > 0;
        String actionKey = "gui.scroll.up-button.action";
        return createScrollButton(
                trans("gui.scroll.up-button.name"),
                transString(actionKey),
                canScroll,
                this::scrollUp
        );
    }

    protected GuiItem createScrollDownButton() {
        boolean canScroll = currentScroll < getMaxScroll();
        String actionKey = "gui.scroll.down-button.action";
        return createScrollButton(
                trans("gui.scroll.down-button.name"),
                transString(actionKey),
                canScroll,
                this::scrollDown
        );
    }

    /**
     * 스크롤 버튼 생성 헬퍼
     */
    private GuiItem createScrollButton(@NotNull Component name, @NotNull String action,
                                       boolean enabled, @NotNull Runnable onClick) {
        Material material = enabled ? Material.LIME_DYE : Material.GRAY_DYE;
        Component lore = enabled ?
                trans("gui.scroll.button.click-to", "action", action) :
                trans("gui.scroll.button.no-more", "action", action);

        return GuiItem.clickable(
                ItemBuilder.of(material)
                        .displayName(name.color(enabled ? NamedTextColor.GREEN : NamedTextColor.GRAY))
                        .addLore(lore)
                        .build(),
                player -> {
                    if (enabled) {
                        onClick.run();
                    }
                }
        );
    }

    /**
     * 스크롤 영역 설정
     */
    protected void setupScrollableArea(@NotNull Inventory inventory,
                                       @NotNull Map<Integer, GuiItem> items,
                                       @NotNull BiConsumer<Integer, GuiItem> setItemMethod) {
        // 스크롤 가능한 아이템 설정
        scrollableItems = getScrollableItems();
        List<GuiItem> visibleItems = getVisibleItems();

        // 모든 스크롤 영역을 빈 슬롯으로 초기화
        for (int row = SCROLL_START_ROW; row <= scrollEndRow; row++) {
            for (int col = SCROLL_START_COL; col <= SCROLL_END_COL; col++) {
                int slot = row * ROWS_PER_PAGE + col;
                if (slot < size) {
                    setItemMethod.accept(slot, GuiFactory.createFiller());
                }
            }
        }

        // 보이는 아이템 배치
        int index = 0;
        for (int row = SCROLL_START_ROW; row <= scrollEndRow && index < visibleItems.size(); row++) {
            for (int col = SCROLL_START_COL; col <= SCROLL_END_COL && index < visibleItems.size(); col++) {
                int slot = row * ROWS_PER_PAGE + col;
                if (slot < size) {
                    setItemMethod.accept(slot, visibleItems.get(index));
                    index++;
                }
            }
        }
    }

    /**
     * 페이지 정보 아이템 생성
     */
    protected GuiItem createPageInfo() {
        int totalPages = getMaxScroll() + 1;
        int currentPage = currentScroll + 1;

        return GuiItem.display(
                ItemBuilder.of(Material.PAPER)
                        .displayName(trans("gui.scroll.page-info.title"))
                        .addLore(trans("gui.scroll.page-info.current",
                                "current", String.valueOf(currentPage),
                                "total", String.valueOf(totalPages)))
                        .addLore(trans("gui.scroll.page-info.items",
                                "count", String.valueOf(scrollableItems.size())))
                        .build()
        );
    }

    /**
     * 스크롤 영역 슬롯인지 확인
     */
    protected boolean isScrollSlot(int slot) {
        int row = slot / ROWS_PER_PAGE;
        int col = slot % ROWS_PER_PAGE;

        return row >= SCROLL_START_ROW && row <= scrollEndRow &&
                col >= SCROLL_START_COL && col <= SCROLL_END_COL;
    }

    /**
     * 스크롤 슬롯에서 아이템 인덱스 계산
     */
    protected int getScrollItemIndex(int slot) {
        int row = slot / ROWS_PER_PAGE;
        int col = slot % ROWS_PER_PAGE;

        if (!isScrollSlot(slot)) {
            return -1;
        }

        int relativeRow = row - SCROLL_START_ROW;
        int relativeCol = col - SCROLL_START_COL;

        return (currentScroll * scrollCols) + (relativeRow * scrollCols) + relativeCol;
    }

    /**
     * 마지막 줄의 시작 슬롯 번호를 반환
     */
    protected int getLastRowStart() {
        return size - ROWS_PER_PAGE;
    }
}