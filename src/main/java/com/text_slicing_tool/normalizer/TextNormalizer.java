package com.text_slicing_tool.normalizer;

import lombok.Builder;

@Builder
public class TextNormalizer {
    /** 是否开启整体预处理，总开关 */
    @Builder.Default
    private boolean enableNormalize = true;

    /** 多个换行、空白字符压缩合并 */
    @Builder.Default
    private boolean compressWhitespace = true;

    /** 移除不可见控制字符 \u0000‑\u001F */
    @Builder.Default
    private boolean removeInvisibleControlChar = true;

    /** 过滤完全空白行 */
    @Builder.Default
    private boolean filterBlankLine = true;

    /** PDF提取：合并被换行打断的中文句子（中文后紧跟换行，直接删掉换行符） */
    @Builder.Default
    private boolean mergePdfBrokenChineseSentence = true;

    /** 简单规则移除页眉页脚：过滤文档中重复出现的行 */
    @Builder.Default
    private boolean removeDuplicateHeaderFooter = false;

    /** 过滤页码行（匹配 第X页 / 页码数字行） */
    @Builder.Default
    private boolean filterPageNumberLine = false;

    /** 去除特殊符号（非文本常用符号） */
    @Builder.Default
    private boolean removeSpecialSymbol = false;

    /** 繁体转简体，可选扩展 */
    @Builder.Default
    private boolean traditionalToSimplified = false;


}
