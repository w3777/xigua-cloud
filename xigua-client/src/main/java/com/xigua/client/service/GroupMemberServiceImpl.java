package com.xigua.client.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xigua.client.mapper.GroupMemberMapper;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.core.util.UserContext;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.entity.GroupMember;
import com.xigua.domain.entity.User;
import com.xigua.domain.enums.GroupRole;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.service.GroupMemberService;
import com.xigua.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName GroupMemberServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/6 11:14
 */
@DubboService
public class GroupMemberServiceImpl extends ServiceImpl<GroupMemberMapper, GroupMember> implements GroupMemberService {
    @Autowired
    private Sequence sequence;
    @Autowired
    private RedisUtil redisUtil;
    @DubboReference
    private UserService userService;

    /**
     * 添加群成员
     * @author wangjinfei
     * @date 2025/7/6 11:27
     * @param groupId
     * @param memberIds
     * @return Boolean
     */
    @Override
    public Boolean addGroupMember(String groupId, List<String> memberIds) {
        List<GroupMember> groupMemberList = new ArrayList<>();

        for (String memberId : memberIds) {
            GroupMember groupMember = new GroupMember();
            groupMember.setId(sequence.nextNo());
            groupMember.setGroupId(groupId);
            groupMember.setUserId(memberId);

            // 设置群内角色
            groupMember.setRole(setGroupRole(memberId));

            // 设置群内昵称
            groupMember.setNickname(setNickname(memberId));

            groupMemberList.add(groupMember);
        }

        // 批量插入
        baseMapper.insert(groupMemberList);

        return true;
    }

    /**
     * 设置群内角色
     * @author wangjinfei
     * @date 2025/7/6 11:42
     * @param memberId
     * @return Integer
    */
    private Integer setGroupRole(String memberId){
        String userId = UserContext.get().getUserId();

        if (memberId.equals(userId)) { // 是当前用户为群主
            return GroupRole.OWNER.getType();
        } else { // 其他默认普通成员
            return GroupRole.MEMBER.getType();
        }
    }

    /**
     * 设置群内昵称
     * @author wangjinfei
     * @date 2025/7/6 11:52
     * @param memberId
     * @return String
    */
    private String setNickname(String memberId){
        // 创建群成员时，默认使用用户名作为群内昵称

        // 缓存中获取用户名
        String userCache = redisUtil.get(RedisEnum.USER.getKey() + memberId);
        if(StringUtils.isNotEmpty(userCache)){
            User user = JSONObject.parseObject(userCache, User.class);
            if(user != null && StringUtils.isNotEmpty(user.getUsername())){
                return user.getUsername();
            }
        }

        // 缓存中没有，查询数据库
        User user = userService.getById(memberId);
        if(user != null && StringUtils.isNotEmpty(user.getUsername())){
            // 可以再次 缓存用户信息
//            redisUtil.set(RedisEnum.USER.getKey() + memberId, JSONObject.toJSONString(user));
            return user.getUsername();
        }

        return null;
    }
}
