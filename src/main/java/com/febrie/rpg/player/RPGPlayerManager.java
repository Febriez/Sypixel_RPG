package com.febrie.rpg.player;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * RPG 플레이어 데이터 관리자
 * 플레이어의 RPG 데이터를 메모리에 캐싱하고 관리
 *
 * @author Febrie, CoffeeTory
 */
public class RPGPlayerManager implements Listener {

    private final Plugin plugin;
    private final Map<UUID, RPGPlayer> players = new HashMap<>();

    public RPGPlayerManager(@NotNull Plugin plugin) {
        this.plugin = plugin;

        // 이미 접속중인 플레이어들 로드
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            loadPlayer(player);
        }
    }

    /**
     * 플레이어 접속 시 데이터 로드
     */
    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        loadPlayer(event.getPlayer());
    }

    /**
     * 플레이어 퇴장 시 데이터 저장 및 정리
     */
    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        unloadPlayer(event.getPlayer());
    }

    /**
     * 플레이어 데이터 로드
     */
    private void loadPlayer(@NotNull Player player) {
        RPGPlayer rpgPlayer = new RPGPlayer(player);
        players.put(player.getUniqueId(), rpgPlayer);

        plugin.getLogger().info("Loaded RPG data for player: " + player.getName());
    }

    /**
     * 플레이어 데이터 언로드
     */
    private void unloadPlayer(@NotNull Player player) {
        RPGPlayer rpgPlayer = players.remove(player.getUniqueId());
        if (rpgPlayer != null) {
            rpgPlayer.saveToPDC();
            plugin.getLogger().info("Unloaded RPG data for player: " + player.getName());
        }
    }

    /**
     * RPG 플레이어 데이터 가져오기
     */
    @Nullable
    public RPGPlayer getPlayer(@NotNull Player player) {
        return players.get(player.getUniqueId());
    }

    /**
     * RPG 플레이어 데이터 가져오기 (UUID)
     */
    @Nullable
    public RPGPlayer getPlayer(@NotNull UUID uuid) {
        return players.get(uuid);
    }

    /**
     * RPG 플레이어 데이터 가져오기 (없으면 생성)
     */
    @NotNull
    public RPGPlayer getOrCreatePlayer(@NotNull Player player) {
        return players.computeIfAbsent(player.getUniqueId(), uuid -> new RPGPlayer(player));
    }

    /**
     * 모든 플레이어 데이터 저장
     */
    public void saveAll() {
        players.values().forEach(RPGPlayer::saveToPDC);
        plugin.getLogger().info("Saved all RPG player data");
    }

    /**
     * 모든 플레이어 데이터 다시 로드
     */
    public void reloadAll() {
        players.clear();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            loadPlayer(player);
        }
        plugin.getLogger().info("Reloaded all RPG player data");
    }

    /**
     * 플레이어 수 가져오기
     */
    public int getPlayerCount() {
        return players.size();
    }

    /**
     * 특정 직업의 플레이어 수 가져오기
     */
    public int getJobPlayerCount(@NotNull com.febrie.rpg.job.JobType jobType) {
        return (int) players.values().stream()
                .filter(rpgPlayer -> rpgPlayer.getJob() == jobType)
                .count();
    }

    /**
     * 레벨 순위 가져오기
     */
    public java.util.List<RPGPlayer> getTopLevelPlayers(int limit) {
        return players.values().stream()
                .sorted((a, b) -> Integer.compare(b.getLevel(), a.getLevel()))
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 전투력 순위 가져오기
     */
    public java.util.List<RPGPlayer> getTopCombatPowerPlayers(int limit) {
        return players.values().stream()
                .sorted((a, b) -> Integer.compare(b.getCombatPower(), a.getCombatPower()))
                .limit(limit)
                .collect(java.util.stream.Collectors.toList());
    }

    /**
     * 정리 작업
     */
    public void cleanup() {
        saveAll();
        players.clear();
    }
}