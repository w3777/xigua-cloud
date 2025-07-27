package com.xigua.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xigua.domain.entity.FriendRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @ClassName FriendRequestMapper
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/13 21:23
 */
@Mapper
public interface FriendRequestMapper extends BaseMapper<FriendRequest> {

    /**
     * 获取好友请求数量
     * @author wangjinfei
     * @date 2025/7/27 9:42
     * @return Integer
     */
    Integer getCountByUserId(@Param("userId") String userId);
}
