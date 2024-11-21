package com.xigua.demo.redisson;

import com.xigua.demo.domain.Goods;
import com.xigua.demo.mapper.GoodsMapper;
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
    private final GoodsMapper goodsMapper;
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

    /**
     * 测试sync锁 分布式的情况下是否会出现超卖
     * 可以用idea多开应用实例，用jmeter压测，看是否会出现超卖
     * @author wangjinfei
     * @date 2024/11/21 15:00
    */
    @PostMapping("test03")
    public synchronized void test03(){
        //sycn是jvm的锁，分布式的情况下，多个jvm之间是无法共享锁的，所以会出现超卖

        Goods goods = goodsMapper.selectById(1);
        Long stock = goods.getStock();
        if (stock == 0) {
            log.info("------->>>>>> 库存已售完");
            return;
        }
        log.info("------->>>>>> 现有库存：{}", stock);
        stock --;
        goods.setStock(stock);
        goodsMapper.updateById(goods);
        log.info("------->>>>>> 创建订单，还剩：{}", stock);
    }

    /**
     * 测试分布式锁 分布式的情况下是否会出现超卖
     * @author wangjinfei
     * @date 2024/11/21 15:59
    */
    @PostMapping("test04")
    public void test04(){
        //一般业务会在有营销活动出现高并发，会进行应用扩容，所以需要加分布式锁
        //以保证同一时间点只有一个应用实例的一个线程拿到锁
        
        RLock lock = redissonClient.getLock("test04");
        boolean b = lock.tryLock();
        if (b == false) {
            log.info("------->>>>>> 线程ID：{},没有获取到锁", Thread.currentThread().getId());
            return;
        }

        Goods goods = goodsMapper.selectById(1);
        Long stock = goods.getStock();
        try {
            if (stock == 0) {
                log.info("------->>>>>> 库存已售完");
                return;
            }

            log.info("------->>>>>> 线程ID：{},获取到锁", Thread.currentThread().getId());
            log.info("------->>>>>> 现有库存：{}", stock);

            stock --;
            goods.setStock(stock);
            goodsMapper.updateById(goods);
            log.info("------->>>>>> 创建订单，还剩：{}", stock);

        } finally {
            // 确保锁被释放
            lock.unlock();
            log.info("------->>>>>> 线程ID：{},释放锁", Thread.currentThread().getId());
        }
    }
}
