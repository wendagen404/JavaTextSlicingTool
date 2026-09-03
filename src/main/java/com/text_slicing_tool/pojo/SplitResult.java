package com.text_slicing_tool.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
                .build();
    }
}
