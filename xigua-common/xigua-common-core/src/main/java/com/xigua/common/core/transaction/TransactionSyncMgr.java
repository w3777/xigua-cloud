package com.xigua.common.core.transaction;

import com.xigua.common.core.model.UserToken;
import com.xigua.common.core.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronizationAdapter;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**
 * @ClassName TransactionSyncMgr
 * @Description spring事务同步管理器
 * @Author wangjinfei
 * @Date 2025/2/6 15:25
 */
@Slf4j
public class TransactionSyncMgr {
    // todo 暂时先创建一个固定线程池，可以优化成自定义线程池
    private static Executor executor = Executors.newFixedThreadPool(1);


    /**
     * 确保事务提交后执行异步任务
     * @author wangjinfei
     * @date 2025/2/6 15:25
     * @param task
     * @return void
     */
    public static CompletableFuture<Void> executeAfterCommit(Runnable task) {
        CompletableFuture<Void> future = new CompletableFuture<>();
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronizationAdapter() {
            @Override
            public void afterCommit() {
                // 在事务提交后执行异步任务
                CompletableFuture.runAsync(task, executor).exceptionally(e -> {
                    UserToken userToken = UserContext.get();
                    // 打印用户信息
                    log.error("-------->>>>>>> 事务提交后执行异步任务失败，用户信息：{}", userToken, e);
                    // 把异常传递给future，让调用者根据业务逻辑处理异常
                    future.completeExceptionally(e);
                    return null;

                    // 注意：这里打印了异常信息，也会把异常传递给future，如果调用者根据该方法返回的future又进行exceptionally处理，那么异常信息会打印两边
                    // 这里的异常信息打印不关注业务逻辑，只打印异常信息堆栈
                    // 调用者的exceptionally异常处理，要打印实际的业务信息
                });
            }
        });
        return future;
    }
}
