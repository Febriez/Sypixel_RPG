package com.febrie.rpg.dto;

/**
 * 완료된 퀘스트 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie
 */
public record CompletedQuestDTO(
        String questId,
        long startedAt,      // 퀘스트 시작 시간
        long completedAt,    // 퀘스트 완료 시간
        long duration,       // 소요 시간 (밀리초)
        int rewardsClaimed   // 보상 수령 횟수 (반복 퀘스트용)
) {
    /**
     * 간편 생성자
     */
    public CompletedQuestDTO(String questId, long startedAt, long completedAt) {
        this(questId, startedAt, completedAt, completedAt - startedAt, 1);
    }

    /**
     * 소요 시간을 분 단위로 반환
     */
    public long getDurationInMinutes() {
        return duration / (1000 * 60);
    }

    /**
     * 소요 시간을 시간 단위로 반환
     */
    public double getDurationInHours() {
        return duration / (1000.0 * 60 * 60);
    }

    /**
     * 포맷된 소요 시간 문자열 반환
     */
    public String getFormattedDuration() {
        long seconds = duration / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return String.format("%d일 %d시간 %d분", days, hours % 24, minutes % 60);
        } else if (hours > 0) {
            return String.format("%d시간 %d분", hours, minutes % 60);
        } else if (minutes > 0) {
            return String.format("%d분 %d초", minutes, seconds % 60);
        } else {
            return String.format("%d초", seconds);
        }
    }
}