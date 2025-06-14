package com.xigua.sso.service;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.common.core.model.UserToken;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.core.util.TokenUtil;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.dto.LoginDTO;
import com.xigua.domain.dto.RegisterUserDTO;
import com.xigua.domain.entity.User;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.service.AuthService;
import com.xigua.service.EmailService;
import com.xigua.service.UserService;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @ClassName AuthServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/12 21:05
 */
@DubboService
public class AuthServiceImpl implements AuthService {
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private Sequence sequence;

    @DubboReference
    private UserService userService;

    @DubboReference
    private EmailService emailService;

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
        Long usernameCount = userService.getCountByName(username);
        if(usernameCount > 0){
            throw new BusinessException("用户名已存在");
        }

        // 邮箱是否存在
        Long emailCount = userService.getCountByEmail(email);
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

        user.setId(sequence.nextNo());
        boolean insert = userService.save(user);

        // 缓存用户信息
        redisUtil.set(RedisEnum.USER_ALL.getKey() + user.getId(), JSONObject.toJSONString(user));
        return insert;
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
        User user = userService.getByUsername(username);
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
}
