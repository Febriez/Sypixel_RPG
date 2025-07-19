package com.febrie.rpg;

import com.febrie.rpg.command.admin.AdminCommands;
import com.febrie.rpg.command.system.MainMenuCommand;
import com.febrie.rpg.command.system.SiteAccountCommand;
import com.febrie.rpg.database.FirestoreRestService;
import com.febrie.rpg.island.command.IslandCommand;
import com.febrie.rpg.island.command.IslandConfirmCommand;
import com.febrie.rpg.island.manager.IslandManager;
import com.febrie.rpg.dto.system.ServerStatsDTO;
import com.febrie.rpg.gui.listener.GuiListener;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.listener.DamageDisplayListener;
import com.febrie.rpg.listener.NPCInteractListener;
import com.febrie.rpg.listener.QuestEventListener;
import com.febrie.rpg.npc.manager.NPCManager;
import com.febrie.rpg.npc.NPCTraitSetter;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.quest.guide.QuestGuideManager;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.talent.TalentManager;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LogUtil;
import com.febrie.rpg.util.display.TextDisplayDamageManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

/**
 * Sypixel RPG 메인 플러그인 클래스
 * 서비스 패키지 제거 및 간소화된 아키텍처
 * 서버 통계 자동 저장 기능 추가
 *
 * @author Febrie, CoffeeTory
 */
public final class RPGMain extends JavaPlugin {

    private static RPGMain plugin;

    // 핵심 매니저
    private LangManager langManager;
    private GuiManager guiManager;
    private RPGPlayerManager rpgPlayerManager;
    private TalentManager talentManager;
    private NPCManager npcManager;
    private NPCTraitSetter npcTraitSetter;
    private QuestGuideManager questGuideManager;
    private TextDisplayDamageManager damageDisplayManager;
    private FirestoreRestService firestoreService;
    private IslandManager islandManager;

    // 명령어
    private MainMenuCommand mainMenuCommand;
    private AdminCommands adminCommands;
    private SiteAccountCommand siteAccountCommand;
    private IslandCommand islandCommand;
    private IslandConfirmCommand islandDeleteConfirmCommand;
    private IslandConfirmCommand islandResetConfirmCommand;

    // 서버 통계 관련
    private BukkitTask serverStatsTask;
    private BukkitTask dailyStatsTask;
    private String lastSavedDate = "";
    private long startTime;

    // TPS 측정
    private final long[] tpsHistory = new long[20]; // 최근 20번의 틱 시간 저장
    private int tpsIndex = 0;

    @Override
    public void onEnable() {
        plugin = this;
        startTime = System.currentTimeMillis();

        // LogUtil 초기화
        LogUtil.initialize(this);
        LogUtil.info("Sypixel RPG 플러그인이 활성화되었습니다!");

        // 시스템 초기화
        initializeSystems();

        // 리스너 및 명령어 등록
        registerListeners();
        registerCommands();

        // 서버 통계 시스템 시작
        startServerStatsSystem();

        LogUtil.info("모든 시스템이 성공적으로 초기화되었습니다!");
        LogUtil.info("사용 가능한 명령어: /메뉴, /menu, /mainmenu, /mm (메인 메뉴)");
    }

    @Override
    public void onDisable() {
        // 서버 통계 태스크 중지
        stopServerStatsSystem();

        // 최종 서버 통계 저장
        saveCurrentServerStats();

        // 모든 데이터 저장
        if (rpgPlayerManager != null) {
            rpgPlayerManager.saveAll();
        }
        
        // QuestManager 종료
        try {
            QuestManager questManager = QuestManager.getInstance();
            if (questManager != null) {
                questManager.shutdown();
            }
        } catch (IllegalStateException e) {
            // QuestManager가 초기화되지 않은 경우 무시
        }

        // GUI 정리
        if (guiManager != null) {
            guiManager.cleanup();
        }

        // NPC 매니저 정리
        if (npcManager != null) {
            npcManager.shutdown();
        }

        // 퀘스트 가이드 매니저 정리
        if (questGuideManager != null) {
            questGuideManager.shutdown();
        }

        // 데미지 표시 매니저 정리
        if (damageDisplayManager != null) {
            damageDisplayManager.shutdown();
        }
        
        // NPC Trait Setter 정리
        if (npcTraitSetter != null) {
            npcTraitSetter.cleanup();
        }
        
        // 섬 매니저 정리
        if (islandManager != null) {
            islandManager.clearCache();
        }

        // Firebase 연결 종료
        if (firestoreService != null) {
            firestoreService.shutdown();
        }

        LogUtil.info("Sypixel RPG 플러그인이 비활성화되었습니다!");
    }

