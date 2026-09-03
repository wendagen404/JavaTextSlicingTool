package com.text_slicing_tool.extractor.impl;

import com.text_slicing_tool.extractor.DocumentExtractor;
import com.text_slicing_tool.pojo.DocumentContent;
import com.text_slicing_tool.pojo.ExtractResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * PdfBox识别
 */
@Slf4j
public class PdfBoxDocumentExtractor implements DocumentExtractor {
    @Override
    public ExtractResult extract(ClassPathResource resource) {
        PDDocument document = null;
        try {
            // 使用InputStream加载PDF
            document = Loader.loadPDF(resource.getInputStream().readAllBytes());
            PDFTextStripper stripper = new PDFTextStripper();

            // 完整文本
            String rawText = stripper.getText(document);
            log.info(rawText);

            return ExtractResult.builder()
                    .extractorType("pdfbox")
                    .content(DocumentContent.builder()
                            .sourceType("pdf")
                            .text(rawText)
                            .build())
                    .build();
        } catch (IOException e) {
            log.error("pdf解析异常", e);
            return null;
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    log.error("关闭pdf失败", e);
                }
            }
        }
    }

}
