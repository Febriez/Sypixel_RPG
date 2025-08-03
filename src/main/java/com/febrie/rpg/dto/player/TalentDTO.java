package com.febrie.rpg.dto.player;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 플레이어 특성 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record TalentDTO(
        int availablePoints,
        Map<String, Integer> learnedTalents
) {
    /**
     * 기본 생성자 - 신규 플레이어용
     */
    public TalentDTO() {
        this(0, new HashMap<>());
    }

    /**
     * 방어적 복사를 위한 생성자
     */
    public TalentDTO(int availablePoints, Map<String, Integer> learnedTalents) {
        this.availablePoints = availablePoints;
        this.learnedTalents = new HashMap<>(learnedTalents);
    }

    /**
     * 학습한 특성 맵의 불변 뷰 반환
     */
    @Contract(value = " -> new", pure = true)
    @Override
    public @NotNull Map<String, Integer> learnedTalents() {
        return new HashMap<>(learnedTalents);
    }

    /**
     * Map으로 변환
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();

        map.put("availablePoints", availablePoints);
        map.put("learnedTalents", new HashMap<>(learnedTalents));

        return map;
    }

    /**
     * Map에서 TalentDTO 생성
     */
    @NotNull
    @SuppressWarnings("unchecked")
    public static TalentDTO fromMap(@NotNull Map<String, Object> map) {
        int availablePoints = FirestoreUtils.getInt(map, "availablePoints", 0);

        Map<String, Integer> learnedTalents = new HashMap<>();
        Object learnedTalentsObj = map.get("learnedTalents");
        if (learnedTalentsObj instanceof Map) {
            Map<String, Object> learnedTalentsMap = (Map<String, Object>) learnedTalentsObj;
            for (Map.Entry<String, Object> entry : learnedTalentsMap.entrySet()) {
                if (entry.getValue() instanceof Number) {
                    learnedTalents.put(entry.getKey(), ((Number) entry.getValue()).intValue());
                }
            }
        }

        return new TalentDTO(availablePoints, learnedTalents);
    }
}