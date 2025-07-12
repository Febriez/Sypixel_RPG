package com.febrie.rpg.gui.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.LeaderboardEntryDTO;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.ScrollableGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * 리더보드 GUI 구현체
 * 여러 타입의 리더보드를 탭으로 전환하며 볼 수 있는 스크롤 가능한 GUI
 *
 * @author Febrie, CoffeeTory
 */
public class LeaderboardGui extends ScrollableGui {

    private static final int GUI_SIZE = 54; // 6줄

    // 리더보드 타입들
    public enum LeaderboardType {
        LEVEL("level", Material.EXPERIENCE_BOTTLE, ColorUtil.EXPERIENCE, "레벨"),
        COMBAT_POWER("combat_power", Material.DIAMOND_SWORD, ColorUtil.LEGENDARY, "전투력"),
        GOLD("gold", Material.GOLD_INGOT, ColorUtil.GOLD, "골드"),
        TOTAL_PLAYTIME("total_playtime", Material.CLOCK, ColorUtil.AQUA, "플레이타임");

        private final String id;
        private final Material icon;
        private final TextColor color;
        private final String displayName;

        LeaderboardType(String id, Material icon, TextColor color, String displayName) {
            this.id = id;
            this.icon = icon;
            this.color = color;
            this.displayName = displayName;
        }

        public String getId() {
            return id;
        }

        public Material getIcon() {
            return icon;
        }

        public TextColor getColor() {
            return color;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    // 탭 위치 (상단)
    private static final int TAB_START_SLOT = 2;
    private static final Map<Integer, LeaderboardType> TAB_POSITIONS = new HashMap<>();

    static {
        TAB_POSITIONS.put(2, LeaderboardType.LEVEL);
        TAB_POSITIONS.put(3, LeaderboardType.COMBAT_POWER);
        TAB_POSITIONS.put(5, LeaderboardType.GOLD);
        TAB_POSITIONS.put(6, LeaderboardType.TOTAL_PLAYTIME);
    }

    // 자신의 순위 표시 위치
    private static final int MY_RANK_SLOT = 8;
    private static final int LOADING_SLOT = 4;

    private LeaderboardType currentType = LeaderboardType.LEVEL;
    private List<LeaderboardEntryDTO> currentLeaderboard = new ArrayList<>();
    private LeaderboardEntryDTO myRankEntry = null;
    private boolean isLoading = false;

    public LeaderboardGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                          @NotNull Player viewer) {
        this(guiManager, langManager, viewer, LeaderboardType.LEVEL);
    }

    public LeaderboardGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                          @NotNull Player viewer, @NotNull LeaderboardType type) {
        super(viewer, guiManager, langManager, GUI_SIZE, "gui.leaderboard.title",
                "type", type.getDisplayName());
        this.currentType = type;
        setupLayout();
        loadLeaderboard();
    }

    @Override
    public @NotNull Component getTitle() {
        // 동적으로 타이틀 생성
        return trans("gui.leaderboard.title", "type", currentType.getDisplayName());
    }

    @Override
    protected void setupLayout() {
        setupBackground();
        setupTabs();
        setupInfoDisplay();
        setupNavigationButtons();
        // 스크롤 영역은 loadLeaderboard 완료 후 setupScrollableArea에서 설정
    }

    @Override
    protected List<GuiItem> getScrollableItems() {
        List<GuiItem> leaderboardItems = new ArrayList<>();

        if (isLoading) {
            // 로딩 중일 때는 빈 리스트 반환
            return leaderboardItems;
        }

        for (int i = 0; i < currentLeaderboard.size(); i++) {
            LeaderboardEntryDTO entry = currentLeaderboard.get(i);
            leaderboardItems.add(createLeaderboardItem(entry, i + 1));
        }

        return leaderboardItems;
    }

