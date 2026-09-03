package com.text_slicing_tool.ai.model;

import com.text_slicing_tool.pojo.ExtractResult;
import com.text_slicing_tool.pojo.SplitResult;

/**
 * 大语言模型顶层抽象
 */
public abstract class LlmModel {
    public abstract SplitResult doSplitter();

    public abstract ExtractResult doExtractor();
}
