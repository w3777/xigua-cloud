package com.xigua.common.core.util;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.xigua.domain.token.UserToken;

/**
 * @ClassName UserContext
 * @Description 用户上下文
 * @Author wangjinfei
 * @Date 2025/3/28 16:12
 */
public class UserContext {

    private static final ThreadLocal<UserToken> context = new TransmittableThreadLocal<>();

    public static void set(UserToken userToken) {
        context.set(userToken);
    }

    public static UserToken get() {
        return context.get();
    }

    public static void clear() {
        context.remove();
    }
}
