package com.xigua.ai.llm;

import com.xigua.ai.context.ChatContext;

import java.io.IOException;

/**
 * @ClassName LLMService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/20 12:47
 */
public interface LLMService {

    String chat(ChatContext chatContext) throws IOException;

    void chatStream(ChatContext chatContext);
}
