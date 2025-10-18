package com.xigua.ai.tool;

import com.xigua.ai.tool.model.ToolRequest;
import com.xigua.ai.tool.model.ToolResult;

/**
 * @ClassName WeatherTool
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/15 19:52
 */
public class WeatherTool implements AITool {
    @Override
    public String name() {
        return "weather";
    }

    @Override
    public String description() {
        return "查询天气，参数为 city";
    }

    @Override
    public ToolResult execute(ToolRequest request) {
        String city = (String) request.getParams().get("city");
        String output = "今天" + city + "天气晴朗，28℃";
        return ToolResult.success(name(), output, null, null);
    }
}
