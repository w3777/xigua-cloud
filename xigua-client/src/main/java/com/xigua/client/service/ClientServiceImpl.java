package com.xigua.client.service;

import com.xigua.client.helper.SessionHelper;
import com.xigua.domain.connect.Client;
import com.xigua.domain.dto.ChatMessageDTO;
import com.xigua.service.ClientService;
import com.xigua.service.CenterService;
import jakarta.websocket.Session;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;

/**
 * @ClassName ClientServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/4/20 15:33
 */
@Slf4j
@RequiredArgsConstructor
@DubboService
public class ClientServiceImpl implements ClientService {
    private final ServletWebServerApplicationContext webServerAppContext;
    @DubboReference
    private final CenterService centerService;

    @Value("${dubbo.protocol.port}")
    private Integer dubboPort;

    /**
     * 当前用户所连接的ws节点信息注册到长连接服务器
     * @author wangjinfei
     * @date 2025/4/20 15:43
     * @param userId
     * @return Boolean
     */
    @Override
    public Boolean clientRegister2Center(String userId) {
        // 获取当前服务ip和端口
        int port = webServerAppContext.getWebServer().getPort();
        String host = null;
        try {
            // 获取本机的 IP 地址
            host = java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("----->>>>> 获取ip失败", e);
        }
        log.info("----->>>>> host:{},port:{}", host, port);

        Client client = new Client();
        client.setHost(host);
        client.setPort(port);
        client.setDubboPort(dubboPort);
        // ws节点信息注册到服务端
        centerService.clientRegister(client, userId);
        return true;
    }

    /**
     * 当前用户所连接的ws节点信息注销到长连接服务器
     * @author wangjinfei
     * @date 2025/4/24 15:36
     * @param userId
     * @return Boolean
     */
    @Override
    public Boolean clientDeregister2Center(String userId) {
        // 获取当前服务ip和端口
        int port = webServerAppContext.getWebServer().getPort();
        String host = null;
        try {
            // 获取本机的 IP 地址
            host = java.net.InetAddress.getLocalHost().getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("----->>>>> 获取ip失败", e);
        }
        log.info("----->>>>> host:{},port:{}", host, port);

        Client client = new Client();
        client.setHost(host);
        client.setPort(port);
        client.setDubboPort(dubboPort);

        // ws节点信息从服务端注销
        centerService.clientDeregister(client, userId);
        return true;
    }

    /**
     * 发消息到长连接服务器
     * @author wangjinfei
     * @date 2025/4/20 15:58
     * @param chatMessageDTO
     */
    @Override
    public void sendMessage2Center(ChatMessageDTO chatMessageDTO) {
        centerService.receiveMessage4Client(chatMessageDTO);
    }

    /**
     * 接收来自长连接服务器的消息
     * @author wangjinfei
     * @date 2025/4/20 19:27
     * @param chatMessageDTO
     */
    @Override
    public void receiveMessage4Center(ChatMessageDTO chatMessageDTO) {
        String receiverId = chatMessageDTO.getReceiverId();
        String message = chatMessageDTO.getMessage();

        Session session = SessionHelper.get(receiverId);
        if(session != null && session.isOpen()){
            try {
                session.getBasicRemote().sendText(message);
            } catch (Exception e) {
                log.error("发送消息失败", e);
            }
        }
    }
}
