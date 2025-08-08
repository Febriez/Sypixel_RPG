package com.febrie.rpg.quest.manager;

import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.dto.quest.ActiveQuestDTO;
import com.febrie.rpg.dto.quest.ClaimedQuestDTO;
import com.febrie.rpg.dto.quest.CompletedQuestDTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map;

/**
 * QuestManager용 헬퍼 메서드들
 * 새로운 데이터 구조로의 전환을 돕는 유틸리티 클래스
 * 
 * @author Febrie
 */
public class QuestManagerHelper {
    
    /**
     * 특정 퀘스트 ID로 활성 퀘스트 찾기
     */
    @Nullable
    public static ActiveQuestDTO findActiveQuest(@NotNull Map<String, ActiveQuestDTO> activeQuests, 
                                                  @NotNull QuestID questId) {
        return activeQuests.values().stream()
                .filter(a -> a.questId().equals(questId.name()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 특정 퀘스트 ID로 완료된 퀘스트 찾기
     */
    @Nullable
    public static CompletedQuestDTO findCompletedQuest(@NotNull Map<String, CompletedQuestDTO> completedQuests,
                                                       @NotNull QuestID questId) {
        return completedQuests.values().stream()
                .filter(c -> c.questId().equals(questId.name()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 특정 퀘스트 ID로 보상 수령한 퀘스트 찾기
     */
    @Nullable
    public static ClaimedQuestDTO findClaimedQuest(@NotNull Map<String, ClaimedQuestDTO> claimedQuests,
                                                   @NotNull QuestID questId) {
        return claimedQuests.values().stream()
                .filter(c -> c.questId().equals(questId.name()))
                .findFirst()
                .orElse(null);
    }
    
    /**
     * 퀘스트가 활성 상태인지 확인
     */
    public static boolean hasActiveQuest(@NotNull Map<String, ActiveQuestDTO> activeQuests,
                                        @NotNull QuestID questId) {
        return findActiveQuest(activeQuests, questId) != null;
    }
    
    /**
     * 퀘스트가 완료되었는지 확인 (보상 수령 여부 무관)
     */
    public static boolean hasCompletedQuest(@NotNull Map<String, CompletedQuestDTO> completedQuests,
                                           @NotNull Map<String, ClaimedQuestDTO> claimedQuests,
                                           @NotNull QuestID questId) {
        return findCompletedQuest(completedQuests, questId) != null || 
               findClaimedQuest(claimedQuests, questId) != null;
    }
    
    /**
     * 퀘스트의 총 완료 횟수 계산
     */
    public static int getTotalCompletionCount(@NotNull Map<String, CompletedQuestDTO> completedQuests,
                                             @NotNull Map<String, ClaimedQuestDTO> claimedQuests,
                                             @NotNull QuestID questId) {
        int count = 0;
        
        // 현재 완료된 퀘스트
        CompletedQuestDTO completed = findCompletedQuest(completedQuests, questId);
        if (completed != null) {
            count += completed.completionCount();
        }
        
        // 과거에 보상까지 받은 퀘스트들
        for (ClaimedQuestDTO claimed : claimedQuests.values()) {
            if (claimed.questId().equals(questId.name())) {
                count += claimed.completionCount();
            }
        }
        
        return count;
    }
    
    /**
     * 활성 퀘스트의 인스턴스 ID로 제거
     */
    public static void removeActiveQuest(@NotNull Map<String, ActiveQuestDTO> activeQuests,
                                        @NotNull String instanceId) {
        activeQuests.remove(instanceId);
    }
    
    /**
     * 완료된 퀘스트의 인스턴스 ID로 제거
     */
    public static void removeCompletedQuest(@NotNull Map<String, CompletedQuestDTO> completedQuests,
                                           @NotNull String instanceId) {
        completedQuests.remove(instanceId);
    }
}