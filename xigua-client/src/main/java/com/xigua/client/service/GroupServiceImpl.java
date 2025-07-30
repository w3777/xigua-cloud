package com.xigua.client.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xigua.api.service.UserService;
import com.xigua.client.mapper.GroupMapper;
import com.xigua.client.utils.NineCellImageUtil;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.common.core.transaction.TransactionSyncMgr;
import com.xigua.common.core.util.DateUtil;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.core.util.UserContext;
import com.xigua.common.mq.constant.TopicEnum;
import com.xigua.common.mq.producer.MessageQueueProducer;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.dto.GroupDTO;
import com.xigua.domain.entity.Group;
import com.xigua.domain.entity.GroupMember;
import com.xigua.domain.entity.User;
import com.xigua.domain.enums.GroupRole;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.api.service.GroupMemberService;
import com.xigua.api.service.GroupService;
import com.xigua.domain.vo.GroupDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;

/**
 * @ClassName GroupServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/6 11:07
 */
@Slf4j
@Service
@DubboService
public class GroupServiceImpl extends ServiceImpl<GroupMapper, Group> implements GroupService {
    @Autowired
    private Sequence sequence;
    @Autowired
    private RedisUtil redisUtil;
    @DubboReference
    private GroupMemberService groupMemberService;
    @Autowired
    private NineCellImageUtil nineCellImageUtil;
    @Autowired
    private MessageQueueProducer messageQueueProducer;
    @Autowired
    private UserService userService;

