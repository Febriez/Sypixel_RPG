package com.febrie.rpg.util.pathfinding;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * A* 알고리즘을 사용한 길찾기 유틸리티
 * 
 * @author Febrie
 */
public class PathfindingUtil {
    
    private static final int MAX_SEARCH_DISTANCE = 100; // 최대 탐색 거리
    private static final int MAX_ITERATIONS = 10000; // 최대 반복 횟수
    
    // 이동 가능한 방향들 (26방향: 3D 공간에서 인접한 모든 블록)
    private static final int[][] DIRECTIONS = {
        // 같은 Y 레벨
        {-1, 0, -1}, {0, 0, -1}, {1, 0, -1},
        {-1, 0, 0},               {1, 0, 0},
        {-1, 0, 1},  {0, 0, 1},  {1, 0, 1},
        
        // 위쪽 Y 레벨 (+1)
        {-1, 1, -1}, {0, 1, -1}, {1, 1, -1},
        {-1, 1, 0},  {0, 1, 0},  {1, 1, 0},
        {-1, 1, 1},  {0, 1, 1},  {1, 1, 1},
        
        // 아래쪽 Y 레벨 (-1)
        {-1, -1, -1}, {0, -1, -1}, {1, -1, -1},
        {-1, -1, 0},  {0, -1, 0},  {1, -1, 0},
        {-1, -1, 1},  {0, -1, 1},  {1, -1, 1}
    };
    
    /**
     * A* 알고리즘을 사용하여 경로 찾기
     * 
     * @param start 시작 위치
     * @param target 목표 위치
     * @return 경로 (시작점부터 목표점까지의 Location 리스트), 경로를 찾을 수 없으면 null
     */
    @Nullable
    public static List<Location> findPath(@NotNull Location start, @NotNull Location target) {
        if (!start.getWorld().equals(target.getWorld())) {
            return null; // 다른 월드 간의 경로는 찾을 수 없음
        }
        
        // 거리가 너무 멀면 경로 탐색 중단
        if (start.distance(target) > MAX_SEARCH_DISTANCE) {
            return null;
        }
        
        World world = start.getWorld();
        PathNode startNode = new PathNode(start);
        PathNode targetNode = new PathNode(target);
        
        // 목표가 이동 불가능한 위치라면 null 반환
        if (!isWalkable(targetNode, world)) {
            return null;
        }
        
        List<PathNode> openSet = new ArrayList<>();
        Set<PathNode> closedSet = new HashSet<>();
        
        openSet.add(startNode);
        
        int iterations = 0;
        
        while (!openSet.isEmpty() && iterations < MAX_ITERATIONS) {
            iterations++;
            
            // F 비용이 가장 낮은 노드 선택
            PathNode currentNode = openSet.get(0);
            for (PathNode node : openSet) {
                if (node.getFCost() < currentNode.getFCost() || 
                    (node.getFCost() == currentNode.getFCost() && node.getHCost() < currentNode.getHCost())) {
                    currentNode = node;
                }
            }
            
            openSet.remove(currentNode);
            closedSet.add(currentNode);
            
            // 목표에 도달했는지 확인
            if (currentNode.equals(targetNode)) {
                return retracePath(startNode, currentNode);
            }
            
            // 인접 노드들 탐색
            for (PathNode neighbor : getNeighbors(currentNode, world)) {
                if (!neighbor.isWalkable() || closedSet.contains(neighbor)) {
                    continue;
                }
                
                double newGCost = currentNode.getGCost() + currentNode.getDistanceTo(neighbor);
                
                if (!openSet.contains(neighbor)) {
                    neighbor.setGCost(newGCost);
                    neighbor.setHCost(neighbor.getHeuristicDistanceTo(targetNode));
                    neighbor.calculateFCost();
                    neighbor.setParent(currentNode);
                    openSet.add(neighbor);
                } else if (newGCost < neighbor.getGCost()) {
                    neighbor.setGCost(newGCost);
                    neighbor.calculateFCost();
                    neighbor.setParent(currentNode);
                }
            }
        }
        
        return null; // 경로를 찾을 수 없음
    }
    
