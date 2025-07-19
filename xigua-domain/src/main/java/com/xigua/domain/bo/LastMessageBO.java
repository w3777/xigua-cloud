package com.xigua.domain.bo;

import com.xigua.domain.enums.ChatType;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName LaseMessageContentBO
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/18 13:20
 */
@Data
public class LastMessageBO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 聊天id（单聊id、群聊id）
     */
    private String chatId;

    /**
     * 聊天类型
     * @see ChatType
     */
    private Integer chatType;

    /**
     * 聊天名称
     */
    private String chatName;

    /**
     * 头像
     */
    private String avatar;

    /**
     * 最后一条消息内容
     */
    private LastMessageContentBO lastMessageContent;

    /**
     * 最后更新时间
     */
    private Long updateTime;
}
