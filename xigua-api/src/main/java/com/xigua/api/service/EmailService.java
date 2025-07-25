package com.xigua.api.service;

/**
 * @ClassName EmailService
 * @Description
 * @Author wangjinfei
 * @Date 2025/5/7 14:33
 */
public interface EmailService {

    /**
     * 发送邮件
     * @author wangjinfei
     * @date 2025/5/7 14:49
     * @param to 收件人
     * @param subject 邮件主题
     * @param text 邮件内容
    */
    Boolean send(String to, String subject, String text);

    /**
     * 校验验证码
     * @author wangjinfei
     * @date 2025/5/7 15:26
     * @param email
     * @param code
     * @return Boolean
    */
    Boolean checkCode(String email, String code);
}