    /**
     * 경로 역추적
     */
    @NotNull
    private static List<Location> retracePath(@NotNull PathNode startNode, @NotNull PathNode endNode) {
        List<PathNode> path = new ArrayList<>();
        PathNode currentNode = endNode;
        
        while (currentNode != null) {
            path.add(currentNode);
            currentNode = currentNode.getParent();
        }
        
        Collections.reverse(path);
        
        // PathNode를 Location으로 변환
        List<Location> locationPath = new ArrayList<>();
        for (PathNode node : path) {
            locationPath.add(node.getLocation().add(0.5, 0.5, 0.5)); // 블록 중앙으로 조정
        }
        
        return locationPath;
    }
    
    /**
     * 인접 노드들 가져오기
     */
    @NotNull
    private static List<PathNode> getNeighbors(@NotNull PathNode node, @NotNull World world) {
        List<PathNode> neighbors = new ArrayList<>();
        
        for (int[] direction : DIRECTIONS) {
            int newX = node.getX() + direction[0];
            int newY = node.getY() + direction[1];
            int newZ = node.getZ() + direction[2];
            
            // Y 좌표 범위 체크
            if (newY < world.getMinHeight() || newY > world.getMaxHeight()) {
                continue;
            }
            
            PathNode neighbor = new PathNode(newX, newY, newZ, world);
            neighbor.setWalkable(isWalkable(neighbor, world));
            neighbors.add(neighbor);
        }
        
        return neighbors;
    }
    
    /**
     * 특정 위치가 이동 가능한지 확인
     */
    private static boolean isWalkable(@NotNull PathNode node, @NotNull World world) {
        Location loc = node.getLocation();
        
        // 현재 위치와 머리 위치가 공기인지 확인
        Block currentBlock = world.getBlockAt(loc);
        Block headBlock = world.getBlockAt(loc.clone().add(0, 1, 0));
        
        // 발밑 블록이 있는지 확인 (공중에 떠있으면 안됨)
        Block footBlock = world.getBlockAt(loc.clone().add(0, -1, 0));
        
        return isPassable(currentBlock) && 
               isPassable(headBlock) && 
               isSolid(footBlock);
    }
    
    /**
     * 블록이 통과 가능한지 확인
     */
    private static boolean isPassable(@NotNull Block block) {
        Material material = block.getType();
        
        // 공기, 물, 용암, 잔디, 꽃 등은 통과 가능
        return material.isAir() || 
               material == Material.WATER || 
               material == Material.LAVA ||
               material == Material.GRASS_BLOCK ||
               material == Material.TALL_GRASS ||
               material == Material.FERN ||
               material == Material.LARGE_FERN ||
               material.name().contains("SAPLING") ||
               material.name().contains("FLOWER") ||
               material == Material.SNOW ||
               material == Material.VINE ||
               material == Material.LADDER ||
               material.name().contains("TORCH") ||
               material.name().contains("SIGN") ||
               material.name().contains("PRESSURE_PLATE") ||
               material.name().contains("BUTTON");
    }
    
    /**
     * 블록이 고체인지 확인 (발밑에 놓을 수 있는지)
     */
    private static boolean isSolid(@NotNull Block block) {
        Material material = block.getType();
        
        // 공기, 물, 용암은 고체가 아님
        if (material.isAir() || material == Material.WATER || material == Material.LAVA) {
            return false;
        }
        
        // 대부분의 블록은 고체로 간주
        return material.isSolid() || 
               material.name().contains("SLAB") ||
               material.name().contains("STAIRS") ||
               material == Material.SNOW_BLOCK ||
               material == Material.ICE ||
               material == Material.PACKED_ICE;
    }
    
    /**
     * 두 위치 사이의 직선 거리 계산
     */
    public static double getDistance(@NotNull Location from, @NotNull Location to) {
        if (!from.getWorld().equals(to.getWorld())) {
            return Double.MAX_VALUE;
        }
        return from.distance(to);
    }
    
    /**
     * 경로가 유효한지 확인 (장애물이 생겼는지 등)
     */
    public static boolean isPathValid(@NotNull List<Location> path) {
        if (path.isEmpty()) {
            return false;
        }
        
        World world = path.get(0).getWorld();
        
        for (Location location : path) {
            PathNode node = new PathNode(location);
            if (!isWalkable(node, world)) {
                return false;
            }
        }
        
        return true;
    }
}