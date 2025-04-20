package com.xigua.domain.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName ContentRequest
 * @Description
 * @Author wangjinfei
 * @Date 2024/12/31 10:20
 */
@Data
public class RichTextDTO implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 富文本内容
     */
    private String content;
}
