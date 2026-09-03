package com.text_slicing_tool.ai.model;

import com.text_slicing_tool.enums.AiFramework;
import com.text_slicing_tool.pojo.AiMessageResult;
import dev.langchain4j.model.chat.ChatModel;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基于 LangChain4j ChatModel 的模型策略基类。
 */
public abstract class AbstractLangChain4jModel extends AbstractAiModel {
    private final ChatModel chatModel;

    protected AbstractLangChain4jModel(ChatModel chatModel) {
        this.chatModel = chatModel;
    }

    /**
     * 返回当前策略使用的 AI 调用框架。
     *
     * @return LangChain4j 框架类型
     */
    @Override
    public AiFramework supportFramework() {
        return AiFramework.LANGCHAIN4J;
    }

    /**
     * 调用 LangChain4j 模型并封装为统一 AI 文本结果。
     *
     * @param prompt 用户提示词
     * @return AI 文本结果
     */
    @Override
    protected AiMessageResult chat(String prompt) {
        if (chatModel == null) {
            throw new IllegalStateException("LangChain4j ChatModel 未配置");
        }

        long start = System.currentTimeMillis();
        String content = chatModel.chat(prompt);

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("client", "LangChain4j ChatModel");
        return AiMessageResult.builder()
                .framework(supportFramework())
                .aiType(supportType())
                .content(content)
                .durationMillis(System.currentTimeMillis() - start)
                .metadata(metadata)
                .build();
    }
}
