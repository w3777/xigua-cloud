package com.xigua.client.ws;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.client.helper.SessionHelper;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.domain.dto.ChatMessageDTO;
import com.xigua.api.service.ClientService;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @ClassName ConnectController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/4/19 9:54
 */
@Data
@Slf4j
@Component
@ServerEndpoint(value = "/connect/{userId}")
public class WsServer {
    private static ClientService clientService;
    @DubboReference
    public void setClientService(ClientService clientService) {
        WsServer.clientService = clientService;
    }

    // 连接打开
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session){
        // todo 可以根据userId判断是否是合法用户，达到token鉴权的目的
        // 根据用户保存session
        SessionHelper.put(userId,session);
        log.info("---->>>>>[websocket] 新的连接：userId={}",userId);

        // 当前用户所连接的ws节点信息注册到长连接服务器
        clientService.clientRegister2Center(userId);
    }

    // 连接关闭
    @OnClose
    public void onClose(@PathParam("userId") String userId, CloseReason closeReason){
        SessionHelper.remove(userId);
        log.info("---->>>>>[websocket] 连接断开：userId={}，reason={}",userId,closeReason);

        // 当前用户所连接的ws节点信息注销到长连接服务器
        clientService.clientDeregister2Center(userId);
    }

    // 收到消息
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("---->>>>>[websocket] 收到消息：sessionId={}，message={}", session.getId(), message);
        ChatMessageDTO chatMessageDTO = JSONObject.parseObject(message, ChatMessageDTO.class);

        // 服务端收到消息的时间
        chatMessageDTO.setServerReceiveTime(System.currentTimeMillis());

        // 发消息到长连接服务器
        try{
            clientService.sendMessage2Center(chatMessageDTO);
        }catch (BusinessException e){
            log.error("---->>>>>[websocket] 发送消息到长连接服务器业务异常：", e);
        }catch (Exception e){
            log.error("---->>>>>[websocket] 发送消息到长连接服务器系统异常：", e);
        }
    }

    // 连接异常
    @OnError
    public void onError(Session session, Throwable throwable) throws IOException {
        log.info("---->>>>>[websocket] 连接异常：", throwable);
    }
}
