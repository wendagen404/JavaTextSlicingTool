package com.text_slicing_tool.splitter.impl;

import com.text_slicing_tool.pojo.DocumentContent;
import com.text_slicing_tool.pojo.SplitResult;
import com.text_slicing_tool.pojo.TextChunk;
import com.text_slicing_tool.splitter.TextSplitter;
import com.text_slicing_tool.splitter.support.ChunkWindowBuilder;
import com.text_slicing_tool.enums.SplitterType;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 标题切割器。
 */
public class TitleSplitter implements TextSplitter {
    private static final Pattern TITLE_PATTERN = Pattern.compile(
            "^\\s*(#{1,6}\\s+.+|[一二三四五六七八九十]+[、.．]\\s*.+|\\d+(?:\\.\\d+)*[、.．)]\\s*.+|（[一二三四五六七八九十]+）\\s*.+|\\([一二三四五六七八九十]+\\)\\s*.+)$"
    );

    private final String sourceId;

    /**
     * 创建默认标题切割器。
     */
    public TitleSplitter() {
        this(null);
    }

    /**
     * 创建标题切割器。
     *
     * @param sourceId 原始文档标识
     */
    public TitleSplitter(String sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * 按标题行切割文本，并将标题和标题下正文保持在同一个片段中。
     *
     * @return 切割结果
     */
    @Override
    public SplitResult split(DocumentContent documentContent) {
        long start = System.currentTimeMillis();
        String text = documentContent.getText();

        // 标题切割先得到章节块，再转换为统一分片结构。
        List<String> sections = splitByTitle(text);
        List<TextChunk> chunks = ChunkWindowBuilder.buildFromParts(
                sections,
                text,
                SplitterType.TITLE.getCode(),
                sourceId
        );

        return SplitResult.builder()
                .splitterType(SplitterType.TITLE.getCode())
                .chunks(chunks)
                .totalChunks(chunks.size())
                .durationMillis(System.currentTimeMillis() - start)
                .build();
    }

    /**
     * 将文本按标题行拆成章节块。
     *
     * @param sourceText 原始文本
     * @return 章节块列表
     */
    private List<String> splitByTitle(String sourceText) {
        List<String> sections = new ArrayList<>();
        if (sourceText == null || sourceText.isBlank()) {
            return sections;
        }

        StringBuilder current = new StringBuilder();
        String[] lines = sourceText.split("\\R");
        for (String line : lines) {
            // 遇到新标题时，先结束上一段章节内容。
            boolean title = isTitle(line);
            if (title && !current.isEmpty()) {
                sections.add(current.toString().trim());
                current.setLength(0);
            }
            current.append(line).append('\n');
        }

        // 收尾时补上最后一个章节块。
        if (!current.isEmpty()) {
            sections.add(current.toString().trim());
        }
        return sections;
    }

    /**
     * 判断当前行是否为标题。
     *
     * @param line 当前文本行
     * @return 是否为标题
     */
    private boolean isTitle(String line) {
        return line != null && TITLE_PATTERN.matcher(line).matches();
    }
}
