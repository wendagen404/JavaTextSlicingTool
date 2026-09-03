package com.text_slicing_tool.splitter;

import com.text_slicing_tool.extractor.impl.TextExtractor;
import com.text_slicing_tool.pojo.ExtractResult;
import com.text_slicing_tool.pojo.SplitResult;
import com.text_slicing_tool.splitter.impl.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

@Slf4j
public class Test {
    public static void main(String[] args) {
        TextExtractor extractor = new TextExtractor();
        ExtractResult extract = extractor.extract(new ClassPathResource("document/test.txt"));
        SplitterContext context = new SplitterContext();
        TextSplitter splitter = new CustomSymbolSplitter("测试");
        SplitResult split = splitter.split(extract.getContent());
        split.getChunks().forEach(chunk -> {
            log.error("--------------------------");
            log.info(chunk.getContent());
        });
    }
}
