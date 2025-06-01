package com.xigua.domain.enums;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum MediaTypeEnum {
    // text/* 类型
    TEXT_HTML("text/html", "html"),
    TEXT_PLAIN("text/plain", "txt"),
    TEXT_XML("text/xml", "xml"),

    // image/* 类型
    IMAGE_GIF("image/gif", "gif"),
    IMAGE_JPEG("image/jpeg", "jpg"),
    IMAGE_PNG("image/png", "png"),

    // application/* 类型
    APPLICATION_XHTML_XML("application/xhtml+xml", "xhtml"),
    APPLICATION_XML("application/xml", "xml"),
    APPLICATION_ATOM_XML("application/atom+xml", "atom"),
    APPLICATION_JSON("application/json", "json"),
    APPLICATION_PDF("application/pdf", "pdf"),
    APPLICATION_MSWORD("application/msword", "doc"),
    APPLICATION_DOCX("application/vnd.openxmlformats-officedocument.wordprocessingml.document", "docx"),
    APPLICATION_OCTET_STREAM("application/octet-stream", ""),
    APPLICATION_X_WWW_FORM_URLENCODED("application/x-www-form-urlencoded", "");

    private final String contentType;
    private final String fileSuffix;

    public String getContentType() {
        return contentType;
    }

    public String getFileSuffix() {
        return fileSuffix;
    }

    public static MediaTypeEnum getBySuffix(String suffix) {
        for (MediaTypeEnum mediaType : MediaTypeEnum.values()) {
            if (mediaType.getFileSuffix().equals(suffix)) {
                return mediaType;
            }
        }
        return null;
    }
}
