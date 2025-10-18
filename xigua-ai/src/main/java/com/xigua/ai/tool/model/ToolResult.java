package com.xigua.ai.tool.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @ClassName ToolResult
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/15 19:48
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ToolResult {
    private boolean success;
    private String output;
    private Long latency;
    private String toolName;
    private Map<String, Object> data;

    public static ToolResult success(String toolName, String output, Long latency, Map<String, Object> data) {
        return ToolResult.builder()
                .success(true)
                .output(output)
                .latency(latency)
                .toolName(toolName)
                .data(data)
                .build();
    }

    public static ToolResult fail(String toolName, String errorMessage) {
        return ToolResult.builder()
                .success(false)
                .output(errorMessage)
                .toolName(toolName)
                .build();
    }
}
