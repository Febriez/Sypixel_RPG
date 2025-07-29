package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.JsonUtil;
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
        
        fields.add("x", JsonUtil.createDoubleValue(x));
        fields.add("y", JsonUtil.createDoubleValue(y));
        fields.add("z", JsonUtil.createDoubleValue(z));
        fields.add("yaw", JsonUtil.createDoubleValue(yaw));
        fields.add("pitch", JsonUtil.createDoubleValue(pitch));
        fields.add("alias", JsonUtil.createStringValue(alias));
        
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
        
        double x = JsonUtil.getDoubleValue(fields, "x", 0.0);
        double y = JsonUtil.getDoubleValue(fields, "y", 64.0);
        double z = JsonUtil.getDoubleValue(fields, "z", 0.0);
        float yaw = (float) JsonUtil.getDoubleValue(fields, "yaw", 0.0);
        float pitch = (float) JsonUtil.getDoubleValue(fields, "pitch", 0.0);
        String alias = JsonUtil.getStringValue(fields, "alias", "섬 중앙");
        
        return new IslandSpawnPointDTO(x, y, z, yaw, pitch, alias);
    }
    
}