package com.febrie.rpg.listener;

import com.febrie.rpg.RPGMain;
import com.febrie.rpg.gui.impl.system.MainMenuGui;
import com.febrie.rpg.gui.manager.GuiManager;
import com.febrie.rpg.util.LangManager;
import com.febrie.rpg.util.SoundUtil;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.jetbrains.annotations.NotNull;

/**
 * 메뉴 단축키 리스너
 * SHIFT + F (손 바꾸기) 키로 메인 메뉴 열기
 */
public class MenuShortcutListener implements Listener {
    
    private final RPGMain plugin;
    private final GuiManager guiManager;
    private final LangManager langManager;
    
    public MenuShortcutListener(@NotNull RPGMain plugin, @NotNull GuiManager guiManager, @NotNull LangManager langManager) {
        this.plugin = plugin;
        this.guiManager = guiManager;
        this.langManager = langManager;
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onSwapHandItems(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        
        // 이벤트 취소
        event.setCancelled(true);
        
        // 메인 메뉴 열기
        MainMenuGui mainMenu = new MainMenuGui(guiManager, langManager, player);
        guiManager.openGui(player, mainMenu);
        SoundUtil.playOpenSound(player);
    }
}