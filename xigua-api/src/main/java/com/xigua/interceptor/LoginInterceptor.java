package com.xigua.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xigua.common.core.util.TokenUtil;
import com.xigua.common.core.util.UserContext;
import com.xigua.common.sequence.sequence.Sequence;
import com.xigua.domain.token.UserToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @ClassName LoginInterceptor
 * @Description
 * @Author wangjinfei
 * @Date 2025/3/28 15:45
 */
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {
    @Autowired
    private Sequence sequence;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 生成链路id 存储到mdc
        String traceId = sequence.nextNo();
        MDC.put("traceId", traceId);

        // 拦截请求 获取token
        String token = request.getHeader("XG-Token");
        if (StringUtils.isEmpty(token)) {
            try {
                // 返回401未授权状态码
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"code\":401,\"msg\":\"未提供认证令牌\"}");
            }catch (Exception e){
                log.error("返回未授权状态码异常：", e);
            }
            return false;
        }

        // 判断token是否过期
        if(TokenUtil.tokenExpire(token)){
            try {
                // 返回401 token过期
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); // 401
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write("{\"code\":401,\"msg\":\"认证已过期\"}");
            }catch (Exception e){
                log.error("认证已过期：", e);
            }
            return false;
        }

        DecodedJWT decodedJWT;
        String userName = null;
        String userId = null;
        String phone = null;
        try {
            // 解密token
            decodedJWT = JWT.require(Algorithm.HMAC256(TokenUtil.TOKEN_SECRET)).build().verify(token);
            userName = decodedJWT.getClaim("userName").asString();
            userId = decodedJWT.getClaim("userId").asString();
            phone = decodedJWT.getClaim("phone").asString();
        } catch (JWTVerificationException e) {
            log.error("解析Token异常：token={}", token, e);
        }

        // todo 从redis中获取token，强转为UserToken

        UserToken userToken = new UserToken();
        userToken.setUserName(userName);
        userToken.setUserId(userId);
        userToken.setPhone(phone);

        UserContext.set(userToken);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex){
        UserContext.clear();
        MDC.remove("traceId");
    }
}
