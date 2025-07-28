package com.xigua.common.mq.constant;

import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum TopicEnum {
    TEST_TOPIC("test_topic", "测试"),
    GROUP_CACHE("group_cache", "群组缓存"),
    ;

    final String topic;
    final String desc;

    public String getTopic() {
        return topic;
    }

    public String getDesc() {
        return desc;
    }

    public static TopicEnum getByQueue(String queue) {
        for (TopicEnum topicEnum : TopicEnum.values()) {
            if (topicEnum.getTopic() == queue) {
                return topicEnum;
            }
        }
        return null;
    }

    public static TopicEnum getByDesc(String desc) {
        for (TopicEnum topicEnum : TopicEnum.values()) {
            if (topicEnum.getDesc().equals(desc)) {
                return topicEnum;
            }
        }
        return null;
    }
}
