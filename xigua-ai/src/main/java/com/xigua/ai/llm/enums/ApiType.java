package com.xigua.ai.llm.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

/**
 * @ClassName ApiType
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/20 14:48
 */
@ToString
@AllArgsConstructor
public enum ApiType {
    OPEN_AI("openai"),
    ;

    final String type;

    public String getType() {
        return type;
    }
}
