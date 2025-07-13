package com.febrie.rpg.gui.framework;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.SoundUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 기본 GUI 구현체
 * 개선된 네비게이션 시스템과 통합
 *
 * @author Febrie, CoffeeTory
 */
public abstract class BaseGui implements InteractiveGui {

    protected static final int ROWS_PER_PAGE = 9;

    protected final Player viewer;
    protected final GuiManager guiManager;
    protected final LangManager langManager;
    protected final int size;
    protected final Inventory inventory;
    protected final Map<Integer, GuiItem> items = new HashMap<>();

    // 네비게이션 버튼 표준 위치
    protected static final int BACK_BUTTON_SLOT = 45;    // 좌측 하단
    protected static final int REFRESH_BUTTON_SLOT = 49; // 중앙 하단
    protected static final int CLOSE_BUTTON_SLOT = 53;   // 우측 하단

    /**
     * 생성자 - GuiManager 필수
     */
    public BaseGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                   @NotNull LangManager langManager, int requestedSize,
                   @NotNull String titleKey, @NotNull String... titleArgs) {
        this.viewer = viewer;
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.size = validateSize(requestedSize);
        this.inventory = createInventory(titleKey, titleArgs);
    }

    /**
     * GUI 레이아웃 설정 - 하위 클래스에서 구현
     */
    protected abstract void setupLayout();

    /**
     * 허용할 클릭 타입 목록 반환 - 하위 클래스에서 구현
     */
    protected abstract List<ClickType> getAllowedClickTypes();

    @Override
    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    @Override
    public void close(@NotNull Player player) {
        // GuiManager에서 처리
    }

    @Override
    public void refresh() {
        inventory.clear();
        items.clear();
        setupLayout();
        // 뒤로가기 버튼 상태 업데이트
        updateNavigationButtons();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public int getSize() {
        return size;
    }

    /**
     * 외부에서 아이템을 설정할 수 있는 public 메서드
     * GuiService와 같은 서비스 클래스에서 사용
     */
    public void setGuiItem(int slot, @NotNull GuiItem item) {
        setItem(slot, item);
    }

    /**
     * 여러 슬롯에 같은 아이템 설정
     */
    public void setGuiItems(@NotNull GuiItem item, int... slots) {
        for (int slot : slots) {
            setItem(slot, item);
        }
    }

    /**
     * 특정 행에 아이템 설정
     */
    public void setGuiRow(int row, @NotNull GuiItem item) {
        if (row < 0 || row >= 6) return;

        for (int col = 0; col < 9; col++) {
            setItem(row * 9 + col, item);
        }
    }

    /**
     * 특정 열에 아이템 설정
     */
    public void setGuiColumn(int column, @NotNull GuiItem item) {
        if (column < 0 || column >= 9) return;

        for (int row = 0; row < 6; row++) {
            setItem(row * 9 + column, item);
        }
    }

    /**
     * 테두리에 아이템 설정
     */
    public void setGuiBorder(@NotNull GuiItem item) {
        // 상단
        setGuiRow(0, item);
        // 하단
        setGuiRow(5, item);
        // 좌측
        setGuiColumn(0, item);
        // 우측
        setGuiColumn(8, item);
    }

    @Override
    public void onSlotClick(@NotNull InventoryClickEvent event, @NotNull Player player,
                            int slot, @NotNull ClickType click) {
        if (!isValidSlot(slot)) {
            return;
        }

        // 허용된 클릭 타입인지 확인
        if (!isAllowedClickType(click)) {
            return;
        }

        GuiItem item = items.get(slot);
        if (item != null && item.hasActions()) {
            item.executeAction(player, click);
        }
    }

    /**
     * 허용된 클릭 타입인지 확인
     */
    protected boolean isAllowedClickType(@NotNull ClickType click) {
        return getAllowedClickTypes().contains(click);
    }

    @Override
    public boolean isSlotClickable(int slot, @NotNull Player player) {
        if (!isValidSlot(slot)) {
            return false;
        }

        GuiItem item = items.get(slot);
        return item != null && item.hasActions() && item.isEnabled();
    }

    /**
     * 표준 네비게이션 버튼 설정
     * 뒤로가기 버튼은 GuiManager의 상태에 따라 동적으로 표시
     */
    protected void setupStandardNavigation(boolean includeRefresh, boolean includeClose) {
        updateNavigationButtons();

        // 새로고침 버튼
        if (includeRefresh) {
            // GuiManager를 통해 새로고침
            setItem(REFRESH_BUTTON_SLOT, GuiFactory.createRefreshButton(
                    guiManager::refreshCurrentGui, langManager, viewer));
        } else {
            // 새로고침 버튼 위치에 장식 배치
            setItem(REFRESH_BUTTON_SLOT, GuiFactory.createDecoration());
        }

        // 닫기 버튼
        if (includeClose) {
            setItem(CLOSE_BUTTON_SLOT, GuiFactory.createCloseButton(langManager, viewer));
        }
    }

    /**
     * 네비게이션 버튼 업데이트
     * 뒤로가기 가능 여부에 따라 버튼 표시/숨김
     */
    public void updateNavigationButtons() {
        // 뒤로가기 버튼 - GuiManager 상태에 따라 표시
        if (guiManager.canGoBack(viewer)) {
            setItem(BACK_BUTTON_SLOT, GuiItem.clickable(
                    new ItemBuilder(Material.ARROW)
                            .displayName(langManager.getComponent(viewer, "gui.buttons.back.name"))
                            .addLore(langManager.getComponent(viewer, "gui.buttons.back.lore"))
                            .build(),
                    guiManager::goBack
            ));
        } else {
            // 뒤로가기 불가능하면 장식용 유리판 배치
            setItem(BACK_BUTTON_SLOT, GuiFactory.createDecoration());
        }
    }

    /**
     * 테두리 생성 - 모든 모서리를 포함하도록 수정
     */
    protected void createBorder(Material material) {
        GuiItem borderItem = GuiFactory.createDecoration(material);
        int rows = size / ROWS_PER_PAGE;

        // 상단과 하단
        for (int i = 0; i < ROWS_PER_PAGE; i++) {
            setItem(i, borderItem);
            setItem((rows - 1) * ROWS_PER_PAGE + i, borderItem);
        }

        // 좌측과 우측 (모든 행 포함)
        for (int row = 0; row < rows; row++) {
            setItem(row * ROWS_PER_PAGE, borderItem);
            setItem(row * ROWS_PER_PAGE + 8, borderItem);
        }
    }

    protected void createBorder() {
        createBorder(Material.GRAY_STAINED_GLASS_PANE);
    }

    /**
     * 인벤토리 생성
     */
    private Inventory createInventory(@NotNull String titleKey, @NotNull String... titleArgs) {
        Component title = langManager.getComponent(viewer, titleKey, titleArgs);
        return Bukkit.createInventory(this, size, title);
    }

    /**
     * 아이템 설정
     */
    protected void setItem(int slot, @NotNull GuiItem item) {
        if (!isValidSlot(slot)) {
            return;
        }

        items.put(slot, item);
        inventory.setItem(slot, item.getItemStack());
    }

    /**
     * 빈 슬롯을 장식용 아이템으로 채우기
     */
    protected void fillEmptySlots(@NotNull GuiItem decoration) {
        for (int i = 0; i < size; i++) {
            if (inventory.getItem(i) == null) {
                setItem(i, decoration);
            }
        }
    }

    /**
     * 슬롯이 유효한지 확인
     */
    protected boolean isValidSlot(int slot) {
        return slot >= 0 && slot < size;
    }

    /**
     * 크기 검증
     */
    private int validateSize(int requestedSize) {
        // 9의 배수로 반올림 (최소 9, 최대 54)
        int validSize = Math.max(9, Math.min(54, requestedSize));
        return (validSize / 9) * 9;
    }

    /**
     * 마지막 행의 시작 슬롯
     */
    protected int getLastRowStart() {
        return size - 9;
    }

    // Utility methods for subclasses
    protected Component trans(@NotNull String key, @NotNull String... args) {
        return langManager.getComponent(viewer, key, args);
    }

    protected String transString(@NotNull String key, @NotNull String... args) {
        return langManager.getMessage(viewer, key, args);
    }

    protected void sendMessage(@NotNull Player player, @NotNull String key, @NotNull String... args) {
        langManager.sendMessage(player, key, args);
    }

    protected void playClickSound(@NotNull Player player) {
        SoundUtil.playClickSound(player);
    }

    protected void playSuccessSound(@NotNull Player player) {
        SoundUtil.playSuccessSound(player);
    }

    protected void playErrorSound(@NotNull Player player) {
        SoundUtil.playErrorSound(player);
    }
}