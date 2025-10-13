package com.xigua.ai.service;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.ai.context.ChatContext;
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
        ChatRequest aiChatReq = ChatRequest.newBuilder()
                .setInput(reactorRequest.block().getInput())
                .setStream(false)
                .setPrompt(getDetectIntentPrompt())
                .build();
        Mono<ChatRequest> aiChatReqM = Mono.just(aiChatReq);
        Flux<ChatResponse> chatFlux = chat(aiChatReqM);
        // 阻塞等待完成
        ChatResponse response = chatFlux.blockLast();
        // 解析意图
        IntentResponse intentResponse = parseIntent(response);
        return Mono.just(intentResponse);
    }

    private String getDetectIntentPrompt(){
        String prompt = "";
        ClassPathResource resource = new ClassPathResource("prompts/detect_intent_prompt");
        try {
            Path path = resource.getFile().toPath();
            prompt = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            log.error("读取prompts/detect_intent_prompt文件失败", e);
        }
        return prompt;
    }

    private IntentResponse parseIntent(ChatResponse response){
        IntentResponse intentResponse = IntentResponse.newBuilder()
                .setIntent(IntentType.UNKNOWN)
                .build();
        try {
            if (StringUtils.isEmpty(response.getOutput())) {
                return intentResponse;
            }
            JSONObject intentJson = JSONObject.parseObject(response.getOutput());
            String intent = intentJson.getString("intent");
            if (StringUtils.isNotEmpty(intent)) {
                intentResponse = intentResponse.toBuilder()
                        .setIntent(IntentType.valueOf(intent))
                        .build();
            }
        } catch (Exception e) {
            log.error("解析意图失败, 输出内容: {}", response.getOutput(), e);
        }
        return intentResponse;
    }
}
