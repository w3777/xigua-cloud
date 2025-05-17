package com.xigua.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xigua.domain.entity.FriendRelation;
import com.xigua.domain.vo.FriendDetailVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @ClassName FriendRelationMapper
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/13 21:24
 */
@Mapper
public interface FriendRelationMapper extends BaseMapper<FriendRelation> {
    /**
     * 好友详情
     * @author wangjinfei
     * @date 2025/5/17 12:06
     * @param friendId
     * @return FriendDetailVO
     */
    FriendDetailVO getFriendDetail(@Param("userId") String userId, @Param("friendId") String friendId);
}
