package com.xigua.domain.dto;

import com.xigua.domain.result.BasePageDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName GetHistoryMessageDTO
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/19 21:52
 */
@Data
public class GetFriendLastMesDTO extends BasePageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String topUserId;
    private String friendId;
}