    /**
     * 모든 핵심 시스템 초기화
     */
    private void initializeSystems() {
        // 언어 시스템 초기화 (가장 먼저)
        this.langManager = new LangManager(this);
        LogUtil.info("언어 시스템 초기화 완료");

        // GUI 시스템 초기화
        this.guiManager = new GuiManager(this, langManager);
        LogUtil.info("GUI 시스템 초기화 완료");

        // Firebase 서비스 초기화
        this.firestoreService = new FirestoreRestService(this);
        LogUtil.info("Firebase 서비스 초기화 완료");

        // 매니저 초기화
        this.rpgPlayerManager = new RPGPlayerManager(this, firestoreService);
        this.talentManager = new TalentManager(this);
        
        // QuestManager 초기화
        QuestManager.initialize(this, firestoreService);
        
        // NPCManager 초기화 (Citizens가 설치되어 있을 때만)
        if (getServer().getPluginManager().getPlugin("Citizens") != null) {
            this.npcManager = new NPCManager(this);
            LogUtil.info("NPC 관리자 초기화 완료");
            
            // NPC Trait Setter 초기화
            this.npcTraitSetter = new NPCTraitSetter(this);
            LogUtil.info("NPC Trait 설정 관리자 초기화 완료");
        }
        
        // 퀘스트 가이드 매니저 초기화
        this.questGuideManager = new QuestGuideManager(this);
        LogUtil.info("퀘스트 가이드 관리자 초기화 완료");
        
        // 데미지 표시 매니저 초기화
        this.damageDisplayManager = new TextDisplayDamageManager(this);
        LogUtil.info("TextDisplay 기반 데미지 표시 관리자 초기화 완료");
        
        // 섬 매니저 초기화
        this.islandManager = new IslandManager(this);
        this.islandManager.initialize();
        LogUtil.info("섬 관리자 초기화 완료");
        
        LogUtil.info("매니저 시스템 초기화 완료");

        // 명령어 객체 생성 (실제 등록은 registerCommands에서)
        this.mainMenuCommand = new MainMenuCommand(this, langManager, guiManager);
        this.adminCommands = new AdminCommands(this, rpgPlayerManager, guiManager, langManager);
        this.siteAccountCommand = new SiteAccountCommand(this, firestoreService);
        this.islandCommand = new IslandCommand(this, islandManager);
        this.islandDeleteConfirmCommand = new IslandConfirmCommand(this, islandManager, true);
        this.islandResetConfirmCommand = new IslandConfirmCommand(this, islandManager, false);
        LogUtil.info("명령어 시스템 초기화 완료");
    }

