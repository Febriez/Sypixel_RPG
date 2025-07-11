package com.febrie.rpg.dto;

import com.febrie.rpg.job.JobType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * Firestore players 컬렉션의 문서 DTO (순수 POJO)
 * 플레이어의 기본 정보를 저장
 *
 * @author Febrie, CoffeeTory
 */
public class PlayerDTO {

    private String uuid;
    private String playerName;
    private String jobType; // JobType enum의 name()

    private long firstJoinDate;
    private long lastSeenDate;
    private long totalPlaytime; // 밀리초 단위

    // 버전 관리용
    private int dataVersion = 1;

    // 기본 생성자 (Firestore 필수)
    public PlayerDTO() {
    }

    // 생성자
    public PlayerDTO(@NotNull String uuid, @NotNull String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
        this.firstJoinDate = System.currentTimeMillis();
        this.lastSeenDate = System.currentTimeMillis();
    }

    // 타임스탬프 업데이트 메소드
    public void updateLastSeen() {
        this.lastSeenDate = System.currentTimeMillis();
    }

    // Getters and Setters
    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    @Nullable
    public String getJobType() {
        return jobType;
    }

    public void setJobType(@Nullable String jobType) {
        this.jobType = jobType;
    }

    @Nullable
    public JobType getJob() {
        if (jobType == null) return null;
        try {
            return JobType.valueOf(jobType);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    public void setJob(@Nullable JobType job) {
        this.jobType = job != null ? job.name() : null;
    }

    public long getFirstJoinDate() {
        return firstJoinDate;
    }

    public void setFirstJoinDate(long firstJoinDate) {
        this.firstJoinDate = firstJoinDate;
    }

    public long getLastSeenDate() {
        return lastSeenDate;
    }

    public void setLastSeenDate(long lastSeenDate) {
        this.lastSeenDate = lastSeenDate;
    }

    public long getTotalPlaytime() {
        return totalPlaytime;
    }

    public void setTotalPlaytime(long totalPlaytime) {
        this.totalPlaytime = totalPlaytime;
    }

    public int getDataVersion() {
        return dataVersion;
    }

    public void setDataVersion(int dataVersion) {
        this.dataVersion = dataVersion;
    }

    /**
     * Map으로 변환 (Firestore 저장용)
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("uuid", uuid);
        map.put("playerName", playerName);
        map.put("jobType", jobType);
        map.put("firstJoinDate", firstJoinDate);
        map.put("lastSeenDate", lastSeenDate);
        map.put("totalPlaytime", totalPlaytime);
        map.put("dataVersion", dataVersion);
        return map;
    }

    /**
     * Map에서 생성
     */
    public static PlayerDTO fromMap(Map<String, Object> map) {
        PlayerDTO dto = new PlayerDTO();

        dto.setUuid(DTOUtil.toString(map.get("uuid")));
        dto.setPlayerName(DTOUtil.toString(map.get("playerName")));
        dto.setJobType(DTOUtil.toString(map.get("jobType")));

        DTOUtil.setLongFromMap(map, "firstJoinDate", dto::setFirstJoinDate);
        DTOUtil.setLongFromMap(map, "lastSeenDate", dto::setLastSeenDate);
        DTOUtil.setLongFromMap(map, "totalPlaytime", dto::setTotalPlaytime);
        DTOUtil.setIntFromMap(map, "dataVersion", dto::setDataVersion);

        return dto;
    }
}