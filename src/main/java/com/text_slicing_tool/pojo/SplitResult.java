package com.text_slicing_tool.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 文本切割操作的领域结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SplitResult {
    /**
     * 切割后得到的分片列表。
     */
    private List<TextChunk> chunks;

    /**
     * 本次使用的切割器类型，例如 fixed-char、paragraph、title、semantic、ai。
     */
    private String splitterType;

    /**
     * 分片总数。
     */
    private Integer totalChunks;

    /**
     * 本次切割耗时，单位毫秒。
     */
    private Long durationMillis;

    /**
     * 切割过程的附加信息，例如最大长度、重叠长度、保护规则等。
     */
    @Builder.Default
    private Map<String, Object> metadata = new LinkedHashMap<>();

    /**
     * 创建一个空的切割结果，适合具体切割器尚未实现时作为占位返回。
     *
     * @param splitterType 切割器类型
     * @return 空切割结果
     */
    public static SplitResult empty(String splitterType) {
        return SplitResult.builder()
                .splitterType(splitterType)
                .chunks(Collections.emptyList())
                .totalChunks(0)
                .durationMillis(0L)
                .build();
    }

    /**
     * 转换为 LangChain4j 的 TextSegment 列表，适合直接做向量化和入库。
     *
     * @return LangChain4j 文本片段列表
     */
    public List<TextSegment> toLangChain4jTextSegments() {
        if (chunks == null || chunks.isEmpty()) {
            return Collections.emptyList();
        }
        return chunks.stream()
                .map(TextChunk::toLangChain4jTextSegment)
                .toList();
    }

    /**
     * 转换为 LangChain4j 的 Document 列表，每个切片对应一个 Document。
     *
     * @return LangChain4j 文档列表
     */
    public List<Document> toLangChain4jDocuments() {
        if (chunks == null || chunks.isEmpty()) {
            return Collections.emptyList();
        }
        return chunks.stream()
                .map(chunk -> Document.from(chunk.getContent(), Metadata.from(chunk.toLangChain4jMetadata())))
                .toList();
    }

    /**
     * 从 LangChain4j TextSegment 列表创建工具库切割结果。
     *
     * @param textSegments LangChain4j 文本片段列表
     * @param splitterType 切割器类型
     * @return 工具库切割结果
     */
    public static SplitResult fromLangChain4jTextSegments(List<TextSegment> textSegments, String splitterType) {
        if (textSegments == null || textSegments.isEmpty()) {
            return SplitResult.empty(splitterType);
        }
        List<TextChunk> chunks = textSegments.stream()
                .map(TextChunk::fromLangChain4jTextSegment)
                .toList();
        return SplitResult.builder()
                .splitterType(splitterType)
                .chunks(chunks)
                .totalChunks(chunks.size())
                .durationMillis(0L)
                .build();
    }

    /**
     * 从 LangChain4j Document 列表创建工具库切割结果。
     *
     * @param documents LangChain4j 文档列表
     * @param splitterType 切割器类型
     * @return 工具库切割结果
     */
    public static SplitResult fromLangChain4jDocuments(List<Document> documents, String splitterType) {
        if (documents == null || documents.isEmpty()) {
            return SplitResult.empty(splitterType);
        }
        List<TextChunk> chunks = documents.stream()
                .map(Document::toTextSegment)
                .map(TextChunk::fromLangChain4jTextSegment)
                .toList();
        return SplitResult.builder()
                .splitterType(splitterType)
                .chunks(chunks)
                .totalChunks(chunks.size())
                .durationMillis(0L)
                .build();
    }
}
