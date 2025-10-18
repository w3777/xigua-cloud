package com.xigua.ai.utils;

import com.xigua.ai.context.Prompt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

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
}
