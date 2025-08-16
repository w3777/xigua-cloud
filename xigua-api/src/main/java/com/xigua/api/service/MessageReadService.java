package com.xigua.api.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xigua.domain.entity.MessageRead;

import java.util.List;

/**
 * @ClassName MessageReadService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/8/16 16:49
 */
public interface MessageReadService extends IService<MessageRead> {

    /**
     * 批量添加
     * @author wangjinfei
     * @date 2025/8/16 17:34
     * @param list
     * @return Boolean
    */
    Boolean saveBatchPlus(List<MessageRead> list);

    /**
     * 标记已读
     * @author wangjinfei
     * @date 2025/8/16 22:14
     * @param messageId
     * @param receiverId
     * @return Boolean
    */
    Boolean markRead(String messageId, String receiverId);

    /**
     * 批量标记已读
     * @author wangjinfei
     * @date 2025/8/16 22:25
     * @param messageIdList
     * @param receiverId
     * @return Boolean
    */
    Boolean markReadBatch(List<String> messageIdList, String receiverId);
}
