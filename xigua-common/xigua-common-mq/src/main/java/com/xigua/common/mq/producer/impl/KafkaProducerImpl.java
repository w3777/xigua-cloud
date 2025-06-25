package com.xigua.common.mq.producer.impl;

import com.xigua.common.mq.producer.AbstractMessageQueueProducer;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

/**
 * @ClassName KafkaProducerImpl
 * @Description
 * @Author wangjinfei
 * @Date 2025/6/20 13:22
 */
@Slf4j
@Service
public class KafkaProducerImpl<T> extends AbstractMessageQueueProducer<T> {
    @Resource
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 发送消息
     * @author wangjinfei
     * @date 2025/6/20 13:17
     * @param topic
     * @param messageJsonStr
     * @return boolean
     */
    @Override
    protected boolean doSyncSend(String topic, String messageJsonStr) {
        try {
            kafkaTemplate.send(topic, messageJsonStr).get();
        } catch (Exception e) {
            log.error("发送消息失败，topic：{}，message：{}", topic, messageJsonStr, e);
            throw new RuntimeException(e);
        }
        return true;
    }

    /**
     * 异步发送消息，并且可以指定回调函数
     * @author wangjinfei
     * @date 2025/6/25 14:54
     * @param topic
     * @param messageJsonStr
     * @param callback
     * @return boolean
     */
    @Override
    protected boolean doAsyncSend(String topic, String messageJsonStr, Consumer<SendResult<String, String>> callback) {
        CompletableFuture<SendResult<String, String>> sendResultFuture = kafkaTemplate.send(topic, messageJsonStr);
        sendResultFuture.whenComplete((result, e) -> {
            if (e != null) {
                log.error("发送消息失败，topic：{}，message：{}", topic, messageJsonStr, e);
                throw new RuntimeException(e);
            } else {
                // 如果有回调函数，执行回调函数
                if (callback != null) {
                    callback.accept(result);
                }
            }
        });

        return true;
    }
}
