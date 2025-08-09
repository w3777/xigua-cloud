package com.xigua.client.service;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.api.service.AuthService;
import com.xigua.api.service.EmailService;
import com.xigua.api.service.UserService;
import com.xigua.common.core.exception.BusinessException;
import com.xigua.common.core.model.UserToken;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.core.util.TokenUtil;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.dto.LoginDTO;
import com.xigua.domain.dto.RegisterUserDTO;
import com.xigua.domain.entity.User;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.domain.vo.LoginVO;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * @ClassName AuthServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/12 21:05
 */
@Service
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
        redisUtil.set(RedisEnum.USER.getKey() + user.getId(), JSONObject.toJSONString(user));
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
    public LoginVO login(LoginDTO loginDTO) {
        LoginVO loginVO = new LoginVO();
        String username = loginDTO.getUsername();
        String password = loginDTO.getPassword();
        User user = userService.getByUsername(username);
        if(user == null){
            throw new BusinessException("用户名不存在");
        }
        if(!user.getPassword().equals(DigestUtils.md5Hex(password))){
            throw new BusinessException("密码错误");
        }

        // 生成token
        String token = createToken(user);
        loginVO.setToken(token);
        LocalDateTime expireAt = LocalDate.now().atTime(LocalTime.MAX);
        loginVO.setExpireAt(expireAt);

        // todo 保存token到redis
        return loginVO;
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
        LocalDateTime expireAt = LocalDate.now().atTime(LocalTime.MAX);
        userToken.setExpireAt(expireAt);
        return TokenUtil.genToken(userToken);
    }

    /**
     * 创建一次性ticket
     * @author wangjinfei
     * @date 2025/6/14 16:07
     * @param userId
     * @return String
     */
    @Override
    public String createTicket(String userId) {
        String ticket = sequence.nextNo();
        // redis存储一次性ticket，有效期1分钟
        redisUtil.set(RedisEnum.TICKET.getKey() + ticket, userId, 60);

        return ticket;
    }

    /**
     * ticket兑换token
     * @author wangjinfei
     * @date 2025/6/14 21:46
     * @param ticket
     * @return String
     */
    @Override
    public String redeemToken(String ticket) {
        String key = RedisEnum.TICKET.getKey() + ticket;
        String userId = redisUtil.get(key);
        if(userId == null){
            throw new BusinessException("ticket已失效或不存在");
        }

        redisUtil.del(key);

        User user = userService.getById(userId);
        if(user == null){
            throw new BusinessException("用户不存在");
        }

        return createToken(user);
    }
}
