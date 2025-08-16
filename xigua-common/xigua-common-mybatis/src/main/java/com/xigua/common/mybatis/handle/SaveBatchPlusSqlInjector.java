package com.xigua.common.mybatis.handle;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.xigua.common.mybatis.menthod.SaveBatchPlus;
import org.apache.ibatis.session.Configuration;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @ClassName SaveBatchPlusSqlInjector
 * @Description 自定义SQL注入器
 * @Author wangjinfei
 * @Date 2025/8/16 17:16
 */
@Component
public class SaveBatchPlusSqlInjector extends DefaultSqlInjector {

    @Override
    public List<AbstractMethod> getMethodList(Configuration configuration, Class<?> mapperClass, TableInfo tableInfo) {
        // 注意：此SQL注入器继承了DefaultSqlInjector(默认注入器)
        // 调用了DefaultSqlInjector的getMethodList方法，保留了mybatis-plus的自带方法
        List<AbstractMethod> methodList = super.getMethodList(configuration, mapperClass, tableInfo);
        methodList.add(new SaveBatchPlus());
        return methodList;
    }
}
