package com.text_slicing_tool.enums;

/**
 * 文本切割策略类型。
 */
public enum SplitterType {
    /**
     * 固定字符长度切割。
     */
    FIXED_CHAR("fixed-char","固定字符长度切割"),

    /**
     * 中文句子或中文标点切割。
     */
    CHINESE_CHAR("chinese-char","中文句子或中文标点切割"),

    /**
     * 自然段切割。
     */
    PARAGRAPH("paragraph","自然段切割"),

    /**
     * 标题切割。
     */
    TITLE("title","标题切割"),

    /**
     * 自定义符号切割。
     */
    CUSTOM_SYMBOL("custom-symbol","自定义符号切割"),

    /**
     * 语义切割。
     */
    SEMANTIC("semantic","语义切割"),

    /**
     * AI 切割。
     */
    AI("ai","AI切割");

    private final String code;
    private final String desc;

    SplitterType(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * 获取适合写入结果 metadata 或 strategy 字段的策略编码。
     *
     * @return 策略编码
     */
    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
