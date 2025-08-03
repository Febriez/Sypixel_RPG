# Sypixel RPG DTO Architecture Analysis

## 개요

Sypixel RPG 프로젝트의 모든 DTO(Data Transfer Object)는 Java 14에서 도입된 record 타입으로 구현되어 있습니다. 이는 불변성(immutability)과 간결한 코드를 보장하며, Firebase/Firestore와의 연동을 위한 일관된 직렬화/역직렬화 메커니즘을 제공합니다.

## 아키텍처 특징

### 1. 모든 DTO가 Record 타입
- **불변성 보장**: 모든 필드가 final이며 setter 메소드 없음
- **간결한 코드**: 자동 생성되는 생성자, getter, equals, hashCode, toString
- **방어적 복사**: 컬렉션 필드에 대한 방어적 복사로 외부 변경 방지

### 2. Firebase/Firestore 통합
- 모든 DTO가 `toJsonObject()` 및 `fromJsonObject()` 메소드 구현
- JsonUtil 유틸리티 클래스를 통한 일관된 JSON 변환
- Firestore의 Document 구조와 완벽하게 매핑

### 3. 계층적 DTO 구조
- 복잡한 도메인 모델을 중첩된 DTO로 표현
- 명확한 책임 분리와 재사용성

## DTO 카테고리별 상세 분석

### 1. Player 패키지 (com.febrie.rpg.dto.player) - 7개 DTO

플레이어 관련 정보를 관리하는 DTO들:

#### PlayerDTO
```java
public record PlayerDTO(
    @NotNull String uuid,
    @NotNull String name,
    long lastLogin,
    long totalPlaytime,
    @NotNull JobType job,
    boolean isAdmin
)
```
- **역할**: 플레이어 기본 정보 저장
- **특징**: 관리자 여부, 직업 정보 포함

#### PlayerDataDTO
```java
public record PlayerDataDTO(
    @NotNull PlayerProfileDTO profile,
    @NotNull WalletDTO wallet
)
```
- **역할**: 플레이어의 전체 데이터 컨테이너
- **특징**: 프로필과 지갑 정보를 포함하는 집합체

#### PlayerProfileDTO
```java
public record PlayerProfileDTO(
    @NotNull String uuid,
    @NotNull String name,
    int level,
    long exp,
    long totalExp,
    long lastPlayed
)
```
- **역할**: 플레이어 프로필 정보
- **특징**: 레벨, 경험치 등 진행도 추적

#### StatsDTO
```java
public record StatsDTO(
    int strength,
    int intelligence,
    int dexterity,
    int vitality,
    int wisdom,
    int luck
)
```
- **역할**: 플레이어 능력치 정보
- **특징**: RPG 기본 스탯 6종

#### WalletDTO
```java
public record WalletDTO(
    @NotNull Map<String, Long> currencies,
    long lastUpdated
)
```
- **역할**: 플레이어 재화 관리
- **특징**: 다중 화폐 지원, 방어적 복사 적용

#### ProgressDTO
```java
public record ProgressDTO(
    int currentLevel,
    long totalExperience,
    double levelProgress,
    int mobsKilled,
    int playersKilled,
    int deaths
)
```
- **역할**: 플레이어 진행도 통계
- **특징**: 전투 및 레벨링 추적

#### TalentDTO
```java
public record TalentDTO(
    int availablePoints,
    @NotNull Map<String, Integer> learnedTalents
)
```
- **역할**: 플레이어 특성/스킬 시스템
- **특징**: 특성 포인트 및 학습된 특성 관리

### 2. Island 패키지 (com.febrie.rpg.dto.island) - 11개 DTO + 1개 Enum

섬 시스템 관련 DTO와 Enum:

