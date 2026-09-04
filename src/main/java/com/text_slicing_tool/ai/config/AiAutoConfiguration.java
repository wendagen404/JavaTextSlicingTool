package com.text_slicing_tool.ai.config;

import com.text_slicing_tool.ai.LLMContext;
import com.text_slicing_tool.ai.model.LlmModel;
import com.text_slicing_tool.extractor.DocumentExtractor;
import com.text_slicing_tool.splitter.TextSplitter;
import com.text_slicing_tool.splitter.impl.AiSplitter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.autoconfigure.AutoConfiguration;

import java.util.List;

/**
 * AI 模块自动装配入口。
 *
 * <p>注册策略上下文 {@link LLMContext} 以及基于配置默认的 AI 切割器/提取器。
 * 具体模型 Bean 由 {@link SpringAiAutoConfiguration} 或 {@link LangChain4jAutoConfiguration}
 * 根据 {@code text-slicing.ai.framework} 配置条件装配。
 *
 * <p>如需完全关闭自动装配，可使用 {@code spring.autoconfigure.exclude} 排除本类。
 */
@AutoConfiguration
@EnableConfigurationProperties(AiProperties.class)
public class AiAutoConfiguration {

    /**
     * 注册 AI 模型策略上下文，自动收集所有 {@link LlmModel} 实现。
     *
     * @param models 所有 LlmModel 策略实现
     * @return 策略上下文
     */
    @Bean
    @ConditionalOnMissingBean
    public LLMContext llmContext(List<LlmModel> models) {
        return new LLMContext(models);
    }
}
