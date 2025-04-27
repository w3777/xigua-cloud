package com.xigua.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xigua.domain.dto.LoginDTO;
import com.xigua.domain.dto.RegisterUserDTO;
import com.xigua.domain.entity.User;

/**
 * @ClassName UserService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/3/18 20:44
 */
public interface UserService extends IService<User> {
    void testDubbo();

    String login(LoginDTO loginDTO);

    void testToken();

    void testTraceId();

    /**
     * 注册
     * @author wangjinfei
     * @date 2025/4/27 9:53
     * @param dto
     * @return Boolean
    */
    Boolean register(RegisterUserDTO dto);
}
