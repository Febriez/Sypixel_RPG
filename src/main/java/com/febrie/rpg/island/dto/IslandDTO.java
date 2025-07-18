package com.febrie.rpg.island.dto;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 섬 기본 정보 DTO (Record)
 * Firebase 저장용 불변 데이터 구조
 *
 * @author Febrie, CoffeeTory
 */
public record IslandDTO(
        @NotNull String islandId,
        @NotNull String ownerUuid,
        @NotNull String ownerName,
        @NotNull String islandName,
        int size, // 현재 섬 크기
        boolean isPublic, // 공개/비공개 설정
        long createdAt,
        long lastActivity,
        @NotNull List<IslandMemberDTO> members, // 섬원 목록 (부섬장, 일반섬원)
        @NotNull List<IslandWorkerDTO> workers, // 알바생 목록
        @NotNull Map<String, Long> contributions, // UUID -> 기여도
        @NotNull IslandSpawnDTO spawnData, // 스폰 위치 정보
        @NotNull IslandUpgradeDTO upgradeData, // 업그레이드 정보
        @NotNull IslandPermissionDTO permissions, // 권한 설정
        @NotNull List<IslandInviteDTO> pendingInvites, // 대기중인 초대
        @NotNull List<IslandVisitDTO> recentVisits, // 최근 방문 기록
        int totalResets, // 총 초기화 횟수
        @Nullable Long deletionScheduledAt // 삭제 예정 시간 (null이면 삭제 예정 없음)
) {
    /**
     * 신규 섬 생성용 기본 생성자
     */
    public static IslandDTO createNew(String islandId, String ownerUuid, String ownerName, String islandName) {
        return new IslandDTO(
                islandId,
                ownerUuid,
                ownerName,
                islandName,
                85, // 초기 크기
                false, // 기본 비공개
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                List.of(),
                List.of(),
                Map.of(ownerUuid, 0L),
                IslandSpawnDTO.createDefault(),
                IslandUpgradeDTO.createDefault(),
                IslandPermissionDTO.createDefault(),
                List.of(),
                List.of(),
                0,
                null
        );
    }
    
    /**
     * 섬 삭제 가능 여부 확인 (생성 후 1주일 경과)
     */
    public boolean canDelete() {
        long oneWeekInMillis = 7L * 24 * 60 * 60 * 1000;
        return System.currentTimeMillis() - createdAt >= oneWeekInMillis;
    }
    
    /**
     * 현재 섬원 수 (섬장 포함)
     */
    public int getMemberCount() {
        return 1 + members.size(); // 섬장 + 섬원들
    }
    
    /**
     * 특정 플레이어의 역할 확인
     */
    public IslandRole getPlayerRole(String playerUuid) {
        if (ownerUuid.equals(playerUuid)) {
            return IslandRole.OWNER;
        }
        
        for (IslandMemberDTO member : members) {
            if (member.uuid().equals(playerUuid)) {
                return member.isCoOwner() ? IslandRole.CO_OWNER : IslandRole.MEMBER;
            }
        }
        
        for (IslandWorkerDTO worker : workers) {
            if (worker.uuid().equals(playerUuid)) {
                return IslandRole.WORKER;
            }
        }
        
        return IslandRole.VISITOR;
    }
    
    /**
     * 섬에 속한 플레이어인지 확인 (알바 제외)
     */
    public boolean isMember(String playerUuid) {
        if (ownerUuid.equals(playerUuid)) {
            return true;
        }
        return members.stream().anyMatch(member -> member.uuid().equals(playerUuid));
    }
    
    /**
     * 섬 크기를 16의 배수로 반올림 (바이옴 설정용)
     */
    public int getBiomeSize() {
        // 500을 넘는 가장 가까운 16의 배수 찾기
        int minSize = Math.max(size, 500);
        return ((minSize + 15) / 16) * 16;
    }
}