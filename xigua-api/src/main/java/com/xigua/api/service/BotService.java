package com.xigua.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xigua.domain.dto.BotDTO;
import com.xigua.domain.entity.Bot;

import java.util.Set;

/**
 * @ClassName BotService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/1 15:23
 */
public interface BotService extends IService<Bot> {

    /**
     * 创建机器人
     * @author wangjinfei
     * @date 2025/10/1 15:29
     * @param dto
     * @return Boolean
    */
    Boolean create(BotDTO dto);

    /**
     * 根据用户id获取机器人数量
     * @author wangjinfei
     * @date 2025/10/1 16:45
     * @param userId
     * @return Integer
    */
    Integer getCountByUserId(String userId);

    /**
     * 根据用户id获取机器人id列表
     * @author wangjinfei
     * @date 2025/10/1 16:45
     * @param userId
     * @return Set<String>
    */
    Set<String> getBotIdsByUserId(String userId);
}
