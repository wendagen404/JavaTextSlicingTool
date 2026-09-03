package com.text_slicing_tool.splitter;

import com.text_slicing_tool.pojo.DocumentContent;
import com.text_slicing_tool.pojo.SplitResult;

public interface TextSplitter {
    SplitResult split(DocumentContent documentContent);
}
