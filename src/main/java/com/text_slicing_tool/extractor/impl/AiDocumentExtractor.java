package com.text_slicing_tool.extractor.impl;

import com.text_slicing_tool.extractor.DocumentExtractor;
import com.text_slicing_tool.pojo.DocumentContent;
import com.text_slicing_tool.pojo.ExtractResult;
import org.springframework.core.io.ClassPathResource;

/**
 * AI提取
 */
public class AiDocumentExtractor implements DocumentExtractor {
    @Override
    public ExtractResult extract(ClassPathResource resource) {
        return ExtractResult.builder()
                .extractorType("ai")
                .content(DocumentContent.builder().sourceType("ai").build())
                .build();
    }
}
