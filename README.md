# Text Slicing Tool

Java 文本提取与切片工具库，专为中文 RAG（检索增强生成）场景设计，可直接集成到 Spring Boot 项目中。

## 项目背景

在 RAG 场景中，文档入库前通常需要先完成 **文本提取**（从 PDF、图片、Markdown 等格式中提取纯文本），再进行 **文本切片**（将长文本按语义或规则切分为适合向量化的片段）。当前 Java 生态中缺少一个面向中文文档、开箱即用且可直接集成到 Spring Boot RAG 项目的独立工具库。

本项目实现了两层核心能力：

- **文本提取层**：统一接口适配 PDFBox、OCR、AI 模型等多种解析方式
- **文本切片层**：提供多种切分策略，包括固定字符、自然段、标题、语义、AI 智能切割等

目标让上层业务只关心"输入什么文档、输出什么文本块"，而不关心解析与切分细节。

## 项目结构

```
src/main/java/com/text_slicing_tool/
├── TextSlicingToolApplication.java        # Spring Boot 启动入口
├── ai/
│   ├── LLMContext.java                    # AI 模型策略上下文（策略模式）
│   └── model/
│       ├── LlmModel.java                  # 大语言模型策略接口
│       ├── AbstractAiModel.java           # 模型策略公共基类（提示词/结果解析）
│       ├── AbstractLangChain4jModel.java  # LangChain4j 模型基类
│       ├── AbstractSpringAiModel.java     # Spring AI 模型基类
│       ├── LangChain4jOpenAiModel.java    # LangChain4j + OpenAI 实现
│       ├── GPT.java                       # Spring AI + OpenAI GPT 实现
│       ├── Qwen.java                      # Spring AI + 通义千问实现
│       └── GLM.java                       # Spring AI + 智谱 GLM 实现
├── config/                                # Spring 配置类（预留）
├── enums/
│   ├── AiFramework.java                   # AI 框架枚举（Spring AI / LangChain4j）
│   ├── AiType.java                        # AI 厂商枚举（OpenAI / Qwen / GLM 等）
│   └── SplitterType.java                  # 切割策略枚举
├── extractor/
│   ├── DocumentExtractor.java             # 文档提取器接口
│   └── impl/
│       ├── TextExtractor.java             # TXT 文本提取
│       ├── MarkdownExtractor.java         # Markdown 解析提取（基于 commonmark）
│       ├── PdfBoxDocumentExtractor.java   # PDF 文本提取（基于 PDFBox）
│       ├── OcrDocumentExtractor.java      # OCR 图片/PDF 文本提取（基于 Tesseract）
│       └── AiDocumentExtractor.java       # AI 模型文本提取
├── normalizer/
│   └── TextNormalizer.java                # 文本规范化（合并空白、过滤控制字符、繁体转简体等）
├── pojo/
│   ├── DocumentContent.java               # 文档提取后的正文内容
│   ├── ExtractResult.java                 # 文本提取结果
│   ├── AiMessageResult.java              # AI 模型调用统一结果
│   ├── TextChunk.java                     # 文本切片（含 LangChain4j 互转方法）
│   └── SplitResult.java                   # 文本切割结果（含 LangChain4j 互转方法）
├── splitter/
│   ├── TextSplitter.java                  # 文本切割器接口
│   ├── SplitterContext.java               # 切割器策略上下文
│   ├── Test.java                          # 快速测试入口
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
├── application.yaml
├── document/                              # 测试文档样本
│   ├── test.txt
│   ├── Java.md
│   ├── *.pdf                              # 多个面试题 PDF
│   └── *.png / *.jpg                      # 图片测试文件
└── tessdata/                              # Tesseract OCR 语言包
```

## 技术栈

| 组件 | 说明 |
|------|------|
| Java 21 | 语言版本 |
| Spring Boot 4.1.1 | 框架基础 |
| PDFBox 3.0.8 | PDF 文本提取 |
| Tesseract (Tess4J) 5.20.0 | OCR 图片文字识别 |
| CommonMark 0.21.0 | Markdown 解析 |
| LangChain4j 1.19.0 | LLM 集成框架 |
| Spring AI 2.0.1 | LLM 集成框架 |
| Lombok 1.18.48 | 简化 POJO |
| Hutool 5.8.47 | 通用工具库 |

