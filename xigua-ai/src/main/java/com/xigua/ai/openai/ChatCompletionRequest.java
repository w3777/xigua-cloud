package com.xigua.ai.openai;

import java.util.List;
import java.util.Map;

/**
 * @ClassName ChatCompletionRequest
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/20 22:16
 */
public class ChatCompletionRequest {
    // 必需字段
    private String model;
    private List<Message> messages;

    // 可选字段
    private Double temperature;
    private Double topP;
    private Integer n;
    private Boolean stream;
    private StreamOptions streamOptions;
    private List<String> stop;
    private Integer maxTokens;
    private Double presencePenalty;
    private Double frequencyPenalty;
    private Map<String, Integer> logitBias;
    private String user;
    private ResponseFormat responseFormat;
    private List<Tool> tools;
    private Object toolChoice;
    private Boolean parallelToolCalls;
    private Integer seed;
    private Object reasoningEffort;
    private Boolean logprobs;
    private Integer topLogprobs;

    // 静态内部类：消息对象
    public static class Message {
        private String role;
        private String content;
        private String name;
        private List<ToolCall> toolCalls;
        private ToolCall toolCall;

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

        public List<ToolCall> getToolCalls() { return toolCalls; }
        public void setToolCalls(List<ToolCall> toolCalls) { this.toolCalls = toolCalls; }

        public ToolCall getToolCall() { return toolCall; }
        public void setToolCall(ToolCall toolCall) { this.toolCall = toolCall; }
    }

    // 静态内部类：工具调用
    public static class ToolCall {
        private String id;
        private String type;
        private Function function;

        // Getter 和 Setter
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }

        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public Function getFunction() { return function; }
        public void setFunction(Function function) { this.function = function; }
    }

    // 静态内部类：函数定义
    public static class Function {
        private String name;
        private String description;
        private Object parameters;

        // Getter 和 Setter
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public Object getParameters() { return parameters; }
        public void setParameters(Object parameters) { this.parameters = parameters; }
    }

    // 静态内部类：工具定义
    public static class Tool {
        private String type;
        private Function function;

        // Getter 和 Setter
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }

        public Function getFunction() { return function; }
        public void setFunction(Function function) { this.function = function; }
    }

    // 静态内部类：流选项
    public static class StreamOptions {
        private Boolean includeUsage;

        // Getter 和 Setter
        public Boolean getIncludeUsage() { return includeUsage; }
        public void setIncludeUsage(Boolean includeUsage) { this.includeUsage = includeUsage; }
    }

    // 静态内部类：响应格式
    public static class ResponseFormat {
        private String type;

        // Getter 和 Setter
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
    }

    // Getter 和 Setter
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }

    public List<Message> getMessages() { return messages; }
    public void setMessages(List<Message> messages) { this.messages = messages; }

    public Double getTemperature() { return temperature; }
    public void setTemperature(Double temperature) { this.temperature = temperature; }

    public Double getTopP() { return topP; }
    public void setTopP(Double topP) { this.topP = topP; }

    public Integer getN() { return n; }
    public void setN(Integer n) { this.n = n; }

    public Boolean getStream() { return stream; }
    public void setStream(Boolean stream) { this.stream = stream; }

    public StreamOptions getStreamOptions() { return streamOptions; }
    public void setStreamOptions(StreamOptions streamOptions) { this.streamOptions = streamOptions; }

    public List<String> getStop() { return stop; }
    public void setStop(List<String> stop) { this.stop = stop; }

    public Integer getMaxTokens() { return maxTokens; }
    public void setMaxTokens(Integer maxTokens) { this.maxTokens = maxTokens; }

    public Double getPresencePenalty() { return presencePenalty; }
    public void setPresencePenalty(Double presencePenalty) { this.presencePenalty = presencePenalty; }

    public Double getFrequencyPenalty() { return frequencyPenalty; }
    public void setFrequencyPenalty(Double frequencyPenalty) { this.frequencyPenalty = frequencyPenalty; }

    public Map<String, Integer> getLogitBias() { return logitBias; }
    public void setLogitBias(Map<String, Integer> logitBias) { this.logitBias = logitBias; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public ResponseFormat getResponseFormat() { return responseFormat; }
    public void setResponseFormat(ResponseFormat responseFormat) { this.responseFormat = responseFormat; }

    public List<Tool> getTools() { return tools; }
    public void setTools(List<Tool> tools) { this.tools = tools; }

    public Object getToolChoice() { return toolChoice; }
    public void setToolChoice(Object toolChoice) { this.toolChoice = toolChoice; }

    public Boolean getParallelToolCalls() { return parallelToolCalls; }
    public void setParallelToolCalls(Boolean parallelToolCalls) { this.parallelToolCalls = parallelToolCalls; }

    public Integer getSeed() { return seed; }
    public void setSeed(Integer seed) { this.seed = seed; }

    public Object getReasoningEffort() { return reasoningEffort; }
    public void setReasoningEffort(Object reasoningEffort) { this.reasoningEffort = reasoningEffort; }

    public Boolean getLogprobs() { return logprobs; }
    public void setLogprobs(Boolean logprobs) { this.logprobs = logprobs; }

    public Integer getTopLogprobs() { return topLogprobs; }
    public void setTopLogprobs(Integer topLogprobs) { this.topLogprobs = topLogprobs; }
}
