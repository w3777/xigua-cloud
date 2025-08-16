package com.xigua.common.mybatis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * @ClassName BaseMapperPlus
 * @Description TODO
 * @Author wangjinfei
 * @Date 2023/12/2 10:53
 */
public interface BaseMapperPlus<T> extends BaseMapper<T> {

    // 批量插入
    // insert into table(field1,field2) values (value1,value2),(value1,value2)
    int saveBatchPlus(List<T> list);
}
