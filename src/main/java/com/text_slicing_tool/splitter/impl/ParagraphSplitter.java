package com.text_slicing_tool.splitter.impl;

import com.text_slicing_tool.pojo.SplitResult;
import com.text_slicing_tool.splitter.TextSplitter;

/**
 * 段落分割(自然段)
 */
public class ParagraphSplitter implements TextSplitter {
    @Override
    public SplitResult split() {
        return SplitResult.empty("paragraph");
    }
}
