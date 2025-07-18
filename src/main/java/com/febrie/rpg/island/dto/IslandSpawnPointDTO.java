package com.febrie.rpg.island.dto;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

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
}