    @Override
    protected void handleNonScrollClick(@NotNull InventoryClickEvent event, @NotNull Player player,
                                        int slot, @NotNull ClickType click) {
        // LEFT_CLICK은 ScrollableGui에서 이미 체크하므로 추가 체크 불필요
        GuiItem item = items.get(slot);
        if (item != null && item.hasActions()) {
            item.executeAction(player, click);
        }
    }

    /**
     * 배경 설정
     */
    private void setupBackground() {
        // 상단 테두리 (탭 위치 제외)
        for (int i = 0; i < 9; i++) {
            if (!TAB_POSITIONS.containsKey(i) && i != LOADING_SLOT && i != MY_RANK_SLOT) {
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 좌우 테두리
        for (int row = 1; row < 5; row++) {
            setItem(row * 9, GuiFactory.createDecoration());
            setItem(row * 9 + 8, GuiFactory.createDecoration());
        }

        // 하단 영역 (네비게이션 제외)
        for (int i = 45; i < 54; i++) {
            if (i != 45 && i != 49 && i != 53) {
                setItem(i, GuiFactory.createDecoration());
            }
        }
    }

    /**
     * 탭 설정
     */
    private void setupTabs() {
        for (Map.Entry<Integer, LeaderboardType> entry : TAB_POSITIONS.entrySet()) {
            int slot = entry.getKey();
            LeaderboardType type = entry.getValue();
            boolean isSelected = type == currentType;

            GuiItem tabItem = GuiItem.clickable(
                    new ItemBuilder(type.getIcon())
                            .displayName(Component.text(type.getDisplayName(), type.getColor())
                                    .decoration(TextDecoration.BOLD, true))
                            .addLore(Component.empty())
                            .addLore(isSelected ?
                                    trans("gui.leaderboard.tab-selected") :
                                    trans("gui.leaderboard.tab-click"))
                            .glint(isSelected)
                            .flags(ItemFlag.values())
                            .build(),
                    clickedPlayer -> switchLeaderboardType(type)
            );

            setItem(slot, tabItem);
        }
    }

    /**
     * 정보 표시 영역
     */
    private void setupInfoDisplay() {
        // 로딩 표시
        if (isLoading) {
            setItem(LOADING_SLOT, GuiItem.display(
                    new ItemBuilder(Material.CLOCK)
                            .displayName(trans("gui.leaderboard.loading"))
                            .addLore(trans("gui.leaderboard.loading-description"))
                            .build()
            ));
        } else {
            setItem(LOADING_SLOT, GuiItem.display(
                    new ItemBuilder(Material.NETHER_STAR)
                            .displayName(trans("gui.leaderboard.title"))
                            .addLore(trans("gui.leaderboard.current-type", "type", currentType.getDisplayName()))
                            .addLore(trans("gui.leaderboard.total-entries", "count", String.valueOf(currentLeaderboard.size())))
                            .glint(true)
                            .build()
            ));
        }

        // 내 순위 표시
        setupMyRankDisplay();
    }

    /**
     * 내 순위 표시
     */
    private void setupMyRankDisplay() {
        if (myRankEntry != null) {
            String rankText = myRankEntry.rank() > 0 ?
                    String.valueOf(myRankEntry.rank()) : "순위권 밖";

            setItem(MY_RANK_SLOT, GuiItem.display(
                    new ItemBuilder(viewer)
                            .displayName(Component.text("내 순위", ColorUtil.SUCCESS)
                                    .decoration(TextDecoration.BOLD, true))
                            .addLore(Component.empty())
                            .addLore(trans("gui.leaderboard.my-rank", "rank", rankText))
                            .addLore(trans("gui.leaderboard.my-value", "value", formatValue(myRankEntry.value())))
                            .addLore(Component.empty())
                            .addLore(trans("gui.leaderboard.last-updated",
                                    "time", formatTime(myRankEntry.lastUpdated())))
                            .build()
            ));
        } else {
            setItem(MY_RANK_SLOT, GuiItem.display(
                    new ItemBuilder(viewer)
                            .displayName(Component.text("내 순위", ColorUtil.GRAY)
                                    .decoration(TextDecoration.BOLD, true))
                            .addLore(Component.empty())
                            .addLore(trans("gui.leaderboard.no-rank-data"))
                            .addLore(trans("gui.leaderboard.play-more"))
                            .build()
            ));
        }
    }

    /**
     * 네비게이션 버튼 설정
     */
    private void setupNavigationButtons() {
        // 뒤로가기 버튼
        if (guiManager.canGoBack(viewer)) {
            setItem(45, GuiItem.clickable(
                    new ItemBuilder(Material.ARROW)
                            .displayName(trans("gui.buttons.back.name"))
                            .addLore(trans("gui.buttons.back.lore"))
                            .build(),
                    guiManager::goBack
            ));
        }

        // 새로고침 버튼
        setItem(49, GuiFactory.createRefreshButton(player -> {
            loadLeaderboard();
            playClickSound(player);
        }, langManager, viewer));

        // 닫기 버튼
        setItem(53, GuiFactory.createCloseButton(langManager, viewer));
    }

    /**
     * 리더보드 타입 전환
     */
    private void switchLeaderboardType(@NotNull LeaderboardType newType) {
        if (newType == currentType) {
            playClickSound(viewer);
            return;
        }

        // 새로운 타입으로 GUI 재생성 및 열기
        LeaderboardGui newGui = new LeaderboardGui(guiManager, langManager, viewer, newType);
        guiManager.openGui(viewer, newGui);
        playClickSound(viewer);
    }

    /**
     * 리더보드 로드
     */
    private void loadLeaderboard() {
        if (isLoading) return;

        isLoading = true;
        setupInfoDisplay(); // 로딩 표시 업데이트

        // 현재 플레이어의 해당 타입 값 수집
        RPGPlayer rpgPlayer = RPGMain.getPlugin().getRPGPlayerManager().getPlayer(viewer);
        long myValue = 0;
        if (rpgPlayer != null) {
            myValue = switch (currentType) {
                case LEVEL -> rpgPlayer.getLevel();
                case COMBAT_POWER -> rpgPlayer.getCombatPower();
                case GOLD -> rpgPlayer.getWallet().getBalance(com.febrie.rpg.economy.CurrencyType.GOLD);
                case TOTAL_PLAYTIME -> rpgPlayer.getTotalPlaytime();
            };
        }

        // 내 순위 엔트리 생성 (임시)
        if (rpgPlayer != null) {
            myRankEntry = new LeaderboardEntryDTO(
                    viewer.getUniqueId().toString(),
                    viewer.getName(),
                    0, // 순위는 나중에 계산
                    myValue,
                    currentType.getId()
            );
        }

        // Firebase에서 리더보드 로드
        CompletableFuture<List<LeaderboardEntryDTO>> leaderboardFuture =
                RPGMain.getPlugin().getFirebaseService().loadLeaderboard(currentType.getId(), 50);

        // 내 순위 로드
        CompletableFuture<LeaderboardEntryDTO> myRankFuture =
                RPGMain.getPlugin().getFirebaseService().loadPlayerLeaderboardEntry(
                        viewer.getUniqueId().toString(), currentType.getId());

        // 두 Future 모두 완료되면 UI 업데이트
        CompletableFuture.allOf(leaderboardFuture, myRankFuture).thenRun(() -> {
            try {
                currentLeaderboard = leaderboardFuture.get();
                LeaderboardEntryDTO serverMyRank = myRankFuture.get();

                if (serverMyRank != null) {
                    myRankEntry = serverMyRank;
                }

                // 메인 스레드에서 UI 업데이트
                Bukkit.getScheduler().runTask(RPGMain.getPlugin(), () -> {
                    isLoading = false;
                    refresh();
                });
            } catch (Exception e) {
                Bukkit.getScheduler().runTask(RPGMain.getPlugin(), () -> {
                    isLoading = false;
                    refresh();
                    sendMessage(viewer, "gui.leaderboard.load-failed");
                    playErrorSound(viewer);
                });
            }
        });
    }

    /**
     * 리더보드 아이템 생성
     */
    private GuiItem createLeaderboardItem(@NotNull LeaderboardEntryDTO entry, int displayRank) {
        boolean isMyself = entry.playerUuid().equals(viewer.getUniqueId().toString());

        // 순위에 따른 색상 결정
        TextColor rankColor = getRankColor(entry.rank());

        // 플레이어 머리 아이템 생성
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(entry.playerUuid()));

        ItemBuilder builder = new ItemBuilder(Bukkit.getPlayer(offlinePlayer.getUniqueId()))
                .displayName(Component.text(entry.rank() + "등 ", rankColor)
                        .append(Component.text(entry.playerName(),
                                isMyself ? ColorUtil.SUCCESS : ColorUtil.WHITE))
                        .decoration(TextDecoration.BOLD, true))
                .addLore(Component.empty())
                .addLore(trans("gui.leaderboard.rank-info", "rank", String.valueOf(entry.rank())))
                .addLore(trans("gui.leaderboard.value-info",
                        "type", currentType.getDisplayName(),
                        "value", formatValue(entry.value())))
                .addLore(Component.empty());

        if (isMyself) {
            builder.addLore(trans("gui.leaderboard.this-is-you"))
                    .glint(true);
        }

        builder.addLore(trans("gui.leaderboard.last-updated",
                        "time", formatTime(entry.lastUpdated())))
                .flags(ItemFlag.values());

        return GuiItem.display(builder.build());
    }

    /**
     * 순위에 따른 색상 결정
     */
    private TextColor getRankColor(int rank) {
        return switch (rank) {
            case 1 -> ColorUtil.LEGENDARY; // 1등 - 금색
            case 2 -> ColorUtil.IRON;      // 2등 - 은색
            case 3 -> ColorUtil.COPPER;    // 3등 - 동색
            default -> rank <= 10 ? ColorUtil.RARE : ColorUtil.COMMON; // 10등 이내 파란색, 그 외 회색
        };
    }

    /**
     * 값 포맷팅
     */
    private String formatValue(long value) {
        return switch (currentType) {
            case LEVEL -> String.valueOf(value);
            case COMBAT_POWER -> String.format("%,d", value);
            case GOLD -> String.format("%,d 골드", value);
            case TOTAL_PLAYTIME -> formatPlaytime(value);
        };
    }

    /**
     * 플레이타임 포맷팅
     */
    private String formatPlaytime(long milliseconds) {
        long hours = milliseconds / (1000 * 60 * 60);
        long minutes = (milliseconds % (1000 * 60 * 60)) / (1000 * 60);

        if (hours > 0) {
            return String.format("%d시간 %d분", hours, minutes);
        } else {
            return String.format("%d분", minutes);
        }
    }

    /**
     * 시간 포맷팅
     */
    private String formatTime(long timestamp) {
        long now = System.currentTimeMillis();
        long diff = now - timestamp;

        if (diff < 60 * 1000) { // 1분 이내
            return "방금 전";
        } else if (diff < 60 * 60 * 1000) { // 1시간 이내
            return (diff / (60 * 1000)) + "분 전";
        } else if (diff < 24 * 60 * 60 * 1000) { // 24시간 이내
            return (diff / (60 * 60 * 1000)) + "시간 전";
        } else { // 하루 이상
            return (diff / (24 * 60 * 60 * 1000)) + "일 전";
        }
    }

    /**
     * 현재 리더보드 타입 반환
     */
    public LeaderboardType getCurrentType() {
        return currentType;
    }

    @Override
    public void refresh() {
        inventory.clear();
        items.clear();
        setupLayout();

        // 스크롤 영역 재설정
        if (!isLoading) {
            setupScrollableArea(inventory, items, this::setItem);
        }
    }
}