package com.xigua.domain.result;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;


/**
 * @ClassName BasePageVO
 * @Description TODO 基本分页返回vo对象
 * @Author wangjinfei
 * @Date 2024/6/18 20:43
 */
@Schema(title = "基本分页返回vo对象")
@Data
public class BasePageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 当前页数
     */
    @Schema(name = "pageNum", description = "当前页数", required = true)
    private Integer pageNum;

    /**
     * 每页显示个数
     */
    @Schema(name = "pageSize" ,description = "每页显示个数", required = true)
    private Integer pageSize;
}
