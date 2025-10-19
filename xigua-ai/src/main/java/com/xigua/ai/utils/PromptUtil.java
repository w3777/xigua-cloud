package com.xigua.ai.utils;

import com.xigua.ai.enums.Prompt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * @ClassName PromptUtil
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/18 22:43
 */
@Slf4j
public class PromptUtil {
    public static String getPrompt(Prompt promptE) {
        String prompt = "";
        ClassPathResource resource = new ClassPathResource(promptE.getPath());
        try {
            Path path = resource.getFile().toPath();
            prompt = new String(Files.readAllBytes(path));
        } catch (IOException e) {
            log.error("读取prompts/system_prompt文件失败", e);
        }
        return prompt;
    }

    public static String fill(String prompt, Map<String, String> values) {
        if (prompt == null || prompt.isEmpty()) {
            return "";
        }
        if (values == null || values.isEmpty()) {
            return prompt;
        }

        String filled = prompt;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            String placeholder = "{{" + entry.getKey() + "}}";
            String value = entry.getValue() == null ? "" : entry.getValue();
            filled = filled.replace(placeholder, value);
        }
        return filled;
    }
}
