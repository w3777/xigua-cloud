package com.xigua.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName MultipartFileDTO
 * @Description TODO
 * @Author wangjinfei
 * @Date 2024/9/27 7:59
 */
@Data
public class MultipartFileDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 源文件名称
     */
    @Schema(name = "源文件名称")
    private String originalFilename;


    /**
     * 媒体类型
     */
    @Schema(name = "媒体类型")
    private String contentType;

    /**
     * 文件大小
     */
    @Schema(name = "文件大小")
    private Long size;

    /**
     * 字节数组
     */
    @Schema(name = "字节数组")
    private byte[] bytes;
}
