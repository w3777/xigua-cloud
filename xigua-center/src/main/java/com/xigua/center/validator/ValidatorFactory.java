package com.xigua.center.validator;

import com.xigua.domain.enums.ChatType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName ValidatorFactory
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/9/13 17:54
 */
@Slf4j
@Component
public class ValidatorFactory {
    private static final Map<String, MessagePermissionValidator> validatorMap = new HashMap<>();

    // 构造函数注入所有的聊天校验器
    public ValidatorFactory(List<MessagePermissionValidator> validators) {
        if(CollectionUtils.isEmpty(validators)){
            log.info("--------->>>>>  聊天校验器为空");
            return;
        }

        for (MessagePermissionValidator validator : validators) {
            ChatType chatType = validator.supportChatType();
            if(chatType == null){
                log.info("--------->>>>>  聊天校验器不支持的聊天类型");
                continue;
            }

            String validatorName = chatType.getDesc() + "聊天校验器";
            validatorMap.put(validatorName, validator);
            log.info("--------->>>>>  {} - 注册成功", validatorName);
        }
    }

    /**
     * 根据聊天类型获取校验器
     * @author wangjinfei
     * @date 2025/9/13 18:02
     * @param chatType
     * @return MessagePermissionValidator
    */
    public static MessagePermissionValidator getValidator(ChatType chatType) {
        log.info("------->>>>>>> 聊天类型：{}", chatType);
        if(chatType == null){
            log.info("--------->>>>>  聊天类型为空");
            return null;
        }

        MessagePermissionValidator validator = validatorMap.get(chatType.getDesc() + "聊天校验器");
        if(validator == null){
            log.info("--------->>>>>  未找到校验器，聊天类型：{}", chatType);
            return null;
        }
        return validator;
    }
}
