package com.text_slicing_tool.splitter.impl;

import com.text_slicing_tool.pojo.DocumentContent;
import com.text_slicing_tool.pojo.SplitResult;
import com.text_slicing_tool.pojo.TextChunk;
import com.text_slicing_tool.splitter.TextSplitter;
import com.text_slicing_tool.splitter.support.ChunkWindowBuilder;
import com.text_slicing_tool.splitter.support.SentenceBoundaryDetector;
import com.text_slicing_tool.enums.SplitterType;

import java.util.ArrayList;
import java.util.List;

/**
 * 语义切割器。
 */
public class SemanticSplitter implements TextSplitter {
    private final String sourceId;
    private final int maxChunkSize;
    private final int overlapSize;

    /**
     * 创建默认语义切割器。
     */
    public SemanticSplitter() {
        this( null, 800, 80);
    }

    /**
     * 创建语义切割器。
     *
     * @param sourceId 原始文档标识
     * @param maxChunkSize 最大分片字符数
     * @param overlapSize 分片重叠字符数
     */
    public SemanticSplitter( String sourceId, int maxChunkSize, int overlapSize) {
        this.sourceId = sourceId;
        this.maxChunkSize = maxChunkSize;
        this.overlapSize = overlapSize;
    }

    /**
     * 按启发式语义规则切割文本。
     *
     * @return 切割结果
     */
    @Override
    public SplitResult split(DocumentContent documentContent) {
        long start = System.currentTimeMillis();
        String text = documentContent.getText();

        // 先按段落聚合语义块，超长段落再按字符窗口兜底。
        List<String> semanticParts = buildSemanticParts(text);
        List<TextChunk> chunks = ChunkWindowBuilder.buildFromParts(
                semanticParts,
                text,
                SplitterType.SEMANTIC.getCode(),
                sourceId
        );

        return SplitResult.builder()
                .splitterType(SplitterType.SEMANTIC.getCode())
                .chunks(chunks)
                .totalChunks(chunks.size())
                .durationMillis(System.currentTimeMillis() - start)
                .build();
    }

    /**
     * 构建语义文本块。
     *
     * @param sourceText 原始文本
     * @return 语义文本块列表
     */
    private List<String> buildSemanticParts(String sourceText) {
        List<String> parts = new ArrayList<>();
        if (sourceText == null || sourceText.isBlank()) {
            return parts;
        }

        String[] paragraphs = sourceText.split("(\\r?\\n\\s*\\r?\\n)+");
        StringBuilder current = new StringBuilder();
        for (String paragraph : paragraphs) {
            String trimmed = paragraph.trim();
            if (trimmed.isEmpty()) {
                continue;
            }

            // 超长段落无法直接保留，使用固定窗口做语义边界兜底。
            if (trimmed.length() > maxChunkSize) {
                flushCurrent(parts, current);
                ChunkWindowBuilder.buildByCharWindow(
                                trimmed,
                                maxChunkSize,
                                overlapSize,
                                SplitterType.SEMANTIC.getCode(),
                                sourceId
                        )
                        .stream()
                        .map(TextChunk::getContent)
                        .forEach(parts::add);
                continue;
            }

            // 当前语义块即将超过最大长度时，先完成当前块。
            if (current.length() + trimmed.length() > maxChunkSize) {
                flushCurrent(parts, current);
            }

            // 相邻段落之间保留空行，避免语义结构被压扁。
            if (!current.isEmpty()) {
                current.append("\n\n");
            }
            current.append(trimmed);

            // 命中主题结束提示词时，提前收束当前语义块。
            if (isTopicBoundary(trimmed)) {
                flushCurrent(parts, current);
            }
        }

        // 收尾时补上最后一个语义块。
        flushCurrent(parts, current);
        return parts;
    }

    /**
     * 判断段落是否出现主题边界提示。
     *
     * @param paragraph 段落文本
     * @return 是否可作为主题边界
     */
    private boolean isTopicBoundary(String paragraph) {
        List<String> sentences = SentenceBoundaryDetector.splitSentences(paragraph);
        if (sentences.size() <= 1) {
            return false;
        }
        String lastSentence = sentences.get(sentences.size() - 1);
        return lastSentence.startsWith("因此")
                || lastSentence.startsWith("所以")
                || lastSentence.startsWith("总结")
                || lastSentence.startsWith("综上")
                || lastSentence.contains("如下")
                || lastSentence.contains("包括");
    }

    /**
     * 将当前缓冲区写入结果列表并清空。
     *
     * @param parts 语义文本块列表
     * @param current 当前缓冲区
     */
    private void flushCurrent(List<String> parts, StringBuilder current) {
        if (!current.isEmpty()) {
            parts.add(current.toString().trim());
            current.setLength(0);
        }
    }
}
