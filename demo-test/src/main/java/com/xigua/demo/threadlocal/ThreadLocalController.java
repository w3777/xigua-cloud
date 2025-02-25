package com.xigua.demo.threadlocal;

import com.alibaba.ttl.TransmittableThreadLocal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ThreadLocalController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/2/25 20:06
 */
@Slf4j
@RestController
@RequestMapping("threadlocal")
@RequiredArgsConstructor
public class ThreadLocalController {

    @PostMapping("/test01")
    public void test01(){
        // 当你使用 ThreadLocal 时，每个线程都会有自己独立的存储空间，而不是共享的
        ThreadLocal<Integer> threadLocal = new ThreadLocal<>();
        // 这里的set不是初始化，是set到主线程的threadLocal中，就会有一个经典的问题"ThreadLocal父子线程传递"
        // 会导致其他线程 拿不到threadLocal的值
        threadLocal.set(100);

        //ThreadLocal.withInitial() 的作用是为每个线程提供一个单独的变量副本，使得每个线程都能拥有自己独立的实例。
        //ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 100);

        Thread thread1 = new Thread(() -> {
            System.out.println("thread1 before = " + threadLocal.get());
            threadLocal.set(200);
            System.out.println("thread1 after = " + threadLocal.get());
            threadLocal.remove();
        });
        thread1.start();

        Thread thread2 = new Thread(() -> {
            System.out.println("thread2 before = " + threadLocal.get());
            threadLocal.set(500);
            System.out.println("thread2 after = " + threadLocal.get());
            threadLocal.remove();
        });
        thread2.start();

        threadLocal.remove();

        // 在业务开发时，也会有一个经典问题（线程上下文传递）就是明明可以通过SessionHelper.getUserId()或者SecurityUtils.getUserId()获取当前用户id
        // 但就是在执行异步线程池或者mq中拿不到当前用户id
        // 线程上下文传递问题和threadLocal父子线程传递的思想很想
        // 解决方法：
        // 1. 显示传参
        // 2. 隐式传参（线程装饰器 - 也是传参思想）
        // 3. 阿里TransmittableThreadLocal（把主线程中的ThreadLocal数据复制到子线程的ThreadLocal中）
    }

    @PostMapping("/test02")
    public void test02(){
        TransmittableThreadLocal<Integer> threadLocal = new TransmittableThreadLocal<>();
        threadLocal.set(100);

        Thread thread1 = new Thread(() -> {
            System.out.println("thread1 before = " + threadLocal.get());
            threadLocal.set(200);
            System.out.println("thread1 after = " + threadLocal.get());
            threadLocal.remove();
        });
        thread1.start();

        Thread thread2 = new Thread(() -> {
            System.out.println("thread2 before = " + threadLocal.get());
            threadLocal.set(500);
            System.out.println("thread2 after = " + threadLocal.get());
            threadLocal.remove();
        });
        thread2.start();

        threadLocal.remove();
    }
}
