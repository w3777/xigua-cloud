package com.xigua.client.kafka;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.api.service.GroupService;
import com.xigua.common.mq.constant.Group;
import com.xigua.common.mq.constant.Topic;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName KafkaConsumer
 * @Description
 * @Author wangjinfei
 * @Date 2025/7/28 9:46
 */
@Slf4j
@Component
public class KafkaConsumer {
    @Autowired
    private GroupService groupService;

    @KafkaListener(topics = Topic.GROUP_CACHE, groupId = Group.XI_GUA)
    public void addGroup2Redis(ConsumerRecord<String, String> record, Acknowledgment ack) {
        String value = record.value();
        if(StringUtils.isEmpty(value)){
            log.info("----->>>>>>  缓存群组收到消息为空");
        }

        Map map = JSONObject.parseObject(value, Map.class);
        if (MapUtils.isEmpty(map)){
            log.info("----->>>>>>  缓存群组收到消息为空");
        }

        Object object = map.get("groupId");
        if(object == null){
            log.info("----->>>>>>  缓存群组收到消息为空");
        }

        // 调用方法 缓存群组
        groupService.addGroup2Redis(String.valueOf(object));

        // 手动提交 offset
        ack.acknowledge();
    }
}
