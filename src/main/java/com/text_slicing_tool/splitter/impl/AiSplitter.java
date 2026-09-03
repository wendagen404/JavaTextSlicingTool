package com.text_slicing_tool.splitter.impl;

import com.text_slicing_tool.pojo.SplitResult;
import com.text_slicing_tool.splitter.TextSplitter;

/**
 * AI切分
 */
public class AiSplitter implements TextSplitter {
    @Override
    public SplitResult split() {
        return SplitResult.empty("ai");
    }
}
