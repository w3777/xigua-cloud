package com.xigua.ai.intent;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum IntentType {
    UNKNOWN("UNKNOWN", "未知意图"),
    TEXT_TO_TEXT("TEXT_TO_TEXT", "文本到文本意图"),
    USE_TOOL("USE_TOOL", "使用工具意图");

    private final String type;
    private final String desc;
    public String getType() {
        return type;
    }
    public String getDesc() {
        return desc;
    }

    public static IntentType fromType(String type) {
        for (IntentType intentType : IntentType.values()) {
            if (intentType.getType().equals(type)) {
                return intentType;
            }
        }
        return UNKNOWN;
    }
}