    /**
     * 모든 이벤트 리스너 등록
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        getServer().getPluginManager().registerEvents(rpgPlayerManager, this);
        
        // Citizens가 설치되어 있으면 NPC 리스너 등록
        if (getServer().getPluginManager().getPlugin("Citizens") != null) {
            getServer().getPluginManager().registerEvents(
                new NPCInteractListener(this, guiManager, langManager), this);
            LogUtil.info("Citizens NPC 리스너 등록 완료");
        }
        
        // 데미지 표시 리스너 등록
        getServer().getPluginManager().registerEvents(new DamageDisplayListener(this), this);
        LogUtil.info("데미지 표시 리스너 등록 완료");
        
        // 퀘스트 이벤트 리스너 등록
        getServer().getPluginManager().registerEvents(new QuestEventListener(this), this);
        LogUtil.info("퀘스트 이벤트 리스너 등록 완료");
        
        // 퀘스트 Trait 등록 아이템 리스너 등록
        getServer().getPluginManager().registerEvents(new com.febrie.rpg.quest.trait.QuestTraitRegistrationItem(), this);
        LogUtil.info("퀘스트 Trait 등록 아이템 리스너 등록 완료");
        
        // 메뉴 단축키 리스너 등록 (SHIFT + F)
        getServer().getPluginManager().registerEvents(new com.febrie.rpg.listener.MenuShortcutListener(this, guiManager, langManager), this);
        LogUtil.info("메뉴 단축키 리스너 등록 완료 (SHIFT + F)");
        
        // 섬 보호 리스너 등록
        getServer().getPluginManager().registerEvents(new com.febrie.rpg.island.listener.IslandProtectionListener(this, islandManager), this);
        LogUtil.info("섬 보호 리스너 등록 완료");
        
        // 섬 방문 추적 리스너 등록
        getServer().getPluginManager().registerEvents(new com.febrie.rpg.island.listener.IslandVisitListener(this), this);
        LogUtil.info("섬 방문 추적 리스너 등록 완료");
    }

    /**
     * 명령어 등록 - 간소화된 버전
     * 메인 명령어만 등록하고 나머지는 plugin.yml에서 aliases로 처리
     */
    private void registerCommands() {
        // 메인 메뉴 명령어 등록
        getCommand("메뉴").setExecutor(mainMenuCommand);
        getCommand("메뉴").setTabCompleter(mainMenuCommand);

        // 관리자 명령어 등록
        getCommand("rpgadmin").setExecutor(adminCommands);
        getCommand("rpgadmin").setTabCompleter(adminCommands);

        // 사이트 계정 명령어 등록
        getCommand("사이트계정발급").setExecutor(siteAccountCommand);

        // 섬 명령어 등록
        getCommand("섬").setExecutor(islandCommand);
        getCommand("섬삭제확인").setExecutor(islandDeleteConfirmCommand);
        getCommand("섬초기화확인").setExecutor(islandResetConfirmCommand);

        LogUtil.info("명령어가 등록되었습니다.");
        LogUtil.info("일반 유저: /메뉴 (별칭: /menu, /메인메뉴, /mainmenu, /mm)");
        LogUtil.info("일반 유저: /사이트계정발급 <이메일>");
        LogUtil.info("일반 유저: /섬 (섬 관리)");
        LogUtil.info("관리자: /rpgadmin");
    }

    /**
     * 서버 통계 시스템 시작
     */
    private void startServerStatsSystem() {
        LogUtil.info("서버 통계 시스템을 시작합니다...");

        // TPS 측정을 위한 틱 리스너 등록
        startTpsMonitoring();

        // 5분마다 통계 업데이트 (메모리에서만)
        serverStatsTask = getServer().getScheduler().runTaskTimerAsynchronously(this,
                this::updateInMemoryStats,
                6000L,  // 5분 후 시작
                6000L   // 5분마다 반복
        );

        // 매일 자정에 통계 저장
        scheduleDailyStatsSave();

        LogUtil.info("서버 통계 시스템이 시작되었습니다.");
    }

    /**
     * TPS 모니터링 시작
     */
    private void startTpsMonitoring() {
        getServer().getScheduler().runTaskTimer(this, () -> {
            long currentTime = System.currentTimeMillis();
            tpsHistory[tpsIndex] = currentTime;
            tpsIndex = (tpsIndex + 1) % tpsHistory.length;
        }, 0L, 1L); // 매 틱마다 실행
    }

    /**
     * 매일 통계 저장 스케줄 설정
     */
    private void scheduleDailyStatsSave() {
        // 다음 자정까지의 시간 계산
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay();
        long delayTicks = ChronoUnit.SECONDS.between(now, nextMidnight) * 20;

        // 자정에 저장 후 24시간마다 반복
        dailyStatsTask = getServer().getScheduler().runTaskTimerAsynchronously(this,
                this::saveCurrentServerStats,
                delayTicks,     // 다음 자정까지 대기
                24 * 60 * 60 * 20L  // 24시간마다 반복
        );

        LogUtil.info("매일 자정 서버 통계 저장이 예약되었습니다. (다음 저장: " + nextMidnight + ")");
    }

    /**
     * 메모리 내 통계 업데이트 (저장하지 않음)
     */
    private void updateInMemoryStats() {
        try {
            ServerStatsDTO currentStats = collectCurrentServerStats();
        } catch (Exception e) {
            LogUtil.error("서버 통계 업데이트 중 오류 발생", e);
        }
    }

