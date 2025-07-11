package com.febrie.rpg.gui.framework;

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

/**
 * 스크롤 가능한 GUI 추상 클래스
 * 버튼을 통해 위아래로 스크롤할 수 있는 기능 제공
 *
 * @author Febrie, CoffeeTory
 */
public abstract class ScrollableGui implements InteractiveGui {

    protected static final int ROWS = 6; // GUI 행 수
    protected static final int COLS = 9; // GUI 열 수
    protected static final int GUI_SIZE = ROWS * COLS; // 54 슬롯

    // 스크롤 영역 설정 (1행~4행, 1열~7열)
    protected static final int SCROLL_START_ROW = 1;
    protected static final int SCROLL_END_ROW = 4;
    protected static final int SCROLL_START_COL = 1;
    protected static final int SCROLL_END_COL = 7;

    protected static final int SCROLL_ROWS = SCROLL_END_ROW - SCROLL_START_ROW + 1; // 4행
    protected static final int SCROLL_COLS = SCROLL_END_COL - SCROLL_START_COL + 1; // 7열
    protected static final int SCROLL_SIZE = SCROLL_ROWS * SCROLL_COLS; // 28 슬롯

    // 스크롤 버튼 위치
    protected static final int SCROLL_UP_SLOT = 8; // 우측 상단
    protected static final int SCROLL_DOWN_SLOT = 44; // 우측 하단
    protected static final int SCROLL_BAR_START = 17; // 우측 스크롤바 시작
    protected static final int SCROLL_BAR_END = 35; // 우측 스크롤바 끝

    protected final Player viewer;
    protected int currentScroll = 0;
    protected List<GuiItem> scrollableItems = new ArrayList<>();
    protected Inventory inventory; // Added for access in subclasses

    public ScrollableGui(@NotNull Player viewer) {
        this.viewer = viewer;
    }

    /**
     * 스크롤 가능한 아이템 목록 가져오기
     * 하위 클래스에서 구현해야 함
     */
    protected abstract List<GuiItem> getScrollableItems();

    /**
     * 스크롤 업데이트
     * 주의: setupLayout() 내부에서는 직접 scrollableItems를 설정하여 순환 호출 방지
     */
    protected void updateScroll() {
        scrollableItems = getScrollableItems();
        // refresh() 호출 제거 - 필요시 호출자가 직접 refresh() 호출
    }

    /**
     * 현재 스크롤 위치에서 보여질 아이템들 가져오기
     */
    protected List<GuiItem> getVisibleItems() {
        List<GuiItem> visible = new ArrayList<>();

        int startIndex = currentScroll * SCROLL_COLS;
        int endIndex = Math.min(startIndex + SCROLL_SIZE, scrollableItems.size());

        for (int i = startIndex; i < endIndex; i++) {
            visible.add(scrollableItems.get(i));
        }

        return visible;
    }

    /**
     * 최대 스크롤 값 계산
     */
    protected int getMaxScroll() {
        int totalRows = (int) Math.ceil((double) scrollableItems.size() / SCROLL_COLS);
        return Math.max(0, totalRows - SCROLL_ROWS);
    }

    /**
     * 스크롤 위로
     */
    protected void scrollUp() {
        if (currentScroll > 0) {
            currentScroll--;
            updateScroll();
            refresh();
        }
    }

    /**
     * 스크롤 아래로
     */
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
        boolean canScroll = currentScroll > 0;
        Material material = canScroll ? Material.LIME_DYE : Material.GRAY_DYE;

        return GuiItem.clickable(
                ItemBuilder.of(material)
                        .displayName(Component.text("▲ 위로", canScroll ? NamedTextColor.GREEN : NamedTextColor.GRAY))
                        .addLore(Component.text(canScroll ? "클릭하여 위로 스크롤" : "더 이상 올라갈 수 없습니다",
                                NamedTextColor.GRAY))
                        .build(),
                player -> scrollUp()
        ).setEnabled(canScroll);
    }

    protected GuiItem createScrollDownButton() {
        boolean canScroll = currentScroll < getMaxScroll();
        Material material = canScroll ? Material.LIME_DYE : Material.GRAY_DYE;

        return GuiItem.clickable(
                ItemBuilder.of(material)
                        .displayName(Component.text("▼ 아래로", canScroll ? NamedTextColor.GREEN : NamedTextColor.GRAY))
                        .addLore(Component.text(canScroll ? "클릭하여 아래로 스크롤" : "더 이상 내려갈 수 없습니다",
                                NamedTextColor.GRAY))
                        .build(),
                player -> scrollDown()
        ).setEnabled(canScroll);
    }

    /**
     * 스크롤바 아이템 생성
     */
    protected GuiItem createScrollBarItem(int position) {
        int maxScroll = getMaxScroll();

        if (maxScroll == 0) {
            // 스크롤이 필요없는 경우
            return GuiItem.display(
                    ItemBuilder.of(Material.WHITE_STAINED_GLASS_PANE)
                            .displayName(Component.empty())
                            .build()
            );
        }

        // 스크롤바 위치 계산 (17, 26, 35 슬롯 중 하나)
        int barSlots = 3; // 스크롤바 슬롯 개수
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
        }

        // 기타 슬롯은 하위 클래스에서 처리
        handleNonScrollClick(event, player, slot, click);
    }

    /**
     * 스크롤 영역 외의 클릭 처리
     * 하위 클래스에서 구현
     */
    protected abstract void handleNonScrollClick(@NotNull InventoryClickEvent event,
                                                 @NotNull Player player, int slot,
                                                 @NotNull ClickType click);

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

    /**
     * 스크롤 영역 설정 - 중복 코드 제거를 위한 공통 메소드
     * 하위 클래스의 setupScrollable 메소드에서 사용
     *
     * @param inventory     설정할 인벤토리
     * @param items         아이템 맵
     * @param setItemMethod 아이템 설정 메소드 (slot, item) -> void
     */
    protected void setupScrollableArea(@NotNull Inventory inventory,
                                       @NotNull Map<Integer, GuiItem> items,
                                       @NotNull java.util.function.BiConsumer<Integer, GuiItem> setItemMethod) {
        // 스크롤 가능한 아이템 설정 (updateScroll() 호출하지 않음)
        scrollableItems = getScrollableItems();
        List<GuiItem> visibleItems = getVisibleItems();

        int index = 0;
        for (int row = SCROLL_START_ROW; row <= SCROLL_END_ROW; row++) {
            for (int col = SCROLL_START_COL; col <= SCROLL_END_COL; col++) {
                int slot = row * COLS + col;

                if (index < visibleItems.size()) {
                    setItemMethod.accept(slot, visibleItems.get(index));
                    index++;
                } else {
                    // 빈 슬롯 - GuiFactory 사용
                    setItemMethod.accept(slot, com.febrie.rpg.gui.component.GuiFactory.createFiller());
                }
            }
        }
    }
}