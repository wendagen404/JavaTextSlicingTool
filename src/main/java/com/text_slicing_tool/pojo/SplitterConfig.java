package com.text_slicing_tool.pojo;

import com.text_slicing_tool.ai.LLMContext;
import com.text_slicing_tool.ai.model.LlmModel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SplitterConfig {
    /**
     * 自定义字符(CustomSymbolSplitter)
     */
    private String charVal;

    /**
     * 模型上下文(AiSplitter)
     */
    private LLMContext llmContext;
}
