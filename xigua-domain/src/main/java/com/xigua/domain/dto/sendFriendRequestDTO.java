package com.xigua.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName sendFriendRequestDTO
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/13 21:30
 */
@Data
public class sendFriendRequestDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String friendId;
    private String applyMsg;
}
