package com.xigua.demo.domain;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

/**
 * 这个富文本表在这里只是用来demo的示例存储，实际项目中，可能只会把这个content字段存到主表中
 * @ClassName RichText
 * @Description
 * @Author wangjinfei
 * @Date 2024/12/31 10:27
 */
@Data
public class RichText {
    @TableId
    private Long id;

    /**
     * 富文本内容
     */
    private String content;
}
