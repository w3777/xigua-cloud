//package com.xigua.impl;
//
//import com.xigua.common.core.util.TokenUtil;
//import com.xigua.common.core.util.UserContext;
//import com.xigua.domain.dto.LoginDTO;
//import com.xigua.domain.token.UserToken;
//import com.xigua.service.TestService;
//import com.xigua.service.UserService;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.dubbo.config.annotation.DubboReference;
//import org.apache.dubbo.config.annotation.DubboService;
//import org.slf4j.MDC;
//
///**
// * @ClassName UserService
// * @Description TODO
// * @Author wangjinfei
// * @Date 2025/3/18 22:02
// */
//@Slf4j
//@RequiredArgsConstructor
//@DubboService
//public class UserServiceImpl implements UserService {
//    @DubboReference
//    private TestService testService;
//
//    @Override
//    public void testDubbo() {
//        String message = testService.send();
//        System.out.println("测试dubbo" + message);
//
//    }
//
//    @Override
//    public String login(LoginDTO loginDTO) {
//        String username = loginDTO.getUsername();
//        String password = loginDTO.getPassword();
//
//
//        if ("admin".equals(username) == false || "123456".equals(password) == false) {
//            return "用户名或密码错误";
//        }
//
//        TokenUtil tokenUtil = new TokenUtil();
//        UserToken userToken = new UserToken();
//        userToken.setUserId(1L);
//        userToken.setUserName(username);
//        userToken.setPhone("13888888888");
//
//        // todo 把登录成功的用户信息存入redis，并设置过期时间
//
//        // 生成token
//        String token = tokenUtil.genToken(userToken);
//        return token;
//    }
//
//    @Override
//    public void testToken() {
//        System.out.println("testService.getClass() = " + testService.getClass());
//        UserToken userToken = UserContext.get();
//        System.out.println("user模块获取，userToken = " + userToken);
//
//        testService.testToken();
//    }
//
//    @Override
//    public void testTraceId() {
//        String traceId = MDC.get("traceId");
//        System.out.println("userService, traceId = " + traceId);
//
//        log.info("userService 测试");
//    }
//}
