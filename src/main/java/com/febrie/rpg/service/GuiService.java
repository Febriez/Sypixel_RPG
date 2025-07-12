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

    private final RPGMain plugin;
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
        this.plugin = plugin;
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
            LogUtil.warning("RPGPlayer를 찾을 수 없습니다: " + player.getName());
        }
        return rpgPlayer;
    }

    /**
     * RPGPlayer 가져오기 (없으면 생성)
     */
    @NotNull
    public RPGPlayer getOrCreateRPGPlayer(@NotNull Player player) {
        return playerManager.getOrCreatePlayer(player);
    }

    /**
     * 직업 체크 및 직업 선택 GUI로 리다이렉트
     */
    public boolean checkAndRedirectJob(@NotNull Player player, @NotNull RPGPlayer rpgPlayer) {
        if (!rpgPlayer.hasJob()) {
            JobSelectionGui jobGui = new JobSelectionGui(guiManager, langManager, player, rpgPlayer);
            guiManager.openGui(player, jobGui);

            langManager.sendMessage(player, "messages.no-job-selected");
            playSound(player, errorSound);
            return true; // 리다이렉트됨
        }
        return false; // 직업이 있음
    }

    /**
     * 표준 네비게이션 버튼 생성
     */
    public void setupStandardNavigation(@NotNull BaseGui gui, @NotNull Player viewer, boolean showBack) {
        // 뒤로가기 버튼
        if (showBack && guiManager.canGoBack(viewer)) {
            GuiItem backButton = createBackButton(viewer);
            gui.setItem(BACK_BUTTON_SLOT, backButton);
        }

        // 닫기 버튼
        GuiItem closeButton = createCloseButton(viewer);
        gui.setItem(CLOSE_BUTTON_SLOT, closeButton);
    }

    /**
     * 뒤로가기 버튼 생성 (캐시 활용)
     */
    @NotNull
    public GuiItem createBackButton(@NotNull Player player) {
        String cacheKey = "back_" + langManager.getPlayerLanguage(player);

        return itemCache.computeIfAbsent(cacheKey, k ->
                GuiItem.clickable(
                        ItemBuilder.of(Material.ARROW)
                                .displayName(langManager.getComponent(player, "gui.buttons.back.name"))
                                .addLore(langManager.getComponent(player, "gui.buttons.back.lore"))
                                .build(),
                        p -> {
                            playSound(p, clickSound);
                            guiManager.goBack(p);
                        }
                )
        );
    }

    /**
     * 닫기 버튼 생성 (캐시 활용)
     */
    @NotNull
    public GuiItem createCloseButton(@NotNull Player player) {
        String cacheKey = "close_" + langManager.getPlayerLanguage(player);

        return itemCache.computeIfAbsent(cacheKey, k ->
                GuiItem.clickable(
                        ItemBuilder.of(Material.BARRIER)
                                .displayName(langManager.getComponent(player, "gui.buttons.close.name"))
                                .addLore(langManager.getComponent(player, "gui.buttons.close.lore"))
                                .build(),
                        p -> {
                            playSound(p, clickSound);
                            p.closeInventory();
                        }
                )
        );
    }

    /**
     * 새로고침 버튼 생성
     */
    @NotNull
    public GuiItem createRefreshButton(@NotNull Player player, @NotNull Runnable refreshAction) {
        return GuiItem.clickable(
                ItemBuilder.of(Material.COMPASS)
                        .displayName(langManager.getComponent(player, "gui.buttons.refresh.name"))
                        .addLore(langManager.getComponent(player, "gui.buttons.refresh.lore"))
                        .build(),
                p -> {
                    playSound(p, clickSound);
                    refreshAction.run();
                }
        );
    }

    /**
     * 페이지 네비게이션 버튼 생성
     */
    public void setupPageNavigation(@NotNull BaseGui gui, @NotNull Player viewer,
                                    int currentPage, int totalPages) {
        // 이전 페이지
        if (currentPage > 0) {
            GuiItem prevButton = GuiItem.clickable(
                    ItemBuilder.of(Material.ARROW)
                            .displayName(langManager.getComponent(viewer, "gui.buttons.previous-page.name"))
                            .addLore(langManager.getComponent(viewer, "gui.buttons.previous-page.lore",
                                    "page", String.valueOf(currentPage)))
                            .build(),
                    p -> {
                        playSound(p, clickSound);
                        // 페이지 전환 로직은 각 GUI에서 구현
                    }
            );
            gui.setItem(48, prevButton);
        }

        // 다음 페이지
        if (currentPage < totalPages - 1) {
            GuiItem nextButton = GuiItem.clickable(
                    ItemBuilder.of(Material.ARROW)
                            .displayName(langManager.getComponent(viewer, "gui.buttons.next-page.name"))
                            .addLore(langManager.getComponent(viewer, "gui.buttons.next-page.lore",
                                    "page", String.valueOf(currentPage + 2)))
                            .build(),
                    p -> {
                        playSound(p, clickSound);
                        // 페이지 전환 로직은 각 GUI에서 구현
                    }
            );
            gui.setItem(50, nextButton);
        }

        // 페이지 정보
        GuiItem pageInfo = GuiItem.display(
                ItemBuilder.of(Material.BOOK)
                        .displayName(langManager.getComponent(viewer, "gui.page-info",
                                "current", String.valueOf(currentPage + 1),
                                "total", String.valueOf(totalPages)))
                        .build()
        );
        gui.setItem(49, pageInfo);
    }

    /**
     * 플레이어 정보 아이템 생성
     */
    @NotNull
    public GuiItem createPlayerInfoItem(@NotNull Player player, @NotNull RPGPlayer rpgPlayer) {
        ItemBuilder builder = new ItemBuilder(player)
                .displayName(Component.text(player.getName(), ColorUtil.LEGENDARY))
                .addLore(Component.empty());

        // 레벨 정보
        builder.addLore(langManager.getComponent(player, "gui.player-info.level",
                "level", String.valueOf(rpgPlayer.getLevel())));

        // 직업 정보
        if (rpgPlayer.hasJob()) {
            String jobKey = rpgPlayer.getJob().name().toLowerCase();
            builder.addLore(langManager.getComponent(player, "gui.player-info.job",
                    "job", langManager.getMessage(player, "job." + jobKey + ".name")));
        }

        // 전투력
        builder.addLore(langManager.getComponent(player, "gui.player-info.combat-power",
                "power", String.valueOf(rpgPlayer.getCombatPower())));

        return GuiItem.display(builder.build());
    }

    /**
     * 구분선 생성
     */
    public void createDivider(@NotNull BaseGui gui, int... slots) {
        GuiItem divider = GuiFactory.createDecoration(Material.GRAY_STAINED_GLASS_PANE);
        for (int slot : slots) {
            gui.setItem(slot, divider);
        }
    }

    /**
     * 전체 테두리 생성
     */
    public void createBorder(@NotNull BaseGui gui, @NotNull Material material) {
        GuiItem border = GuiFactory.createDecoration(material);
        int size = gui.getInventory().getSize();
        int rows = size / 9;

        // 상단, 하단
        for (int i = 0; i < 9; i++) {
            gui.setItem(i, border);
            gui.setItem(size - 9 + i, border);
        }

        // 좌측, 우측
        for (int row = 1; row < rows - 1; row++) {
            gui.setItem(row * 9, border);
            gui.setItem(row * 9 + 8, border);
        }
    }

    /**
     * 사운드 재생
     */
    public void playSound(@NotNull Player player, @NotNull Sound sound) {
        player.playSound(player.getLocation(), sound, soundVolume, soundPitch);
    }

    /**
     * 클릭 사운드
     */
    public void playClickSound(@NotNull Player player) {
        playSound(player, clickSound);
    }

    /**
     * 성공 사운드
     */
    public void playSuccessSound(@NotNull Player player) {
        playSound(player, successSound);
    }

    /**
     * 에러 사운드
     */
    public void playErrorSound(@NotNull Player player) {
        playSound(player, errorSound);
    }

    /**
     * 사운드 설정 변경
     */
    public void setSounds(@Nullable Sound click, @Nullable Sound success, @Nullable Sound error) {
        if (click != null) this.clickSound = click;
        if (success != null) this.successSound = success;
        if (error != null) this.errorSound = error;
    }

    /**
     * 사운드 볼륨 설정
     */
    public void setSoundVolume(float volume, float pitch) {
        this.soundVolume = Math.max(0.0f, Math.min(1.0f, volume));
        this.soundPitch = Math.max(0.5f, Math.min(2.0f, pitch));
    }

    /**
     * 캐시 정리
     */
    public void clearCache() {
        itemCache.clear();
        LogUtil.debug("GUI 서비스 캐시가 정리되었습니다.");
    }

    /**
     * GUI 제목 생성 (표준화)
     */
    @NotNull
    public Component createGuiTitle(@NotNull Player player, @NotNull String titleKey) {
        return langManager.getComponent(player, titleKey);
    }

    /**
     * 에러 메시지 표시 및 사운드
     */
    public void showError(@NotNull Player player, @NotNull String messageKey, String... placeholders) {
        langManager.sendMessage(player, messageKey, placeholders);
        playErrorSound(player);
    }

    /**
     * 성공 메시지 표시 및 사운드
     */
    public void showSuccess(@NotNull Player player, @NotNull String messageKey, String... placeholders) {
        langManager.sendMessage(player, messageKey, placeholders);
        playSuccessSound(player);
    }

    /**
     * Lore 리스트에 조건부 추가
     */
    public void addConditionalLore(@NotNull List<Component> lore, boolean condition,
                                   @NotNull Component text) {
        if (condition) {
            lore.add(text);
        }
    }

    /**
     * 빈 슬롯 채우기
     */
    public void fillEmptySlots(@NotNull BaseGui gui, @NotNull Material material) {
        GuiItem filler = GuiFactory.createDecoration(material);
        int size = gui.getInventory().getSize();

        for (int i = 0; i < size; i++) {
            if (gui.getInventory().getItem(i) == null) {
                gui.setItem(i, filler);
            }
        }
    }
}