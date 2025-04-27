package com.xigua.client.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xigua.client.mapper.UserMapper;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.domain.dto.LoginDTO;
import com.xigua.domain.dto.RegisterUserDTO;
import com.xigua.domain.entity.User;
import com.xigua.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

/**
 * @ClassName UserServiceImpl
 * @Description
 * @Author wangjinfei
 * @Date 2025/4/27 9:53
 */
@DubboService
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Override
    public void testDubbo() {

    }

    @Override
    public String login(LoginDTO loginDTO) {
        return null;
    }

    @Override
    public void testToken() {

    }

    @Override
    public void testTraceId() {

    }

    /**
     * 注册
     * @author wangjinfei
     * @date 2025/4/27 9:53
     * @param dto
     * @return Boolean
     */
    @Override
    public Boolean register(RegisterUserDTO dto) {
        String email = dto.getEmail();

        // 邮箱是否存在
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        if(count > 0){
            throw new BusinessException("邮箱已存在");
        }

        User user = new User();
        BeanUtils.copyProperties(dto,user);

        // md5加密
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));

        int insert = baseMapper.insert(user);
        return insert > 0;
    }
}