    /**
     * 현재 서버 통계 저장
     */
    private void saveCurrentServerStats() {
        if (!firestoreService.isConnected()) {
            LogUtil.warning("Firebase 연결이 없어 서버 통계를 저장할 수 없습니다.");
            return;
        }

        String today = LocalDate.now().toString();

        // 같은 날짜에 이미 저장했으면 건너뛰기
        if (today.equals(lastSavedDate)) {
            return;
        }

        try {
            ServerStatsDTO serverStats = collectCurrentServerStats();

            firestoreService.saveServerStats(serverStats).thenAccept(success -> {
                if (success) {
                    lastSavedDate = today;
                    LogUtil.info("서버 통계가 성공적으로 저장되었습니다: " + today);
                    LogUtil.info("저장된 통계 - 온라인: " + serverStats.onlinePlayers() +
                            ", 총 플레이어: " + serverStats.totalPlayers() +
                            ", TPS: " + String.format("%.2f", serverStats.tps()));
                } else {
                    LogUtil.error("서버 통계 저장에 실패했습니다: " + today);
                }
            });
        } catch (Exception e) {
            LogUtil.error("서버 통계 수집 중 오류 발생", e);
        }
    }

    /**
     * 현재 서버 통계 수집
     */
    private ServerStatsDTO collectCurrentServerStats() {
        int onlinePlayers = getServer().getOnlinePlayers().size();
        int maxPlayers = getServer().getMaxPlayers();
        int totalPlayers = rpgPlayerManager != null ? rpgPlayerManager.getOnlinePlayerCount() : onlinePlayers;
        long uptime = System.currentTimeMillis() - startTime;
        double tps = calculateCurrentTPS();
        long totalPlaytime = calculateTotalPlaytime();
        String version = getServer().getVersion();

        return new ServerStatsDTO(
                onlinePlayers, maxPlayers, totalPlayers,
                uptime, tps, totalPlaytime, version
        );
    }

    /**
     * 현재 TPS 계산
     */
    private double calculateCurrentTPS() {
        if (tpsHistory[0] == 0) {
            return 20.0; // 아직 충분한 데이터가 없으면 이상적인 값 반환
        }

        try {
            long newest = tpsHistory[(tpsIndex - 1 + tpsHistory.length) % tpsHistory.length];
            long oldest = tpsHistory[tpsIndex];

            if (oldest == 0 || newest <= oldest) {
                return 20.0;
            }

            long timeDiff = newest - oldest;
            double secondsDiff = timeDiff / 1000.0;

            if (secondsDiff <= 0) {
                return 20.0;
            }

            // 20틱 동안의 시간을 측정했으므로
            double actualTps = (tpsHistory.length - 1) / secondsDiff;

            // TPS는 최대 20으로 제한
            return Math.min(20.0, Math.max(0.0, actualTps));
        } catch (Exception e) {
            return 20.0;
        }
    }

    /**
     * 총 플레이타임 계산
     */
    private long calculateTotalPlaytime() {
        if (rpgPlayerManager == null) {
            return 0L;
        }
        return rpgPlayerManager.getTotalPlaytime(); // 임시로 서버 업타임 반환
    }

    /**
     * 서버 통계 시스템 중지
     */
    private void stopServerStatsSystem() {
        if (serverStatsTask != null && !serverStatsTask.isCancelled()) {
            serverStatsTask.cancel();
            LogUtil.info("서버 통계 업데이트 태스크가 중지되었습니다.");
        }

        if (dailyStatsTask != null && !dailyStatsTask.isCancelled()) {
            dailyStatsTask.cancel();
            LogUtil.info("일일 통계 저장 태스크가 중지되었습니다.");
        }
    }

    /**
     * 현재 TPS 조회 (외부에서 사용)
     */
    public double getCurrentTPS() {
        return calculateCurrentTPS();
    }

    /**
     * 서버 업타임 조회 (외부에서 사용)
     */
    public long getUptime() {
        return System.currentTimeMillis() - startTime;
    }

    // Getter 메소드들

    public LangManager getLangManager() {
        return langManager;
    }

    public GuiManager getGuiManager() {
        return guiManager;
    }

    public RPGPlayerManager getRPGPlayerManager() {
        return rpgPlayerManager;
    }

    public TalentManager getTalentManager() {
        return talentManager;
    }

    public NPCManager getNPCManager() {
        return npcManager;
    }

    public QuestGuideManager getQuestGuideManager() {
        return questGuideManager;
    }
    
    public QuestManager getQuestManager() {
        return QuestManager.getInstance();
    }

    public TextDisplayDamageManager getDamageDisplayManager() {
        return damageDisplayManager;
    }

    public FirestoreRestService getFirebaseService() {
        return firestoreService;
    }
    
    public FirestoreRestService getFirestoreService() {
        return firestoreService;
    }
    
    public IslandManager getIslandManager() {
        return islandManager;
    }

    public static RPGMain getPlugin() {
        return plugin;
    }

    public long getStartTime() {
        return startTime;
    }
}