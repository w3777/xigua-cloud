package com.xigua.ai.agent.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName AgentContext
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/18 19:06
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AgentContext {
    private String input;
    private Boolean stream;
}
