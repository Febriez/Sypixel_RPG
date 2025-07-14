package com.febrie.rpg.command;

import com.febrie.rpg.gui.impl.MainMenuGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.LangManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * 메인 메뉴 명령어 처리 - 간소화된 버전
 *
 * @author Febrie, CoffeeTory
 */
public class MainMenuCommand extends BaseCommand {

    private final GuiManager guiManager;

    public MainMenuCommand(@NotNull Plugin plugin, @NotNull LangManager langManager,
                           @NotNull GuiManager guiManager) {
        super(plugin, langManager);
        this.guiManager = guiManager;
    }

    @Override
    protected boolean executeCommand(@NotNull CommandSender sender, @NotNull Command command,
                                     @NotNull String label, @NotNull String[] args) {

        if (!requirePlayer(sender)) {
            return true;
        }

        Player player = (Player) sender;

        if (!checkPermission(player, "sypixelrpg.mainmenu")) {
            return true;
        }

        // 네비게이션 스택 초기화하고 메인 메뉴 열기
        guiManager.clearNavigationStack(player);
        MainMenuGui mainMenu = new MainMenuGui(guiManager, langManager, player);
        guiManager.openGui(player, mainMenu);

        langManager.sendMessage(player, "commands.mainmenu.success");

        return true;
    }
}