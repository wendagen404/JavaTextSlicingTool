package com.text_slicing_tool.splitter.impl;

import com.text_slicing_tool.pojo.SplitResult;
import com.text_slicing_tool.splitter.TextSplitter;

/**
 * 中文字符切割
 */
public class ChineseCharSplitter implements TextSplitter {
    @Override
    public SplitResult split() {
        return SplitResult.empty("chinese-char");
    }
}
