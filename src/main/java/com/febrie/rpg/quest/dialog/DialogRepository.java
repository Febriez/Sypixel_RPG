package com.febrie.rpg.quest.dialog;

import com.febrie.rpg.quest.QuestID;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * 퀘스트 대화 데이터 저장소
 * 모든 대화 데이터를 중앙에서 관리
 *
 * @author Febrie
 */
public class DialogRepository {

    private static DialogRepository instance;
    
    // 대화 ID별 대화 데이터
    private final Map<String, QuestDialog> dialogMap = new HashMap<>();
    
    // 퀘스트별 대화 매핑
    private final Map<QuestID, Map<String, QuestDialog>> questDialogMap = new EnumMap<>(QuestID.class);
    
    /**
     * 프라이빗 생성자
     */
    private DialogRepository() {
        initializeDialogs();
    }
    
    /**
     * 싱글톤 인스턴스 반환
     */
    public static DialogRepository getInstance() {
        if (instance == null) {
            instance = new DialogRepository();
        }
        return instance;
    }
    
    /**
     * 대화 ID로 대화 가져오기
     */
    @Nullable
    public QuestDialog getDialog(@NotNull String dialogId) {
        return dialogMap.get(dialogId);
    }
    
    /**
     * 퀘스트별 대화 가져오기
     */
    @Nullable
    public QuestDialog getQuestDialog(@NotNull QuestID questId, @NotNull String dialogType) {
        Map<String, QuestDialog> questDialogs = questDialogMap.get(questId);
        if (questDialogs == null) {
            return null;
        }
        return questDialogs.get(dialogType);
    }
    
    /**
     * 대화 등록
     */
    private void registerDialog(@NotNull String dialogId, @NotNull QuestDialog dialog) {
        dialogMap.put(dialogId, dialog);
    }
    
    /**
     * 퀘스트 대화 등록
     */
    private void registerQuestDialog(@NotNull QuestID questId, @NotNull String dialogType, @NotNull QuestDialog dialog) {
        questDialogMap.computeIfAbsent(questId, k -> new HashMap<>()).put(dialogType, dialog);
        dialogMap.put(dialog.getId(), dialog);
    }
    
    /**
     * 모든 대화 초기화
     * 나중에 JSON이나 YAML 파일에서 로드하도록 변경 가능
     */
    private void initializeDialogs() {
        // 예시: 튜토리얼 퀘스트 대화
        QuestDialog tutorialStartDialog = new QuestDialog("tutorial_first_steps_start")
                .addLine("가이드", "안녕하세요! Sypixel RPG에 오신 것을 환영합니다!", "Hello! Welcome to Sypixel RPG!")
                .addLine("가이드", "이 세계에서 당신의 모험을 시작해보세요.", "Start your adventure in this world.")
                .addLineWithChoices("가이드", "준비되셨나요?", "Are you ready?", 
                    java.util.List.of(
                        new QuestDialog.DialogChoice("yes", "네, 준비됐습니다!", "Yes, I'm ready!", 3),
                        new QuestDialog.DialogChoice("no", "아직은...", "Not yet...", -1)
                    ))
                .addLine("가이드", "좋습니다! 첫 번째 목표를 알려드리겠습니다.", "Great! Let me tell you your first objective.");
                
        registerQuestDialog(QuestID.TUTORIAL_FIRST_STEPS, "start", tutorialStartDialog);
        
        // 더 많은 대화는 여기에 추가
    }
    
    /**
     * 대화 데이터 리로드
     * 파일 기반 시스템으로 전환 시 사용
     */
    public void reload() {
        dialogMap.clear();
        questDialogMap.clear();
        initializeDialogs();
    }
}