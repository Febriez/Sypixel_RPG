package com.febrie.rpg.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import net.kyori.adventure.text.Component;
/**
 * 텍스트 관련 유틸리티 클래스
 *
 * @author Febrie
 */
public class TextUtil {

    private TextUtil() {
        throw new UnsupportedOperationException("유틸리티 클래스는 인스턴스화할 수 없습니다.");
    }

    /**
     * 텍스트를 지정된 길이로 줄바꿈
     *
     * @param text      줄바꿈할 텍스트
     * @param maxLength 한 줄의 최대 길이
     * @return 줄바꿈된 텍스트 배열
     */
    @NotNull
    public static String[] wrapText(@NotNull String text, int maxLength) {
        if (text.isEmpty()) {
            return new String[]{""};
        }

        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();

        for (String word : words) {
            // 단어 자체가 maxLength를 초과하는 경우 강제로 자르기
            if (word.length() > maxLength) {
                // 현재 줄이 있으면 먼저 추가
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
                
                // 긴 단어를 maxLength 단위로 자르기
                for (int i = 0; i < word.length(); i += maxLength) {
                    int endIndex = Math.min(i + maxLength, word.length());
                    lines.add(word.substring(i, endIndex));
                }
                continue;
            }

            // 현재 줄에 단어를 추가했을 때 길이 확인
            if (currentLine.length() + word.length() + (currentLine.length() > 0 ? 1 : 0) > maxLength) {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
            }

            // 단어 추가
            if (currentLine.length() > 0) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        }

        // 마지막 줄 추가
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines.isEmpty() ? new String[]{""} : lines.toArray(new String[0]);
    }

    /**
     * 텍스트를 지정된 길이로 줄바꿈 (null 체크 포함)
     *
     * @param text         줄바꿈할 텍스트 (null 가능)
     * @param maxLength    한 줄의 최대 길이
     * @param defaultValue null인 경우 반환할 기본값
     * @return 줄바꿈된 텍스트 배열
     */
    @NotNull
    public static String[] wrapTextOrDefault(String text, int maxLength, @NotNull String defaultValue) {
        if (text == null || text.isEmpty()) {
            return new String[]{defaultValue};
        }
        return wrapText(text, maxLength);
    }

    /**
     * 한글과 영문의 실제 표시 너비를 고려한 텍스트 줄바꿈
     * 한글은 2칸, 영문은 1칸으로 계산
     *
     * @param text      줄바꿈할 텍스트
     * @param maxWidth  한 줄의 최대 너비
     * @return 줄바꿈된 텍스트 배열
     */
    @NotNull
    public static String[] wrapTextWithWidth(@NotNull String text, int maxWidth) {
        if (text.isEmpty()) {
            return new String[]{""};
        }

        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        int currentWidth = 0;

        for (String word : words) {
            int wordWidth = getDisplayWidth(word);
            int spaceWidth = currentLine.length() > 0 ? 1 : 0;

            // 단어가 한 줄을 초과하는 경우
            if (wordWidth > maxWidth) {
                // 현재 줄 마무리
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                    currentWidth = 0;
                }

                // 긴 단어를 문자 단위로 자르기
                StringBuilder tempLine = new StringBuilder();
                int tempWidth = 0;
                
                for (char c : word.toCharArray()) {
                    int charWidth = isKorean(c) ? 2 : 1;
                    if (tempWidth + charWidth > maxWidth) {
                        lines.add(tempLine.toString());
                        tempLine = new StringBuilder();
                        tempWidth = 0;
                    }
                    tempLine.append(c);
                    tempWidth += charWidth;
                }
                
                if (tempLine.length() > 0) {
                    currentLine = tempLine;
                    currentWidth = tempWidth;
                }
                continue;
            }

            // 현재 줄에 단어 추가 시 너비 초과 확인
            if (currentWidth + spaceWidth + wordWidth > maxWidth) {
                if (currentLine.length() > 0) {
                    lines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                    currentWidth = 0;
                }
            }

            // 단어 추가
            if (currentLine.length() > 0) {
                currentLine.append(" ");
                currentWidth += 1;
            }
            currentLine.append(word);
            currentWidth += wordWidth;
        }

        // 마지막 줄 추가
        if (currentLine.length() > 0) {
            lines.add(currentLine.toString());
        }

        return lines.isEmpty() ? new String[]{""} : lines.toArray(new String[0]);
    }

    /**
     * 문자열의 표시 너비 계산 (한글 2, 영문 1)
     */
    private static int getDisplayWidth(@NotNull String text) {
        int width = 0;
        for (char c : text.toCharArray()) {
            width += isKorean(c) ? 2 : 1;
        }
        return width;
    }

    /**
     * 한글 문자인지 확인
     */
    private static boolean isKorean(char c) {
        return (c >= 0xAC00 && c <= 0xD7A3) || // 한글 완성형
               (c >= 0x1100 && c <= 0x11FF) || // 한글 자모
               (c >= 0x3130 && c <= 0x318F);   // 한글 호환 자모
    }
}