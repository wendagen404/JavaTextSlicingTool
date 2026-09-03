package com.text_slicing_tool.enums;

/**
 * AI 厂商类型枚举。
 */
public enum AiType {
    OPENAI("openai"),
    QWEN("qwen"),
    GLM("glm"),
    DEEPSEEK("deepseek"),
    OLLAMA("ollama"),
    BAIDU_QIANFAN("baidu-qianfan"),
    MINIMAX("minimax");

    private final String vendorCode;

    AiType(String vendorCode) {
        this.vendorCode = vendorCode;
    }

    /**
     * 获取厂商编码。
     *
     * @return 厂商编码
     */
    public String getVendorCode() {
        return vendorCode;
    }
}
