package com.febrie.rpg.dto.player;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 플레이어 진행도 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record ProgressDTO(
        int currentLevel,
        long totalExperience,
        double levelProgress,
        int mobsKilled,
        int playersKilled,
        int deaths
) {
    /**
     * 기본 생성자 - 신규 플레이어용
     */
    public ProgressDTO() {
        this(1, 0L, 0.0, 0, 0, 0);
    }
    
    /**
     * Map으로 변환
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("currentLevel", currentLevel);
        map.put("totalExperience", totalExperience);
        map.put("levelProgress", levelProgress);
        map.put("mobsKilled", mobsKilled);
        map.put("playersKilled", playersKilled);
        map.put("deaths", deaths);
        
        return map;
    }
    
    /**
     * Map에서 ProgressDTO 생성
     */
    @NotNull
    public static ProgressDTO fromMap(@NotNull Map<String, Object> map) {
        if (map.isEmpty()) {
            return new ProgressDTO();
        }
        
        int currentLevel = FirestoreUtils.getInt(map, "currentLevel");
        if (currentLevel == 0) currentLevel = 1; // 기본값 1로 설정
        long totalExperience = FirestoreUtils.getLong(map, "totalExperience");
        double levelProgress = FirestoreUtils.getDouble(map, "levelProgress");
        int mobsKilled = FirestoreUtils.getInt(map, "mobsKilled");
        int playersKilled = FirestoreUtils.getInt(map, "playersKilled");
        int deaths = FirestoreUtils.getInt(map, "deaths");
        
        return new ProgressDTO(currentLevel, totalExperience, levelProgress, mobsKilled, playersKilled, deaths);
    }
}