## 核心功能

### 文本提取

支持从多种文档格式中提取纯文本：

| 提取器 | 输入格式 | 说明 |
|--------|----------|------|
| TextExtractor | .txt | 纯文本文件读取 |
| MarkdownExtractor | .md | 基于 CommonMark 解析，保留标题、段落、表格、代码块等结构 |
| PdfBoxDocumentExtractor | .pdf | 使用 PDFBox 提取文字层文本 |
| OcrDocumentExtractor | .png/.jpg/.pdf | 使用 Tesseract OCR 识别图片文字，适用于扫描件 |
| AiDocumentExtractor | 任意文本 | 调用 LLM 进行智能提取，支持多种模型厂商 |

### 文本切割

提供 7 种切割策略，均通过 TextSplitter 统一接口调用：

| 切割器 | 策略说明 | 适用场景 |
|--------|----------|----------|
| FixeCharSplitter | 固定字符长度，支持重叠分片 | 通用场景，快速均匀分片 |
| ChineseCharSplitter | 按中文/英文句末标点断句 | 句子级切割，适合问答 |
| ParagraphSplitter | 按连续空行识别自然段 | 段落结构清晰的文档 |
| TitleSplitter | 根据 Markdown/Markdown 风格标题行切割 | 章节结构明确的文档 |
| CustomSymbolSplitter | 按自定义分隔符切割，保护 URL/邮箱/版本号 | 特定格式文本 |
| SemanticSplitter | 启发式语义切割，聚合段落、识别主题边界 | 需要语义完整性的场景 |
| AiSplitter | 调用 LLM 智能切割 | 高质量切分，适合 RAG 入库 |

### 向量化集成

所有 TextChunk 和 SplitResult 均内置与 LangChain4j 的互转方法：

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
- （可选）OpenAI API Key 或其他 LLM 厂商 API Key

### 引入依赖

当前项目是 Spring Boot 应用，可直接作为依赖引入。核心依赖在 pom.xml 中已配置。

### 使用示例

#### 1. 文本提取

```java
// 从 PDF 提取文本
DocumentExtractor pdfExtractor = new PdfBoxDocumentExtractor();
ExtractResult result = pdfExtractor.extract(new ClassPathResource("document/example.pdf"));
String text = result.getContent().getText();

// 从 Markdown 提取文本
DocumentExtractor mdExtractor = new MarkdownExtractor();
ExtractResult result = mdExtractor.extract(new ClassPathResource("document/example.md"));
```

#### 2. 文本切割

```java
// 创建切割器上下文
SplitterContext context = new SplitterContext();

// 使用固定字符切割（每段 500 字符，重叠 50 字符）
TextSplitter splitter = new FixeCharSplitter("doc-001", 500, 50);
SplitResult result = splitter.split(documentContent);

// 使用语义切割
TextSplitter splitter = new SemanticSplitter("doc-001", 800, 80);
SplitResult result = splitter.split(documentContent);

// 使用标题切割
TextSplitter splitter = new TitleSplitter("doc-001");
SplitResult result = splitter.split(documentContent);

// 通过策略上下文选择切割器
SplitterContext context = new SplitterContext();
TextSplitter splitter = context.getTextSplitter(SplitterType.SEMANTIC);
SplitResult result = splitter.split(documentContent);

// 遍历分片
result.getChunks().forEach(chunk -> {
    System.out.println(chunk.getContent());
    System.out.println("偏移: " + chunk.getStartOffset() + "-" + chunk.getEndOffset());
});
```

#### 3. AI 切割

```java
// 创建 LLM 上下文（需要 Spring 注入 ChatModel/ChatClient）
LLMContext llmContext = new LLMContext(models);

// 创建 AI 切割器
SplitterContext context = new SplitterContext(llmContext);
TextSplitter splitter = context.getTextSplitter(SplitterType.AI);
SplitResult result = splitter.split(documentContent);
```

#### 4. 完整流程：提取 → 切割 → 向量化

