package com.febrie.rpg;

import com.febrie.rpg.command.admin.AdminCommands;
import com.febrie.rpg.command.system.MainMenuCommand;
import com.febrie.rpg.command.system.SiteAccountCommand;
import com.febrie.rpg.command.island.IslandCommand;
import com.febrie.rpg.command.social.FriendCommand;
import com.febrie.rpg.command.social.MailCommand;
import com.febrie.rpg.command.social.WhisperCommand;
import com.febrie.rpg.database.FirestoreManager;
import com.febrie.rpg.database.service.impl.PlayerFirestoreService;
import com.febrie.rpg.database.service.impl.QuestFirestoreService;
import com.febrie.rpg.database.service.impl.IslandFirestoreService;
import com.febrie.rpg.database.service.impl.PlayerIslandDataService;
import com.febrie.rpg.island.manager.IslandManager;
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
import com.febrie.rpg.system.ServerStatsManager;
import com.febrie.rpg.talent.TalentManager;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LogUtil;
import com.febrie.rpg.util.display.TextDisplayDamageManager;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.FirestoreOptions;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.CompletableFuture;

/**
 * Sypixel RPG 메인 플러그인 클래스
 * 서비스 패키지 제거 및 간소화된 아키텍처
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
    private FirestoreManager firestoreManager;
    private PlayerFirestoreService playerFirestoreService;
    private QuestFirestoreService questFirestoreService;
    private IslandFirestoreService islandFirestoreService;
    private PlayerIslandDataService playerIslandDataService;
    private IslandManager islandManager;
    private ServerStatsManager serverStatsManager;

    // 명령어
    private MainMenuCommand mainMenuCommand;
    private AdminCommands adminCommands;
    private SiteAccountCommand siteAccountCommand;
    private IslandCommand islandCommand;
    private FriendCommand friendCommand;
    private MailCommand mailCommand;
    private WhisperCommand whisperCommand;

    @Override
    public void onEnable() {
        plugin = this;

        // LogUtil 초기화
        LogUtil.initialize(this);

        // 시스템 초기화
        initializeSystems();

        // 리스너 및 명령어 등록
        registerListeners();
        registerCommands();

        // 서버 통계 시스템 시작
        if (serverStatsManager != null) {
            serverStatsManager.start();
        }
    }

    @Override
    public void onDisable() {
        // 서버 통계 매니저 종료
        if (serverStatsManager != null) {
            serverStatsManager.shutdown();
        }
        
        // 섬 매니저 종료
        if (islandManager != null) {
            islandManager.shutdown();
        }

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

        // Firestore 서비스들 정리
        if (playerFirestoreService != null) {
            playerFirestoreService.shutdown();
        }
        if (questFirestoreService != null) {
            questFirestoreService.shutdown();
        }
        if (islandFirestoreService != null) {
            islandFirestoreService.shutdown();
        }

        // Firebase 종료
        if (firestoreManager != null) {
            firestoreManager.shutdown();
        }

    }

    /**
     * 모든 핵심 시스템 초기화
     */
    private void initializeSystems() {
        // 언어 시스템 초기화 (가장 먼저)
        this.langManager = new LangManager(this);

        // GUI 시스템 초기화
        this.guiManager = new GuiManager(this, langManager);

        // Firestore 초기화
        this.firestoreManager = new FirestoreManager(this);
        if (!firestoreManager.initialize()) {
            LogUtil.error("Firestore 초기화 실패! 일부 기능이 제한될 수 있습니다.");
            // Firestore 없이도 서버가 실행되도록 계속 진행
        } else {
            // Firestore 서비스 초기화
            if (firestoreManager.getFirestore() != null) {
                this.playerFirestoreService = new PlayerFirestoreService(this, firestoreManager.getFirestore());
                this.questFirestoreService = new QuestFirestoreService(this, firestoreManager.getFirestore());
                this.islandFirestoreService = new IslandFirestoreService(this, firestoreManager.getFirestore());
                this.playerIslandDataService = new PlayerIslandDataService(this, firestoreManager.getFirestore());
            }
        }

        // 매니저 초기화
        this.rpgPlayerManager = RPGPlayerManager.create(this, playerFirestoreService);
        this.talentManager = new TalentManager(this);
        
        // QuestManager 초기화
        QuestManager.initialize(this, questFirestoreService);
        
        // NPCManager 초기화 (Citizens가 설치되어 있을 때만)
        if (getServer().getPluginManager().getPlugin("Citizens") != null) {
            this.npcManager = new NPCManager(this);
            
            // NPC Trait Setter 초기화
            this.npcTraitSetter = new NPCTraitSetter(this);
        }
        
        // 퀘스트 가이드 매니저 초기화
        this.questGuideManager = new QuestGuideManager(this);
        
        // 데미지 표시 매니저 초기화
        this.damageDisplayManager = new TextDisplayDamageManager(this);
        
        // 섬 매니저 초기화
        this.islandManager = new IslandManager(this, islandFirestoreService, playerIslandDataService);
        this.islandManager.initialize();
        
        // 서버 통계 매니저 초기화
        this.serverStatsManager = new ServerStatsManager(this, firestoreManager, rpgPlayerManager);

        // 명령어 객체 생성 (실제 등록은 registerCommands에서)
        this.mainMenuCommand = new MainMenuCommand(this, langManager, guiManager);
        this.adminCommands = new AdminCommands(this, rpgPlayerManager, guiManager, langManager);
        this.siteAccountCommand = new SiteAccountCommand(this);
        this.islandCommand = new IslandCommand(this);
        this.friendCommand = new FriendCommand(this, guiManager, langManager);
        this.mailCommand = new MailCommand(this, guiManager, langManager);
        this.whisperCommand = new WhisperCommand(this);
    }

    /**
     * 모든 이벤트 리스너 등록
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        
        // RPGPlayerManager 리스너 등록 (null 체크)
        if (rpgPlayerManager != null) {
            getServer().getPluginManager().registerEvents(rpgPlayerManager, this);
        }
        
        // Citizens가 설치되어 있으면 NPC 리스너 등록
        if (getServer().getPluginManager().getPlugin("Citizens") != null) {
            getServer().getPluginManager().registerEvents(
                new NPCInteractListener(this, guiManager, langManager), this);
        }
        
        // 데미지 표시 리스너 등록
        getServer().getPluginManager().registerEvents(new DamageDisplayListener(this), this);
        
        // 퀘스트 이벤트 리스너 등록
        getServer().getPluginManager().registerEvents(new QuestEventListener(this), this);
        
        // 퀘스트 Trait 등록 아이템 리스너 등록
        getServer().getPluginManager().registerEvents(new com.febrie.rpg.quest.trait.QuestTraitRegistrationItem(), this);
        
        // 보상 Trait 등록 아이템 리스너 등록
        getServer().getPluginManager().registerEvents(new com.febrie.rpg.quest.trait.RewardTraitRegistrationItem(), this);
        
        // 메뉴 단축키 리스너 등록 (SHIFT + F)
        getServer().getPluginManager().registerEvents(new com.febrie.rpg.listener.MenuShortcutListener(this, guiManager, langManager), this);
        
        // 섬 보호 리스너 등록 (null 체크)
        if (islandManager != null) {
            getServer().getPluginManager().registerEvents(new com.febrie.rpg.island.listener.IslandProtectionListener(this, islandManager), this);
        }
        
        // 섬 방문 추적 리스너 등록
        getServer().getPluginManager().registerEvents(new com.febrie.rpg.island.listener.IslandVisitListener(this), this);
    }

    /**
     * 명령어 등록 - 간소화된 버전
     * 메인 명령어만 등록하고 나머지는 plugin.yml에서 aliases로 처리
     */
    private void registerCommands() {
        // 메인 메뉴 명령어 등록
        getCommand("메뉴").setExecutor(mainMenuCommand);
        getCommand("메뉴").setTabCompleter(mainMenuCommand);

        // 관리자 명령어 등록 (null 체크)
        if (adminCommands != null) {
            getCommand("rpgadmin").setExecutor(adminCommands);
            getCommand("rpgadmin").setTabCompleter(adminCommands);
        }

        // 사이트 계정 명령어 등록
        if (siteAccountCommand != null) {
            getCommand("사이트계정발급").setExecutor(siteAccountCommand);
        }
        
        // 섬 명령어 등록
        if (islandCommand != null) {
            getCommand("섬").setExecutor(islandCommand);
        }
        
        // 소셜 명령어 등록
        if (friendCommand != null) {
            getCommand("친구").setExecutor(friendCommand);
            getCommand("친구").setTabCompleter(friendCommand);
        }
        
        if (mailCommand != null) {
            getCommand("메일").setExecutor(mailCommand);
            getCommand("메일").setTabCompleter(mailCommand);
        }
        
        if (whisperCommand != null) {
            getCommand("귀속말").setExecutor(whisperCommand);
            getCommand("귀속말").setTabCompleter(whisperCommand);
        }

    }

    /**
     * 현재 TPS 조회 (외부에서 사용)
     */
    public double getCurrentTPS() {
        return serverStatsManager != null ? serverStatsManager.calculateCurrentTPS() : 20.0;
    }

    /**
     * 서버 업타임 조회 (외부에서 사용)
     */
    public long getUptime() {
        return serverStatsManager != null ? serverStatsManager.getUptime() : 0L;
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

    
    public IslandManager getIslandManager() {
        return islandManager;
    }
    
    public FirestoreManager getFirestoreManager() {
        return firestoreManager;
    }

    public static RPGMain getPlugin() {
        return plugin;
    }
    
    public static RPGMain getInstance() {
        return plugin;
    }

    public long getStartTime() {
        return serverStatsManager != null ? serverStatsManager.getUptime() : 0L;
    }
    
    public PlayerFirestoreService getPlayerFirestoreService() {
        return playerFirestoreService;
    }
}