package com.text_slicing_tool.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    /**
     * 按字面字符串分割
     */
    public static String[] splitByWholeSeparator(String source, String separator) {
        if (source == null || separator == null || separator.isEmpty()) {
            return new String[]{source};
        }
        List<String> result = new ArrayList<>();
        int offset = 0;
        int sepLen = separator.length();
        int idx;
        while ((idx = source.indexOf(separator, offset)) != -1) {
            result.add(source.substring(offset, idx) + separator);
            offset = idx + sepLen;
        }
        result.add(source.substring(offset));
        return result.toArray(new String[0]);
    }

}
