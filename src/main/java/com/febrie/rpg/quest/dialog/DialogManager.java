package com.febrie.rpg.quest.dialog;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import com.febrie.rpg.util.LogUtil;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 퀘스트 대화 진행도 관리
 * 플레이어별 대화 상태를 메모리에서 관리
 *
 * @author Febrie
 */
public class DialogManager {

    private static DialogManager instance;
    private final RPGMain plugin;
    private final GuiManager guiManager;

    // 플레이어별 대화 진행도 (RAM only)
    private final Map<UUID, Map<String, DialogProgress>> playerDialogs = new ConcurrentHashMap<>();

    /**
     * 대화 진행도 정보
     */
    static class DialogProgress {
        private final String dialogId;
        private int currentLineIndex;
        private long lastInteractionTime;

        public DialogProgress(@NotNull String dialogId) {
            this.dialogId = dialogId;
            this.currentLineIndex = 0;
            this.lastInteractionTime = System.currentTimeMillis();
        }
    }

    /**
     * 프라이빗 생성자
     */
    private DialogManager(@NotNull RPGMain plugin, @NotNull GuiManager guiManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
    }

    /**
     * 싱글톤 초기화
     */
    public static void initialize(@NotNull RPGMain plugin, @NotNull GuiManager guiManager) {
        if (instance == null) {
            instance = new DialogManager(plugin, guiManager);
            LogUtil.info("DialogManager initialized");
        }
    }

    /**
     * 싱글톤 인스턴스
     */
    public static DialogManager getInstance() {
        if (instance == null) {
            throw new IllegalStateException("DialogManager is not initialized!");
        }
        return instance;
    }

    /**
     * 대화 시작 또는 재개
     */
    public void startDialog(@NotNull Player player, @NotNull QuestDialog dialog) {
        UUID playerId = player.getUniqueId();
        String dialogId = dialog.getId();

        // 현재 진행도 가져오기
        Map<String, DialogProgress> playerProgressMap = playerDialogs.computeIfAbsent(playerId, k -> new HashMap<>());
        DialogProgress progress = playerProgressMap.computeIfAbsent(dialogId, k -> new DialogProgress(dialogId));

        // 마지막 상호작용 시간 업데이트
        progress.lastInteractionTime = System.currentTimeMillis();

        // 현재 대화 표시
        showDialogLine(player, dialog, progress);
    }

    /**
     * 대화 라인 표시
     */
    private void showDialogLine(@NotNull Player player, @NotNull QuestDialog dialog, @NotNull DialogProgress progress) {
        QuestDialog.DialogLine line = dialog.getLine(progress.currentLineIndex);
        if (line == null) {
            // 대화 종료
            endDialog(player, dialog.getId());
            return;
        }

        if (line.hasChoices()) {
            // 선택지가 있으면 GUI로 표시
            showChoiceGUI(player, dialog, line, progress);
        } else {
            // 일반 대화는 채팅으로 표시
            player.sendMessage(line.toComponent(player));

            // 다음 대화로 자동 진행 (1.5초 후)
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                progress.currentLineIndex++;
                showDialogLine(player, dialog, progress);
            }, 30L); // 1.5초
        }
    }

    /**
     * 선택지 GUI 표시
     */
    private void showChoiceGUI(@NotNull Player player, @NotNull QuestDialog dialog,
                               @NotNull QuestDialog.DialogLine line, @NotNull DialogProgress progress) {
        // DialogChoiceGui 생성 및 표시
        DialogChoiceGui choiceGui = DialogChoiceGui.create(guiManager, player, dialog, line, progress);
        guiManager.openGui(player, choiceGui);
    }

    /**
     * 선택지 처리
     */
    public void handleChoice(@NotNull Player player, @NotNull String dialogId, @NotNull String choiceId) {
        UUID playerId = player.getUniqueId();

        Map<String, DialogProgress> playerProgressMap = playerDialogs.get(playerId);
        if (playerProgressMap == null) {
            return;
        }

        DialogProgress progress = playerProgressMap.get(dialogId);
        if (progress == null) {
            return;
        }

        // 선택에 따른 다음 대화 인덱스 찾기
        QuestDialog dialog = DialogRepository.getInstance().getDialog(dialogId);
        if (dialog != null) {
            QuestDialog.DialogLine currentLine = dialog.getLine(progress.currentLineIndex);
            if (currentLine != null && currentLine.hasChoices()) {
                for (QuestDialog.DialogChoice choice : currentLine.getChoices()) {
                    if (choice.id().equals(choiceId)) {
                        // 선택에 따른 다음 대화 인덱스로 이동
                        progress.currentLineIndex = choice.nextLineIndex();
                        showDialogLine(player, dialog, progress);
                        return;
                    }
                }
            }
        }
        
        // 선택지를 찾지 못한 경우
        player.sendMessage(Component.text("잘못된 선택입니다.", UnifiedColorUtil.ERROR));
    }

    /**
     * 대화 종료
     */
    public void endDialog(@NotNull Player player, @NotNull String dialogId) {
        UUID playerId = player.getUniqueId();

        Map<String, DialogProgress> playerProgressMap = playerDialogs.get(playerId);
        if (playerProgressMap != null) {
            playerProgressMap.remove(dialogId);

            // 맵이 비었으면 플레이어 엔트리도 제거
            if (playerProgressMap.isEmpty()) {
                playerDialogs.remove(playerId);
            }
        }

        player.sendMessage(Component.text("대화가 종료되었습니다.", UnifiedColorUtil.GRAY));
    }

    /**
     * 대화 진행도 초기화
     */
    public void resetDialog(@NotNull Player player, @NotNull String dialogId) {
        UUID playerId = player.getUniqueId();

        Map<String, DialogProgress> playerProgressMap = playerDialogs.get(playerId);
        if (playerProgressMap != null) {
            playerProgressMap.remove(dialogId);
        }
    }

    /**
     * 모든 대화 진행도 초기화
     */
    public void resetAllDialogs(@NotNull Player player) {
        playerDialogs.remove(player.getUniqueId());
    }

}