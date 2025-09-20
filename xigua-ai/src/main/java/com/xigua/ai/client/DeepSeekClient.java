package com.xigua.ai.client;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.ai.openai.ChatCompletionRequest;
import com.xigua.ai.openai.ChatCompletionResponse;
import com.xigua.ai.sse.StreamCallback;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import okhttp3.sse.EventSources;

import java.io.IOException;

/**
 * @ClassName DeepSeekClient
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/20 21:19
 */
@Slf4j
public class DeepSeekClient {
    private static final String BASE_URL = "https://api.deepseek.com";
    private static final String CHAT_COMPLETIONS = "/chat/completions";

    private String apiKey;
    private String model;


    public ChatCompletionResponse chatCompletions(ChatCompletionRequest requestBody) throws IOException {
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType, JSONObject.toJSONString(requestBody));
        Request request = new Request.Builder()
                .url(BASE_URL + CHAT_COMPLETIONS)
                .method("POST", body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "application/json")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();
        Response response = client.newCall(request).execute();
        ChatCompletionResponse responseBody = null;
        if (response.isSuccessful()) {
            String responseStr = response.body().string();
            System.out.println("Response: " + responseStr);
            responseBody = JSONObject.parseObject(responseStr, ChatCompletionResponse.class);
        } else {
            System.out.println("Request failed with code: " + response.code());
        }
        return responseBody;
    }

    public void chatCompletionsStream(ChatCompletionRequest requestBody, StreamCallback callback){
        OkHttpClient client = new OkHttpClient.Builder().build();

        // 开启流式模式
        requestBody.setStream(true);

        RequestBody body = RequestBody.create(
                MediaType.parse("application/json"),
                JSONObject.toJSONString(requestBody)
        );

        Request request = new Request.Builder()
                .url(BASE_URL + CHAT_COMPLETIONS)
                .post(body)
                .addHeader("Content-Type", "application/json")
                .addHeader("Accept", "text/event-stream")
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        EventSource.Factory factory = EventSources.createFactory(client);

        factory.newEventSource(request, new EventSourceListener() {
            @Override
            public void onOpen(EventSource eventSource, Response response) {
                System.out.println("SSE connection opened.");
            }

            @Override
            public void onEvent(EventSource eventSource, String id, String type, String data) {
                if ("[DONE]".equals(data)) {
                    // 延迟一小段时间再关闭，让管道中的数据被处理完
                    new Thread(() -> {
                        try {
                            Thread.sleep(300); // 延迟300毫秒
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                        callback.onComplete();
                        eventSource.cancel(); // 主动关闭
                    }).start();
                    return;
                }

                try {
                    JSONObject json = JSONObject.parseObject(data);
                    JSONObject delta = json.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("delta");

                    if (delta != null && delta.containsKey("content")) {
                        String content = delta.getString("content");
                        if (content != null && !content.isEmpty()) {
                            callback.onMessage(content); // 输出增量内容
                        }
                    }

                    // 判断是否有 finish_reason
                    String finishReason = json.getJSONArray("choices")
                            .getJSONObject(0)
                            .getString("finish_reason");
                    if ("stop".equals(finishReason)) {
                        log.info("finish_reason: {}", finishReason);
                        // 这里也是结束sse和上面的"[DONE]"一样，有点冲突
//                        callback.onComplete();
//                        eventSource.cancel(); // 主动关闭
                    }

                } catch (Exception e) {
                    callback.onError(e);
                }
            }

            @Override
            public void onClosed(EventSource eventSource) {
                System.out.println("SSE connection closed.");
            }

            @Override
            public void onFailure(EventSource eventSource, Throwable t, Response response) {
                callback.onError(t);

                log.error("SSE connection failed", t);
                if (response != null) {
                    System.err.println("Response code: " + response.code());
                }
            }
        });
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder{
        private String apiKey;
        private String model;

        public Builder apiKey(String apiKey) {
            this.apiKey = apiKey;
            return this;
        }

        public Builder model(String model) {
            this.model = model;
            return this;
        }

        public DeepSeekClient build(){
            DeepSeekClient deepSeekClient = new DeepSeekClient();
            deepSeekClient.apiKey = this.apiKey;
            deepSeekClient.model = this.model;
            return deepSeekClient;
        }
    }
}
