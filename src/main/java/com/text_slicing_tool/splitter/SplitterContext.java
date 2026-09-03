package com.text_slicing_tool.splitter;

import com.text_slicing_tool.ai.LLMContext;
import com.text_slicing_tool.enums.SplitterType;
import com.text_slicing_tool.pojo.SplitterConfig;
import com.text_slicing_tool.splitter.impl.AiSplitter;
import com.text_slicing_tool.splitter.impl.ChineseCharSplitter;
import com.text_slicing_tool.splitter.impl.CustomSymbolSplitter;
import com.text_slicing_tool.splitter.impl.FixeCharSplitter;
import com.text_slicing_tool.splitter.impl.ParagraphSplitter;
import com.text_slicing_tool.splitter.impl.SemanticSplitter;
import com.text_slicing_tool.splitter.impl.TitleSplitter;

/**
 * 文本切割器上下文，用于按切割类型选择具体切割策略。
 */
public class SplitterContext {
    private final LLMContext llmContext;

    /**
     * 创建只支持本地切割策略的上下文。
     */
    public SplitterContext() {
        this(null);
    }

    /**
     * 创建支持 AI 切割策略的上下文。
     *
     * @param llmContext AI 模型策略上下文
     */
    public SplitterContext(LLMContext llmContext) {
        this.llmContext = llmContext;
    }

    /**
     * 根据切割类型获取具体切割器。
     *
     * @param type 切割类型
     * @return 文本切割器
     */
    public TextSplitter getTextSplitter(SplitterType type, SplitterConfig config) {
        return switch (type) {
            case AI -> getAiSplitter();
            case TITLE -> new TitleSplitter();
            case SEMANTIC -> new SemanticSplitter();
            case PARAGRAPH -> new ParagraphSplitter();
            case CHINESE_CHAR -> new ChineseCharSplitter();
            case CUSTOM_SYMBOL -> new CustomSymbolSplitter(config.getCharVal());
            default -> new FixeCharSplitter();
        };
    }

    private TextSplitter getAiSplitter() {
        if (llmContext == null) {
            throw new IllegalStateException("AI 切割器需要传入 LLMContext");
        }
        return new AiSplitter(llmContext);
    }
}
