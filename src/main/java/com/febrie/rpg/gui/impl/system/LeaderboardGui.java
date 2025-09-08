package com.febrie.rpg.gui.impl.system;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.system.LeaderboardEntryDTO;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.framework.ScrollableGui;
import com.febrie.rpg.gui.impl.system.MainMenuGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import com.febrie.rpg.util.SkullUtil;
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
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;
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
        LEVEL("level", Material.EXPERIENCE_BOTTLE, UnifiedColorUtil.EXPERIENCE, LangKey.GUI_LEADERBOARD_TYPE_LEVEL),
        COMBAT_POWER("combat_power", Material.DIAMOND_SWORD, UnifiedColorUtil.LEGENDARY, LangKey.GUI_LEADERBOARD_TYPE_COMBAT_POWER),
        GOLD("gold", Material.GOLD_INGOT, UnifiedColorUtil.GOLD, LangKey.GUI_LEADERBOARD_TYPE_GOLD),
        TOTAL_PLAYTIME("total_playtime", Material.CLOCK, UnifiedColorUtil.AQUA, LangKey.GUI_LEADERBOARD_TYPE_PLAYTIME);
        private final String id;
        private final Material icon;
        private final TextColor color;
        private final LangKey displayNameKey;
        LeaderboardType(String id, Material icon, TextColor color, LangKey displayNameKey) {
            this.id = id;
            this.icon = icon;
            this.color = color;
            this.displayNameKey = displayNameKey;
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
        public LangKey getDisplayNameKey() {
            return displayNameKey;
        }
        
        public Component getDisplayName() {
            return LangManager.text(displayNameKey);
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
    private LeaderboardGui(@NotNull GuiManager guiManager,
                          @NotNull Player viewer) {
        this(guiManager, viewer, LeaderboardType.LEVEL);
    }
    
    private LeaderboardGui(@NotNull GuiManager guiManager,
                          @NotNull Player viewer, @NotNull LeaderboardType type) {
        super(viewer, guiManager, GUI_SIZE, 
                LangManager.text(LangKey.GUI_LEADERBOARD_TITLE, viewer, type.getDisplayName()));
        this.currentType = type;
    }
    
    /**
     * LeaderboardGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매니저
     * @param langManager 언어 매니저
     * @param viewer 보는 플레이어
     * @return 초기화된 LeaderboardGui 인스턴스
     */
    public static LeaderboardGui create(@NotNull GuiManager guiManager,
                                       @NotNull Player viewer) {
        return create(guiManager, viewer, LeaderboardType.LEVEL);
    }
    
    /**
     * LeaderboardGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매니저
     * @param viewer 보는 플레이어
     * @param type 리더보드 타입
     * @return 초기화된 LeaderboardGui 인스턴스
     */
    public static LeaderboardGui create(@NotNull GuiManager guiManager,
                                       @NotNull Player viewer, @NotNull LeaderboardType type) {
        LeaderboardGui gui = new LeaderboardGui(guiManager, viewer, type);
        gui.loadLeaderboard();
        return gui;
    }
    
    @Override
    public @NotNull Component getTitle() {
        return LangManager.text(LangKey.GUI_LEADERBOARD_TITLE, viewer, currentType.getDisplayName());
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
            if (i != 49) {
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
                    ItemBuilder.of(type.getIcon())
                            .displayName(type.getDisplayName().color(type.getColor())
                                    .decoration(TextDecoration.BOLD, true))
                            .addLore(Component.empty())
                            .addLore(isSelected ?
                                    LangManager.text(LangKey.GUI_LEADERBOARD_TAB_SELECTED, viewer) :
                                    LangManager.text(LangKey.GUI_LEADERBOARD_TAB_CLICK, viewer))
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
                    ItemBuilder.of(Material.CLOCK)
                            .displayName(LangManager.text(LangKey.ITEMS_LEADERBOARD_LOADING_NAME, viewer))
                            .addLore(LangManager.text(LangKey.ITEMS_LEADERBOARD_LOADING_LORE, viewer))
                            .hideAllFlags()
                            .build()
            ));
        } else {
            setItem(LOADING_SLOT, GuiItem.display(
                    ItemBuilder.of(Material.NETHER_STAR)
                            .displayName(LangManager.text(LangKey.GUI_LEADERBOARD_TITLE, viewer, currentType.getDisplayName()))
                            .addLore(LangManager.text(LangKey.GUI_LEADERBOARD_CURRENT_TYPE, viewer, currentType.getDisplayName()))
                            .addLore(LangManager.text(LangKey.GUI_LEADERBOARD_TOTAL_ENTRIES, viewer, String.valueOf(currentLeaderboard.size())))
                            .hideAllFlags()
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
                    ItemBuilder.from(SkullUtil.getPlayerHead(viewer.getUniqueId().toString()))
                            .displayName(LangManager.text(LangKey.ITEMS_LEADERBOARD_MY_RANK_NAME, viewer))
                            .addLore(LangManager.text(LangKey.GUI_LEADERBOARD_MY_RANK, viewer, String.valueOf(myRankEntry.rank())))
                            .addLore(LangManager.text(LangKey.GUI_LEADERBOARD_MY_VALUE, viewer, formatValue(myRankEntry.value())))
                            .hideAllFlags()
                            .addLore(LangManager.text(LangKey.GUI_LEADERBOARD_LAST_UPDATED, viewer, formatTime(myRankEntry.lastUpdated())))
                            .glint(true)
                            .build()
            ));
        } else {
            setItem(MY_RANK_SLOT, GuiItem.display(
                    ItemBuilder.from(SkullUtil.getPlayerHead(viewer.getUniqueId().toString()))
                            .displayName(LangManager.text(LangKey.ITEMS_LEADERBOARD_NO_RANK_NAME, viewer))
                            .addLore(LangManager.text(LangKey.ITEMS_LEADERBOARD_NO_RANK_LORE1, viewer))
                            .addLore(LangManager.text(LangKey.ITEMS_LEADERBOARD_NO_RANK_LORE2, viewer))
                            .hideAllFlags()
                            .build()
            ));
        }
    }
    
    /**
     * 네비게이션 버튼 설정
     */
    private void setupNavigationButtons() {
        // BaseGui의 표준 네비게이션 사용
        setupStandardNavigation(false, true);
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
        // Firebase 서비스가 현재 비활성화됨 - 더미 데이터 사용
        isLoading = false;
        currentLeaderboard = new ArrayList<>();
        
        // 더미 데이터로 테스트
        for (int i = 0; i < 10; i++) {
            LeaderboardEntryDTO entry = new LeaderboardEntryDTO(
                    UUID.randomUUID().toString(),
                    "Player" + (i + 1),
                    i + 1,
                    1000 - (i * 100),
                    currentType.getId(),
                    System.currentTimeMillis()
            );
            currentLeaderboard.add(entry);
        }
        
        // 내 데이터 생성
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
        
        // UI 업데이트
        Bukkit.getScheduler().runTask(RPGMain.getPlugin(), () -> {
            isLoading = false;
            refresh();
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
            builder = ItemBuilder.of(material);
        } else {
            builder = ItemBuilder.from(SkullUtil.getPlayerHead(entry.playerUuid()));
        }
        
        builder.displayName(Component.text(entry.rank() + ". " + entry.playerName(), color)
                        .decoration(TextDecoration.BOLD, entry.rank() <= 3))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.GUI_LEADERBOARD_VALUE, viewer, formatValue(entry.value())))
                .addLore(LangManager.text(LangKey.GUI_LEADERBOARD_LAST_UPDATED, viewer, formatTime(entry.lastUpdated())));
        if (isMyself) {
            builder.addLore(Component.empty())
                    .addLore(LangManager.text(LangKey.GUI_LEADERBOARD_THIS_IS_YOU, viewer))
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
            case 1 -> UnifiedColorUtil.LEGENDARY;
            case 2 -> UnifiedColorUtil.GOLD;
            case 3 -> UnifiedColorUtil.IRON;
            default -> rank <= 10 ? UnifiedColorUtil.AQUA : UnifiedColorUtil.WHITE;
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
        // Format directly since we need a String return value
        return hours + "h " + minutes + "m";
    }
    
    /**
     * 시간 포맷팅
     */
    private String formatTime(long timestamp) {
        long diff = System.currentTimeMillis() - timestamp;
        if (diff < 60000) return "Just now";
        if (diff < 3600000) return (diff / 60000) + " minutes ago";
        if (diff < 86400000) return (diff / 3600000) + " hours ago";
        return (diff / 86400000) + " days ago";
    }
    
    @Override
    protected List<ClickType> getAllowedClickTypes() {
        return List.of(ClickType.LEFT, ClickType.RIGHT);
    }
    
    @Override
    protected GuiFramework getBackTarget() {
        // LeaderboardGui는 MainMenuGui로 돌아갑니다
        return MainMenuGui.create(guiManager, viewer);
    }
}
