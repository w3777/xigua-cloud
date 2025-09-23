package com.xigua.api.service;

import java.io.IOException;

/**
 * @ClassName AIService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/20 13:07
 */
public interface AIService {
    String chat(String input, boolean stream) throws IOException;
}
