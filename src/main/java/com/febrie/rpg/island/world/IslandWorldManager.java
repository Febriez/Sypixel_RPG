package com.febrie.rpg.island.world;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.dto.island.IslandLocationDTO;
import com.febrie.rpg.util.LogUtil;
import org.bukkit.*;
import org.bukkit.block.Biome;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 섬 월드 관리자
 * 섬 전용 월드 생성 및 관리
 *
 * @author Febrie, CoffeeTory
 */
public class IslandWorldManager {
    
    private static final String ISLAND_WORLD_NAME = "island_world";
    private static final int ISLAND_HEIGHT = 64; // 섬 기본 높이
    private static final int ISLAND_SPACING = 1000; // 섬 간 간격
    
    private final RPGMain plugin;
    private World islandWorld;
    private final AtomicInteger nextIslandIndex = new AtomicInteger(0);
    
    public IslandWorldManager(@NotNull RPGMain plugin) {
        this.plugin = plugin;
    }
    
    /**
     * 섬 월드 초기화
     */
    public void initialize() {
        // 섬 월드 생성 또는 로드
        islandWorld = Bukkit.getWorld(ISLAND_WORLD_NAME);
        
        if (islandWorld == null) {
            LogUtil.info("섬 월드 생성 중...");
            createIslandWorld();
        } else {
            LogUtil.info("기존 섬 월드 로드 완료");
        }
        
        // 월드 설정
        configureIslandWorld();
    }
    
    /**
     * 섬 월드 생성
     */
    private void createIslandWorld() {
        WorldCreator creator = new WorldCreator(ISLAND_WORLD_NAME);
        creator.environment(World.Environment.NORMAL);
        creator.type(WorldType.NORMAL);
        creator.generateStructures(false);
        creator.generator(new EmptyWorldGenerator()); // 빈 월드 생성기
        
        islandWorld = creator.createWorld();
        LogUtil.info("섬 월드 생성 완료: " + ISLAND_WORLD_NAME);
    }
    
    /**
     * 섬 월드 설정
     */
    private void configureIslandWorld() {
        if (islandWorld == null) return;
        
        // 게임 규칙 설정
        islandWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        islandWorld.setGameRule(GameRule.DO_FIRE_TICK, false);
        islandWorld.setGameRule(GameRule.MOB_GRIEFING, false);
        islandWorld.setGameRule(GameRule.KEEP_INVENTORY, true);
        
        // 월드 설정
        islandWorld.setDifficulty(Difficulty.NORMAL);
        islandWorld.setPVP(false);
        islandWorld.setAutoSave(true);
        
        // 스폰 위치 설정 (0, 100, 0)
        islandWorld.setSpawnLocation(0, 100, 0);
        
        LogUtil.info("섬 월드 설정 완료");
    }
    
    /**
     * 새 섬 생성
     */
    public CompletableFuture<IslandLocationDTO> createNewIsland(int size) {
        return CompletableFuture.supplyAsync(() -> {
            int islandIndex = nextIslandIndex.getAndIncrement();
            IslandLocationDTO location = IslandLocationDTO.getNextIslandLocation(islandIndex);
            
            // 섬 지형 생성
            generateIslandTerrain(location.centerX(), location.centerZ(), size);
            
            // 바이옴 설정
            int biomeSize = calculateBiomeSize(size);
            setBiome(location.centerX(), location.centerZ(), biomeSize, Biome.PLAINS);
            
            LogUtil.info(String.format("새 섬 생성 완료 - 위치: (%d, %d), 크기: %d", 
                    location.centerX(), location.centerZ(), size));
            
            return location;
        });
    }
    
