package com.xigua.ai.context;

import lombok.Builder;
import lombok.Data;

/**
 * @ClassName ChatContext
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/23 20:07
 */
@Data
@Builder
public class ChatContext {
    private String prompt;
    private String input;
}
