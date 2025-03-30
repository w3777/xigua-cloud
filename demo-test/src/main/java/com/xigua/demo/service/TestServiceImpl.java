package com.xigua.demo.service;

import com.xigua.common.core.util.UserContext;
import com.xigua.domain.token.UserToken;
import com.xigua.service.TestService;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @ClassName UserService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/3/18 20:11
 */
@DubboService
public class TestServiceImpl implements TestService {

    @Override
    public String send() {
        return "hello,我是TestService";
    }

    @Override
    public void testToken() {
        UserToken userToken = UserContext.get();
        System.out.println("demo模块获取，userToken = " + userToken);
    }
}
