# Text Slicing Tool

Java 文本提取与切片工具库，专为中文 RAG（检索增强生成）场景设计，可作为 **Spring Boot Starter** 直接集成到任何 Spring Boot 项目中。

## 项目背景

在 RAG 场景中，文档入库前通常需要先完成 **文本提取**（从 PDF、图片、Markdown 等格式中提取纯文本），再进行 **文本切片**（将长文本按语义或规则切分为适合向量化的片段）。当前 Java 生态中缺少一个面向中文文档、开箱即用且可直接集成到 Spring Boot RAG 项目的独立工具库。

本项目实现了两层核心能力：

- **文本提取层**：统一接口适配 PDFBox、OCR、纯文本、Markdown 等多种解析方式
- **文本切片层**：提供多种切分策略，包括固定字符、自然段、标题、语义、AI 智能切割等

目标让上层业务只关心"输入什么文档、输出什么文本块"，而不关心解析与切分细节。

## 项目结构

```
src/main/java/com/text_slicing_tool/
├── TextSlicingToolApplication.java        # Spring Boot 启动入口
├── ai/
│   ├── LLMContext.java                    # AI 模型策略上下文（策略模式）
│   ├── config/                            # ★ Spring Boot 自动装配
│   │   ├── AiProperties.java              #   text-slicing.ai 配置属性
│   │   ├── AiAutoConfiguration.java       #   自动装配入口（注册 LLMContext）
│   │   ├── SpringAiAutoConfiguration.java #   Spring AI 框架装配
│   │   └── LangChain4jAutoConfiguration.java # LangChain4j 框架装配
│   └── model/
│       ├── LlmModel.java                  # 大语言模型策略接口
│       ├── AbstractAiModel.java           # 模型策略公共基类（提示词/结果解析）
│       ├── AbstractSpringAiModel.java     # Spring AI 模型基类（基于 ChatClient）
│       ├── AbstractLangChain4jModel.java  # LangChain4j 模型基类（基于 ChatModel）
│       ├── SpringAiOpenAiModel.java       # Spring AI + OpenAI 兼容实现（厂商可配置）
│       └── LangChain4jOpenAiModel.java    # LangChain4j + OpenAI 兼容实现（厂商可配置）
├── enums/
│   ├── AiFramework.java                   # AI 框架枚举（SPRING_AI / LANGCHAIN4J）
│   ├── AiType.java                        # AI 厂商枚举（OPENAI / QWEN / GLM / DEEPSEEK / OLLAMA ...）
│   └── SplitterType.java                  # 切割策略枚举
├── extractor/
│   ├── DocumentExtractor.java             # 文档提取器接口
│   └── impl/
│       ├── TextExtractor.java             # TXT 文本提取
│       ├── MarkdownExtractor.java         # Markdown 解析提取（基于 commonmark）
│       ├── PdfBoxDocumentExtractor.java   # PDF 文本提取（基于 PDFBox）
│       └── OcrDocumentExtractor.java      # OCR 图片/PDF 文本提取（基于 Tesseract）
├── normalizer/
│   └── TextNormalizer.java                # 文本规范化（合并空白、过滤控制字符、繁体转简体等）
├── pojo/
│   ├── DocumentContent.java               # 文档提取后的正文内容
│   ├── ExtractResult.java                 # 文本提取结果
│   ├── AiMessageResult.java               # AI 模型调用统一结果
│   ├── TextChunk.java                     # 文本切片（含 LangChain4j 互转方法）
│   ├── SplitResult.java                   # 文本切割结果（含 LangChain4j 互转方法）
│   └── SplitterConfig.java               # 切割器配置（自定义符号 / LLMContext）
├── splitter/
│   ├── TextSplitter.java                  # 文本切割器接口
│   ├── SplitterContext.java               # 切割器策略上下文
│   ├── impl/
│   │   ├── FixeCharSplitter.java          # 固定字符长度切割
│   │   ├── ChineseCharSplitter.java       # 中文标点断句切割
│   │   ├── ParagraphSplitter.java         # 自然段切割
│   │   ├── TitleSplitter.java             # 标题切割
│   │   ├── CustomSymbolSplitter.java      # 自定义符号切割
│   │   ├── SemanticSplitter.java          # 启发式语义切割
│   │   └── AiSplitter.java                # AI 智能切割
│   └── support/
│       ├── ChunkWindowBuilder.java        # 分片窗口构建工具
│       ├── SentenceBoundaryDetector.java  # 句子边界识别
│       └── ProtectedTextReplacer.java     # URL/邮箱/版本号等保护替换
└── utils/
    └── StringUtils.java                   # 字符串工具

src/main/resources/
├── application.yaml                       # 配置文件（含 text-slicing.ai 模型配置示例）
├── document/                              # 测试文档样本
│   ├── test.txt
│   ├── Java.md
│   ├── *.pdf                              # 多个面试题 PDF
│   └── *.png / *.jpg                      # 图片测试文件
├── tessdata/                              # Tesseract OCR 语言包
└── META-INF/spring/
    └── org.springframework.boot.autoconfigure.AutoConfiguration.imports  # 自动装配注册
```

