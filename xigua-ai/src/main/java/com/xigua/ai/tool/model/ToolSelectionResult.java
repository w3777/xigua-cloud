package com.xigua.ai.tool.model;

import com.xigua.ai.tool.ToolType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @ClassName ToolSelectionResult
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/19 17:27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(staticName = "of")
public class ToolSelectionResult {
    private ToolType toolType;
    private String args;
    private Map<String, Object> params;
}
