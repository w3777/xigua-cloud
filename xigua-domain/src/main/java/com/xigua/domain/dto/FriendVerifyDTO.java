package com.xigua.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName FriendVerifyDTO
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/14 22:00
 */
@Data
public class FriendVerifyDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    private String requestId;
    private String friendId;
    private Integer flowStatus;
}
