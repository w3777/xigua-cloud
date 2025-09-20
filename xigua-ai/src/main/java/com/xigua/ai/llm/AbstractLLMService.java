package com.xigua.ai.llm;

import com.xigua.ai.llm.properties.LLMProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName AbstractLLMService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/20 15:23
 */
@Service
public abstract class AbstractLLMService implements LLMService {
    @Autowired
    protected LLMProperties llmProperties;

    public AbstractLLMService(LLMProperties llmProperties) {
        this.llmProperties = llmProperties;
    }
}
