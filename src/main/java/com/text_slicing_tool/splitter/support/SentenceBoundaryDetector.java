package com.text_slicing_tool.splitter.support;

import java.util.ArrayList;
import java.util.List;

/**
 * 句子边界识别工具。
 */
public final class SentenceBoundaryDetector {
    private static final String SENTENCE_END_CHARS = "。！？!?；;";
    private static final String CLOSING_CHARS = "”’」』）)]】》";

    private SentenceBoundaryDetector() {
    }

    /**
     * 按中文和英文句末标点拆分句子。
     *
     * @param text 待拆分文本
     * @return 句子列表
     */
    public static List<String> splitSentences(String text) {
        List<String> sentences = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return sentences;
        }

        int sentenceStart = 0;
        for (int index = 0; index < text.length(); index++) {
            char current = text.charAt(index);
            if (!isSentenceEnd(current)) {
                continue;
            }

            int sentenceEnd = consumeClosingChars(text, index + 1);
            addIfNotBlank(sentences, text.substring(sentenceStart, sentenceEnd));
            sentenceStart = sentenceEnd;
            index = sentenceEnd - 1;
        }

        if (sentenceStart < text.length()) {
            addIfNotBlank(sentences, text.substring(sentenceStart));
        }
        return sentences;
    }

    /**
     * 在指定位置附近寻找更自然的切割点。
     *
     * @param text 文本
     * @param targetIndex 目标切割位置
     * @param searchRadius 前后搜索半径
     * @return 找到的切割点，未找到时返回目标位置
     */
    public static int findNearestBoundary(String text, int targetIndex, int searchRadius) {
        if (text == null || text.isEmpty()) {
            return 0;
        }
        int safeTarget = Math.max(0, Math.min(targetIndex, text.length()));
        int safeRadius = Math.max(0, searchRadius);

        for (int index = safeTarget; index >= Math.max(0, safeTarget - safeRadius); index--) {
            if (isStrongBoundary(text, index)) {
                return consumeClosingChars(text, index + 1);
            }
        }

        for (int index = safeTarget; index < Math.min(text.length(), safeTarget + safeRadius); index++) {
            if (isStrongBoundary(text, index)) {
                return consumeClosingChars(text, index + 1);
            }
        }

        for (int index = safeTarget; index >= Math.max(0, safeTarget - safeRadius); index--) {
            if (isWeakBoundary(text, index)) {
                return index + 1;
            }
        }

        return safeTarget;
    }

    /**
     * 判断字符是否为句末标点。
     *
     * @param current 当前字符
     * @return 是否为句末标点
     */
    public static boolean isSentenceEnd(char current) {
        return SENTENCE_END_CHARS.indexOf(current) >= 0;
    }

    private static boolean isStrongBoundary(String text, int index) {
        return index >= 0 && index < text.length() && isSentenceEnd(text.charAt(index));
    }

    private static boolean isWeakBoundary(String text, int index) {
        if (index < 0 || index >= text.length()) {
            return false;
        }
        char current = text.charAt(index);
        return Character.isWhitespace(current)
                || current == ','
                || current == '，'
                || current == '、'
                || current == ':'
                || current == '：';
    }

    private static int consumeClosingChars(String text, int index) {
        int cursor = index;
        while (cursor < text.length() && CLOSING_CHARS.indexOf(text.charAt(cursor)) >= 0) {
            cursor++;
        }
        return cursor;
    }

    private static void addIfNotBlank(List<String> sentences, String sentence) {
        String trimmed = sentence.trim();
        if (!trimmed.isEmpty()) {
            sentences.add(trimmed);
        }
    }
}