## 技术栈

| 组件 | 说明 |
|------|------|
| Java 21 | 语言版本 |
| Spring Boot 4.1.1 | 框架基础 |
| PDFBox 3.0.8 | PDF 文本提取 |
| Tesseract (Tess4J) 5.20.0 | OCR 图片文字识别 |
| CommonMark 0.21.0 | Markdown 解析 |
| LangChain4j 1.19.0 | LLM 集成框架（可选） |
| Spring AI 2.0.1 | LLM 集成框架（可选） |
| Lombok 1.18.48 | 简化 POJO |
| Hutool 5.8.47 | 通用工具库 |

> AI 相关依赖（Spring AI / LangChain4j）在 pom.xml 中标记为 `optional`，使用方按需引入其一即可，避免引入不需要的框架。

## 核心功能

### 文本提取

支持从多种文档格式中提取纯文本：

| 提取器 | 输入格式 | 说明 |
|--------|----------|------|
| TextExtractor | .txt | 纯文本文件读取 |
| MarkdownExtractor | .md | 基于 CommonMark 解析，保留标题、段落、表格、代码块等结构 |
| PdfBoxDocumentExtractor | .pdf | 使用 PDFBox 提取文字层文本 |
| OcrDocumentExtractor | .png/.jpg/.pdf | 使用 Tesseract OCR 识别图片文字，适用于扫描件 |

### 文本切割

提供 7 种切割策略，均通过 `TextSplitter` 统一接口调用：

| 切割器 | 策略说明 | 适用场景 |
|--------|----------|----------|
| FixeCharSplitter | 固定字符长度，支持重叠分片 | 通用场景，快速均匀分片 |
| ChineseCharSplitter | 按中文/英文句末标点断句 | 句子级切割，适合问答 |
| ParagraphSplitter | 按连续空行识别自然段 | 段落结构清晰的文档 |
| TitleSplitter | 根据 Markdown 风格标题行切割 | 章节结构明确的文档 |
| CustomSymbolSplitter | 按自定义分隔符切割，保护 URL/邮箱/版本号 | 特定格式文本 |
| SemanticSplitter | 启发式语义切割，聚合段落、识别主题边界 | 需要语义完整性的场景 |
| AiSplitter | 调用 LLM 智能切割 | 高质量切分，适合 RAG 入库 |

### AI 模块（双框架兼容）

AI 切割/提取通过策略模式同时兼容 **Spring AI** 与 **LangChain4j** 两套框架，由 yml 一行配置切换：

