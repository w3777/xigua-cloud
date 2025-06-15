package com.xigua.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName LastChatVO
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/17 19:23
 */
@Data
public class LastChatVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;
    private String username;
    private String avatar;
    private String lastMessage;
    private Boolean isOnline;
    private Long friendUnreadCount;
}
