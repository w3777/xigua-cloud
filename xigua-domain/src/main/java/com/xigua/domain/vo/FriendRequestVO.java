package com.xigua.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName receiveFriendRequestVO
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/13 23:09
 */
@Data
public class FriendRequestVO implements Serializable {

    private static final long serialVersionUID = 1L;

    private String requestId;
    private String userId;
    private String username;
    private String avatar;
    private String signature;
    private String createTime;
    private String source;
    private Integer flowStatus;
}
