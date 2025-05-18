package com.xigua.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xigua.domain.entity.ChatMessage;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName Message
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/17 19:16
 */
@Mapper
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}
