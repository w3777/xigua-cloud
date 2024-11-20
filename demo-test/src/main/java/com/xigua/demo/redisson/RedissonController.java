package com.xigua.demo.redisson;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName RedissionController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2024/11/18 11:33
 */
@Slf4j
@RestController
@RequestMapping("redisson")
@RequiredArgsConstructor
public class RedissonController {
    private final RedissonClient redissonClient;
    private int stock = 5;

    @PostMapping("test01")
    public void test01(){
        RLock lock = redissonClient.getLock("test01");
        lock.lock();
        try {
            log.info("------->>>>>> 加锁成功，执行后续代码。线程 ID：" + Thread.currentThread().getId());
            Thread.sleep(10000);
        } catch (Exception e) {
            //TODO
        } finally {
            lock.unlock();
            log.info("------->>>>>> Finally，释放锁成功。线程 ID：" + Thread.currentThread().getId());
        }
    }

    @PostMapping("test02")
    public void test02(){
//        RLock lock = redissonClient.getLock("test02");
        RLock lock = redissonClient.getFairLock("test02");
        boolean b = lock.tryLock();
        if (b == false) {
            log.info("------->>>>>> 线程ID：{},没有获取到锁", Thread.currentThread().getId());
            return;
        }

        try {
            if (stock == 0) {
                log.info("------->>>>>> 库存已售完");
                return;
            }

            log.info("------->>>>>> 线程ID：{},获取到锁", Thread.currentThread().getId());
            log.info("------->>>>>> 现有库存：{}", stock);

            stock--;
            log.info("------->>>>>> 创建订单，还剩：{}", stock);

        } finally {
            // 确保锁被释放
            lock.unlock();
            log.info("------->>>>>> 线程ID：{},释放锁", Thread.currentThread().getId());
        }
    }
}
