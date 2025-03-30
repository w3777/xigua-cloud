package com.xigua.domain.token;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(name = "id", description = "tokenId")
    private Long id;

    /**
     * 用户id
     */
    @Schema(name = "userId", description = "用户id")
    private Long userId;

    /**
     * 用户名
     */
    @Schema(name = "userName", description = "用户名")
    private String userName;

    /**
     * 手机号
     */
    @Schema(name = "phone", description = "手机号")
    private String phone;

    /**
     * 过期时间
     */
    @Schema(name = "expireTime", description = "过期时间")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireTime;
}
