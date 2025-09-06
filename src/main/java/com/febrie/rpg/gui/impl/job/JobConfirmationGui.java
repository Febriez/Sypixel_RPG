package com.febrie.rpg.gui.impl.job;

import com.febrie.rpg.gui.component.GuiFactory;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.impl.player.ProfileGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.job.JobType;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;

import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LangKey;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;

/**
 * 직업 선택 확인 GUI
 * 직업을 최종적으로 선택하기 전에 한 번 더 확인하는 작은 GUI
 *
 * @author Febrie, CoffeeTory
 */
public class JobConfirmationGui extends BaseGui {

    private static final int GUI_SIZE = 27; // 3줄 (작은 확인창)

    private final RPGPlayer rpgPlayer;
    private final JobType selectedJob;

    private JobConfirmationGui(@NotNull GuiManager guiManager,
                               @NotNull Player player, @NotNull RPGPlayer rpgPlayer, @NotNull JobType selectedJob) {
        super(player, guiManager, GUI_SIZE, LangManager.text(LangKey.GUI_JOB_CONFIRMATION_TITLE, player));
        this.rpgPlayer = rpgPlayer;
        this.selectedJob = selectedJob;
    }
    
    /**
     * Factory method to create and initialize JobConfirmationGui
     */
    public static JobConfirmationGui create(@NotNull GuiManager guiManager,
                                           @NotNull Player player, @NotNull RPGPlayer rpgPlayer, @NotNull JobType selectedJob) {
        return new JobConfirmationGui(guiManager, player, rpgPlayer, selectedJob);
    }

    @Override
    public @NotNull Component getTitle() {
        return LangManager.text(LangKey.GUI_JOB_CONFIRMATION_TITLE, viewer);
    }

    @Override
    protected void setupLayout() {
        setupBackground();
        setupJobDisplay();
        setupConfirmationButtons();
    }

    /**
     * 배경 설정
     */
    private void setupBackground() {
        // 전체 배경을 빨간색 유리판으로 (위험을 강조)
        for (int i = 0; i < GUI_SIZE; i++) {
            setItem(i, GuiFactory.createDecoration(Material.RED_STAINED_GLASS_PANE));
        }
    }