```java
// 1. 提取
DocumentExtractor extractor = new PdfBoxDocumentExtractor();
ExtractResult extractResult = extractor.extract(new ClassPathResource("document/doc.pdf"));
DocumentContent content = extractResult.getContent();

// 2. 切割
TextSplitter splitter = new SemanticSplitter(content.getSourceId(), 800, 80);
SplitResult splitResult = splitter.split(content);

// 3. 转为 LangChain4j TextSegment，供向量化入库
List<TextSegment> segments = splitResult.toLangChain4jTextSegments();

// 4. 向量化并存入内存向量库（需要 OPENAI_API_KEY 环境变量）
EmbeddingModel embeddingModel = OpenAiEmbeddingModel.builder()
        .apiKey(System.getenv("OPENAI_API_KEY"))
        .modelName("text-embedding-3-small")
        .build();

List<Embedding> embeddings = embeddingModel.embedAll(segments).content();

InMemoryEmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();
store.addAll(embeddings, segments);

// 5. 语义搜索
Embedding queryEmbedding = embeddingModel.embed("查询内容").content();
List<EmbeddingMatch<TextSegment>> matches = store.findRelevant(queryEmbedding, 3);
```

## 设计要点

### 策略模式

- **AI 模型策略**：LLMContext 通过策略模式管理不同框架（Spring AI / LangChain4j）和不同厂商（OpenAI / Qwen / GLM）的模型实现，支持灵活切换和扩展
- **切割器策略**：SplitterContext 根据 SplitterType 选择对应的切割器实现

### 与 LangChain4j 深度集成

- TextChunk 内置 	oLangChain4jTextSegment() 和 romLangChain4jTextSegment() 双向转换
- SplitResult 内置 	oLangChain4jTextSegments() 和 	oLangChain4jDocuments() 批量转换
- 元数据会自动映射，支持 LangChain4j 的 Metadata 格式

### 文本保护机制

切割时自动保护 URL、邮箱、版本号、文件路径等特殊文本，防止被分隔符误切，确保分片语义完整性。

### 中文优化

- 中文句子边界识别（支持 。！？； 等标点）
- 语义切割器识别中文主题边界词（"因此""所以""总结""综上"等）
- 标题切割器支持中文编号标题（"一、""1." "（一）"等）

## 配置

```yaml
spring:
  application:
    name: text_slicing_tool
```

AI 模型配置（通过 Spring AI / LangChain4j 各自的配置属性）：

```yaml
# Spring AI OpenAI
spring:
  ai:
    openai:
      api-key: 
      chat:
        options:
          model: gpt-4o-mini

# LangChain4j OpenAI
langchain4j:
  open-ai:
    api-key: 
    model-name: gpt-4o-mini
```

## 测试

```ash
# 运行所有测试
mvn test

# 运行单测（需 OPENAI_API_KEY 环境变量）
OPENAI_API_KEY=sk-xxx mvn test -Dtest=LangChain4jInMemoryEmbeddingTest
```

## TODO

### 文本提取

- [ ] 支持 Word (.docx) 文档提取
- [ ] 支持 Excel (.xlsx) 表格提取
- [ ] 支持 HTML 提取
- [ ] 支持 图片中表格结构化提取（OCR + 表格识别）
- [ ] AiDocumentExtractor 支持 PDF/图片自动降级（PDF 文字层不足时自动切到 OCR）
- [ ] 添加提取器 SPI 机制，支持自定义扩展

### 文本切割

- [ ] 支持递归字符切割（RecursiveCharacterTextSplitter）
- [ ] 支持 Token 长度切割（适配不同模型的 Tokenizer）
- [ ] 语义切割器引入 Embedding 相似度断点检测
- [ ] 支持代码切割（按函数/类边界）
- [ ] 保护规则可配置化（正则表达式从外部传入）

### AI 集成

- [ ] 添加 DeepSeek 模型支持
- [ ] 添加 Ollama 本地模型支持
- [ ] 添加百度千帆模型支持
- [ ] 支持 Embedding 模型配置（当前仅限 chat 模型）
- [ ] AI 切割结果缓存（相同文本避免重复调用）

### 质量与工程

- [ ] 抽取为独立 Maven 模块，发布到私有仓库
- [ ] 提供 Spring Boot AutoConfiguration 自动配置

### 性能与规模

- [ ] 大文件分片处理（避免 OOM）
- [ ] 并行提取与切割
- [ ] 切割结果流式返回（Reactive）
- [ ] 支持自定义向量库集成（Redis / Milvus / PGVector）

