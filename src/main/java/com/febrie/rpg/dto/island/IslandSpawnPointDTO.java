package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.FirestoreUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import net.kyori.adventure.text.Component;
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
     * Map으로 변환 (Firebase 저장용)
     */
    @NotNull
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
     * Map에서 생성
     */
    @NotNull
    public static IslandSpawnPointDTO fromMap(@NotNull Map<String, Object> map) {
        double x = FirestoreUtils.getDouble(map, "x", 0.0);
        double y = FirestoreUtils.getDouble(map, "y", 64.0);
        double z = FirestoreUtils.getDouble(map, "z", 0.0);
        float yaw = FirestoreUtils.getFloat(map, "yaw", 0.0f);
        float pitch = FirestoreUtils.getFloat(map, "pitch", 0.0f);
        
        String alias = (String) map.getOrDefault("alias", "섬 중앙");
        
        return new IslandSpawnPointDTO(x, y, z, yaw, pitch, alias);
    }
    
}