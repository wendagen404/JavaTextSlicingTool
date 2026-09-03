package com.text_slicing_tool.splitter.impl;

import com.text_slicing_tool.pojo.DocumentContent;
import com.text_slicing_tool.pojo.SplitResult;
import com.text_slicing_tool.pojo.TextChunk;
import com.text_slicing_tool.splitter.TextSplitter;
import com.text_slicing_tool.splitter.support.ChunkWindowBuilder;
import com.text_slicing_tool.splitter.support.ProtectedTextReplacer;
import com.text_slicing_tool.enums.SplitterType;
import com.text_slicing_tool.utils.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 自定义符号切割器。
 */
public class CustomSymbolSplitter implements TextSplitter {
    private final String sourceId;
    private final String charVal;

    /**
     * 创建默认自定义符号切割器。
     */
    public CustomSymbolSplitter() {
        this(null, "\\R{2,}");
    }

    /**
     * 创建自定义符号切割器。
     *
     * @param charVal 分隔符
     */
    public CustomSymbolSplitter(String charVal) {
        this(null, charVal);
    }

    /**
     * 创建自定义符号切割器。
     *
     * @param sourceId 原始文档标识
     * @param charVal  分隔符
     */
    public CustomSymbolSplitter(String sourceId, String charVal) {
        this.sourceId = sourceId;
        this.charVal = charVal;
    }

    /**
     * 按自定义分隔符切割文本，并保护 URL、邮箱、版本号、文件路径等特殊片段。
     *
     * @return 切割结果
     */
    @Override
    public SplitResult split(DocumentContent documentContent) {
        long start = System.currentTimeMillis();
        String text = documentContent.getText();

        // 切割前先保护 URL、邮箱、版本号、路径，避免分隔符误切。
        ProtectedTextReplacer.ProtectedText protectedText = ProtectedTextReplacer.protect(text);
        String protectedStr = protectedText.getText();

        // charVal：分隔字符串，；默认使用两个及以上换行 "\n\n"
        String separator = (charVal == null || charVal.isBlank()) ? "\n\n" : charVal;

        List<String> parts;
        if (protectedStr.isBlank()) {
            parts = List.of();
        } else {
            String[] splitArray = StringUtils.splitByWholeSeparator(protectedStr, separator);
            parts = Arrays.stream(splitArray)
                    .map(part -> ProtectedTextReplacer.restore(part, protectedText.getPlaceholders()))
                    .map(String::trim)
                    .filter(part -> !part.isEmpty())
                    .toList();
        }

        // 将切割结果统一包装为 TextChunk。
        List<TextChunk> chunks = ChunkWindowBuilder.buildFromParts(
                parts,
                text,
                SplitterType.CUSTOM_SYMBOL.getCode(),
                sourceId
        );

        return SplitResult.builder()
                .splitterType(SplitterType.CUSTOM_SYMBOL.getCode())
                .chunks(chunks)
                .totalChunks(chunks.size())
                .durationMillis(System.currentTimeMillis() - start)
                .build();
    }

    /**
     * 使用普通文本分隔符创建切割器，内部会自动转义为正则安全文本。
     *
     * @param text      待切割文本
     * @param separator 普通文本分隔符
     * @return 自定义符号切割器
     */
    public static CustomSymbolSplitter byLiteralSeparator(String text, String separator) {
        return new CustomSymbolSplitter(null, Pattern.quote(separator));
    }
}
