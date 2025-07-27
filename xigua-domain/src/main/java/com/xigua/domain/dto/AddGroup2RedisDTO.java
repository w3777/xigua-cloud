package com.xigua.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName AddGroup2RedisDTO
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/27 11:31
 */
@Data
public class AddGroup2RedisDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private List<String> groupIds;
}
