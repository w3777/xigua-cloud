package com.xigua.ai.agent;

import com.xigua.ai.agent.model.AgentContext;
import reactor.core.publisher.Flux;

/**
 * @ClassName Agent
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/18 19:03
 */
public interface Agent {
    /**
     * 执行一次智能任务（完整的感知→推理→执行→输出流程）
     */
    Flux<String> process(AgentContext agentContext);
}
