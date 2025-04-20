package com.xigua.client.helper;

import jakarta.websocket.Session;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName SessionHelper
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/4/20 19:41
 */
public class SessionHelper {
    private static Map<String, Session> wsSessionMap = new ConcurrentHashMap<>();

    public static void put(String userId, Session session) {
        wsSessionMap.put(userId, session);
    }

    public static Session get(String userId) {
        return wsSessionMap.get(userId);
    }

    public static void remove(String userId) {
        wsSessionMap.remove(userId);
    }
}
