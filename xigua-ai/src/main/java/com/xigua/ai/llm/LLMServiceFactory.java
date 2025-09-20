package com.xigua.ai.llm;

import com.xigua.ai.llm.enums.Provider;
import com.xigua.ai.llm.properties.LLMProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName LLMServiceFactory
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/20 15:05
 */
@Slf4j
@Component
public class LLMServiceFactory {
    private static final Map<String, AbstractLLMService> llmMap = new HashMap<>();

    // 构造函数注入所有的llm
    public LLMServiceFactory(List<AbstractLLMService> llmServices) {
        if(CollectionUtils.isEmpty(llmServices)){
            log.info("llm服务为空");
            return;
        }

        for (AbstractLLMService llmService : llmServices) {
            LLMProperties llmProperties = llmService.llmProperties;
            String provider = llmProperties.getProvider();

            if(StringUtils.isEmpty(provider)){
                log.info("未找到LLM提供商");
                return;
            }
            if(Provider.DEEPSEEK.getName().equals(provider)){
                llmMap.put(Provider.DEEPSEEK.getName(), llmService);
                log.info("LLM - {} 注册成功", provider);
            }
        }
    }

    /**
     * 根据提供商类型获取llm
     * @author wangjinfei
     * @date 2025/9/20 15:38
     * @param provider
     * @return AbstractLLMService
    */
    public static AbstractLLMService getValidator(Provider provider) {
        if(provider == null){
            return null;
        }

        AbstractLLMService llmService = llmMap.get(provider.getName());
        if(llmService == null){
            return null;
        }
        return llmService;
    }
}
