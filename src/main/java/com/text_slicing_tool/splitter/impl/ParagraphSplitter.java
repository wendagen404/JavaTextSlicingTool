package com.text_slicing_tool.splitter.impl;

import com.text_slicing_tool.pojo.DocumentContent;
import com.text_slicing_tool.pojo.SplitResult;
import com.text_slicing_tool.pojo.TextChunk;
import com.text_slicing_tool.splitter.TextSplitter;
import com.text_slicing_tool.splitter.support.ChunkWindowBuilder;
import com.text_slicing_tool.enums.SplitterType;

import java.util.Arrays;
import java.util.List;

/**
 * 自然段切割器。
 */
public class ParagraphSplitter implements TextSplitter {
    private final String sourceId;

    /**
     * 创建默认自然段切割器。
     */
    public ParagraphSplitter() {
        this(null);
    }


    /**
     * 创建自然段切割器。
     *
     * @param sourceId 原始文档标识
     */
    public ParagraphSplitter(String sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * 按连续空行识别自然段并切割文本。
     *
     * @return 切割结果
     */
    @Override
    public SplitResult split(DocumentContent documentContent) {
        long start = System.currentTimeMillis();
        String text = documentContent.getText();

        // 连续空行通常是 PDF/Markdown 提取文本中的自然段边界。
        List<String> paragraphs = text == null || text.isBlank()
                ? List.of()
                : Arrays.stream(text.split("(\\r?\\n\\s*\\r?\\n)+"))
                .map(String::trim)
                .filter(paragraph -> !paragraph.isEmpty())
                .toList();

        // 将自然段统一包装为 TextChunk。
        List<TextChunk> chunks = ChunkWindowBuilder.buildFromParts(
                paragraphs,
                text,
                SplitterType.PARAGRAPH.getCode(),
                sourceId
        );

        return SplitResult.builder()
                .splitterType(SplitterType.PARAGRAPH.getCode())
                .chunks(chunks)
                .totalChunks(chunks.size())
                .durationMillis(System.currentTimeMillis() - start)
                .build();
    }
}
