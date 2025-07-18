package com.febrie.rpg.dto.quest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 완료된 퀘스트 정보 DTO
 * Firebase 저장용 데이터 구조
 *
 * @author Febrie
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletedQuestDTO {
    private String questId;
    private long completedAt;    // 퀘스트 완료 시간
    private int completionCount; // 완료 횟수 (반복 퀘스트용)
    private boolean rewarded;    // 보상 수령 여부
    
    /**
     * 간편 생성자 (이전 버전 호환용)
     */
    public CompletedQuestDTO(String questId, long completedAt, int completionCount) {
        this(questId, completedAt, completionCount, false);
    }

}