package com.febrie.rpg.gui.framework;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * 뒤로가기 기능을 제공하는 GUI 인터페이스
 * 
 * 하위 GUI에서 상위 GUI로 돌아갈 때 상태를 유지하기 위해 사용
 * 
 * @author CoffeeTory
 */
public interface BackableGui extends GuiFramework {
    
    /**
     * 뒤로가기 시 돌아갈 GUI를 반환
     * 
     * @return 돌아갈 GUI 인스턴스, null인 경우 GUI를 닫음
     */
    @Nullable
    GuiFramework getBackDestination();
    
    /**
     * 이 GUI가 뒤로가기를 지원하는지 여부
     * 
     * @return 뒤로가기 지원 여부
     */
    default boolean supportsBack() {
        return getBackDestination() != null;
    }
    
    /**
     * 뒤로가기 실행
     * 
     * 기본 구현은 getBackDestination()의 GUI를 열거나,
     * null인 경우 GUI를 닫음
     */
    default void executeBack() {
        GuiFramework backGui = getBackDestination();
        if (backGui != null) {
            // GuiManager를 통해 GUI 열기
            // 이 부분은 구현체에서 처리
        } else {
            // GUI 닫기
            getInventory().close();
        }
    }
}