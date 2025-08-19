package com.febrie.rpg.database.constants;

/**
 * 데이터베이스 관련 상수 정의
 *
 * @author Febrie
 */
public final class DatabaseConstants {
    
    private DatabaseConstants() {
        // 유틸리티 클래스는 인스턴스화 방지
    }
    
    // 배치 처리 관련
    public static final int BATCH_SIZE_LIMIT = 200; // 최적화된 배치 크기 (500 -> 200)
    public static final int BATCH_PARTITION_SIZE = 100; // 배치 분할 크기
    
    // 캐시 관련
    public static final int PLAYER_CACHE_MAX_SIZE = 5000; // 플레이어 캐시 크기 (10,000 -> 5,000)
    public static final long CACHE_TTL_MINUTES = 5; // 캐시 TTL (분)
    public static final long CACHE_CLEANUP_INTERVAL_MINUTES = 1; // 캐시 정리 주기
    
    // 재시도 관련
    public static final int MAX_RETRY_ATTEMPTS = 3; // 최대 재시도 횟수
    public static final long INITIAL_RETRY_DELAY_MS = 1000; // 초기 재시도 지연시간 (밀리초)
    public static final long RETRY_BACKOFF_MULTIPLIER = 2; // 재시도 지연시간 배수
    
    // 타임아웃 관련
    public static final long TRANSACTION_TIMEOUT_SECONDS = 10; // 트랜잭션 타임아웃
    public static final long WRITE_TIMEOUT_SECONDS = 5; // 쓰기 타임아웃
    public static final long READ_TIMEOUT_SECONDS = 3; // 읽기 타임아웃
    
    // 저장 간격 관련 (밀리초)
    public static final long SAVE_INTERVAL_HIGH_PRIORITY = 60_000; // 1분
    public static final long SAVE_INTERVAL_MEDIUM_PRIORITY = 180_000; // 3분
    public static final long SAVE_INTERVAL_LOW_PRIORITY = 300_000; // 5분
    public static final long SAVE_INTERVAL_RANDOM_OFFSET = 120_000; // 0-2분 랜덤 오프셋
    
    // 배치 저장 작업 관련
    public static final long BATCH_SAVE_INTERVAL_TICKS = 600L; // 30초 (20틱 = 1초)
    public static final long BATCH_SAVE_INITIAL_DELAY_TICKS = 20L; // 1초 후 시작
    
    // 섬 관련
    public static final int ISLAND_INITIAL_SIZE = 85; // 섬 초기 크기
    public static final int ISLAND_MIN_BIOME_SIZE = 500; // 바이옴 최소 크기
    public static final int ISLAND_BIOME_SIZE_MULTIPLE = 16; // 바이옴 크기 배수
    public static final long ISLAND_DELETE_COOLDOWN_MS = 7L * 24 * 60 * 60 * 1000; // 7일
    public static final long ISLAND_INVITE_EXPIRES_MS = 60 * 1000; // 초대 만료 시간 (60초)
    
    // 컬렉션 이름
    public static final String COLLECTION_PLAYERS = "players";
    public static final String COLLECTION_ISLANDS = "islands";
    public static final String COLLECTION_QUESTS = "quests";
    public static final String COLLECTION_FRIENDSHIPS = "friendships";
    public static final String COLLECTION_MAILS = "mails";
    public static final String COLLECTION_LEADERBOARDS = "leaderboards";
    public static final String COLLECTION_SERVER_STATS = "server_stats";
    
    // 페이지네이션
    public static final int DEFAULT_PAGE_SIZE = 50;
    public static final int MAX_PAGE_SIZE = 200;
}