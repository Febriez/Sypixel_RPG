package com.febrie.rpg.gui.impl;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.ScrollableGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.talent.Talent;
import com.febrie.rpg.talent.TalentRequirement;
import com.febrie.rpg.util.ColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 특성 GUI - 올바르게 수정된 버전
 * 플레이어가 특성을 관리할 수 있는 GUI
 *
 * @author Febrie, CoffeeTory
 */
public class TalentGui extends ScrollableGui {

    private static final int GUI_SIZE = 54; // 6줄
    private static final int TALENT_TREE_START = 10;
    private static final int TALENTS_PER_ROW = 7;

    private final RPGPlayer rpgPlayer;
    private final String pageId;
    private final List<Talent> talents;
    private final Map<Integer, GuiItem> items = new HashMap<>();

    public TalentGui(@NotNull GuiManager guiManager, @NotNull LangManager langManager,
                     @NotNull Player viewer, @NotNull RPGPlayer rpgPlayer,
                     @NotNull String pageId, @NotNull List<Talent> talents) {
        super(viewer, guiManager, langManager, GUI_SIZE, "gui.talent.title");
        this.rpgPlayer = rpgPlayer;
        this.pageId = pageId;
        this.talents = talents;
        setupLayout();
    }

    @Override
    public @NotNull Component getTitle() {
        return trans("gui.talent.title");
    }

    @Override
    public void refresh() {
        inventory.clear();
        items.clear();
        setupLayout();
    }

