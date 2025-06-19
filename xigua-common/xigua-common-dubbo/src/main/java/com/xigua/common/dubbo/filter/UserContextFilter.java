package com.xigua.common.dubbo.filter;

import com.alibaba.fastjson2.JSONObject;
import com.xigua.common.core.util.UserContext;
import com.xigua.common.core.model.UserToken;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.rpc.*;

/**
 * @ClassName UserContextFilter
 * @Description TODO
 * @Author wangjinfei
 * @Date 2024/10/5 9:19
 */
@Activate(group = {CommonConstants.CONSUMER, CommonConstants.PROVIDER})
public class UserContextFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        // 1.consumer启动时向provider查询Meta信息（getMetadataInfo()）,provider就进入了自定义过滤器，而这时的RpcContext.getServiceContext()..getUrl()为null
        // 2.正常的服务接口调用不会出现上述问题
        // 3.暂时通过以下代码回避这个问题
        URL url = RpcServiceContext.getContext().getUrl();
        if(url == null){
            return invoker.invoke(invocation);
        }

        if (RpcContext.getContext().isConsumerSide()) {
            UserToken userToken = UserContext.get();
            if (userToken != null) {
                // 隐式传参
                RpcContext.getContext().setAttachment("userInfo", JSONObject.toJSONString(userToken));
            }
        }

        if (RpcContext.getContext().isProviderSide()) {
            String userInfo = RpcContext.getContext().getAttachment("userInfo");
            if (userInfo != null) {
                // 将用户信息放入 ThreadLocal
                UserToken userToken = JSONObject.parseObject(userInfo, UserToken.class);
                UserContext.set(userToken);
            }
        }
        return invoker.invoke(invocation);
    }
}
