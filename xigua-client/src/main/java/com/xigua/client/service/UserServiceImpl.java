package com.xigua.client.service;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xigua.client.mapper.UserMapper;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.core.util.TokenUtil;
import com.xigua.common.core.util.UserContext;
import com.xigua.domain.dto.LoginDTO;
import com.xigua.domain.dto.RegisterUserDTO;
import com.xigua.domain.entity.User;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.domain.enums.UserConnectStatus;
import com.xigua.common.core.model.UserToken;
import com.xigua.domain.vo.UserSearchVO;
import com.xigua.service.CenterService;
import com.xigua.service.EmailService;
import com.xigua.service.UserService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.dubbo.common.utils.CollectionUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName UserServiceImpl
 * @Description
 * @Author wangjinfei
 * @Date 2025/4/27 9:53
 */
@DubboService
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
    private final RedisUtil redisUtil;
    @DubboReference
    private final EmailService emailService;
    @DubboReference
    private final CenterService centerService;

    @Override
    public void testDubbo() {

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
        String username = dto.getUsername();
        String code = dto.getCode();

        // 用户名是否存在
        Long usernameCount = baseMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username));
        if(usernameCount > 0){
            throw new BusinessException("用户名已存在");
        }

        // 邮箱是否存在
        Long emailCount = baseMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getEmail, email));
        if(emailCount > 0){
            throw new BusinessException("邮箱已存在");
        }

        // 校验邮箱
        Boolean checkCode = emailService.checkCode(email, code);
        redisUtil.del(RedisEnum.EMAIL_CODE.getKey() + email);

        User user = new User();
        BeanUtils.copyProperties(dto,user);

        // md5加密
        user.setPassword(DigestUtils.md5Hex(user.getPassword()));

        int insert = baseMapper.insert(user);

        // 缓存用户信息
        redisUtil.set(RedisEnum.USER_ALL.getKey() + user.getId(), JSONObject.toJSONString(user));
        return insert > 0;
    }

    /**
     * 登录
     * @author wangjinfei
     * @date 2025/5/7 13:40
     * @param loginDTO
     * @return Boolean
     */
    @Override
    public String login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        User user = baseMapper.selectOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername, username)
                .or()
                .eq(User::getEmail, username));
        if(user == null){
            throw new BusinessException("用户名不存在");
        }
        if(!user.getPassword().equals(DigestUtils.md5Hex(password))){
            throw new BusinessException("密码错误");
        }

        // todo 登录成功，生成token
        String token = createToken(user);

        // todo 保存token到redis
        return token;
    }

    /**
     * 创建token
     * @author wangjinfei
     * @date 2025/5/10 12:20
     * @param user
     * @return String
     */
    @Override
    public String createToken(User user) {
        UserToken userToken = new UserToken();
        userToken.setUserId(user.getId());
        userToken.setUserName(user.getUsername());
        userToken.setPhone(user.getPhone());
        return TokenUtil.genToken(userToken);
    }

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
        redisUtil.set(RedisEnum.USER_ALL.getKey() + userId, JSONObject.toJSONString(user));
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
        redisUtil.set(RedisEnum.USER_ALL.getKey() + user.getId(), JSONObject.toJSONString(user));
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
}
