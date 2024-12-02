package com.xigua.demo.sse;

import com.alibaba.fastjson2.JSON;
import com.xigua.demo.util.SSEUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName SSEController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2024/11/24 12:55
 */
@Slf4j
@RestController
@RequestMapping("sse")
@RequiredArgsConstructor
public class SSEController {
    private final KafkaTemplate<String, Object> kafkaTemplate;

    @GetMapping("/test01")
    public SseEmitter stream() {
        // 用于创建一个 SSE 连接对象
        SseEmitter emitter = new SseEmitter();

        // 在后台线程中模拟实时数据
        new Thread(() -> {
            try {
                for (int i = 0; i < 10; i++) {
                    // emitter.send() 方法向客户端发送消息
                    // 使用SseEmitter.event()创建一个事件对象，设置事件名称和数据
                    emitter.send(SseEmitter.event()
//                            .name("message")
                            .data("[" + new Date() + "] Data #" + i));
                    Thread.sleep(1000);
                }
                // 数据发送完成后，关闭连接  这里可以不关一直向前端推送数据，或者前端主动关闭
                emitter.complete();
            } catch (Exception e) {
                // 发生错误时，关闭连接并报错
                emitter.completeWithError(e);
            }
        }).start();
        return emitter;
    }

    @GetMapping("/test02")
    public SseEmitter test02(){
        // 在高并发的情况下，如果有服务端向客户端单方面实时推送的情况下，可以采用mq + sse的方式  （大屏、消息提醒、商品活动提醒、新闻推送）
        // 1.首先将客户端和服务端建立sse长连接
        // 2.修改数据的同时，将数据推送到mq中
        // 3.在mq的消费者中，处理业务，将数据推送到客户端

        // 用key区分不同的sse连接
        SseEmitter sseEmitter = SSEUtils.create("1");
        // 模拟向mq中推送数据  （实际可以根据不同的service向mq推数据）
        Map<String, Object> messageMap = new HashMap<>();
        messageMap.put("id", "1");
        for (int i = 0; i < 10; i++) {
            messageMap.put("data", String.valueOf(i));
            String json = JSON.toJSONString(messageMap);
            kafkaTemplate.send("sseTopic", "1", json);
        }
        return sseEmitter;

    }
}