#### IslandDTO (핵심 DTO)
```java
public record IslandDTO(
    @NotNull String islandId,
    @NotNull String ownerUuid,
    @NotNull String ownerName,
    @NotNull String islandName,
    int size,
    @NotNull List<IslandMemberDTO> members,
    @NotNull List<IslandWorkerDTO> workers,
    @NotNull Map<String, Long> contributions,
    @NotNull IslandSpawnDTO spawnData,
    @NotNull IslandUpgradeDTO upgradeData,
    @NotNull IslandPermissionDTO permissions,
    @NotNull IslandSettingsDTO settings,
    @NotNull IslandLocationDTO location,
    @NotNull List<IslandInviteDTO> pendingInvites,
    @NotNull List<IslandVisitDTO> recentVisits,
    long createdAt,
    long lastActivity
)
```
- **역할**: 섬의 모든 정보를 포함하는 메인 DTO
- **특징**: 여러 하위 DTO를 포함하는 복합 구조

#### IslandRole (Enum)
```java
public enum IslandRole {
    OWNER(4),
    CO_OWNER(3),
    MEMBER(2),
    WORKER(1),
    VISITOR(0);
    
    private final int priority;
    
    public String getLangKey() {
        return switch (this) {
            case OWNER -> "island.roles.owner";
            case CO_OWNER -> "island.roles.sub-owner";
            case MEMBER -> "island.roles.member";
            case WORKER -> "island.roles.worker";
            case VISITOR -> "island.roles.visitor";
        };
    }
}
```
- **역할**: 섬 내 역할 정의
- **특징**: 우선순위 시스템, 언어 키 제공

#### IslandMemberDTO
```java
public record IslandMemberDTO(
    @NotNull String uuid,
    @NotNull String name,
    boolean isCoOwner,
    long joinedAt,
    long lastActivity,
    @Nullable IslandSpawnPointDTO personalSpawn
)
```
- **역할**: 섬 구성원 정보
- **관계**: IslandDTO의 members 리스트 구성요소

#### IslandWorkerDTO
```java
public record IslandWorkerDTO(
    @NotNull String uuid,
    @NotNull String name,
    long hiredAt,
    long lastActivity
)
```
- **역할**: 알바생 정보
- **관계**: IslandDTO의 workers 리스트 구성요소

#### IslandSettingsDTO
```java
public record IslandSettingsDTO(
    @Nullable String nameColorHex,
    @NotNull String biome,
    @NotNull String template
)
```
- **역할**: 섬 설정 정보
- **관계**: IslandDTO의 settings 필드

#### IslandPermissionDTO
```java
public record IslandPermissionDTO(
    @NotNull Map<String, RolePermissions> rolePermissions,
    long lastUpdated
) {
    public record RolePermissions(
        boolean canBuild,
        boolean canInteract,
        boolean canInvite,
        boolean canKick,
        boolean canManageWorkers,
        boolean canSetSpawn,
        boolean canManagePermissions
    )
}
```
- **역할**: 역할별 권한 설정
- **관계**: IslandDTO의 permissions 필드, 중첩 record 구조

#### IslandUpgradeDTO
```java
public record IslandUpgradeDTO(
    int sizeLevel,
    int memberLimitLevel,
    int workerLimitLevel,
    int memberLimit,
    int workerLimit,
    long lastUpgradeAt
)
```
- **역할**: 섬 업그레이드 정보
- **관계**: IslandDTO의 upgradeData 필드

#### IslandSpawnDTO
```java
public record IslandSpawnDTO(
    @NotNull IslandSpawnPointDTO defaultSpawn,
    @NotNull List<IslandSpawnPointDTO> ownerSpawns,
    @NotNull Map<String, IslandSpawnPointDTO> memberSpawns
)
```
- **역할**: 스폰 위치 관리
- **관계**: IslandDTO의 spawnData 필드

#### IslandSpawnPointDTO
```java
public record IslandSpawnPointDTO(
    double x,
    double y,
    double z,
    float yaw,
    float pitch,
    @Nullable String alias
)
```
- **역할**: 개별 스폰 포인트
- **특징**: Bukkit Location과 상호 변환 가능

#### IslandLocationDTO
```java
public record IslandLocationDTO(
    int centerX,
    int centerZ,
    int size
)
```
- **역할**: 섬의 월드 내 위치
- **관계**: IslandDTO의 location 필드

