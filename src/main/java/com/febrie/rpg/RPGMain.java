package com.febrie.rpg;

import com.febrie.rpg.command.MainMenuCommand;
import com.febrie.rpg.command.ProfileCommand;
import com.febrie.rpg.database.FirebaseService;
import com.febrie.rpg.gui.listener.GuiListener;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.service.DataService;
import com.febrie.rpg.service.GuiService;
import com.febrie.rpg.service.PlayerService;
import com.febrie.rpg.talent.TalentManager;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.LogUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Sypixel RPG 메인 플러그인 클래스
 * 개선된 서비스 아키텍처 적용
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

    // 서비스 레이어
    private PlayerService playerService;
    private GuiService guiService;
    private DataService dataService;
    private FirebaseService firebaseService;

    // 명령어
    private ProfileCommand profileCommand;
    private MainMenuCommand mainMenuCommand;

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
        if (playerService != null) {
            playerService.saveAllOnlinePlayers().join();
        }

        // GUI 정리
        if (guiManager != null) {
            guiManager.cleanup();
        }

        // 캐시 정리
        if (dataService != null) {
            dataService.clearAllCaches();
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

        // 매니저 초기화
        this.rpgPlayerManager = new RPGPlayerManager(this);
        this.talentManager = new TalentManager(this);
        LogUtil.info("매니저 시스템 초기화 완료");

        // Firebase 서비스 초기화
        this.firebaseService = new FirebaseService(this);

        // 서비스 레이어 초기화
        this.playerService = new PlayerService(this, firebaseService);
        this.guiService = new GuiService(this);
        this.dataService = new DataService(this, playerService);
        LogUtil.info("서비스 레이어 초기화 완료");

        // 명령어 초기화
        this.profileCommand = new ProfileCommand(this, langManager, guiManager);
        this.mainMenuCommand = new MainMenuCommand(this, langManager, guiManager);
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

        LogUtil.debug("명령어 등록 완료");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String @NotNull [] args) {

        // 관리자 명령어 (OP 전용)
        if (command.getName().equalsIgnoreCase("sypixelrpg") && sender.isOp()) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players!");
                return true;
            }

            if (args.length > 0 && args[0].equalsIgnoreCase("stats")) {
                showStats(player);
                return true;
            }

            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                reloadPlugin(player);
                return true;
            }

            if (args.length > 0 && args[0].equalsIgnoreCase("debug")) {
                toggleDebug(player);
                return true;
            }

            // 사용법 표시
            player.sendMessage(Component.text("=== Sypixel RPG Admin Commands ===", NamedTextColor.GOLD));
            player.sendMessage(Component.text("/sypixelrpg stats - Show plugin statistics", NamedTextColor.WHITE));
            player.sendMessage(Component.text("/sypixelrpg reload - Reload language files", NamedTextColor.WHITE));
            player.sendMessage(Component.text("/sypixelrpg debug - Toggle debug mode", NamedTextColor.WHITE));
            return true;
        }

        return false;
    }

    /**
     * 플러그인 통계 표시
     */
    private void showStats(@NotNull Player player) {
        player.sendMessage(Component.text("=== Sypixel RPG Statistics ===", NamedTextColor.GOLD));

        // GUI 통계
        var guiStats = guiManager.getStats();
        player.sendMessage(Component.text("GUI System:", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("  Active GUIs: " + guiStats.get("activeGuis"), NamedTextColor.WHITE));
        player.sendMessage(Component.text("  Cached GUIs: " + guiStats.get("cachedGuis"), NamedTextColor.WHITE));

        // 언어 시스템 통계
        var availableLanguages = langManager.getAvailableLanguages();
        var langCacheStats = langManager.getCacheStats();
        player.sendMessage(Component.text("Language System:", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("  Available: " + availableLanguages, NamedTextColor.WHITE));
        player.sendMessage(Component.text("  Your language: " + langManager.getPlayerLanguage(player), NamedTextColor.WHITE));
        player.sendMessage(Component.text("  Cache stats: " + langCacheStats, NamedTextColor.WHITE));

        // 데이터 서비스 통계
        player.sendMessage(Component.text("Data Service:", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("  Mode: " + dataService.getDataMode(), NamedTextColor.WHITE));
        player.sendMessage(Component.text("  Firebase: " + (firebaseService.isConnected() ? "Connected" : "Disconnected"), NamedTextColor.WHITE));

        // 서버 정보
        player.sendMessage(Component.text("Server:", NamedTextColor.AQUA));
        player.sendMessage(Component.text("  Online Players: " + getServer().getOnlinePlayers().size(), NamedTextColor.WHITE));

        // 메모리 정보
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        player.sendMessage(Component.text("  Memory: " + usedMemory + "/" + maxMemory + " MB", NamedTextColor.WHITE));
    }

    /**
     * 플러그인 리로드
     */
    private void reloadPlugin(@NotNull Player player) {
        try {
            langManager.reload();
            guiService.clearCache();
            dataService.clearAllCaches();

            player.sendMessage(Component.text("Language files and caches reloaded successfully!", NamedTextColor.GREEN));
            LogUtil.info("플러그인이 " + player.getName() + "에 의해 리로드되었습니다.");
        } catch (Exception e) {
            player.sendMessage(Component.text("Failed to reload: " + e.getMessage(), NamedTextColor.RED));
            LogUtil.error("리로드 실패", e);
        }
    }

    /**
     * 디버그 모드 토글
     */
    private void toggleDebug(@NotNull Player player) {
        // 현재 구현에서는 LogUtil에 static 디버그 모드가 있다고 가정
        // TODO: 실제 디버그 모드 구현
        player.sendMessage(Component.text("Debug mode toggled!", NamedTextColor.YELLOW));
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

    public PlayerService getPlayerService() {
        return playerService;
    }

    public GuiService getGuiService() {
        return guiService;
    }

    public DataService getDataService() {
        return dataService;
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