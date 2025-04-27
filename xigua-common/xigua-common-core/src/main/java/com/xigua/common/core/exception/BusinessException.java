package com.xigua.common.core.exception;


/**
 * @ClassName BizException
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/4/27 21:15
 */
public class BusinessException extends RuntimeException{
    public BusinessException(String msg) {
        super(msg);
    }
}
