package com.febrie.rpg.command.admin;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.command.admin.subcommand.*;
import com.febrie.rpg.command.admin.subcommand.base.SubCommand;
import com.febrie.rpg.util.LogUtil;
import com.febrie.rpg.util.UnifiedColorUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import java.util.List;
/**
 * 관리자 명령어 핸들러
 * Command Pattern을 사용한 서브커맨드 관리
 *
 * @author Febrie, CoffeeTory
 */
public class AdminCommandHandler implements CommandExecutor, TabCompleter {

    private final RPGMain plugin;
    private final Map<String, SubCommand> subCommands = new HashMap<>();
    private final Map<String, SubCommand> aliases = new HashMap<>();

    public AdminCommandHandler(@NotNull RPGMain plugin) {
        this.plugin = plugin;
        registerSubCommands();
    }

    private void registerSubCommands() {
        // 서브커맨드 등록
        registerSubCommand(new GiveCurrencyCommand(plugin));
        registerSubCommand(new SetLevelCommand(plugin));
        registerSubCommand(ExpCommand.create(plugin));
        registerSubCommand(JobCommand.create(plugin));
        registerSubCommand(ViewProfileCommand.create(plugin));
        registerSubCommand(QuestCommand.create(plugin));
        registerSubCommand(IslandCommand.create(plugin));
        registerSubCommand(NpcCommand.create(plugin));
        registerSubCommand(new ReloadCommand(plugin));
        registerSubCommand(new SaveCommand(plugin));
        registerSubCommand(new DebugCommand(plugin));
        registerSubCommand(new StatsSubCommand(plugin.getRPGPlayerManager()));
        registerSubCommand(new LangTestCommand(plugin));
    }

    private void registerSubCommand(@NotNull SubCommand command) {
        subCommands.put(command.getName()
                .toLowerCase(), command);

        // 별칭 등록
        for (String alias : command.getAliases()) {
            aliases.put(alias.toLowerCase(), command);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command,
                             @NotNull String label, @NotNull String[] args) {
        // 권한 체크
        if (!sender.hasPermission("rpg.admin")) {
            sender.sendMessage(UnifiedColorUtil.parse("&c이 명령어를 사용할 권한이 없습니다"));
            return true;
        }

        // 도움말
        if (args.length == 0 || args[0].equalsIgnoreCase("help")) {
            showHelp(sender, args.length > 1 ? args[1] : "1");
            return true;
        }

        // 서브커맨드 찾기
        String subCommandName = args[0].toLowerCase();
        SubCommand subCommand = subCommands.get(subCommandName);
        // 별칭 확인
        if (subCommand == null) {
            subCommand = aliases.get(subCommandName);
        }

        if (subCommand == null) {
            sender.sendMessage(UnifiedColorUtil.parse("&c알 수 없는 명령어입니다: " + args[0]));
            sender.sendMessage(UnifiedColorUtil.parse("&7/rpgadmin help - 도움말 보기"));
            return true;
        }

        // 플레이어 전용 명령어 체크
        if (subCommand.isPlayerOnly() && !(sender instanceof Player)) {
            sender.sendMessage(UnifiedColorUtil.parse("&c이 명령어는 플레이어만 사용할 수 있습니다"));
            return true;
        }

        if (!sender.hasPermission(subCommand.getPermission())) {
            sender.sendMessage(UnifiedColorUtil.parse("&c이 서브커맨드를 사용할 권한이 없습니다"));
            return true;
        }

        // 인자 개수 체크
        String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
        if (subArgs.length < subCommand.getMinArgs()) {
            sender.sendMessage(UnifiedColorUtil.parse("&c인자가 부족합니다"));
            sender.sendMessage(UnifiedColorUtil.parse("&7사용법: " + subCommand.getUsage()));
            return true;
        }

        if (subCommand.getMaxArgs() != -1 && subArgs.length > subCommand.getMaxArgs()) {
            sender.sendMessage(UnifiedColorUtil.parse("&c인자가 너무 많습니다"));
            sender.sendMessage(UnifiedColorUtil.parse("&7사용법: " + subCommand.getUsage()));
            return true;
        }

        // 서브커맨드 실행
        try {
            boolean success = subCommand.execute(sender, subArgs);
            if (!success) {
                sender.sendMessage(UnifiedColorUtil.parse("&7사용법: " + subCommand.getUsage()));
            }
        } catch (Exception e) {
            sender.sendMessage(UnifiedColorUtil.parse("&c명령어 실행 중 오류가 발생했습니다"));
            LogUtil.error("Error executing admin command", e);
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command,
                                      @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("rpg.admin")) {
            return List.of();
        }

        if (args.length == 1) {
            // 서브커맨드 목록
            return subCommands.keySet()
                    .stream()
                    .filter(name -> name.startsWith(args[0].toLowerCase()))
                    .sorted()
                    .collect(Collectors.toList());
        }

        if (args.length > 1) {
            // 서브커맨드 탭 완성
            String subCommandName = args[0].toLowerCase();
            SubCommand subCommand = subCommands.get(subCommandName);

            if (subCommand == null) {
                subCommand = aliases.get(subCommandName);
            }

            if (subCommand != null && sender.hasPermission(subCommand.getPermission())) {
                String[] subArgs = Arrays.copyOfRange(args, 1, args.length);
                return subCommand.tabComplete(sender, subArgs);
            }
        }

        return List.of();
    }

    private void showHelp(@NotNull CommandSender sender, @NotNull String pageStr) {
        int page;
        try {
            page = Integer.parseInt(pageStr);
        } catch (NumberFormatException e) {
            page = 1;
        }

        List<SubCommand> commands = new ArrayList<>(subCommands.values());
        commands.sort(Comparator.comparing(SubCommand::getName));
        int perPage = 8;
        int totalPages = (commands.size() + perPage - 1) / perPage;
        page = Math.max(1, Math.min(page, totalPages));
        sender.sendMessage(UnifiedColorUtil.parse("&6=== RPG Admin Commands (페이지 " + page + "/" + totalPages + ") ==="));
        int start = (page - 1) * perPage;
        int end = Math.min(start + perPage, commands.size());
        for (int i = start; i < end; i++) {
            SubCommand cmd = commands.get(i);
            sender.sendMessage(UnifiedColorUtil.parse(String.format(
                    "&e%s &7- %s",
                    cmd.getUsage(),
                    cmd.getDescription()
            )));
        }

        if (totalPages > 1) {
            sender.sendMessage(UnifiedColorUtil.parse("&7/rpgadmin help " + (page + 1) + " - 다음 페이지"));
        }
    }
}
