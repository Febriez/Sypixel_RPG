package com.febrie.rpg.dto.player;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 플레이어 스탯 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record StatsDTO(
        int strength,
        int intelligence,
        int dexterity,
        int vitality,
        int wisdom,
        int luck
) {
    /**
     * 기본 생성자 - 초기 스탯
     */
    public StatsDTO() {
        this(10, 10, 10, 10, 10, 1);
    }
    
    /**
     * Map으로 변환
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("strength", strength);
        map.put("intelligence", intelligence);
        map.put("dexterity", dexterity);
        map.put("vitality", vitality);
        map.put("wisdom", wisdom);
        map.put("luck", luck);
        
        return map;
    }
    
    /**
     * Map에서 StatsDTO 생성
     */
    @NotNull
    public static StatsDTO fromMap(@NotNull Map<String, Object> map) {
        if (map.isEmpty()) {
            return new StatsDTO(); // 기본값 반환
        }
        
        int strength = FirestoreUtils.getInt(map, "strength", 10);
        int intelligence = FirestoreUtils.getInt(map, "intelligence", 10);
        int dexterity = FirestoreUtils.getInt(map, "dexterity", 10);
        int vitality = FirestoreUtils.getInt(map, "vitality", 10);
        int wisdom = FirestoreUtils.getInt(map, "wisdom", 10);
        int luck = FirestoreUtils.getInt(map, "luck", 1);
        
        return new StatsDTO(strength, intelligence, dexterity, vitality, wisdom, luck);
    }
}