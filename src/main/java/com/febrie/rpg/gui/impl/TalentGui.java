package com.febrie.rpg.gui.impl;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.ScrollableGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.talent.Talent;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * 특성 관리 GUI
 * 웹 형태의 특성 트리를 탐색하고 특성을 배울 수 있음
 *
 * @author Febrie, CoffeeTory
 */
public class TalentGui extends ScrollableGui {

    private final GuiManager guiManager;
    private final LangManager langManager;
    private final RPGPlayer rpgPlayer;
    private final Inventory inventory;
    private final Map<Integer, GuiItem> items = new HashMap<>();

    // 현재 페이지 정보
    private final String pageId;
    private final List<Talent> pageTalents;
    private final Stack<String> pageHistory = new Stack<>();

    public TalentGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                     @NotNull Player viewer, @NotNull RPGPlayer rpgPlayer,
                     @Nullable String pageId, @NotNull List<Talent> talents) {
        super(viewer);
        this.guiManager = guiManager;
        this.langManager = langManager;
        this.rpgPlayer = rpgPlayer;
        this.pageId = pageId != null ? pageId : "main";
        this.pageTalents = talents;

        String title = pageId != null && !pageId.equals("main") ?
                "특성 - " + getPageTitle() : "특성 관리";
        this.inventory = Bukkit.createInventory(this, GUI_SIZE,
                Component.text(title, ColorUtil.EPIC));

        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.text("특성 관리", ColorUtil.EPIC);
    }

    @Override
    public int getSize() {
        return GUI_SIZE;
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
    protected List<GuiItem> getScrollableItems() {
        List<GuiItem> talentItems = new ArrayList<>();
        boolean isKorean = langManager.getPlayerLanguage(viewer).equals("ko_KR");

        // 현재 페이지의 특성들을 GuiItem으로 변환
        for (Talent talent : pageTalents) {
            talentItems.add(createTalentItem(talent, isKorean));
        }

        return talentItems;
    }

    @Override
    protected void handleNonScrollClick(@NotNull InventoryClickEvent event,
                                        @NotNull Player player, int slot,
                                        @NotNull ClickType click) {
        GuiItem item = items.get(slot);
        if (item != null && item.hasActions()) {
            item.executeAction(player, click);
        }
    }

    /**
     * GUI 레이아웃 설정
     */
    private void setupLayout() {
        setupBackground();
        setupPlayerInfo();
        setupScrollableTalents();
        setupScrollButtons();
        setupNavigationButtons();
    }

    /**
     * 배경 설정
     */
    private void setupBackground() {
        // 상단 테두리
        for (int i = 0; i < 9; i++) {
            if (i != 4 && i != SCROLL_UP_SLOT) {
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 하단 테두리
        for (int i = 45; i < 54; i++) {
            if (i != 49 && i != SCROLL_DOWN_SLOT) {
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 좌우 테두리
        for (int row = 1; row < 5; row++) {
            setItem(row * 9, GuiFactory.createDecoration());
            if (row != 2 && row != 4) {
                setItem(row * 9 + 8, GuiFactory.createDecoration());
            }
        }
    }

    /**
     * 플레이어 정보 표시
     */
    private void setupPlayerInfo() {
        // 현재 페이지 정보
        GuiItem pageInfo = GuiItem.display(
                ItemBuilder.of(Material.KNOWLEDGE_BOOK)
                        .displayName(Component.text(getPageTitle(), ColorUtil.EPIC)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(Component.text("레벨 " + rpgPlayer.getLevel(), ColorUtil.SUCCESS))
                        .addLore(Component.text("직업: " + (rpgPlayer.hasJob() ?
                                rpgPlayer.getJob().getKoreanName() : "없음"), ColorUtil.INFO))
                        .build()
        );
        setItem(4, pageInfo);

        // 특성 포인트 정보
        Talent.TalentHolder talents = rpgPlayer.getTalents();
        GuiItem talentPointInfo = GuiItem.display(
                ItemBuilder.of(Material.EXPERIENCE_BOTTLE)
                        .displayName(Component.text("특성 포인트", ColorUtil.LEGENDARY))
                        .addLore(Component.text("보유 포인트: " + talents.getAvailablePoints(), ColorUtil.WHITE))
                        .addLore(Component.text("사용한 포인트: " + talents.getSpentPoints(), ColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("10레벨마다 1포인트를 획득합니다", ColorUtil.GRAY))
                        .addLore(Component.text("특성을 클릭하여 배우세요", ColorUtil.GRAY))
                        .build()
        );
        setItem(49, talentPointInfo);
    }

    /**
     * 특성 아이템 생성
     */
    private GuiItem createTalentItem(@NotNull Talent talent, boolean isKorean) {
        Talent.TalentHolder talentHolder = rpgPlayer.getTalents();
        int currentLevel = talentHolder.getTalentLevel(talent);
        boolean canActivate = talent.canActivate(talentHolder);
        boolean isMaxLevel = currentLevel >= talent.getMaxLevel();

        ItemBuilder builder = ItemBuilder.of(talent.getIcon())
                .displayName(Component.text(talent.getName(isKorean), talent.getColor())
                        .decoration(TextDecoration.BOLD, true))
                .addLore(Component.empty());

        // 레벨 정보
        if (talent.getMaxLevel() > 1) {
            builder.addLore(Component.text("레벨: " + currentLevel + " / " + talent.getMaxLevel(),
                    isMaxLevel ? ColorUtil.LEGENDARY : ColorUtil.INFO));
        }

        // 필요 포인트
        if (!isMaxLevel) {
            builder.addLore(Component.text("필요 포인트: " + talent.getRequiredPoints(),
                    ColorUtil.EXPERIENCE));
        }

        builder.addLore(Component.empty());

        // 설명
        List<Component> description = talent.getDescription(isKorean, currentLevel);
        builder.lore(description);

        builder.addLore(Component.empty());

        // 상호작용 안내
        if (isMaxLevel) {
            builder.addLore(Component.text("✦ 최대 레벨 달성!", ColorUtil.LEGENDARY));
        } else if (canActivate) {
            builder.addLore(Component.text("▶ 좌클릭: 특성 배우기", ColorUtil.SUCCESS));
        } else {
            if (talentHolder.getAvailablePoints() < talent.getRequiredPoints()) {
                builder.addLore(Component.text("✖ 특성 포인트가 부족합니다", ColorUtil.ERROR));
            } else {
                builder.addLore(Component.text("✖ 선행 조건을 만족하지 않습니다", ColorUtil.ERROR));
            }
        }

        if (talent.hasSubPage()) {
            builder.addLore(Component.text("▶ 우클릭: 하위 특성 페이지 열기", ColorUtil.INFO));
        }

        // 이미 배운 특성은 인챈트 효과
        if (currentLevel > 0) {
            builder.enchant(Enchantment.UNBREAKING, currentLevel);
        }

        builder.flags(ItemFlag.values());

        GuiItem item = GuiItem.of(builder.build());

        // 좌클릭 - 특성 배우기
        item.onClick(ClickType.LEFT, (player, click) -> {
            if (isMaxLevel) {
                player.playSound(player.getLocation(),
                        org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
                return;
            }

            if (talent.levelUp(talentHolder)) {
                refresh();
                player.playSound(player.getLocation(),
                        org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                player.sendMessage(Component.text("특성을 배웠습니다: " + talent.getName(isKorean),
                        ColorUtil.SUCCESS));

                // 스탯 보너스 업데이트
                updateStatBonuses();
            } else {
                player.playSound(player.getLocation(),
                        org.bukkit.Sound.ENTITY_VILLAGER_NO, 1.0f, 1.0f);
            }
        });

        // 우클릭 - 하위 페이지 열기
        if (talent.hasSubPage()) {
            item.onClick(ClickType.RIGHT, (player, click) -> {
                openSubPage(talent);
            });
        }

        return item;
    }

    /**
     * 하위 페이지 열기
     */
    private void openSubPage(@NotNull Talent talent) {
        pageHistory.push(pageId);

        // 새 GUI 생성하여 열기
        TalentGui subPageGui = new TalentGui(
                guiManager, langManager, viewer, rpgPlayer,
                talent.getPageId(), talent.getChildren()
        );
        subPageGui.pageHistory.addAll(this.pageHistory);

        viewer.closeInventory();
        subPageGui.open(viewer);
    }

    /**
     * 이전 페이지로 돌아가기
     */
    private void goToPreviousPage() {
        if (pageHistory.isEmpty()) {
            // 메인 메뉴나 프로필로
            if (guiManager != null) {
                guiManager.goBack(viewer);
            }
            return;
        }

        String previousPageId = pageHistory.pop();

        // TODO: 실제 구현 시 TalentManager에서 페이지별 특성 목록 가져오기
        // 임시로 빈 목록 사용
        TalentGui previousGui = new TalentGui(
                guiManager, langManager, viewer, rpgPlayer,
                previousPageId, new ArrayList<>()
        );
        previousGui.pageHistory.addAll(this.pageHistory);

        viewer.closeInventory();
        previousGui.open(viewer);
    }

    /**
     * 스탯 보너스 업데이트
     */
    private void updateStatBonuses() {
        Map<com.febrie.rpg.stat.Stat, Integer> bonuses = rpgPlayer.getTalents().calculateStatBonuses();

        for (Map.Entry<com.febrie.rpg.stat.Stat, Integer> entry : bonuses.entrySet()) {
            rpgPlayer.getStats().setBonusStat(entry.getKey(), entry.getValue());
        }

        rpgPlayer.saveToPDC();
    }

    /**
     * 스크롤 가능한 특성 목록 설정
     */
    private void setupScrollableTalents() {
        updateScroll();
        List<GuiItem> visibleItems = getVisibleItems();

        int index = 0;
        for (int row = SCROLL_START_ROW; row <= SCROLL_END_ROW; row++) {
            for (int col = SCROLL_START_COL; col <= SCROLL_END_COL; col++) {
                int slot = row * COLS + col;

                if (index < visibleItems.size()) {
                    setItem(slot, visibleItems.get(index));
                    index++;
                } else {
                    setItem(slot, GuiFactory.createFiller());
                }
            }
        }
    }

    /**
     * 스크롤 버튼 설정
     */
    private void setupScrollButtons() {
        setItem(SCROLL_UP_SLOT, createScrollUpButton());
        setItem(SCROLL_DOWN_SLOT, createScrollDownButton());

        // 스크롤바
        setItem(SCROLL_BAR_START, createScrollBarItem(SCROLL_BAR_START));
        setItem(26, createScrollBarItem(26));
        setItem(SCROLL_BAR_END, createScrollBarItem(SCROLL_BAR_END));
    }

    /**
     * 네비게이션 버튼 설정
     */
    private void setupNavigationButtons() {
        // 뒤로가기 버튼
        setItem(45, GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(Component.text("뒤로가기", ColorUtil.YELLOW))
                        .addLore(Component.text("이전 페이지로", ColorUtil.GRAY))
                        .build(),
                player -> goToPreviousPage()
        ));

        // 새로고침 버튼
        setItem(46, GuiFactory.createRefreshButton(player -> refresh(), langManager, viewer));

        // 스탯 페이지로 가기 버튼
        GuiItem statButton = GuiItem.clickable(
                ItemBuilder.of(Material.IRON_SWORD)
                        .displayName(Component.text("스탯 관리", ColorUtil.COPPER))
                        .addLore(Component.text("클릭하여 스탯 페이지로 이동", ColorUtil.GRAY))
                        .build(),
                player -> {
                    if (guiManager != null) {
                        viewer.closeInventory();
                        StatsGui statsGui = new StatsGui(guiManager, langManager, viewer, rpgPlayer);
                        statsGui.open(viewer);
                    }
                }
        );
        setItem(52, statButton);

        // 닫기 버튼
        setItem(53, GuiFactory.createCloseButton(langManager, viewer));
    }

    /**
     * 페이지 제목 가져오기
     */
    private String getPageTitle() {
        // TODO: 실제 구현 시 페이지 ID에 따른 제목 반환
        return pageId.equals("main") ? "메인 특성" : "하위 특성";
    }

    /**
     * 아이템 설정
     */
    private void setItem(int slot, @NotNull GuiItem item) {
        items.put(slot, item);
        inventory.setItem(slot, item.getItemStack());
    }
}