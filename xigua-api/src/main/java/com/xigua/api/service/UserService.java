package com.xigua.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xigua.domain.dto.LoginDTO;
import com.xigua.domain.dto.RegisterUserDTO;
import com.xigua.domain.entity.User;
import com.xigua.domain.vo.UserSearchVO;

import java.util.List;

/**
 * @ClassName UserService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/3/18 20:44
 */
public interface UserService extends IService<User> {

    /**
     * 获取当前登录用户信息
     * @author wangjinfei
     * @date 2025/5/10 12:52
     * @return User
    */
    User getUserInfo();

    /**
     * 上传头像
     * @author wangjinfei
     * @date 2025/5/10 20:37
     * @param avatar
     * @return String
    */
    Boolean uploadAvatar(String avatar);

    /**
     * 更新用户信息
     * @author wangjinfei
     * @date 2025/5/11 20:34
     * @param user
     * @return Boolean
    */
    Boolean updateUserInfo(User user);

    /**
     * 根据用户名查询用户列表
     * @author wangjinfei
     * @date 2025/5/12 21:18
     * @param username
     * @return List<User>
    */
    List<UserSearchVO> getListByName(String username);

    /**
     * 根据id列表查询用户列表
     * @author wangjinfei
     * @date 2025/5/13 23:36
     * @param ids
     * @return List<User>
    */
    List<User> getListByIds(List<String> ids);

    /**
     * 根据用户名获取数量
     * @author wangjinfei
     * @date 2025/6/12 21:51
     * @param username
     * @return Long
    */
    Long getCountByName(String username);

    /**
     * 根据邮箱获取数量
     * @author wangjinfei
     * @date 2025/6/12 21:53
     * @param email
     * @return Long
    */
    Long getCountByEmail(String email);

    /**
     * 根据用户名获取用户
     * @author wangjinfei
     * @date 2025/6/12 21:54
     * @param username
     * @return User
    */
    User getByUsername(String username);

    /**
     * 根据id获取用户
     * @author wangjinfei
     * @date 2025/7/6 11:50
     * @param id
     * @return User
    */
    User getById(String id);

    /**
     * 添加用户到redis
     * @author wangjinfei
     * @date 2025/7/6 16:02
     * @param userId
    */
    Boolean addUser2Redis(String userId);

    /**
     * 获取所有用户id
     * @author wangjinfei
     * @date 2025/7/29 17:40
     * @return List<String>
    */
    List<String> getAllUserId();
}
