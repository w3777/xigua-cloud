package com.xigua.center.chatMessage;


import com.alibaba.fastjson2.JSONObject;
import com.xigua.api.service.CenterService;
import com.xigua.api.service.GroupMemberService;
import com.xigua.api.service.MessageReadService;
import com.xigua.common.core.util.DateUtil;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.bo.*;
import com.xigua.domain.connect.Client;
import com.xigua.domain.dto.ChatMessageDTO;
import com.xigua.domain.entity.Group;
import com.xigua.domain.entity.MessageRead;
import com.xigua.domain.entity.User;
import com.xigua.domain.enums.*;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @ClassName PrivateChatMessageService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/8/17 15:50
 */
@Component
public class GroupChatMessageService extends AbstractChatMessageService {
    @Autowired
    private Sequence sequence;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private MessageReadService messageReadService;
    @Autowired
    private CenterService centerService;
    @DubboReference
    private GroupMemberService groupMemberService;

    @Override
    protected ChatType getChatType() {
        return ChatType.TWO;
    }

    /**
     * 保存未读消息
     * @author wangjinfei
     * @date 2025/8/17 15:49
     * @param chatMessageDTO
     * @return Boolean
     */
    @Override
    public Boolean saveMessageUnread(ChatMessageDTO chatMessageDTO) {
        Set<String> groupMemberIds = groupMemberService.getGroupMembers(chatMessageDTO.getReceiverId());
        if (CollectionUtils.isEmpty(groupMemberIds)){
            return false;
        }

        List<MessageRead> messageReads = new ArrayList<>();
        // 给群组成员添加未读消息
        for (String groupMemberId : groupMemberIds) {
            MessageRead messageRead = new MessageRead();
            messageRead.setId(sequence.nextNo());
            messageRead.setMessageId(chatMessageDTO.getChatMessageId());
            messageRead.setSenderId(chatMessageDTO.getSenderId());
            messageRead.setReceiverId(groupMemberId);
            messageRead.setIsRead(MessageReadStatus.UNREAD.getType());
            // ws拿不到threadLocal存储的当前用户 （此时的发送人就是创建人）
            messageRead.setCreateBy(chatMessageDTO.getSenderId());
            messageReads.add(messageRead);
        }

        // 批量保存未读消息
        Boolean b = messageReadService.saveBatchPlus(messageReads);

        return b;
    }

    /**
     * 最后一条消息  (消息列表)
     * @author wangjinfei
     * @date 2025/8/17 17:09
     * @param chatMessageDTO
     */
    @Override
    public void lastMessage(ChatMessageDTO chatMessageDTO) {
        String groupId = chatMessageDTO.getReceiverId();
        Set<String> groupMemberIds = groupMemberService.getGroupMembers(chatMessageDTO.getReceiverId());
        if (CollectionUtils.isEmpty(groupMemberIds)){
            return;
        }

        for (String groupMemberId : groupMemberIds) {
            long timestamp = System.currentTimeMillis();

            // 存储redis 最后消息
            redisUtil.zsadd(RedisEnum.LAST_MES.getKey() + groupMemberId, groupId, timestamp);
            // 存储redis 最后消息内容
            LastMessageBO lastMessageBO = chatMessageDTO2LastMessageBO(chatMessageDTO);
            redisUtil.hashPut(RedisEnum.LAST_MES_CONTENT.getKey() + groupMemberId, groupId, JSONObject.toJSONString(lastMessageBO));
        }
    }