- **策略层**：`LlmModel` 接口 + `AbstractSpringAiModel` / `AbstractLangChain4jModel` 两个基类，分别封装 `ChatClient` 与 `ChatModel` 调用，统一返回 `AiMessageResult`。
- **实现层**：`SpringAiOpenAiModel` 与 `LangChain4jOpenAiModel` 对称设计，厂商类型由配置注入，底层走 OpenAI 兼容接口，适配 OpenAI / 通义千问 / 智谱 GLM / DeepSeek 等任意兼容厂商。
- **装配层**：`AiAutoConfiguration` + `SpringAiAutoConfiguration` / `LangChain4jAutoConfiguration` 根据 `text-slicing.ai.framework` 条件装配对应 Bean。

### 向量化集成

所有 `TextChunk` 和 `SplitResult` 均内置与 LangChain4j 的互转方法：

```java
// TextChunk → TextSegment
TextSegment segment = chunk.toLangChain4jTextSegment();

// SplitResult → TextSegment 列表
List<TextSegment> segments = splitResult.toLangChain4jTextSegments();

// SplitResult → Document 列表
List<Document> documents = splitResult.toLangChain4jDocuments();

// TextSegment → TextChunk
TextChunk chunk = TextChunk.fromLangChain4jTextSegment(segment);
```

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.8+
- （可选）任一 LLM 厂商 API Key（仅使用 AI 切割时需要）

### 作为 Starter 引入

在业务项目 `pom.xml` 中引入本工具，并按所选 AI 框架补一个依赖（二选一）：

```xml
<!-- 文本切片工具 starter -->
<dependency>
    <groupId>com.wdg</groupId>
    <artifactId>text-slicing-tool</artifactId>
    <version>1.0.0</version>
</dependency>

<!-- ===== AI 框架二选一（仅使用 AI 切割时需要） ===== -->
<!-- 方案 A：Spring AI -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-openai</artifactId>
</dependency>
<!-- 方案 B：LangChain4j -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-open-ai</artifactId>
</dependency>
```

### 配置

在 `application.yml` 中通过 `text-slicing.ai` 统一配置框架、厂商与密钥：

```yaml
text-slicing:
  ai:
    # 调用框架：spring-ai | langchain4j
    framework: spring-ai
    # 厂商类型（策略路由与元数据标记）：openai | qwen | glm | deepseek | ollama ...
    type: qwen
    # OpenAI 兼容端点（不同厂商使用各自兼容地址）
    #   OpenAI   : https://api.openai.com
    #   通义千问  : https://dashscope.aliyuncs.com/compatible-mode/v1
    #   智谱 GLM : https://open.bigmodel.cn/api/paas/v4
    #   DeepSeek : https://api.deepseek.com
    base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
    api-key: ${QWEN_API_KEY}
    # 模型名称，例如 gpt-4o-mini / qwen-plus / glm-4 / deepseek-chat
    model: qwen-plus
    temperature: 0.4
    max-tokens: 2048
```

> 切换框架只需改 `framework` 一行，代码零改动。`type` 作为策略路由与元数据标记，底层统一走 OpenAI 兼容接口，因此任意提供兼容端点的厂商（含 DeepSeek、Ollama 等）均可直接使用。

### 使用示例

#### 1. Spring 注入方式（推荐）

自动装配后，可直接注入 `TextSplitter`、`LLMContext`、`LlmModel` 等 Bean：

```java
@Service
public class RagService {
    @Autowired private LLMContext llmContext;       // 策略上下文（高级用法）
    @Autowired private LlmModel llmModel;           // 当前模型策略

    public void run() {
        // 1. 提取
        DocumentExtractor extractor = new PdfBoxDocumentExtractor();
        ExtractResult ext = extractor.extract(new ClassPathResource("document/doc.pdf"));
        // 2. AI 切割
        SplitterContext context = new SplitterContext();
        SplitterConfig config = SplitterConfig.builder()
                .llmContext(llmContext)
                .build();
        TextSplitter splitter = context.getTextSplitter(SplitterType.AI, config);
        SplitResult result = textSplitter.split(ext.getContent());
        // 3. 转 LangChain4j TextSegment 入库
        List<TextSegment> segments = result.toLangChain4jTextSegments();
    }
}
```

