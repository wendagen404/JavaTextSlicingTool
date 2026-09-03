package com.text_slicing_tool.ai.model;

import com.text_slicing_tool.pojo.ExtractResult;
import com.text_slicing_tool.pojo.SplitResult;

public class GLM extends LlmModel{
    @Override
    public SplitResult doSplitter() {
        return SplitResult.empty("glm");
    }

    @Override
    public ExtractResult doExtractor() {
        return ExtractResult.empty("glm", "ai");
    }
}
