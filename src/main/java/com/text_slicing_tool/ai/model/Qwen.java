package com.text_slicing_tool.ai.model;

import com.text_slicing_tool.enums.AiType;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Component;

/**
 * 通义千问模型策略。
 */
public class Qwen extends AbstractSpringAiModel {
    public Qwen(ChatClient chatClientBuilder) {
        super(chatClientBuilder);
    }

    /**
     * 返回当前策略支持的模型厂商类型。
     *
     * @return 通义千问类型
     */
    @Override
    public AiType supportType() {
        return AiType.QWEN;
    }
}
