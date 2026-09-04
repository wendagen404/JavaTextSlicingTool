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

    /**
     * 根据切割类型获取具体切割器。
     *
     * @param type 切割类型
     * @return 文本切割器
     */
    public TextSplitter getTextSplitter(SplitterType type, SplitterConfig config) {
        return switch (type) {
            case AI -> new AiSplitter(config.getLlmContext());
            case TITLE -> new TitleSplitter();
            case SEMANTIC -> new SemanticSplitter();
            case PARAGRAPH -> new ParagraphSplitter();
            case CHINESE_CHAR -> new ChineseCharSplitter();
            case CUSTOM_SYMBOL -> new CustomSymbolSplitter(config.getCharVal());
            default -> new FixeCharSplitter();
        };
    }
}
