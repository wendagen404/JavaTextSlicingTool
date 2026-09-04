package com.text_slicing_tool.ai.config;

import com.text_slicing_tool.ai.model.LlmModel;
import com.text_slicing_tool.ai.model.SpringAiOpenAiModel;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

/**
 * Spring AI 框架自动装配。
 *
 * <p>当 {@code text-slicing.ai.framework=spring-ai} 时生效，依据 yml 中的 base-url / api-key / model
 * 构建 {@link OpenAiChatModel} 与 {@link ChatClient}，并按 {@code text-slicing.ai.type}
 * 注入 {@link SpringAiOpenAiModel} 策略 Bean。
 *
 * <p>与 {@link LangChain4jAutoConfiguration} 对称：type 作为厂商元数据，底层统一走 OpenAI 兼容接口，
 * 适配 OpenAI / 通义千问 / 智谱 GLM / DeepSeek 等任意 OpenAI 兼容厂商。
 */
@AutoConfiguration
@EnableConfigurationProperties(AiProperties.class)
@ConditionalOnProperty(prefix = "text-slicing.ai", name = "framework", havingValue = "spring-ai", matchIfMissing = true)
@ConditionalOnClass(ChatClient.class)
public class SpringAiAutoConfiguration {

    /**
     * 构建 Spring AI OpenAI 兼容聊天模型。
     *
     * @param properties AI 配置
     * @return OpenAiChatModel 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public OpenAiChatModel textSlicingOpenAiChatModel(AiProperties properties) {
        OpenAiChatOptions.Builder options = OpenAiChatOptions.builder()
                .apiKey(properties.getApiKey())
                .model(properties.getModel());
        if (properties.getBaseUrl() != null && !properties.getBaseUrl().isBlank()) {
            options.baseUrl(properties.getBaseUrl());
        }
        if (properties.getTemperature() != null) {
            options.temperature(properties.getTemperature());
        }
        if (properties.getMaxTokens() != null) {
            options.maxTokens(properties.getMaxTokens());
        }
        return OpenAiChatModel.builder()
                .options(options.build())
                .build();
    }

    /**
     * 基于 {@link OpenAiChatModel} 构建 {@link ChatClient}。
     *
     * @param chatModel OpenAI 聊天模型
     * @return ChatClient 实例
     */
    @Bean
    @ConditionalOnMissingBean
    public ChatClient textSlicingChatClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }

    /**
     * 注入 Spring AI 模型策略，厂商类型由配置决定（作为策略路由与元数据标记）。
     *
     * @param chatClient ChatClient 实例
     * @param properties AI 配置
     * @return LlmModel 策略实现
     */
    @Bean
    @ConditionalOnMissingBean(LlmModel.class)
    public LlmModel textSlicingSpringAiLlmModel(ChatClient chatClient, AiProperties properties) {
        return new SpringAiOpenAiModel(chatClient, properties.getType());
    }
}
