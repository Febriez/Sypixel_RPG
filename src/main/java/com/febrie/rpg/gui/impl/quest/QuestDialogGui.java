package com.febrie.rpg.gui.impl.quest;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.component.GuiItem;
import com.febrie.rpg.gui.framework.BaseGui;
import com.febrie.rpg.gui.framework.GuiFramework;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.quest.Quest;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.ItemBuilder;
import com.febrie.rpg.util.SoundUtil;
import com.febrie.rpg.util.TextUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 퀘스트 대화 GUI
 * 한글자씩 애니메이션으로 대화를 표시하는 시스템
 *
 * @author Febrie
 */
public class QuestDialogGui extends BaseGui {

    private final Quest quest;

    // 대화 상태
    private int currentDialogIndex = 0;
    private int currentCharIndex = 0;
    private String currentDialog = "";
    private boolean isTyping = false;
    private boolean isDialogComplete = false;

    // 애니메이션 관리
    private BukkitTask typingTask;
    private final int typingSpeed; // 틱 단위 (기본값: 2틱 = 100ms)

    // 슬롯 정의
    private static final int DIALOG_SLOT = 4; // 가운데 슬롯
    private static final int EXIT_SLOT = 0; // 나가기 버튼
    private static final int ACCEPT_SLOT = 3; // 수락 버튼
    private static final int DECLINE_SLOT = 5; // 거절 버튼

    private QuestDialogGui(@NotNull GuiManager guiManager,
                           @NotNull Player player, @NotNull Quest quest) {
        super(player, guiManager, 9, "gui.quest-dialog.title");
        this.quest = quest;
        this.typingSpeed = getTypingSpeed(player); // 플레이어 설정에서 가져오기
    }

    /**
     * QuestDialogGui 인스턴스를 생성하고 초기화합니다.
     *
     * @param guiManager GUI 매니저
     * @param player     플레이어
     * @param quest      퀘스트
     * @return 초기화된 QuestDialogGui 인스턴스
     */
    public static QuestDialogGui create(@NotNull GuiManager guiManager,
                                        @NotNull Player player, @NotNull Quest quest) {
        QuestDialogGui gui = new QuestDialogGui(guiManager, player, quest);
        return gui;
    }

    /**
     * 플레이어의 대화 속도 설정 가져오기
     */
    private int getTypingSpeed(@NotNull Player player) {
        return RPGMain.getPlugin().getRPGPlayerManager().getOrCreatePlayer(player).getPlayerSettings().getDialogSpeed();
    }

    @Override
    protected void setupLayout() {
        setupDialogGui();
        startDialog();
    }

    @Override
    public @NotNull Component getTitle() {
        return Component.translatable("gui.quest-dialog.title")
                .color(UnifiedColorUtil.GOLD);
    }

    @Override
    protected GuiFramework getBackTarget() {
        return null; // 뒤로가기 없음 (대화 중)
    }

    /**
     * 대화 GUI 초기 설정
     */
    private void setupDialogGui() {
        // 나가기 버튼 설정
        setItem(EXIT_SLOT, createExitButton());

        // 빈 슬롯들을 투명한 유리판으로 채우기
        for (int i = 1; i < 9; i++) {
            if (i != DIALOG_SLOT) {
                setItem(i, GuiItem.display(
                        new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                                .displayName(Component.empty())
                                .build()
                ));
            }
        }
    }

    /**
     * 대화 시작
     */
    private void startDialog() {
        // 퀘스트 대화가 있는지 확인
        if (quest.getDialogCount() > 0) {
            // 대화가 있으면 첫 번째 대화 로드
            loadCurrentDialog();
            startTypingAnimation();
        } else {
            // 대화가 없으면 바로 수락/거절 선택 표시
            showQuestChoice();
        }
    }

    /**
     * 현재 대화 로드
     */
    private void loadCurrentDialog() {
        Component dialogComponent = quest.getDialog(currentDialogIndex, viewer);

        if (dialogComponent != null) {
            // Component를 플레인 텍스트로 변환 (애니메이션을 위해)

            currentDialog = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                    .serialize(dialogComponent);
            currentCharIndex = 0;
            isDialogComplete = false;
            isTyping = true;
            // 초기 대화 아이템 표시
            updateDialogItem("", false);
        }
    }

