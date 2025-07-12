package com.febrie.rpg.service;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.impl.JobSelectionGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LogUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * GUI 관련 공통 로직 및 유틸리티 서비스
 * 중복 코드 제거 및 GUI 생성 표준화
 *
 * @author Febrie, CoffeeTory
 */
public class GuiService {

    private final GuiManager guiManager;
    private final LangManager langManager;
    private final RPGPlayerManager playerManager;

    // GUI 사운드 설정 (설정 가능하도록)
    private Sound clickSound = Sound.UI_BUTTON_CLICK;
    private Sound successSound = Sound.ENTITY_PLAYER_LEVELUP;
    private Sound errorSound = Sound.ENTITY_VILLAGER_NO;
    private float soundVolume = 0.5f;
    private float soundPitch = 1.0f;

    // 공통 슬롯 위치 상수
    public static final int BACK_BUTTON_SLOT = 45;
    public static final int CLOSE_BUTTON_SLOT = 53;
    public static final int REFRESH_BUTTON_SLOT = 49;

    // 캐시 (자주 사용되는 아이템)
    private final ConcurrentHashMap<String, GuiItem> itemCache = new ConcurrentHashMap<>();

    public GuiService(@NotNull RPGMain plugin) {
        this.guiManager = plugin.getGuiManager();
        this.langManager = plugin.getLangManager();
        this.playerManager = plugin.getRPGPlayerManager();
    }

    /**
     * RPGPlayer 가져오기 (null 체크 포함)
     */
    @Nullable
    public RPGPlayer getRPGPlayer(@NotNull Player player) {
        RPGPlayer rpgPlayer = playerManager.getPlayer(player);
        if (rpgPlayer == null) {
            LogUtil.warning("RPGPlayer not found for " + player.getName());
        }
        return rpgPlayer;
    }

    /**
     * 직업 선택 필요 체크
     */
    public boolean requiresJobSelection(@NotNull Player player) {
        RPGPlayer rpgPlayer = getRPGPlayer(player);
        if (rpgPlayer == null) {
            rpgPlayer = playerManager.getOrCreatePlayer(player);
        }

        if (!rpgPlayer.hasJob()) {
            // 직업 선택 GUI로 이동
            guiManager.openGui(player, new JobSelectionGui(guiManager, langManager, player, rpgPlayer));
            playErrorSound(player);
            langManager.sendMessage(player, "messages.no-job-for-" +
                    (player.hasPermission("rpg.talent") ? "talents" : "stats"));
            return true;
        }
        return false;
    }

    /**
     * 공통 뒤로가기 버튼 생성
     */
    @NotNull
    public GuiItem createBackButton() {
        return getCachedItem("back_button", () ->
                GuiItem.clickable(
                        new ItemBuilder(Material.ARROW)
                                .displayName(langManager.getComponent("ko_KR", "gui.buttons.back.name"))
                                .addLore(langManager.getComponentList("ko_KR", "gui.buttons.back.lore"))
                                .build(),
                        player -> {
                            playClickSound(player);
                            guiManager.goBack(player);
                        }
                )
        );
    }

    /**
     * 플레이어별 뒤로가기 버튼 생성
     */
    @NotNull
    public GuiItem createBackButton(@NotNull Player player) {
        return GuiItem.clickable(
                new ItemBuilder(Material.ARROW)
                        .displayName(langManager.getComponent(player, "gui.buttons.back.name"))
                        .addLore(langManager.getComponentList(player, "gui.buttons.back.lore"))
                        .build(),
                p -> {
                    playClickSound(p);
                    guiManager.goBack(p);
                }
        );
    }

    /**
     * 공통 닫기 버튼 생성
     */
    @NotNull
    public GuiItem createCloseButton() {
        return getCachedItem("close_button", () ->
                GuiItem.clickable(
                        new ItemBuilder(Material.BARRIER)
                                .displayName(langManager.getComponent("ko_KR", "gui.buttons.close.name"))
                                .addLore(langManager.getComponentList("ko_KR", "gui.buttons.close.lore"))
                                .build(),
                        player -> {
                            playClickSound(player);
                            player.closeInventory();
                        }
                )
        );
    }

    /**
     * 플레이어별 닫기 버튼 생성
     */
    @NotNull
    public GuiItem createCloseButton(@NotNull Player player) {
        return GuiItem.clickable(
                new ItemBuilder(Material.BARRIER)
                        .displayName(langManager.getComponent(player, "gui.buttons.close.name"))
                        .addLore(langManager.getComponentList(player, "gui.buttons.close.lore"))
                        .build(),
                p -> {
                    playClickSound(p);
                    p.closeInventory();
                }
        );
    }

