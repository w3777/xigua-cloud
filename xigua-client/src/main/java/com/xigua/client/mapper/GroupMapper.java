package com.xigua.client.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.xigua.domain.entity.Group;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @ClassName GroupMapper
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/7/6 11:08
 */
@Mapper
public interface GroupMapper extends BaseMapper<Group> {

    /**
     * 获取所有群组id
     * @author wangjinfei
     * @date 2025/7/29 17:50
     * @return List<String>
     */
    List<String> getAllGroupId();
}
