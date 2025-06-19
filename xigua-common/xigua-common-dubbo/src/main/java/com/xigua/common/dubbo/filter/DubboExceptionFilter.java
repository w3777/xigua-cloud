package com.xigua.common.dubbo.filter;

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
 * @Description dubbo异常处理过滤器
 * @Author wangjinfei
 * @Date 2025/4/27 22:28
 */
@Activate(group = {CommonConstants.CONSUMER, CommonConstants.PROVIDER})
public class DubboExceptionFilter extends ExceptionFilter {
    /*
    * 如果代码抛出自定义异常，会被dubbo默认ExecuteLimitFilter处理，又会把异常包装成RuntimeException再抛出
    * 导致ExceptionHandle会捕捉到Exception，而不是自定义异常
    * 所以需要自定义异常过滤器，判断是否是自定义异常，如果是，直接抛出，否则使用dubbo的默认异常处理
    */

    @Override
    public void onResponse(Result appResponse, Invoker<?> invoker, Invocation invocation) {
        if (appResponse.hasException() && GenericService.class != invoker.getInterface()) {

            Throwable exception = appResponse.getException();
            String classname = exception.getClass().getName();
            // 如果是指定自定义异常直接抛出
            if (classname.startsWith("com.xigua.common.core.exception") && exception instanceof Exception) {
                return;
            }
            // 如果是其他异常，使用Dubbo的业务进行处理 (它会把异常包装成RuntimeException再抛出)
            super.onResponse(appResponse, invoker, invocation);
        }
    }
}