    /**
     * 공통 새로고침 버튼 생성
     */
    @NotNull
    public GuiItem createRefreshButton(@NotNull Runnable refreshAction) {
        return GuiItem.clickable(
                new ItemBuilder(Material.COMPASS)
                        .displayName(langManager.getComponent("ko_KR", "gui.buttons.refresh.name"))
                        .addLore(langManager.getComponentList("ko_KR", "gui.buttons.refresh.lore"))
                        .build(),
                player -> {
                    playClickSound(player);
                    refreshAction.run();
                    langManager.sendMessage(player, "messages.gui-refreshed");
                }
        );
    }

    /**
     * 플레이어별 새로고침 버튼 생성
     */
    @NotNull
    public GuiItem createRefreshButton(@NotNull Player player, @NotNull Runnable refreshAction) {
        return GuiItem.clickable(
                new ItemBuilder(Material.COMPASS)
                        .displayName(langManager.getComponent(player, "gui.buttons.refresh.name"))
                        .addLore(langManager.getComponentList(player, "gui.buttons.refresh.lore"))
                        .build(),
                p -> {
                    playClickSound(p);
                    refreshAction.run();
                    langManager.sendMessage(p, "messages.gui-refreshed");
                }
        );
    }

    /**
     * 페이지 정보 아이템 생성
     */
    @NotNull
    public GuiItem createPageInfo(@NotNull Player player, int currentPage, int totalPages) {
        return GuiItem.display(
                new ItemBuilder(Material.BOOK)
                        .displayName(langManager.getComponent(player, "gui.buttons.page-info.name"))
                        .addLore(langManager.getComponentList(player, "gui.buttons.page-info.lore",
                                "current", String.valueOf(currentPage),
                                "total", String.valueOf(totalPages)))
                        .build()
        );
    }

    /**
     * 이전 페이지 버튼 생성
     */
    @NotNull
    public GuiItem createPreviousPageButton(@NotNull Player player, boolean enabled, @NotNull Runnable action) {
        Material material = enabled ? Material.SPECTRAL_ARROW : Material.GRAY_DYE;

        GuiItem item = new GuiItem(
                new ItemBuilder(material)
                        .displayName(langManager.getComponent(player, "gui.buttons.previous-page.name"))
                        .addLore(langManager.getComponentList(player, "gui.buttons.previous-page.lore"))
                        .build()
        );

        if (enabled) {
            item.onAnyClick(p -> {
                playClickSound(p);
                action.run();
            });
        } else {
            item.onAnyClick(this::playErrorSound);
        }

        return item;
    }

    /**
     * 다음 페이지 버튼 생성
     */
    @NotNull
    public GuiItem createNextPageButton(@NotNull Player player, boolean enabled, @NotNull Runnable action) {
        Material material = enabled ? Material.SPECTRAL_ARROW : Material.GRAY_DYE;

        GuiItem item = new GuiItem(
                new ItemBuilder(material)
                        .displayName(langManager.getComponent(player, "gui.buttons.next-page.name"))
                        .addLore(langManager.getComponentList(player, "gui.buttons.next-page.lore"))
                        .build()
        );

        if (enabled) {
            item.onAnyClick(p -> {
                playClickSound(p);
                action.run();
            });
        } else {
            item.onAnyClick(this::playErrorSound);
        }

        return item;
    }

    /**
     * GUI에 기본 버튼들 설정
     */
    public void setupCommonButtons(@NotNull BaseGui gui, boolean includeRefresh) {
        Player viewer = gui.getViewer();
        if (viewer == null) return;

        // 뒤로가기 버튼
        setGuiItem(gui, BACK_BUTTON_SLOT, createBackButton(viewer));

        // 닫기 버튼
        setGuiItem(gui, CLOSE_BUTTON_SLOT, createCloseButton(viewer));

        // 새로고침 버튼 (선택적)
        if (includeRefresh) {
            // GUI 새로고침
            setGuiItem(gui, REFRESH_BUTTON_SLOT, createRefreshButton(viewer, gui::refresh));
        }
    }

    /**
     * GUI 테두리 설정
     */
    public void setupBorder(@NotNull BaseGui gui) {
        GuiItem borderItem = GuiFactory.createDecoration();

        // 상단 테두리
        for (int i = 0; i < 9; i++) {
            setGuiItem(gui, i, borderItem);
        }

        // 하단 테두리
        for (int i = 45; i < 54; i++) {
            // 버튼 슬롯은 제외
            if (i != BACK_BUTTON_SLOT && i != CLOSE_BUTTON_SLOT && i != REFRESH_BUTTON_SLOT) {
                setGuiItem(gui, i, borderItem);
            }
        }

        // 좌우 테두리
        for (int row = 1; row < 5; row++) {
            setGuiItem(gui, row * 9, borderItem);
            setGuiItem(gui, row * 9 + 8, borderItem);
        }
    }