#### IslandInviteDTO
```java
public record IslandInviteDTO(
    @NotNull String inviteId,
    @NotNull String islandId,
    @NotNull String inviterUuid,
    @NotNull String inviterName,
    @NotNull String invitedUuid,
    @NotNull String invitedName,
    long invitedAt,
    long expiresAt,
    @Nullable String message
)
```
- **역할**: 섬 초대 정보
- **관계**: IslandDTO의 pendingInvites 리스트 구성요소

#### IslandVisitDTO
```java
public record IslandVisitDTO(
    @NotNull String visitorUuid,
    @NotNull String visitorName,
    long visitedAt,
    long duration
)
```
- **역할**: 방문 기록
- **관계**: IslandDTO의 recentVisits 리스트 구성요소

#### PlayerIslandDataDTO
```java
public record PlayerIslandDataDTO(
    @NotNull String playerUuid,
    @Nullable String currentIslandId,
    @NotNull IslandRole role,
    int totalIslandResets,
    long totalContribution,
    long lastJoined,
    long lastActivity
)
```
- **역할**: 플레이어-섬 관계 추적
- **특징**: 플레이어의 섬 활동 기록

### 3. Quest 패키지 (com.febrie.rpg.dto.quest) - 4개 DTO

퀘스트 시스템 관련 DTO:

#### PlayerQuestDTO
```java
public record PlayerQuestDTO(
    String playerId,
    Map<String, QuestProgress> activeQuests,
    Map<String, CompletedQuestDTO> completedQuests,
    long lastUpdated
)
```
- **역할**: 플레이어의 전체 퀘스트 상태
- **특징**: 활성/완료 퀘스트 분리 관리
- **주의**: 컬렉션 수정 시 새로운 Map 인스턴스 생성 필요

#### QuestProgressDTO
```java
public record QuestProgressDTO(
    @NotNull String questId,
    @NotNull String playerId,
    @NotNull String state,
    int currentObjectiveIndex,
    long startedAt,
    long lastUpdatedAt,
    long completedAt,
    @NotNull Map<String, ObjectiveProgressDTO> objectives
)
```
- **역할**: 진행 중인 퀘스트 상태
- **특징**: 목표별 진행도 추적

#### CompletedQuestDTO
```java
public record CompletedQuestDTO(
    @NotNull String questId,
    long completedAt,
    int completionCount,
    boolean rewarded
)
```
- **역할**: 완료된 퀘스트 정보
- **특징**: 반복 퀘스트 지원, 보상 수령 추적

#### ObjectiveProgressDTO
```java
public record ObjectiveProgressDTO(
    @NotNull String objectiveId,
    boolean completed,
    int progress,
    int target,
    long lastUpdated
)
```
- **역할**: 퀘스트 목표별 진행도
- **특징**: 진행률 계산 가능

### 4. Social 패키지 (com.febrie.rpg.dto.social) - 4개 DTO

소셜 기능 관련 DTO:

#### FriendshipDTO
```java
public record FriendshipDTO(
    @NotNull String player1Uuid,
    @NotNull String player1Name,
    @NotNull String player2Uuid,
    @NotNull String player2Name,
    long createdAt
)
```
- **역할**: 양방향 친구 관계
- **특징**: 두 플레이어 정보 동시 저장

#### FriendRequestDTO
```java
public record FriendRequestDTO(
    @NotNull String id,
    @NotNull String fromPlayerId,
    @NotNull String fromPlayerName,
    @NotNull String toPlayerId,
    @NotNull String toPlayerName,
    @NotNull LocalDateTime requestTime,
    @NotNull String status,
    String message
)
```
- **역할**: 친구 요청 관리
- **특징**: 상태 추적, 메시지 포함 가능

#### MailDTO
```java
public record MailDTO(
    @NotNull String mailId,
    @NotNull String senderUuid,
    @NotNull String senderName,
    @NotNull String receiverUuid,
    @NotNull String receiverName,
    @NotNull String subject,
    @NotNull String content,
    long sentAt,
    long readAt
)
```
- **역할**: 게임 내 메일 시스템
- **특징**: 읽음 상태 추적

