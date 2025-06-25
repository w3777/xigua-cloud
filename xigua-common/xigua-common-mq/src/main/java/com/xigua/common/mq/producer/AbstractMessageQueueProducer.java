package com.xigua.common.mq.producer;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.common.mq.constant.TopicEnum;
import org.springframework.kafka.support.SendResult;

import java.util.function.Consumer;

/**
 * @ClassName AbstractMessageQueueProducer
 * @Description 再抽一层，用于通用得序列化消息为json，以及模板设计模式封装发送消息
 * @Author wangjinfei
 * @Date 2025/6/20 13:01
 */
public abstract class AbstractMessageQueueProducer<T> implements MessageQueueProducer<T> {

    /**
     * 序列化消息为json
     * @author wangjinfei
     * @date 2025/6/20 13:12
     * @param message
     * @return String
    */
    protected String serializeMessage2Json(T message) {
        return JSONObject.toJSONString(message);
    }

    /**
     * 同步发送消息
     * @author wangjinfei
     * @date 2025/6/20 13:17
     * @param topic
     * @param messageJsonStr
     * @return boolean
    */
    protected abstract boolean doSyncSend(String topic, String messageJsonStr);

    /**
     * 异步发送消息，并且可以指定回调函数
     * @author wangjinfei
     * @date 2025/6/25 14:54
     * @param topic
     * @param messageJsonStr
     * @param callback
     * @return boolean
     */
    protected abstract boolean doAsyncSend(String topic, String messageJsonStr, Consumer<SendResult<String, String>> callback);

    /**
     * 模板设计模式封装发送消息
     * 1. 序列化消息为json
     * 2. 调用doSend方法发送消息
     * @author wangjinfei
     * @date 2025/6/20 11:55
     * @param topicE 消息队列枚举
     * @param message 消息
     */
    @Override
    public Boolean syncSend(TopicEnum topicE, T message) {
        String json = serializeMessage2Json(message);
        return doSyncSend(topicE.getTopic(), json);
    }

    /**
     * 异步发送消息
     * @author wangjinfei
     * @date 2025/6/25 14:54
     * @param topicE
     * @param message
     */
    @Override
    public void asyncSend(TopicEnum topicE, T message) {
        String json = serializeMessage2Json(message);
        doAsyncSend(topicE.getTopic(), json, null);
    }

    /**
     * 异步发送消息，并且可以指定回调函数
     * @author wangjinfei
     * @date 2025/6/25 14:54
     * @param topicE
     * @param message
     * @param callback
     */
    @Override
    public void asyncSend(TopicEnum topicE, T message, Consumer<SendResult<String, String>> callback) {
        String json = serializeMessage2Json(message);
        doAsyncSend(topicE.getTopic(), json, callback);
    }
}
