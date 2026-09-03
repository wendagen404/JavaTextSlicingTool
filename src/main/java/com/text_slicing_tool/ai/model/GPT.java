package com.text_slicing_tool.ai.model;

import com.text_slicing_tool.pojo.ExtractResult;
import com.text_slicing_tool.pojo.SplitResult;

public class GPT  extends LlmModel{
    @Override
    public SplitResult doSplitter() {
        return SplitResult.empty("gpt");
    }

    @Override
    public ExtractResult doExtractor() {
        return ExtractResult.empty("gpt", "ai");
    }
}