    /**
     * 섬 지형 생성
     */
    private void generateIslandTerrain(int centerX, int centerZ, int size) {
        int radius = size / 2;
        
        // 기본 플랫폼 생성 (돌)
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                // 원형 섬 생성
                double distance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(z - centerZ, 2));
                if (distance <= radius) {
                    // 기반암 층
                    islandWorld.getBlockAt(x, ISLAND_HEIGHT - 5, z).setType(Material.BEDROCK);
                    
                    // 돌 층
                    for (int y = ISLAND_HEIGHT - 4; y < ISLAND_HEIGHT; y++) {
                        islandWorld.getBlockAt(x, y, z).setType(Material.STONE);
                    }
                    
                    // 흙 층
                    for (int y = ISLAND_HEIGHT; y < ISLAND_HEIGHT + 3; y++) {
                        islandWorld.getBlockAt(x, y, z).setType(Material.DIRT);
                    }
                    
                    // 잔디 층
                    islandWorld.getBlockAt(x, ISLAND_HEIGHT + 3, z).setType(Material.GRASS_BLOCK);
                }
            }
        }
        
        // 중앙에 나무 생성
        generateTree(centerX, ISLAND_HEIGHT + 4, centerZ);
    }
    
    /**
     * 나무 생성
     */
    private void generateTree(int x, int y, int z) {
        // 나무 줄기
        for (int i = 0; i < 5; i++) {
            islandWorld.getBlockAt(x, y + i, z).setType(Material.OAK_LOG);
        }
        
        // 나뭇잎
        for (int dx = -2; dx <= 2; dx++) {
            for (int dz = -2; dz <= 2; dz++) {
                for (int dy = 3; dy <= 5; dy++) {
                    if (Math.abs(dx) + Math.abs(dz) <= 3) {
                        Location loc = new Location(islandWorld, x + dx, y + dy, z + dz);
                        if (loc.getBlock().getType() == Material.AIR) {
                            loc.getBlock().setType(Material.OAK_LEAVES);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 바이옴 크기 계산 (500을 넘는 16의 배수)
     */
    private int calculateBiomeSize(int islandSize) {
        int minSize = Math.max(islandSize, 500);
        return ((minSize + 15) / 16) * 16;
    }
    
    /**
     * 바이옴 설정
     */
    private void setBiome(int centerX, int centerZ, int size, Biome biome) {
        int radius = size / 2;
        
        for (int x = centerX - radius; x <= centerX + radius; x += 16) {
            for (int z = centerZ - radius; z <= centerZ + radius; z += 16) {
                Chunk chunk = islandWorld.getChunkAt(x >> 4, z >> 4);
                
                for (int cx = 0; cx < 16; cx++) {
                    for (int cz = 0; cz < 16; cz++) {
                        for (int y = islandWorld.getMinHeight(); y < islandWorld.getMaxHeight(); y++) {
                            chunk.getBlock(cx, y, cz).setBiome(biome);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 섬 초기화 (리셋)
     */
    public CompletableFuture<Void> resetIsland(int centerX, int centerZ, int oldSize, int newSize) {
        return CompletableFuture.runAsync(() -> {
            // 기존 섬 제거
            clearIsland(centerX, centerZ, oldSize);
            
            // 새 섬 생성
            generateIslandTerrain(centerX, centerZ, newSize);
            
            // 바이옴 재설정
            int biomeSize = calculateBiomeSize(newSize);
            setBiome(centerX, centerZ, biomeSize, Biome.PLAINS);
            
            LogUtil.info(String.format("섬 초기화 완료 - 위치: (%d, %d)", centerX, centerZ));
        });
    }
    
    /**
     * 섬 제거
     */
    private void clearIsland(int centerX, int centerZ, int size) {
        int radius = size / 2;
        
        for (int x = centerX - radius; x <= centerX + radius; x++) {
            for (int z = centerZ - radius; z <= centerZ + radius; z++) {
                for (int y = 0; y < islandWorld.getMaxHeight(); y++) {
                    islandWorld.getBlockAt(x, y, z).setType(Material.AIR);
                }
            }
        }
    }
    
    /**
     * 섬 삭제
     */
    public CompletableFuture<Void> deleteIsland(int centerX, int centerZ, int size) {
        return CompletableFuture.runAsync(() -> {
            clearIsland(centerX, centerZ, size);
            LogUtil.info(String.format("섬 삭제 완료 - 위치: (%d, %d)", centerX, centerZ));
        });
    }
    
    /**
     * 섬 확장
     */
    public CompletableFuture<Void> expandIsland(int centerX, int centerZ, int oldSize, int newSize) {
        return CompletableFuture.runAsync(() -> {
            // 기존 섬 외곽에 새로운 지형 추가
            int oldRadius = oldSize / 2;
            int newRadius = newSize / 2;
            
            // 확장된 영역에만 새 지형 생성
            for (int x = centerX - newRadius; x <= centerX + newRadius; x++) {
                for (int z = centerZ - newRadius; z <= centerZ + newRadius; z++) {
                    double oldDistance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(z - centerZ, 2));
                    double newDistance = Math.sqrt(Math.pow(x - centerX, 2) + Math.pow(z - centerZ, 2));
                    
                    // 기존 섬 범위 밖이고 새 섬 범위 안인 경우에만 생성
                    if (oldDistance > oldRadius && newDistance <= newRadius) {
                        // 기반암 층
                        islandWorld.getBlockAt(x, ISLAND_HEIGHT - 5, z).setType(Material.BEDROCK);
                        
                        // 돌 층
                        for (int y = ISLAND_HEIGHT - 4; y < ISLAND_HEIGHT; y++) {
                            islandWorld.getBlockAt(x, y, z).setType(Material.STONE);
                        }
                        
                        // 흙 층
                        for (int y = ISLAND_HEIGHT; y < ISLAND_HEIGHT + 3; y++) {
                            islandWorld.getBlockAt(x, y, z).setType(Material.DIRT);
                        }
                        
                        // 잔디 층
                        islandWorld.getBlockAt(x, ISLAND_HEIGHT + 3, z).setType(Material.GRASS_BLOCK);
                    }
                }
            }
            
            // 바이옴 재설정 (확장된 크기에 맞게)
            int biomeSize = calculateBiomeSize(newSize);
            setBiome(centerX, centerZ, biomeSize, Biome.PLAINS);
            
            LogUtil.info(String.format("섬 확장 완료 - 위치: (%d, %d), 크기: %d -> %d", 
                    centerX, centerZ, oldSize, newSize));
        });
    }
    
    /**
     * 섬 월드 가져오기
     */
    public World getIslandWorld() {
        return islandWorld;
    }
    
    /**
     * 섬 월드인지 확인
     */
    public boolean isIslandWorld(World world) {
        return world != null && world.getName().equals(ISLAND_WORLD_NAME);
    }
    
    /**
     * 빈 월드 생성기
     */
    private static class EmptyWorldGenerator extends ChunkGenerator {
        @Override
        public void generateNoise(@NotNull WorldInfo worldInfo, @NotNull Random random, 
                                 int chunkX, int chunkZ, @NotNull ChunkData chunkData) {
            // 빈 청크 생성 (아무것도 하지 않음)
        }
        
        @Override
        public boolean shouldGenerateSurface() {
            return false;
        }
        
        @Override
        public boolean shouldGenerateCaves() {
            return false;
        }
        
        @Override
        public boolean shouldGenerateDecorations() {
            return false;
        }
        
        @Override
        public boolean shouldGenerateMobs() {
            return false;
        }
        
        @Override
        public boolean shouldGenerateStructures() {
            return false;
        }
    }
}