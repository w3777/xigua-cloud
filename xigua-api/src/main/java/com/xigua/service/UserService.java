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

    /**
     * 登录
     * @author wangjinfei
     * @date 2025/5/7 13:40
     * @param loginDTO
     * @return Boolean
    */
    Boolean login(LoginDTO loginDTO);
}
