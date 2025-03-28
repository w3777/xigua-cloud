package com.xigua.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.client.TomcatWebSocketClient;
import org.springframework.web.reactive.socket.client.WebSocketClient;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.upgrade.TomcatRequestUpgradeStrategy;

/**
 * @ClassName WebSocketConfig
 * @Description websocket通过网关代理的配置
 * @Author wangjinfei
 * @Date 2025/1/23 11:50
 */
@Component
public class WebSocketConfig {
    /**
     * class org.apache.catalina.connector.ResponseFacade cannot be cast to class reactor.netty.http.server.HttpServerResponse
     * 网关默认使用是基于 Netty 容器的 RequestUpgradeStrategy 和 WebSocketClient 导致错误，声明 Tomcat 容器对应的 Bean 来覆盖它就可以解决这个问题
    */

    @Bean
    @Primary
    public WebSocketClient tomcatWebSocketClient() {
        // 使用tomcat的websocket客户端
        return new TomcatWebSocketClient();
    }

    @Bean
    @Primary
    public RequestUpgradeStrategy requestUpgradeStrategy() {
        // 使用tomcat请求响应策略
        return new TomcatRequestUpgradeStrategy();
    }
}
