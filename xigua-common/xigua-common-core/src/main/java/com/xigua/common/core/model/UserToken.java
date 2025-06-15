package com.xigua.common.core.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName UserToken
 * @Description
 * @Author wangjinfei
 * @Date 2025/3/25 17:01
 */
@Data
public class UserToken implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * tokenId
     */
    private String id;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 过期时间
     */
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireAt;
}
