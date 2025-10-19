package com.xigua.ai.llm.model;

import com.xigua.ai.sse.StreamCallback;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ChatContext
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/23 20:07
 */
@Data
@Builder
public class ChatContext implements Serializable {
    private static final long serialVersionUID = 1L;

    private String prompt;
    private String input;
    private boolean stream;
    private StreamCallback streamCallback;
}
