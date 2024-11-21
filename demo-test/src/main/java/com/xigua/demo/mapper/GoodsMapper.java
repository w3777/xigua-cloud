package com.xigua.demo.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xigua.demo.domain.Goods;
import org.apache.ibatis.annotations.Mapper;

/**
 * @ClassName GoodsMapper
 * @Description TODO
 * @Author wangjinfei
 * @Date 2024/11/21 15:39
 */
@Mapper
public interface GoodsMapper extends BaseMapper<Goods> {
}
