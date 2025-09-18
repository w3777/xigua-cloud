package com.xigua.client.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xigua.client.mapper.UserMapper;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.core.util.UserContext;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.entity.User;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.common.core.model.UserToken;
import com.xigua.domain.vo.UserSearchVO;
import com.xigua.api.service.CenterService;
import com.xigua.api.service.EmailService;
import com.xigua.api.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName UserServiceImpl
 * @Description
 * @Author wangjinfei
 * @Date 2025/4/27 9:53
 */
@Slf4j
@Service
@DubboService
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private Sequence sequence;
    @DubboReference
    private EmailService emailService;
    @DubboReference
    private CenterService centerService;

    /**
     * 获取当前登录用户信息
     * @author wangjinfei
     * @date 2025/5/10 12:52
     * @return User
     */
    @Override
    public User getUserInfo() {
        UserToken userToken = UserContext.get();
        User user = baseMapper.selectById(userToken.getUserId());
        return user;
    }

    /**
     * 上传头像
     * @author wangjinfei
     * @date 2025/5/10 20:37
     * @param avatar
     * @return String
     */
    @Override
    public Boolean uploadAvatar(String avatar) {
        UserToken userToken = UserContext.get();
        String userId = userToken.getUserId();
        baseMapper.update(new User(), new LambdaUpdateWrapper<User>().eq(User::getId, userId)
                .set(User::getAvatar, avatar));

        // 缓存用户信息
        User user = baseMapper.selectById(userId);
        redisUtil.set(RedisEnum.USER.getKey() + userId, JSONObject.toJSONString(user));
        return true;
    }

    /**
     * 更新用户信息
     * @author wangjinfei
     * @date 2025/5/11 20:34
     * @param user
     * @return Boolean
     */
    @Override
    public Boolean updateUserInfo(User user) {
        UserToken userToken = UserContext.get();
        String userId = userToken.getUserId();
        String originalUserName = userToken.getUserName();
        String username = user.getUsername();

        if("admin".equals(originalUserName)){
            throw new BusinessException("管理员不能修改用户信息");
        }

        // 用户名根之前不一样在校验
        if(!originalUserName.equals(username)){
            // 用户名是否存在
            Long usernameCount = baseMapper.selectCount(new LambdaQueryWrapper<User>()
                    .eq(User::getUsername, username));
            if(usernameCount > 0){
                throw new BusinessException("用户名已存在");
            }
        }

        user.setId(userId);
        int i = baseMapper.updateById(user);

        // 更新缓存
        redisUtil.set(RedisEnum.USER.getKey() + user.getId(), JSONObject.toJSONString(user));
        return i > 0;
    }

    /**
     * 根据用户名查询用户列表
     * @author wangjinfei
     * @date 2025/5/12 21:18
     * @param username
     * @return List<User>
     */
    @Override
    public List<UserSearchVO> getListByName(String username) {
        List<UserSearchVO> userList = new ArrayList<>();

        String myselfId = UserContext.get().getUserId();
        // 根据用户名模糊查询且排除自己
        userList = baseMapper.getListByName(username, myselfId);

        // 获取用户连接状态
        for (UserSearchVO user : userList) {
            user.setIsOnline(centerService.isOnline(user.getId()));
        }

        return userList;
    }

    /**
     * 根据id列表查询用户列表
     * @author wangjinfei
     * @date 2025/5/13 23:36
     * @param ids
     * @return List<User>
     */
    @Override
    public List<User> getListByIds(List<String> ids) {
        List<User> userList = new ArrayList<>();
        if(CollectionUtils.isEmpty(ids)){
            return userList;
        }

        userList = baseMapper.selectList(new LambdaQueryWrapper<User>()
                .in(User::getId, ids));
        return userList;
    }

    /**
     * 根据用户名获取数量
     * @author wangjinfei
     * @date 2025/6/12 21:51
     * @param username
     * @return Long
     */
    @Override
    public Long getCountByName(String username) {
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        return count;
    }

    /**
     * 根据邮箱获取数量
     * @author wangjinfei
     * @date 2025/6/12 21:53
     * @param email
     * @return Long
     */
    @Override
    public Long getCountByEmail(String email) {
        Long count = baseMapper.selectCount(new LambdaQueryWrapper<User>().eq(User::getEmail, email));
        return count;
    }

    /**
     * 根据用户名获取用户
     * @author wangjinfei
     * @date 2025/6/12 21:54
     * @param username
     * @return User
     */
    @Override
    public User getByUsername(String username) {
        User user = baseMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .or()
                .eq(User::getEmail, username));
        return user;
    }

    /**
     * 根据id获取用户
     * @author wangjinfei
     * @date 2025/7/6 11:50
     * @param id
     * @return User
     */
    @Override
    public User getById(String id) {
        // 先查询缓存
        String userCache = redisUtil.get(RedisEnum.USER.getKey() + id);
        if(StringUtils.isNotEmpty(userCache)){
            User user = JSONObject.parseObject(userCache, User.class);
            return user;
        }

        // 缓存不存在查询数据库
        User user = baseMapper.selectById(id);
        return user;
    }

    /**
     * 添加用户到redis
     * @author wangjinfei
     * @date 2025/7/6 16:02
     * @param userId
     */
    @Override
    public Boolean addUser2Redis(String userId) {
        if(StringUtils.isEmpty(userId)){
            return false;
        }

        User user = baseMapper.selectById(userId);
        if(user == null){
            return false;
        }

        redisUtil.set(RedisEnum.USER.getKey() + userId, JSONObject.toJSONString(user));
        return true;
    }

    /**
     * 获取所有用户id
     * @author wangjinfei
     * @date 2025/7/29 17:40
     * @return List<String>
     */
    @Override
    public List<String> getAllUserId() {
        return baseMapper.getAllUserId();
    }
}
