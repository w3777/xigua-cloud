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
    Boolean clientRegister2Server(String userId);


    /**
     * 发消息到长连接服务器
     * @author wangjinfei
     * @date 2025/4/20 15:58
     * @param chatMessageDTO
    */
    void sendMessage2Server(ChatMessageDTO chatMessageDTO);

    /**
     * 接收来自长连接服务器的消息
     * @author wangjinfei
     * @date 2025/4/20 19:27
     * @param chatMessageDTO
    */
    void receiveMessage4Server(ChatMessageDTO chatMessageDTO);
}
