package com.text_slicing_tool.ai.model;

import com.text_slicing_tool.enums.AiType;
import dev.langchain4j.model.chat.ChatModel;

/**
 * LangChain4j OpenAI 兼容模型策略。
 *
 * <p>厂商类型由配置注入，作为元数据标记；底层统一使用 OpenAI 兼容接口，
 * 通过不同 base-url 适配通义千问、智谱 GLM、DeepSeek 等厂商。
 */
public class LangChain4jOpenAiModel extends AbstractLangChain4jModel {
    private final AiType type;

    public LangChain4jOpenAiModel(ChatModel chatModel, AiType type) {
        super(chatModel);
        this.type = type;
    }

    /**
     * 返回当前策略支持的模型厂商类型。
     *
     * @return 配置的厂商类型
     */
    @Override
    public AiType supportType() {
        return type;
    }
}
