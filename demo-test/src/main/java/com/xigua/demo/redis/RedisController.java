package com.xigua.demo.redis;

import com.xigua.common.core.util.RedisUtil;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.enums.RedisEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName RedisController
 * @Description
 * @Author wangjinfei
 * @Date 2025/4/18 16:28
 */
@Slf4j
@RestController
@RequestMapping("/redis")
@RequiredArgsConstructor
public class RedisController {
    private final Sequence sequence;
    private final RedisUtil redisUtil;

    private final static int partSize = 10;

    @RequestMapping("/test01")
    public void test01(){
        // redis大key 分片拆分

        // 模拟10000个数据 存入redis
        for (int i = 0; i < 10000; i++) {
            long seq = sequence.nextValue();
            // 取模 计算出要存入的index
            int index = (int) (seq % partSize);

            boolean b = redisUtil.sadd(RedisEnum.BIG_KEY_PREFIX.getKey() + index, String.valueOf(seq));
            if (b){
                log.info("---->>>>> 添加成功, seq:{}, 存入{}：{}", seq, RedisEnum.BIG_KEY_PREFIX.getKey(), index);
            }else{
                log.error("---->>>>> 添加失败, seq:{}", seq);
            }
        }
        log.info("---->>>>> 结束");
    }

    @RequestMapping("/test02")
    public void test02(){
        Long total = 0L;

        // 遍历10个集合 计算出总大小
        for (int i = 0; i < partSize; i++) {
            long size = redisUtil.scard(RedisEnum.BIG_KEY_PREFIX.getKey() + i);
            log.info("---->>>>> 第{}个集合的大小为：{}", i, size);

            total += size;
        }

        log.info("---->>>>> 总大小为：{}", total);
    }
}
