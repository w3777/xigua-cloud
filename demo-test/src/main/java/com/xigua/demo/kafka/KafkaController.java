package com.xigua.demo.kafka;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.xigua.common.mq.constant.TopicEnum;
import com.xigua.common.mq.producer.MessageQueueProducer;
import com.xigua.demo.kafka.producer.KafkaProducer;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("kafka")
@AllArgsConstructor
public class KafkaController {
    private final KafkaProducer kafkaProducer;
    private final MessageQueueProducer messageQueueProducer;

    @PostMapping("/test01")
    public void test01() {
        Map<String, String> messageMap = new HashMap();
        messageMap.put("message", "hello world!");
        String jsonString = JSON.toJSONString(messageMap);
        String key = String.valueOf(UUID.randomUUID());
        kafkaProducer.syncSend("testTopic", key, jsonString);
    }

    @PostMapping("/test02")
    public void test02() {
        Map<String, String> messageMap = new HashMap();
        messageMap.put("message", "hello world!");
        String jsonString = JSON.toJSONString(messageMap);
        String key = String.valueOf(UUID.randomUUID());
        kafkaProducer.asyncSend("testTopic", key, jsonString);
    }

    @PostMapping("/test03")
    public void test03() {
        Map<String, String> messageMap = new HashMap();
        messageMap.put("message", "hello world!");

        // 同步发送消息
        messageQueueProducer.syncSend(TopicEnum.TEST_TOPIC, messageMap);

        // 异步发送消息
        messageQueueProducer.asyncSend(TopicEnum.TEST_TOPIC, messageMap);

        // 异步回调发送消息
        messageQueueProducer.asyncSend(TopicEnum.TEST_TOPIC, messageMap, result -> {
            System.out.println("result = " + result);
        });
    }
}
