package com.febrie.rpg.dto.island;

import com.febrie.rpg.util.FirestoreUtils;
import org.jetbrains.annotations.NotNull;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.Objects;

/**
 * 섬 소셜 정보 DTO (Record)
 * 초대, 방문 기록 관련 정보
 *
 * @author Febrie
 */
public record IslandSocialDTO(
        @NotNull String islandId,
        @NotNull List<IslandInviteDTO> pendingInvites,
        @NotNull List<IslandVisitDTO> recentVisits
) {
    /**
     * 기본 생성자 - 빈 소셜 정보
     */
    public static IslandSocialDTO createEmpty(String islandId) {
        return new IslandSocialDTO(
                islandId,
                List.of(),
                List.of()
        );
    }
    
    /**
     * 대기중인 초대 수
     */
    public int getPendingInviteCount() {
        return pendingInvites.size();
    }
    
    /**
     * 특정 플레이어에 대한 초대가 있는지 확인
     */
    public boolean hasInviteFor(String playerUuid) {
        return pendingInvites.stream()
                .anyMatch(invite -> invite.invitedUuid().equals(playerUuid));
    }
    
    /**
     * 만료된 초대 필터링
     */
    public List<IslandInviteDTO> getActiveInvites() {
        long currentTime = System.currentTimeMillis();
        return pendingInvites.stream()
                .filter(invite -> invite.expiresAt() > currentTime)
                .collect(Collectors.toList());
    }
    
    /**
     * 최근 N일 이내 방문 기록
     */
    public List<IslandVisitDTO> getRecentVisits(int days) {
        long cutoffTime = System.currentTimeMillis() - (days * 24L * 60 * 60 * 1000);
        return recentVisits.stream()
                .filter(visit -> visit.visitedAt() > cutoffTime)
                .sorted((a, b) -> Long.compare(b.visitedAt(), a.visitedAt()))
                .collect(Collectors.toList());
    }
    
    /**
     * Map으로 변환 (Firestore SDK용)
     */
    @NotNull
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("islandId", islandId);
        
        List<Map<String, Object>> invitesList = pendingInvites.stream()
                .map(IslandInviteDTO::toMap)
                .collect(Collectors.toList());
        map.put("pendingInvites", invitesList);
        List<Map<String, Object>> visitsList = recentVisits.stream()
                .map(IslandVisitDTO::toMap)
                .collect(Collectors.toList());
        map.put("recentVisits", visitsList);
        return map;
    }
    
    /**
     * Map에서 생성 (Firestore SDK용)
     */
    @SuppressWarnings("unchecked")
    public static IslandSocialDTO fromMap(@NotNull Map<String, Object> map) {
        @NotNull String islandId = Objects.requireNonNull(FirestoreUtils.getString(map, "islandId", ""));
        // 필수 필드 검증
        if (islandId.isEmpty()) {
            throw new IllegalArgumentException("IslandSocialDTO: islandId cannot be empty");
        }
        List<IslandInviteDTO> pendingInvites = new ArrayList<>();
        Object invitesObj = map.get("pendingInvites");
        if (invitesObj instanceof List) {
            for (Object inviteObj : (List<?>) invitesObj) {
                if (inviteObj instanceof Map) {
                    pendingInvites.add(IslandInviteDTO.fromMap((Map<String, Object>) inviteObj));
                }
            }
        }
        
        List<IslandVisitDTO> recentVisits = new ArrayList<>();
        Object visitsObj = map.get("recentVisits");
        if (visitsObj instanceof List) {
            for (Object visitObj : (List<?>) visitsObj) {
                if (visitObj instanceof Map) {
                    recentVisits.add(IslandVisitDTO.fromMap((Map<String, Object>) visitObj));
                }
            }
        }
        
        return new IslandSocialDTO(islandId, pendingInvites, recentVisits);
    }
}
