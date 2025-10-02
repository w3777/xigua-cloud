package com.xigua.center.chatMessage;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.api.service.*;
import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.connect.Client;
import com.xigua.domain.enums.*;
import com.xigua.domain.ws.MessageRequest;
import com.xigua.domain.ws.MessageResponse;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * @ClassName AISendChatMessage
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/10/1 10:48
 */
@Service
public class AISendChatMessageService extends AbstractSendChatMessageService{
    @Autowired
    private Sequence sequence;
    @Autowired
    private RedisUtil redisUtil;
    @Autowired
    private CenterService centerService;
    @Autowired
    private MessageReadService messageReadService;
    @DubboReference
    private AIService aiService;

    @Override
    protected ChatType getChatType() {
        return ChatType.THREE;
    }

    @Override
    public Boolean saveMessageUnread(MessageRequest messageRequest) {
        return null;
    }

    @Override
    public void lastMessage(MessageRequest messageRequest) {

    }

    @Override
    public void chatMessage2Receiver(MessageRequest messageRequest) {
        // 获取接收人所在的节点信息
        String receiverInServer = centerService.onlineUser(messageRequest.getSenderId());
        if(StringUtils.isEmpty(receiverInServer)){
            // 如果接收人不在线，直接返回，不做后续处理
            return;
        }
        // 获取接收者所在的节点信息
        String key = RedisEnum.CLIENT_CONNECT_CENTER.getKey() +
                receiverInServer.split(":")[1] + ":" + receiverInServer.split(":")[2];
        String value = redisUtil.get(key);
        Client client = JSONObject.parseObject(value, Client.class);

        Mono<ChatRequest> aiReq = Mono.just(
                ChatRequest.newBuilder()
                        .setInput(messageRequest.getMessage())
                        .setStream(true)
                        .build()
        );
        Flux<ChatResponse> chatFlux = aiService.chat(aiReq);
        // todo 暂时用一个新id 来表示一次ai的会话
        String sessionId = sequence.nextNo();
        chatFlux.subscribe(output -> {
            // 实时推送消息，发送到接收人所在节点
            MessageResponse messageResponse = new MessageResponse();
            messageResponse.setSenderId(messageRequest.getReceiverId());
            messageResponse.setReceiverId(messageRequest.getSenderId());
            messageResponse.setMessageType(MessageType.CHAT.getType());
            messageResponse.setChatType(ChatType.THREE.getType());
            messageResponse.setSubType(MessageSubType.MES_RECEIVE.getType());
            messageResponse.setMessage(output.getOutput());
            messageResponse.setChatMessageId(sessionId);
            centerService.sendMessage2Client(messageResponse, client);
        });
    }

    @Override
    public void unreadMessageCount(MessageRequest messageRequest) {

    }

    @Override
    public void unread2Read(MessageRequest messageRequest) {

    }
}
