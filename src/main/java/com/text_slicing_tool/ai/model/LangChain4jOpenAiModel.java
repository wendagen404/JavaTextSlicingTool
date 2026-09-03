package com.text_slicing_tool.ai.model;

import com.text_slicing_tool.enums.AiType;
import dev.langchain4j.model.chat.ChatModel;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.stereotype.Component;

/**
 * LangChain4j OpenAI 模型策略。
 */
public class LangChain4jOpenAiModel extends AbstractLangChain4jModel {
    public LangChain4jOpenAiModel(ObjectProvider<ChatModel> chatModelProvider) {
        super(chatModelProvider.getIfAvailable());
    }

    /**
     * 返回当前策略支持的模型厂商类型。
     *
     * @return OpenAI 类型
     */
    @Override
    public AiType supportType() {
        return AiType.OPENAI;
    }
}
