package com.febrie.rpg.command.admin.subcommand;

import net.kyori.adventure.text.Component;
import com.febrie.rpg.RPGMain;
import com.febrie.rpg.command.admin.subcommand.base.SubCommand;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.UnifiedColorUtil;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Reload subcommand
 *
 * @author Febrie, CoffeeTory
 */
public class ReloadCommand implements SubCommand {
    
    private final RPGMain plugin;
    
    public ReloadCommand(@NotNull RPGMain plugin) {
        this.plugin = plugin;
    }
    
    @Override
    @NotNull
    public String getName() {
        return "reload";
    }
    
    @Override
    @NotNull
    public String getDescription() {
        return "Reloads configuration files";
    }
    
    @Override
    @NotNull
    public String getUsage() {
        return "/rpgadmin reload [config|lang|all]";
    }
    
    @Override
    @NotNull
    public String getPermission() {
        return "rpg.admin.reload";
    }
    
    @Override
    @NotNull
    public List<String> getAliases() {
        return List.of("rl");
    }
    
    @Override
    public int getMinArgs() {
        return 0;
    }
    
    @Override
    public int getMaxArgs() {
        return 1;
    }
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        String type = args.length > 0 ? args[0].toLowerCase() : "all";
        
        sender.sendMessage(Component.translatable("commands.admin.reload.starting").color(UnifiedColorUtil.WARNING));
        
        long startTime = System.currentTimeMillis();
        boolean success = true;
        
        try {
            switch (type) {
                case "config" -> {
                    plugin.reloadConfig();
                    sender.sendMessage(Component.translatable("commands.admin.reload.config-complete").color(UnifiedColorUtil.SUCCESS));
                }
                case "lang" -> {
                    LangManager.reload();
                    sender.sendMessage(Component.translatable("commands.admin.reload.lang-complete").color(UnifiedColorUtil.SUCCESS));
                }
                case "all" -> {
                    plugin.reloadConfig();
                    LangManager.reload();
                    sender.sendMessage(Component.translatable("commands.admin.reload.config-complete").color(UnifiedColorUtil.SUCCESS));
                    sender.sendMessage(Component.translatable("commands.admin.reload.lang-complete").color(UnifiedColorUtil.SUCCESS));
                }
                default -> {
                    sender.sendMessage(Component.translatable("commands.admin.reload.unknown-type", Component.text(type)).color(UnifiedColorUtil.ERROR));
                    sender.sendMessage(Component.translatable("commands.admin.reload.available-types").color(UnifiedColorUtil.fromName("GRAY")));
                    return false;
                }
            }
        } catch (Exception e) {
            sender.sendMessage(Component.translatable("commands.admin.reload.error", Component.text(e.getMessage())).color(UnifiedColorUtil.ERROR));
            plugin.getLogger().severe("Reload error: " + e.getMessage());
            e.printStackTrace();
            success = false;
        }
        
        long elapsed = System.currentTimeMillis() - startTime;
        
        if (success) {
            sender.sendMessage(Component.translatable("commands.admin.reload.success", Component.text(elapsed)).color(UnifiedColorUtil.SUCCESS));
        } else {
            sender.sendMessage(Component.translatable("commands.admin.reload.failed").color(UnifiedColorUtil.ERROR));
        }
        
        return success;
    }
    
    @Override
    @NotNull
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("config", "lang", "all").stream()
                .filter(type -> type.startsWith(args[0].toLowerCase()))
                .toList();
        }
        return List.of();
    }
}