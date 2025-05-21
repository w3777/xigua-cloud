package com.xigua.center.service;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.connect.Client;
import com.xigua.domain.dto.ChatMessageDTO;
import com.xigua.domain.entity.ChatMessage;
import com.xigua.domain.enums.ChatMessageIsRead;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.service.ClientService;
import com.xigua.service.CenterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.ReferenceConfig;
import org.apache.dubbo.config.annotation.DubboService;
import org.apache.dubbo.rpc.cluster.specifyaddress.Address;
import org.apache.dubbo.rpc.cluster.specifyaddress.UserSpecifiedAddressUtil;

import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName CenterServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/4/20 10:12
 */
@Slf4j
@RequiredArgsConstructor
@DubboService
public class CenterServiceImpl implements CenterService {
    private final Sequence sequence;
    private final RedisUtil redisUtil;
    private final ChatMessageServiceImpl chatMessageService;


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

        // 存储在线用户
        String onlineUserKey = RedisEnum.ONLINE_USER.getKey() + host + ":" + port;
        redisUtil.sadd(onlineUserKey, userId);

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

        // 如果当前节点没有在线用户了，移除节点信息
        String wsClientKey = RedisEnum.CLIENT_CONNECT_CENTER.getKey() + host + ":" + port;
        long wsClientSize = redisUtil.scard(onlineUserKey);
        if(wsClientSize == 0){
            // 客户端ws节点信息从redis移除
            redisUtil.del(wsClientKey);
        }

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
        String senderId = chatMessageDTO.getSenderId();
        String receiverId = chatMessageDTO.getReceiverId();

        // 判断接收者是否在线
        String userInServer = onlineUser(receiverId);
        if(StringUtils.isEmpty(userInServer)){
            // 这里做离线消息存储
            // db处理 mysql持久化 redis缓存
            messageDbHandle(chatMessageDTO);
            return;
        }

        // 获取接收者所在的节点信息
        String key = RedisEnum.CLIENT_CONNECT_CENTER.getKey() +
                userInServer.split(":")[1] + ":" + userInServer.split(":")[2];
        String value = redisUtil.get(key);
        Client client = JSONObject.parseObject(value, Client.class);

        // 实时推送消息，发送到接收人所在节点
        sendMessage2Client(chatMessageDTO, client);

        // db处理 mysql持久化 redis缓存
        messageDbHandle(chatMessageDTO);
    }

    /**
     * 用户是否在线
     * @author wangjinfei
     * @date 2025/4/20 11:42
     * @param userId
     * @return Boolean
     */
    private String onlineUser(String userId) {
        // 获取在线用户所在的节点信息
        String onlineKey = redisUtil.isValueInSet(userId, RedisEnum.ONLINE_USER.getKey() + "*");
        return onlineKey;
    }

    /**
     * db处理 mysql持久化 redis缓存最后聊天消息
     * @author wangjinfei
     * @date 2025/5/20 22:02
     * @param chatMessageDTO
    */
    private void messageDbHandle(ChatMessageDTO chatMessageDTO){
        String senderId = chatMessageDTO.getSenderId();
        String receiverId = chatMessageDTO.getReceiverId();
        String message = chatMessageDTO.getMessage();

        // 封装聊天消息
        ChatMessage chatMessage = new ChatMessage();
        chatMessage.setId(sequence.nextNo());
        chatMessage.setSenderId(senderId);
        chatMessage.setReceiverId(receiverId);
        chatMessage.setMessage(message);
        // ws拿不到threadLocal存储的当前用户 （此时的发送人就是创建人）
        chatMessage.setCreateBy(senderId);
        // 消息默认全部未读，已读状态由前端主动修改
        chatMessage.setIsRead(ChatMessageIsRead.UNREAD.getType());
        chatMessageService.save(chatMessage);
        // 注意 不采用双写同步es，后期用同步工具解决，减少侵入性

        long timestamp = System.currentTimeMillis();
        // 存储redis 最后消息的好友（这样的key可以保证唯一）
        redisUtil.zsadd(RedisEnum.LAST_MES_FRIEND.getKey() + receiverId, senderId, timestamp);
        // 存储redis 最后消息
        redisUtil.hashPut(RedisEnum.LAST_MES.getKey() + receiverId, senderId, JSONObject.toJSONString(chatMessageDTO));
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
