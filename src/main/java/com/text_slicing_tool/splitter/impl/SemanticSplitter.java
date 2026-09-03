package com.text_slicing_tool.splitter.impl;

import com.text_slicing_tool.pojo.SplitResult;
import com.text_slicing_tool.splitter.TextSplitter;

/**
 * 语义切分
 */
public class SemanticSplitter implements TextSplitter {
    @Override
    public SplitResult split() {
        return SplitResult.empty("semantic");
    }
}
