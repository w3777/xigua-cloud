package com.xigua.ai.service;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.ai.agent.Agent;
import com.xigua.ai.agent.model.AgentContext;
import com.xigua.ai.context.ChatContext;
import com.xigua.ai.intent.IntentRecognizer;
import com.xigua.ai.llm.LLMService;
import com.xigua.ai.sse.StreamCallback;
import com.xigua.api.service.*;
import com.xigua.api.service.AIService;
import com.xigua.common.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @ClassName AIServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/20 13:08
 */
@Slf4j
@Service
@DubboService
public class AIServiceImpl extends DubboAIServiceTriple.AIServiceImplBase {
    @Autowired
    private LLMService llmService;
    @Autowired
    private IntentRecognizer intentRecognizer;

    @Autowired
    private Agent agent;

    @Override
    public Flux<ChatResponse> chat(Mono<ChatRequest> request) {
        // 阻塞等待请求完成
        ChatRequest req = request.block();
        String input = req.getInput();
        boolean stream = req.getStream();
        String prompt = req.getPrompt();

        if(StringUtils.isEmpty(prompt)){
            // 获取默认提示词
            prompt = getDefaultPrompt();
        }

        ChatContext chatContext = ChatContext.builder()
                .input(input)
                .prompt(prompt)
                .build();
        Flux<String> output = null;
        try {
            output = stream ? adaptStream(chatContext) : Flux.just(llmService.chat(chatContext));
        } catch (IOException e) {
            log.error("调用LLM服务失败", e);
            throw new BusinessException("调用LLM服务失败");
        }

        // Flux<String> 转 Flux<ChatResponse>
        Flux<ChatResponse> responseFlux = output.map(s ->
                ChatResponse.newBuilder().setOutput(s).build()
        );
        return responseFlux;
    }

    private String getDefaultPrompt(){
        String prompt = "";
        ClassPathResource resource = new ClassPathResource("prompts/system_prompt");
        try {
            Path path = resource.getFile().toPath();
            prompt = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            log.error("读取prompts/system_prompt文件失败", e);
        }
        return prompt;
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

    @Override
    public Mono<IntentResponse> detectIntent(Mono<IntentRequest> reactorRequest) {
        com.xigua.ai.intent.IntentType intentType = intentRecognizer.detect(reactorRequest.block().getInput());
        // 解析意图
        IntentResponse intentResponse = parseIntent(intentType);
        return Mono.just(intentResponse);
    }

    private IntentResponse parseIntent(com.xigua.ai.intent.IntentType intentType){
        IntentResponse intentResponse = IntentResponse.newBuilder()
                .setIntent(IntentType.UNKNOWN)
                .build();
        try {
            if (StringUtils.isEmpty(intentType.getType())) {
                return intentResponse;
            }
            intentResponse = intentResponse.toBuilder()
                    .setIntent(IntentType.valueOf(intentType.getType()))
                    .build();
        } catch (Exception e) {
            log.error("解析意图失败, 意图类型: {}", intentType.getType(), e);
        }
        return intentResponse;
    }

    @Override
    public Flux<AgentResponse> agentProcess(Mono<AgentRequest> request) {
        AgentRequest req = request.block();
        Boolean stream = req.getStream();

        Flux<String> output = agent.process(AgentContext.builder()
                .input(req.getInput())
                .stream(stream)
                .build());

        return output.map(s ->
                AgentResponse.newBuilder().setOutput(s).build()
        );
    }
}
