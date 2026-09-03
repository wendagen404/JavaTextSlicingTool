package com.text_slicing_tool.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 文本提取操作的领域结果。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExtractResult {
    /**
     * 提取得到的文档正文内容。
     */
    private DocumentContent content;

    /**
     * 本次使用的提取器类型，例如 pdfbox、ocr、ai、markdown。
     */
    private String extractorType;

    /**
     * 本次提取耗时，单位毫秒。
     */
    private Long durationMillis;

    /**
     * 提取过程的附加信息，例如 OCR 语言、AI 模型、页数、图片数量等。
     */
    @Builder.Default
    private Map<String, Object> metadata = new LinkedHashMap<>();

    /**
     * 创建一个空的提取结果，适合具体提取器尚未实现时作为占位返回。
     *
     * @param extractorType 提取器类型
     * @param sourceType 原始文档类型
     * @return 空提取结果
     */
    public static ExtractResult empty(String extractorType, String sourceType) {
        return ExtractResult.builder()
                .extractorType(extractorType)
                .content(DocumentContent.builder().sourceType(sourceType).build())
                .build();
    }
}
