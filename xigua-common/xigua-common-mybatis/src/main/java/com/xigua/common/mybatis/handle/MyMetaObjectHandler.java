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
        //todo 后续通过threadLocal获取当前用户
//        String.valueOf(SecurityUtils.getUserId())
        this.strictInsertFill(metaObject, "createBy", String.class, UserContext.get().getUserId());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
        this.strictUpdateFill(metaObject, "updateBy", String.class, "管理员");
    }
}
