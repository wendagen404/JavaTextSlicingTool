package com.text_slicing_tool.splitter.impl;

import com.text_slicing_tool.pojo.SplitResult;
import com.text_slicing_tool.splitter.TextSplitter;

/**
 * 根据标题号切割
 * 例如:
 * 标题一、1.1、一、等
 */
public class TitleSplitter implements TextSplitter {
    @Override
    public SplitResult split() {
        return SplitResult.empty("title");
    }
}
