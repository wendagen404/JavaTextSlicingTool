package com.text_slicing_tool.enums;

/**
 * AI 调用框架枚举，用于区分同一模型厂商在不同 Java AI 框架下的实现。
 */
public enum AiFramework {
    /**
     * Spring AI 调用框架。
     */
    SPRING_AI("spring-ai"),

    /**
     * LangChain4j 调用框架。
     */
    LANGCHAIN4J("langchain4j");

    private final String code;

    AiFramework(String code) {
        this.code = code;
    }

    /**
     * 获取框架编码。
     *
     * @return 框架编码
     */
    public String getCode() {
        return code;
    }
}
