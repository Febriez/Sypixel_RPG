package com.febrie.rpg.dto.island;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 섬 설정 정보 DTO
 * 색상, 바이옴, 템플릿 등의 시각적 설정 저장
 *
 * @author CoffeeTory
 */
public record IslandSettingsDTO(
        @NotNull String nameColorHex,  // 섬 이름 색상 (Hex)
        @NotNull String biome,         // 바이옴 타입
        @NotNull String template       // 섬 템플릿
) {
    
    /**
     * 기본 설정 생성
     */
    public static IslandSettingsDTO createDefault() {
        return new IslandSettingsDTO(
                "#FFFF00",  // 기본 노란색
                "PLAINS",   // 기본 평원
                "BASIC"     // 기본 템플릿
        );
    }
    
    /**
     * Map으로 변환 (Firebase 저장용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        
        map.put("nameColorHex", nameColorHex);
        map.put("biome", biome);
        map.put("template", template);
        
        return map;
    }
    
    /**
     * Map에서 생성
     */
    @NotNull
    public static IslandSettingsDTO fromMap(@NotNull Map<String, Object> map) {
        String colorHex = map.containsKey("nameColorHex") ? (String) map.get("nameColorHex") : "#FFFF00";
        String biome = map.containsKey("biome") ? (String) map.get("biome") : "PLAINS";
        String template = map.containsKey("template") ? (String) map.get("template") : "BASIC";
                
        return new IslandSettingsDTO(colorHex, biome, template);
    }
}