package com.xigua.ai.controller;

import com.xigua.ai.client.DeepSeekClient;
import com.xigua.ai.openai.ChatCompletionRequest;
import com.xigua.ai.openai.ChatCompletionResponse;
import com.xigua.ai.service.AIServiceImpl;
import com.xigua.ai.sse.StreamCallback;
import com.xigua.ai.tool.WebSearchTool;
import com.xigua.ai.tool.model.ToolRequest;
import com.xigua.api.service.*;
import com.xigua.domain.result.R;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName AITestController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/21 16:57
 */
@Tag(name = "ai测试接口")
@RequestMapping("/test")
@RestController
public class AITestController {
    @Autowired(required = false)
    private DeepSeekClient deepSeekClient;
    @DubboReference
    private AIService aiService;
    @Autowired
    private WebSearchTool webSearchTool;

    @ApiResponses({@ApiResponse(responseCode = "200", description = "查询成功", content =
            { @Content(mediaType = "application/json") })})
    @Operation(summary = "测试接口")
    @PostMapping("/testChat")
    public R<ChatCompletionResponse> testChat() throws IOException {
        ChatCompletionRequest request = new ChatCompletionRequest();

        // 设置基本参数
        request.setModel("deepseek-chat");
        request.setFrequencyPenalty(0.0);
        request.setMaxTokens(4096);
        request.setPresencePenalty(0.0);
        request.setStream(false);
        request.setStreamOptions(null);
        request.setTemperature(1.0);
        request.setTopP(1.0);
        request.setTools(null);
        request.setToolChoice("none");
        request.setLogprobs(false);
        request.setTopLogprobs(null);
        request.setStop(null);

        // 设置响应格式
        ChatCompletionRequest.ResponseFormat responseFormat =
                new ChatCompletionRequest.ResponseFormat();
        responseFormat.setType("text");
        request.setResponseFormat(responseFormat);

        // 设置消息列表
        ChatCompletionRequest.Message systemMessage = new ChatCompletionRequest.Message();
        systemMessage.setRole("system");
        systemMessage.setContent("You are a helpful assistant");

        ChatCompletionRequest.Message userMessage = new ChatCompletionRequest.Message();
        userMessage.setRole("user");
        userMessage.setContent("Hi");

        request.setMessages(List.of(systemMessage, userMessage));

        ChatCompletionResponse response = deepSeekClient.chatCompletions(request);
        return R.ok(response);
    }

    @Operation(summary = "测试流式")
    @PostMapping("/testChatStream")
    public void testChatStream() throws IOException {
        ChatCompletionRequest request = new ChatCompletionRequest();

        // 设置基本参数
        request.setModel("deepseek-chat");
        request.setFrequencyPenalty(0.0);
        request.setMaxTokens(4096);
        request.setPresencePenalty(0.0);
        request.setStream(true);
        request.setStreamOptions(null);
        request.setTemperature(1.0);
        request.setTopP(1.0);
        request.setTools(null);
        request.setToolChoice("none");
        request.setLogprobs(false);
        request.setTopLogprobs(null);
        request.setStop(null);

        // 设置响应格式
        ChatCompletionRequest.ResponseFormat responseFormat =
                new ChatCompletionRequest.ResponseFormat();
        responseFormat.setType("text");
        request.setResponseFormat(responseFormat);

        // 设置消息列表
        ChatCompletionRequest.Message systemMessage = new ChatCompletionRequest.Message();
        systemMessage.setRole("system");
        systemMessage.setContent("You are a helpful assistant");

        ChatCompletionRequest.Message userMessage = new ChatCompletionRequest.Message();
        userMessage.setRole("user");
        userMessage.setContent("Hi");

        request.setMessages(List.of(systemMessage, userMessage));

        deepSeekClient.chatCompletionsStream(request, new StreamCallback() {
            @Override
            public void onMessage(String content) {
                System.out.println(content);
            }

            @Override
            public void onError(Throwable t) {
                t.printStackTrace();
            }

            @Override
            public void onComplete() {
                System.out.println("Stream completed");
            }
        });
    }

    @Operation(summary = "测试ai对话")
    @PostMapping(value = "/test3", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> test3(@RequestBody Map<String, String> map) {
        String input = map.getOrDefault("input", "你是谁");
        boolean stream = Boolean.parseBoolean(map.getOrDefault("stream", "true"));
        try {
            Mono<ChatRequest> request = Mono.just(
                    ChatRequest.newBuilder()
                            .setInput(input)
                            .setStream(stream)
                            .build()
            );
            Flux<ChatResponse> chat = aiService.chat(request);
            Flux<String> stringFlux = chat.map(ChatResponse::getOutput);
            return stringFlux;
        } catch (Exception e) {
            return Flux.error(e);
        }
    }

    @Operation(summary = "识别用户意图")
    @PostMapping(value = "/test4")
    public R<Map<String, Object>> test4(@RequestBody Map<String, String> map) {
        String input = map.getOrDefault("input", "你好啊");
        try {
            Mono<IntentRequest> request = Mono.just(
                    IntentRequest.newBuilder()
                            .setInput(input)
                            .build()
            );
            Mono<IntentResponse> intentResponseMono = aiService.detectIntent(request);
            IntentResponse intentResponse = intentResponseMono.block();
            Map<String, Object> result = new HashMap<>();
            result.put("intent", intentResponse.getIntent());
            return R.ok(result);
        } catch (Exception e) {
            return R.fail(e.getMessage());
        }
    }


    @Operation(summary = "测试agent")
    @PostMapping(value = "/test5", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> test5(@RequestBody Map<String, String> map) {
        String input = map.getOrDefault("input", "北京今天天气怎么样");
        boolean stream = Boolean.parseBoolean(map.getOrDefault("stream", "true"));
        try {
            Mono<AgentRequest> request = Mono.just(
                    AgentRequest.newBuilder()
                            .setInput(input)
                            .setStream(stream)
                            .build()
            );
            Flux<AgentResponse> chat = aiService.agentProcess(request);
            Flux<String> stringFlux = chat.map(AgentResponse::getOutput);
            return stringFlux;
        } catch (Exception e) {
            return Flux.error(e);
        }
    }

    @Operation(summary = "测试")
    @PostMapping(value = "/test6")
    public R test6(@RequestBody Map<String, String> map) {
        String content = map.getOrDefault("content", "河北各个城市最近的天气");
        ToolRequest toolRequest = new ToolRequest();
        toolRequest.setParams(Map.of("content", content));
        webSearchTool.execute(toolRequest);

        return R.ok();
    }
}
