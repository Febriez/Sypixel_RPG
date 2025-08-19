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
     * 대화 추가 (키 기반)
     */
    public QuestDialog addLine(@NotNull String speakerKey, @NotNull String messageKey) {
        lines.add(new DialogLine(speakerKey, messageKey, null));
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
     * 대화 라인 레코드
     */
    public record DialogLine(@NotNull String speakerKey, @NotNull String messageKey, 
                           @Nullable List<DialogChoice> choices) {

        public DialogLine(@NotNull String speakerKey, @NotNull String messageKey,
                          @Nullable List<DialogChoice> choices) {
            this.speakerKey = speakerKey;
            this.messageKey = messageKey;
            this.choices = choices != null ? new ArrayList<>(choices) : null;
        }

        /**
         * 화자 이름 Component (Player 기반)
         */
        public @NotNull Component getSpeaker(@NotNull Player player) {
            return Component.translatable(speakerKey);
        }

        /**
         * 메시지 Component (Player 기반)
         */
        public @NotNull Component getMessage(@NotNull Player player) {
            return Component.translatable(messageKey);
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
     * 대화 선택지 record
     */
    public record DialogChoice(@NotNull String id, @NotNull String textKey, int nextLineIndex) {
        
        /**
         * 선택지 텍스트 Component
         */
        public @NotNull Component getText() {
            return Component.translatable(textKey);
        }
    }
}