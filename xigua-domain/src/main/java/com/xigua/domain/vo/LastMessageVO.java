package com.xigua.domain.vo;

import com.xigua.domain.bo.LastMessageContentBO;
import com.xigua.domain.enums.ChatType;
import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName LastChatVO
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/17 19:23
 */
@Data
public class LastMessageVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 聊天id
     * 单聊：好友id
     * 群聊：群id
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
     * 未读消息数量
     */
    private Long unreadCount;

    /**
     * 是否在线
     */
    private Boolean isOnline;

    /**
     * 最后更新时间
     */
    private Long updateTime;

    // todo 后续可能扩展的字段
//    private Boolean isMuted;       // 是否静音
//    private Boolean isStickTop;    // 是否置顶
//    private Long stickTopTime;     // 置顶时间(用于排序)
//    private Boolean isAtMe;        // 是否有@我的消息(群聊)
}
