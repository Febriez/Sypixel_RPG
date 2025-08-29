package com.febrie.rpg.gui.impl.job;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.impl.player.ProfileGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.job.JobType;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 직업 선택 GUI
 * 플레이어가 직업을 선택할 수 있는 인터페이스
 *
 * @author Febrie, CoffeeTory
 */
public class JobSelectionGui extends BaseGui {

    private static final int GUI_SIZE = 54; // 6줄

    private final RPGPlayer rpgPlayer;
    private JobType.JobCategory selectedCategory = JobType.JobCategory.WARRIOR;

    private JobSelectionGui(@NotNull GuiManager guiManager,
                           @NotNull Player viewer, @NotNull RPGPlayer rpgPlayer) {
        super(viewer, guiManager, GUI_SIZE, LangManager.getComponent("gui.job_selection.title", viewer.locale()));
        this.rpgPlayer = rpgPlayer;
    }

    /**
     * JobSelectionGui 인스턴스를 생성하고 초기화합니다.
     * 
     * @param guiManager GUI 매니저
     * @param viewer 보는 플레이어
     * @param rpgPlayer RPG 플레이어
     * @return 초기화된 JobSelectionGui 인스턴스
     */
    public static JobSelectionGui create(@NotNull GuiManager guiManager,
                                        @NotNull Player viewer, @NotNull RPGPlayer rpgPlayer) {
        return new JobSelectionGui(guiManager, viewer, rpgPlayer);
    }

    @Override
    public @NotNull Component getTitle() {
        return LangManager.getComponent("gui.job_selection.title", viewer.locale());
    }

    @Override
    public int getSize() {
        return GUI_SIZE;
    }

    @Override
    protected void setupLayout() {
        setupBackground();
        setupCategoryTabs();
        setupJobDisplay();
        setupNavigationButtons();
    }

    /**
     * 배경 설정
     */
    private void setupBackground() {
        // 상단 테두리
        for (int i = 0; i < 9; i++) {
            if (i < 3 || i > 5) { // 카테고리 탭 위치 제외
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 중간 구분선
        for (int i = 18; i < 27; i++) {
            setItem(i, GuiFactory.createDecoration(Material.GRAY_STAINED_GLASS_PANE));
        }

        // 하단 테두리 - 네비게이션 버튼 위치 제외
        for (int i = 45; i < 54; i++) {
            if (i != getBackButtonSlot() && i != getCloseButtonSlot()) {
                setItem(i, GuiFactory.createDecoration());
            }
        }

        // 좌우 테두리
        setItem(9, GuiFactory.createDecoration());
        setItem(17, GuiFactory.createDecoration());
        setItem(36, GuiFactory.createDecoration());
        setItem(44, GuiFactory.createDecoration());
    }

    /**
     * 카테고리 탭 설정
     */
    private void setupCategoryTabs() {
        int tabSlot = 3;

        for (JobType.JobCategory category : JobType.JobCategory.values()) {
            boolean isSelected = category == selectedCategory;

            GuiItem tabItem = GuiItem.clickable(
                    ItemBuilder.of(isSelected ? Material.ENCHANTED_BOOK : Material.BOOK)
                            .displayName(LangManager.getComponent("job.categories." + category.name().toLowerCase(), viewer.locale())
                                    .color(category.getColor())
                                    .decoration(TextDecoration.BOLD, isSelected))
                            .addLore(Component.empty())
                            .addLore(isSelected ?
                                    LangManager.getComponent("gui.job_selection.tab_selected", viewer.locale()) :
                                    LangManager.getComponent("gui.job_selection.tab_click", viewer.locale()))
                            .glint(isSelected)
                            .build(),
                    player -> {
                        if (!isSelected) {
                            selectedCategory = category;
                            playClickSound(player);
                            refresh();
                        }
                    }
            );
            setItem(tabSlot++, tabItem);
        }
    }

    /**
     * 직업 표시
     */
    private void setupJobDisplay() {
        // 선택된 카테고리의 직업들만 표시
        List<JobType> categoryJobs = new ArrayList<>();
        for (JobType job : JobType.values()) {
            if (job.getCategory() == selectedCategory) {
                categoryJobs.add(job);
            }
        }

        // 직업들을 그리드로 배치
        int[] slots = {
                28, 29, 30, 31, 32, 33, 34,  // 4번째 줄
                37, 38, 39, 40, 41, 42, 43   // 5번째 줄
        };

        for (int i = 0; i < categoryJobs.size() && i < slots.length; i++) {
            JobType job = categoryJobs.get(i);
            setItem(slots[i], createJobItem(job));
        }

        // 빈 슬롯은 장식 아이템으로 채우기
        for (int i = categoryJobs.size(); i < slots.length; i++) {
            setItem(slots[i], GuiFactory.createDecoration(Material.GRAY_STAINED_GLASS_PANE));
        }
    }

    /**
     * 직업 아이템 생성
     */
    private GuiItem createJobItem(@NotNull JobType job) {
        String jobKey = job.name().toLowerCase();

        ItemBuilder builder = ItemBuilder.of(job.getMaterial())
                .displayName(Component.text(job.getIcon() + " ")
                        .append(LangManager.getComponent("job." + jobKey + ".name", viewer.locale()))
                        .decoration(TextDecoration.BOLD, true))
                .addLore(Component.empty())
                .addLore(LangManager.getComponent("gui.job-selection.max-level", viewer.locale(), String.valueOf(job.getMaxLevel())))
                .addLore(Component.empty());

        // 직업 설명 추가
        List<Component> description = List.of(); /* TODO: Convert LangManager.getList("job." + jobKey + ".description") manually */
        for (Component line : description) {
            builder.addLore(line);
        }

        builder.addLore(Component.empty())
                .addLore(LangManager.getComponent("general.separator", viewer.locale()))
                .addLore(LangManager.getComponent("gui.job-selection.warning", viewer.locale()))
                .addLore(LangManager.getComponent("general.separator", viewer.locale()))
                .addLore(Component.empty())
                .addLore(LangManager.getComponent("gui.job-selection.click-to-choose", viewer.locale()))
                .flags(ItemFlag.values())
                .glint(true);

        return GuiItem.clickable(
                builder.build(),
                player -> openConfirmationGui(job)
        );
    }

    /**
     * 네비게이션 버튼 설정
     */
    private void setupNavigationButtons() {
        // 표준 네비게이션 설정 사용
        setupStandardNavigation(false, true); // refresh 버튼 없음, close 버튼 있음
    }

    /**
     * 직업 선택 확인 GUI 열기
     */
    private void openConfirmationGui(@NotNull JobType job) {
        // 확인 GUI 열기
        JobConfirmationGui confirmationGui = JobConfirmationGui.create(
                guiManager, viewer, rpgPlayer, job
        );
        guiManager.openGui(viewer, confirmationGui);
        playClickSound(viewer);
    }

    @Override
    public GuiFramework getBackTarget() {
        // ProfileGui로 돌아가기
        return ProfileGui.create(guiManager, viewer, rpgPlayer.getPlayer());
    }
}