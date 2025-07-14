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
 * 새로고침 버튼 제거, 유동적 버튼 위치 계산
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

    /**
     * 유동적으로 계산된 뒤로가기 버튼 슬롯
     */
    protected int getBackButtonSlot() {
        int lastRow = (size / ROWS_PER_PAGE) - 1;
        int center = lastRow * ROWS_PER_PAGE + 4; // 가운데
        return center - 1; // 가운데에서 한 칸 왼쪽
    }

    /**
     * 유동적으로 계산된 닫기 버튼 슬롯
     */
    protected int getCloseButtonSlot() {
        int lastRow = (size / ROWS_PER_PAGE) - 1;
        return lastRow * ROWS_PER_PAGE + 4; // 가운데
    }

    /**
     * 유동적으로 계산된 설정 버튼 슬롯 (프로필용)
     */
    protected int getSettingsButtonSlot() {
        int lastRow = (size / ROWS_PER_PAGE) - 1;
        int center = lastRow * ROWS_PER_PAGE + 4; // 가운데
        return center + 1; // 가운데에서 한 칸 오른쪽
    }

    /**
     * 마지막 행의 시작 슬롯
     */
    protected int getLastRowStart() {
        return ((size / ROWS_PER_PAGE) - 1) * ROWS_PER_PAGE;
    }

    @Override
    public void open(@NotNull Player player) {
        player.openInventory(inventory);
    }

    @Override
    public void close(@NotNull Player player) {
        // GUI 관련 정리 작업
    }

    @Override
    public void refresh() {
        inventory.clear();
        items.clear();
        setupLayout();
    }

    @Override
    public void onSlotClick(@NotNull InventoryClickEvent event, @NotNull Player player, int slot, @NotNull ClickType click) {
        event.setCancelled(true);

        if (!isAllowedClickType(click)) {
            return;
        }

        if (!isValidSlot(slot)) {
            return;
        }

        GuiItem item = items.get(slot);
        if (item != null && item.hasActions()) {
            item.executeAction(player, click);
        }
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
     * 뷰어 가져오기 (GuiFramework에는 없지만 내부적으로 필요)
     */
    public @NotNull Player getViewer() {
        return viewer;
    }

    /**
     * GUI 타이틀 - 구현체에서 정의
     */
    public abstract @NotNull Component getTitle();

    /**
     * 슬롯에 아이템 설정
     */
    protected void setItem(int slot, @NotNull GuiItem item) {
        if (isValidSlot(slot)) {
            items.put(slot, item);
            inventory.setItem(slot, item.getItemStack());
        }
    }

    /**
     * 여러 슬롯에 같은 아이템 설정
     */
    protected void setItems(@NotNull GuiItem item, int... slots) {
        for (int slot : slots) {
            setItem(slot, item);
        }
    }

    /**
     * 특정 범위에 아이템 채우기
     */
    protected void fillRange(int start, int end, @NotNull GuiItem item) {
        for (int i = start; i <= end && i < size; i++) {
            setItem(i, item);
        }
    }

    /**
     * 빈 슬롯을 장식으로 채우기
     */
    protected void fillEmptySlots(@NotNull GuiItem filler) {
        for (int i = 0; i < size; i++) {
            if (!items.containsKey(i)) {
                setItem(i, filler);
            }
        }
    }

    /**
     * 표준 네비게이션 버튼 설정
     * 새로고침 버튼 제거됨
     */
    protected void setupStandardNavigation(boolean includeClose) {
        updateNavigationButtons();

        // 닫기 버튼
        if (includeClose) {
            setItem(getCloseButtonSlot(), GuiFactory.createCloseButton(langManager, viewer));
        }
    }

    /**
     * 네비게이션 버튼 업데이트
     * 뒤로가기 가능 여부에 따라 버튼 표시/숨김
     */
    public void updateNavigationButtons() {
        int backSlot = getBackButtonSlot();

        // 뒤로가기 버튼 - GuiManager 상태에 따라 표시
        if (guiManager.canGoBack(viewer)) {
            setItem(backSlot, GuiItem.clickable(
                    new ItemBuilder(Material.ARROW)
                            .displayName(langManager.getComponent(viewer, "gui.buttons.back.name"))
                            .addLore(langManager.getComponent(viewer, "gui.buttons.back.lore"))
                            .build(),
                    guiManager::goBack
            ));
        } else {
            // 뒤로가기 불가능하면 장식용 유리판 배치
            setItem(backSlot, GuiFactory.createDecoration());
        }
    }

    /**
     * 테두리 생성 - 모든 모서리를 포함하도록 수정
     */
    protected void createBorder() {
        createBorder(Material.GRAY_STAINED_GLASS_PANE);
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

    /**
     * 인벤토리 생성
     */
    private Inventory createInventory(@NotNull String titleKey, @NotNull String... titleArgs) {
        Component title = langManager.getComponent(viewer, titleKey, titleArgs);
        return Bukkit.createInventory(this, size, title);
    }

    /**
     * 크기 유효성 검증
     */
    private int validateSize(int requestedSize) {
        // 9의 배수로 맞추기 (최소 9, 최대 54)
        int adjusted = Math.max(9, Math.min(54, requestedSize));
        return (adjusted / 9) * 9;
    }

    /**
     * 슬롯 유효성 검증
     */
    protected boolean isValidSlot(int slot) {
        return slot >= 0 && slot < size;
    }

    /**
     * 클릭 타입 허용 여부
     */
    protected boolean isAllowedClickType(@NotNull ClickType click) {
        return getAllowedClickTypes().contains(click);
    }

    /**
     * 사운드 재생 헬퍼 메서드들
     */
    protected void playClickSound(@NotNull Player player) {
        SoundUtil.playClickSound(player);
    }

    protected void playSuccessSound(@NotNull Player player) {
        SoundUtil.playSuccessSound(player);
    }

    protected void playErrorSound(@NotNull Player player) {
        SoundUtil.playErrorSound(player);
    }

    /**
     * 언어 번역 헬퍼 메서드들
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
     * 슬롯이 클릭 가능한지 확인
     */
    protected boolean isClickable(int slot, @NotNull Player player) {
        if (!isValidSlot(slot)) {
            return false;
        }

        GuiItem item = items.get(slot);
        return item != null && item.hasActions() && item.isEnabled();
    }
}