    /**
     * 封装最后消息内容
     * @author wangjinfei
     * @date 2025/7/18 13:46
     * @param chatMessageDTO
     * @return LastMessageBO
     */
    private LastMessageBO chatMessageDTO2LastMessageBO(ChatMessageDTO chatMessageDTO){
        LastMessageBO lastMessageBO = new LastMessageBO();
        LastMessageContentBO lastMessageContent = new LastMessageContentBO();
        String senderId = chatMessageDTO.getSenderId();
        String groupId = chatMessageDTO.getReceiverId();

        lastMessageBO.setChatId(groupId);
        lastMessageBO.setChatType(ChatType.TWO.getType());
        lastMessageBO.setUpdateTime(System.currentTimeMillis());

        // 群组
        String groupCache = redisUtil.get(RedisEnum.GROUP.getKey() + groupId);
        if(StringUtils.isNotEmpty(groupCache)){
            Group group = JSONObject.parseObject(groupCache, Group.class);
            lastMessageBO.setChatName(group.getGroupName());
            lastMessageBO.setAvatar(group.getGroupAvatar());
        }else{
            // 从数据库获取用户信息
            lastMessageBO.setAvatar("");
            lastMessageBO.setChatName("");
        }

        // 封装 最后消息内容
        String userCache = redisUtil.get(RedisEnum.USER.getKey() + senderId);
        if(StringUtils.isNotEmpty(userCache)){
            User user = JSONObject.parseObject(userCache, User.class);
            lastMessageContent.setContent(user.getUsername() + ": " + chatMessageDTO.getMessage());
        }
        lastMessageBO.setLastMessageContent(lastMessageContent);

        return lastMessageBO;
    }

    /**
     * 聊天消息发送到接收人
     * @author wangjinfei
     * @date 2025/8/17 17:14
     * @param chatMessageDTO
     */
    @Override
    public void chatMessage2Receiver(ChatMessageDTO chatMessageDTO) {
        Set<String> groupMemberIds = groupMemberService.getGroupMembers(chatMessageDTO.getReceiverId());
        if (CollectionUtils.isEmpty(groupMemberIds)){
            return;
        }

        for (String groupMemberId : groupMemberIds) {
            // 群聊 不用给自己推消息
            if(groupMemberId.equals(chatMessageDTO.getSenderId())){
                continue;
            }

            /**
             * 下面要获取每个群员节点，再进行推送
             * 本地发送群聊消息广播到每个在线群员都有延迟，更不要说体验版、线上了
             * todo 可以mq + 线程池每个节点发送一批，就是多消费者思想
            */

            // 获取接收人所在的节点信息
            String receiverInServer = centerService.onlineUser(groupMemberId);
            if(StringUtils.isEmpty(receiverInServer)){
                // 如果接收人不在线，直接返回，不做后续处理
                continue;
            }
            // 获取接收者所在的节点信息
            String key = RedisEnum.CLIENT_CONNECT_CENTER.getKey() +
                    receiverInServer.split(":")[1] + ":" + receiverInServer.split(":")[2];
            String value = redisUtil.get(key);
            Client client = JSONObject.parseObject(value, Client.class);

            // 此时的接收人是群组成员
            ChatMessageDTO chatMessageDTO2 = new ChatMessageDTO();
            BeanUtils.copyProperties(chatMessageDTO, chatMessageDTO2);
            chatMessageDTO2.setSenderId(chatMessageDTO.getReceiverId());
            chatMessageDTO2.setSender(getRealSender(chatMessageDTO.getSenderId()));
            chatMessageDTO2.setReceiverId(groupMemberId);
            chatMessageDTO2.setSubType(MessageSubType.MES_RECEIVE.getType());
            // 实时推送消息，发送到接收人所在节点
            centerService.sendMessage2Client(chatMessageDTO2, client);
        }
    }

    /**
     * 真正发送人
     * @author wangjinfei
     * @date 2025/8/18 21:25
     * @param realSenderId
     * @return SenderBO
    */
    private SenderBO getRealSender(String realSenderId){
        SenderBO senderBO = new SenderBO();

        // 获取真正发送人缓存
        String userCache = redisUtil.get(RedisEnum.USER.getKey() + realSenderId);
        if(StringUtils.isNotEmpty(userCache)){
            User user = JSONObject.parseObject(userCache, User.class);
            senderBO.setUsername(user.getUsername());
            senderBO.setAvatar(user.getAvatar());
        }

        return senderBO;
    }

