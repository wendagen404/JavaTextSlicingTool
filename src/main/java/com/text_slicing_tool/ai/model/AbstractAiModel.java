package com.text_slicing_tool.ai.model;

import com.text_slicing_tool.enums.SplitterType;
import com.text_slicing_tool.pojo.AiMessageResult;
import com.text_slicing_tool.pojo.DocumentContent;
import com.text_slicing_tool.pojo.ExtractResult;
import com.text_slicing_tool.pojo.SplitResult;
import com.text_slicing_tool.pojo.TextChunk;
import com.text_slicing_tool.splitter.support.ChunkWindowBuilder;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
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
     * 构建适合 RAG 入库的 AI 切割提示词。
     *
     * @param text 原始文本
     * @return 提示词
     */
    protected String buildSplitPrompt(String text) {
        return """
            角色：
            你是一个RAG文档切分专家，负责对输入文档做语义分片，保障检索语义完整性。
            任务：
            请将下面文本按语义关联切分为适合RAG入库的片段。
            要求：
            1. 每个片段保证语义完整，避免切分过短，拒绝把孤立标题单独作为分片。
            2. 禁止切断URL、邮箱、版本号、文件路径。
            3. 一个片段占一行，片段之间使用单个换行分隔，不要出现两行空行。
            4. 不要输出编号、额外解释、Markdown代码块，只输出分片结果。
            5. 标题处理规则：大标题必须跟随其下属正文内容合并为同一个分片；仅当大标题下整体内容篇幅过长时，才允许基于小标题做拆分；禁止将大标题、小标题剥离正文单独拆成独立分片；禁止把同一个大标题下的多个小标题内容拆成互相隔离的零散分片，并且大标题需要和内容在同一分片以“:”进行连接。

            【正确示例】
            ## 环境配置:配置JDK版本为17，修改application.yml配置文件，设置端口为8080，数据库地址jdbc:mysql://127.0.0.1:3306/test。

            【错误反例】
            ## 环境配置
            
            JDK版本为17
            
            修改application.yml配置文件
            
            设置端口为8080，数据库地址jdbc:mysql://127.0.0.1:3306/test

            文本：
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
