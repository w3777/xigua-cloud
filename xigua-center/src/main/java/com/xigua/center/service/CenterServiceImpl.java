package com.xigua.center.service;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.domain.connect.Client;
import com.xigua.domain.dto.ChatMessageDTO;
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
    private final RedisUtil redisUtil;

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
        String userInServer = userIsOnline(receiverId);
        if(StringUtils.isEmpty(userInServer)){
            // todo 这里可以做离线消息存储, 可以存储到es后期做全文检索

            // 对方不在线 返回消息给发送者
            chatMessageDTO.setReceiverId(senderId);
            chatMessageDTO.setMessage("对方不在线");
            userInServer = userIsOnline(senderId);
        }

        // 获取接收者所在的节点信息
        String key = RedisEnum.CLIENT_CONNECT_CENTER.getKey() +
                userInServer.split(":")[1] + ":" + userInServer.split(":")[2];
        String value = redisUtil.get(key);
        Client client = JSONObject.parseObject(value, Client.class);

        // 把消息再发送到接收人所在节点
        sendMessage2Client(chatMessageDTO, client);
    }

    /**
     * 用户是否在线
     * @author wangjinfei
     * @date 2025/4/20 11:42
     * @param userId
     * @return Boolean
     */
    private String userIsOnline(String userId) {
        // 判断接收者是否在线
        String onlineKey = redisUtil.isValueInSet(userId, RedisEnum.ONLINE_USER.getKey() + "*");
        if(StringUtils.isEmpty(onlineKey)){
            log.info("------->>>>>>> 用户：{}，不在线", userId);
        }
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
}
