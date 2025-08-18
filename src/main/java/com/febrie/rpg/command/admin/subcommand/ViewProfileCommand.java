package com.febrie.rpg.command.admin.subcommand;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.command.admin.subcommand.base.BaseSubCommand;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.quest.manager.QuestManager;
import com.febrie.rpg.util.LangManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 플레이어 프로필 확인 명령어
 * 
 * @author Febrie
 */
public class ViewProfileCommand extends BaseSubCommand {
    
    private final RPGPlayerManager playerManager;
    private final QuestManager questManager;
    
    private ViewProfileCommand(@NotNull String name, @NotNull String permission, @NotNull String description,
                               @NotNull RPGPlayerManager playerManager, @NotNull QuestManager questManager) {
        super(name, permission, description);
        this.playerManager = playerManager;
        this.questManager = questManager;
        this.setMinArgs(1);
        this.setUsage("/rpgadmin viewprofile <player>");
    }
    
    public static ViewProfileCommand create(@NotNull RPGMain plugin) {
        return new ViewProfileCommand("viewprofile", "rpg.admin.viewprofile", "View a player's RPG profile",
                                     plugin.getRPGPlayerManager(), QuestManager.getInstance());
    }
    
    @Override
    public boolean execute(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length < 1) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.viewprofile.usage"));
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-not-found"));
            return true;
        }
        
        RPGPlayer rpgPlayer = playerManager.getPlayer(target);
        if (rpgPlayer == null) {
            sender.sendMessage(LangManager.getComponent(sender, "commands.admin.player-data-not-found"));
            return true;
        }
        
        // 프로필 정보 표시
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.viewprofile.title", 
            "player", target.getName()));
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.viewprofile.level", 
            "level", String.valueOf(rpgPlayer.getLevel())));
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.viewprofile.exp", 
            "current", String.valueOf(rpgPlayer.getExperience()), 
            "next", String.valueOf(rpgPlayer.getExperienceToNextLevel())));
        
        Component jobNameComp = rpgPlayer.getJob() != null ? 
            Component.text(rpgPlayer.getJob().name()) : 
            LangManager.getComponent(sender, "commands.admin.viewprofile.job-none");
        String jobName = PlainTextComponentSerializer.plainText().serialize(jobNameComp);
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.viewprofile.job", 
            "job", jobName));
        
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.viewprofile.gold", 
            "amount", String.valueOf(rpgPlayer.getWallet().getBalance(CurrencyType.GOLD))));
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.viewprofile.diamond", 
            "amount", String.valueOf(rpgPlayer.getWallet().getBalance(CurrencyType.DIAMOND))));
        
        // 퀘스트 정보
        Map<String, com.febrie.rpg.dto.quest.ActiveQuestDTO> activeQuests = 
            questManager.getActiveQuests(target.getUniqueId());
        sender.sendMessage(LangManager.getComponent(sender, "commands.admin.viewprofile.active-quests", 
            "count", String.valueOf(activeQuests.size())));
        
        return true;
    }
    
    @Override
    public List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        if (args.length == 1) {
            return getOnlinePlayerNames().stream()
                .filter(name -> name.toLowerCase().startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        }
        return List.of();
    }
}