package com.xigua.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xigua.domain.entity.Bot;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Set;

/**
 * @ClassName BotMapper
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/1 15:25
 */
@Mapper
public interface BotMapper extends BaseMapper<Bot> {

    /**
     * 根据用户id获取机器人数量
     * @author wangjinfei
     * @date 2025/10/1 16:45
     * @param userId
     * @return Integer
     */
    Integer getCountByUserId(@Param("userId")  String userId);

    /**
     * 根据用户id获取机器人id列表
     * @author wangjinfei
     * @date 2025/10/1 16:45
     * @param userId
     * @return Set<String>
     */
    Set<String> getBotIdsByUserId(@Param("userId") String userId);
}
