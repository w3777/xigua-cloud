package com.xigua.ai.openai;

import java.util.List;

/**
 * @ClassName ChatCompletionResponse
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/20 22:16
 */
public class ChatCompletionResponse {
    private String id;
    private String object;
    private Long created;
    private String model;
    private String systemFingerprint;
    private List<Choice> choices;
    private Usage usage;

    // 静态内部类：选择项
    public static class Choice {
        private Integer index;
        private Message message;
        private Message delta;
        private String finishReason;
        private Double logprobs;

        // Getter 和 Setter
        public Integer getIndex() { return index; }
        public void setIndex(Integer index) { this.index = index; }

        public Message getMessage() { return message; }
        public void setMessage(Message message) { this.message = message; }

        public Message getDelta() { return delta; }
        public void setDelta(Message delta) { this.delta = delta; }

        public String getFinishReason() { return finishReason; }
        public void setFinishReason(String finishReason) { this.finishReason = finishReason; }

        public Double getLogprobs() { return logprobs; }
        public void setLogprobs(Double logprobs) { this.logprobs = logprobs; }
    }

    // 静态内部类：消息对象
    public static class Message {
        private String role;
        private String content;
        private String name;
        private List<ChatCompletionRequest.ToolCall> toolCalls;

        // 构造函数
        public Message() {}

        public Message(String role, String content) {
            this.role = role;
            this.content = content;
        }

        // Getter 和 Setter
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public List<ChatCompletionRequest.ToolCall> getToolCalls() { return toolCalls; }
        public void setToolCalls(List<ChatCompletionRequest.ToolCall> toolCalls) { this.toolCalls = toolCalls; }
    }

    // 静态内部类：使用情况
    public static class Usage {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;


        // Getter 和 Setter
        public Integer getPromptTokens() { return promptTokens; }
        public void setPromptTokens(Integer promptTokens) { this.promptTokens = promptTokens; }

        public Integer getCompletionTokens() { return completionTokens; }
        public void setCompletionTokens(Integer completionTokens) { this.completionTokens = completionTokens; }

        public Integer getTotalTokens() { return totalTokens; }
        public void setTotalTokens(Integer totalTokens) { this.totalTokens = totalTokens; }
    }

    // Getter 和 Setter
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getObject() { return object; }
    public void setObject(String object) { this.object = object; }

    public Long getCreated() { return created; }
    public void setCreated(Long created) { this.created = created; }

    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public String getSystemFingerprint() { return systemFingerprint; }
    public void setSystemFingerprint(String systemFingerprint) { this.systemFingerprint = systemFingerprint; }

    public List<Choice> getChoices() { return choices; }
    public void setChoices(List<Choice> choices) { this.choices = choices; }

    public Usage getUsage() { return usage; }
    public void setUsage(Usage usage) { this.usage = usage; }
}
