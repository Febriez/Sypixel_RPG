package com.febrie.rpg;

import com.febrie.rpg.command.MainMenuCommand;
import com.febrie.rpg.command.ProfileCommand;
import com.febrie.rpg.gui.listener.GuiListener;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.talent.TalentManager;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

/**
 * Main plugin class for Sypixel RPG
 * Handles plugin initialization and core functionality
 *
 * @author Febrie, CoffeeTory
 */
public final class RPGMain extends JavaPlugin {

    private static RPGMain plugin;
    private LangManager langManager;
    private GuiManager guiManager;
    private ProfileCommand profileCommand;
    private MainMenuCommand mainMenuCommand;
    private RPGPlayerManager rpgPlayerManager;
    private TalentManager talentManager;

    @Override
    public void onEnable() {
        plugin = this;

        getLogger().info("Sypixel RPG 플러그인이 활성화되었습니다!");

        // Initialize core systems
        initializeSystems();

        // Register listeners and commands
        registerListeners();
        registerCommands();

        getLogger().info("모든 시스템이 성공적으로 초기화되었습니다!");
        getLogger().info("사용 가능한 명령어: /profile, /프로필, /viewprofile, /프로필보기, /mainmenu, /메인메뉴");
    }

    @Override
    public void onDisable() {
        if (guiManager != null) {
            guiManager.cleanup();
        }
        getLogger().info("Sypixel RPG 플러그인이 비활성화되었습니다!");
    }

    /**
     * Initialize all core systems
     */
    private void initializeSystems() {
        // Initialize language system first
        this.langManager = new LangManager(this);
        getLogger().info("언어 시스템 초기화 완료");

        // Initialize GUI system
        this.guiManager = new GuiManager(this, langManager);
        getLogger().info("GUI 시스템 초기화 완료");

        //Initialize Manager
        this.rpgPlayerManager = new RPGPlayerManager(this);
        this.talentManager = new TalentManager(this);

        // Initialize commands
        this.profileCommand = new ProfileCommand(this, langManager, guiManager);
        this.mainMenuCommand = new MainMenuCommand(this, langManager, guiManager);
        getLogger().info("명령어 시스템 초기화 완료");
    }

    /**
     * Register all event listeners
     */
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        getServer().getPluginManager().registerEvents(rpgPlayerManager, this);
    }

    /**
     * Register all commands
     */
    private void registerCommands() {
        // Profile commands (Korean and English)
        profileCommand.register("profile");
        profileCommand.register("프로필");
        profileCommand.register("viewprofile");
        profileCommand.register("프로필보기");

        // Main menu commands (Korean and English)
        mainMenuCommand.register("mainmenu");
        mainMenuCommand.register("메인메뉴");
        mainMenuCommand.register("menu");
        mainMenuCommand.register("메뉴");
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String @NotNull [] args) {

        // Admin command for plugin management (OP only)
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

            // Show usage
            player.sendMessage(Component.text("=== Sypixel RPG Admin Commands ===", NamedTextColor.GOLD));
            player.sendMessage(Component.text("/sypixelrpg stats - Show plugin statistics", NamedTextColor.WHITE));
            player.sendMessage(Component.text("/sypixelrpg reload - Reload language files", NamedTextColor.WHITE));
            return true;
        }

        return false;
    }

    /**
     * Show plugin statistics to admin
     */
    private void showStats(@NotNull Player player) {
        var guiStats = guiManager.getStats();
        var availableLanguages = langManager.getAvailableLanguages();

        player.sendMessage(Component.text("=== Sypixel RPG Statistics ===", NamedTextColor.GOLD));

        player.sendMessage(Component.text("GUI System:", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("  Active GUIs: " + guiStats.get("activeGuis"), NamedTextColor.WHITE));
        player.sendMessage(Component.text("  Cached GUIs: " + guiStats.get("cachedGuis"), NamedTextColor.WHITE));

        player.sendMessage(Component.text("Language System:", NamedTextColor.YELLOW));
        player.sendMessage(Component.text("  Available: " + availableLanguages, NamedTextColor.WHITE));
        player.sendMessage(Component.text("  Your language: " + langManager.getPlayerLanguage(player), NamedTextColor.WHITE));

        player.sendMessage(Component.text("Online Players: " + getServer().getOnlinePlayers().size(), NamedTextColor.AQUA));
    }

    /**
     * Reload plugin systems
     */
    private void reloadPlugin(@NotNull Player player) {
        try {
            langManager.reload();
            player.sendMessage(Component.text("Language files reloaded successfully!", NamedTextColor.GREEN));
        } catch (Exception e) {
            player.sendMessage(Component.text("Failed to reload: " + e.getMessage(), NamedTextColor.RED));
            getLogger().severe("Reload failed: " + e.getMessage());
        }
    }

    /**
     * Gets the language manager instance
     */
    public LangManager getLangManager() {
        return langManager;
    }

    /**
     * Gets the GUI manager instance
     */
    public GuiManager getGuiManager() {
        return guiManager;
    }

    /**
     * Gets the plugin instance
     */
    public static RPGMain getPlugin() {
        return plugin;
    }

    public RPGPlayerManager getRPGPlayerManager() {
        return rpgPlayerManager;
    }

    public TalentManager getTalentManager() {
        return talentManager;
    }
}