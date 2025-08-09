package com.xigua.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName HomeCountVO
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/8/9 10:55
 */
@Data
public class HomeCountVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer friendCount;

    private Integer groupCount;

    private Long unreadCount;
}
