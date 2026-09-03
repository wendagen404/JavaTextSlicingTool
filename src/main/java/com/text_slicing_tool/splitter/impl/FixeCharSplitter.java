package com.text_slicing_tool.splitter.impl;

import com.text_slicing_tool.pojo.SplitResult;
import com.text_slicing_tool.splitter.TextSplitter;

/**
 * 固定字符分割
 */
public class FixeCharSplitter implements TextSplitter {
    @Override
    public SplitResult split() {
        return SplitResult.empty("fixed-char");
    }
}
