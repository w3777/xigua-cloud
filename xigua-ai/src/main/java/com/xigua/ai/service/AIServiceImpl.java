package com.xigua.ai.service;

import com.xigua.ai.context.ChatContext;
import com.xigua.ai.llm.LLMService;
import com.xigua.ai.sse.StreamCallback;
import com.xigua.api.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

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
public class AIServiceImpl implements AIService {
    @Autowired
    private LLMService llmService;

    @Override
    public Flux<String> chat(String input, boolean stream) throws IOException {
        String prompt = "";
        ClassPathResource resource = new ClassPathResource("prompts/system_prompt");
        Path path = resource.getFile().toPath();
        try {
            prompt = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            log.error("读取prompts/system_prompt文件失败", e);
        }

        ChatContext chatContext = ChatContext.builder()
                .input(input)
                .prompt(prompt)
                .build();
        Flux<String> output = stream ? adaptStream(chatContext) : Flux.just(llmService.chat(chatContext));
        return output;
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
