package com.xigua.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName RegisterUserDTO
 * @Description
 * @Author wangjinfei
 * @Date 2025/4/27 10:09
 */
@Data
@Schema(title = "注册用户对象")
public class RegisterUserDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @NotEmpty(message = "用户名不可以为空")
    @Size(min = 1, max = 20, message = "用户名最大长度为20")
    @Schema(name = "username", description = "用户名")
    private String username;

    /**
     * 密码
     */
    @NotEmpty(message = "密码不可以为空")
    @Schema(name = "password", description = "密码")
    private String password;

    /**
     * 邮箱
     */
    @NotEmpty(message = "邮箱不可以为空")
    @Schema(name = "email", description = "邮箱")
    private String email;

    /**
     * 邮箱验证码
     */
    @NotEmpty(message = "邮箱验证码不可以为空")
    @Schema(name = "code", description = "邮箱验证码")
    private String code;
}
