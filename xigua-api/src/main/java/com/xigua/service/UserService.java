package com.xigua.service;

import com.xigua.domain.dto.LoginDTO;

/**
 * @ClassName UserService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/3/18 20:44
 */
public interface UserService {
    void testDubbo();

    String login(LoginDTO loginDTO);

    void testToken();

    void testTraceId();
}
