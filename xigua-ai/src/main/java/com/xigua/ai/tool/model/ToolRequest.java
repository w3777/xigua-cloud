package com.xigua.ai.tool.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @ClassName ToolRequest
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/15 19:47
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToolRequest {
    private String input;
    private Map<String, Object> params;
}
