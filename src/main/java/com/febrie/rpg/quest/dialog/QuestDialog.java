package com.febrie.rpg.quest.dialog;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 퀘스트 대화 정보
 * NPC와의 대화 내용을 관리
 *
 * @author Febrie
 */
public class QuestDialog {

    private final String id;
    private final List<DialogLine> lines;

    /**
     * 생성자
     */
    public QuestDialog(@NotNull String id) {
        this.id = id;
        this.lines = new ArrayList<>();
    }

    /**
     * 대화 추가
     */
    public QuestDialog addLine(@NotNull String speaker, @NotNull String messageKo, @NotNull String messageEn) {
        lines.add(new DialogLine(speaker, messageKo, messageEn, null));
        return this;
    }

    /**
     * 선택지가 있는 대화 추가
     */
    public QuestDialog addLineWithChoices(@NotNull String speaker, @NotNull String messageKo,
                                          @NotNull String messageEn, @NotNull List<DialogChoice> choices) {
        lines.add(new DialogLine(speaker, messageKo, messageEn, choices));
        return this;
    }

    /**
     * 대화 ID
     */
    public @NotNull String getId() {
        return id;
    }

    /**
     * 전체 대화 라인
     */
    public @NotNull List<DialogLine> getLines() {
        return new ArrayList<>(lines);
    }

    /**
     * 특정 인덱스의 대화 가져오기
     */
    @Nullable
    public DialogLine getLine(int index) {
        if (index < 0 || index >= lines.size()) {
            return null;
        }
        return lines.get(index);
    }

    /**
     * 대화 라인 수
     */
    public int getLineCount() {
        return lines.size();
    }

    /**
     * 대화 라인 클래스
     */
    public static class DialogLine {
        private final String speaker;
        private final String messageKo;
        private final String messageEn;
        private final List<DialogChoice> choices;

        public DialogLine(@NotNull String speaker, @NotNull String messageKo,
                          @NotNull String messageEn, @Nullable List<DialogChoice> choices) {
            this.speaker = speaker;
            this.messageKo = messageKo;
            this.messageEn = messageEn;
            this.choices = choices != null ? new ArrayList<>(choices) : null;
        }

        public @NotNull String getSpeaker() {
            return speaker;
        }

        public @NotNull String getMessage(boolean isKorean) {
            return isKorean ? messageKo : messageEn;
        }

        public boolean hasChoices() {
            return choices != null && !choices.isEmpty();
        }

        @Nullable
        public List<DialogChoice> getChoices() {
            return choices != null ? new ArrayList<>(choices) : null;
        }

        /**
         * Component로 변환 (채팅 표시용)
         */
        public Component toComponent(boolean isKorean) {
            return Component.text("[" + speaker + "] " + getMessage(isKorean));
        }
    }

    /**
     * 대화 선택지 클래스
     */
    public static class DialogChoice {
        private final String id;
        private final String textKo;
        private final String textEn;
        private final int nextLineIndex; // 선택시 이동할 대화 인덱스 (-1이면 대화 종료)

        public DialogChoice(@NotNull String id, @NotNull String textKo,
                            @NotNull String textEn, int nextLineIndex) {
            this.id = id;
            this.textKo = textKo;
            this.textEn = textEn;
            this.nextLineIndex = nextLineIndex;
        }

        public @NotNull String getId() {
            return id;
        }

        public @NotNull String getText(boolean isKorean) {
            return isKorean ? textKo : textEn;
        }

        public int getNextLineIndex() {
            return nextLineIndex;
        }
    }
}