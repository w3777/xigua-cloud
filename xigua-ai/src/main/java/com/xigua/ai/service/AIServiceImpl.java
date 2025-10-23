package com.xigua.ai.service;

import com.xigua.ai.agent.Agent;
import com.xigua.ai.agent.model.AgentContext;
import com.xigua.ai.enums.Prompt;
import com.xigua.ai.intent.IntentType;
import com.xigua.ai.llm.model.ChatContext;
import com.xigua.ai.intent.IntentRecognizer;
import com.xigua.ai.llm.LLMService;
import com.xigua.ai.utils.PromptUtil;
import com.xigua.api.service.*;
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
            prompt = PromptUtil.getPrompt(Prompt.DEFAULT);
        }

        ChatContext chatContext = ChatContext.builder()
                .input(input)
                .prompt(prompt)
                .stream(stream)
                .build();
        Flux<String> output = null;
        try {
            output = llmService.chat(chatContext);
        } catch (Exception e) {
            log.error("调用LLM服务失败", e);
            throw new BusinessException("调用LLM服务失败");
        }

        // Flux<String> 转 Flux<ChatResponse>
        Flux<ChatResponse> responseFlux = output.map(s ->
                ChatResponse.newBuilder().setOutput(s).build()
        );
        return responseFlux;
    }

    @Override
    public Mono<IntentResponse> detectIntent(Mono<IntentRequest> reactorRequest) {
        IntentType intentType = intentRecognizer.detect(reactorRequest.block().getInput());
        // 解析意图
        IntentResponse intentResponse = IntentResponse.newBuilder()
                .setIntent(intentType.getType())
                .build();
        return Mono.just(intentResponse);
    }

    @Override
    public Flux<AgentResponse> agentProcess(Mono<AgentRequest> request) {
        AgentRequest req = request.block();
        Boolean stream = req.getStream();

        log.info("agentProcess开始处理，requestId: {}，入参: {}", req.getRequestId(), req);
        Flux<String> output = agent.process(AgentContext.builder()
                 .requestId(req.getRequestId())
                .input(req.getInput())
                .stream(stream)
                .systemPrompt(req.getPrompt())
                .build());
        log.info("agentProcess处理完成，requestId: {}", req.getRequestId());

        return output.map(s ->
                AgentResponse.newBuilder().setOutput(s).build()
        );
    }
}
