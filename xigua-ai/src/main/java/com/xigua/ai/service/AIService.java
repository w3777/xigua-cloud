package com.xigua.ai.service;

import reactor.core.publisher.Flux;

/**
 * @ClassName AIService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/20 13:07
 */
public interface AIService {
    Flux<String> chat(String input, boolean stream, String prompt);
}
