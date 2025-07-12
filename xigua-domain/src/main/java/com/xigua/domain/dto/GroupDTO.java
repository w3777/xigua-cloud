package com.xigua.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName GroupDTO
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/6 11:01
 */
@Data
@Schema(title = "创建群组对象")
public class GroupDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    @Schema(name = "groupId", description = "群组id")
    private String groupId;

    @Schema(name = "groupAvatar", description = "群组头像")
    private String groupAvatar;

    @Schema(name = "memberIds", description = "群成员")
    private List<String> memberIds;
}
