package com.xigua.center.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xigua.api.service.MessageReadService;
import com.xigua.center.mapper.MessageReadMapper;
import com.xigua.domain.entity.MessageRead;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName MessageReadServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/8/16 16:50
 */
@Slf4j
@Service
@DubboService
public class MessageReadServiceImpl extends ServiceImpl<MessageReadMapper, MessageRead> implements MessageReadService {
    /**
     * 批量添加
     * @author wangjinfei
     * @date 2025/8/16 17:34
     * @param list
     * @return Boolean
     */
    @Override
    public Boolean saveBatchPlus(List<MessageRead> list) {
        int i = baseMapper.saveBatchPlus(list);
        return i > 0;
    }

    /**
     * 标记已读
     * @author wangjinfei
     * @date 2025/8/16 22:14
     * @param messageId
     * @param receiverId
     * @return Boolean
     */
    @Override
    public Boolean markRead(String messageId, String receiverId) {
        Integer i = baseMapper.markRead(messageId, receiverId);
        return i > 0;
    }

    /**
     * 批量标记已读
     * @author wangjinfei
     * @date 2025/8/16 22:25
     * @param messageIdList
     * @param receiverId
     * @return Boolean
     */
    @Override
    public Boolean markReadBatch(List<String> messageIdList, String receiverId) {
        Integer i = baseMapper.markReadBatch(messageIdList, receiverId);
        return  i > 0;
    }

}
