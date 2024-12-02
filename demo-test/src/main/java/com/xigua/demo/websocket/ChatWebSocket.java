package com.xigua.demo.websocket;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.demo.domain.dto.ChatMessageDTO;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName XGChannel
 * @Description
 * 使用 @ServerEndpoint 注解表示此类是一个 WebSocket 端点
 * 通过 value 注解，指定 websocket 的路径
 * OnOpen 表示有浏览器链接过来的时候被调用
 * OnClose 表示浏览器发出关闭请求的时候被调用
 * OnMessage 表示浏览器发消息的时候被调用
 * OnError 表示有错误发生，比如网络断开了等等
 * @Author wangjinfei
 * @Date 2024/12/2 14:40
 */
@Data
@Component
@Slf4j
@ServerEndpoint(value = "/chat/{userId}")
public class ChatWebSocket {
    // 存储所有在线用户的 WebSocket的Session
    private static final Map<String, Session> chatWebSocketMap = new ConcurrentHashMap<>();

    // 连接打开
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session){
        // 根据用户保存session
        chatWebSocketMap.put(userId,session);
        log.info("---->>>>>[websocket] 新的连接：userId={}",userId);
    }

    // 连接关闭
    @OnClose
    public void onClose(@PathParam("userId") String userId,CloseReason closeReason){
        chatWebSocketMap.remove(userId);
        log.info("---->>>>>[websocket] 连接断开：userId={}，reason={}",userId,closeReason);
    }

    // 收到消息
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        log.info("---->>>>>[websocket] 收到消息：sessionId={}，message={}", session.getId(), message);

        ChatMessageDTO chatMessageDTO = JSONObject.parseObject(message, ChatMessageDTO.class);
        String senderId = chatMessageDTO.getSenderId();
        String receiverId = chatMessageDTO.getReceiverId();

        // todo 这里的聊天信息可以做持久化，存储到mongodb，也可以存储到es后期做全文检索

        // 找到接收者会话
        Session receiverSess = chatWebSocketMap.get(receiverId);
        // 转发消息到接收者
        if(receiverSess != null && receiverSess.isOpen()){
            receiverSess.getBasicRemote().sendText(message);
        } else {
            session.getBasicRemote().sendText("用户：" + receiverId + "不在线");
        }
    }

    // 连接异常
    @OnError
    public void onError(Session session,Throwable throwable) throws IOException {
        log.info("---->>>>>[websocket] 连接异常：throwable={}", throwable.getMessage());
    }
}
