package com.xigua.center.mapper;

import com.xigua.common.mybatis.mapper.BaseMapperPlus;
import com.xigua.domain.entity.MessageRead;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @ClassName MessageReadMapper
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/8/16 16:51
 */
@Mapper
public interface MessageReadMapper extends BaseMapperPlus<MessageRead> {
    /**
     * 标记已读
     * @author wangjinfei
     * @date 2025/8/16 22:14
     * @param messageId
     * @param receiverId
     * @return Integer
     */
    Integer markRead(@Param("messageId") String messageId, @Param("receiverId") String receiverId);

    /**
     * 批量标记已读
     * @author wangjinfei
     * @date 2025/8/16 22:25
     * @param messageIdList
     * @param receiverId
     * @return Integer
     */
    Integer markReadBatch(@Param("messageIdList") List<String> messageIdList, @Param("receiverId") String receiverId);
}
