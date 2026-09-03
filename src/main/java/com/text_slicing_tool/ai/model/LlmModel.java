package com.text_slicing_tool.ai.model;

import com.text_slicing_tool.enums.AiFramework;
import com.text_slicing_tool.enums.AiType;
import com.text_slicing_tool.pojo.DocumentContent;
import com.text_slicing_tool.pojo.ExtractResult;
import com.text_slicing_tool.pojo.SplitResult;
import org.springframework.core.io.ClassPathResource;

/**
 * 大语言模型策略接口。
 */
public interface LlmModel {
    /**
     * 返回当前策略支持的 AI 调用框架。
     *
     * @return AI 调用框架
     */
    AiFramework supportFramework();

    /**
     * 返回当前策略支持的模型厂商类型。
     *
     * @return 模型厂商类型
     */
    AiType supportType();

    /**
     * 使用 AI 对文档内容进行切割。
     *
     * @param documentContent 待切割文档内容
     * @return AI 切割结果
     */
    SplitResult doSplitter(DocumentContent documentContent);

    /**
     * 使用 AI 对文档资源进行文本提取。
     *
     * @param resource 待解析资源
     * @return AI 提取结果
     */
    ExtractResult doExtractor(ClassPathResource resource);
}
