package com.xigua.ai.llm;

import com.xigua.ai.llm.properties.DeepseekLLMProperties;
import com.xigua.ai.llm.properties.LLMProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

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

    public DeepSeekLLMService(LLMProperties llmProperties, DeepseekLLMProperties properties) {
        super(llmProperties);
        this.llmProperties = llmProperties;
        this.deepseekLLMProperties = properties;
    }


    @Override
    public void chat() {

    }

    @Override
    public void streamChat() {

    }
}
