package com.xigua.impl;

import com.xigua.service.TestService;
import com.xigua.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @ClassName UserService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/3/18 22:02
 */
@RequiredArgsConstructor
@DubboService
public class UserServiceImpl implements UserService {
    @DubboReference
    private TestService testService;

    @Override
    public void testDubbo() {
        String message = testService.send();
        System.out.println("测试dubbo" + message);

    }
}
