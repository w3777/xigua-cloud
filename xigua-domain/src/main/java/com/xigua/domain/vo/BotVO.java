package com.xigua.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName Bot
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/1 15:19
 */
@Data
public class BotVO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 主键id
     */
    @Schema(description = "主键id")
    private String id;

    /**
     * 名称
     */
    @Schema(description = "名称")
    private String name;

    /**
     * 头像
     */
    @Schema(description = "头像")
    private String avatar;

    /**
     * 描述
     */
    @Schema(description = "描述")
    private String description;
}
