package com.text_slicing_tool.extractor.impl;

import com.text_slicing_tool.extractor.DocumentExtractor;
import com.text_slicing_tool.pojo.DocumentContent;
import com.text_slicing_tool.pojo.ExtractResult;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.util.LoadLibs;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.core.io.ClassPathResource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;

/**
 * OCR识别
 */
@Slf4j
public class OcrDocumentExtractor implements DocumentExtractor {

    @Resource
    private PdfBoxDocumentExtractor pdfBoxDocumentExtractor;

    @Override
    public ExtractResult extract(ClassPathResource resource) {
        try {
            BufferedImage image;
            try (InputStream is = resource.getInputStream()) {
                image = ImageIO.read(is);
            }

            ITesseract tesseract = new Tesseract();

            // tessdata 目录
            tesseract.setDatapath(LoadLibs.extractTessResources("tessdata").getAbsolutePath());

            // 中文 + 英文
            tesseract.setLanguage("chi_sim+eng");

            // 可选：按你的图片类型调
            // tesseract.setPageSegMode(ITessAPI.TessPageSegMode.PSM_AUTO);

            String text = tesseract.doOCR(image);
            log.info(text);
            return ExtractResult.builder()
                    .extractorType("ocr")
                    .content(DocumentContent.builder()
                            .sourceType("image")
                            .text(text)
                            .build())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("OCR识别失败", e);
        }
    }

//    public ExtractResult extract(ClassPathResource resource) {
//        try (InputStream is = resource.getInputStream();
//             PDDocument document = Loader.loadPDF(is.readAllBytes())) {
//
//            String embeddedText = new PDFTextStripper().getText(document).trim();
//            if (embeddedText.length() >= 80) {
//                return ExtractResult.builder()
//                        .extractorType("ocr")
//                        .content(DocumentContent.builder()
//                                .sourceType("pdf")
//                                .text(embeddedText)
//                                .build())
//                        .build();
//            }
//
//            PDFRenderer renderer = new PDFRenderer(document);
//            ITesseract tesseract = new Tesseract();
//            tesseract.setDatapath(LoadLibs.extractTessResources("tessdata").getAbsolutePath());
//            tesseract.setLanguage("chi_sim+eng");
//
//            StringBuilder sb = new StringBuilder();
//            for (int page = 0; page < document.getNumberOfPages(); page++) {
//                BufferedImage image = renderer.renderImageWithDPI(page, 300);
//                String pageText = tesseract.doOCR(image);
//                sb.append(pageText).append('\n');
//            }
//            return ExtractResult.builder()
//                    .extractorType("ocr")
//                    .content(DocumentContent.builder()
//                            .sourceType("pdf")
//                            .text(sb.toString().trim())
//                            .build())
//                    .build();
//        } catch (Exception e) {
//            throw new RuntimeException("OCR识别失败", e);
//        }
//    }


    public static void main(String[] args) {
//        ClassPathResource resource = new ClassPathResource("document/Java集合相关面试题.pdf");
        ClassPathResource resource = new ClassPathResource("document/img.png");
        new OcrDocumentExtractor().extract(resource);
    }
}
