package com.text_slicing_tool.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 文档提取后的正文内容。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentContent {
    /**
     * 原始文档的唯一标识，例如文件路径、文件 ID 或业务 ID。
     */
    private String sourceId;

    /**
     * 原始文档类型，例如 pdf、markdown、txt、image。
     */
    private String sourceType;

    /**
     * 从文档中提取出来的纯文本内容。
     */
    private String text;

    /**
     * 文档级元数据，例如文件名、页码范围、图片信息、解析来源等。
     */
    @Builder.Default
    private Map<String, Object> metadata = new LinkedHashMap<>();
}
