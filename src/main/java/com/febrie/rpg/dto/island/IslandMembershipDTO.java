package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.Objects;
import net.kyori.adventure.text.Component;
/**
 * 섬 멤버십 정보 DTO (Record)
 * 섬원, 알바생, 기여도 관련 정보
 *
 * @author Febrie
 */
public record IslandMembershipDTO(
        @NotNull String islandId,
        @NotNull List<IslandMemberDTO> members,
        @NotNull List<IslandWorkerDTO> workers,
        @NotNull Map<String, Long> contributions
) {
    /**
     * 기본 생성자 - 빈 멤버십
     */
    public static IslandMembershipDTO createEmpty(String islandId, String ownerUuid) {
        return new IslandMembershipDTO(
                islandId,
                List.of(),
                List.of(),
                Map.of(ownerUuid, 0L)
        );
    }
    
    /**
     * 현재 섬원 수 (섬장 제외)
     */
    public int getMemberCount() {
        return members.size();
    }
    
    /**
     * 현재 알바생 수
     */
    public int getWorkerCount() {
        return workers.size();
    }
    
    /**
     * 특정 플레이어가 섬원인지 확인
     */
    public boolean isMember(String playerUuid) {
        return members.stream().anyMatch(member -> member.uuid().equals(playerUuid));
    }
    
    /**
     * 특정 플레이어가 부섬장인지 확인
     */
    public boolean isCoOwner(String playerUuid) {
        return members.stream()
                .filter(member -> member.uuid().equals(playerUuid))
                .findFirst()
                .map(IslandMemberDTO::isCoOwner)
                .orElse(false);
    }
    
    /**
     * 특정 플레이어가 알바생인지 확인
     */
    public boolean isWorker(String playerUuid) {
        return workers.stream().anyMatch(worker -> worker.uuid().equals(playerUuid));
    }
    
    /**
     * 특정 플레이어의 기여도 조회
     */
    public long getContribution(String playerUuid) {
        return contributions.getOrDefault(playerUuid, 0L);
    }
    
    /**
     * Map으로 변환 (Firestore SDK용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("islandId", islandId);
        
        List<Map<String, Object>> membersList = members.stream()
                .map(IslandMemberDTO::toMap)
                .collect(Collectors.toList());
        map.put("members", membersList);
        List<Map<String, Object>> workersList = workers.stream()
                .map(IslandWorkerDTO::toMap)
                .collect(Collectors.toList());
        map.put("workers", workersList);
        map.put("contributions", new HashMap<>(contributions));
        return map;
    }
    
    /**
     * Map에서 생성 (Firestore SDK용)
     */
    @SuppressWarnings("unchecked")
    public static IslandMembershipDTO fromMap(@NotNull Map<String, Object> map) {
        @NotNull String islandId = Objects.requireNonNull(FirestoreUtils.getString(map, "islandId", ""));
        // 필수 필드 검증
        if (islandId.isEmpty()) {
            throw new IllegalArgumentException("IslandMembershipDTO: islandId cannot be empty");
        }
        
        List<IslandMemberDTO> members = new ArrayList<>();
        Object membersObj = map.get("members");
        if (membersObj instanceof List) {
            for (Object memberObj : (List<?>) membersObj) {
                if (memberObj instanceof Map) {
                    members.add(IslandMemberDTO.fromMap((Map<String, Object>) memberObj));
                }
            }
        }
        
        List<IslandWorkerDTO> workers = new ArrayList<>();
        Object workersObj = map.get("workers");
        if (workersObj instanceof List) {
            for (Object workerObj : (List<?>) workersObj) {
                if (workerObj instanceof Map) {
                    workers.add(IslandWorkerDTO.fromMap((Map<String, Object>) workerObj));
                }
            }
        }
        
        Map<String, Long> contributions = new HashMap<>();
        Object contributionsObj = map.get("contributions");
        if (contributionsObj instanceof Map) {
            Map<String, Object> contributionsMap = (Map<String, Object>) contributionsObj;
            contributionsMap.forEach((key, value) -> {
                if (value instanceof Number) {
                    contributions.put(key, ((Number) value).longValue());
                }
            });
        }
        return new IslandMembershipDTO(islandId, members, workers, contributions);
    }
}
