package com.xigua.ai.agent;

import com.xigua.ai.agent.model.AgentContext;
import com.xigua.ai.context.ChatContext;
import com.xigua.ai.context.Prompt;
import com.xigua.ai.intent.IntentRecognizer;
import com.xigua.ai.intent.IntentType;
import com.xigua.ai.llm.LLMService;
import com.xigua.ai.sse.StreamCallback;
import com.xigua.ai.tool.ToolDispatcher;
import com.xigua.ai.tool.ToolType;
import com.xigua.ai.tool.model.ToolCall;
import com.xigua.ai.tool.model.ToolRequest;
import com.xigua.ai.tool.model.ToolResult;
import com.xigua.ai.utils.PromptUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * @ClassName SimpleAgent
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/18 19:04
 */
@Service
public class SimpleAgent implements Agent {
    @Autowired
    private LLMService llmService;
    @Autowired
    private IntentRecognizer intentRecognizer;
    @Autowired
    private ToolDispatcher toolDispatcher;

    @Override
    public Flux<String> process(AgentContext agentContext) {
        Flux<String> result = Flux.empty();
        Boolean stream = agentContext.getStream();

        IntentType intentType = intentRecognizer.detect(agentContext.getInput());

        switch (intentType) {
            case UNKNOWN:
                return Flux.just("未知意图");
            case TEXT_TO_TEXT:
                ChatContext chatContext = ChatContext.builder()
                        .prompt(PromptUtil.getPrompt(Prompt.DEFAULT))
                        .input(agentContext.getInput())
                        .stream(agentContext.getStream())
                        .build();
                try {
                    result = stream ? adaptStream(chatContext) : Flux.just(llmService.chat(chatContext));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                break;
            case USE_TOOL:
                // todo llm推理调用工具名称和参数

                // 调用工具
                ToolCall toolCall = ToolCall.builder()
                        .toolName(ToolType.WEATHER.getName())
                        .request(ToolRequest.builder()
                                .params(Map.of("city", "北京"))
                                .build())
                        .build();
                ToolResult toolResult = toolDispatcher.dispatch(toolCall);
                // todo 调用结果 再根据llm推理处理一下
                result = Flux.just(toolResult.getOutput());
                break;
            default:
                return Flux.just("未知意图");
        }

        return result;
    }

    private Flux<String> adaptStream(ChatContext chatContext) {
        return Flux.create(sink -> {
            chatContext.setStreamCallback(new StreamCallback() {
                @Override
                public void onMessage(String content) {
                    sink.next(content);
                }

                @Override
                public void onComplete() {
                    sink.complete();
                }

                @Override
                public void onError(Throwable t) {
                    sink.error(t);
                }
            });

            llmService.chatStream(chatContext);
        });
    }
}
