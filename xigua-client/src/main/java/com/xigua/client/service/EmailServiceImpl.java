package com.xigua.client.service;

import com.xigua.common.core.exception.BusinessException;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.api.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @ClassName EmailServiceImpl
 * @Description
 * @Author wangjinfei
 * @Date 2025/5/7 14:42
 */
@Slf4j
@Service
@RequiredArgsConstructor
@DubboService
public class EmailServiceImpl implements EmailService {
    @Value("${spring.mail.username}")
    private String from;

    private final JavaMailSender javaMailSender;

    private final RedisUtil redisUtil;

    /**
     * 发送邮件
     * @author wangjinfei
     * @date 2025/5/7 14:49
     * @param to 收件人
     * @param subject 邮件主题
     * @param text 邮件内容
     */
    public Boolean send(String to, String subject, String text) {
        subject = "西瓜 - 邮箱验证码";
        String code = RandomStringUtils.randomNumeric(6);
        text = "您的验证码是: " + code + "，有效期为3分钟";

        SimpleMailMessage message = new SimpleMailMessage();
        // 发件人
        message.setFrom(from);
        // 收件人
        message.setTo(to);
        // 邮件主题
        message.setSubject(subject);
        // 邮件内容
        message.setText(text);

        try {
            javaMailSender.send(message);
        } catch (Exception e) {
            log.error("邮件发送失败", e);
            return false;
        }

        // 将验证码存储到 Redis 中，设置过期时间为 3 分钟
        String key = RedisEnum.EMAIL_CODE.getKey() + to;
        redisUtil.set(key, code,180);

        return true;
    }

    /**
     * 校验验证码
     * @author wangjinfei
     * @date 2025/5/7 15:26
     * @param email
     * @param code
     * @return Boolean
     */
    @Override
    public Boolean checkCode(String email, String code) {
        String key = RedisEnum.EMAIL_CODE.getKey() + email;
        String redisCode = redisUtil.get(key);
        if(StringUtils.isEmpty(redisCode)){
            throw new BusinessException("验证码已过期，请重新获取");
        }

        if(code.equals(redisCode) == false){
            throw new BusinessException("验证码错误");
        }

        return true;
    }
}
