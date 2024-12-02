package com.xigua.demo.util;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName SSEUtil
 * @Description TODO
 * @Author wangjinfei
 * @Date 2024/12/2 10:50
 */
public class SSEUtils {
    // 存储所有的 SseEmitter，key 通常为用户标识（如 userId）
    private static final Map<String, SseEmitter> emitterMap = new ConcurrentHashMap<>();

    /**
     * 创建并添加一个 SseEmitter
     *
     * @param key 唯一标识
     * @return SseEmitter
     */
    public static SseEmitter create(String key) {
        SseEmitter emitter = new SseEmitter();

        // 添加到 Map
        emitterMap.put(key, emitter);

        // 绑定事件，自动移除无效连接
        emitter.onCompletion(() -> removeEmitter(key));
        emitter.onTimeout(() -> removeEmitter(key));
        emitter.onError(e -> removeEmitter(key));

        return emitter;
    }

    /**
     * 移除 SseEmitter
     *
     * @param key 唯一标识
     */
    public static void removeEmitter(String key) {
        emitterMap.remove(key);
    }

    /**
     * 根据key获取SseEmitter
     * @author wangjinfei
     * @date 2024/12/2 11:10
     * @param key
     * @return SseEmitter
    */
    public static SseEmitter get(String key){
        return emitterMap.get(key);
    }
}
