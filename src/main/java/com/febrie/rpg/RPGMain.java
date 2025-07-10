package com.febrie.rpg;

import com.febrie.rpg.command.MainMenuCommand;
import com.febrie.rpg.command.ProfileCommand;
import com.febrie.rpg.gui.listener.GuiListener;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

public final class RPGMain extends JavaPlugin implements Listener {

    private static RPGMain plugin;
    private LangManager langManager;
    private GuiManager guiManager;
    private ProfileCommand profileCommand;
    private MainMenuCommand mainMenuCommand;

    @Override
    public void onEnable() {

        plugin = this;

        // Plugin startup logic
        getLogger().info("Sypixel RPG 플러그인이 활성화되었습니다!");

        // Initialize language system first
        this.langManager = new LangManager(this);
        getLogger().info("다국어 시스템이 초기화되었습니다!");

        // Initialize GUI system
        this.guiManager = new GuiManager(this, langManager);

        // Initialize commands
        this.profileCommand = new ProfileCommand(this, langManager, guiManager);
        this.mainMenuCommand = new MainMenuCommand(this, langManager, guiManager);

        // Register event listeners
        getServer().getPluginManager().registerEvents(new GuiListener(), this);
        getServer().getPluginManager().registerEvents(this, this);

        // Register commands
        registerCommands();

        getLogger().info("GUI 시스템이 등록되었습니다!");
        getLogger().info("사용 가능한 명령어:");
        getLogger().info("  - /profile, /프로필 : 자신의 프로필");
        getLogger().info("  - /viewprofile, /프로필보기 : 다른 플레이어 프로필");
        getLogger().info("  - /mainmenu, /메인메뉴 : 메인 메뉴");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        if (guiManager != null) {
            guiManager.cleanup();
        }
        getLogger().info("Sypixel RPG 플러그인이 비활성화되었습니다!");
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

    @EventHandler
    public void onPlayerJoin(@NotNull PlayerJoinEvent event) {
        // Detect and set player language
        Player player = event.getPlayer();
        langManager.getPlayerLanguage(player); // This will auto-detect and cache
    }

    @EventHandler
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        // Clean up player's data when they logout
        Player player = event.getPlayer();
        if (guiManager != null) {
            guiManager.onPlayerLogout(player);
        }
        if (langManager != null) {
            langManager.onPlayerLogout(player);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, String @NotNull [] args) {

        // Debug command for GUI and language statistics (OP only)
        if (command.getName().equalsIgnoreCase("sypixelrpg") && sender.isOp()) {
            if (!(sender instanceof Player player)) {
                sender.sendMessage("This command can only be used by players!");
                return true;
            }

            if (args.length > 0 && args[0].equalsIgnoreCase("stats")) {
                var guiStats = guiManager.getStats();
                var availableLanguages = langManager.getAvailableLanguages();

                player.sendMessage(Component.text("=== Sypixel RPG Stats ===", NamedTextColor.GOLD));
                player.sendMessage(Component.text("GUI Manager:", NamedTextColor.YELLOW));
                guiStats.forEach((key, value) -> {
                    player.sendMessage(Component.text("  " + key + ": " + value, NamedTextColor.WHITE));
                });

                player.sendMessage(Component.text("Language Manager:", NamedTextColor.YELLOW));
                player.sendMessage(Component.text("  Available languages: " + availableLanguages, NamedTextColor.WHITE));
                player.sendMessage(Component.text("  Your language: " + langManager.getPlayerLanguage(player), NamedTextColor.WHITE));

                return true;
            }

            if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
                langManager.reload();
                player.sendMessage(Component.text("Language files reloaded!", NamedTextColor.GREEN));
                return true;
            }

            // Show usage
            player.sendMessage(Component.text("Usage:", NamedTextColor.YELLOW));
            player.sendMessage(Component.text("  /sypixelrpg stats - Show statistics", NamedTextColor.WHITE));
            player.sendMessage(Component.text("  /sypixelrpg reload - Reload language files", NamedTextColor.WHITE));
            return true;
        }

        return false;
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

    public static RPGMain getPlugin() {
        return plugin;
    }
}