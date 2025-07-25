package com.xigua.demo.service;

import com.xigua.common.core.util.UserContext;
import com.xigua.common.core.model.UserToken;
import com.xigua.api.service.TestService;
import com.xigua.api.service.UserService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.slf4j.MDC;

/**
 * @ClassName UserService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/3/18 20:11
 */
@Slf4j
@Data
@RequiredArgsConstructor
@DubboService
public class TestServiceImpl implements TestService {
    @DubboReference
    private UserService userService;

    @Override
    public String send() {
        return "hello,我是TestService";
    }

    @Override
    public void testToken() {
        UserToken userToken = UserContext.get();
        System.out.println("demo模块获取，userToken = " + userToken);
    }

    @Override
    public void testTraceId() {
        String traceId = MDC.get("traceId");
        System.out.println("testService, traceId = " + traceId);

        log.info("testService 测试");
    }
}
