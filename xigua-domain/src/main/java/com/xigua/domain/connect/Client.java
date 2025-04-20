package com.xigua.domain.connect;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName Client
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/4/20 10:22
 */
@Data
public class Client implements Serializable {
    private static final long serialVersionUID = 1L;
    private String host;

    // 应用服务端口
    private Integer port;

    // dubbo调用端口
    private Integer dubboPort;
}
