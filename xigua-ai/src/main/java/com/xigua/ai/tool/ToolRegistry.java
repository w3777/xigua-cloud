package com.xigua.ai.tool;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ToolRegistry
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/15 19:50
 */
@Component
public class ToolRegistry {
    private final Map<String, AITool> tools = new HashMap<>();

    public ToolRegistry(List<AITool> tools) {
        tools.forEach(this::register);
    }

    public void register(AITool tool) {
        tools.put(tool.name(), tool);
    }

    public AITool getTool(String name) {
        return tools.getOrDefault(name, null);
    }
}