    /**
     * 未读消息数量
     * @author wangjinfei
     * @date 2025/8/17 17:25
     * @param chatMessageDTO
     */
    @Override
    public void unreadMessageCount(ChatMessageDTO chatMessageDTO) {
        String senderId = chatMessageDTO.getSenderId();
        Set<String> groupMemberIds = groupMemberService.getGroupMembers(chatMessageDTO.getReceiverId());
        if (CollectionUtils.isEmpty(groupMemberIds)){
            return;
        }

        for (String groupMemberId : groupMemberIds) {
            // 获取接收人所在的节点信息
            String receiverInServer = centerService.onlineUser(chatMessageDTO.getReceiverId());
            if(StringUtils.isEmpty(receiverInServer)){
                return;
            }

            // 获取接收人打开的聊天窗口好友是谁
            String receiverActiveFriend = redisUtil.get(RedisEnum.CURRENT_CHAT_WINDOW.getKey() + groupMemberId);

            // 存储redis 好友未读数量
            String groupUnreadCountKey = RedisEnum.GROUP_UNREAD_COUNT.getKey() + groupMemberId;

            // 接收人在线 && 打开聊天框是发送人
            if(StringUtils.isNotEmpty(receiverInServer) && senderId.equals(receiverActiveFriend)){
                // 未读消息清零
                redisUtil.hashPut(groupUnreadCountKey, senderId, 0);
            }else{
                /**
                 * 如果使用hash先获取，再put +1，时间复杂度是O(1) + O(1)
                 * hincrby 是原子操作，时间复杂度是O(1)
                 * 时间复杂度：O(1) > O(1) + O(1)
                 */

                // 使用redis中的hincrby命令  未读消息 + 1
                redisUtil.hincrby(groupUnreadCountKey, senderId, 1);
            }
        }
    }

    /**
     * 消息未读到消息已读
     * @author wangjinfei
     * @date 2025/8/17 16:33
     * @param chatMessageDTO
     */
    @Override
    public void unread2Read(ChatMessageDTO chatMessageDTO) {
        String senderId = chatMessageDTO.getSenderId();
        Set<String> groupMemberIds = groupMemberService.getGroupMembers(chatMessageDTO.getReceiverId());
        if (CollectionUtils.isEmpty(groupMemberIds)){
            return;
        }

        for (String groupMemberId : groupMemberIds) {
            // 获取接收人所在的节点信息
            String receiverInServer = centerService.onlineUser(chatMessageDTO.getReceiverId());
            if(StringUtils.isEmpty(receiverInServer)){
                return;
            }

            // 获取接收人打开的聊天窗口好友是谁
            String receiverActiveFriend = redisUtil.get(RedisEnum.CURRENT_CHAT_WINDOW.getKey() + groupMemberId);
            if (StringUtils.isEmpty(receiverActiveFriend)) {
                return;
            }

            if (!receiverActiveFriend.equals(senderId)) {
                return;
            }

            // 接收人打开的聊天窗口好友是发送人，发送已读通知  （对方在线 && 对方打开的聊天框是发送人）
            // 获取发送人所在的节点信息
            String userInServer = centerService.onlineUser(senderId);
            if (StringUtils.isEmpty(userInServer)) {
                return;
            }
            String key = RedisEnum.CLIENT_CONNECT_CENTER.getKey() +
                    userInServer.split(":")[1] + ":" + userInServer.split(":")[2];
            String value = redisUtil.get(key);
            Client client = JSONObject.parseObject(value, Client.class);

            // 系统推送已读通知
            ChatMessageDTO dto = new ChatMessageDTO();
            dto.setSenderId(Sender.SYSTEM.getSender());
            dto.setReceiverId(senderId);
            dto.setMessageType(MessageType.CHAT.getType());
            dto.setSubType(MessageSubType.MES_READ.getType());
            String readChatMessageIds = JSONObject.toJSONString(Arrays.asList(chatMessageDTO.getChatMessageId()));
            String json = JSONObject.of("readChatMessageIds", readChatMessageIds).toJSONString();

            dto.setMessage(json);
            dto.setCreateTime(DateUtil.formatDateTime(LocalDateTime.now(), DateUtil.DATE_TIME_FORMATTER));
            centerService.sendMessage2Client(dto, client);

            // 标记为已读
            messageReadService.markRead(chatMessageDTO.getChatMessageId(), groupMemberId);
        }
    }
}
