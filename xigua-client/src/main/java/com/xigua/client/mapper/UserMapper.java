package com.xigua.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xigua.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName UserMapper
 * @Description
 * @Author wangjinfei
 * @Date 2025/4/27 15:32
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {
}
