package com.text_slicing_tool.ai.config;

import com.text_slicing_tool.enums.AiFramework;
import com.text_slicing_tool.enums.AiType;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.Duration;

/**
 * AI 模块配置属性。
 *
 * <p>在 application.yml 中通过 {@code text-slicing.ai} 前缀配置：
 * <pre>
 * text-slicing:
 *   ai:
 *     framework: spring-ai      # spring-ai | langchain4j
 *     type: openai              # openai | qwen | glm | deepseek ...
 *     base-url: https://api.openai.com
 *     api-key: sk-xxx
 *     model: gpt-4o-mini
 *     temperature: 0.4
 *     max-tokens: 2048
 * </pre>
 *
 * <p>{@code framework} 与 {@code type} 均为枚举，Spring Boot 宽松绑定支持
 * kebab-case（如 {@code spring-ai} 绑定到 {@link AiFramework#SPRING_AI}）。
 */
@Data
@ConfigurationProperties(prefix = "text-slicing.ai")
public class AiProperties {

    /**
     * 使用的 AI 调用框架，取值：spring-ai、langchain4j。
     */
    private AiFramework framework = AiFramework.SPRING_AI;

    /**
     * 模型厂商类型，用于策略路由与元数据标记。
     */
    private AiType type = AiType.OPENAI;

    /**
     * OpenAI 兼容接口的 Base URL。不同厂商使用各自的兼容端点。
     */
    private String baseUrl;

    /**
     * API Key。
     */
    private String apiKey;

    /**
     * 模型名称，例如 gpt-4o-mini、qwen-plus、glm-4 等。
     */
    private String model;

    /**
     * 采样温度。
     */
    private Double temperature;

    /**
     * 单次生成的最大 token 数。
     */
    private Integer maxTokens;

    /**
     * 请求超时时间。
     */
    private Duration timeout;
}
