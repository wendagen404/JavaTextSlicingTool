package com.text_slicing_tool.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;

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

    /**
     * 转换为 LangChain4j 的 TextSegment，适合直接进入 EmbeddingModel 或 EmbeddingStoreIngestor。
     *
     * @return LangChain4j 文本片段
     */
    public TextSegment toLangChain4jTextSegment() {
        return TextSegment.from(content, Metadata.from(toLangChain4jMetadata()));
    }

    /**
     * 将当前分片元数据转换为 LangChain4j 支持的 Metadata。
     *
     * @return LangChain4j 可接受的元数据 Map
     */
    public Map<String, Object> toLangChain4jMetadata() {
        Map<String, Object> langChain4jMetadata = new LinkedHashMap<>();
        putIfNotNull(langChain4jMetadata, "chunk_id", chunkId);
        putIfNotNull(langChain4jMetadata, "source_id", sourceId);
        putIfNotNull(langChain4jMetadata, "index", chunkIndex);
        putIfNotNull(langChain4jMetadata, "start_offset", startOffset);
        putIfNotNull(langChain4jMetadata, "end_offset", endOffset);
        putIfNotNull(langChain4jMetadata, "strategy", strategy);

        // LangChain4j Metadata 只支持基础标量类型，复杂对象统一转成字符串保存。
        if (metadata != null) {
            metadata.forEach((key, value) -> putIfNotNull(langChain4jMetadata, key, normalizeMetadataValue(value)));
        }
        return langChain4jMetadata;
    }

    /**
     * 从 LangChain4j TextSegment 转回工具库的 TextChunk。
     *
     * @param textSegment LangChain4j 文本片段
     * @return 工具库文本分片
     */
    public static TextChunk fromLangChain4jTextSegment(TextSegment textSegment) {
        Map<String, Object> langChain4jMetadata = textSegment.metadata().toMap();
        return TextChunk.builder()
                .chunkId(asString(langChain4jMetadata.get("chunk_id")))
                .sourceId(asString(langChain4jMetadata.get("source_id")))
                .chunkIndex(asInteger(langChain4jMetadata.get("index")))
                .content(textSegment.text())
                .startOffset(asInteger(langChain4jMetadata.get("start_offset")))
                .endOffset(asInteger(langChain4jMetadata.get("end_offset")))
                .strategy(asString(langChain4jMetadata.get("strategy")))
                .metadata(new LinkedHashMap<>(langChain4jMetadata))
                .build();
    }

    private static void putIfNotNull(Map<String, Object> target, String key, Object value) {
        if (value != null) {
            target.put(key, value);
        }
    }

    private static Object normalizeMetadataValue(Object value) {
        if (value instanceof String || value instanceof Integer || value instanceof Long
                || value instanceof Float || value instanceof Double) {
            return value;
        }
        return String.valueOf(value);
    }

    private static String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private static Integer asInteger(Object value) {
        if (value instanceof Integer integer) {
            return integer;
        }
        if (value instanceof Number number) {
            return number.intValue();
        }
        if (value == null) {
            return null;
        }
        return Integer.parseInt(String.valueOf(value));
    }
}