    /**
     * 확인/취소 다이얼로그 생성
     */
    public void showConfirmDialog(@NotNull Player player,
                                  @NotNull Component title,
                                  @NotNull List<Component> description,
                                  @NotNull Runnable onConfirm,
                                  @NotNull Runnable onCancel) {
        // ConfirmationGui 구현 필요
        // 임시로 채팅 메시지로 처리
        player.sendMessage(title);
        description.forEach(player::sendMessage);
        player.sendMessage(Component.text("이 작업을 계속하시겠습니까? (Y/N)", ColorUtil.WARNING));

        // 실제 구현시 별도의 ConfirmationGui 클래스 필요
    }

    /**
     * 오류 메시지 표시
     */
    public void showError(@NotNull Player player, @NotNull String messageKey, @NotNull String... placeholders) {
        playErrorSound(player);
        langManager.sendMessage(player, messageKey, placeholders);
    }

    /**
     * 성공 메시지 표시
     */
    public void showSuccess(@NotNull Player player, @NotNull String messageKey, @NotNull String... placeholders) {
        playSuccessSound(player);
        langManager.sendMessage(player, messageKey, placeholders);
    }

    /**
     * 클릭 사운드 재생
     */
    public void playClickSound(@NotNull Player player) {
        player.playSound(player.getLocation(), clickSound, soundVolume, soundPitch);
    }

    /**
     * 성공 사운드 재생
     */
    public void playSuccessSound(@NotNull Player player) {
        player.playSound(player.getLocation(), successSound, soundVolume, soundPitch);
    }

    /**
     * 오류 사운드 재생
     */
    public void playErrorSound(@NotNull Player player) {
        player.playSound(player.getLocation(), errorSound, soundVolume, soundPitch);
    }

    /**
     * 캐시된 아이템 가져오기
     */
    @NotNull
    private GuiItem getCachedItem(@NotNull String key, @NotNull java.util.function.Supplier<GuiItem> supplier) {
        return itemCache.computeIfAbsent(key, k -> supplier.get());
    }

    /**
     * 캐시 초기화
     */
    public void clearCache() {
        itemCache.clear();
    }

    /**
     * 사운드 설정 변경
     */
    public void setSounds(@NotNull Sound click, @NotNull Sound success, @NotNull Sound error) {
        this.clickSound = click;
        this.successSound = success;
        this.errorSound = error;
    }

    /**
     * 사운드 볼륨 설정
     */
    public void setSoundVolume(float volume) {
        this.soundVolume = Math.max(0f, Math.min(1f, volume));
    }

    /**
     * 사운드 피치 설정
     */
    public void setSoundPitch(float pitch) {
        this.soundPitch = Math.max(0.5f, Math.min(2f, pitch));
    }

    /**
     * BaseGui에 아이템 설정
     */
    private void setGuiItem(@NotNull BaseGui gui, int slot, @NotNull GuiItem item) {
        gui.setGuiItem(slot, item);
    }

    /**
     * 아이템 설명 포맷팅 유틸리티
     */
    @NotNull
    public List<Component> formatItemDescription(@NotNull Player player, @NotNull String descriptionKey, @NotNull String... placeholders) {
        List<Component> formatted = langManager.getComponentList(
                player, descriptionKey, placeholders
        );

        // 빈 줄 추가
        formatted.addFirst(Component.empty());
        formatted.add(Component.empty());

        return formatted;
    }

    /**
     * 진행도 바 생성
     */
    @NotNull
    public Component createProgressBar(double progress, int length, char filled, char empty) {
        int filledLength = (int) (progress * length);

        StringBuilder bar = new StringBuilder();
        for (int i = 0; i < length; i++) {
            if (i < filledLength) {
                bar.append(filled);
            } else {
                bar.append(empty);
            }
        }

        return Component.text(bar.toString())
                .color(progress >= 1.0 ? ColorUtil.SUCCESS :
                        progress >= 0.5 ? ColorUtil.WARNING :
                                ColorUtil.ERROR);
    }

    /**
     * 숫자 포맷팅 (천 단위 구분)
     */
    @NotNull
    public String formatNumber(long number) {
        return String.format("%,d", number);
    }

    /**
     * 시간 포맷팅 (밀리초 -> 읽기 쉬운 형식)
     */
    @NotNull
    public String formatTime(long milliseconds) {
        long seconds = milliseconds / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return String.format("%d일 %d시간", days, hours % 24);
        } else if (hours > 0) {
            return String.format("%d시간 %d분", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d분 %d초", minutes, seconds % 60);
        } else {
            return String.format("%d초", seconds);
        }
    }

