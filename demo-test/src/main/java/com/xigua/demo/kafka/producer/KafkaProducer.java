package com.xigua.demo.kafka.producer;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@AllArgsConstructor
@Component
@Slf4j
public class KafkaProducer {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void syncSend(String topic, String key, String data) {
        try {
            // 同步发送消息
            SendResult<String, Object> result = kafkaTemplate.send(new ProducerRecord<>(topic, key, data)).get();
            RecordMetadata recordMetadata = result.getRecordMetadata();
            log.info("---------->>> 同步消息发送成功，结果：{}", recordMetadata);
        } catch (Exception e) {
            log.error("---------->>> 同步消息发送失败: " + e.getMessage());
        }
    }

    public void asyncSend(String topic, String key, String data) {
        // 异步发送消息
        CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(new ProducerRecord<>(topic, key, data));
        future.whenComplete((result,e) -> {
            RecordMetadata recordMetadata = result.getRecordMetadata();
            log.info("---------->>> 异步消息发送成功，结果：{}", recordMetadata);
        });
    }
}
