package com.febrie.rpg.gui.framework;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.gui.util.GuiUtility;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

/**
 * Base abstract class for all GUI implementations
 * Provides common functionality to reduce code duplication
 * <p>
 * 개선사항:
 * - 인벤토리 크기 검증 추가
 * - 슬롯 범위 검증 추가
 * - 상수 정의로 매직 넘버 제거
 * - 에러 핸들링 개선
 *
 * @author Febrie, CoffeeTory
 */
public abstract class BaseGui implements InteractiveGui {

    // GUI 크기 상수
    protected static final int MIN_GUI_SIZE = 9;
    protected static final int MAX_GUI_SIZE = 54;
    protected static final int ROWS_PER_PAGE = 9;

    // 표준 네비게이션 버튼 위치 (동적 계산을 위한 오프셋)
    protected static final int NAV_BACK_OFFSET = 9; // 좌측 하단 모서리에서 9칸
    protected static final int NAV_REFRESH_OFFSET = 5; // 중앙 하단
    protected static final int NAV_CLOSE_OFFSET = 1; // 우측 하단 모서리에서 1칸

    protected final GuiManager guiManager;
    protected final LangManager langManager;
    protected final Player viewer;
    protected final Inventory inventory;
    protected final Map<Integer, GuiItem> items = new HashMap<>();
    protected final int size;

    /**
     * Creates a new BaseGui with validation
     *
     * @param viewer      The player viewing the GUI
     * @param guiManager  The GUI manager (nullable)
     * @param langManager The language manager
     * @param size        The inventory size (must be multiple of 9, between 9-54)
     * @param titleKey    The language key for the title
     * @param titleArgs   Arguments for the title translation
     * @throws IllegalArgumentException if size is invalid
     */
    protected BaseGui(@NotNull Player viewer, @Nullable GuiManager guiManager,
                      @NotNull LangManager langManager, int size,
                      @NotNull String titleKey, @NotNull String... titleArgs) {
        this.viewer = viewer;
        this.guiManager = guiManager;
        this.langManager = langManager;

        // 크기 검증
        this.size = validateAndAdjustSize(size);

        // 인벤토리 생성
        this.inventory = createInventory(titleKey, titleArgs);

        // 디버그 로그
        if (Bukkit.getLogger().isLoggable(Level.FINE)) {
            Bukkit.getLogger().fine(String.format(
                    "Created %s GUI for %s with size %d",
                    this.getClass().getSimpleName(), viewer.getName(), this.size
            ));
        }
    }

    /**
     * 인벤토리 크기 검증 및 조정
     */
    private int validateAndAdjustSize(int requestedSize) {
        // 9의 배수가 아니면 올림 처리
        int adjustedSize = ((requestedSize + 8) / 9) * 9;

        // 범위 제한
        if (adjustedSize < MIN_GUI_SIZE) {
            adjustedSize = MIN_GUI_SIZE;
        } else if (adjustedSize > MAX_GUI_SIZE) {
            adjustedSize = MAX_GUI_SIZE;
        }

        // 조정이 발생한 경우 경고
        if (adjustedSize != requestedSize) {
            Bukkit.getLogger().warning(String.format(
                    "GUI size adjusted from %d to %d for %s",
                    requestedSize, adjustedSize, this.getClass().getSimpleName()
            ));
        }

        return adjustedSize;
    }

    /**
     * 인벤토리 생성
     */
    private Inventory createInventory(@NotNull String titleKey, @NotNull String... titleArgs) {
        Component title = langManager.getComponent(viewer, titleKey, titleArgs);
        return Bukkit.createInventory(this, size, title);
    }

