package com.xigua.ai.llm.enums;


import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum Provider {
    DEEPSEEK("deepseek"),
    ;

    final String name;

    public String getName() {
        return name;
    }
}
