package com.xigua.ai.intent;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.ai.llm.model.ChatContext;
import com.xigua.ai.llm.LLMService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @ClassName IntentRecognizer
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/18 19:38
 */
@Slf4j
@Component
public class IntentRecognizer {
    @Autowired
    private LLMService llmService;

    public IntentType detect(String input){
        ChatContext chatContext = ChatContext.builder()
                .input(input)
                .prompt(getDetectIntentPrompt())
                .stream(false)
                .build();
        chatContext.setPrompt(getDetectIntentPrompt());
        try {
            Flux<String> output = llmService.chat(chatContext);
            IntentType intentType = parseIntent(output.blockFirst());
            if(intentType != null){
                return intentType;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return IntentType.UNKNOWN;
    }

    private String getDetectIntentPrompt(){
        String prompt = "";
        ClassPathResource resource = new ClassPathResource("prompts/detect_intent_prompt.txt");
        try {
            Path path = resource.getFile().toPath();
            prompt = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            log.error("读取prompts/detect_intent_prompt文件失败", e);
        }
        return prompt;
    }

    private IntentType parseIntent(String output){
        IntentType intentType = IntentType.UNKNOWN;
        try {
            if (StringUtils.isEmpty(output)) {
                return intentType;
            }
            JSONObject intentJson = JSONObject.parseObject(output);
            String intent = intentJson.getString("intent");
            if (StringUtils.isNotEmpty(intent)) {
                intentType = IntentType.fromType(intent);
                if (intentType != null) {
                    return intentType;
                }
            }
        } catch (Exception e) {
            log.error("解析意图失败, 输出内容: {}", output, e);
        }
        return intentType;
    }
}