#### WhisperMessageDTO
```java
public record WhisperMessageDTO(
    @NotNull String id,
    @NotNull String fromPlayerId,
    @NotNull String fromPlayerName,
    @NotNull String toPlayerId,
    @NotNull String toPlayerName,
    @NotNull String message,
    @NotNull LocalDateTime sentTime,
    boolean isRead
)
```
- **역할**: 플레이어 간 귓말
- **특징**: 실시간 메시징 지원

### 5. System 패키지 (com.febrie.rpg.dto.system) - 2개 DTO

시스템 관련 DTO:

#### ServerStatsDTO
```java
public record ServerStatsDTO(
    int onlinePlayers,
    int maxPlayers,
    int totalPlayers,
    long uptime,
    double tps,
    long totalPlaytime,
    @NotNull String version,
    long lastUpdated
)
```
- **역할**: 서버 통계 정보
- **특징**: 실시간 성능 지표 포함

#### LeaderboardEntryDTO
```java
public record LeaderboardEntryDTO(
    @NotNull String playerUuid,
    @NotNull String playerName,
    int rank,
    long value,
    @NotNull String type,
    long lastUpdated
)
```
- **역할**: 순위표 항목
- **특징**: 다양한 순위 타입 지원

## 데이터 흐름과 통합

### 1. 계층 구조
```
Player System:
PlayerDTO (기본 정보)
├── PlayerDataDTO (전체 데이터)
│   ├── PlayerProfileDTO (프로필)
│   └── WalletDTO (재화)
├── StatsDTO (능력치)
├── ProgressDTO (진행도)
└── TalentDTO (특성)

Island System:
IslandDTO (메인)
├── IslandMemberDTO[] (구성원)
├── IslandWorkerDTO[] (알바생)
├── IslandPermissionDTO (권한)
│   └── RolePermissions (역할별 권한)
├── IslandSettingsDTO (설정)
├── IslandUpgradeDTO (업그레이드)
├── IslandSpawnDTO (스폰 관리)
│   └── IslandSpawnPointDTO[] (스폰 포인트)
├── IslandLocationDTO (위치)
├── IslandInviteDTO[] (초대)
├── IslandVisitDTO[] (방문 기록)
└── PlayerIslandDataDTO (플레이어-섬 관계)

Quest System:
PlayerQuestDTO (플레이어 퀘스트)
├── QuestProgress[] (진행중)
│   └── ObjectiveProgressDTO[] (목표)
└── CompletedQuestDTO[] (완료)

Social System:
├── FriendshipDTO (친구 관계)
├── FriendRequestDTO (친구 요청)
├── MailDTO (메일)
└── WhisperMessageDTO (귓말)

System:
├── ServerStatsDTO (서버 통계)
└── LeaderboardEntryDTO (순위표)
```

### 2. Firebase/Firestore 통합

#### JSON 변환 패턴
모든 DTO가 JsonUtil 헬퍼 클래스를 사용하여 일관된 변환:

```java
// 저장
JsonObject json = dto.toJsonObject();

// 로드
DTO loaded = DTO.fromJsonObject(json);
```

#### Firestore Document 구조
```json
{
  "fields": {
    "fieldName": {
      "stringValue": "value",
      "integerValue": 123,
      "booleanValue": true,
      "mapValue": {
        "fields": { ... }
      },
      "arrayValue": {
        "values": [ ... ]
      }
    }
  }
}
```

### 3. 방어적 프로그래밍

#### 컬렉션 방어적 복사
```java
public record ExampleDTO(Map<String, String> data) {
    public ExampleDTO(Map<String, String> data) {
        this.data = new HashMap<>(data); // 생성자에서 복사
    }
    
    @Override
    public Map<String, String> data() {
        return new HashMap<>(data); // getter에서도 복사
    }
}
```

