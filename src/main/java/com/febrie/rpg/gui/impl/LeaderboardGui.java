package com.febrie.rpg.gui.impl;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.LeaderboardEntryDTO;
import com.febrie.rpg.economy.CurrencyType;
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
 * 리더보드 GUI - 올바르게 수정된 버전
 * 여러 타입의 리더보드를 탭으로 전환하며 볼 수 있는 GUI
 *
 * @author Febrie, CoffeeTory
 */
public class LeaderboardGui extends ScrollableGui {

    private static final int GUI_SIZE = 54; // 6줄

    // 리더보드 타입
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

    // 탭 위치
    private static final Map<Integer, LeaderboardType> TAB_POSITIONS = new HashMap<>();

    static {
        TAB_POSITIONS.put(2, LeaderboardType.LEVEL);
        TAB_POSITIONS.put(3, LeaderboardType.COMBAT_POWER);
        TAB_POSITIONS.put(5, LeaderboardType.GOLD);
        TAB_POSITIONS.put(6, LeaderboardType.TOTAL_PLAYTIME);
    }

    // 특수 슬롯
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
        return trans("gui.leaderboard.title", "type", currentType.getDisplayName());
    }

    @Override
    protected void setupLayout() {
        setupBackground();
        setupTabs();
        setupInfoDisplay();
        setupNavigationButtons();
    }

    @Override
    protected List<GuiItem> getScrollableItems() {
        if (isLoading) {
            return new ArrayList<>();
        }

        List<GuiItem> leaderboardItems = new ArrayList<>();
        for (int i = 0; i < currentLeaderboard.size(); i++) {
            LeaderboardEntryDTO entry = currentLeaderboard.get(i);
            leaderboardItems.add(createLeaderboardItem(entry, i + 1));
        }
        return leaderboardItems;
    }

    @Override
    protected void handleNonScrollClick(@NotNull InventoryClickEvent event, @NotNull Player player,
                                        int slot, @NotNull ClickType click) {
        GuiItem item = items.get(slot);
        if (item != null && item.hasActions()) {
            item.executeAction(player, click);
        }
    }

    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT);
    }

    /**
     * 배경 설정
     */
    private void setupBackground() {
        // 상단 테두리 (탭 제외)
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

        // 하단 영역
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
        TAB_POSITIONS.forEach((slot, type) -> {
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
                    player -> switchLeaderboardType(type)
            );
            setItem(slot, tabItem);
        });
    }

    /**
     * 정보 표시
     */
    private void setupInfoDisplay() {
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
                            .displayName(trans("gui.leaderboard.title", "type", currentType.getDisplayName()))
                            .addLore(trans("gui.leaderboard.current-type", "type", currentType.getDisplayName()))
                            .addLore(trans("gui.leaderboard.total-entries", "count", String.valueOf(currentLeaderboard.size())))
                            .glint(true)
                            .build()
            ));
        }

        setupMyRankDisplay();
    }

    /**
     * 내 순위 표시
     */
    private void setupMyRankDisplay() {
        if (myRankEntry != null && myRankEntry.rank() > 0) {
            setItem(MY_RANK_SLOT, GuiItem.display(
                    new ItemBuilder(viewer)
                            .displayName(Component.text("내 순위", ColorUtil.SUCCESS)
                                    .decoration(TextDecoration.BOLD, true))
                            .addLore(Component.empty())
                            .addLore(trans("gui.leaderboard.my-rank", "rank", String.valueOf(myRankEntry.rank())))
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
        if (guiManager.canNavigateBack(viewer)) {
            setItem(45, GuiItem.clickable(
                    new ItemBuilder(Material.ARROW)
                            .displayName(trans("gui.buttons.back.name"))
                            .addLore(trans("gui.buttons.back.lore"))
                            .build(),
                    guiManager::navigateBack
            ));
        }

        // 닫기 버튼
        setItem(53, GuiFactory.createCloseButton(langManager, viewer));
    }

    /**
     * 리더보드 타입 전환
     */
    private void switchLeaderboardType(@NotNull LeaderboardType type) {
        if (type == currentType || isLoading) {
            return;
        }

        currentType = type;
        currentLeaderboard.clear();
        myRankEntry = null;

        refresh();
        loadLeaderboard();
        playClickSound(viewer);
    }

    /**
     * 리더보드 데이터 로드
     */
    private void loadLeaderboard() {
        isLoading = true;
        setupInfoDisplay();

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
                } else {
                    // 서버에 없으면 현재 데이터로 생성
                    RPGPlayer rpgPlayer = RPGMain.getPlugin().getRPGPlayerManager().getPlayer(viewer);
                    if (rpgPlayer != null) {
                        long value = getValueForType(rpgPlayer, currentType);
                        myRankEntry = new LeaderboardEntryDTO(
                                viewer.getUniqueId().toString(),
                                viewer.getName(),
                                0, // 순위는 계산 필요
                                value,
                                currentType.getId(),
                                System.currentTimeMillis()
                        );
                    }
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
     * 리더보드 항목 생성
     */
    private GuiItem createLeaderboardItem(@NotNull LeaderboardEntryDTO entry, int displayRank) {
        boolean isMyself = entry.playerUuid().equals(viewer.getUniqueId().toString());
        Material material = getRankMaterial(entry.rank());
        TextColor color = getRankColor(entry.rank());

        // 플레이어 머리 사용 (상위 3명 제외)
        ItemBuilder builder;
        if (entry.rank() <= 3) {
            builder = new ItemBuilder(material);
        } else {
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(UUID.fromString(entry.playerUuid()));
            builder = new ItemBuilder(offlinePlayer.getPlayer());
        }

        builder.displayName(Component.text(entry.rank() + ". " + entry.playerName(), color)
                        .decoration(TextDecoration.BOLD, entry.rank() <= 3))
                .addLore(Component.empty())
                .addLore(trans("gui.leaderboard.value", "value", formatValue(entry.value())))
                .addLore(trans("gui.leaderboard.last-updated",
                        "time", formatTime(entry.lastUpdated())));

        if (isMyself) {
            builder.addLore(Component.empty())
                    .addLore(trans("gui.leaderboard.this-is-you"))
                    .glint(true);
        }

        builder.flags(ItemFlag.values());

        return GuiItem.display(builder.build());
    }

    /**
     * 타입별 값 가져오기
     */
    private long getValueForType(@NotNull RPGPlayer rpgPlayer, @NotNull LeaderboardType type) {
        return switch (type) {
            case LEVEL -> rpgPlayer.getLevel();
            case COMBAT_POWER -> rpgPlayer.getCombatPower();
            case GOLD -> rpgPlayer.getWallet().getBalance(CurrencyType.GOLD);
            case TOTAL_PLAYTIME -> rpgPlayer.getTotalPlaytime();
        };
    }

    /**
     * 순위별 아이템 재료
     */
    private Material getRankMaterial(int rank) {
        return switch (rank) {
            case 1 -> Material.DIAMOND_BLOCK;
            case 2 -> Material.GOLD_BLOCK;
            case 3 -> Material.IRON_BLOCK;
            default -> Material.PLAYER_HEAD;
        };
    }

    /**
     * 순위별 색상
     */
    private TextColor getRankColor(int rank) {
        return switch (rank) {
            case 1 -> ColorUtil.LEGENDARY;
            case 2 -> ColorUtil.GOLD;
            case 3 -> ColorUtil.IRON;
            default -> rank <= 10 ? ColorUtil.AQUA : ColorUtil.WHITE;
        };
    }

    /**
     * 값 포맷팅
     */
    private String formatValue(long value) {
        if (currentType == LeaderboardType.TOTAL_PLAYTIME) {
            return formatPlaytime(value);
        }
        return String.format("%,d", value);
    }

    /**
     * 플레이타임 포맷팅
     */
    private String formatPlaytime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        return String.format("%d시간 %d분", hours, minutes);
    }

    /**
     * 시간 포맷팅
     */
    private String formatTime(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        if (diff < 60000) return "방금 전";
        if (diff < 3600000) return (diff / 60000) + "분 전";
        if (diff < 86400000) return (diff / 3600000) + "시간 전";
        return (diff / 86400000) + "일 전";
    }
}