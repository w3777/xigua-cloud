package com.xigua.ai.tool.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ToolCall
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/18 18:44
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ToolCall {
    private String toolName;
    private ToolRequest request;
}
