package com.febrie.rpg.command.admin.subcommand;

import net.kyori.adventure.text.Component;
import com.febrie.rpg.command.admin.subcommand.base.SubCommand;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.registry.QuestRegistry;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Statistics command handler
 */
public class StatsSubCommand implements SubCommand {
    
    private final RPGPlayerManager playerManager;
    
    public StatsSubCommand(@NotNull RPGPlayerManager playerManager) {
        this.playerManager = playerManager;
    }
    
    @Override
    @NotNull
    public String getName() {
        return "stats";
    }
    
    @Override
    @NotNull
    public String getDescription() {
        return "View server statistics";
    }
    
    @Override
    @NotNull
    public String getUsage() {
        return "/rpgadmin stats";
    }
    
    @Override
    @NotNull
    public String getPermission() {
        return "rpg.admin.stats";
    }
    
    @Override
    @NotNull
    public List<String> getAliases() {
        return List.of("info", "status");
    }
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        sender.sendMessage(Component.translatable("commands.admin.stats.title"));
        sender.sendMessage(Component.translatable("commands.admin.stats.online-players", 
                Component.text(Bukkit.getOnlinePlayers().size())));
        sender.sendMessage(Component.translatable("commands.admin.stats.loaded-players", 
                Component.text(playerManager.getAllPlayers().size())));
        sender.sendMessage(Component.translatable("commands.admin.stats.registered-quests", 
                Component.text(QuestID.values().length)));
        sender.sendMessage(Component.translatable("commands.admin.stats.implemented-quests", 
                Component.text(QuestRegistry.getImplementedCount())));

        // 메모리 사용량
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        sender.sendMessage(Component.translatable("commands.admin.stats.memory-usage", 
                Component.text(usedMemory), Component.text(maxMemory)));

        return true;
    }
}