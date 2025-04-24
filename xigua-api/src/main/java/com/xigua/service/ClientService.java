package com.xigua.service;

import com.xigua.domain.dto.ChatMessageDTO;

/**
 * @ClassName ClientService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/4/20 15:32
 */
public interface ClientService {
    /**
     * 当前用户所连接的ws节点信息注册到长连接服务器
     * @author wangjinfei
     * @date 2025/4/20 15:43
     * @param userId
     * @return Boolean
    */
    Boolean clientRegister2Center(String userId);

    /**
     * 当前用户所连接的ws节点信息注销到长连接服务器
     * @author wangjinfei
     * @date 2025/4/24 15:36
     * @param userId
     * @return Boolean
    */
    Boolean clientDeregister2Center(String userId);


    /**
     * 发消息到长连接服务器
     * @author wangjinfei
     * @date 2025/4/20 15:58
     * @param chatMessageDTO
    */
    void sendMessage2Center(ChatMessageDTO chatMessageDTO);

    /**
     * 接收来自长连接服务器的消息
     * @author wangjinfei
     * @date 2025/4/20 19:27
     * @param chatMessageDTO
    */
    void receiveMessage4Center(ChatMessageDTO chatMessageDTO);
}
