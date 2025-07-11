package com.febrie.rpg.gui.impl;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.InteractiveGui;
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
 * 특성 관리 GUI - 세로 중앙 정렬 레이아웃
 * 웹 형태의 특성 트리를 탐색하고 특성을 배울 수 있음
 *
 * @author Febrie, CoffeeTory
 */
public class TalentGui implements InteractiveGui {

    private static final int GUI_SIZE = 54; // 6행

    // 스크롤 설정 (중앙 3열 사용)
    private static final int SCROLL_START_ROW = 1;
    private static final int SCROLL_END_ROW = 4;
    private static final int CONTENT_COLUMN = 4; // 중앙 열 (0-8 중 4)

    // 스크롤 버튼
    private static final int SCROLL_UP_SLOT = 4; // 상단 중앙
    private static final int SCROLL_DOWN_SLOT = 49; // 하단 중앙

    private final GuiManager guiManager;
    private final LangManager langManager;
    private final RPGPlayer rpgPlayer;
    private final Inventory inventory;
    private final Map<Integer, GuiItem> items = new HashMap<>();

    // 현재 페이지 정보
    private final String pageId;
    private final List<Talent> pageTalents;
    private final Stack<PageInfo> pageHistory = new Stack<>();

    // 스크롤 관련
    private int currentScroll = 0;
    private static final int VISIBLE_ROWS = 4; // 보이는 행 수

    // 페이지 정보를 저장하는 내부 클래스
    private static class PageInfo {
        final String pageId;
        final List<Talent> talents;

        PageInfo(String pageId, List<Talent> talents) {
            this.pageId = pageId;
            this.talents = talents;
        }
    }

