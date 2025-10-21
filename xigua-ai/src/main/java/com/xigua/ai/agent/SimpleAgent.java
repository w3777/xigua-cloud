package com.xigua.ai.agent;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.ai.agent.model.AgentContext;
import com.xigua.ai.llm.model.ChatContext;
import com.xigua.ai.enums.Prompt;
import com.xigua.ai.intent.IntentRecognizer;
import com.xigua.ai.intent.IntentType;
import com.xigua.ai.llm.LLMService;
import com.xigua.ai.sse.StreamCallback;
import com.xigua.ai.tool.ToolDispatcher;
import com.xigua.ai.tool.ToolSelector;
import com.xigua.ai.tool.ToolType;
import com.xigua.ai.tool.model.ToolCall;
import com.xigua.ai.tool.model.ToolRequest;
import com.xigua.ai.tool.model.ToolResult;
import com.xigua.ai.tool.model.ToolSelectionResult;
import com.xigua.ai.utils.PromptUtil;
import com.xigua.common.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.util.Map;

/**
 * @ClassName SimpleAgent
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/18 19:04
 */
@Slf4j
@Service
public class SimpleAgent implements Agent {
    @Autowired
    private LLMService llmService;
    @Autowired
    private IntentRecognizer intentRecognizer;
    @Autowired
    private ToolDispatcher toolDispatcher;
    @Autowired
    private ToolSelector toolSelector;

    @Override
    public Flux<String> process(AgentContext agentContext) {
        Flux<String> result = Flux.empty();
        String requestId = agentContext.getRequestId();
        Boolean stream = agentContext.getStream();
        String input = agentContext.getInput();

        IntentType intentType = intentRecognizer.detect(input);
        log.info("agentProcess requestId: {}，意图: {}", requestId, intentType);

        switch (intentType) {
            case UNKNOWN:
                return Flux.just("未知意图");
            case TEXT_TO_TEXT:
                String systemPrompt = agentContext.getSystemPrompt();
                if(StringUtils.isEmpty(systemPrompt)) {
                    systemPrompt = PromptUtil.getPrompt(Prompt.DEFAULT);
                }
                ChatContext chatContext = ChatContext.builder()
                        .prompt(systemPrompt)
                        .input(input)
                        .stream(stream)
                        .build();
                try {
                    log.info("agentProcess ai对话开始 requestId: {}，调用模型入参: {}", requestId, chatContext);
                    result = llmService.chat(chatContext);
                    log.info("agentProcess ai对话结束 requestId: {}，调用模型结束", requestId);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case USE_TOOL:
                // llm推理调用工具名称和参数
                ToolSelectionResult toolSelectionResult = toolSelector.selectTool(input);
                log.info("agentProcess requestId: {}，工具选择结果: {}", requestId, toolSelectionResult);
                if(toolSelectionResult == null){
                    return Flux.just("未选择到合适的工具");
                }

                // 调用工具
                ToolCall toolCall = ToolCall.builder()
                        .toolName(toolSelectionResult.getToolType().getName())
                        .request(ToolRequest.builder()
                                .params(toolSelectionResult.getParams())
                                .build())
                        .build();
                log.info("agentProcess requestId: {}，工具调用入参: {}", requestId, toolCall);
                ToolResult toolResult = toolDispatcher.dispatch(toolCall);
                log.info("agentProcess requestId: {}，工具调用结果: {}", requestId, toolResult);

                // tool调用结果 再根据llm推理处理一下
                log.info("agentProcess requestId: {}，大模型推理处理工具调用结果", requestId);
                Flux<String> refinedOutput = refineOutput(input, stream, toolResult);
                result = refinedOutput;
                break;
            default:
                return Flux.just("未知意图");
        }

        return result;
    }

    private Flux<String> refineOutput(String input, Boolean stream, ToolResult toolResult){
        Flux<String> refinedOutput = Flux.empty();
        String prompt = PromptUtil.getPrompt(Prompt.REFINE);
        prompt = PromptUtil.fill(prompt, Map.of(
                "user_input", input,
                "tool_name", toolResult.getToolName(),
                "tool_output", JSONObject.toJSONString(toolResult.getData())
        ));

        String refineInput = "这是工具调用后的结果，请根据它为用户生成自然语言回答：\n" + JSONObject.toJSONString(toolResult.getData());

        // 调用模型
        ChatContext chatContext = ChatContext.builder()
                .prompt(prompt)
                .input(refineInput)
                .stream(stream)
                .build();
        try {
            refinedOutput = llmService.chat(chatContext);
        } catch (Exception e) {
            log.error("调用模型失败, input: {}", input, e);
            throw new BusinessException("调用模型失败");
        }
        return refinedOutput;
    }
}
