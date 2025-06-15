package com.xigua.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName LoginVO
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/14 16:13
 */
@Data
public class LoginVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String token;

    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expireAt;
}
