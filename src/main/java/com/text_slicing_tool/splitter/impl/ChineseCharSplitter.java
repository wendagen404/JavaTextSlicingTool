package com.text_slicing_tool.splitter.impl;

import com.text_slicing_tool.pojo.DocumentContent;
import com.text_slicing_tool.pojo.SplitResult;
import com.text_slicing_tool.pojo.TextChunk;
import com.text_slicing_tool.splitter.TextSplitter;
import com.text_slicing_tool.splitter.support.ChunkWindowBuilder;
import com.text_slicing_tool.splitter.support.SentenceBoundaryDetector;
import com.text_slicing_tool.enums.SplitterType;

import java.util.List;

/**
 * 中文句子切割器。
 */
public class ChineseCharSplitter implements TextSplitter {
    private final String sourceId;

    /**
     * 创建默认中文句子切割器。
     */
    public ChineseCharSplitter() {
        this(null);
    }

    /**
     * 创建中文句子切割器。
     *
     * @param sourceId 原始文档标识
     */
    public ChineseCharSplitter(String sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * 按中文和英文句末标点切割文本。
     *
     * @return 切割结果
     */
    @Override
    public SplitResult split(DocumentContent documentContent) {
        long start = System.currentTimeMillis();
        String text = documentContent.getText();
        // 先识别句子边界，再统一转换为 TextChunk。
        List<String> sentences = SentenceBoundaryDetector.splitSentences(text);
        List<TextChunk> chunks = ChunkWindowBuilder.buildFromParts(
                sentences,
                text,
                SplitterType.CHINESE_CHAR.getCode(),
                sourceId
        );

        return SplitResult.builder()
                .splitterType(SplitterType.CHINESE_CHAR.getCode())
                .chunks(chunks)
                .totalChunks(chunks.size())
                .durationMillis(System.currentTimeMillis() - start)
                .build();
    }
}