#### 2. 文本提取

```java
    // 从 PDF 提取
    DocumentExtractor pdfExtractor = new PdfBoxDocumentExtractor();
    ExtractResult result = pdfExtractor.extract(new ClassPathResource("document/example.pdf"));
    String text = result.getContent().getText();
    
    // 从 Markdown 提取
    DocumentExtractor mdExtractor = new MarkdownExtractor();
    ExtractResult mdResult = mdExtractor.extract(new ClassPathResource("document/example.md"));
```

#### 3. 文本切割（策略上下文方式）

```java
import com.text_slicing_tool.enums.SplitterType;// 创建切割器上下文（AI 切割需传入 LLMContext）
SplitterContext context = new SplitterContext();
SplitterConfig config=new SplitterConfig();
// 固定字符切割（每段 500 字符，重叠 50）
TextSplitter splitter = context.getTextSplitter(SplitterType.FIXED_CHAR, config);
SplitResult result = splitter.split(documentContent);

// 语义切割
TextSplitter semantic = context.getTextSplitter(SplitterType.SEMANTIC, config);
SplitResult semResult = semantic.split(documentContent);

// 标题切割
TextSplitter title = context.getTextSplitter(SplitterType.TITLE, config);

// 通过策略上下文选择切割器（AI 切割需通过 SplitterConfig 传入 llmContext）
SplitterConfig config = SplitterConfig.builder().llmContext(llmContext).build();
TextSplitter aiSplitter = context.getTextSplitter(SplitterType.AI, config);
SplitResult aiResult = aiSplitter.split(documentContent);
```

#### 4. 完整流程：提取 → AI 切割 → 向量化 → 检索

```java
@Autowired
private LLMContext llmContext;
// 1. 提取
DocumentExtractor extractor = new PdfBoxDocumentExtractor();
ExtractResult extractResult = extractor.extract(new ClassPathResource("document/doc.pdf"));
DocumentContent content = extractResult.getContent();

// 2. AI 切割（textSplitter 由 Spring 自动装配，按 yml 配置调用 Qwen/GPT/GLM 等）
SplitterConfig config = SplitterConfig.builder().llmContext(llmContext).build();
TextSplitter aiSplitter = context.getTextSplitter(SplitterType.AI, config);
SplitResult aiResult = aiSplitter.split(documentContent);

// 3. 转 LangChain4j TextSegment 并向量化
List<TextSegment> segments = splitResult.toLangChain4jTextSegments();
EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
        .apiKey(System.getenv("QWEN_API_KEY"))
        .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
        .modelName("text-embedding-v3")
        .build();

// Qwen text-embedding-v3 单批上限 10，需分批向量化
List<Embedding> embeddings = new ArrayList<>();
int batchSize = 10;
for(int i = 0; i <segments.size();i+=batchSize){
    List<TextSegment> batch = segments.subList(i, Math.min(i + batchSize, segments.size()));
    embeddings.addAll(embeddingModel.embedAll(batch).content());
}

InMemoryEmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();
store.addAll(embeddings, segments);

// 4. 语义搜索
Embedding queryEmbedding = embeddingModel.embed("查询内容").content();
EmbeddingSearchResult<TextSegment> searchResult = store.search(
        EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(3)
                .minScore(0.25)
                .build());
```

完整可运行示例见 `src/test/java/com/wdg/text_slicing_tool/LangChain4jInMemoryEmbeddingTest.java`。

## 设计要点

### 策略模式

