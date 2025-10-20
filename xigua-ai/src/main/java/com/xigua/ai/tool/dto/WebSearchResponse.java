package com.xigua.ai.tool.dto;

import lombok.Data;

import java.util.List;

/**
 * @ClassName WebSearchResponse
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/20 20:44
 */
@Data
public class WebSearchResponse {
    private String requestId;
    private String code;
    private String message;
    private List<Reference> references;

    @Data
    public static class Reference {
        private String icon; // 网站图标地址
        private Integer id; // 引用编号
        private String title; // 网页标题
        private String url; // 网页地址
        private String webAnchor; // 网站锚文本或网站标题
        private String website; // 站点名称
        private String content; // 网页内容，2000字以内
        private Float rerankScore; // 原文片段相关性评分
        private Float authorityScore; // 网页权威性评分
        private String date; // 网页日期
        private String type; // 检索资源类型：web、video、image、aladdin
        private ImageDetail image; // 图片详情
        private VideoDetail video; // 视频详情
        private Boolean isAladdin; // 是否为阿拉丁内容
        private Object aladdin; // 阿拉丁详细内容
        private WebExtensions webExtensions; // 网页相关图片扩展

        // 图片详情
        @Data
        public static class ImageDetail {
            private String url;
            private String height;
            private String width;
        }

        // 视频详情
        @Data
        public static class VideoDetail {
            private String url;
            private String height;
            private String width;
            private String size; // 单位：Bytes
            private String duration; // 单位：秒
            private String hoverPic; // 视频封面图
        }

        // 网页扩展信息
        @Data
        public static class WebExtensions {
            private List<ImageDetail> images;
        }
    }
}
