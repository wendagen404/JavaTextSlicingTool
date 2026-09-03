package com.text_slicing_tool.extractor.impl;

import com.text_slicing_tool.extractor.DocumentExtractor;
import com.text_slicing_tool.pojo.DocumentContent;
import com.text_slicing_tool.pojo.ExtractResult;
import org.springframework.core.io.ClassPathResource;

/**
 * markdown识别
 */
public class MarkdownExtractor implements DocumentExtractor {
    @Override
    public ExtractResult extract(ClassPathResource resource) {
        return ExtractResult.builder()
                .extractorType("markdown")
                .content(DocumentContent.builder().sourceType("markdown").build())
                .build();
    }
}
