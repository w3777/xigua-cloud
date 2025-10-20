package com.xigua.ai.llm;

import com.xigua.ai.llm.model.ChatContext;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * @ClassName LLMService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/20 12:47
 */
public interface LLMService {

    Flux<String> chat(ChatContext chatContext);
}
