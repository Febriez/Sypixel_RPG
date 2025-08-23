package com.febrie.rpg.command.admin.subcommand;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * 관리자 하위 명령어 인터페이스
 */
public interface AdminSubCommand {
    
    /**
     * 명령어 실행
     * @param sender 명령어 실행자
     * @param args 인자 (첫 번째 인자는 이미 제거된 상태)
     * @return 명령어 처리 성공 여부
     */
    boolean execute(@NotNull CommandSender sender, @NotNull String[] args);
    
    /**
     * 탭 완성 제공
     * @param sender 명령어 실행자
     * @param args 인자 (첫 번째 인자는 이미 제거된 상태)
     * @return 탭 완성 목록
     */
    @Nullable
    default List<String> tabComplete(@NotNull CommandSender sender, @NotNull String[] args) {
        return null;
    }
    
    /**
     * 명령어가 플레이어 전용인지 여부
     * @return 플레이어 전용 여부
     */
    default boolean requiresPlayer() {
        return false;
    }
    
    /**
     * 필요한 권한
     * @return 권한 문자열
     */
    @NotNull
    default String getPermission() {
        return "rpg.admin";
    }
}