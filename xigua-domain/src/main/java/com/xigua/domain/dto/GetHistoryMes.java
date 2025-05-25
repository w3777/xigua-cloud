package com.xigua.domain.dto;

import com.xigua.domain.result.BasePageDTO;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName GetHistoryMes
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/25 10:15
 */
@Data
public class GetHistoryMes extends BasePageDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private String senderId;
    private String receiverId;
}
