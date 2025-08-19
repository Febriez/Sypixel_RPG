package com.febrie.rpg.command.admin.subcommand.base;

import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 서브커맨드 기본 인터페이스
 * Command Pattern 적용
 *
 * @author Febrie, CoffeeTory
 */
public interface SubCommand {
    
    /**
     * 서브커맨드 이름
     */
    @NotNull
    String getName();
    
    /**
     * 서브커맨드 설명
     */
    @NotNull
    String getDescription();
    
    /**
     * 서브커맨드 사용법
     */
    @NotNull
    String getUsage();
    
    /**
     * 필요한 권한
     */
    @NotNull
    String getPermission();
    
    /**
     * 서브커맨드 별칭
     */
    @NotNull
    default List<String> getAliases() {
        return List.of();
    }
    
    /**
     * 최소 인자 개수
     */
    default int getMinArgs() {
        return 0;
    }
    
    /**
     * 최대 인자 개수 (-1은 무제한)
     */
    default int getMaxArgs() {
        return -1;
    }
    
    /**
     * 플레이어만 실행 가능한지
     */
    default boolean isPlayerOnly() {
        return false;
    }
    
    /**
     * 서브커맨드 실행
     */
    boolean execute(@NotNull CommandSender sender, @NotNull String[] args);
    
    /**
     * 탭 완성
     */
    @NotNull
    default List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        return List.of();
    }
}