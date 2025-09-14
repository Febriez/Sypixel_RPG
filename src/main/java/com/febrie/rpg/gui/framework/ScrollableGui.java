package com.febrie.rpg.gui.framework;

import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.Component;
import com.febrie.rpg.util.lang.GuiLangKey;
/**
 * Scrollable GUI implementation for large content lists
 * Provides automatic pagination with scroll controls
 *
 * @author Febrie, CoffeeTory
 */
public abstract class ScrollableGui extends BaseGui {

    // Scroll area configuration
    protected final int scrollRows;
    protected final int scrollCols;
    protected final int scrollStartRow;
    protected final int scrollStartCol;
    protected final int scrollSize;

    // Scroll state
    protected int currentScroll = 0;
    protected List<GuiItem> scrollableItems = new ArrayList<>();

    // Scroll button slots
    protected final int scrollUpSlot;
    protected final int scrollDownSlot;

    /**
     * Creates a scrollable GUI with default configuration
     * Default: 3 rows, 7 columns, starting at row 1, col 1
     */
    public ScrollableGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                         int size, @NotNull Component title) {
        this(viewer, guiManager, size, title, 3, 7, 1, 1);
    }

    /**
     * Creates a scrollable GUI with custom configuration
     */
    public ScrollableGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                         int size, @NotNull Component title,
                         int scrollRows, int scrollCols,
                         int scrollStartRow, int scrollStartCol) {
        super(viewer, guiManager, size, title);

        this.scrollRows = scrollRows;
        this.scrollCols = scrollCols;
        this.scrollStartRow = scrollStartRow;
        this.scrollStartCol = scrollStartCol;
        this.scrollSize = scrollRows * scrollCols;

        // Calculate scroll button positions (right side of scroll area)
        this.scrollUpSlot = (scrollStartRow * 9) + scrollStartCol + scrollCols;
        this.scrollDownSlot = ((scrollStartRow + scrollRows - 1) * 9) + scrollStartCol + scrollCols;
    }

    /**
     * 허용할 클릭 타입 목록 반환 - 하위 클래스에서 구현
     */
    @Override
    protected abstract List<ClickType> getAllowedClickTypes();

    /**
     * Gets the list of scrollable items
     * Override this to provide dynamic content
     */
    protected abstract List<GuiItem> getScrollableItems();

    /**
     * Handles clicks outside the scroll area
     */
    protected abstract void handleNonScrollClick(@NotNull InventoryClickEvent event,
                                                 @NotNull Player player, int slot,
                                                 @NotNull ClickType click);

    @Override
    public void refresh() {
        super.refresh();
        updateScrollItems();
    }

    /**
     * Updates the displayed scroll items
     */
    protected void updateScrollItems() {
        // Clear scroll area
        clearScrollArea();

        // Get current page items
        List<GuiItem> visibleItems = getVisibleItems();

        // Place items in scroll area
        int index = 0;
        for (int row = 0; row < scrollRows && index < visibleItems.size(); row++) {
            for (int col = 0; col < scrollCols && index < visibleItems.size(); col++) {
                int slot = ((scrollStartRow + row) * 9) + (scrollStartCol + col);
                setItem(slot, visibleItems.get(index));
                index++;
            }
        }

        // Update scroll buttons
        updateScrollButtons();
    }

    /**
     * Clears the scroll area
     */
    protected void clearScrollArea() {
        for (int row = 0; row < scrollRows; row++) {
            for (int col = 0; col < scrollCols; col++) {
                int slot = ((scrollStartRow + row) * 9) + (scrollStartCol + col);
                inventory.setItem(slot, null);
                items.remove(slot);
            }
        }
    }

    /**
     * Updates scroll button states
     */
    protected void updateScrollButtons() {
        if (getMaxScroll() > 0) {
            // Up button
            setItem(scrollUpSlot, createScrollUpButton());

            // Down button
            setItem(scrollDownSlot, createScrollDownButton());
        }
    }

    /**
     * Creates the scroll up button
     */
    protected GuiItem createScrollUpButton() {
        boolean canScrollUp = currentScroll > 0;
        Material material = canScrollUp ? Material.ARROW : Material.GRAY_DYE;

        return GuiItem.clickable(
                ItemBuilder.of(material)
                        .displayName(LangManager.text(GuiLangKey.GUI_SCROLL_UP))
                        .addLore(canScrollUp ?
                                LangManager.text(GuiLangKey.GUI_SCROLL_CLICK_TO_SCROLL_UP) :
                                LangManager.text(GuiLangKey.GUI_SCROLL_AT_TOP))
                        .build(),
                player -> {
                    if (canScrollUp) {
                        scrollUp();
                        playClickSound(player);
                    } else {
                        playErrorSound(player);
                    }
                }
        );
    }

    /**
     * Creates the scroll down button
     */
    protected GuiItem createScrollDownButton() {
        boolean canScrollDown = currentScroll < getMaxScroll();
        Material material = canScrollDown ? Material.ARROW : Material.GRAY_DYE;

        return GuiItem.clickable(
                ItemBuilder.of(material)
                        .displayName(LangManager.text(GuiLangKey.GUI_SCROLL_DOWN))
                        .addLore(canScrollDown ?
                                LangManager.text(GuiLangKey.GUI_SCROLL_CLICK_TO_SCROLL_DOWN) :
                                LangManager.text(GuiLangKey.GUI_SCROLL_AT_BOTTOM))
                        .build(),
                player -> {
                    if (canScrollDown) {
                        scrollDown();
                        playClickSound(player);
                    } else {
                        playErrorSound(player);
                    }
                }
        );
    }

    /**
     * Scrolls up one page
     */
    protected void scrollUp() {
        if (currentScroll > 0) {
            currentScroll--;
            updateScrollItems();
        }
    }

    /**
     * Scrolls down one page
     */
    protected void scrollDown() {
        if (currentScroll < getMaxScroll()) {
            currentScroll++;
            updateScrollItems();
        }
    }

    /**
     * Checks if a slot is within the scroll area
     */
    protected boolean isScrollSlot(int slot) {
        int row = slot / 9;
        int col = slot % 9;

        return row >= scrollStartRow && row < scrollStartRow + scrollRows &&
                col >= scrollStartCol && col < scrollStartCol + scrollCols;
    }

    /**
     * Gets the item index for a scroll slot
     */
    protected int getScrollItemIndex(int slot) {
        if (!isScrollSlot(slot)) {
            return -1;
        }

        int row = slot / 9;
        int col = slot % 9;

        int relativeRow = row - scrollStartRow;
        int relativeCol = col - scrollStartCol;

        int indexInPage = (relativeRow * scrollCols) + relativeCol;
        return (currentScroll * scrollRows * scrollCols) + indexInPage;
    }

    @Override
    public void onSlotClick(@NotNull InventoryClickEvent event, @NotNull Player player,
                            int slot, @NotNull ClickType click) {
        if (!isValidSlot(slot)) {
            return;
        }

        // 허용되지 않은 클릭 타입인지 확인
        if (isDisallowedClickType(click)) {
            return;
        }

        // 스크롤 버튼 클릭 처리
        if (slot == scrollUpSlot || slot == scrollDownSlot) {
            GuiItem item = items.get(slot);
            if (item != null && item.hasActions()) {
                item.executeAction(player, click);
            }
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
            // BaseGui의 items Map에서 아이템 찾기
            GuiItem item = items.get(slot);
            if (item != null && item.hasActions()) {
                item.executeAction(player, click);
            } else {
                // 그래도 없으면 하위 클래스에서 처리
                handleNonScrollClick(event, player, slot, click);
            }
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

        int totalPages = (int) Math.ceil((double) scrollableItems.size() / scrollSize);
        return Math.max(0, totalPages - 1);
    }

    /**
     * 스크롤 위치 초기화
     */
    protected void resetScroll() {
        currentScroll = 0;
    }

    /**
     * 특정 페이지로 스크롤
     */
    protected void scrollToPage(int page) {
        currentScroll = Math.max(0, Math.min(page, getMaxScroll()));
        updateScrollItems();
    }
}