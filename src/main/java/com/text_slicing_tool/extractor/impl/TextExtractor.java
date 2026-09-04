package com.text_slicing_tool.extractor.impl;

import com.text_slicing_tool.extractor.DocumentExtractor;
import com.text_slicing_tool.pojo.DocumentContent;
import com.text_slicing_tool.pojo.ExtractResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * txt文本识别
 */
@Slf4j
public class TextExtractor implements DocumentExtractor {
    @Override
    public ExtractResult extract(ClassPathResource resource) {
        BufferedReader buffReader = null;
        try {
            InputStreamReader reader = new InputStreamReader(resource.getInputStream());
            buffReader = new BufferedReader(reader);
            String strTmp = "";
            StringBuilder sb = new StringBuilder();
            while ((strTmp = buffReader.readLine()) != null) {
                sb.append(strTmp).append("\n");
            }
            return ExtractResult.builder()
                    .extractorType("text")
                    .content(DocumentContent.builder()
                            .sourceType("txt")
                            .text(sb.toString())
                            .build())
                    .build();
        } catch (IOException e) {
            log.error("读取文本文件出错", e.getMessage());
            return ExtractResult.empty("text","txt");
        } finally {
            try {
                if(buffReader != null) {
                    buffReader.close();
                }
            } catch (IOException e) {
                log.error("关闭buffReader出错");
            }
        }
    }

}