    /**
     * 타이핑 애니메이션 시작
     */
    private void startTypingAnimation() {
        if (typingTask != null) {
            typingTask.cancel();
        }

        typingTask = Bukkit.getScheduler().runTaskTimer(
                plugin,
                this::updateDialogDisplay,
                0L,
                typingSpeed
        );
    }

    /**
     * 대화 표시 업데이트
     */
    private void updateDialogDisplay() {
        if (!isTyping || currentCharIndex >= currentDialog.length()) {
            // 타이핑 완료
            isTyping = false;
            isDialogComplete = true;
            if (typingTask != null) {
                typingTask.cancel();
                typingTask = null;
            }
            updateDialogItem(currentDialog, true);
            return;
        }

        // 한글자씩 표시
        currentCharIndex++;
        String displayText = currentDialog.substring(0, currentCharIndex);
        updateDialogItem(displayText, false);
    }

    /**
     * 대화 아이템 업데이트
     */
    private void updateDialogItem(@NotNull String text, boolean isComplete) {
        List<Component> lore = new ArrayList<>();

        // 텍스트를 로어로 분할 (한 줄당 최대 40자)
        String[] lines = TextUtil.wrapText(text, 40);
        for (String line : lines) {
            lore.add(Component.text(line, UnifiedColorUtil.WHITE));
        }

        // 상태 표시
        lore.add(Component.empty());
        if (isComplete) {
            if (currentDialogIndex < quest.getDialogCount() - 1) {
                lore.add(Component.translatable("gui.quest-dialog.next-page")
                        .color(UnifiedColorUtil.SUCCESS));
            } else {
                lore.add(Component.translatable("gui.quest-dialog.accept-quest")
                        .color(UnifiedColorUtil.GOLD));
            }
        } else {
            lore.add(Component.translatable("gui.quest-dialog.skip")
                    .color(UnifiedColorUtil.YELLOW));
        }

        Component npcNameComponent = quest.getNPCName(viewer);
        String npcName = net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer.plainText()
                .serialize(npcNameComponent);

        ItemBuilder dialogBuilder = new ItemBuilder(Material.ENCHANTED_BOOK)
                .displayName(Component.text(npcName, UnifiedColorUtil.AQUA));

        for (Component loreLine : lore) {
            dialogBuilder.addLore(loreLine);
        }

        GuiItem dialogItem = GuiItem.clickable(dialogBuilder.build(), this::handleDialogClick);
        setItem(DIALOG_SLOT, dialogItem);
    }


    /**
     * 대화 클릭 처리
     */
    private void handleDialogClick(@NotNull Player player) {
        if (isTyping) {
            // 타이핑 중이면 즉시 완료
            stopTyping();
            isDialogComplete = true;
            updateDialogItem(currentDialog, true);
            SoundUtil.playClickSound(player);
        } else if (isDialogComplete) {
            // 대화가 완료된 상태에서 클릭
            if (currentDialogIndex < quest.getDialogCount() - 1) {
                // 다음 대화로
                currentDialogIndex++;
                loadCurrentDialog();
                startTypingAnimation();
                SoundUtil.playClickSound(player);
            } else {
                // 마지막 대화 완료, 수락/거절 선택 표시
                showQuestChoice();
            }
        }
    }

    /**
     * 타이핑 애니메이션 중지
     */
    private void stopTyping() {
        if (typingTask != null) {
            typingTask.cancel();
            typingTask = null;
        }
        isTyping = false;
    }

