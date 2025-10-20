package com.xigua.ai.tool;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum ToolType {
    WEATHER("weather", "获取天气信息"),
    WEB_SEARCH("web_search", "网络搜索"),
    ;

    private final String name;
    private final String description;

    /**
     * 获取工具名称
     */
    public String getName() {
        return name;
    }

    /**
     * 获取工具描述
     */
    public String getDescription() {
        return description;
    }

    /**
     * 根据工具名称获取工具类型
     */
    public static ToolType getByToolName(String name) {
        for (ToolType toolType : ToolType.values()) {
            if (toolType.getName().equalsIgnoreCase(name)) {
                return toolType;
            }
        }
        return null;
    }
}
