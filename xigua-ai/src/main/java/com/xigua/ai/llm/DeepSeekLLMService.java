package com.xigua.ai.llm;

import com.xigua.ai.client.DeepSeekClient;
import com.xigua.ai.llm.model.ChatContext;
import com.xigua.ai.llm.properties.DeepseekLLMProperties;
import com.xigua.ai.llm.properties.LLMProperties;
import com.xigua.ai.openai.ChatCompletionRequest;
import com.xigua.ai.openai.ChatCompletionResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @ClassName DeepSeekLLMService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/20 13:27
 */
@Slf4j
@Service
public class DeepSeekLLMService extends AbstractLLMService {
    private LLMProperties llmProperties;
    private DeepseekLLMProperties deepseekLLMProperties;

    @Autowired
    private DeepSeekClient deepSeekClient;

    public DeepSeekLLMService(LLMProperties llmProperties, DeepseekLLMProperties properties) {
        super(llmProperties);
        this.llmProperties = llmProperties;
        this.deepseekLLMProperties = properties;
    }


    @Override
    public String chat(ChatContext chatContext) throws IOException {
        String output = "";

        // 设置响应格式
        ChatCompletionRequest.ResponseFormat responseFormat =
                new ChatCompletionRequest.ResponseFormat();
        responseFormat.setType("text");

        // 设置系统消息
        ChatCompletionRequest.Message systemMessage = new ChatCompletionRequest.Message();
        systemMessage.setRole("system");
        systemMessage.setContent(chatContext.getPrompt());

        // 用户input
        ChatCompletionRequest.Message userMessage = new ChatCompletionRequest.Message();
        userMessage.setRole("user");
        userMessage.setContent(chatContext.getInput());

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(deepseekLLMProperties.getModel())
                .maxTokens(llmProperties.getMaxTokens())
                .temperature(llmProperties.getTemperature())
                .frequencyPenalty(0.0)
                .presencePenalty(0.0)
                .responseFormat(responseFormat)
                .messages(List.of(systemMessage, userMessage))
                .build();

        ChatCompletionResponse response = deepSeekClient.chatCompletions(request);
        if(response == null){
            return output;
        }
        if(CollectionUtils.isEmpty(response.getChoices())){
            return output;
        }

        output = response.getChoices().get(0).getMessage().getContent();
        return output;
    }

    @Override
    public void chatStream(ChatContext chatContext) {
        // 设置响应格式
        ChatCompletionRequest.ResponseFormat responseFormat =
                new ChatCompletionRequest.ResponseFormat();
        responseFormat.setType("text");

        // 设置系统消息
        ChatCompletionRequest.Message systemMessage = new ChatCompletionRequest.Message();
        systemMessage.setRole("system");
        systemMessage.setContent(chatContext.getPrompt());

        // 用户input
        ChatCompletionRequest.Message userMessage = new ChatCompletionRequest.Message();
        userMessage.setRole("user");
        userMessage.setContent(chatContext.getInput());

        ChatCompletionRequest request = ChatCompletionRequest.builder()
                .model(deepseekLLMProperties.getModel())
                .maxTokens(llmProperties.getMaxTokens())
                .temperature(llmProperties.getTemperature())
                .frequencyPenalty(0.0)
                .presencePenalty(0.0)
                .responseFormat(responseFormat)
                .messages(List.of(systemMessage, userMessage))
                .build();

        deepSeekClient.chatCompletionsStream(request, chatContext.getStreamCallback());
    }
}
