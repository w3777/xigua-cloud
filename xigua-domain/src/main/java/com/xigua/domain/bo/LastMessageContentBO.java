package com.xigua.domain.bo;

import lombok.Data;

import java.io.Serializable;

/**
 * @ClassName LastMessageContentBO
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/18 13:47
 */
@Data
public class LastMessageContentBO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 消息内容
     */
    private String content;

    // todo 后续可能扩展的字段
//    private Integer messageType;   // 消息类型 1:文本 2:图片 3:语音 4:视频 5:文件 6:红包 7:链接...
//
//    // 特殊消息标识
//    private Boolean isAtMe;       // 是否@我
//    private Boolean isRevoked;    // 是否已撤回
//    private Boolean isSystemMsg;  // 是否是系统消息
//
//    // 富媒体消息预览
//    private String mediaPreview;  // 媒体预览("[图片]"/"[语音]")
//    private String linkTitle;     // 链接标题
//    private String linkCover;     // 链接封面
//
//    // 消息状态
//    private Integer status;       // 消息状态(1:发送中 2:已发送 3:已送达 4:已读)
}
