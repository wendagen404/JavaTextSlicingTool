package com.text_slicing_tool.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 文本切割后的单个分片。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TextChunk {
    /**
     * 当前分片的唯一标识。
     */
    private String chunkId;

    /**
     * 当前分片所属的原始文档标识。
     */
    private String sourceId;

    /**
     * 当前分片在原始文档切片结果中的顺序。
     */
    private Integer chunkIndex;

    /**
     * 当前分片的文本内容。
     */
    private String content;

    /**
     * 当前分片在原始文本中的起始字符位置。
     */
    private Integer startOffset;

    /**
     * 当前分片在原始文本中的结束字符位置。
     */
    private Integer endOffset;

    /**
     * 生成当前分片所使用的切割策略。
     */
    private String strategy;

    /**
     * 分片级元数据，例如页码、标题路径、是否重叠、是否来自图片等。
     */
    @Builder.Default
    private Map<String, Object> metadata = new LinkedHashMap<>();
}
