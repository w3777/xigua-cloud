package com.xigua.gateway.websocket;

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
     * 由于websocket默认使用的是spring-webflux模型，项目中用的是tomcat和spring-webmvc,所以导致类型转换异常
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
