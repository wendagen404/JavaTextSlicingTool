package com.text_slicing_tool.extractor.impl;

import com.text_slicing_tool.extractor.DocumentExtractor;
import com.text_slicing_tool.pojo.ExtractResult;
import org.springframework.core.io.ClassPathResource;

/**
 * txt文本识别
 */
public class TextExtractor implements DocumentExtractor {
    @Override
    public ExtractResult extract(ClassPathResource resource) {
        return null;
    }
}
