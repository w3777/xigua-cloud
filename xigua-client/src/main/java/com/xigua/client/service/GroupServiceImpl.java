package com.xigua.client.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xigua.client.mapper.GroupMapper;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.core.util.UserContext;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.dto.GroupDTO;
import com.xigua.domain.entity.Group;
import com.xigua.domain.entity.User;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.service.GroupMemberService;
import com.xigua.service.GroupService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @ClassName GroupServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/6 11:07
 */
@DubboService
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {
    @Autowired
    private Sequence sequence;
    @Autowired
    private RedisUtil redisUtil;
    @DubboReference
    private GroupMemberService groupMemberService;

    /**
     * 创建群组
     * @author wangjinfei
     * @date 2025/7/6 11:07
     * @param dto
     * @return Boolean
     */
    @Override
    public Boolean createGroup(GroupDTO dto) {
        String groupId = sequence.nextNo();
        List<String> memberIds = dto.getMemberIds();

        // 校验数据
        checkData(dto);

        // 添加群组
        dto.setGroupId(groupId);
        Boolean addGroup = addGroup(dto);

        // 添加群成员
        Boolean addGroupMember = groupMemberService.addGroupMember(groupId, memberIds);

        // todo 异步设置群头像 (像微信群头像那样，在没有主动设置的情况下，根据群成员头像生成群头像)

        // todo 异步设置群组缓存

        return true;
    }

    /**
     * 校验数据
     * @author wangjinfei
     * @date 2025/7/6 14:50
     * @param dto
    */
    private void checkData(GroupDTO dto){
        String userId = UserContext.get().getUserId();
        List<String> memberIds = dto.getMemberIds();

        if(CollectionUtils.isEmpty(memberIds)){
            throw new BusinessException("群组成员不能为空");
        }
        if(memberIds.size() < 3 ){
            throw new BusinessException("群组成员不能少于3人");
        }
        if(memberIds.size() > 300 ){
            throw new BusinessException("群组成员不能多于300人");
        }
        if(!memberIds.contains(userId)){
            throw new BusinessException("群组成员必须包含自己");
        }
    }

    /**
     * 添加群组
     * @author wangjinfei
     * @date 2025/7/6 11:21
     * @param dto
     * @return Boolean
    */
    private Boolean addGroup(GroupDTO dto){
        String userId = UserContext.get().getUserId();

        Group group = new Group();
        group.setId(dto.getGroupId());
        group.setOwnerId(userId);
        group.setGroupName(setGroupName(dto.getMemberIds()));
        group.setCurrentMember(dto.getMemberIds().size());
        int insert = baseMapper.insert(group);

        return insert > 0;
    }

    /**
     * 设置群名称
     * @author wangjinfei
     * @date 2025/7/6 15:11
     * @param memberIds
     * @return String
    */
    private String setGroupName(List<String> memberIds){
        // 获取前三的成员id
        List<String> top3 = memberIds.subList(0, 3);
        StringBuilder groupName = new StringBuilder("");

        // 用前三个成员的用户名拼接
        for (int i = 0; i < top3.size(); i++) {
            String memberId = top3.get(i);
            String userCache = redisUtil.get(RedisEnum.USER.getKey() + memberId);
            if(StringUtils.isEmpty(userCache)){
                // todo 缓存中如果没有，从数据库中获取
                continue;
            }

            User user = JSONObject.parseObject(userCache, User.class);
            if(user != null && StringUtils.isNotEmpty(user.getUsername())){
                groupName.append(user.getUsername());
            }

            // 拼接分隔符
            if(i != top3.size() - 1){
                groupName.append("、");
            }
        }

        return groupName.toString();
    }
}
