package com.xigua.common.mq.producer;

import com.xigua.common.mq.constant.TopicEnum;
import org.springframework.kafka.support.SendResult;

import java.util.function.Consumer;

/**
 * @ClassName MessageQueueProducer
 * @Description 上层接口封装定义消息队列生产者的通用方法，下层 Kafka、RocketMQ、Redis 等多种消息队列实现该接口
 * @Author wangjinfei
 * @Date 2025/6/20 11:45
 */
public interface MessageQueueProducer<T> {

    /**
     * 同步发送消息
     * @author wangjinfei
     * @date 2025/6/20 11:55
     * @param topicE 消息队列枚举
     * @param message 消息
    */
    Boolean syncSend(TopicEnum topicE, T message);

    /**
     * 异步发送消息
     * @author wangjinfei
     * @date 2025/6/25 14:54
     * @param topicE
     * @param message
    */
    void asyncSend(TopicEnum topicE, T message);

    /**
     * 异步发送消息，并且可以指定回调函数
     * @author wangjinfei
     * @date 2025/6/25 14:54
     * @param topicE
     * @param message
     * @param callback
    */
    void asyncSend(TopicEnum topicE, T message, Consumer<SendResult<String, String>> callback);

    // todo 可以添加其他方法
}
