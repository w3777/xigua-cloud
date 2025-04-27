package com.xigua.common.core.handle;

import com.xigua.common.core.exception.BusinessException;
import com.xigua.domain.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * @ClassName ExceptionHandler
 * @Description
 * @Author wangjinfei
 * @Date 2025/4/27 10:15
 */
@Slf4j
@RestControllerAdvice
public class ExceptionHandle {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R MethodArgumentNotValidExceptionHandle(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();
        if (result.hasErrors()) {
            // 拼接所有错误提示
            String errorMsg = result.getAllErrors()
                    .stream()
                    .map(e -> e.getDefaultMessage())
                    .collect(Collectors.joining(","));
            return R.fail(errorMsg);
        }
        return R.fail("参数不可为空！");
    }

    @ExceptionHandler(BusinessException.class)
    public R BusinessExceptionHandle(BusinessException e) {
        log.error("业务异常捕获 --->>>", e);
        return R.fail(e.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public R exceptionHandle(Exception e) {
        log.error("异常捕获 --->>>", e);
        return R.fail("系统错误");
    }
}
