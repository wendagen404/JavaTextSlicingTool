package com.text_slicing_tool.ai.model;

import com.text_slicing_tool.enums.AiType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * 智谱 GLM 模型策略。
 */
public class GLM extends AbstractSpringAiModel {
    public GLM(ChatClient chatClientBuilder) {
        super(chatClientBuilder);
    }

    /**
     * 返回当前策略支持的模型厂商类型。
     *
     * @return GLM 类型
     */
    @Override
    public AiType supportType() {
        return AiType.GLM;
    }
}
