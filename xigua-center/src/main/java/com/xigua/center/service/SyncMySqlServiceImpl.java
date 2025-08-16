package com.xigua.center.service;

import com.xigua.api.service.ChatMessageService;
import com.xigua.api.service.MessageReadService;
import com.xigua.api.service.SyncMySqlService;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.entity.ChatMessage;
import com.xigua.domain.entity.MessageRead;
import com.xigua.domain.enums.MessageReadStatus;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.dubbo.config.annotation.DubboService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName SyncMySqlServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/8/16 16:46
 */
@Slf4j
@Service
@DubboService
public class SyncMySqlServiceImpl implements SyncMySqlService {
    @Autowired
    private Sequence sequence;
    @Autowired
    private ChatMessageService chatMessageService;
    @Autowired
    private MessageReadService messageReadService;

    /**
     * 同步消息已读
     * @author wangjinfei
     * @date 2025/8/16 16:46
     * @return Boolean
     */
    @Override
    public Boolean syncMessageRead() {
        List<ChatMessage> chatMessages = chatMessageService.list();
        if(CollectionUtils.isEmpty(chatMessages)){
            return false;
        }

        // 分区大小
        int partitionSize = 300;

        // 分区
        List<List<ChatMessage>> partitioned = ListUtils.partition(chatMessages, partitionSize);
        List<MessageRead> addMessageReads = new ArrayList<>();

        // 遍历分区
        for (List<ChatMessage> messages : partitioned) {
            for (ChatMessage message : messages) {
                MessageRead messageRead = new MessageRead();
                messageRead.setId(sequence.nextNo());
                messageRead.setSenderId(message.getSenderId());
                messageRead.setReceiverId(message.getReceiverId());
                messageRead.setMessageId(message.getId());
                messageRead.setIsRead(message.getIsRead());
                messageRead.setReadTime(message.getReadTime());
                messageRead.setDelFlag(0);
                messageRead.setCreateBy(messageRead.getSenderId());
                messageRead.setCreateTime(message.getCreateTime());
                if(messageRead.getIsRead() == MessageReadStatus.READ.getType()){
                    messageRead.setUpdateBy(message.getReceiverId());
                    messageRead.setUpdateTime(message.getReadTime());
                }
                addMessageReads.add(messageRead);
            }

            // 批量插入
            messageReadService.saveBatchPlus(addMessageReads);
            addMessageReads.clear();
        }

        return true;
    }
}
