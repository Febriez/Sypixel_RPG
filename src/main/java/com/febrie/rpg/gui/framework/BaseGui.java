package com.febrie.rpg.gui.framework;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.PlayerSettings;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.SoundUtil;
import com.febrie.rpg.util.ColorUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
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
    
    // 편의용 plugin 접근자
    protected final com.febrie.rpg.RPGMain plugin;

    // 네비게이션 버튼 동적 위치 계산을 위한 메소드
    protected int getBackButtonSlot() {
        return size - 6;  // 하단 중앙에서 왼쪽 (마지막 줄의 3번째 슬롯)
    }
    
    protected int getCloseButtonSlot() {
        return size - 5;  // 하단 중앙 (마지막 줄의 4번째 슬롯)
    }
    
    protected int getRefreshButtonSlot() {
        return size - 4;  // 하단 중앙에서 오른쪽 (마지막 줄의 5번째 슬롯)
    }
    
    // 레거시 지원을 위한 상수 (점진적 마이그레이션용)
    protected static final int BACK_BUTTON_SLOT = 48;    // 6줄 GUI 기준
    protected static final int CLOSE_BUTTON_SLOT = 49;   // 6줄 GUI 기준
    protected static final int REFRESH_BUTTON_SLOT = 50; // 6줄 GUI 기준

    /**
     * 생성자
     */
    public BaseGui(@NotNull Player viewer, @NotNull GuiManager guiManager,
                   @NotNull LangManager langManager, int requestedSize,
                   @NotNull String titleKey, @NotNull String... titleArgs) {
        this.viewer = viewer;
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.plugin = guiManager.getPlugin();
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
     * GUI 타이틀 색상과 스타일을 가져옴 - 하위 클래스에서 오버라이드 가능
     * 기본값: #4297FF 색상에 볼드 적용
     */
    protected @NotNull Component applyTitleStyle(@NotNull Component title) {
        return title.color(ColorUtil.GUI_TITLE).decorate(TextDecoration.BOLD);
    }

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
        Component styledTitle = applyTitleStyle(title);
        return Bukkit.createInventory(this, size, styledTitle);
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
        // 기본적으로는 GUI 열기 소리 재생하지 않음
        // 필요한 경우 하위 클래스에서 오버라이드
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
     * 아이템 가져오기 - GuiFramework에 없는 자체 메소드
     */
    public GuiItem getItem(int slot) {
        return items.get(slot);
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
            setItem(getRefreshButtonSlot(), GuiFactory.createRefreshButton(() -> {
                refresh();
                playClickSound(viewer);
            }, langManager, viewer));
        }

        // 닫기 버튼
        if (includeClose) {
            setItem(getCloseButtonSlot(), GuiFactory.createCloseButton(langManager, viewer));
        }
    }

    /**
     * 뒤로가기 대상 GUI 반환 - 하위 클래스에서 구현
     * null을 반환하면 뒤로가기 버튼이 표시되지 않음
     */
    protected abstract GuiFramework getBackTarget();
    
    /**
     * 네비게이션 버튼 업데이트
     */
    public void updateNavigationButtons() {
        GuiFramework backTarget = getBackTarget();
        if (backTarget != null) {
            setItem(getBackButtonSlot(), createBackButton(backTarget));
        } else {
            // 뒤로가기 불가능한 경우 장식 아이템
            setItem(getBackButtonSlot(), GuiFactory.createDecoration());
        }
    }
    
    /**
     * 뒤로가기 버튼 생성
     */
    private GuiItem createBackButton(GuiFramework backTarget) {
        return GuiFactory.createBackButton(p -> {
            guiManager.openGui(p, backTarget);
            playBackSound(p);
        }, langManager, viewer);
    }
    
    /**
     * 뒤로가기 소리 재생 - 오버라이드 가능
     */
    protected void playBackSound(@NotNull Player player) {
        SoundUtil.playPageTurnSound(player);
    }

    /**
     * 경계선 생성
     */
    protected void createBorder() {
        GuiItem borderItem = GuiFactory.createDecoration();

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
        RPGPlayer rpgPlayer = RPGMain.getPlugin().getRPGPlayerManager().getOrCreatePlayer(player);
        PlayerSettings settings = rpgPlayer.getPlayerSettings();
        
        if (!settings.isGuiSoundMuted()) {
            float volume = settings.getGuiSoundVolume() / 100.0f * 0.3f; // 0.3f is base volume
            SoundUtil.playLeverSound(player, volume);
        }
    }

    protected void playSuccessSound(@NotNull Player player) {
        RPGPlayer rpgPlayer = RPGMain.getPlugin().getRPGPlayerManager().getOrCreatePlayer(player);
        PlayerSettings settings = rpgPlayer.getPlayerSettings();
        
        if (!settings.isGuiSoundMuted()) {
            float volume = settings.getGuiSoundVolume() / 100.0f * 0.5f; // 0.5f is base volume
            SoundUtil.playSuccessSound(player, volume);
        }
    }

    protected void playErrorSound(@NotNull Player player) {
        RPGPlayer rpgPlayer = RPGMain.getPlugin().getRPGPlayerManager().getOrCreatePlayer(player);
        PlayerSettings settings = rpgPlayer.getPlayerSettings();
        
        if (!settings.isGuiSoundMuted()) {
            float volume = settings.getGuiSoundVolume() / 100.0f * 0.5f; // 0.5f is base volume
            SoundUtil.playErrorSound(player, volume);
        }
    }

    protected void playOpenSound(@NotNull Player player) {
        RPGPlayer rpgPlayer = RPGMain.getPlugin().getRPGPlayerManager().getOrCreatePlayer(player);
        PlayerSettings settings = rpgPlayer.getPlayerSettings();
        
        if (!settings.isGuiSoundMuted()) {
            float volume = settings.getGuiSoundVolume() / 100.0f * 0.5f; // 0.5f is base volume
            SoundUtil.playOpenSound(player, volume);
        }
    }

    protected void playCloseSound(@NotNull Player player) {
        RPGPlayer rpgPlayer = RPGMain.getPlugin().getRPGPlayerManager().getOrCreatePlayer(player);
        PlayerSettings settings = rpgPlayer.getPlayerSettings();
        
        if (!settings.isGuiSoundMuted()) {
            float volume = settings.getGuiSoundVolume() / 100.0f * 0.5f; // 0.5f is base volume
            SoundUtil.playCloseSound(player, volume);
        }
    }
}