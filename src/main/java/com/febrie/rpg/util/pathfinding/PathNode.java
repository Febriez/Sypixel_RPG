package com.febrie.rpg.util.pathfinding;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

/**
 * A* 알고리즘을 위한 패스 노드
 * 
 * @author Febrie
 */
public class PathNode {
    
    private final Location location;
    private final int x, y, z;
    
    // A* 알고리즘 관련 값들
    private double gCost = 0; // 시작점으로부터의 실제 비용
    private double hCost = 0; // 목표점까지의 추정 비용 (휴리스틱)
    private double fCost = 0; // gCost + hCost
    
    private PathNode parent;
    private boolean walkable = true;
    
    public PathNode(@NotNull Location location) {
        this.location = location.clone();
        this.x = location.getBlockX();
        this.y = location.getBlockY();
        this.z = location.getBlockZ();
    }
    
    public PathNode(int x, int y, int z, @NotNull org.bukkit.World world) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.location = new Location(world, x, y, z);
    }
    
    /**
     * F 비용 계산 (G + H)
     */
    public void calculateFCost() {
        fCost = gCost + hCost;
    }
    
    /**
     * 두 노드 간의 거리 계산 (맨하탄 거리)
     */
    public double getDistanceTo(@NotNull PathNode other) {
        int deltaX = Math.abs(x - other.x);
        int deltaY = Math.abs(y - other.y);
        int deltaZ = Math.abs(z - other.z);
        
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY + deltaZ * deltaZ);
    }
    
    /**
     * 휴리스틱 거리 계산 (목표까지의 추정 거리)
     */
    public double getHeuristicDistanceTo(@NotNull PathNode target) {
        return getDistanceTo(target);
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof PathNode other)) return false;
        return x == other.x && y == other.y && z == other.z;
    }
    
    @Override
    public int hashCode() {
        return x * 31 * 31 + y * 31 + z;
    }
    
    @Override
    public String toString() {
        return String.format("PathNode{x=%d, y=%d, z=%d, fCost=%.2f}", x, y, z, fCost);
    }
    
    // Getters and Setters
    @NotNull
    public Location getLocation() {
        return location.clone();
    }
    
    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public int getZ() {
        return z;
    }
    
    public double getGCost() {
        return gCost;
    }
    
    public void setGCost(double gCost) {
        this.gCost = gCost;
    }
    
    public double getHCost() {
        return hCost;
    }
    
    public void setHCost(double hCost) {
        this.hCost = hCost;
    }
    
    public double getFCost() {
        return fCost;
    }
    
    public PathNode getParent() {
        return parent;
    }
    
    public void setParent(PathNode parent) {
        this.parent = parent;
    }
    
    public boolean isWalkable() {
        return walkable;
    }
    
    public void setWalkable(boolean walkable) {
        this.walkable = walkable;
    }
}