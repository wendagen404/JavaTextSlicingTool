package com.text_slicing_tool.pojo;

import com.text_slicing_tool.enums.AiFramework;
import com.text_slicing_tool.enums.AiType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * AI 模型调用后的统一文本结果，避免业务返回值直接依赖 Spring AI 或 LangChain4j 的响应类型。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiMessageResult {
    /**
     * 调用 AI 使用的 Java 框架。
     */
    private AiFramework framework;

    /**
     * 调用 AI 使用的模型厂商。
     */
    private AiType aiType;

    /**
     * 模型返回的文本内容。
     */
    private String content;

    /**
     * 模型调用耗时，单位毫秒。
     */
    private Long durationMillis;

    /**
     * 模型调用过程中的附加信息，例如模型名、token 用量、降级状态等。
     */
    @Builder.Default
    private Map<String, Object> metadata = new LinkedHashMap<>();
}