    /**
     * 퀘스트 수락/거절 선택 표시
     */
    private void showQuestChoice() {
        stopTyping();

        for (int i = 1; i < 9; i++) {
            setItem(i, GuiItem.display(
                    new ItemBuilder(Material.LIGHT_GRAY_STAINED_GLASS_PANE)
                            .displayName(Component.empty())
                            .build()
            ));
        }

        // 나가기 버튼 유지
        setItem(EXIT_SLOT, createExitButton());

        // 퀘스트 정보 표시
        ItemBuilder questInfoBuilder = new ItemBuilder(Material.WRITTEN_BOOK)
                .displayName(quest.getDisplayName(viewer).color(UnifiedColorUtil.GOLD))
                .addLore(Component.empty());

        // 퀘스트 설명 추가
        for (Component line : quest.getDisplayInfo(viewer)) {
            questInfoBuilder.addLore(line.color(UnifiedColorUtil.WHITE));
        }

        setItem(DIALOG_SLOT, GuiItem.display(questInfoBuilder.build()));

        // 수락 버튼
        GuiItem acceptButton = GuiItem.clickable(
                new ItemBuilder(Material.LIME_DYE)
                        .displayName(Component.translatable("gui.quest-dialog.accept-quest")
                                .color(UnifiedColorUtil.SUCCESS))
                        .addLore(Component.translatable("gui.quest-accept.accept-desc")
                                .color(UnifiedColorUtil.GRAY))
                        .build(),
                p -> handleQuestAccept()
        );
        setItem(ACCEPT_SLOT, acceptButton);

        // 거절 버튼
        GuiItem declineButton = GuiItem.clickable(
                new ItemBuilder(Material.RED_DYE)
                        .displayName(Component.translatable("gui.quest-dialog.decline-quest")
                                .color(UnifiedColorUtil.ERROR))
                        .addLore(Component.translatable("gui.quest-accept.decline-desc")
                                .color(UnifiedColorUtil.GRAY))
                        .build(),
                p -> handleQuestDecline()
        );
        setItem(DECLINE_SLOT, declineButton);
    }

    /**
     * 퀘스트 수락 처리
     */
    private void handleQuestAccept() {
        // GUI 즉시 닫기
        viewer.closeInventory();

        // 퀘스트 시작
        com.febrie.rpg.quest.manager.QuestManager.getInstance().startQuest(viewer, quest.getId());

        // 수락 대화 표시 (Component 직접 조합)
        Component npcNameComponent = quest.getNPCName(viewer);
        Component acceptDialog = quest.getAcceptDialog(viewer);
        
        // NPC 이름: 대화 형식으로 Component 조합
        Component message = npcNameComponent.color(UnifiedColorUtil.YELLOW)
                .append(Component.text(": ", UnifiedColorUtil.YELLOW))
                .append(acceptDialog.color(UnifiedColorUtil.YELLOW));
        
        viewer.sendMessage(message);
        SoundUtil.playSuccessSound(viewer);
    }

    /**
     * 퀘스트 거절 처리
     */
    private void handleQuestDecline() {
        // GUI 즉시 닫기
        viewer.closeInventory();

        // 거절 대화 표시 (Component 직접 조합)
        Component npcNameComponent = quest.getNPCName(viewer);
        Component declineDialog = quest.getDeclineDialog(viewer);
        
        // NPC 이름: 대화 형식으로 Component 조합
        Component message = npcNameComponent.color(UnifiedColorUtil.GRAY)
                .append(Component.text(": ", UnifiedColorUtil.GRAY))
                .append(declineDialog.color(UnifiedColorUtil.GRAY));
        
        viewer.sendMessage(message);
        viewer.sendMessage(Component.translatable("gui.quest-dialog.quest-declined")
                .color(UnifiedColorUtil.GRAY));
        SoundUtil.playClickSound(viewer);
    }
    
    /**
     * 나가기 버튼 생성
     */
    private GuiItem createExitButton() {
        return GuiItem.clickable(
                new ItemBuilder(Material.BARRIER)
                        .displayName(Component.translatable("gui.quest-dialog.close")
                                .color(UnifiedColorUtil.ERROR))
                        .addLore(Component.translatable("gui.buttons.close.lore")
                                .color(UnifiedColorUtil.GRAY))
                        .build(),
                p -> {
                    stopTyping();
                    p.closeInventory();
                    SoundUtil.playCloseSound(p);
                }
        );
    }


}