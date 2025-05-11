package com.xigua.client.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName IpUtil
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/5/11 15:03
 */
public class IpUtil {
    private static final String[] HEADERS_TO_CHECK = {
            "X-Forwarded-For",
            "Proxy-Client-IP",
            "WL-Proxy-Client-IP",
            "HTTP_X_FORWARDED_FOR",
            "HTTP_CLIENT_IP"
    };

    /**
     * 获取客户端真实IP（自动处理代理和多级转发）
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        // 1. 按优先级检查各个代理头
        String ip = parseProxyHeaders(request);

        // 2. 如果代理头未找到有效IP，使用远程地址
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }

        // 3. 处理多级代理（取第一个非unknown的IP）
        ip = splitFirstIp(ip);

        // 4. 处理IPv6本地地址
        return normalizeLocalIp(ip);
    }

    private static String parseProxyHeaders(HttpServletRequest request) {
        for (String header : HEADERS_TO_CHECK) {
            String ip = request.getHeader(header);
            if (StringUtils.isNotBlank(ip) && !"unknown".equalsIgnoreCase(ip)) {
                return ip;
            }
        }
        return null;
    }

    private static String splitFirstIp(String ip) {
        if (ip != null && ip.contains(",")) {
            return ip.split(",")[0].trim();
        }
        return ip;
    }

    private static String normalizeLocalIp(String ip) {
        return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" : ip;
    }
}
