package com.febrie.rpg.command.admin.subcommand;

import net.kyori.adventure.text.Component;
import com.febrie.rpg.RPGMain;
import com.febrie.rpg.command.admin.subcommand.base.BaseSubCommand;
import com.febrie.rpg.job.JobType;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 직업 관리 명령어
 * 
 * @author Febrie
 */
public class JobCommand extends BaseSubCommand {
    
    private final RPGPlayerManager playerManager;
    
    private JobCommand(@NotNull String name, @NotNull String permission, @NotNull String description,
                      @NotNull RPGPlayerManager playerManager) {
        super(name, permission, description);
        this.playerManager = playerManager;
        this.setMinArgs(3);
        this.setUsage("/rpgadmin job set <player> <job|none>");
    }
    
    public static JobCommand create(@NotNull RPGMain plugin) {
        return new JobCommand("job", "rpg.admin.job", "Manage player jobs", plugin.getRPGPlayerManager());
    }
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 3 || !args[0].equalsIgnoreCase("set")) {
            sender.sendMessage(Component.translatable("commands.admin.job.usage"));
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[1]);
        if (target == null) {
            sender.sendMessage(Component.translatable("commands.admin.player-not-found"));
            return true;
        }
        
        String jobName = args[2];
        JobType job = null;
        
        // "none" 또는 "reset"이 아닌 경우 직업 찾기
        if (!jobName.equalsIgnoreCase("none") && !jobName.equalsIgnoreCase("reset")) {
            try {
                job = JobType.valueOf(jobName.toUpperCase());
            } catch (IllegalArgumentException e) {
                sender.sendMessage(Component.translatable("commands.admin.job.invalid"));
                
                // 사용 가능한 직업 목록 표시
                String jobList = Arrays.stream(JobType.values())
                    .map(JobType::name)
                    .collect(Collectors.joining(", "));
                sender.sendMessage(Component.translatable("commands.admin.job.available", 
                    Component.text(jobList)));
                return true;
            }
        }
        
        RPGPlayer rpgPlayer = playerManager.getPlayer(target);
        if (rpgPlayer == null) {
            sender.sendMessage(Component.translatable("commands.admin.player-data-not-found"));
            return true;
        }
        
        rpgPlayer.setJob(job);
        
        if (job == null) {
            sender.sendMessage(Component.translatable("commands.admin.job.reset", 
                Component.text(target.getName())));
            target.sendMessage(Component.translatable("commands.admin.job.your-reset"));
        } else {
            sender.sendMessage(Component.translatable("commands.admin.job.success", 
                Component.text(target.getName()), Component.text(job.name())));
            target.sendMessage(Component.translatable("commands.admin.job.your-changed", 
                Component.text(job.name())));
        }
        
        return true;
    }
    
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("set");
        } else if (args.length == 2 && args[0].equalsIgnoreCase("set")) {
            return getOnlinePlayerNames().stream()
                .filter(name -> name.toLowerCase().startsWith(args[1].toLowerCase()))
                .collect(Collectors.toList());
        } else if (args.length == 3 && args[0].equalsIgnoreCase("set")) {
            List<String> jobs = Arrays.stream(JobType.values())
                .map(JobType::name)
                .map(String::toLowerCase)
                .collect(Collectors.toList());
            jobs.add("none");
            jobs.add("reset");
            
            return jobs.stream()
                .filter(job -> job.toLowerCase().startsWith(args[2].toLowerCase()))
                .collect(Collectors.toList());
        }
        return List.of();
    }
}