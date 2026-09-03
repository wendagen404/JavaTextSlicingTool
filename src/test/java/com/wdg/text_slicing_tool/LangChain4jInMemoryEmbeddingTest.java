package com.wdg.text_slicing_tool;

import com.text_slicing_tool.enums.SplitterType;
import com.text_slicing_tool.extractor.impl.TextExtractor;
import com.text_slicing_tool.pojo.ExtractResult;
import com.text_slicing_tool.pojo.SplitResult;
import com.text_slicing_tool.splitter.SplitterContext;
import com.text_slicing_tool.splitter.TextSplitter;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
class LangChain4jInMemoryEmbeddingTest {

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

        // ---------- 2. 文档解析 + 语义分片 ----------
        TextExtractor extractor = new TextExtractor();
        ExtractResult extract = extractor.extract(new ClassPathResource("document/test.txt"));
        // 打印完整原始文档
        log.info("====原始完整文档内容====");

        SplitterContext context = new SplitterContext();
        TextSplitter splitter = context.getTextSplitter(SplitterType.SEMANTIC);
        SplitResult result = splitter.split(extract.getContent());
        List<TextSegment> documents = result.toLangChain4jTextSegments();

        log.info("\n====文档分片完成，共 {} 个分片====", documents.size());
        // 输出每一段【分片后的原始文本】，不是向量
        for (int i = 0; i < documents.size(); i++) {
            TextSegment seg = documents.get(i);
            log.info("【分片{}】原始文本:\n{}\n", i, seg.text());
        }
        assertThat(documents).isNotEmpty().as("文档分片结果不能为空");

        // ----------3.批量向量化（向量仅用于检索计算，不输出） ----------
        log.info("开始向量化分片文本，向量仅内部使用...");
        List<Embedding> embeddings = embeddingModel.embedAll(documents).content();
        assertThat(embeddings).hasSize(documents.size());

        InMemoryEmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();
        // 存储：向量<-->原始分片文本一一对应
        store.addAll(embeddings, documents);
        log.info("✅向量库存储完毕，向量用于计算相似度；原始文本保存在store中");

        // ----------4.语义检索 ----------
        String query = "Java 文本切片工具库的背景";
        Embedding queryEmbedding = embeddingModel.embed(query).content();

        EmbeddingSearchRequest searchRequest = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(1)
                .minScore(0.25)
                .build();

        EmbeddingSearchResult<TextSegment> searchResult = store.search(searchRequest);
        List<EmbeddingMatch<TextSegment>> matches = searchResult.matches();
        assertThat(matches).isNotEmpty();

        log.info("\n==== 查询问题：{} =====", query);
        log.info("检索返回 {} 条匹配分片", matches.size());
        for (EmbeddingMatch<TextSegment> match : matches) {
            TextSegment seg = match.embedded();

            // 打印类名用于调试，确认包正确
            log.info("[DEBUG] Segment类={}", seg.getClass().getName());
            String originSegmentText = seg.text();

            log.info("  - 相似度: {} | 分片原始内容：\n{}",
                    String.format("%.4f", match.score()),
                    match.embedded().text());
        }

    }
}
