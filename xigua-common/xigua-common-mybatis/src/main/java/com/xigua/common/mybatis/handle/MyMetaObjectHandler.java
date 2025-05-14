package com.xigua.common.mybatis.handle;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.xigua.common.core.util.UserContext;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * @ClassName MetaObjectHandler
 * @Description TODO
 * @Author wangjinfei
 * @Date 2024/6/22 16:45
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());

        this.strictInsertFill(metaObject, "createBy", String.class, UserContext.get().getUserId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", String.class, UserContext.get().getUserId());
    }
}
