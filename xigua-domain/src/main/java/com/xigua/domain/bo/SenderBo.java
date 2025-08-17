package com.xigua.domain.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName SenderBo
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/8/17 20:27
 */
@Data
public class SenderBo implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 名称
     */
    private String username;

    /**
     * 头像
     */
    private String avatar;
}
