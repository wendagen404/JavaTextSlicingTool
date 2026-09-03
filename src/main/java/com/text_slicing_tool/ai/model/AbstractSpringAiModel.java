package com.text_slicing_tool.ai.model;

import com.text_slicing_tool.enums.AiFramework;
import com.text_slicing_tool.pojo.AiMessageResult;
import org.springframework.ai.chat.client.ChatClient;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 基于 Spring AI ChatClient 的模型策略基类。
 */
public abstract class AbstractSpringAiModel extends AbstractAiModel {
    private final ChatClient chatClient;

    protected AbstractSpringAiModel(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder == null ? null : chatClientBuilder.build();
    }

    /**
     * 返回当前策略使用的 AI 调用框架。
     *
     * @return Spring AI 框架类型
     */
    @Override
    public AiFramework supportFramework() {
        return AiFramework.SPRING_AI;
    }

    /**
     * 调用 Spring AI 模型并封装为统一 AI 文本结果。
     *
     * @param prompt 用户提示词
     * @return AI 文本结果
     */
    @Override
    protected AiMessageResult chat(String prompt) {
        if (chatClient == null) {
            throw new IllegalStateException("Spring AI ChatClient 未配置");
        }

        long start = System.currentTimeMillis();
        String content = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("client", "Spring AI ChatClient");
        return AiMessageResult.builder()
                .framework(supportFramework())
                .aiType(supportType())
                .content(content)
                .durationMillis(System.currentTimeMillis() - start)
                .metadata(metadata)
                .build();
    }
}
