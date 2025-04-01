package com.xigua.demo.thread;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName ThreadController
 * @Description
 * @Author wangjinfei
 * @Date 2025/3/31 10:14
 */
@Slf4j
@RestController
@RequestMapping("thread")
@RequiredArgsConstructor
public class ThreadController {
    private static final Object lock = new Object(); // 用于同步
    private static int count = 0; // 用来跟踪输出顺序

    @PostMapping("/test01")
    public void test01(){
        Thread thread1 = new Thread(new PrintTask(1), "thread1");
        Thread thread2 = new Thread(new PrintTask(2), "thread2");
        Thread thread3 = new Thread(new PrintTask(3), "thread3");

        thread1.start();
        thread2.start();
        thread3.start();

    }

    class PrintTask implements Runnable {
        private final int number;  // 每个线程输出的数字

        PrintTask(int number) {
            this.number = number;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                synchronized (lock) {
                    System.out.println("count = " + count);
                    // 控制输出顺序
                    while (count % 3 != number - 1) { // 这里的count不需要保证原子性，因为在synchronized块内已经保证了可见性
                        try {
                            lock.wait(); // 当前线程等待
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }

                    // 输出自己的数字
                    log.info("Thread {}: {}", Thread.currentThread().getName(), number);

                    // 更新 count，通知下一个线程
                    count++;
                    lock.notifyAll(); // 通知其他线程
                }
            }
        }
    }
}
