package com.text_slicing_tool.splitter.impl;

import com.text_slicing_tool.ai.LLMContext;
import com.text_slicing_tool.enums.AiFramework;
import com.text_slicing_tool.enums.AiType;
import com.text_slicing_tool.pojo.DocumentContent;
import com.text_slicing_tool.pojo.SplitResult;
import com.text_slicing_tool.splitter.TextSplitter;
import org.springframework.stereotype.Component;

/**
 * AI 切割器。
 */
public class AiSplitter implements TextSplitter {
    private final LLMContext llmContext;
    private final AiFramework aiFramework;
    private final AiType aiType;

    /**
     * 创建默认 AI 切割器。
     *
     * @param llmContext AI 模型策略上下文
     */
    public AiSplitter(LLMContext llmContext) {
        this(llmContext, AiFramework.SPRING_AI, AiType.OPENAI);
    }

    /**
     * 创建指定模型厂商的 Spring AI 切割器。
     *
     * @param llmContext AI 模型策略上下文
     * @param aiType 模型厂商类型
     */
    public AiSplitter(LLMContext llmContext, AiType aiType) {
        this(llmContext, AiFramework.SPRING_AI, aiType);
    }

    /**
     * 创建指定调用框架和模型厂商的 AI 切割器。
     *
     * @param llmContext AI 模型策略上下文
     * @param aiFramework AI 调用框架
     * @param aiType 模型厂商类型
     */
    public AiSplitter(LLMContext llmContext, AiFramework aiFramework, AiType aiType) {
        this.llmContext = llmContext;
        this.aiFramework = aiFramework;
        this.aiType = aiType;
    }

    /**
     * 使用指定 AI 模型策略切割文档内容。
     *
     * @param documentContent 待切割文档内容
     * @return AI 切割结果
     */
    @Override
    public SplitResult split(DocumentContent documentContent) {
        // 从上下文中选择框架和厂商对应的策略，再委托具体模型完成切割。
        return llmContext.getLlmModel(aiFramework, aiType).doSplitter(documentContent);
    }
}
