package com.wdg.text_slicing_tool;

import com.text_slicing_tool.TextSlicingToolApplication;
import com.text_slicing_tool.ai.LLMContext;
import com.text_slicing_tool.ai.model.LlmModel;
import com.text_slicing_tool.enums.SplitterType;
import com.text_slicing_tool.extractor.DocumentExtractor;
import com.text_slicing_tool.extractor.impl.TextExtractor;
import com.text_slicing_tool.pojo.ExtractResult;
import com.text_slicing_tool.pojo.SplitResult;
import com.text_slicing_tool.pojo.SplitterConfig;
import com.text_slicing_tool.splitter.SplitterContext;
import com.text_slicing_tool.splitter.TextSplitter;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * 基于 LangChain4j 的内存向量检索测试。
 *
 * <p>使用 Spring Boot 自动装配的 AI Bean（由 {@code text-slicing.ai} 配置驱动）处理文档，
 * 配合 LangChain4j 内存向量库完成语义检索。运行需提供环境变量 {@code QWEN_API_KEY}。
 */
@Slf4j
@SpringBootTest(
        classes = TextSlicingToolApplication.class)
class LangChain4jInMemoryEmbeddingTest {


    // 直接拿策略上下文/模型（高级用法）
    @Autowired
    private LLMContext llmContext;

    @Test
    void testEmbedAndSearch() {

        // ---------- 1. 创建 Embedding 模型 ----------
        String apiKey = System.getenv("QWEN_API_KEY");
        assertThat(apiKey)
                .as("请设置环境变量 QWEN_API_KEY")
                .isNotBlank();

        EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
                .apiKey(apiKey)
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .modelName("text-embedding-v3")
                .build();

        // ---------- 2. 文档解析 + AI 语义分片（直接使用自动装配的 AI 切割 Bean） ----------
        TextExtractor extractor = new TextExtractor();
        ExtractResult extract = extractor.extract(new ClassPathResource("document/test.txt"));

        SplitterContext context = new SplitterContext();
        SplitterConfig config = SplitterConfig.builder()
                .llmContext(llmContext)
                .build();
        TextSplitter splitter = context.getTextSplitter(SplitterType.AI, config);
        SplitResult result = splitter.split(extract.getContent());
        List<TextSegment> documents = result.toLangChain4jTextSegments();
        log.info("\n====文档分片完成，共 {} 个分片====", documents.size());
        for (int i = 0; i < documents.size(); i++) {
            TextSegment seg = documents.get(i);
            log.info("【分片{}】原始文本:\n{}\n", i, seg.text());
        }
        assertThat(documents).isNotEmpty().as("文档分片结果不能为空");

        // ---------- 3. 批量向量化（Qwen text-embedding-v3 单批上限 10，需分批） ----------
//        log.info("开始向量化分片文本，向量仅内部使用...");
//        List<Embedding> embeddings = new ArrayList<>();
//        int batchSize = 10;
//        for (int i = 0; i < documents.size(); i += batchSize) {
//            List<TextSegment> batch = documents.subList(i, Math.min(i + batchSize, documents.size()));
//            embeddings.addAll(embeddingModel.embedAll(batch).content());
//        }
//        assertThat(embeddings).hasSize(documents.size());
//
//        InMemoryEmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();
//        // 存储：向量<-->原始分片文本一一对应
//        store.addAll(embeddings, documents);
//        log.info("向量库存储完毕，向量用于计算相似度；原始文本保存在store中");
//
//        // ---------- 4. 语义检索 ----------
//        String query = "Java 文本切片工具库的背景";
//        Embedding queryEmbedding = embeddingModel.embed(query).content();
//
//        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
//                .queryEmbedding(queryEmbedding)
//                .maxResults(1)
//                .minScore(0.25)
//                .build();
//
//        EmbeddingSearchResult<TextSegment> searchResult = store.search(searchRequest);
//        List<EmbeddingMatch<TextSegment>> matches = searchResult.matches();
//        assertThat(matches).isNotEmpty();
//
//        log.info("\n==== 查询问题：{} =====", query);
//        log.info("检索返回 {} 条匹配分片", matches.size());
//        for (EmbeddingMatch<TextSegment> match : matches) {
//            TextSegment seg = match.embedded();
//            log.info("[DEBUG] Segment类={}", seg.getClass().getName());
//            log.info("  - 相似度: {} | 分片原始内容：\n{}",
//                    String.format("%.4f", match.score()),
//                    match.embedded().text());
//        }
    }
}
