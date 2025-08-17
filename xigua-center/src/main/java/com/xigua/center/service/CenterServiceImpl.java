package com.xigua.center.service;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.center.wsMessage.MessageServiceFactory;
import com.xigua.center.wsMessage.AbstractMessageService;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.domain.connect.Client;
import com.xigua.domain.dto.ChatMessageDTO;
import com.xigua.domain.enums.MessageSubType;
import com.xigua.domain.enums.MessageType;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.api.service.ClientService;
import com.xigua.api.service.CenterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.cluster.specifyaddress.Address;
import org.apache.dubbo.rpc.cluster.specifyaddress.UserSpecifiedAddressUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName CenterServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/4/20 10:12
 */
@Slf4j
@Service
@DubboService
public class CenterServiceImpl implements CenterService {
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 客户端注册上线
     * @author wangjinfei
     * @date 2025/4/20 10:23
     * @param client
     */
    @Override
    public void clientRegister(Client client, String userId) {
        // 客户端ws节点信息存储到redis
        String host = client.getHost();
        Integer port = client.getPort();
        String key = RedisEnum.CLIENT_CONNECT_CENTER.getKey() + host + ":" + port;
        redisUtil.set(key, JSONObject.toJSONString(client));


        /**
         * todo可能服务器直接挂了，但redis还存储着在线用户
         * 需要维护一个过期时间,或者心跳检测
        */
        // 存储在线用户
        String onlineUserKey = RedisEnum.ONLINE_USER.getKey() + host + ":" + port;
        redisUtil.sadd(onlineUserKey, userId);

        // 通知好友用户上线
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setSenderId(userId);

        AbstractMessageService messageService = MessageServiceFactory.getMessageService(MessageType.NOTIFY.getType(), MessageSubType.FRIEND_ONLINE.getType());
        if(messageService == null){
            log.error("未找到消息服务：消息主类型：{}，消息子类型：{}", MessageType.NOTIFY.getType(), MessageSubType.FRIEND_ONLINE.getType());
            throw new BusinessException("未找到消息服务");
        }
        messageService.handleMessage(chatMessageDTO);
        log.info("------->>>>>>> 用户：{}，所在服务器：{}，注册上线", userId, key);
    }

    /**
     * 客户端注销下线
     * @author wangjinfei
     * @date 2025/4/24 15:41
     * @param client
     * @param userId
     */
    @Override
    public void clientDeregister(Client client, String userId) {
        String host = client.getHost();
        Integer port = client.getPort();

        // redis移除在线用户
        String onlineUserKey = RedisEnum.ONLINE_USER.getKey() + host + ":" + port;
        redisUtil.srem(onlineUserKey, userId);

        // redis删除当前用户的活跃好友
        String currentActiveFriendKey = RedisEnum.CURRENT_CHAT_WINDOW.getKey() + userId;
        redisUtil.del(currentActiveFriendKey);

        // 如果当前节点没有在线用户了，移除节点信息
        String wsClientKey = RedisEnum.CLIENT_CONNECT_CENTER.getKey() + host + ":" + port;
        long wsClientSize = redisUtil.scard(onlineUserKey);
        if(wsClientSize == 0){
            // 客户端ws节点信息从redis移除
            redisUtil.del(wsClientKey);
        }

        // 通知好友用户下线
        ChatMessageDTO chatMessageDTO = new ChatMessageDTO();
        chatMessageDTO.setSenderId(userId);

        AbstractMessageService messageService = MessageServiceFactory.getMessageService(MessageType.NOTIFY.getType(), MessageSubType.FRIEND_OFFLINE.getType());
        if(messageService == null){
            log.error("未找到消息服务：消息主类型：{}，消息子类型：{}", MessageType.NOTIFY.getType(), MessageSubType.FRIEND_OFFLINE.getType());
            throw new BusinessException("未找到消息服务");
        }
        messageService.handleMessage(chatMessageDTO);

        log.info("------->>>>>>> 用户：{}，所在服务器：{}，注销下线", userId, wsClientKey);
    }

    /**
     * 接收来自客户端的消息
     * @author wangjinfei
     * @date 2025/4/20 15:06
     * @param chatMessageDTO
     */
    @Override
    public void receiveMessage4Client(ChatMessageDTO chatMessageDTO) {
        String messageType = chatMessageDTO.getMessageType();
        String subType = chatMessageDTO.getSubType();

        log.info("------->>>>>>> 接收到客户端消息：{}", chatMessageDTO);

        // 根据主类和子类型获取消息service
        AbstractMessageService messageService = MessageServiceFactory.getMessageService(messageType, subType);
        if(messageService == null){
            log.error("未找到消息服务：消息主类型：{}，消息子类型：{}", messageType, subType);
            throw new BusinessException("未找到消息服务");
        }

        // 根据不同的消息service处理消息
        messageService.handleMessage(chatMessageDTO);
    }

    /**
     * 用户是否在线
     * @author wangjinfei
     * @date 2025/4/20 11:42
     * @param userId
     * @return Boolean
     */
    public String onlineUser(String userId) {
        // 获取在线用户所在的节点信息
        String onlineKey = redisUtil.isValueInSet(userId, RedisEnum.ONLINE_USER.getKey() + "*");
        return onlineKey;
    }

    /**
     * 发送消息到指定节点
     * @author wangjinfei
     * @date 2025/4/20 11:11
     * @param chatMessageDTO
     */
    @Override
    public void sendMessage2Client(ChatMessageDTO chatMessageDTO, Client client) {
        String host = client.getHost();
        Integer dubboPort = client.getDubboPort();

        // 通过ip和port指定节点 进行rpc调用 发送消息
        ReferenceConfig<ClientService> referenceConfig = new ReferenceConfig<>();
        referenceConfig.setInterface(ClientService.class);
        ClientService clientService = referenceConfig.get();
        UserSpecifiedAddressUtil.setAddress(new Address(host, dubboPort, true));
        clientService.receiveMessage4Center(chatMessageDTO);
    }

    /**
     * 获取在线人员id
     * @author wangjinfei
     * @date 2025/4/23 19:52
     * @return List<String>
     */
    @Override
    public Set<String> getOnlineId() {
        Set<String> onlineIds = new HashSet<>();

        // 获取所有的节点信息
        String key = RedisEnum.CLIENT_CONNECT_CENTER.getKey() + "*";
        Set<String> wsServers = redisUtil.getKeysByKey(key);

        // 根据节点信息获取在线用户
        for (String wsServer : wsServers) {
            int index = wsServer.indexOf(":");
            String wsServerKey = RedisEnum.ONLINE_USER.getKey() + wsServer.substring(index + 1);
            Set<Object> members = redisUtil.members(wsServerKey);

            members.forEach(member -> {
                onlineIds.add((String) member);
            });
        }

        return onlineIds;
    }

    /**
     * 根据用户id判断是否在线
     * @author wangjinfei
     * @date 2025/5/18 14:12
     * @param userId
     * @return Boolean
     */
    @Override
    public Boolean isOnline(String userId) {
        String onlineKey = onlineUser(userId);
        if(StringUtils.isEmpty(onlineKey)){
            return false;
        }
        return true;
    }
}
