package com.text_slicing_tool.splitter.impl;

import com.text_slicing_tool.pojo.SplitResult;
import com.text_slicing_tool.splitter.TextSplitter;

/**
 * 自定义字符分割
 */
public class CustomSymbolSplitter implements TextSplitter {
    @Override
    public SplitResult split() {
        return SplitResult.empty("custom-symbol");
    }
}
