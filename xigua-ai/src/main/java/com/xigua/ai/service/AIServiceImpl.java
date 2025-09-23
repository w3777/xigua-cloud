package com.xigua.ai.service;

import com.xigua.ai.context.ChatContext;
import com.xigua.ai.llm.LLMService;
import com.xigua.api.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

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
    public String chat(String input, boolean stream) throws IOException {
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
        String output = stream ? llmService.chatStream(chatContext) : llmService.chat(chatContext);
        return output;
    }
}
