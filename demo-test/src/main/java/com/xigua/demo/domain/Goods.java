package com.xigua.demo.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * @ClassName Goods
 * @Description TODO
 * @Author wangjinfei
 * @Date 2024/11/21 15:34
 */
@Data
public class Goods {
    @TableId
    private Long id;

    private String name;

    private Long stock;
}