    @Override
    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    @Override
    public void refresh() {
        inventory.clear();
        items.clear();
        setupLayout();
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void onSlotClick(@NotNull InventoryClickEvent event, @NotNull Player player, int slot, @NotNull ClickType click) {
        // 범위 검증
        if (!isValidSlot(slot)) {
            return;
        }

        GuiItem item = items.get(slot);
        if (item != null && item.hasActions()) {
            item.executeAction(player, click);
        }
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
     * Sets up the GUI layout. Must be implemented by subclasses.
     */
    protected abstract void setupLayout();

    /**
     * Sets an item at the specified slot with validation
     *
     * @param slot The slot index
     * @param item The GUI item to set
     * @return true if item was set successfully
     */
    protected boolean setItem(int slot, @NotNull GuiItem item) {
        if (!isValidSlot(slot)) {
            Bukkit.getLogger().warning(String.format(
                    "Attempted to set item at invalid slot %d in %s (size: %d)",
                    slot, this.getClass().getSimpleName(), size
            ));
            return false;
        }

        GuiUtility.setItem(slot, item, items, inventory);
        return true;
    }

    /**
     * 슬롯이 유효한 범위인지 확인
     */
    protected boolean isValidSlot(int slot) {
        return slot >= 0 && slot < size;
    }

    /**
     * Fills a range of slots with the same item
     *
     * @param startSlot The starting slot (inclusive)
     * @param endSlot   The ending slot (inclusive)
     * @param item      The item to fill with
     */
    protected void fillSlots(int startSlot, int endSlot, @NotNull GuiItem item) {
        for (int i = startSlot; i <= endSlot && i < size; i++) {
            setItem(i, item);
        }
    }

    /**
     * Creates a standard border around the GUI
     *
     * @param material The material for the border (default: GRAY_STAINED_GLASS_PANE)
     */
    protected void createBorder(@NotNull Material material) {
        GuiItem borderItem = GuiFactory.createDecoration(material);
        int rows = size / ROWS_PER_PAGE;

        // Top row
        fillSlots(0, 8, borderItem);

        // Bottom row
        if (rows > 1) {
            fillSlots(size - 9, size - 1, borderItem);
        }

        // Left and right columns
        for (int row = 1; row < rows - 1; row++) {
            setItem(row * 9, borderItem);
            setItem(row * 9 + 8, borderItem);
        }
    }

    /**
     * Creates a standard border with default material
     */
    protected void createBorder() {
        createBorder(Material.GRAY_STAINED_GLASS_PANE);
    }

    /**
     * Sets up standard navigation buttons with dynamic positioning
     *
     * @param includeBack    Whether to include back button
     * @param includeRefresh Whether to include refresh button
     * @param includeClose   Whether to include close button
     */
    protected void setupStandardNavigation(boolean includeBack, boolean includeRefresh, boolean includeClose) {
        int lastRowStart = size - ROWS_PER_PAGE;

        // Back button (좌측)
        if (includeBack && guiManager != null) {
            int backSlot = lastRowStart;
            setItem(backSlot, GuiFactory.createBackButton(guiManager, langManager, viewer));
        }

        // Refresh button (중앙)
        if (includeRefresh) {
            int refreshSlot = lastRowStart + 4;
            if (guiManager != null) {
                setItem(refreshSlot, GuiFactory.createRefreshButton(guiManager, langManager, viewer));
            } else {
                setItem(refreshSlot, GuiFactory.createRefreshButton(player -> refresh(), langManager, viewer));
            }
        }

        // Close button (우측)
        if (includeClose) {
            int closeSlot = size - 1;
            setItem(closeSlot, GuiFactory.createCloseButton(langManager, viewer));
        }
    }

    /**
     * Sets up standard navigation buttons at specific slots
     * 슬롯 번호를 직접 지정하는 레거시 메서드 (하위 호환성)
     *
     * @param backSlot    The slot for back button (-1 to skip)
     * @param refreshSlot The slot for refresh button (-1 to skip)
     * @param closeSlot   The slot for close button (-1 to skip)
     */
    protected void setupNavigationButtons(int backSlot, int refreshSlot, int closeSlot) {
        // Back button
        if (backSlot >= 0 && isValidSlot(backSlot) && guiManager != null) {
            setItem(backSlot, GuiFactory.createBackButton(guiManager, langManager, viewer));
        }

        // Refresh button
        if (refreshSlot >= 0 && isValidSlot(refreshSlot)) {
            if (guiManager != null) {
                setItem(refreshSlot, GuiFactory.createRefreshButton(guiManager, langManager, viewer));
            } else {
                setItem(refreshSlot, GuiFactory.createRefreshButton(player -> refresh(), langManager, viewer));
            }
        }

        // Close button
        if (closeSlot >= 0 && isValidSlot(closeSlot)) {
            setItem(closeSlot, GuiFactory.createCloseButton(langManager, viewer));
        }
    }

    /**
     * 동적으로 계산된 표준 위치에 네비게이션 버튼 설정
     */
    protected void setupDynamicNavigation() {
        int lastRowStart = size - ROWS_PER_PAGE;

        // 표준 위치: 좌측(0), 중앙(4), 우측(8)
        setupNavigationButtons(
                lastRowStart,           // 좌측
                lastRowStart + 4,       // 중앙
                size - 1                // 우측
        );
    }

    /**
     * Gets a translatable component for the viewer
     *
     * @param key  The translation key
     * @param args The arguments
     * @return The translated component
     */
    protected Component trans(@NotNull String key, @NotNull String... args) {
        return langManager.getComponent(viewer, key, args);
    }

    /**
     * Gets a translated message for the viewer
     *
     * @param key  The translation key
     * @param args The arguments
     * @return The translated string
     */
    protected String transString(@NotNull String key, @NotNull String... args) {
        return langManager.getMessage(viewer, key, args);
    }

    /**
     * Sends a translated message to a player
     *
     * @param player The player to send to
     * @param key    The translation key
     * @param args   The arguments
     */
    protected void sendMessage(@NotNull Player player, @NotNull String key, @NotNull String... args) {
        langManager.sendMessage(player, key, args);
    }

    /**
     * Plays a success sound to the player
     *
     * @param player The player
     */
    protected void playSuccessSound(@NotNull Player player) {
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }

    /**
     * Plays an error sound to the player
     *
     * @param player The player
     */
    protected void playErrorSound(@NotNull Player player) {
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
    }

    /**
     * Plays a click sound to the player
     *
     * @param player The player
     */
    protected void playClickSound(@NotNull Player player) {
        player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
    }

    /**
     * 마지막 줄의 시작 슬롯 번호를 반환
     */
    protected int getLastRowStart() {
        return size - ROWS_PER_PAGE;
    }

    /**
     * GUI의 행 수를 반환
     */
    protected int getRows() {
        return size / ROWS_PER_PAGE;
    }
}