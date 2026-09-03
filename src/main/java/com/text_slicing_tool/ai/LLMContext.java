package com.text_slicing_tool.ai;

import com.text_slicing_tool.ai.model.LlmModel;
import com.text_slicing_tool.enums.AiFramework;
import com.text_slicing_tool.enums.AiType;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 模型策略上下文。
 */
@Component
public class LLMContext {
    private final Map<String, LlmModel> modelMap = new LinkedHashMap<>();

    public LLMContext(List<LlmModel> models) {
        // Spring 自动注入所有 LlmModel 实现类，并按框架和厂商注册到策略表。
        for (LlmModel model : models) {
            modelMap.put(buildKey(model.supportFramework(), model.supportType()), model);
        }
    }

    /**
     * 根据模型厂商类型获取默认 Spring AI 策略。
     *
     * @param type 模型厂商类型
     * @return 模型策略
     */
    public LlmModel getLlmModel(AiType type) {
        return getLlmModel(AiFramework.SPRING_AI, type);
    }

    /**
     * 根据 AI 框架和模型厂商类型获取对应策略。
     *
     * @param framework AI 调用框架
     * @param type 模型厂商类型
     * @return 模型策略
     */
    public LlmModel getLlmModel(AiFramework framework, AiType type) {
        LlmModel model = modelMap.get(buildKey(framework, type));
        if (model == null) {
            throw new IllegalArgumentException("Unsupported AI model strategy: " + framework + "/" + type);
        }
        return model;
    }

    private String buildKey(AiFramework framework, AiType type) {
        return framework.name() + ":" + type.name();
    }
}