    /**
     * 创建群组
     * @author wangjinfei
     * @date 2025/7/6 11:07
     * @param dto
     * @return Boolean
     */
    @Override
    public Boolean createGroup(GroupDTO dto) throws Exception {
        String groupId = sequence.nextNo();
        String groupAvatar = null;
        List<String> memberIds = dto.getMemberIds();

        // 1.校验数据
        checkData(dto);

        // 2.异步生成群组头像
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try {
                return generateGroupAvatar(memberIds);
            } catch (Exception e) {
                log.error("--------->>>>>> 生成群组头像失败，群成员:{}", memberIds, e);
            }
            return null;
        });

        // 3.添加群组
        dto.setGroupId(groupId);

        // 3.1 如果生成群组头像完成，随着群组添加头像
        if(future.isDone()){
            groupAvatar = future.get();
            dto.setGroupAvatar(groupAvatar);
        }
        Group group = addGroup(dto);

        // 4.添加群成员
        Boolean addGroupMember = groupMemberService.addGroupMember(groupId, memberIds);

        // 5.如果群组头像没有随着添加保存（1.get阻塞获取群组头像  2.并根据群组id修改群组头像）
        if(StringUtils.isEmpty(groupAvatar)){
            groupAvatar = future.get();
            if (StringUtils.isNotEmpty(groupAvatar)){
                updateGroupAvatar(groupId, groupAvatar);
            }
        }

        // mq设置群组缓存
        Map<String, String> map = new HashMap<>();
        map.put("groupId", groupId);
        messageQueueProducer.syncSend(TopicEnum.GROUP_CACHE, map);

        return true;
    }

    /**
     * 生成群组头像
     * @author wangjinfei
     * @date 2025/7/12 19:31
     * @param memberIds
    */
    private String generateGroupAvatar(List<String> memberIds) throws Exception {
        List<BufferedImage> userAvatars = new ArrayList<>();

        // 获取群成员头像
        for (String memberId : memberIds) {
            String userCache = redisUtil.get(RedisEnum.USER.getKey() + memberId);
            if(StringUtils.isEmpty(userCache)){
                // todo 缓存中如果没有，从数据库中获取
                continue;
            }
            User user = JSONObject.parseObject(userCache, User.class);
            if(user != null && StringUtils.isNotEmpty(user.getAvatar())){
                BufferedImage image = ImageIO.read(new URL(user.getAvatar()));
                userAvatars.add(image);
            }
        }

        // 生成群头像
        String groupAvatar = nineCellImageUtil.generateNineCellImage(userAvatars);

        return groupAvatar;
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
    private Group addGroup(GroupDTO dto){
        String userId = UserContext.get().getUserId();

        Group group = new Group();
        group.setId(dto.getGroupId());
        group.setOwnerId(userId);
        group.setGroupName(setGroupName(dto.getMemberIds()));
        if(StringUtils.isNotEmpty(dto.getGroupAvatar())){
            group.setGroupAvatar(dto.getGroupAvatar());
        }
        group.setCurrentMember(dto.getMemberIds().size());
        int insert = baseMapper.insert(group);

        return group;
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

    /**
     * 修改群头像
     * @author wangjinfei
     * @date 2025/7/12 20:10
     * @param groupId
     * @param groupAvatar
     * @return Boolean
    */
    private Boolean updateGroupAvatar(String groupId, String groupAvatar){
        int update = baseMapper.update(new LambdaUpdateWrapper<Group>()
                .eq(Group::getId, groupId)
                .set(Group::getGroupAvatar, groupAvatar));

        return update > 0;
    }

    /**
     * 群组添加到缓存
     * @author wangjinfei
     * @date 2025/7/28 15:28
     * @param groupId
     * @return Boolean
     */
    @Override
    public Boolean addGroup2Redis(String groupId) {
        if(StringUtils.isEmpty(groupId)){
            return false;
        }

        Group group = baseMapper.selectById(groupId);
        if(group == null){
            return false;
        }

        // 群聊信息添加缓存
        redisUtil.set(RedisEnum.GROUP.getKey() + groupId, JSONObject.toJSONString(group));

        // 群成员添加缓存
        List<GroupMember> groupMembers = groupMemberService.getGroupMembersByGroupId(groupId);
        if(CollectionUtils.isNotEmpty(groupMembers)){
            for (GroupMember groupMember : groupMembers) {
                String userId = groupMember.getUserId();
                LocalDateTime createTime = groupMember.getCreateTime();

                // set 缓存群成员信息
                redisUtil.set(RedisEnum.GROUP_MEMBER.getKey() + groupId + ":" + userId, JSONObject.toJSONString(groupMember));

                // zset 缓存群成员id
                redisUtil.zsadd(RedisEnum.GROUP_MEMBER_ID.getKey() + groupId, userId, DateUtil.toEpochMilli(createTime));
            }
        }

        return true;
    }

    /**
     * 获取所有群组id
     * @author wangjinfei
     * @date 2025/7/29 17:50
     * @return List<String>
     */
    @Override
    public List<String> getAllGroupId() {
        return baseMapper.getAllGroupId();
    }

    /**
     * 获取群组详情
     * @author wangjinfei
     * @date 2025/7/30 19:35
     * @param groupId
     * @return GroupDetailVO
     */
    @Override
    public GroupDetailVO getGroupDetail(String groupId) {
        String userId = UserContext.get().getUserId();

        if(StringUtils.isEmpty(groupId)){
            throw new BusinessException("群组id不能为空");
        }

        // 从缓存中获取群组信息
        String groupCache = redisUtil.get(RedisEnum.GROUP.getKey() + groupId);
        if(StringUtils.isEmpty(groupCache)){
            throw new BusinessException("群组不存在");
        }

        Group group = JSONObject.parseObject(groupCache, Group.class);
        if(group == null){
            throw new BusinessException("群组不存在");
        }

        GroupDetailVO groupDetailVO = new GroupDetailVO();
        groupDetailVO.setGroupId(group.getId());
        groupDetailVO.setGroupName(group.getGroupName());
        groupDetailVO.setGroupAvatar(group.getGroupAvatar());
        groupDetailVO.setDescription(group.getDescription());
        groupDetailVO.setCurrentMember(group.getCurrentMember());

        // 格式化时间
        if(group.getCreateTime() != null){
            groupDetailVO.setCreateTime(DateUtil.formatDateTime(group.getCreateTime(), DateUtil.DATE_FORMATTER));
        }

        // 获取群主名称
        if(StringUtils.isNotEmpty(group.getOwnerId())){
            User user = userService.getById(group.getOwnerId());
            if(user != null){
                groupDetailVO.setGroupOwner(user.getUsername());
            }
        }

        // 获取加入时间
        LocalDateTime joinTime = groupMemberService.getJoinTime(groupId, userId);
        if(joinTime != null){
            groupDetailVO.setJoinTime(DateUtil.formatDateTime(joinTime, DateUtil.DATE_FORMATTER));
        }

        // 获取我的角色
        Integer role = groupMemberService.getGroupRole(groupId, userId);
        if(role != null){
            GroupRole groupRoleE = GroupRole.getByType(role);
            if(groupRoleE != null){
                groupDetailVO.setRoleName(groupRoleE.getDesc());
            }
        }

        return groupDetailVO;
    }
}
