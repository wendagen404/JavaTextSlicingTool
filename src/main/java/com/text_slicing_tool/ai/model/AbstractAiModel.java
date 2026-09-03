package com.text_slicing_tool.ai.model;

import com.text_slicing_tool.enums.SplitterType;
import com.text_slicing_tool.pojo.AiMessageResult;
import com.text_slicing_tool.pojo.DocumentContent;
import com.text_slicing_tool.pojo.ExtractResult;
import com.text_slicing_tool.pojo.SplitResult;
import com.text_slicing_tool.pojo.TextChunk;
import com.text_slicing_tool.splitter.support.ChunkWindowBuilder;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 模型策略公共基类，负责提示词、结果解析和领域返回值组装。
 */
public abstract class AbstractAiModel implements LlmModel {
    /**
     * 调用底层模型并返回统一 AI 文本结果。
     *
     * @param prompt 用户提示词
     * @return AI 文本结果
     */
    protected abstract AiMessageResult chat(String prompt);

    /**
     * 使用当前模型对文档内容进行 AI 切割。
     *
     * @param documentContent 待切割文档内容
     * @return AI 切割结果
     */
    @Override
    public SplitResult doSplitter(DocumentContent documentContent) {
        long start = System.currentTimeMillis();
        String text = documentContent == null ? "" : documentContent.getText();
        if (text == null || text.isBlank()) {
            return SplitResult.empty(SplitterType.AI.getCode());
        }

        // 让模型只返回每个分片的正文，统一在本地转换成 TextChunk。
        AiMessageResult aiResult = chat(buildSplitPrompt(text));
        List<String> parts = parseAiLines(aiResult.getContent());
        List<TextChunk> chunks = ChunkWindowBuilder.buildFromParts(
                parts,
                text,
                SplitterType.AI.getCode(),
                documentContent.getSourceId()
        );

        Map<String, Object> metadata = buildMetadata(aiResult);
        return SplitResult.builder()
                .splitterType(SplitterType.AI.getCode())
                .chunks(chunks)
                .totalChunks(chunks.size())
                .durationMillis(System.currentTimeMillis() - start)
                .metadata(metadata)
                .build();
    }

    /**
     * 使用当前模型对文档资源进行 AI 文本提取。
     *
     * @param resource 待解析资源
     * @return AI 提取结果
     */
    @Override
    public ExtractResult doExtractor(ClassPathResource resource) {
        long start = System.currentTimeMillis();
        try (InputStream inputStream = resource.getInputStream()) {
            String rawText = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            // AI 适配层只处理文本输入，PDF/图片等二进制内容先由专门提取器转成文本。
            AiMessageResult aiResult = chat(buildExtractPrompt(rawText));
            Map<String, Object> metadata = buildMetadata(aiResult);
            metadata.put("resourcePath", resource.getPath());

            return ExtractResult.builder()
                    .extractorType(supportType().getVendorCode())
                    .durationMillis(System.currentTimeMillis() - start)
                    .metadata(metadata)
                    .content(DocumentContent.builder()
                            .sourceId(resource.getPath())
                            .sourceType("ai")
                            .text(aiResult.getContent())
                            .metadata(metadata)
                            .build())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException("AI 文本提取失败", e);
        }
    }

    /**
     * 构建适合 RAG 入库的 AI 切割提示词。
     *
     * @param text 原始文本
     * @return 提示词
     */
    protected String buildSplitPrompt(String text) {
        return """
                请将下面文本切分为适合 RAG 入库的语义片段。
                要求：
                1. 每个片段保持语义完整。
                2. 不要切断 URL、邮箱、版本号、文件路径。
                3. 每个片段独占一行。
                4. 不要输出编号、解释或 Markdown 代码块。

                文本：
                %s
                """.formatted(text);
    }

    /**
     * 构建适合 RAG 入库的 AI 文本提取提示词。
     *
     * @param text 原始内容
     * @return 提示词
     */
    protected String buildExtractPrompt(String text) {
        return """
                请从下面内容中提取适合 RAG 入库的纯文本。
                要求：
                1. 保留标题、段落、表格语义。
                2. 清理无意义噪声。
                3. 不要输出额外解释。

                内容：
                %s
                """.formatted(text);
    }

    /**
     * 将模型按行返回的片段解析为分片文本列表。
     *
     * @param response 模型返回文本
     * @return 分片文本列表
     */
    protected List<String> parseAiLines(String response) {
        if (response == null || response.isBlank()) {
            return List.of();
        }
        return response.lines()
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .toList();
    }

    private Map<String, Object> buildMetadata(AiMessageResult aiResult) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("aiType", supportType().name());
        metadata.put("framework", supportFramework().getCode());
        metadata.put("aiDurationMillis", aiResult.getDurationMillis());
        metadata.putAll(aiResult.getMetadata());
        return metadata;
    }
}
