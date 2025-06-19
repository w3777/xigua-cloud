package com.xigua.center.service;

import com.xigua.common.core.util.RedisUtil;
import com.xigua.domain.enums.RedisEnum;
import com.xigua.service.CenterService;
import com.xigua.service.TimerTaskService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.config.annotation.DubboService;

import java.util.Set;

/**
 * @ClassName TimerTaskServiceImpl
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/6/16 20:37
 */
@Slf4j
@RequiredArgsConstructor
@DubboService
public class TimerTaskServiceImpl implements TimerTaskService {
    private final RedisUtil redisUtil;
    private final CenterService centerService;

    @Override
    public void checkOnlineConnection() {
        /**
         * todo 如果在线用户过多
         * 可以采用分片的方式查询reids中的在线用户
         * 负载均衡的方式执行任务方式
        */

        // 获取所有在线用户
        Set<String> onlineUsers = redisUtil.getSetsByPattern(RedisEnum.ONLINE_USER.getKey() + "*");
        if(CollectionUtils.isEmpty(onlineUsers)){
            return;
        }

        for (String onlineUser : onlineUsers) {
            // 获取在线用户的最后心跳时间
            String lastPing = redisUtil.get(RedisEnum.LAST_PING_TIME.getKey() + onlineUser);
            if(StringUtils.isEmpty(lastPing)){
                // 如果没有最后心跳时间，说明用户不在线 删除在线用户
                delOnlineUser(onlineUser);
                continue;
            }

            long timestamp = System.currentTimeMillis();
            if(timestamp - Long.parseLong(lastPing) > 150000){
                // 如果最后心跳时间超过150秒，说明用户不在线 删除在线用户
                delOnlineUser(onlineUser);
                continue;
            }
        }
    }

    /**
     * redis 删除在线用户
     * @author wangjinfei
     * @date 2025/6/16 21:11
     * @param onlineUser
    */
    private void delOnlineUser(String onlineUser) {
        // 用户不在线
        log.info("----->>>>>> 用户{}不在线", onlineUser);
        String userInServer = centerService.onlineUser(onlineUser);
        String node = userInServer.split(":")[1] + ":" + userInServer.split(":")[2];

        // 删除在线用户
        redisUtil.srem(RedisEnum.ONLINE_USER.getKey() + node, onlineUser);

        // todo 通知在线好友 用户下线
    }
}
