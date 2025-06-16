package com.xigua.center.task;

import com.xigua.service.TimerTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @ClassName TaskHandler
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/16 21:08
 */
@Component
public class TaskHandler {
    @Autowired
    private TimerTaskService timerTaskService;

    /**
     * 每隔2分钟检查一次在线连接
     * 后期可以采用分布式调度
     * @author wangjinfei
     * @date 2025/6/16 21:12
    */
    @Scheduled(fixedRate = 120000)
    public void checkOnlineConnection() {
        timerTaskService.checkOnlineConnection();
    }
}
