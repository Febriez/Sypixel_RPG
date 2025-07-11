package com.febrie.rpg.gui.framework;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.util.ItemBuilder;
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
 * 버튼을 통해 위아래로 스크롤할 수 있는 기능 제공
 *
 * @author Febrie, CoffeeTory
 */
public abstract class ScrollableGui implements InteractiveGui {

    // GUI 크기 상수
    protected static final int ROWS = 6;
    protected static final int COLS = 9;
    protected static final int GUI_SIZE = ROWS * COLS;

    // 스크롤 영역 설정
    protected static final int SCROLL_START_ROW = 1;
    protected static final int SCROLL_END_ROW = 4;
    protected static final int SCROLL_START_COL = 1;
    protected static final int SCROLL_END_COL = 7;
    protected static final int SCROLL_ROWS = SCROLL_END_ROW - SCROLL_START_ROW + 1;
    protected static final int SCROLL_COLS = SCROLL_END_COL - SCROLL_START_COL + 1;
    protected static final int SCROLL_SIZE = SCROLL_ROWS * SCROLL_COLS;

    // 스크롤 버튼 위치
    protected static final int SCROLL_UP_SLOT = 8;
    protected static final int SCROLL_DOWN_SLOT = 44;
    protected static final int SCROLL_BAR_START = 17;
    protected static final int SCROLL_BAR_END = 35;

    protected final Player viewer;
    protected Inventory inventory;
    protected int currentScroll = 0;
    protected List<GuiItem> scrollableItems = new ArrayList<>();

    public ScrollableGui(@NotNull Player viewer) {
        this.viewer = viewer;
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
        int startIndex = currentScroll * SCROLL_COLS;
        int endIndex = Math.min(startIndex + SCROLL_SIZE, scrollableItems.size());

        return scrollableItems.subList(startIndex, endIndex);
    }

    /**
     * 최대 스크롤 값 계산
     */
    protected int getMaxScroll() {
        int totalRows = (int) Math.ceil((double) scrollableItems.size() / SCROLL_COLS);
        return Math.max(0, totalRows - SCROLL_ROWS);
    }

    /**
     * 스크롤 이동
     */
    protected void scrollUp() {
        if (currentScroll > 0) {
            currentScroll--;
            updateScroll();
            refresh();
        }
    }

    protected void scrollDown() {
        if (currentScroll < getMaxScroll()) {
            currentScroll++;
            updateScroll();
            refresh();
        }
    }

    /**
     * 스크롤 버튼 생성
     */
    protected GuiItem createScrollUpButton() {
        return createScrollButton("▲ 위로", "위로 스크롤", currentScroll > 0, this::scrollUp);
    }

    protected GuiItem createScrollDownButton() {
        return createScrollButton("▼ 아래로", "아래로 스크롤", currentScroll < getMaxScroll(), this::scrollDown);
    }

    /**
     * 스크롤 버튼 생성 헬퍼
     */
    private GuiItem createScrollButton(@NotNull String name, @NotNull String action,
                                       boolean enabled, @NotNull Runnable onClick) {
        Material material = enabled ? Material.LIME_DYE : Material.GRAY_DYE;
        String lore = enabled ? "클릭하여 " + action : "더 이상 " + action.replace("스크롤", "갈 수") + " 없습니다";

        return GuiItem.clickable(
                ItemBuilder.of(material)
                        .displayName(Component.text(name, enabled ? NamedTextColor.GREEN : NamedTextColor.GRAY))
                        .addLore(Component.text(lore, NamedTextColor.GRAY))
                        .build(),
                player -> onClick.run()
        ).setEnabled(enabled);
    }

    /**
     * 스크롤바 아이템 생성
     */
    protected GuiItem createScrollBarItem(int position) {
        int maxScroll = getMaxScroll();

        if (maxScroll == 0) {
            return GuiFactory.createDecoration(Material.WHITE_STAINED_GLASS_PANE);
        }

        // 스크롤바 위치 계산
        int barSlots = 3;
        int activeSlot = (int) Math.round((double) currentScroll / maxScroll * (barSlots - 1));
        int slotIndex = (position - SCROLL_BAR_START) / 9;

        Material material = (slotIndex == activeSlot) ?
                Material.LIME_STAINED_GLASS_PANE : Material.GRAY_STAINED_GLASS_PANE;

        return GuiItem.display(
                ItemBuilder.of(material)
                        .displayName(Component.text("스크롤 위치", NamedTextColor.GRAY))
                        .addLore(Component.text(String.format("%d / %d", currentScroll + 1, maxScroll + 1),
                                NamedTextColor.WHITE))
                        .build()
        );
    }

    /**
     * 스크롤 영역 내의 슬롯인지 확인
     */
    protected boolean isScrollSlot(int slot) {
        int row = slot / COLS;
        int col = slot % COLS;

        return row >= SCROLL_START_ROW && row <= SCROLL_END_ROW &&
                col >= SCROLL_START_COL && col <= SCROLL_END_COL;
    }

    /**
     * 스크롤 영역 슬롯을 실제 아이템 인덱스로 변환
     */
    protected int getScrollItemIndex(int slot) {
        int row = slot / COLS;
        int col = slot % COLS;

        int relativeRow = row - SCROLL_START_ROW;
        int relativeCol = col - SCROLL_START_COL;

        return (currentScroll + relativeRow) * SCROLL_COLS + relativeCol;
    }

    @Override
    public void onSlotClick(@NotNull InventoryClickEvent event, @NotNull Player player,
                            int slot, @NotNull ClickType click) {
        // 스크롤 영역 클릭 처리
        if (isScrollSlot(slot)) {
            int itemIndex = getScrollItemIndex(slot);
            if (itemIndex < scrollableItems.size()) {
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
     * 스크롤 영역 설정
     */
    protected void setupScrollableArea(@NotNull Inventory inventory,
                                       @NotNull Map<Integer, GuiItem> items,
                                       @NotNull BiConsumer<Integer, GuiItem> setItemMethod) {
        // 스크롤 가능한 아이템 설정
        scrollableItems = getScrollableItems();
        List<GuiItem> visibleItems = getVisibleItems();

        // 모든 스크롤 영역을 빈 슬롯으로 초기화
        for (int row = SCROLL_START_ROW; row <= SCROLL_END_ROW; row++) {
            for (int col = SCROLL_START_COL; col <= SCROLL_END_COL; col++) {
                int slot = row * COLS + col;
                setItemMethod.accept(slot, GuiFactory.createFiller());
            }
        }

        // 보이는 아이템 배치
        int index = 0;
        for (int row = SCROLL_START_ROW; row <= SCROLL_END_ROW && index < visibleItems.size(); row++) {
            for (int col = SCROLL_START_COL; col <= SCROLL_END_COL && index < visibleItems.size(); col++) {
                int slot = row * COLS + col;
                setItemMethod.accept(slot, visibleItems.get(index));
                index++;
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
                        .displayName(Component.text("페이지 정보", com.febrie.rpg.util.ColorUtil.INFO))
                        .addLore(Component.text(String.format("페이지 %d / %d", currentPage, totalPages),
                                NamedTextColor.WHITE))
                        .addLore(Component.text(String.format("아이템 %d개", scrollableItems.size()),
                                NamedTextColor.GRAY))
                        .build()
        );
    }
}