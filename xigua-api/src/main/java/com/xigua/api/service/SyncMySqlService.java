package com.xigua.api.service;

/**
 * @ClassName SyncMySqlService
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/8/16 16:45
 */
public interface SyncMySqlService {

    /**
     * 同步消息已读
     * @author wangjinfei
     * @date 2025/8/16 16:46
     * @return Boolean
    */
    Boolean syncMessageRead();
}
