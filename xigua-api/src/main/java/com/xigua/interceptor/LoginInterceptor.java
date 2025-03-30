package com.xigua.interceptor;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.xigua.common.core.util.TokenUtil;
import com.xigua.common.core.util.UserContext;
import com.xigua.domain.token.UserToken;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
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
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 拦截请求 获取token
        String token = request.getHeader("XG-Token");
        if (StringUtils.isEmpty(token)) {
            return false;
        }

        DecodedJWT decodedJWT;
        String userName = null;
        Long userId = null;
        String phone = null;
        try {
            // 解密token
            decodedJWT = JWT.require(Algorithm.HMAC256(TokenUtil.TOKEN_SECRET)).build().verify(token);
            userName = decodedJWT.getClaim("userName").asString();
            userId = decodedJWT.getClaim("userId").asLong();
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
    }
}
