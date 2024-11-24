package com.xigua.demo.sse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Date;

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
}