#### Record 수정 패턴
```java
// 잘못된 방법 (작동하지 않음)
data.activeQuests().put(questId, progress);

// 올바른 방법
Map<String, QuestProgress> updated = new HashMap<>(data.activeQuests());
updated.put(questId, progress);
PlayerQuestDTO newData = new PlayerQuestDTO(
    data.playerId(),
    updated,
    data.completedQuests(),
    System.currentTimeMillis()
);
```

## JsonUtil 헬퍼 클래스

모든 DTO가 사용하는 공통 유틸리티:

### 주요 메소드
- `createStringValue(String)`: 문자열 값 생성
- `createIntegerValue(long)`: 정수 값 생성
- `createBooleanValue(boolean)`: 불린 값 생성
- `createMapField(Map<K,V>, Function)`: Map 필드 생성
- `createArrayField(List<T>, Function)`: 배열 필드 생성
- `getStringValue(JsonObject, String, String)`: 문자열 값 추출
- `getLongValue(JsonObject, String, long)`: 정수 값 추출
- `getBooleanValue(JsonObject, String, boolean)`: 불린 값 추출

### 에러 처리
- `validateDTOJson(JsonObject, String)`: DTO JSON 유효성 검증
- `validateRequiredField(JsonObject, String, String)`: 필수 필드 검증

## 완료된 개선사항 (2025-01-29)

### 1. 모든 DTO가 Record로 통일
- CompletedQuestDTO를 class에서 record로 변환
- 100% record 사용으로 불변성 보장

### 2. JsonUtil 패턴 100% 적용
- 모든 30개 DTO가 JsonUtil 사용
- 중복 코드 제거 및 일관성 확보

### 3. 메소드 명명 통일
- 모든 DTO가 `toJsonObject()`/`fromJsonObject()` 사용
- 사용하지 않는 메소드 제거

### 4. 에러 처리 개선
- JsonUtil에 검증 메소드 추가
- 구체적인 에러 메시지 제공

## 모범 사례

### 1. 새로운 DTO 생성 시
```java
public record NewDTO(
    @NotNull String id,
    @NotNull String name,
    long timestamp
) {
    // 방어적 복사가 필요한 경우
    public NewDTO(String id, String name, long timestamp) {
        this.id = Objects.requireNonNull(id);
        this.name = Objects.requireNonNull(name);
        this.timestamp = timestamp;
    }
    
    @NotNull
    public JsonObject toJsonObject() {
        JsonObject fields = new JsonObject();
        fields.add("id", JsonUtil.createStringValue(id));
        fields.add("name", JsonUtil.createStringValue(name));
        fields.add("timestamp", JsonUtil.createIntegerValue(timestamp));
        return JsonUtil.wrapInDocument(fields);
    }
    
    @NotNull
    public static NewDTO fromJsonObject(@NotNull JsonObject json) {
        JsonObject fields = JsonUtil.unwrapDocument(json);
        return new NewDTO(
            JsonUtil.getStringValue(fields, "id", ""),
            JsonUtil.getStringValue(fields, "name", ""),
            JsonUtil.getLongValue(fields, "timestamp", 0L)
        );
    }
}
```

### 2. 테스트 작성
```java
@Test
public void testJsonRoundTrip() {
    NewDTO original = new NewDTO("id", "name", 12345L);
    JsonObject json = original.toJsonObject();
    NewDTO restored = NewDTO.fromJsonObject(json);
    assertEquals(original, restored);
}
```

## 결론

Sypixel RPG의 DTO 아키텍처는:
- **100% Record 타입**으로 불변성 보장
- **일관된 JsonUtil 패턴**으로 유지보수성 향상
- **Firebase/Firestore**와의 완벽한 통합
- **타입 안전성**과 **null 안전성** 보장
- **계층적 구조**로 복잡한 도메인 모델 표현
- **방어적 프로그래밍**으로 안전한 데이터 처리

이러한 설계는 대규모 RPG 게임의 복잡한 데이터 모델을 효과적으로 관리하고, 
동시에 성능과 안정성을 보장합니다.

---
*최종 업데이트: 2025-01-29*
*작성자: Claude Code Assistant*