package com.febrie.rpg.command.admin.subcommand.base;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 서브커맨드 기본 추상 클래스
 * SubCommand 인터페이스의 기본 구현 제공
 *
 * @author Febrie
 */
public abstract class BaseSubCommand implements SubCommand {
    
    private final String name;
    private final String permission;
    private final String description;
    private String usage;
    private int minArgs = 0;
    private int maxArgs = -1;
    private boolean playerOnly = false;
    private List<String> aliases = List.of();
    
    protected BaseSubCommand(@NotNull String name, @NotNull String permission, @NotNull String description) {
        this.name = name;
        this.permission = permission;
        this.description = description;
        this.usage = "/rpgadmin " + name;
    }
    
    @Override
    @NotNull
    public String getName() {
        return name;
    }
    
    @Override
    @NotNull
    public String getDescription() {
        return description;
    }
    
    @Override
    @NotNull
    public String getUsage() {
        return usage;
    }
    
    @Override
    @NotNull
    public String getPermission() {
        return permission;
    }
    
    @Override
    @NotNull
    public List<String> getAliases() {
        return aliases;
    }
    
    @Override
    public int getMinArgs() {
        return minArgs;
    }
    
    @Override
    public int getMaxArgs() {
        return maxArgs;
    }
    
    @Override
    public boolean isPlayerOnly() {
        return playerOnly;
    }
    
    // Setter methods for configuration
    protected void setUsage(@NotNull String usage) {
        this.usage = usage;
    }
    
    protected void setMinArgs(int minArgs) {
        this.minArgs = minArgs;
    }
    
    protected void setMaxArgs(int maxArgs) {
        this.maxArgs = maxArgs;
    }
    
    protected void setPlayerOnly(boolean playerOnly) {
        this.playerOnly = playerOnly;
    }
    
    protected void setAliases(@NotNull List<String> aliases) {
        this.aliases = aliases;
    }
    
    // Helper methods
    protected List<String> getOnlinePlayerNames() {
        return Bukkit.getOnlinePlayers().stream()
            .map(Player::getName)
            .collect(Collectors.toList());
    }
    
    protected Player getPlayerFromSender(@NotNull CommandSender sender) {
        if (sender instanceof Player) {
            return (Player) sender;
        }
        return null;
    }
}