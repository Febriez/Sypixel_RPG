package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.JsonUtil;
import com.google.gson.JsonObject;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * 섬 위치 정보 DTO (Record)
 * 섬의 월드 내 위치와 경계 정보
 *
 * @author Febrie, CoffeeTory
 */
public record IslandLocationDTO(
        int centerX,
        int centerZ,
        int size
) {
    /**
     * 섬의 최소 X 좌표
     */
    public int getMinX() {
        return centerX - (size / 2);
    }
    
    /**
     * 섬의 최대 X 좌표
     */
    public int getMaxX() {
        return centerX + (size / 2);
    }
    
    /**
     * 섬의 최소 Z 좌표
     */
    public int getMinZ() {
        return centerZ - (size / 2);
    }
    
    /**
     * 섬의 최대 Z 좌표
     */
    public int getMaxZ() {
        return centerZ + (size / 2);
    }
    
    /**
     * 특정 위치가 섬 범위 내에 있는지 확인
     */
    public boolean contains(Location location) {
        return location.getBlockX() >= getMinX() && location.getBlockX() <= getMaxX() &&
               location.getBlockZ() >= getMinZ() && location.getBlockZ() <= getMaxZ();
    }
    
    /**
     * 섬 중앙 위치 가져오기
     */
    public Location getCenter(World world) {
        return new Location(world, centerX, 64, centerZ);
    }
    
    /**
     * 다음 섬 위치 계산 (1000블록 간격)
     */
    public static IslandLocationDTO getNextIslandLocation(int islandIndex) {
        // 나선형으로 섬 배치
        int gridSize = 1000; // 섬 간 간격
        
        if (islandIndex == 0) {
            return new IslandLocationDTO(0, 0, 85);
        }
        
        // 나선형 패턴으로 좌표 계산
        int layer = (int) Math.ceil((Math.sqrt(islandIndex) - 1) / 2);
        int maxInLayer = (2 * layer + 1) * (2 * layer + 1);
        int minInLayer = (2 * layer - 1) * (2 * layer - 1) + 1;
        int posInLayer = islandIndex - minInLayer;
        int sideLength = 2 * layer;
        
        int x, z;
        
        if (posInLayer < sideLength) {
            // 상단 (왼쪽에서 오른쪽)
            x = -layer + posInLayer;
            z = -layer;
        } else if (posInLayer < 2 * sideLength) {
            // 오른쪽 (위에서 아래)
            x = layer;
            z = -layer + (posInLayer - sideLength);
        } else if (posInLayer < 3 * sideLength) {
            // 하단 (오른쪽에서 왼쪽)
            x = layer - (posInLayer - 2 * sideLength);
            z = layer;
        } else {
            // 왼쪽 (아래에서 위)
            x = -layer;
            z = layer - (posInLayer - 3 * sideLength);
        }
        
        return new IslandLocationDTO(x * gridSize, z * gridSize, 85);
    }
    
    /**
     * JsonObject로 변환 (Firebase 저장용)
     */
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject json = new JsonObject();
        JsonObject fields = new JsonObject();
        
        fields.add("centerX", JsonUtil.createIntegerValue(centerX));
        fields.add("centerZ", JsonUtil.createIntegerValue(centerZ));
        fields.add("size", JsonUtil.createIntegerValue(size));
        
        json.add("fields", fields);
        return json;
    }
    
    /**
     * JsonObject에서 생성
     */
    @NotNull
    public static IslandLocationDTO fromJsonObject(@NotNull JsonObject json) {
        JsonUtil.validateDTOJson(json, "IslandLocationDTO");
        
        JsonObject fields = json.getAsJsonObject("fields");
        
        try {
            // 필수 필드 검증
            JsonUtil.validateRequiredField(fields, "centerX", "IslandLocationDTO");
            JsonUtil.validateRequiredField(fields, "centerZ", "IslandLocationDTO");
            
            int centerX = (int) JsonUtil.getLongValue(fields, "centerX");
            int centerZ = (int) JsonUtil.getLongValue(fields, "centerZ");
            int size = (int) JsonUtil.getLongValue(fields, "size", 85);
            
            // 유효성 검증
            if (size <= 0) {
                throw new IllegalArgumentException(
                    String.format("Invalid IslandLocationDTO: size must be positive, but found: %d", size)
                );
            }
            
            return new IslandLocationDTO(centerX, centerZ, size);
        } catch (Exception e) {
            if (e instanceof IllegalArgumentException) {
                throw e;
            }
            throw new IllegalArgumentException(
                String.format("Failed to parse IslandLocationDTO: %s. JSON structure: %s", 
                    e.getMessage(), 
                    json.toString().length() > 200 ? json.toString().substring(0, 200) + "..." : json.toString())
            );
        }
    }
    
}