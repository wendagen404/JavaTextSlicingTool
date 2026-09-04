package com.text_slicing_tool.ai;

import com.text_slicing_tool.ai.model.LlmModel;
import com.text_slicing_tool.enums.AiFramework;
import com.text_slicing_tool.enums.AiType;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 模型策略上下文。
 */
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
     * <p>若精确匹配不到且当前仅注册了一个模型，则回退返回该唯一模型——
     * 支持调用方仅传入 {@link LLMContext} 而未指定 type 的便捷用法
     * （此时使用 yml 中配置的唯一模型）。
     *
     * @param framework AI 调用框架
     * @param type 模型厂商类型
     * @return 模型策略
     */
    public LlmModel getLlmModel(AiFramework framework, AiType type) {
        LlmModel model = modelMap.get(buildKey(framework, type));
        if (model == null) {
            // 便捷回退：仅注册了一个模型时，直接使用它（对应 yml 单模型配置场景）
            if (modelMap.size() == 1) {
                return modelMap.values().iterator().next();
            }
            throw new IllegalArgumentException(
                    "Unsupported AI model strategy: " + framework + "/" + type
                            + ", registered: " + modelMap.keySet());
        }
        return model;
    }

    /**
     * 返回当前上下文中注册的唯一模型；若未注册或注册多个则抛异常。
     *
     * @return 唯一已注册的模型策略
     */
    public LlmModel getDefaultModel() {
        if (modelMap.isEmpty()) {
            throw new IllegalStateException("未注入任何 LlmModel，请检查 text-slicing.ai 配置");
        }
        if (modelMap.size() > 1) {
            throw new IllegalStateException(
                    "存在多个 LlmModel：" + modelMap.keySet() + "，请显式指定 framework/type");
        }
        return modelMap.values().iterator().next();
    }

    private String buildKey(AiFramework framework, AiType type) {
        return framework.name() + ":" + type.name();
    }
}
