package com.xigua.demo.trace;

import com.xigua.service.TestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName TraceController
 * @Description TODO
 * @Author wangjinfei
 * @Date 2025/4/5 11:13
 */
@Slf4j
@RestController
@RequestMapping("trace")
@RequiredArgsConstructor
public class TraceController {
    @DubboReference
    private TestService testService;

    @PostMapping("/test01")
    public void test01(){
        testService.testTraceId();
    }
}
