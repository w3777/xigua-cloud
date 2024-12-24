package com.xigua.demo.guava;

import com.alibaba.fastjson2.JSON;
import com.alibaba.nacos.shaded.com.google.common.util.concurrent.ListenableFutureTask;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;

/**
 * @ClassName GuavaController
 * @Description
 * @Author wangjinfei
 * @Date 2024/12/24 13:45
 */
@Slf4j
@RestController
@RequestMapping("guava")
@AllArgsConstructor
public class GuavaController {


    @PostMapping("/test01")
    public void test01() throws Exception{
        // 创建一个 ListeningExecutorService，这里使用固定大小的线程池
        ListeningExecutorService executorService = MoreExecutors.listeningDecorator(Executors.newFixedThreadPool(2));

        // 定义一个简单的任务，返回一个字符串
        Callable<String> task = () -> {
            Thread.sleep(2000);  // 模拟任务耗时
            return "任务完成!";
        };

        // 创建 ListenableFutureTask
        ListenableFuture<String> listenableFuture = executorService.submit(task);

        // 注册一个回调，任务完成后执行
        listenableFuture.addListener(() -> {
            try {
                System.out.println("回调执行了");
                // 获取任务的返回值并打印
                String result = listenableFuture.get();
                System.out.println("任务的结果: " + result);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, executorService);

        // 主线程继续执行其他操作
        System.out.println("主线程继续执行...");
        Thread.sleep(3000);  // 等待任务完成
        executorService.shutdown();  // 关闭线程池

        // 输出结果
        // 回调执行了
        // 主线程继续执行...
        // 任务的结果: 任务完成!
    }
}
