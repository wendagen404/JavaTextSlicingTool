package com.text_slicing_tool.splitter.impl;

import com.text_slicing_tool.pojo.DocumentContent;
import com.text_slicing_tool.pojo.SplitResult;
import com.text_slicing_tool.pojo.TextChunk;
import com.text_slicing_tool.splitter.TextSplitter;
import com.text_slicing_tool.splitter.support.ChunkWindowBuilder;
import com.text_slicing_tool.enums.SplitterType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 固定字符切割器。
 */
public class FixeCharSplitter implements TextSplitter {
    private final String sourceId;
    private final int fixedCharLen;
    private final int overlapSize;

    /**
     * 创建默认固定字符切割器。
     */
    public FixeCharSplitter() {
        this(null, 500, 0);
    }

    /**
     * 创建固定字符切割器。
     *
     * @param fixedCharLen 最大分片字符数
     */
    public FixeCharSplitter(int fixedCharLen) {
        this(null, fixedCharLen, 0);
    }

    /**
     * 创建固定字符切割器。
     *
     * @param sourceId     原始文档标识
     * @param fixedCharLen 最大分片字符数
     * @param overlapSize  分片重叠字符数
     */
    public FixeCharSplitter(String sourceId, int fixedCharLen, int overlapSize) {
        this.sourceId = sourceId;
        this.fixedCharLen = fixedCharLen;
        this.overlapSize = overlapSize;
    }

    /**
     * 按固定字符长度切割文本，并在目标长度附近优先寻找自然语义边界。
     *
     * @return 切割结果
     */
    @Override
    public SplitResult split(DocumentContent documentContent) {
        long start = System.currentTimeMillis();
        String text = documentContent.getText();

        // 使用窗口构建器处理长度、重叠和自然边界回退。
        List<TextChunk> chunks = ChunkWindowBuilder.buildByCharWindow(
                text,
                fixedCharLen,
                overlapSize,
                SplitterType.FIXED_CHAR.getCode(),
                sourceId
        );

        // 记录本次切割参数，方便调用方排查切片效果。
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("maxChunkSize", fixedCharLen);
        metadata.put("overlapSize", overlapSize);

        return SplitResult.builder()
                .splitterType(SplitterType.FIXED_CHAR.getCode())
                .chunks(chunks)
                .totalChunks(chunks.size())
                .durationMillis(System.currentTimeMillis() - start)
                .metadata(metadata)
                .build();
    }
}
