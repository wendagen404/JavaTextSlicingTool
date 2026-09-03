package com.text_slicing_tool.splitter.support;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 受保护文本替换工具。
 */
public final class ProtectedTextReplacer {
    private static final Pattern PROTECTED_PATTERN = Pattern.compile(
            "(https?://[^\\s，。！？；;、)）\\]}]+)"
                    + "|(www\\.[^\\s，。！？；;、)）\\]}]+)"
                    + "|([a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})"
                    + "|(\\bv?\\d+(?:\\.\\d+){1,}\\b)"
                    + "|((?:[a-zA-Z]:)?[\\\\/][^\\s，。！？；;、]+)"
    );

    private ProtectedTextReplacer() {
    }

    /**
     * 将 URL、邮箱、版本号、路径等内容替换为占位符，避免自定义符号切割时误切。
     *
     * @param text 原始文本
     * @return 替换结果
     */
    public static ProtectedText protect(String text) {
        if (text == null || text.isEmpty()) {
            return new ProtectedText("", Map.of(), List.of());
        }

        Matcher matcher = PROTECTED_PATTERN.matcher(text);
        StringBuilder protectedText = new StringBuilder();
        Map<String, String> placeholders = new LinkedHashMap<>();
        List<ProtectedRange> ranges = new ArrayList<>();
        int lastEnd = 0;
        int index = 0;

        while (matcher.find()) {
            protectedText.append(text, lastEnd, matcher.start());
            String placeholder = "__PROTECTED_" + index + "__";
            protectedText.append(placeholder);
            placeholders.put(placeholder, matcher.group());
            ranges.add(new ProtectedRange(matcher.start(), matcher.end(), matcher.group()));
            lastEnd = matcher.end();
            index++;
        }

        protectedText.append(text, lastEnd, text.length());
        return new ProtectedText(protectedText.toString(), placeholders, ranges);
    }

    /**
     * 恢复被保护的文本。
     *
     * @param text 带占位符的文本
     * @param placeholders 占位符映射
     * @return 恢复后的文本
     */
    public static String restore(String text, Map<String, String> placeholders) {
        if (text == null || text.isEmpty() || placeholders == null || placeholders.isEmpty()) {
            return text;
        }

        String restored = text;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            restored = restored.replace(entry.getKey(), entry.getValue());
        }
        return restored;
    }

    /**
     * 判断候选切点是否落在受保护文本内部。
     *
     * @param ranges 受保护区间
     * @param cutPoint 候选切点
     * @return 命中的受保护区间，未命中时返回 null
     */
    public static ProtectedRange findRangeContainingCutPoint(List<ProtectedRange> ranges, int cutPoint) {
        if (ranges == null || ranges.isEmpty()) {
            return null;
        }
        for (ProtectedRange range : ranges) {
            if (range.containsCutPoint(cutPoint)) {
                return range;
            }
        }
        return null;
    }

    /**
     * 保护替换后的文本、占位符映射和原文区间。
     */
    @Getter
    @AllArgsConstructor
    public static final class ProtectedText {
        /**
         * 替换后的文本。
         */
        private final String text;

        /**
         * 占位符到原文的映射。
         */
        private final Map<String, String> placeholders;

        /**
         * 原文中的受保护文本区间。
         */
        private final List<ProtectedRange> ranges;

        /**
         * 恢复当前文本中的占位符。
         *
         * @return 恢复后的文本
         */
        public String restore() {
            return ProtectedTextReplacer.restore(text, placeholders);
        }
    }

    /**
     * 原文中的受保护文本区间。
     */
    @Getter
    @AllArgsConstructor
    public static final class ProtectedRange {
        /**
         * 区间起始位置，包含该位置。
         */
        private final int start;

        /**
         * 区间结束位置，不包含该位置。
         */
        private final int end;

        /**
         * 被保护的原始文本。
         */
        private final String text;

        /**
         * 判断切点是否落在受保护区间内部。
         *
         * @param index 候选切点
         * @return 是否位于受保护区间内部
         */
        public boolean containsCutPoint(int index) {
            return index > start && index < end;
        }
    }
}
