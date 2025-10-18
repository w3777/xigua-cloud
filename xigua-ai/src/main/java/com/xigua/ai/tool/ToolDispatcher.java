package com.xigua.ai.tool;

import com.xigua.ai.tool.model.ToolCall;
import com.xigua.ai.tool.model.ToolRequest;
import com.xigua.ai.tool.model.ToolResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName ToolDispatcher
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/18 18:37
 */
@Slf4j
@Component
public class ToolDispatcher {
    @Autowired
    private ToolRegistry toolRegistry;

    public ToolResult dispatch(ToolCall call) {
        AITool tool = toolRegistry.getTool(call.getToolName());
        if (tool == null) {
            throw new IllegalArgumentException("未知工具：" + call.getToolName());
        }
        return safeExecute(tool, call.getRequest());
    }

    public List<ToolResult> dispatchBatch(List<ToolCall> toolCalls) {
        return toolCalls.parallelStream()
                .map(this::dispatch)
                .collect(Collectors.toList());
    }

    private ToolResult safeExecute(AITool tool, ToolRequest request) {
        long start = System.currentTimeMillis();
        try {
            ToolResult result = tool.execute(request);
            result.setLatency(System.currentTimeMillis() - start);
            result.setToolName(tool.name());
            return result;
        } catch (Exception e) {
            log.error("工具执行异常：{}", tool.name(), e);
            return ToolResult.fail(tool.name(), e.getMessage());
        }
    }
}