    public TalentGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                     @NotNull Player viewer, @NotNull RPGPlayer rpgPlayer,
                     @Nullable String pageId, @NotNull List<Talent> talents) {
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
    public void onSlotClick(@NotNull InventoryClickEvent event, @NotNull Player player, int slot, @NotNull ClickType click) {
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
        setupTalentDisplay();
        setupScrollButtons();
        setupNavigationButtons();
        setupInfoDisplay();
    }

    /**
     * 배경 설정
     */
    private void setupBackground() {
        // 전체 배경을 회색 유리판으로
        for (int i = 0; i < GUI_SIZE; i++) {
            setItem(i, GuiFactory.createDecoration());
        }

        // 중앙 열을 특별한 색으로 (특성이 표시될 곳)
        for (int row = 0; row < 6; row++) {
            int slot = row * 9 + CONTENT_COLUMN;
            if (row >= SCROLL_START_ROW && row <= SCROLL_END_ROW) {
                setItem(slot, GuiFactory.createDecoration(Material.BLACK_STAINED_GLASS_PANE));
            }
        }
    }

    /**
     * 특성 표시 (세로 중앙 정렬)
     */
    private void setupTalentDisplay() {
        List<GuiItem> talentItems = createTalentItems();
        int totalItems = talentItems.size();

        // 스크롤이 필요한지 확인
        boolean needsScroll = totalItems > VISIBLE_ROWS;

        if (needsScroll) {
            // 스크롤 적용
            int startIndex = currentScroll;
            int endIndex = Math.min(startIndex + VISIBLE_ROWS, totalItems);

            for (int i = startIndex; i < endIndex; i++) {
                int row = SCROLL_START_ROW + (i - startIndex);
                int slot = row * 9 + CONTENT_COLUMN;
                setItem(slot, talentItems.get(i));
            }
        } else {
            // 스크롤 불필요 - 중앙 정렬
            int startRow = SCROLL_START_ROW + (VISIBLE_ROWS - totalItems) / 2;

            for (int i = 0; i < totalItems; i++) {
                int row = startRow + i;
                int slot = row * 9 + CONTENT_COLUMN;
                setItem(slot, talentItems.get(i));
            }
        }
    }

    /**
     * 특성 아이템들 생성
     */
    private List<GuiItem> createTalentItems() {
        List<GuiItem> talentItems = new ArrayList<>();
        boolean isKorean = langManager.getPlayerLanguage(rpgPlayer.getBukkitPlayer()).equals("ko_KR");

        for (Talent talent : pageTalents) {
            talentItems.add(createTalentItem(talent, isKorean));
        }

        return talentItems;
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
        for (Component line : description) {
            builder.addLore(line);
        }

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
     * 스크롤 버튼 설정
     */
    private void setupScrollButtons() {
        int maxScroll = Math.max(0, pageTalents.size() - VISIBLE_ROWS);

        // 위로 스크롤
        if (currentScroll > 0) {
            setItem(SCROLL_UP_SLOT, GuiItem.clickable(
                    ItemBuilder.of(Material.LIME_DYE)
                            .displayName(Component.text("▲ 위로", ColorUtil.SUCCESS))
                            .addLore(Component.text("클릭하여 위로 스크롤", ColorUtil.GRAY))
                            .build(),
                    player -> {
                        currentScroll--;
                        refresh();
                        player.playSound(player.getLocation(),
                                org.bukkit.Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    }
            ));
        }

        // 아래로 스크롤
        if (currentScroll < maxScroll) {
            setItem(SCROLL_DOWN_SLOT, GuiItem.clickable(
                    ItemBuilder.of(Material.LIME_DYE)
                            .displayName(Component.text("▼ 아래로", ColorUtil.SUCCESS))
                            .addLore(Component.text("클릭하여 아래로 스크롤", ColorUtil.GRAY))
                            .build(),
                    player -> {
                        currentScroll++;
                        refresh();
                        player.playSound(player.getLocation(),
                                org.bukkit.Sound.UI_BUTTON_CLICK, 0.5f, 1.0f);
                    }
            ));
        }
    }

    /**
     * 네비게이션 버튼 설정
     */
    private void setupNavigationButtons() {
        // 뒤로가기 버튼 (좌측 하단)
        setItem(45, GuiItem.clickable(
                ItemBuilder.of(Material.ARROW)
                        .displayName(Component.text("뒤로가기", ColorUtil.YELLOW))
                        .addLore(Component.text("이전 페이지로", ColorUtil.GRAY))
                        .build(),
                player -> goToPreviousPage()
        ));

        // 새로고침 버튼
        setItem(46, GuiFactory.createRefreshButton(player -> refresh(), langManager, rpgPlayer.getBukkitPlayer()));

        // 스탯 페이지로 가기 버튼
        setItem(52, GuiItem.clickable(
                ItemBuilder.of(Material.IRON_SWORD)
                        .displayName(Component.text("스탯 관리", ColorUtil.COPPER))
                        .addLore(Component.text("클릭하여 스탯 페이지로 이동", ColorUtil.GRAY))
                        .build(),
                player -> {
                    if (guiManager != null) {
                        player.closeInventory();
                        StatsGui statsGui = new StatsGui(guiManager, langManager, player, rpgPlayer);
                        statsGui.open(player);
                    }
                }
        ));

        // 닫기 버튼 (우측 하단)
        setItem(53, GuiFactory.createCloseButton(langManager, rpgPlayer.getBukkitPlayer()));
    }

    /**
     * 정보 표시 영역
     */
    private void setupInfoDisplay() {
        // 현재 페이지 정보 (좌측 상단)
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
        setItem(0, pageInfo);

        // 특성 포인트 정보 (우측 상단)
        Talent.TalentHolder talents = rpgPlayer.getTalents();
        GuiItem talentPointInfo = GuiItem.display(
                ItemBuilder.of(Material.EXPERIENCE_BOTTLE)
                        .displayName(Component.text("특성 포인트", ColorUtil.LEGENDARY))
                        .addLore(Component.text("보유 포인트: " + talents.getAvailablePoints(), ColorUtil.WHITE))
                        .addLore(Component.text("사용한 포인트: " + talents.getSpentPoints(), ColorUtil.GRAY))
                        .addLore(Component.empty())
                        .addLore(Component.text("10레벨마다 1포인트를 획득합니다", ColorUtil.GRAY))
                        .build()
        );
        setItem(8, talentPointInfo);
    }

    /**
     * 하위 페이지 열기
     */
    private void openSubPage(@NotNull Talent talent) {
        // 현재 페이지 정보 저장
        pageHistory.push(new PageInfo(pageId, pageTalents));

        // 새 GUI 생성하여 열기
        TalentGui subPageGui = new TalentGui(
                guiManager, langManager, rpgPlayer.getBukkitPlayer(), rpgPlayer,
                talent.getPageId(), talent.getChildren()
        );

        // 페이지 히스토리 복사
        subPageGui.pageHistory.addAll(this.pageHistory);

        rpgPlayer.getBukkitPlayer().closeInventory();
        subPageGui.open(rpgPlayer.getBukkitPlayer());
    }

    /**
     * 이전 페이지로 돌아가기
     */
    private void goToPreviousPage() {
        if (pageHistory.isEmpty()) {
            // 메인 메뉴로
            if (guiManager != null) {
                guiManager.openMainMenuGui(rpgPlayer.getBukkitPlayer());
            }
            return;
        }

        PageInfo previousPage = pageHistory.pop();

        // 이전 페이지 GUI 생성
        TalentGui previousGui = new TalentGui(
                guiManager, langManager, rpgPlayer.getBukkitPlayer(), rpgPlayer,
                previousPage.pageId, previousPage.talents
        );

        // 남은 히스토리 복사
        previousGui.pageHistory.addAll(this.pageHistory);

        rpgPlayer.getBukkitPlayer().closeInventory();
        previousGui.open(rpgPlayer.getBukkitPlayer());
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
     * 페이지 제목 가져오기
     */
    private String getPageTitle() {
        // 페이지 ID에 따른 제목
        return switch (pageId) {
            case "main" -> "메인 특성";
            case "strength_tree" -> "근력 트리";
            case "berserker_offense" -> "버서커 공격 특성";
            case "tank_defense" -> "탱커 방어 특성";
            case "priest_healing" -> "사제 치유 특성";
            case "dark_curses" -> "흑마법사 저주 특성";
            case "archer_offense" -> "궁수 공격 특성";
            case "sniper_special" -> "스나이퍼 특수 특성";
            default -> "특성 트리";
        };
    }

    /**
     * 아이템 설정
     */
    private void setItem(int slot, @NotNull GuiItem item) {
        items.put(slot, item);
        inventory.setItem(slot, item.getItemStack());
    }
}