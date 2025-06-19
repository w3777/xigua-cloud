package com.xigua.common.dubbo.filter;

import org.apache.commons.lang3.StringUtils;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;
import org.slf4j.MDC;

/**
 * @ClassName TraceIdFilter
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/4/5 10:10
 */
@Activate(group = {CommonConstants.CONSUMER, CommonConstants.PROVIDER})
public class TraceIdFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        RpcContext rpcContext = RpcContext.getContext();
        URL url = rpcContext.getUrl();
        String traceId;

        if(url == null){
            return invoker.invoke(invocation);
        }

        if (rpcContext.isConsumerSide()) {
            traceId = MDC.get("traceId");

            if (StringUtils.isNotEmpty(traceId)) {
                rpcContext.setAttachment("traceId", traceId);
            }
        }

        if (rpcContext.isProviderSide()) {
            traceId = rpcContext.getAttachment("traceId");
            if(StringUtils.isNotEmpty(traceId)){
                MDC.put("traceId", traceId);
            }
        }

        return invoker.invoke(invocation);
    }
}