    @Override
    protected List<GuiItem> getScrollableItems() {
        // 특성은 고정 위치에 표시
        return new ArrayList<>();
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
    protected void setupLayout() {
        setupBackground();
        setupInfoDisplay();
        setupTalentTree();
        setupNavigationButtons();
    }

    /**
     * 배경 설정
     */
    private void setupBackground() {
        createBorder();

        // 특성 영역 배경
        for (int row = 1; row < 4; row++) {
            for (int col = 1; col < 8; col++) {
                int slot = row * 9 + col;
                if (items.get(slot) == null) {
                    setItem(slot, GuiFactory.createDecoration(Material.BLACK_STAINED_GLASS_PANE));
                }
            }
        }
    }

    /**
     * 정보 표시 영역
     */
    private void setupInfoDisplay() {
        String jobName = rpgPlayer.hasJob() ?
                transString("job." + rpgPlayer.getJob().name().toLowerCase() + ".name") :
                transString("gui.talent.no-job");

        GuiItem pageInfo = GuiItem.display(
                ItemBuilder.of(Material.ENCHANTED_BOOK)
                        .displayName(trans("gui.talent.page-info"))
                        .addLore(trans("gui.talent.current-page", "page", getPageTitle()))
                        .addLore(trans("gui.talent.job", "job", jobName))
                        .addLore(Component.empty())
                        .addLore(trans("gui.talent.available-points",
                                "points", String.valueOf(rpgPlayer.getTalents().getAvailablePoints())))
                        .build()
        );
        setItem(4, pageInfo);
    }

    /**
     * 특성 트리 표시
     */
    private void setupTalentTree() {
        int index = 0;
        for (Talent talent : talents) {
            if (index >= TALENTS_PER_ROW * 3) break; // 최대 3줄

            int row = index / TALENTS_PER_ROW;
            int col = index % TALENTS_PER_ROW;
            int slot = TALENT_TREE_START + (row * 9) + col;

            setItem(slot, createTalentItem(talent));
            index++;
        }
    }

    /**
     * 특성 아이템 생성
     */
    private GuiItem createTalentItem(@NotNull Talent talent) {
        int currentLevel = rpgPlayer.getTalents().getTalentLevel(talent.getId());
        boolean canLearn = rpgPlayer.getTalents().canLearnTalent(talent.getId());
        boolean maxed = currentLevel >= talent.getMaxLevel();

        Material material = talent.getIcon();
        if (!canLearn && currentLevel == 0) {
            material = Material.GRAY_DYE;
        }

        ItemBuilder builder = ItemBuilder.of(material)
                .displayName(trans("talent." + talent.getId() + ".name"))
                .amount(Math.max(1, currentLevel));

        // 레벨 정보
        if (currentLevel > 0) {
            builder.addLore(trans("gui.talent.level-info",
                    "current", String.valueOf(currentLevel),
                    "max", String.valueOf(talent.getMaxLevel())));
        } else {
            builder.addLore(trans("gui.talent.not-learned"));
        }

        builder.addLore(Component.empty());

        // 설명
        List<Component> description = langManager.getComponentList(viewer,
                "talent." + talent.getId() + ".description");
        description.forEach(builder::addLore);

        // 효과
        if (currentLevel > 0 || canLearn) {
            builder.addLore(Component.empty())
                    .addLore(trans("gui.talent.effects"));

            int displayLevel = Math.max(1, currentLevel);
            List<Component> effects = talent.getEffectDescription(displayLevel, langManager, viewer);
            effects.forEach(line -> builder.addLore(Component.text("  ", ColorUtil.GRAY).append(line)));
        }

        // 요구사항
        if (!canLearn && currentLevel == 0) {
            builder.addLore(Component.empty())
                    .addLore(trans("gui.talent.requirements"));

            for (TalentRequirement req : talent.getRequirements()) {
                Component reqText = req.getDisplayText(rpgPlayer, langManager, viewer);
                builder.addLore(Component.text("  ", ColorUtil.GRAY).append(reqText));
            }
        }

        // 클릭 정보
        builder.addLore(Component.empty());
        if (maxed) {
            builder.addLore(trans("gui.talent.maxed"));
        } else if (canLearn) {
            builder.addLore(trans("gui.talent.click-to-learn"));
            builder.addLore(trans("gui.talent.right-click-preview"));
        } else {
            builder.addLore(trans("gui.talent.cannot-learn"));
        }

        builder.flags(ItemFlag.values());

        if (canLearn || currentLevel > 0) {
            builder.glint(true);
        }

        GuiItem talentItem = GuiItem.of(builder.build())
                .onClick(ClickType.LEFT, (player, click) ->
                        handleTalentLeftClick(player, talent, currentLevel, canLearn))
                .onClick(ClickType.RIGHT, (player, click) ->
                        handleTalentRightClick(player, talent, currentLevel));

        return talentItem;
    }

    /**
     * 네비게이션 버튼 설정
     */
    private void setupNavigationButtons() {
        // 뒤로가기 버튼
        if (guiManager.canNavigateBack(viewer)) {
            setItem(45, GuiItem.clickable(
                    ItemBuilder.of(Material.ARROW)
                            .displayName(trans("gui.buttons.back.name"))
                            .addLore(trans("gui.buttons.back.lore"))
                            .build(),
                    player -> guiManager.navigateBack(player)
            ));
        }

        // 스탯 페이지로 가기 버튼
        setItem(50, GuiItem.clickable(
                ItemBuilder.of(Material.IRON_SWORD)
                        .displayName(trans("gui.stats.title"))
                        .addLore(trans("gui.talent.click-stats"))
                        .build(),
                player -> {
                    StatsGui statsGui = new StatsGui(guiManager, langManager, player, rpgPlayer);
                    guiManager.openGui(player, statsGui);
                }
        ));

        // 닫기 버튼
        setItem(53, GuiFactory.createCloseButton(langManager, viewer));
    }

    /**
     * 특성 좌클릭 처리 (레벨업)
     */
    private void handleTalentLeftClick(@NotNull Player player, @NotNull Talent talent,
                                       int currentLevel, boolean canLearn) {
        if (currentLevel >= talent.getMaxLevel()) {
            playErrorSound(player);
            return;
        }

        if (canLearn) {
            if (talent.levelUp(rpgPlayer.getTalents())) {
                String talentName = transString("talent." + talent.getId() + ".name");
                sendMessage(player, "messages.talent-learned", "talent", talentName);
                playSuccessSound(player);
                refresh();
            } else {
                sendMessage(player, "messages.talent-learn-failed");
                playErrorSound(player);
            }
        } else {
            sendMessage(player, "messages.talent-learn-failed");
            playErrorSound(player);
        }
    }

    /**
     * 특성 우클릭 처리 (미리보기)
     */
    private void handleTalentRightClick(@NotNull Player player, @NotNull Talent talent, int currentLevel) {
        // 다음 레벨 효과 미리보기
        if (currentLevel < talent.getMaxLevel()) {
            player.sendMessage(Component.empty());
            player.sendMessage(trans("gui.talent.preview-header",
                    "talent", transString("talent." + talent.getId() + ".name"),
                    "level", String.valueOf(currentLevel + 1)));

            List<Component> effects = talent.getEffectDescription(currentLevel + 1, langManager, player);
            effects.forEach(effect -> player.sendMessage(Component.text("  ").append(effect)));

            player.sendMessage(Component.empty());
        }
        playClickSound(player);
    }

    /**
     * 페이지 제목 가져오기
     */
    private String getPageTitle() {
        return switch (pageId) {
            case "main" -> transString("gui.talent.page.main");
            case "secondary" -> transString("gui.talent.page.secondary");
            case "ultimate" -> transString("gui.talent.page.ultimate");
            default -> pageId;
        };
    }
}