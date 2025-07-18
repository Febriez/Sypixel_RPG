package com.febrie.rpg.dto.island;

import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 섬 스폰 포인트 정보 DTO (Record)
 *
 * @author Febrie, CoffeeTory
 */
public record IslandSpawnPointDTO(
        double x,
        double y,
        double z,
        float yaw,
        float pitch,
        @NotNull String alias // 스폰 위치 별칭
) {
    /**
     * 기본 스폰 포인트 생성
     */
    public static IslandSpawnPointDTO createDefault() {
        return new IslandSpawnPointDTO(0, 64, 0, 0, 0, "섬 중앙");
    }
    
    /**
     * Location에서 생성
     */
    public static IslandSpawnPointDTO fromLocation(Location location, String alias) {
        return new IslandSpawnPointDTO(
                location.getX(),
                location.getY(),
                location.getZ(),
                location.getYaw(),
                location.getPitch(),
                alias
        );
    }
    
    /**
     * Bukkit Location으로 변환
     */
    public Location toLocation(World world) {
        return new Location(world, x, y, z, yaw, pitch);
    }
    
    /**
     * 상대 좌표를 절대 좌표로 변환
     */
    public Location toAbsoluteLocation(World world, int islandCenterX, int islandCenterZ) {
        return new Location(
                world,
                islandCenterX + x,
                y,
                islandCenterZ + z,
                yaw,
                pitch
        );
    }
    
    /**
     * JsonObject로 변환 (Firebase 저장용)
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        JsonObject xValue = new JsonObject();
        xValue.addProperty("doubleValue", x);
        fields.add("x", xValue);
        
        JsonObject yValue = new JsonObject();
        yValue.addProperty("doubleValue", y);
        fields.add("y", yValue);
        
        JsonObject zValue = new JsonObject();
        zValue.addProperty("doubleValue", z);
        fields.add("z", zValue);
        
        JsonObject yawValue = new JsonObject();
        yawValue.addProperty("doubleValue", yaw);
        fields.add("yaw", yawValue);
        
        JsonObject pitchValue = new JsonObject();
        pitchValue.addProperty("doubleValue", pitch);
        fields.add("pitch", pitchValue);
        
        JsonObject aliasValue = new JsonObject();
        aliasValue.addProperty("stringValue", alias);
        fields.add("alias", aliasValue);
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static IslandSpawnPointDTO fromJsonObject(@NotNull JsonObject json) {
        if (!json.has("fields")) {
            return createDefault();
        }
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        double x = fields.has("x") && fields.getAsJsonObject("x").has("doubleValue")
                ? fields.getAsJsonObject("x").get("doubleValue").getAsDouble()
                : 0.0;
                
        double y = fields.has("y") && fields.getAsJsonObject("y").has("doubleValue")
                ? fields.getAsJsonObject("y").get("doubleValue").getAsDouble()
                : 64.0;
                
        double z = fields.has("z") && fields.getAsJsonObject("z").has("doubleValue")
                ? fields.getAsJsonObject("z").get("doubleValue").getAsDouble()
                : 0.0;
                
        float yaw = fields.has("yaw") && fields.getAsJsonObject("yaw").has("doubleValue")
                ? fields.getAsJsonObject("yaw").get("doubleValue").getAsFloat()
                : 0.0f;
                
        float pitch = fields.has("pitch") && fields.getAsJsonObject("pitch").has("doubleValue")
                ? fields.getAsJsonObject("pitch").get("doubleValue").getAsFloat()
                : 0.0f;
                
        String alias = fields.has("alias") && fields.getAsJsonObject("alias").has("stringValue")
                ? fields.getAsJsonObject("alias").get("stringValue").getAsString()
                : "섬 중앙";
        
        return new IslandSpawnPointDTO(x, y, z, yaw, pitch, alias);
    }
    
    /**
     * Map으로 변환 (하위 호환성)
     */
    @Deprecated
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("x", x);
        map.put("y", y);
        map.put("z", z);
        map.put("yaw", yaw);
        map.put("pitch", pitch);
        map.put("alias", alias);
        return map;
    }
    
    /**
     * Map에서 생성 (하위 호환성)
     */
    @Deprecated
    public static IslandSpawnPointDTO fromMap(Map<String, Object> map) {
        if (map == null) return null;
        
        return new IslandSpawnPointDTO(
                ((Number) map.get("x")).doubleValue(),
                ((Number) map.get("y")).doubleValue(),
                ((Number) map.get("z")).doubleValue(),
                ((Number) map.get("yaw")).floatValue(),
                ((Number) map.get("pitch")).floatValue(),
                (String) map.get("alias")
        );
    }
}