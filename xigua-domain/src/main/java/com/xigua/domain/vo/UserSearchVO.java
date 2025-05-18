package com.xigua.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName UserSearchVO
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/12 21:23
 */
@Data
@Schema(title = "用户搜索vo")
public class UserSearchVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Schema(name = "主键id")
    private String id;

    /**
     * 用户名
     */
    @Schema(name = "用户名")
    private String username;

    /**
     * 头像
     */
    @Schema(name = "头像")
    private String avatar;

    /**
     * 性别（1；男；2：女）
     */
    @Schema(name = "性别（1；男；2：女）")
    private Integer sex;

    /**
     * 是否在线
     */
    @Schema(name = "是否在线")
    private Boolean isOnline;
}
