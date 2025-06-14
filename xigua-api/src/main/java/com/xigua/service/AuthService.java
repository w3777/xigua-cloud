package com.xigua.service;

import com.xigua.domain.dto.LoginDTO;
import com.xigua.domain.dto.RegisterUserDTO;
import com.xigua.domain.entity.User;

/**
 * @ClassName AuthService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/12 21:03
 */
public interface AuthService {
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
    String login(LoginDTO loginDTO);

    /**
     * 创建token
     * @author wangjinfei
     * @date 2025/5/10 12:20
     * @param user
     * @return String
     */
    String createToken(User user);
}
