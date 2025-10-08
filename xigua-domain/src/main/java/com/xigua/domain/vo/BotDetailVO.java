package com.xigua.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @ClassName BotDetailVO
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/8 18:17
 */
@Data
public class BotDetailVO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String name;
    private String avatar;
    private String description;
    private String prompt;
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;
}
