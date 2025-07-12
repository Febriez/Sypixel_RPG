package com.febrie.rpg.gui.framework;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.gui.util.GuiUtility;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
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

    @Override
    public void onSlotClick(@NotNull InventoryClickEvent event, @NotNull Player player,
                            int slot, @NotNull ClickType click) {
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
     * 표준 네비게이션 버튼 설정
     * 뒤로가기 버튼은 GuiManager의 상태에 따라 동적으로 표시
     */
    protected void setupStandardNavigation(boolean includeRefresh, boolean includeClose) {
        updateNavigationButtons();

        // 새로고침 버튼
        if (includeRefresh) {
            setItem(REFRESH_BUTTON_SLOT, GuiFactory.createRefreshButton(
                    player -> refresh(), langManager, viewer));
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
    protected void updateNavigationButtons() {
        // 뒤로가기 버튼 - GuiManager 상태에 따라 표시
        if (guiManager.canGoBack(viewer)) {
            setItem(BACK_BUTTON_SLOT, GuiItem.clickable(
                    ItemBuilder.of(Material.ARROW)
                            .displayName(langManager.getComponent(viewer, "gui.buttons.back.name"))
                            .addLore(langManager.getComponent(viewer, "gui.buttons.back.lore"))
                            .build(),
                    player -> guiManager.goBack(player)
            ));
        } else {
            // 뒤로가기 불가능하면 버튼 제거 또는 비활성화
            items.remove(BACK_BUTTON_SLOT);
            inventory.setItem(BACK_BUTTON_SLOT, null);
        }
    }

    /**
     * 레거시 네비게이션 버튼 설정 메서드
     * 하위 호환성을 위해 유지하되 새로운 방식 사용 권장
     */
    @Deprecated
    protected void setupNavigationButtons(int backSlot, int refreshSlot, int closeSlot) {
        // 뒤로가기 버튼
        if (backSlot >= 0 && isValidSlot(backSlot)) {
            if (guiManager.canGoBack(viewer)) {
                setItem(backSlot, GuiItem.clickable(
                        ItemBuilder.of(Material.ARROW)
                                .displayName(langManager.getComponent(viewer, "gui.buttons.back.name"))
                                .addLore(langManager.getComponent(viewer, "gui.buttons.back.lore"))
                                .build(),
                        player -> guiManager.goBack(player)
                ));
            }
        }

        // 새로고침 버튼
        if (refreshSlot >= 0 && isValidSlot(refreshSlot)) {
            setItem(refreshSlot, GuiFactory.createRefreshButton(
                    player -> refresh(), langManager, viewer));
        }

        // 닫기 버튼
        if (closeSlot >= 0 && isValidSlot(closeSlot)) {
            setItem(closeSlot, GuiFactory.createCloseButton(langManager, viewer));
        }
    }

    /**
     * 동적 네비게이션 설정
     * 표준 위치에 네비게이션 버튼 배치
     */
    protected void setupDynamicNavigation() {
        setupStandardNavigation(true, true);
    }

    /**
     * 테두리 생성
     */
    protected void createBorder(Material material) {
        GuiItem borderItem = GuiFactory.createDecoration(material);
        int rows = size / ROWS_PER_PAGE;

        // 상단과 하단
        for (int i = 0; i < ROWS_PER_PAGE; i++) {
            setItem(i, borderItem);
            setItem((rows - 1) * ROWS_PER_PAGE + i, borderItem);
        }

        // 좌측과 우측
        for (int row = 1; row < rows - 1; row++) {
            setItem(row * ROWS_PER_PAGE, borderItem);
            setItem(row * ROWS_PER_PAGE + 8, borderItem);
        }
    }

    protected void createBorder() {
        createBorder(Material.GRAY_STAINED_GLASS_PANE);
    }

    /**
     * 아이템 설정
     */
    protected void setItem(int slot, @NotNull GuiItem item) {
        GuiUtility.setItem(slot, item, items, inventory);
    }

    /**
     * 슬롯 유효성 검사
     */
    protected boolean isValidSlot(int slot) {
        return slot >= 0 && slot < size;
    }

    /**
     * 인벤토리 크기 검증
     */
    private int validateSize(int requestedSize) {
        if (requestedSize < ROWS_PER_PAGE || requestedSize > 54) {
            throw new IllegalArgumentException("GUI size must be between 9 and 54");
        }

        int adjustedSize = Math.min(54, Math.max(ROWS_PER_PAGE,
                (requestedSize / ROWS_PER_PAGE) * ROWS_PER_PAGE));

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

    /**
     * 번역 헬퍼 메서드들
     */
    protected Component trans(@NotNull String key, @NotNull String... args) {
        return langManager.getComponent(viewer, key, args);
    }

    protected String transString(@NotNull String key, @NotNull String... args) {
        return langManager.getMessage(viewer, key, args);
    }

    protected void sendMessage(@NotNull Player player, @NotNull String key, @NotNull String... args) {
        langManager.sendMessage(player, key, args);
    }

    /**
     * 사운드 헬퍼 메서드들
     */
    protected void playSuccessSound(@NotNull Player player) {
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1.0f, 1.0f);
    }

    protected void playErrorSound(@NotNull Player player) {
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
    }

    protected void playClickSound(@NotNull Player player) {
        player.playSound(player.getLocation(), org.bukkit.Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
    }
}