package com.xigua.ai.tool;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.ai.enums.Prompt;
import com.xigua.ai.llm.LLMService;
import com.xigua.ai.llm.model.ChatContext;
import com.xigua.ai.tool.model.ToolSelectionResult;
import com.xigua.ai.utils.PromptUtil;
import com.xigua.common.core.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.io.IOException;

/**
 * @ClassName ToolSelector
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/19 17:13
 */
@Slf4j
@Component
public class ToolSelector {
    @Autowired
    private LLMService llmService;

    /**
     * 根据用户输入，选择合适的工具
     */
    public ToolSelectionResult selectTool(String input) {
        ToolSelectionResult result = new ToolSelectionResult();
        String prompt = PromptUtil.getPrompt(Prompt.TOOL_SELECTOR);

        // 调用模型
        ChatContext chatContext = ChatContext.builder()
                .prompt(prompt)
                .input(input)
                .stream(false)
                .build();
        Flux<String> output = null;
        try {
            output = llmService.chat(chatContext);
        } catch (Exception e) {
            log.error("调用模型失败, input: {}", input, e);
            throw new BusinessException("调用模型失败");
        }

        try {
            JSONObject json = JSONObject.parseObject(output.blockFirst());
            String toolName = json.getString("tool");
            JSONObject args = json.getJSONObject("args");

            ToolType toolTypeE = ToolType.getByToolName(toolName);

            if (toolTypeE == null) {
                log.error("未找到匹配的工具: {}", toolName);
                return result;
            }

            return ToolSelectionResult.of(toolTypeE, args.toString(), args);
        } catch (Exception e) {
            log.error("解析 ToolSelector 输出失败: {}", output, e);
            return result;
        }
    }
}
