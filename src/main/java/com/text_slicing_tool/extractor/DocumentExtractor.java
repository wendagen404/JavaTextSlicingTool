package com.text_slicing_tool.extractor;

import com.text_slicing_tool.pojo.ExtractResult;
import org.springframework.core.io.ClassPathResource;

public interface DocumentExtractor {
    ExtractResult extract(ClassPathResource resource);
}
