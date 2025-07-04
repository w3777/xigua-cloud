package com.xigua.gateway.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName OpenAPIConfig
 * @Description
 * @Author wangjinfei
 * @Date 2025/3/28 9:15
 */
@OpenAPIDefinition
@Configuration
public class OpenAPIConfig {
    /*
    * http://localhost:8090/swagger-ui/index.html swagger网关统一访问路径
    * 转发到具体的服务接口路径
    */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI().info(new Info()
                .title("API Gateway Service")
                .description("API Gateway Service")
                .version("1.0.0"));
    }

    // demo的swagger路由转发配置
    @Bean
    public RouteLocator demoRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("demo-api", r -> r.path("/demo/v3/api-docs")
                        .filters(f -> f.rewritePath("/demo/(?<segment>.*)", "/demo/${segment}"))
                        .uri("http://127.0.0.1:8081"))
                .build();
    }

    // client的swagger路由转发配置
    @Bean
    public RouteLocator clientRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("client-api", r -> r.path("/client/v3/api-docs")
                        .filters(f -> f.rewritePath("/client/(?<segment>.*)", "/client/${segment}"))
                        .uri("http://127.0.0.1:8084"))
                .build();
    }

    // center的swagger路由转发配置
    @Bean
    public RouteLocator centerRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("center-api", r -> r.path("/center/v3/api-docs")
                        .filters(f -> f.rewritePath("/center/(?<segment>.*)", "/center/${segment}"))
                        .uri("http://127.0.0.1:8083"))
                .build();
    }

    // sso的swagger路由转发配置
    @Bean
    public RouteLocator ssoRoute(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("sso-api", r -> r.path("/sso/v3/api-docs")
                        .filters(f -> f.rewritePath("/sso/(?<segment>.*)", "/sso/${segment}"))
                        .uri("http://127.0.0.1:8082"))
                .build();
    }
}
