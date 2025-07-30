package com.xigua.client.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xigua.client.mapper.GroupMemberMapper;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.common.core.util.DateUtil;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.core.util.UserContext;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.entity.GroupMember;
import com.xigua.domain.entity.User;
import com.xigua.domain.enums.GroupRole;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.api.service.GroupMemberService;
import com.xigua.api.service.UserService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName GroupMemberServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/6 11:14
 */
@Service
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

    /**
     * 获取群成员数量
     * @author wangjinfei
     * @date 2025/7/27 9:42
     * @param userId
     * @return Integer
     */
    @Override
    public Integer getCountByUserId(String userId) {
        Integer count = baseMapper.getCountByUserId(userId);
        return count == null ? 0 : count;
    }

    /**
     * 获取加入的群列表
     * @author wangjinfei
     * @date 2025/7/27 11:08
     * @param userId
     * @return Set<String>
     */
    @Override
    public Set<String> getGroupIdsByUserId(String userId) {
        Set<String> groupIdKeys = redisUtil.scanZSetKeysInMember(userId, RedisEnum.GROUP_MEMBER_ID.getKey() + "*");
        if (CollectionUtils.isEmpty(groupIdKeys)) {
            return Set.of();
        }

        Set<String> groupIds = groupIdKeys.stream()
                .map(key -> key.replace(RedisEnum.GROUP_MEMBER_ID.getKey(), "")) // 去除前缀
                .collect(Collectors.toSet());

        return groupIds;
    }

    /**
     * 获取群成员列表
     * @author wangjinfei
     * @date 2025/7/27 11:21
     * @param groupId
     * @return List<GroupMember>
     */
    @Override
    public List<GroupMember> getGroupMembersByGroupId(String groupId) {
        List<GroupMember> groupMembers = baseMapper.selectList(new LambdaQueryWrapper<GroupMember>()
                .eq(GroupMember::getGroupId, groupId));
        if(CollectionUtils.isEmpty(groupMembers)){
            return List.of();
        }

        return groupMembers;
    }

    /**
     * 获取加入时间
     * @author wangjinfei
     * @date 2025/7/30 19:46
     * @param groupId
     * @param userId
     * @return LocalDateTime
     */
    @Override
    public LocalDateTime getJoinTime(String groupId, String userId) {
        if(StringUtils.isEmpty(groupId) || StringUtils.isEmpty(userId)){
            throw new BusinessException("群ID或用户ID不能为空");
        }

        long score = redisUtil.getScore(RedisEnum.GROUP_MEMBER_ID.getKey() + groupId, userId);
        if(score == 0){
            throw new BusinessException("用户未加入群聊");
        }

        LocalDateTime localDateTime = DateUtil.toLocalDateTime(score);
        return localDateTime;
    }

    /**
     * 获取群角色
     * @author wangjinfei
     * @date 2025/7/30 19:59
     * @param groupId
     * @param userId
     * @return Integer
     */
    @Override
    public Integer getGroupRole(String groupId, String userId) {
        if(StringUtils.isEmpty(groupId) || StringUtils.isEmpty(userId)){
            throw new BusinessException("群ID或用户ID不能为空");
        }

        String groupMemberCache = redisUtil.get(RedisEnum.GROUP_MEMBER.getKey() + groupId + "_" + userId);
        if(StringUtils.isEmpty(groupMemberCache)){
            throw new BusinessException("用户未加入群聊");
        }

        GroupMember groupMember = JSONObject.parseObject(groupMemberCache, GroupMember.class);
        if(groupMember == null){
            throw new BusinessException("用户未加入群聊");
        }

        return groupMember.getRole();
    }
}
