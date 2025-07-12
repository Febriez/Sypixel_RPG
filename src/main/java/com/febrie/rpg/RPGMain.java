package com.febrie.rpg;

import com.febrie.rpg.command.AdminCommands;
import com.febrie.rpg.command.MainMenuCommand;
import com.febrie.rpg.command.ProfileCommand;
import com.febrie.rpg.database.FirebaseService;
import com.febrie.rpg.gui.listener.GuiListener;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.talent.TalentManager;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LogUtil;
import org.bukkit.plugin.java.JavaPlugin;

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
    private FirebaseService firebaseService;

    // 명령어
    private ProfileCommand profileCommand;
    private MainMenuCommand mainMenuCommand;
    private AdminCommands adminCommands;

    private long startTime;

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

        LogUtil.info("모든 시스템이 성공적으로 초기화되었습니다!");
        LogUtil.info("사용 가능한 명령어: /profile, /프로필, /viewprofile, /프로필보기, /mainmenu, /메인메뉴");
    }

    @Override
    public void onDisable() {
        // 모든 데이터 저장
        if (rpgPlayerManager != null) {
            rpgPlayerManager.saveAll();
        }

        // GUI 정리
        if (guiManager != null) {
            guiManager.cleanup();
        }

        // Firebase 연결 종료
        if (firebaseService != null) {
            firebaseService.shutdown();
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
        this.firebaseService = new FirebaseService(this);
        LogUtil.info("Firebase 서비스 초기화 완료");

        // 매니저 초기화
        this.rpgPlayerManager = new RPGPlayerManager(this, firebaseService);
        this.talentManager = new TalentManager(this);
        LogUtil.info("매니저 시스템 초기화 완료");

        // 명령어 초기화
        this.profileCommand = new ProfileCommand(this, langManager, guiManager);
        this.mainMenuCommand = new MainMenuCommand(this, langManager, guiManager);
        this.adminCommands = new AdminCommands(this, langManager);
        LogUtil.info("명령어 시스템 초기화 완료");
    }

    /**
     * 모든 이벤트 리스너 등록
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        getServer().getPluginManager().registerEvents(rpgPlayerManager, this);
        LogUtil.debug("이벤트 리스너 등록 완료");
    }

    /**
     * 모든 명령어 등록
     */
    private void registerCommands() {
        // 프로필 명령어 (한국어 및 영어)
        profileCommand.register("profile");
        profileCommand.register("프로필");
        profileCommand.register("viewprofile");
        profileCommand.register("프로필보기");

        // 메인 메뉴 명령어 (한국어 및 영어)
        mainMenuCommand.register("mainmenu");
        mainMenuCommand.register("메인메뉴");
        mainMenuCommand.register("menu");
        mainMenuCommand.register("메뉴");

        // 관리자 명령어
        getCommand("sypixelrpg").setExecutor(adminCommands);
        getCommand("sypixelrpg").setTabCompleter(adminCommands);

        LogUtil.debug("명령어 등록 완료");
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

    public FirebaseService getFirebaseService() {
        return firebaseService;
    }

    public static RPGMain getPlugin() {
        return plugin;
    }

    public long getStartTime() {
        return startTime;
    }
}