package com.text_slicing_tool.extractor.impl;

import com.text_slicing_tool.ai.LLMContext;
import com.text_slicing_tool.enums.AiFramework;
import com.text_slicing_tool.enums.AiType;
import com.text_slicing_tool.extractor.DocumentExtractor;
import com.text_slicing_tool.pojo.ExtractResult;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

/**
 * AI 文本提取器。
 */
public class AiDocumentExtractor implements DocumentExtractor {
    private final LLMContext llmContext;
    private final AiFramework aiFramework;
    private final AiType aiType;

    /**
     * 创建默认 AI 文本提取器。
     *
     * @param llmContext AI 模型策略上下文
     */
    public AiDocumentExtractor(LLMContext llmContext) {
        this(llmContext, AiFramework.SPRING_AI, AiType.OPENAI);
    }

    /**
     * 创建指定模型厂商的 Spring AI 文本提取器。
     *
     * @param llmContext AI 模型策略上下文
     * @param aiType 模型厂商类型
     */
    public AiDocumentExtractor(LLMContext llmContext, AiType aiType) {
        this(llmContext, AiFramework.SPRING_AI, aiType);
    }

    /**
     * 创建指定调用框架和模型厂商的 AI 文本提取器。
     *
     * @param llmContext AI 模型策略上下文
     * @param aiFramework AI 调用框架
     * @param aiType 模型厂商类型
     */
    public AiDocumentExtractor(LLMContext llmContext, AiFramework aiFramework, AiType aiType) {
        this.llmContext = llmContext;
        this.aiFramework = aiFramework;
        this.aiType = aiType;
    }

    /**
     * 使用指定 AI 模型策略提取文档文本。
     *
     * @param resource 待解析资源
     * @return AI 提取结果
     */
    @Override
    public ExtractResult extract(ClassPathResource resource) {
        // 从上下文中选择框架和厂商对应的策略，再委托具体模型完成文本提取。
        return llmContext.getLlmModel(aiFramework, aiType).doExtractor(resource);
    }
}
