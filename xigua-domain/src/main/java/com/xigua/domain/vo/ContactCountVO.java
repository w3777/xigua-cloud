package com.xigua.domain.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ContactCountVO
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/27 9:40
 */
@Data
public class ContactCountVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer friendCount;

    private Integer groupCount;

    private Integer friendRequestCount;
}