- **AI 模型策略**：`LLMContext` 通过策略模式管理不同框架（Spring AI / LangChain4j）和不同厂商（OpenAI / Qwen / GLM / DeepSeek ...）的模型实现。当上下文仅注册一个模型时，`AiSplitter(LLMContext)` 会自动解析该模型作为默认策略，无需显式指定 type。
- **切割器策略**：`SplitterContext` 根据 `SplitterType` 选择对应的切割器实现，AI 切割通过 `SplitterConfig.llmContext` 传入策略上下文。

### 双框架对称设计

`SpringAiOpenAiModel(ChatClient, AiType)` 与 `LangChain4jOpenAiModel(ChatModel, AiType)` 形成对称设计：同一 `AiType` 在两个框架下分别由这两个类承载，自动装配时按 `framework/type` 选择其一。任意 `AiType` 均可工作，无 "unsupported" 分支。

### Spring Boot 自动装配

通过 `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` 注册三个自动配置类：

| 配置类 | 作用 | 生效条件 |
|--------|------|----------|
| `AiAutoConfiguration` | 注册 `LLMContext` | 总是生效（可 exclude 关闭） |
| `SpringAiAutoConfiguration` | 构建 `OpenAiChatModel` + `ChatClient` + `LlmModel` | `framework=spring-ai`（默认） |
| `LangChain4jAutoConfiguration` | 构建 LangChain4j `OpenAiChatModel` + `LlmModel` | `framework=langchain4j` |

如需完全关闭 AI 自动装配，可使用 `spring.autoconfigure.exclude=com.text_slicing_tool.ai.config.AiAutoConfiguration`。

### 与 LangChain4j 深度集成

- `TextChunk` 内置 `toLangChain4jTextSegment()` 和 `fromLangChain4jTextSegment()` 双向转换
- `SplitResult` 内置 `toLangChain4jTextSegments()` 和 `toLangChain4jDocuments()` 批量转换
- 元数据会自动映射，支持 LangChain4j 的 Metadata 格式

### 文本保护机制

切割时自动保护 URL、邮箱、版本号、文件路径等特殊文本，防止被分隔符误切，确保分片语义完整性。

### 中文优化

- 中文句子边界识别（支持 。！？； 等标点）
- 语义切割器识别中文主题边界词（"因此""所以""总结""综上"等）
- 标题切割器支持中文编号标题（"一、""1." "（一）"等）

## 测试

```bash
# 运行所有测试
mvn test

# 运行 AI 切割 + 向量检索集成测试（需 QWEN_API_KEY 环境变量）
# Windows PowerShell
$env:QWEN_API_KEY="sk-xxx"; mvn test -Dtest=LangChain4jInMemoryEmbeddingTest
```

> 注：项目 pom 目标为 Java 21，构建请使用 JDK 21（Lombok 1.18.48 暂不支持 JDK 25+）。
## TODO

### 文本提取

- [ ] 支持 Word (.docx) 文档提取
- [ ] 支持 Excel (.xlsx) 表格提取
- [ ] 支持 HTML 提取
- [ ] 支持图片中表格结构化提取（OCR + 表格识别）
- [ ] 添加提取器 SPI 机制，支持自定义扩展

### 文本切割

- [ ] 支持递归字符切割（RecursiveCharacterTextSplitter）
- [ ] 支持 Token 长度切割（适配不同模型的 Tokenizer）
- [ ] 语义切割器引入 Embedding 相似度断点检测
- [ ] 支持代码切割（按函数/类边界）
- [ ] 保护规则可配置化（正则表达式从外部传入）

### AI 集成

- [ ] 支持 Embedding 模型配置（当前仅限 chat 模型）
- [ ] AI 切割结果缓存（相同文本避免重复调用）
- [ ] AI 文本提取器（AiDocumentExtractor）回补并接入自动装配

### 性能与规模

- [ ] 大文件分片处理（避免 OOM）
- [ ] 并行提取与切割
- [ ] 切割结果流式返回（Reactive）
- [ ] 支持自定义向量库集成（Redis / Milvus / PGVector）
