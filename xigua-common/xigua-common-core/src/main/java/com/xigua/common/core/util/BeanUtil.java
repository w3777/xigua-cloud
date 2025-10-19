package com.xigua.common.core.util;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName BeanUtil
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/19 17:59
 */
public class BeanUtil {
    public static Map<String, Object> convertToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();

        if (obj == null) {
            return map;
        }

        Class<?> clazz = obj.getClass();
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(obj);
                map.put(field.getName(), value);
            } catch (IllegalAccessException e) {
                // 忽略无法访问的字段
            }
        }

        return map;
    }
}
