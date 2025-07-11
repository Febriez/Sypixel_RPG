package com.febrie.rpg.gui.impl;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.talent.Talent;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * 특성 관리 GUI - 세로 중앙 정렬 레이아웃
 * 웹 형태의 특성 트리를 탐색하고 특성을 배울 수 있음
 *
 * @author Febrie, CoffeeTory
 */
public class TalentGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6행

    // 스크롤 설정 (중앙 3열 사용)
    private static final int SCROLL_START_ROW = 1;
    private static final int SCROLL_END_ROW = 4;
    private static final int CONTENT_COLUMN = 4; // 중앙 열 (0-8 중 4)

    // 스크롤 버튼
    private static final int SCROLL_UP_SLOT = 17; // 상단 오른쪽 한칸 아래
    private static final int SCROLL_DOWN_SLOT = 44; // 하단 오른쪽 한칸 위

    private final RPGPlayer rpgPlayer;

    // 현재 페이지 정보
    private final String pageId;
    private final List<Talent> pageTalents;
    private final Stack<PageInfo> pageHistory = new Stack<>();

    // 스크롤 관련
    private int currentScroll = 0;
    private static final int VISIBLE_ROWS = 4; // 보이는 행 수

    // 페이지 정보를 저장하는 내부 클래스
    private record PageInfo(String pageId, List<Talent> talents) {
    }

    public TalentGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                     @NotNull Player viewer, @NotNull RPGPlayer rpgPlayer,
                     @Nullable String pageId, @NotNull List<Talent> talents) {
        super(viewer, guiManager, langManager, GUI_SIZE,
                pageId != null && !pageId.equals("main") ? "gui.talent.title-with-page" : "gui.talent.title",
                "page", pageId != null ? langManager.getMessage(viewer, "gui.talent.pages." + pageId,
                        "gui.talent.pages.default") : "");
        this.rpgPlayer = rpgPlayer;
        this.pageId = pageId != null ? pageId : "main";
        this.pageTalents = talents;
        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        if (pageId != null && !pageId.equals("main")) {
            return trans("gui.talent.title-with-page", "page", getPageTitle());
        }
        return trans("gui.talent.title");
    }

    @Override
    public int getSize() {
        return GUI_SIZE;
    }

    @Override
    protected void setupLayout() {
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

        for (Talent talent : pageTalents) {
            talentItems.add(createTalentItem(talent));
        }

        return talentItems;
    }

    /**
     * 특성 아이템 생성
     */
    private GuiItem createTalentItem(@NotNull Talent talent) {
        Talent.TalentHolder talentHolder = rpgPlayer.getTalents();
        int currentLevel = talentHolder.getTalentLevel(talent);
        boolean canActivate = talent.canActivate(talentHolder);
        boolean isMaxLevel = currentLevel >= talent.getMaxLevel();

        // 이름 가져오기 - Talent에 언어별 이름이 하드코딩되어 있으므로 그대로 사용
        String talentName = talent.getName(transString("general.language-code").equals("ko_KR"));

        ItemBuilder builder = ItemBuilder.of(talent.getIcon())
                .displayName(Component.text(talentName, talent.getColor())
                        .decoration(TextDecoration.BOLD, true))
                .addLore(Component.empty());

        // 레벨 정보
        if (talent.getMaxLevel() > 1) {
            String levelKey = isMaxLevel ? "gui.talent.talent-level-max" : "gui.talent.talent-level";
            builder.addLore(trans(levelKey,
                    "current", String.valueOf(currentLevel),
                    "max", String.valueOf(talent.getMaxLevel())));
        }

        // 필요 포인트
        if (!isMaxLevel) {
            builder.addLore(trans("gui.talent.required-points",
                    "points", String.valueOf(talent.getRequiredPoints())));
        }

        builder.addLore(Component.empty());

        // 설명 - Talent의 getDescription이 언어 지원하므로 그대로 사용
        boolean isKorean = transString("general.language-code").equals("ko_KR");
        List<Component> description = talent.getDescription(isKorean, currentLevel);
        for (Component line : description) {
            builder.addLore(line);
        }

        builder.addLore(Component.empty());

        // 상호작용 안내
        if (isMaxLevel) {
            builder.addLore(trans("gui.talent.max-level-reached"));
        } else if (canActivate) {
            builder.addLore(trans("gui.talent.click-learn"));
        } else {
            if (talentHolder.getAvailablePoints() < talent.getRequiredPoints()) {
                builder.addLore(trans("gui.talent.insufficient-points"));
            } else {
                builder.addLore(trans("gui.talent.prerequisite-not-met"));
            }
        }

        if (talent.hasSubPage()) {
            builder.addLore(trans("gui.talent.click-subpage"));
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
                playErrorSound(player);
                return;
            }

            if (talent.levelUp(talentHolder)) {
                refresh();
                player.playSound(player.getLocation(),
                        org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                sendMessage(player, "gui.talent.talent-learned", "talent", talentName);

                // 스탯 보너스 업데이트
                updateStatBonuses();
            } else {
                playErrorSound(player);
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
                            .displayName(trans("gui.buttons.previous-page.name"))
                            .addLore(trans("gui.buttons.previous-page.lore"))
                            .build(),
                    player -> {
                        currentScroll--;
                        refresh();
                        playClickSound(player);
                    }
            ));
        }

        // 아래로 스크롤
        if (currentScroll < maxScroll) {
            setItem(SCROLL_DOWN_SLOT, GuiItem.clickable(
                    ItemBuilder.of(Material.LIME_DYE)
                            .displayName(trans("gui.buttons.next-page.name"))
                            .addLore(trans("gui.buttons.next-page.lore"))
                            .build(),
                    player -> {
                        currentScroll++;
                        refresh();
                        playClickSound(player);
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
                        .displayName(trans("gui.buttons.back.name"))
                        .addLore(trans("gui.buttons.back.lore"))
                        .build(),
                player -> goToPreviousPage()
        ));

        // 새로고침 버튼
        setItem(46, GuiFactory.createRefreshButton(player -> refresh(), langManager, viewer));

        // 스탯 페이지로 가기 버튼
        setItem(52, GuiItem.clickable(
                ItemBuilder.of(Material.IRON_SWORD)
                        .displayName(trans("gui.stats.title"))
                        .addLore(trans("gui.talent.click-stats"))
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
        setItem(53, GuiFactory.createCloseButton(langManager, viewer));
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
                        .addLore(trans("gui.talent.level", "level", String.valueOf(rpgPlayer.getLevel())))
                        .addLore(trans(rpgPlayer.hasJob() ? "gui.talent.job" : "gui.talent.no-job",
                                "job", rpgPlayer.hasJob() ?
                                        trans("job." + rpgPlayer.getJob().name().toLowerCase() + ".name").toString() : ""))
                        .build()
        );
        setItem(0, pageInfo);

        // 특성 포인트 정보 (우측 상단)
        Talent.TalentHolder talents = rpgPlayer.getTalents();
        GuiItem talentPointInfo = GuiItem.display(
                ItemBuilder.of(Material.EXPERIENCE_BOTTLE)
                        .displayName(trans("gui.talent.points-info"))
                        .addLore(trans("gui.talent.available-points",
                                "points", String.valueOf(talents.getAvailablePoints())))
                        .addLore(trans("gui.talent.spent-points",
                                "points", String.valueOf(talents.getSpentPoints())))
                        .addLore(Component.empty())
                        .addLore(trans("gui.talent.points-per-level"))
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
        return transString("gui.talent.pages." + pageId, transString("gui.talent.pages.default"));
    }
}