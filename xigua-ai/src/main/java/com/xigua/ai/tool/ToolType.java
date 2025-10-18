package com.xigua.ai.tool;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum ToolType {
    WEATHER("weather", "获取天气信息"),
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
}
