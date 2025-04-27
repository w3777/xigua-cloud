package com.xigua.common.core.handle;

import com.xigua.domain.result.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @ClassName ExceptionHandler
 * @Description
 * @Author wangjinfei
 * @Date 2025/4/27 10:15
 */
@Slf4j
@ControllerAdvice
public class ExceptionHandle {

    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public R exceptionHandler2(MethodArgumentNotValidException exception) {
        BindingResult result = exception.getBindingResult();
        if (result.hasErrors()) {
            // 返回第一个错误信息
            return R.fail(result.getAllErrors().get(0).getDefaultMessage());
        }
        return R.fail("参数不可为空！");
    }

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public R exceptionHandler(Exception e) {
        log.error("全局异常捕获 --->>>", e);
        return R.fail("系统错误");
    }
}
