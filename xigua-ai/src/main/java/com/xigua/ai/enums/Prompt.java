package com.xigua.ai.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum Prompt {
    DEFAULT("prompts/system_prompt", "默认提示词"),
    DETECT_INTENT("prompts/detect_intent_prompt", "意图识别提示提"),
    TOOL_SELECTOR("prompts/tool_selector_prompt", "工具选择提示词"),
    REFINE("prompts/refine_prompt", "回答优化提示词"),
    ;

    private final String path;
    private final String desc;
     public String getPath() {
        return path;
    }
    public String getDesc() {
        return desc;
    }
}
