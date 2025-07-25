package com.xigua.api.service;

import com.xigua.domain.connect.Client;
import com.xigua.domain.dto.ChatMessageDTO;

import java.util.Set;

/**
 * @ClassName ConnectService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/4/20 10:11
 */
public interface CenterService {
    /**
     * 客户端注册上线
     * @author wangjinfei
     * @date 2025/4/20 10:23
     * @param client
    */
    void clientRegister(Client client, String userId);

    /**
     * 客户端注销下线
     * @author wangjinfei
     * @date 2025/4/24 15:41
     * @param client
     * @param userId
    */
    void clientDeregister(Client client, String userId);

    /**
     * 接收来自客户端的消息
     * @author wangjinfei
     * @date 2025/4/20 15:06
     * @param chatMessageDTO
    */
    void receiveMessage4Client(ChatMessageDTO chatMessageDTO);

    /**
     * 发送消息到客户端
     * @author wangjinfei
     * @date 2025/4/20 11:11
     * @param chatMessageDTO
    */
    void sendMessage2Client(ChatMessageDTO chatMessageDTO, Client client);

    /**
     * 用户是否在线
     * @author wangjinfei
     * @date 2025/4/20 11:42
     * @param userId
     * @return Boolean
     */
    String onlineUser(String userId);

    /**
     * 获取在线人员id
     * @author wangjinfei
     * @date 2025/4/23 19:52
     * @return List<String>
    */
    Set<String> getOnlineId();

    /**
     * 根据用户id判断是否在线
     * @author wangjinfei
     * @date 2025/5/18 14:12
     * @param userId
     * @return Boolean
    */
    Boolean isOnline(String userId);
}
