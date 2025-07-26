package com.febrie.rpg.command.admin.subcommand;

import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.quest.QuestID;
import com.febrie.rpg.quest.registry.QuestRegistry;
import com.febrie.rpg.util.LangManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

/**
 * 통계 명령어 처리
 */
public class StatsSubCommand implements AdminSubCommand {
    
    private final RPGPlayerManager playerManager;
    private final LangManager langManager;
    
    public StatsSubCommand(@NotNull RPGPlayerManager playerManager, @NotNull LangManager langManager) {
        this.playerManager = playerManager;
        this.langManager = langManager;
    }
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.stats.title"));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.stats.online-players", 
                "count", String.valueOf(Bukkit.getOnlinePlayers().size())));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.stats.loaded-players", 
                "count", String.valueOf(playerManager.getAllPlayers().size())));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.stats.registered-quests", 
                "count", String.valueOf(QuestID.values().length)));
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.stats.implemented-quests", 
                "count", String.valueOf(QuestRegistry.getImplementedCount())));

        // 메모리 사용량
        Runtime runtime = Runtime.getRuntime();
        long usedMemory = (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024;
        long maxMemory = runtime.maxMemory() / 1024 / 1024;
        sender.sendMessage(langManager.getComponent(sender, "commands.admin.stats.memory-usage", 
                "used", String.valueOf(usedMemory), 
                "max", String.valueOf(maxMemory)));

        return true;
    }
}