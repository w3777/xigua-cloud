package com.xigua.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName LoginDTO
 * @Description
 * @Author wangjinfei
 * @Date 2025/3/28 15:26
 */
@Data
public class LoginDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String username;

    private String password;
}
