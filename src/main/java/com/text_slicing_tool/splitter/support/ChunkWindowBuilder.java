package com.text_slicing_tool.splitter.support;

import com.text_slicing_tool.pojo.TextChunk;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 分片窗口构建工具。
 */
public final class ChunkWindowBuilder {
    private ChunkWindowBuilder() {
    }

    /**
     * 按最大长度构建分片，并在目标位置附近优先寻找自然语义边界。
     *
     * @param text 原始文本
     * @param maxChunkSize 最大分片长度
     * @param overlapSize 重叠字符数
     * @param strategy 切割策略编码
     * @param sourceId 原始文档标识
     * @return 文本分片列表
     */
    public static List<TextChunk> buildByCharWindow(
            String text,
            int maxChunkSize,
            int overlapSize,
            String strategy,
            String sourceId
    ) {
        List<TextChunk> chunks = new ArrayList<>();
        if (text == null || text.isBlank()) {
            return chunks;
        }
        if (maxChunkSize <= 0) {
            throw new IllegalArgumentException("maxChunkSize must be greater than 0");
        }

        // 先识别 URL、邮箱、版本号、路径等受保护区间，后续切点不能落在这些区间内部。
        ProtectedTextReplacer.ProtectedText protectedText = ProtectedTextReplacer.protect(text);
        List<ProtectedTextReplacer.ProtectedRange> protectedRanges = protectedText.getRanges();

        int safeOverlap = Math.max(0, Math.min(overlapSize, maxChunkSize - 1));
        int start = 0;
        int chunkIndex = 0;
        while (start < text.length()) {
            int targetEnd = Math.min(start + maxChunkSize, text.length());

            // 先找自然边界，再根据受保护区间修正切点。
            int end = targetEnd == text.length()
                    ? targetEnd
                    : SentenceBoundaryDetector.findNearestBoundary(text, targetEnd, Math.max(20, maxChunkSize / 5));
            end = avoidProtectedCutPoint(end, start, targetEnd, text.length(), protectedRanges);
            if (end <= start) {
                end = targetEnd;
            }

            String content = text.substring(start, end).trim();
            if (!content.isEmpty()) {
                Map<String, Object> metadata = new LinkedHashMap<>();
                metadata.put("overlapSize", safeOverlap);
                metadata.put("originalStartOffset", start);
                metadata.put("originalEndOffset", end);

                chunks.add(TextChunk.builder()
                        .chunkId(UUID.randomUUID().toString())
                        .sourceId(sourceId)
                        .chunkIndex(chunkIndex++)
                        .content(content)
                        .startOffset(start)
                        .endOffset(end)
                        .strategy(strategy)
                        .metadata(metadata)
                        .build());
            }

            if (end >= text.length()) {
                break;
            }

            // overlap 回退后的新起点也不能落在 URL 等受保护文本内部。
            int nextStart = Math.max(end - safeOverlap, start + 1);
            start = avoidProtectedStartPoint(nextStart, end, protectedRanges);
        }
        return chunks;
    }

    /**
     * 将已经切好的文本块转换为标准分片。
     *
     * @param parts 文本块列表
     * @param originalText 原始文本
     * @param strategy 切割策略编码
     * @param sourceId 原始文档标识
     * @return 文本分片列表
     */
    public static List<TextChunk> buildFromParts(
            List<String> parts,
            String originalText,
            String strategy,
            String sourceId
    ) {
        List<TextChunk> chunks = new ArrayList<>();
        if (parts == null || parts.isEmpty()) {
            return chunks;
        }

        int searchFrom = 0;
        int chunkIndex = 0;
        String safeOriginal = originalText == null ? "" : originalText;
        for (String part : parts) {
            if (part == null || part.isBlank()) {
                continue;
            }

            String content = part.trim();
            int start = safeOriginal.indexOf(content, searchFrom);
            if (start < 0) {
                start = -1;
            }
            int end = start >= 0 ? start + content.length() : -1;
            if (end >= 0) {
                searchFrom = end;
            }

            chunks.add(TextChunk.builder()
                    .chunkId(UUID.randomUUID().toString())
                    .sourceId(sourceId)
                    .chunkIndex(chunkIndex++)
                    .content(content)
                    .startOffset(start)
                    .endOffset(end)
                    .strategy(strategy)
                    .metadata(new LinkedHashMap<>())
                    .build());
        }
        return chunks;
    }

    private static int avoidProtectedCutPoint(
            int cutPoint,
            int start,
            int targetEnd,
            int textLength,
            List<ProtectedTextReplacer.ProtectedRange> protectedRanges
    ) {
        ProtectedTextReplacer.ProtectedRange range =
                ProtectedTextReplacer.findRangeContainingCutPoint(protectedRanges, cutPoint);
        if (range == null) {
            return cutPoint;
        }

        // 如果 URL 等受保护文本本身不超过窗口，优先把切点挪到受保护文本之后。
        if (range.getEnd() - start <= targetEnd - start || range.getEnd() <= textLength) {
            return range.getEnd();
        }

        // 受保护文本过长时，退到受保护文本之前，避免把 URL 前后的普通文本混进同一强切片。
        return range.getStart() > start ? range.getStart() : targetEnd;
    }

    private static int avoidProtectedStartPoint(
            int startPoint,
            int previousEnd,
            List<ProtectedTextReplacer.ProtectedRange> protectedRanges
    ) {
        ProtectedTextReplacer.ProtectedRange range =
                ProtectedTextReplacer.findRangeContainingCutPoint(protectedRanges, startPoint);
        if (range == null) {
            return startPoint;
        }

        // 新分片起点落在受保护文本内部时，回到受保护文本起点，保证 URL 等内容完整。
        return Math.min(range.getStart(), previousEnd);
    }
}
