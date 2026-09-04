package com.text_slicing_tool.ai.model;

import com.text_slicing_tool.enums.AiType;
import com.text_slicing_tool.pojo.ExtractResult;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.core.io.ClassPathResource;

/**
 * Spring AI OpenAI 兼容模型策略。
 *
 * <p>厂商类型由配置注入，作为策略路由与元数据标记；底层统一使用 OpenAI 兼容接口，
 * 通过不同 base-url 适配 OpenAI、通义千问、智谱 GLM、DeepSeek 等厂商。
 *
 * <p>与 {@link LangChain4jOpenAiModel} 形成对称设计：同一 AiType 在两个框架下
 * 分别由这两个类承载，{@code ai/config} 自动装配时按 {@code framework/type} 选择其一。
 */
public class SpringAiOpenAiModel extends AbstractSpringAiModel {
    private final AiType type;

    public SpringAiOpenAiModel(ChatClient chatClient, AiType type) {
        super(chatClient);
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
