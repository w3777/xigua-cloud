package com.xigua.demo.kafka.consumer;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.demo.util.SSEUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @ClassName KafkaProducer
 * @Description TODO
 * @Author wangjinfei
 * @Date 2024/11/30 14:44
 */
@Component
@Slf4j
public class KafkaConsumer {
    /**
     * 下面的主题是一个数组，可以同时订阅多主题，只需按数组格式即可，也就是用","隔开
     */
    @KafkaListener(topics = {"testTopic"}, groupId = "test-group")
    public void receive(ConsumerRecord<?, ?> record){
        log.info("---------->>>  消费者收到的消息key: " + record.key());
        log.info("---------->>>  消费者收到的消息value: " + record.value().toString());
    }

    @KafkaListener(topics = {"sseTopic"}, groupId = "test-group")
    public void receive2(ConsumerRecord<?, ?> record){
        String str = record.value().toString();
        Map<String,Object> map = JSONObject.parseObject(str, Map.class);
        // 拿到对应的SseEmitter对象
        SseEmitter sseEmitter = SSEUtils.get(map.get("id").toString());
        try {
            // 这里可以处理业务逻辑，然后将数据推送到客户端
            StringBuilder sb = new StringBuilder();
            String data = sb.append("时间：").append(LocalDateTime.now()).append("，").append("消息：").append(map.get("data")).toString();
            sseEmitter.send(SseEmitter.event().data(data));
        }catch (Exception e){
            log.error("---------->>>  消费者发送消息失败: " + e.getMessage());
        }
    }
}