    /**
     * 퍼센트 포맷팅
     */
    @NotNull
    public String formatPercent(double value) {
        return String.format("%.1f%%", value * 100);
    }

    /**
     * 체력바 생성
     */
    @NotNull
    public Component createHealthBar(double current, double max) {
        double ratio = current / max;
        return createProgressBar(ratio, 20, '█', '░');
    }

    /**
     * 경험치바 생성
     */
    @NotNull
    public Component createExpBar(double progress) {
        return createProgressBar(progress, 30, '■', '□');
    }

    /**
     * 공통 GUI 패턴 적용 (테두리 + 기본 버튼)
     */
    public void applyCommonPattern(@NotNull BaseGui gui, boolean includeRefresh) {
        setupBorder(gui);
        setupCommonButtons(gui, includeRefresh);
    }

    /**
     * 플레이어 정보 아이템 생성
     */
    @NotNull
    public GuiItem createPlayerInfoItem(@NotNull Player player, @NotNull RPGPlayer rpgPlayer) {
        ItemBuilder builder = ItemBuilder.of(Material.PLAYER_HEAD)
                .skull(player)
                .displayName(Component.text(player.getName(), ColorUtil.LEGENDARY))
                .addLore(Component.empty())
                .addLore(langManager.getComponent(player, "gui.items.profile.level-info.lore",
                        "level", String.valueOf(rpgPlayer.getLevel()),
                        "exp", formatPercent(rpgPlayer.getLevelProgress()),
                        "total_exp", formatNumber(rpgPlayer.getExperience())))
                .addLore(Component.empty());

        if (rpgPlayer.hasJob()) {
            builder.addLore(langManager.getComponent(player, "status.job", "job",
                    langManager.getMessage(player, "job." + rpgPlayer.getJob().name().toLowerCase() + ".name")));
        } else {
            builder.addLore(langManager.getComponent(player, "status.no-job"));
        }

        return GuiItem.display(builder.build());
    }

    /**
     * 잠긴 아이템 생성
     */
    @NotNull
    public GuiItem createLockedItem(@NotNull Player player, @NotNull Material material, @NotNull String nameKey, @NotNull String reason) {
        return GuiItem.display(
                new ItemBuilder(material)
                        .displayName(langManager.getComponent(player, nameKey))
                        .addLore(Component.empty())
                        .addLore(Component.text("🔒 잠김", ColorUtil.ERROR))
                        .addLore(Component.text(reason, ColorUtil.GRAY))
                        .build()
        );
    }

    /**
     * 곧 출시 아이템
     */
    @NotNull
    public GuiItem createComingSoonItem(@NotNull Player player, @NotNull Material material, @NotNull String nameKey) {
        String lang = langManager.getPlayerLanguage(player);
        return getCachedItem("coming_soon_" + material.name() + "_" + lang, () ->
                GuiItem.display(
                        new ItemBuilder(material)
                                .displayName(langManager.getComponent(player, nameKey))
                                .addLore(Component.empty())
                                .addLore(Component.text("🚧 곧 출시 예정!", ColorUtil.WARNING))
                                .addLore(Component.text("다음 업데이트를 기대해주세요", ColorUtil.GRAY))
                                .build()
                )
        );
    }

    /**
     * GUI 타입별 적절한 크기 계산
     */
    public int calculateGuiSize(int itemCount) {
        // 9개씩 한 줄, 최소 9칸, 최대 54칸
        int rows = Math.max(1, Math.min(6, (itemCount + 8) / 9));
        return rows * 9;
    }

    /**
     * 아이템 배치 위치 계산 (중앙 정렬)
     */
    public int[] calculateCenteredSlots(int itemCount, int guiSize) {
        int[] slots = new int[itemCount];
        int rows = guiSize / 9;
        int itemsPerRow = Math.min(7, itemCount); // 좌우 1칸 여백
        int startRow = (rows - ((itemCount + itemsPerRow - 1) / itemsPerRow)) / 2;

        for (int i = 0; i < itemCount; i++) {
            int row = startRow + (i / itemsPerRow);
            int col = 1 + ((itemsPerRow - Math.min(itemsPerRow, itemCount - (i / itemsPerRow) * itemsPerRow)) / 2) + (i % itemsPerRow);
            slots[i] = row * 9 + col;
        }

        return slots;
    }

    /**
     * 재화 포맷팅
     */
    @NotNull
    public String formatCurrency(long amount) {
        if (amount >= 1_000_000_000) {
            return String.format("%.1fB", amount / 1_000_000_000.0);
        } else if (amount >= 1_000_000) {
            return String.format("%.1fM", amount / 1_000_000.0);
        } else if (amount >= 1_000) {
            return String.format("%.1fK", amount / 1_000.0);
        } else {
            return String.valueOf(amount);
        }
    }
}