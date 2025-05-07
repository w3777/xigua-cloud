package com.xigua.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName LoginDTO
 * @Description
 * @Author wangjinfei
 * @Date 2025/3/28 15:26
 */
@Data
@Schema(title = "登录用户对象")
public class LoginDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(name = "username", description = "用户名")
    private String username;

    @Schema(name = "password", description = "密码")
    private String password;
}
