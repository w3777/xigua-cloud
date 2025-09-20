package com.xigua.ai.sse;

/**
 * @ClassName StreamCallback
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/21 18:08
 */
public interface StreamCallback {
    // 流式增量内容
    void onMessage(String content);

    // 结束
    void onComplete();

    // 错误
    void onError(Throwable t);
}
