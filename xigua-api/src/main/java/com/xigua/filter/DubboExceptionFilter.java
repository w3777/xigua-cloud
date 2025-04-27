package com.xigua.filter;

import com.xigua.common.core.exception.BusinessException;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.filter.ExceptionFilter;
import org.apache.dubbo.rpc.service.GenericService;

/**
 * @ClassName DubboExceptionFilter
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/4/27 22:28
 */
@Activate(group = {CommonConstants.CONSUMER, CommonConstants.PROVIDER})
public class DubboExceptionFilter extends ExceptionFilter {

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
        // 如果有异常
        if (appResponse.hasException() && GenericService.class != invoker.getInterface()) {
            // 获取抛出的异常
            Throwable exception = appResponse.getException();
//            if (exception instanceof RuntimeException) {
//                System.out.println("exception = " + exception);
//                if(exception instanceof BusinessException){
//                    System.out.println("exception = " + exception);
//                }
//            }
//            String classname = exception.getClass().getName();
            // 如果是自定义异常，直接抛出
            if (exception.getMessage().contains("com.xigua.common.core.exception.BusinessException")) {
                appResponse.setException(new BusinessException(exception.getMessage()));
                return;
            }
            // 如果是其他异常，使用Dubbo的业务进行处理
            super.onResponse(appResponse, invoker, invocation);
        }
    }
}
