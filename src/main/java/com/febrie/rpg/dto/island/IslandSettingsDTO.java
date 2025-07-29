package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.JsonUtil;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.NotNull;

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
     * JsonObject로 변환 (Firebase 저장용)
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject fields = new JsonObject();
        
        fields.add("nameColorHex", JsonUtil.createStringValue(nameColorHex));
        fields.add("biome", JsonUtil.createStringValue(biome));
        fields.add("template", JsonUtil.createStringValue(template));
        
        return fields;
    }
    
    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static IslandSettingsDTO fromJsonObject(@NotNull JsonObject fields) {
        String colorHex = JsonUtil.getStringValue(fields, "nameColorHex", "#FFFF00");
        String biome = JsonUtil.getStringValue(fields, "biome", "PLAINS");
        String template = JsonUtil.getStringValue(fields, "template", "BASIC");
                
        return new IslandSettingsDTO(colorHex, biome, template);
    }
}