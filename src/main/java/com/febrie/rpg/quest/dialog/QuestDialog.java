package com.febrie.rpg.quest.dialog;

import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 퀘스트 대화 정보
 * NPC와의 대화 내용을 관리
 * Component 기반으로 모든 텍스트 처리
 *
 * @author Febrie
 */
public class QuestDialog {

    private final String id;
    private final List<DialogLine> lines;
    private String npcName;

    /**
     * 생성자
     */
    public QuestDialog(@NotNull String id) {
        this.id = id;
        this.lines = new ArrayList<>();
        this.npcName = "Quest NPC";
    }
    
    /**
     * NPC 이름 설정
     */
    public QuestDialog setNpcName(@NotNull String npcName) {
        this.npcName = npcName;
        return this;
    }
    
    /**
     * NPC 이름 가져오기
     */
    @Nullable
    public String getNpcName() {
        return npcName;
    }
    
    /**
     * 대화 목록 가져오기 (레거시 호환성)
     */
    public @NotNull List<String> getDialogues() {
        List<String> dialogues = new ArrayList<>();
        for (DialogLine line : lines) {
            // 간단한 텍스트 표현 반환
            dialogues.add(line.messageKey != null ? line.messageKey : "");
        }
        return dialogues;
    }

    /**
     * 대화 추가 (키 기반)
     */
    public QuestDialog addLine(@NotNull String speakerKey, @NotNull String messageKey) {
        lines.add(new DialogLine(speakerKey, messageKey, null));
        return this;
    }

    /**
     * 대화 추가 (레거시 호환성 - Component 버전)
     * @deprecated Use {@link #addLine(String, String)}
     */
    @Deprecated
    public QuestDialog addLine(@NotNull Component speaker, @NotNull Component messageKo, @NotNull Component messageEn) {
        lines.add(new DialogLine(speaker, messageKo, messageEn, null));
        return this;
    }

    /**
     * 선택지가 있는 대화 추가 (키 기반)
     */
    public QuestDialog addLineWithChoices(@NotNull String speakerKey, @NotNull String messageKey, 
                                          @NotNull List<DialogChoice> choices) {
        lines.add(new DialogLine(speakerKey, messageKey, choices));
        return this;
    }

    /**
     * 선택지가 있는 대화 추가 (레거시 Component 버전 - 호환성)
     * @deprecated Use {@link #addLineWithChoices(String, String, List)}
     */
    @Deprecated
    public QuestDialog addLineWithChoices(@NotNull Component speaker, @NotNull Component messageKo,
                                          @NotNull Component messageEn, @NotNull List<DialogChoice> choices) {
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
        // 키 기반 필드
        private final String speakerKey;
        private final String messageKey;
        
        // 레거시 Component 필드 (호환성)
        private final Component speaker;
        private final Component messageKo;
        private final Component messageEn;
        
        private final List<DialogChoice> choices;

        // 새로운 키 기반 생성자
        public DialogLine(@NotNull String speakerKey, @NotNull String messageKey,
                          @Nullable List<DialogChoice> choices) {
            this.speakerKey = speakerKey;
            this.messageKey = messageKey;
            this.speaker = null;
            this.messageKo = null;
            this.messageEn = null;
            this.choices = choices != null ? new ArrayList<>(choices) : null;
        }
        
        // 레거시 Component 생성자 (호환성)
        @Deprecated
        public DialogLine(@NotNull Component speaker, @NotNull Component messageKo,
                          @NotNull Component messageEn, @Nullable List<DialogChoice> choices) {
            this.speaker = speaker;
            this.messageKo = messageKo;
            this.messageEn = messageEn;
            this.speakerKey = null;
            this.messageKey = null;
            this.choices = choices != null ? new ArrayList<>(choices) : null;
        }

        /**
         * 화자 이름 Component (Player 기반)
         */
        public @NotNull Component getSpeaker(@NotNull Player player) {
            if (speakerKey != null) {
                return LangManager.getMessage(player, speakerKey);
            }
            // 레거시 호환성
            return speaker != null ? speaker : Component.text("Unknown");
        }

        /**
         * 메시지 Component (Player 기반)
         */
        public @NotNull Component getMessage(@NotNull Player player) {
            if (messageKey != null) {
                return LangManager.getMessage(player, messageKey);
            }
            // 레거시 호환성
            return messageKo != null ? messageKo : (messageEn != null ? messageEn : Component.text("Unknown"));
        }

        /**
         * 선택지 여부
         */
        public boolean hasChoices() {
            return choices != null && !choices.isEmpty();
        }

        /**
         * 선택지 목록
         */
        @Nullable
        public List<DialogChoice> getChoices() {
            return choices != null ? new ArrayList<>(choices) : null;
        }

        /**
         * 채팅 표시용 Component (Player 기반)
         */
        public Component toComponent(@NotNull Player player) {
            return Component.text("[", NamedTextColor.GRAY)
                    .append(getSpeaker(player).color(NamedTextColor.YELLOW))
                    .append(Component.text("] ", NamedTextColor.GRAY))
                    .append(getMessage(player).color(NamedTextColor.WHITE));
        }
    }

    /**
     * 대화 선택지 클래스
     */
    public static class DialogChoice {
        private final String id;
        private final String textKey;  // 키 기반
        private final Component textKo;  // 레거시
        private final Component textEn;  // 레거시
        private final int nextLineIndex;

        // 새로운 키 기반 생성자
        public DialogChoice(@NotNull String id, @NotNull String textKey, int nextLineIndex) {
            this.id = id;
            this.textKey = textKey;
            this.textKo = null;
            this.textEn = null;
            this.nextLineIndex = nextLineIndex;
        }
        
        // 레거시 Component 생성자 (호환성)
        @Deprecated
        public DialogChoice(@NotNull String id, @NotNull Component textKo,
                            @NotNull Component textEn, int nextLineIndex) {
            this.id = id;
            this.textKo = textKo;
            this.textEn = textEn;
            this.textKey = null;
            this.nextLineIndex = nextLineIndex;
        }

        /**
         * 선택지 ID
         */
        public @NotNull String getId() {
            return id;
        }

        /**
         * 선택지 텍스트 Component (Player 기반)
         */
        public @NotNull Component getText(@NotNull Player player) {
            if (textKey != null) {
                return LangManager.getMessage(player, textKey);
            }
            // 레거시 호환성
            return textKo != null ? textKo : (textEn != null ? textEn : Component.text("Unknown"));
        }

        /**
         * 다음 대화 인덱스
         */
        public int getNextLineIndex() {
            return nextLineIndex;
        }
    }
}