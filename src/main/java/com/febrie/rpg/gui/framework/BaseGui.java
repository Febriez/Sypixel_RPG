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
 * 기본 GUI 프레임워크
 * 모든 GUI가 상속받는 추상 클래스
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
    protected static final int BACK_BUTTON_SLOT = 48;    // 하단 중앙에서 왼쪽
    protected static final int CLOSE_BUTTON_SLOT = 49;   // 하단 중앙
    protected static final int REFRESH_BUTTON_SLOT = 50; // 하단 중앙에서 오른쪽

    /**
     * 생성자
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
     * GUI 타이틀 - 하위 클래스에서 구현
     */
    @Override
    public abstract @NotNull Component getTitle();

    /**
     * 허용된 클릭 타입 목록 - 하위 클래스에서 오버라이드 가능
     */
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }

    /**
     * 크기 유효성 검사
     */
    private int validateSize(int requestedSize) {
        if (requestedSize < 9 || requestedSize > 54 || requestedSize % 9 != 0) {
            throw new IllegalArgumentException("Invalid GUI size: " + requestedSize);
        }
        return requestedSize;
    }

    /**
     * 인벤토리 생성
     */
    private Inventory createInventory(@NotNull String titleKey, @NotNull String... titleArgs) {
        Component title = langManager.getComponent(viewer, titleKey, titleArgs);
        return Bukkit.createInventory(null, size, title);
    }

    // InteractiveGui 구현

    @Override
    public int getSize() {
        return size;
    }

    @Override
    public void open(@NotNull Player player) {
        if (!player.equals(viewer)) {
            throw new IllegalStateException("Cannot open GUI for different player");
        }
        player.openInventory(inventory);
        SoundUtil.playOpenSound(player);
    }

    @Override
    public void refresh() {
        inventory.clear();
        items.clear();
        setupLayout();
    }

    /**
     * 아이템 설정 - GuiFramework에 없는 자체 메소드
     */
    public void setItem(int slot, @NotNull GuiItem item) {
        if (!isValidSlot(slot)) {
            return;
        }
        items.put(slot, item);
        inventory.setItem(slot, item.getItemStack());
    }

    /**
     * 아이템 제거 - GuiFramework에 없는 자체 메소드
     */
    public void removeItem(int slot) {
        if (isValidSlot(slot)) {
            items.remove(slot);
            inventory.setItem(slot, null);
        }
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    /**
     * 유효한 슬롯인지 확인 - GuiFramework에 없는 자체 메소드
     */
    protected boolean isValidSlot(int slot) {
        return slot >= 0 && slot < size;
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
     */
    protected void setupStandardNavigation(boolean includeRefresh, boolean includeClose) {
        updateNavigationButtons();

        // 새로고침 버튼
        if (includeRefresh) {
            setItem(REFRESH_BUTTON_SLOT, GuiItem.clickable(
                    new ItemBuilder(Material.EMERALD)
                            .displayName(langManager.getComponent(viewer, "gui.buttons.refresh.name"))
                            .addLore(langManager.getComponent(viewer, "gui.buttons.refresh.lore"))
                            .build(),
                    p -> {
                        refresh();
                        playClickSound(p);
                    }
            ));
        }

        // 닫기 버튼
        if (includeClose) {
            setItem(CLOSE_BUTTON_SLOT, GuiFactory.createCloseButton(langManager, viewer));
        }
    }

    /**
     * 네비게이션 버튼 업데이트
     * GuiManager의 네비게이션 스택 상태에 따라 뒤로가기 버튼 표시
     */
    public void updateNavigationButtons() {
        // 뒤로가기 버튼 - GuiManager에서 뒤로갈 수 있는지 확인
        if (guiManager.canNavigateBack(viewer)) {
            setItem(BACK_BUTTON_SLOT, GuiItem.clickable(
                    new ItemBuilder(Material.ARROW)
                            .displayName(langManager.getComponent(viewer, "gui.buttons.back.name"))
                            .addLore(langManager.getComponent(viewer, "gui.buttons.back.lore"))
                            .build(),
                    p -> {
                        if (guiManager.navigateBack(p)) {
                            playClickSound(p);
                        }
                    }
            ));
        } else {
            // 뒤로가기 불가능한 경우 장식 아이템
            setItem(BACK_BUTTON_SLOT, GuiFactory.createDecoration());
        }
    }

    /**
     * 경계선 생성
     */
    protected void createBorder() {
        createBorder(Material.GRAY_STAINED_GLASS_PANE);
    }

    /**
     * 커스텀 재료로 경계선 생성
     */
    protected void createBorder(@NotNull Material material) {
        GuiItem borderItem = GuiFactory.createDecoration(material);

        // 상단 행
        for (int i = 0; i < 9; i++) {
            setItem(i, borderItem);
        }

        // 하단 행
        for (int i = size - 9; i < size; i++) {
            setItem(i, borderItem);
        }

        // 좌우 열
        for (int i = 9; i < size - 9; i += 9) {
            setItem(i, borderItem);
            setItem(i + 8, borderItem);
        }
    }

    // 유틸리티 메소드들

    /**
     * 번역된 컴포넌트 가져오기 (간편 메소드)
     */
    protected Component trans(@NotNull String key, @NotNull String... args) {
        return langManager.getComponent(viewer, key, args);
    }

    /**
     * 번역된 문자열 가져오기 (간편 메소드)
     */
    protected String transString(@NotNull String key, @NotNull String... args) {
        return langManager.getMessage(viewer, key, args);
    }

    /**
     * 메시지 전송 (간편 메소드)
     */
    protected void sendMessage(@NotNull Player player, @NotNull String key, @NotNull String... args) {
        langManager.sendMessage(player, key, args);
    }

    // 사운드 재생 메소드들

    protected void playClickSound(@NotNull Player player) {
        SoundUtil.playClickSound(player);
    }

    protected void playSuccessSound(@NotNull Player player) {
        SoundUtil.playSuccessSound(player);
    }

    protected void playErrorSound(@NotNull Player player) {
        SoundUtil.playErrorSound(player);
    }

    protected void playOpenSound(@NotNull Player player) {
        SoundUtil.playOpenSound(player);
    }

    protected void playCloseSound(@NotNull Player player) {
        SoundUtil.playCloseSound(player);
    }
}