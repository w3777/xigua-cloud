package com.xigua.common.core.util;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.xigua.domain.token.UserToken;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

/**
 * @ClassName TokenUtil
 * @Description
 * @Author wangjinfei
 * @Date 2025/3/25 16:56
 */
@Slf4j
public class TokenUtil {
    //密钥盐
    public static final String TOKEN_SECRET="xigua";

    // 过期时间1天
    private static final Long EXPIRE_TIME = 24 * 60 * 60 * 1000L;

    public static String genToken(UserToken userToken) {
        String token = null;

        // 过期时间
        Date expiresAt = new Date(System.currentTimeMillis() + EXPIRE_TIME);
        try {
            token = JWT.create()
                    .withClaim("userName", userToken.getUserName())
                    .withClaim("userId", userToken.getUserId())
                    .withClaim("phone", userToken.getPhone())
                    .withExpiresAt(expiresAt)
                    .withIssuedAt(new Date())
                    // 使用了HMAC256加密算法
                    .sign(Algorithm.HMAC256(TOKEN_SECRET));
        } catch (Exception e){
            e.printStackTrace();
        }
        return token;
    }

    /**
     * 判断token是否过期
     * @author wangjinfei
     * @date 2025/5/11 21:30
     * @param token
     * @return Boolean
    */
    public static Boolean tokenExpire(String token) {
        try {
            JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).build().verify(token);
            return false;
        } catch (TokenExpiredException e){
            log.error("token过期：", e);
            return true;
        }
    }
}
