package com.xigua.ai.tool;

import com.xigua.ai.tool.model.ToolRequest;
import com.xigua.ai.tool.model.ToolResult;

/**
 * @ClassName AITool
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/15 19:43
 */
public interface AITool {
    /**
     * 工具名称
     */
    String name();

    /**
     * 工具描述
     */
    String description();

    /**
     * 执行
     */
    ToolResult execute(ToolRequest request);
}
