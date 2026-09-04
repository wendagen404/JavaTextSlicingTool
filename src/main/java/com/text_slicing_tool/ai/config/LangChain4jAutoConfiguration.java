package com.text_slicing_tool.ai.config;

import com.text_slicing_tool.ai.model.LangChain4jOpenAiModel;
import com.text_slicing_tool.ai.model.LlmModel;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * LangChain4j 框架自动装配。
 *
 * <p>当 {@code text-slicing.ai.framework=langchain4j} 时生效，依据 yml 中的 base-url / api-key / model
 * 构建 LangChain4j 的 {@link OpenAiChatModel}，并按 {@code text-slicing.ai.type}
 * 注入对应的 {@link LlmModel} 策略 Bean。
 */
@AutoConfiguration
@EnableConfigurationProperties(AiProperties.class)
@ConditionalOnProperty(prefix = "text-slicing.ai", name = "framework", havingValue = "langchain4j")
@ConditionalOnClass(ChatModel.class)
public class LangChain4jAutoConfiguration {

    /**
     * 构建 LangChain4j OpenAI 兼容聊天模型。
     *
     * @param properties AI 配置
     * @return OpenAiChatModel 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenAiChatModel textSlicingLangChain4jChatModel(AiProperties properties) {
        var builder = OpenAiChatModel.builder()
                .apiKey(properties.getApiKey())
                .modelName(properties.getModel());
        if (properties.getBaseUrl() != null && !properties.getBaseUrl().isBlank()) {
            builder.baseUrl(properties.getBaseUrl());
        }
        if (properties.getTemperature() != null) {
            builder.temperature(properties.getTemperature());
        }
        if (properties.getMaxTokens() != null) {
            builder.maxTokens(properties.getMaxTokens());
        }
        return builder.build();
    }

    /**
     * 注入 LangChain4j 模型策略，厂商类型由配置决定（作为元数据标记）。
     *
     * @param chatModel LangChain4j 聊天模型
     * @param properties AI 配置
     * @return LlmModel 策略实现
     */
    @Bean
    @ConditionalOnMissingBean(LlmModel.class)
    public LlmModel textSlicingLangChain4jLlmModel(ChatModel chatModel, AiProperties properties) {
        return new LangChain4jOpenAiModel(chatModel, properties.getType());
    }
}
