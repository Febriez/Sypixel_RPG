package com.febrie.rpg.command.admin.subcommand;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.command.admin.subcommand.base.BaseSubCommand;
import com.febrie.rpg.economy.CurrencyType;
import com.febrie.rpg.player.RPGPlayer;
import com.febrie.rpg.player.RPGPlayerManager;
import com.febrie.rpg.quest.manager.QuestManager;
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
            sender.sendMessage(Component.translatable("commands.admin.viewprofile.usage"));
            return true;
        }
        
        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            sender.sendMessage(Component.translatable("commands.admin.player-not-found"));
            return true;
        }
        
        RPGPlayer rpgPlayer = playerManager.getPlayer(target);
        if (rpgPlayer == null) {
            sender.sendMessage(Component.translatable("commands.admin.player-data-not-found"));
            return true;
        }
        
        // 프로필 정보 표시
        sender.sendMessage(Component.translatable("commands.admin.viewprofile.title", Component.text(target.getName())));
        sender.sendMessage(Component.translatable("commands.admin.viewprofile.level", Component.text(rpgPlayer.getLevel())));
        sender.sendMessage(Component.translatable("commands.admin.viewprofile.exp", 
            Component.text(rpgPlayer.getExperience()), Component.text(rpgPlayer.getExperienceToNextLevel())));
        
        Component jobNameComp = rpgPlayer.getJob() != null ? 
            Component.text(rpgPlayer.getJob().name()) : 
            Component.translatable("commands.admin.viewprofile.job-none");
        String jobName = PlainTextComponentSerializer.plainText().serialize(jobNameComp);
        sender.sendMessage(Component.translatable("commands.admin.viewprofile.job", Component.text(jobName)));
        
        sender.sendMessage(Component.translatable("commands.admin.viewprofile.gold", 
            Component.text(rpgPlayer.getWallet().getBalance(CurrencyType.GOLD))));
        sender.sendMessage(Component.translatable("commands.admin.viewprofile.diamond", 
            Component.text(rpgPlayer.getWallet().getBalance(CurrencyType.DIAMOND))));
        
        // 퀘스트 정보
        Map<String, com.febrie.rpg.dto.quest.ActiveQuestDTO> activeQuests = 
            questManager.getActiveQuests(target.getUniqueId());
        sender.sendMessage(Component.translatable("commands.admin.viewprofile.active-quests", 
            Component.text(activeQuests.size())));
        
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