    /**
     * 선택한 직업 표시
     */
    private void setupJobDisplay() {
        String jobKey = selectedJob.name().toLowerCase();

        // 중앙에 직업 정보 표시
        ItemBuilder builder = ItemBuilder.of(selectedJob.getMaterial())
                .displayName(Component.text(selectedJob.getIcon() + " ")
                        .append(LangManager.text(LangKey.valueOf("JOB_" + jobKey.toUpperCase() + "_NAME"), viewer))
                        .color(selectedJob.getColor())
                        .decoration(TextDecoration.BOLD, true))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.GUI_JOB_CONFIRMATION_SELECTED_JOB, viewer))
                .addLore(Component.empty());

        // 직업 설명
        List<Component> description = LangManager.list(LangKey.valueOf("JOB_" + jobKey.toUpperCase() + "_DESCRIPTION"), viewer);
        for (Component line : description) {
            builder.addLore(line);
        }

        builder.addLore(Component.empty())
                .addLore(LangManager.text(LangKey.GUI_JOB_CONFIRMATION_MAX_LEVEL, viewer, String.valueOf(selectedJob.getMaxLevel())))
                .addLore(Component.empty())
                .addLore(LangManager.text(LangKey.GENERAL_SEPARATOR, viewer))
                .addLore(LangManager.text(LangKey.GUI_JOB_CONFIRMATION_WARNING, viewer))
                .addLore(LangManager.text(LangKey.GUI_JOB_CONFIRMATION_CANNOT_CHANGE, viewer))
                .addLore(LangManager.text(LangKey.GENERAL_SEPARATOR, viewer))
                .flags(ItemFlag.values())
                .glint(true);

        setItem(13, GuiItem.display(builder.build()));

        // 추가 경고 아이콘들
        GuiItem warningItem = GuiItem.display(
                ItemBuilder.of(Material.BARRIER)
                        .displayName(LangManager.text(LangKey.GUI_JOB_CONFIRMATION_WARNING_TITLE, viewer))
                        .addLore(LangManager.text(LangKey.GUI_JOB_CONFIRMATION_WARNING_DESCRIPTION, viewer))
                        .build()
        );
        setItem(3, warningItem);
        setItem(5, warningItem);
    }

    /**
     * 확인/취소 버튼 설정
     */
    private void setupConfirmationButtons() {
        // 확인 버튼 (좌측)
        GuiItem confirmButton = GuiItem.clickable(
                ItemBuilder.of(Material.LIME_WOOL)
                        .displayName(LangManager.text(LangKey.GUI_JOB_CONFIRMATION_CONFIRM, viewer)
                                .color(UnifiedColorUtil.SUCCESS)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_JOB_CONFIRMATION_CONFIRM_DESCRIPTION, viewer, selectedJob.name()))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_JOB_CONFIRMATION_CLICK_TO_CONFIRM, viewer))
                        .glint(true)
                        .build(),
                player -> handleConfirm()
        );
        setItem(11, confirmButton);

        // 취소 버튼 (우측)
        GuiItem cancelButton = GuiItem.clickable(
                ItemBuilder.of(Material.RED_WOOL)
                        .displayName(LangManager.text(LangKey.GUI_JOB_CONFIRMATION_CANCEL, viewer)
                                .color(UnifiedColorUtil.ERROR)
                                .decoration(TextDecoration.BOLD, true))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_JOB_CONFIRMATION_CANCEL_DESCRIPTION, viewer))
                        .addLore(Component.empty())
                        .addLore(LangManager.text(LangKey.GUI_JOB_CONFIRMATION_CLICK_TO_CANCEL, viewer))
                        .build(),
                player -> handleCancel()
        );
        setItem(15, cancelButton);

        // 추가 정보 아이템 (하단 중앙)
        GuiItem infoItem = GuiItem.display(
                ItemBuilder.of(Material.BOOK)
                        .displayName(LangManager.text(LangKey.GUI_JOB_CONFIRMATION_INFO_TITLE, viewer))
                        .addLore(LangManager.text(LangKey.GUI_JOB_CONFIRMATION_INFO_LINE1, viewer))
                        .addLore(LangManager.text(LangKey.GUI_JOB_CONFIRMATION_INFO_LINE2, viewer))
                        .addLore(LangManager.text(LangKey.GUI_JOB_CONFIRMATION_INFO_LINE3, viewer))
                        .build()
        );
        setItem(22, infoItem);
    }

    /**
     * 확인 버튼 처리
     */
    private void handleConfirm() {
        if (rpgPlayer.setJob(selectedJob)) {
            Component jobName = LangManager.text(LangKey.valueOf("JOB_" + selectedJob.name().toUpperCase() + "_NAME"), viewer);

            // 성공 메시지
            viewer.sendMessage(LangManager.text(LangKey.GUI_JOB_CONFIRMATION_SUCCESS, viewer, jobName));

            // 축하 효과
            playSuccessSound(viewer);

            // Title 표시
            viewer.showTitle(Title.title(
                    LangManager.text(LangKey.GUI_JOB_CONFIRMATION_TITLE_SUCCESS, viewer),
                    LangManager.text(LangKey.GUI_JOB_CONFIRMATION_SUBTITLE_SUCCESS, viewer, jobName),
                    Title.Times.times(
                            Duration.ofMillis(500),   // fadeIn
                            Duration.ofMillis(3000),  // stay
                            Duration.ofMillis(1000)   // fadeOut
                    )
            ));

            // GUI 닫고 프로필로 이동
            viewer.closeInventory();

            // 프로필 GUI 열기
            ProfileGui profileGui = ProfileGui.create(guiManager, viewer);
            guiManager.openGui(viewer, profileGui);

        } else {
            // 이미 직업이 있는 경우 (보통 일어나지 않아야 함)
            viewer.sendMessage(LangManager.text(LangKey.GUI_JOB_CONFIRMATION_ALREADY_HAS_JOB, viewer));
            playErrorSound(viewer);
            viewer.closeInventory();
        }
    }

    /**
     * 취소 버튼 처리
     */
    private void handleCancel() {
        // 이전 화면으로 돌아가기
        playClickSound(viewer);
        GuiFramework backTarget = getBackTarget();
        if (backTarget != null) {
            guiManager.openGui(viewer, backTarget);
        } else {
            // 백타겟이 없으면 직업 선택 GUI로
            JobSelectionGui jobSelectionGui = JobSelectionGui.create(guiManager, viewer, rpgPlayer);
            guiManager.openGui(viewer, jobSelectionGui);
        }
    }

    @Override
    public GuiFramework getBackTarget() {
        // JobConfirmationGui는 JobSelectionGui로 돌아갑니다
        return JobSelectionGui.create(guiManager, viewer, rpgPlayer);
    }
}
